package mx.org.bamx.sigo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Sigo is the superclass for all the activity screens. Variables that need to be persisted are
 * saved here, and methods which are common for all activity screens (eg. logout) are kept here
 */
public abstract class Sigo extends AppCompatActivity {

    public String DB_ID = "int_id";

    private static String currentCommunity;

    private static boolean isSyncing = false;
    private static List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_communities, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(Sigo.this, SettingsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_down_in, R.anim.fade_out);
        }
        if (id == R.id.sync_now) {
            if (!isSyncing) {
                syncWithSigo();
            }
        }
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.log_out) {
            logout();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Logs out and returns to the login screen.
     */
    protected void logout() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle(R.string.log_out)
                .setMessage(R.string.confirm_log_out)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Sigo.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.push_right_out);

                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Start a separate asynchronous trhead, which will sync with the external SIGO server.
     */
    protected void syncWithSigo() {
        setIsSyncing(true);
        new SigoConnector(this).execute();
    }

    /**
     * Makes sync icons rotate, until sync has completed.
     *
     * @param menu Current menu where the sync icon is located (eg. menu_communities).
     */
    public void rotateSyncItem(Menu menu) {
        if (menu != null) {
            MenuItem sync = menu.findItem(R.id.sync_now);

            if (sync != null && sync.getActionView() == null) {

                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
                iv.startAnimation(rotation);
                sync.setActionView(iv);
            }

        }

    }

    /**
     * Called when SIGO external sync is done, to make sync icons stop rotating.
     *
     * @param menu Current menu where the sync icon is located (eg. menu_communities).
     */
    public void stopRotateSyncItem(Menu menu) {
        if (menu != null) {
            MenuItem sync = menu.findItem(R.id.sync_now);
            if (sync != null) {
                if (sync.getActionView() != null) {
                    sync.getActionView().clearAnimation();
                }
                sync.setActionView(null);
            }
        }
    }

    /**
     * Notifies all listeners (CommunityActivity and FamilyActivity) that syncing with SIGO
     * has started or stopped.
     *
     * @param property isSyncing
     * @param oldValue true if syncing was ongoing, false otherwise
     * @param newValue true is syncing is now ongoing, false otherwise
     */
    private void notifyListeners(String property, boolean oldValue, boolean newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    /**
     * Adds a new listener to list of listeners that will be notified when syncing with SIGO starts
     * or stops.
     *
     * @param newListener The PropertyChangeListener to be added.
     */
    protected void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    /**
     * Shows and alert dialog with the provided message
     *
     * @param message the message to show.
     */
    protected void showAlertDialogue(int message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialog.setTitle(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Changes isSyncing and notofoes listeners.
     *
     * @param isSyncing the new isSyncing value
     */
    public void setIsSyncing(boolean isSyncing) {
        this.notifyListeners("isSyncing", this.isSyncing, isSyncing);
        Sigo.isSyncing = isSyncing;
    }

    protected static boolean isSyncing() {
        return isSyncing;
    }

    protected String getCurrentCommunity() {
        return currentCommunity;
    }

    protected void setCurrentCommunity(String current) {
        currentCommunity = current;
    }

}

