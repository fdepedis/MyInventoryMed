package it.flaviodepedis.myinventorymed.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.flaviodepedis.myinventorymed.data.InventoryMedContract.*;


/**
 * Created by flavio.depedis on 28/09/2017.
 */
public class InventoryMedDbHelper extends SQLiteOpenHelper {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryMedDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "medicines.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Create a String that contains the SQL statement to create the medicines table
     */
    private final String SQL_CREATE_MEDICINES_TABLE = "CREATE TABLE "
            + InventoryMedEntry.TABLE_NAME + " ("
            + InventoryMedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryMedEntry.COLUMN_MED_NAME + " TEXT NOT NULL, "
            + InventoryMedEntry.COLUMN_MED_TYPE + " TEXT NOT NULL, "
            + InventoryMedEntry.COLUMN_MED_QUANTITY + " INTEGER NOT NULL, "
            + InventoryMedEntry.COLUMN_MED_EXP_DATE + " TEXT NOT NULL,"
            + InventoryMedEntry.COLUMN_MED_PRICE + " REAL NOT NULL,"
            + InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT + " REAL,"
            + InventoryMedEntry.COLUMN_MED_IMAGE + " TEXT NOT NULL,"
            + InventoryMedEntry.COLUMN_MED_NOTE + " TEXT,"
            + InventoryMedEntry.COLUMN_MED_SUP_NAME + " TEXT,"
            + InventoryMedEntry.COLUMN_MED_SUP_PHONE + " TEXT,"
            + InventoryMedEntry.COLUMN_MED_SUP_EMAIL + " TEXT);";

    /**
     * Create a String that contains the SQL statement to create the medicines table
     */
    private final String SQL_DROP_MEDICINES_TABLE = "DROP TABLE IF EXISTS "
            + InventoryMedEntry.TABLE_NAME;

    public InventoryMedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statement to create table
        db.execSQL(SQL_CREATE_MEDICINES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Execute the SQL statement to delete table and recreate again

        // COMMENT FOR APP READY TO PRODUCTION ENVIRONMENT
        /*sqLiteDatabase.execSQL(SQL_DROP_MEDICINES_TABLE);
        onCreate(sqLiteDatabase);*/
    }
}

