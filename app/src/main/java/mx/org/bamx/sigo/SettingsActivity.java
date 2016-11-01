package mx.org.bamx.sigo;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

/**
 * SettingsActivity is used for displaying the available preferences - language and automatic sync
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static String language = "language_pref";
    public static String automatic_sync = "automatic_sync";
    public static String sync_frequency = "sync_frequency";

    protected static PendingIntent pendingIntent;
    protected static AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = this.getPreferences(0);
        Intent alarmIntent = new Intent(this, SyncReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addeditentry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.push_up_out);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Whenever the user changes the preferences, this method is automatically called.
     *
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
        if (key.equals(language)){
            setLocale(sharedPreferences.getString(key, null));
        } else if (key.equals(automatic_sync)) {
            if (sharedPreferences.getBoolean(key, false)) {
                startAlarm(Integer.valueOf(sharedPreferences.getString(sync_frequency, "21600000")));
            } else {
                cancelAlarm();
            }
        } else if (key.equals(sync_frequency)) {
            startAlarm(Integer.valueOf(sharedPreferences.getString(sync_frequency, "21600000")));
        }
    }

    /**
     * Changes language of the application
     *
     * @param lang The new language.
     */
    public void setLocale(String lang) {
        if (null == lang) {
            return;
        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;

        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, SettingsActivity.class);

        finish();
        startActivity(refresh);
    }

    /**
     * Persists a preference.
     */
    public static void setPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Retrieves a persisted preference.
     */
    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    /**
     * Method for starting a background sync frequency. This will be executed even when the app is
     * not runnning.
     *
     * @param interval The interval in milliseconds for how often synchronization should occur.
     */
    public void startAlarm(int interval) {
        if (manager != null) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
    }

    /**
     * Cancels the currently set alarm.
     */
    public void cancelAlarm() {

        if (manager!= null) {
            manager.cancel(pendingIntent);
        }
    }
}
