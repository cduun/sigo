package mx.org.bamx.sigo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * AddEditEntryActivity is used for displaying the socio-economic study
 */
public class AddEditEntryActivity extends Sigo {

    private final DBHelper db = new DBHelper(AddEditEntryActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addeditentry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if (getCurrentCommunity() != null) {
            EditText editText = (EditText) findViewById(R.id.editCommunity);
            editText.setText(getCurrentCommunity());
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt(DB_ID);
            SocioEconomicStudy ses = db.getEntry(Integer.toString(id));
            setTitle(getString(R.string.edit) + " " + ses.getName());

            EditText editText = (EditText) findViewById(R.id.editName);
            editText.setText(ses.getName());
            editText.setTag(id);

            if (ses.isHungryInBed() == 1) {
                RadioButton yes = (RadioButton) findViewById(R.id.hungryInBedRadioYes);
                yes.toggle();
            }

            Spinner skippedMeals= (Spinner) findViewById(R.id.editskippedMeals);
            skippedMeals.setSelection(getIndex(skippedMeals, ses.getSkippedMeals()));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addeditentry, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle(R.string.exit_without_saving)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmNoSave();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveButton) {
            if (saveEntry()) {
                super.onBackPressed();
                Toast.makeText(this, R.string.saveSuccessful,
                        Toast.LENGTH_LONG).show();
            } else {
                showAlertDialogue(R.string.saveNotSuccessful);
            }
        }

        if (id == R.id.deleteButton) {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle(R.string.confirm_delete)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmDelete();
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the current question answers in a SocioEconomicStudy bean in the local database
     *
     * @return true if the SocioEconomicStudy was successfully saved/updated, false otherwise
     */
    private boolean saveEntry() {
        boolean result = false;
        SocioEconomicStudy ses = getSocioEconomicStudy();

        if(ses.getId() != null) {
            result = db.updateEntry(ses);
        } else {
            result = db.addNewEntry(ses);
        }
        return result;
    }

    /**
     * Deletes an entry from the local database
     *
     * @return true if the SocioEconomicStudy was successfully deleted, false otherwise
     */
    private boolean confirmDelete() {
        boolean result = false;
        EditText name = (EditText) findViewById(R.id.editName);

        if (name.getTag() != null) {
            result = db.deleteEntry((Integer) name.getTag());
        } else {
            result = true;
        }
        if (result) {
            super.onBackPressed();
            Toast.makeText(this, R.string.deleteSuccessful,
                    Toast.LENGTH_LONG).show();
        } else {
            showAlertDialogue(R.string.deleteNotSuccessful);
        }
        return result;
    }

    /**
     * Gets the index of a specific answer in a spinner.
     *
     * @param spinner The spinner
     * @param string The answer
     * @return the Index
     */
    private int getIndex(Spinner spinner, String string)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void confirmNoSave() {
        super.onBackPressed();
    }

    /**
     * Retrieves the current answers to each question form the user interface, and creates a
     * SocioEconomicStudy bean from the answers.
     *
     * @return the SocioEconomicStudy
     */
    @NonNull
    private SocioEconomicStudy getSocioEconomicStudy() {
        SocioEconomicStudy ses = new SocioEconomicStudy();

        EditText community = (EditText) findViewById(R.id.editCommunity);
        ses.setCommunity(community.getText().toString());

        EditText name = (EditText) findViewById(R.id.editName);
        ses.setName(name.getText().toString());

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.hungryInBedRadioGroup);
        ses.setHungryInBed(radioGroup.getCheckedRadioButtonId() == R.id.hungryInBedRadioYes ? 1 : 0);

        Spinner skippedMeals= (Spinner) findViewById(R.id.editskippedMeals);
        ses.setSkippedMeals(skippedMeals.getSelectedItem().toString());

        if (name.getTag() != null) {
            ses.setId((Integer) name.getTag());
        }
        return ses;
    }

}
