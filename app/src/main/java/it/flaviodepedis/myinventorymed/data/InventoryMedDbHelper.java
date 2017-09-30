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

    public InventoryMedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the medicines table
        String SQL_CREATE_MEDICINES_TABLE = "CREATE TABLE " + InventoryMedEntry.TABLE_NAME + " ("
                + InventoryMedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryMedEntry.COLUMN_MED_NAME + " TEXT NOT NULL, "
                + InventoryMedEntry.COLUMN_MED_TYPE + " TEXT NOT NULL, "
                + InventoryMedEntry.COLUMN_MED_QUANTITY + " INTEGER NOT NULL, "
                + InventoryMedEntry.COLUMN_MED_EXP_DATE + " TEXT NOT NULL,"
                + InventoryMedEntry.COLUMN_MED_NOTE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MEDICINES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

