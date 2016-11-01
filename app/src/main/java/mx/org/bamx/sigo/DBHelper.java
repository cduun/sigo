package mx.org.bamx.sigo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper contains all methods for accessing the local offline database. Each entry is assigned a
 * unique id (not identical to the external SIGO server).
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "beneficiaries";
    private static final String FAMILIES = "families";

    private static final String KEY_ID = "_id";
    private static final String KEY_COMMUNITY = "_community";
    private static final String KEY_NAME = "_name";
    private static final String KEY_SKIPPED_MEALS = "_skippedMeal";
    private static final String KEY_HUNGRY_IN_BED = "_hungryInBed";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDY_DETAIL_TABLE = "CREATE TABLE "+ FAMILIES + "(" +
                KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_COMMUNITY + " TEXT, " +
                KEY_NAME + " TEXT, " +
                KEY_HUNGRY_IN_BED + " INTEGER, " +
                KEY_SKIPPED_MEALS + " TEXT) ";

        db.execSQL(CREATE_STUDY_DETAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAMILIES);

        onCreate(db);

    }

    /**
     * Method for saving a SocioEconomicStudy in the local database.
     *
     * @param ses The SocioEconomicStudy to be added to the local database
     * @return true if the SocioEconomicStudy was successfully saved, false otherwise
     */
    public boolean addNewEntry(SocioEconomicStudy ses) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_COMMUNITY, ses.getCommunity());
        values.put(KEY_NAME, ses.getName());
        values.put(KEY_HUNGRY_IN_BED, ses.isHungryInBed());
        values.put(KEY_SKIPPED_MEALS, ses.getSkippedMeals());

        long result = db.insert(FAMILIES, null, values);
        db.close();

        if (result > 0) {
            return true;
        }
        return false;
    }


    /**
     * Method for retrieving all SocioEconomicStudies from the local database.
     *
     * @return List of SocioEconomicStudies currently persisted in the local database
     */
    public List<SocioEconomicStudy> getAllEntries() {
        List<SocioEconomicStudy> studyList = new ArrayList<SocioEconomicStudy>();

        String selectQuery = "SELECT * FROM " + FAMILIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SocioEconomicStudy ses = new SocioEconomicStudy();
                ses.setId(Integer.parseInt(cursor.getString(0)));
                ses.setCommunity(cursor.getString(1));
                ses.setName(cursor.getString(2));
                ses.setHungryInBed(cursor.getInt(3));
                ses.setSkippedMeals(cursor.getString(4));

                studyList.add(ses);
            } while (cursor.moveToNext());
        }

        return studyList;
    }

    /**
     * Method for retrieving number of SocioEconomicStudies currently found in the local database.
     *
     * @return The number of SocioEconomicStudies currently found in the local database
     */

    public int getNumberOfEntries() {
        String countQuery = "SELECT * FROM " + FAMILIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    /**
     * method of updating an existing SocioEconomicStudy in the local database.
     *
     * @param ses The SocioEconomicStudy to update
     * @return true if the SocioEconomicStudy was successfully updated, false otherwise
     */

    public boolean updateEntry(SocioEconomicStudy ses) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COMMUNITY, ses.getCommunity());
        values.put(KEY_NAME, ses.getName());
        values.put(KEY_HUNGRY_IN_BED, ses.isHungryInBed());
        values.put(KEY_SKIPPED_MEALS, ses.getSkippedMeals());

        long result = db.update(FAMILIES, values, KEY_ID + " = ?",
                new String[]{ses.getId().toString()});

        if (result > 0) {
            return true;
        }
        return false;
    }

    /**
     * Method for deleting a SocioEconomicStudy from the database.
     *
     * @param id The id of the entry
     * @return true if the SocioEconomicStudy was successfully deleted, false otherwise
     */
    public boolean deleteEntry(int id) {
        String string_id = String.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(FAMILIES, KEY_ID + " = ?",
        new String[] { string_id });
        db.close();

        if (result > 0) {
            return true;
        }
        return false;
    }

    /**
     * Method for retrieving current communities from the database.
     *
     * @return ArrayList<String> containing current communities (each unique community only added once)
     */
    public ArrayList<String> getCommunities() {
        ArrayList<String> communities = new ArrayList<String>();

        String selectQuery = "SELECT DISTINCT " + KEY_COMMUNITY + " FROM " + FAMILIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                communities.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return communities;
    }

    /**
     * Method for deleting all entries in the local database
     *
     */
    public void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + FAMILIES);

        onCreate(db);
    }

    /**
     * Method for retrieving all entries in the local database currently associated with a
     * specific community.
     *
     * @param community The community
     * @return ArrayList<FamilyItem> containing family name and id of entries in the local database
     * associated with the provided community
     */
    public ArrayList<FamilyItem> getFamiliesForCommunity(String community) {
        ArrayList<FamilyItem> families = new ArrayList<FamilyItem>();

        String selectQuery = "SELECT " + KEY_ID + ", " + KEY_NAME + " FROM " + FAMILIES + " WHERE " + KEY_COMMUNITY + " = '" + community + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                families.add(new FamilyItem(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        return families;
    }

    /**
     * method for retrieving a specific entry from the local database.
     *
     * @param id The id of the entry
     * @return The SocioEconomicStudy associated with the provided id
     */
    public SocioEconomicStudy getEntry(String id) {
        List<SocioEconomicStudy> studyList = new ArrayList<SocioEconomicStudy>();

        String selectQuery = "SELECT * FROM " + FAMILIES + " WHERE " + KEY_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        SocioEconomicStudy ses = new SocioEconomicStudy();

        if (cursor.moveToFirst()) {
                ses.setId(Integer.parseInt(cursor.getString(0)));
                ses.setCommunity(cursor.getString(1));
                ses.setName(cursor.getString(2));
                ses.setHungryInBed(cursor.getInt(3));
                ses.setSkippedMeals(cursor.getString(4));
        }

        return ses;
    }
}
