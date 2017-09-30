package it.flaviodepedis.myinventorymed;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import it.flaviodepedis.myinventorymed.data.InventoryMedContract.*;
import it.flaviodepedis.myinventorymed.data.InventoryMedDbHelper;

/**
 * Created by flavio.depedis on 28/09/2017.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Define an instance of DBHelper class */
        InventoryMedDbHelper mDbHelper = new InventoryMedDbHelper(this);

        /** Insert dummy records to test */
        insertMedicine();

        /** Read the table through a cursor */
        /*
        Cursor medicineCursor = mDbHelper.queryMedicine();

        try {
            while (medicineCursor.moveToNext()) {
                Log.i(LOG_TAG,
                        "Med Name: " + medicineCursor.getString(0) + "\n"
                                + "Med Type: " + medicineCursor.getString(1) + "\n"
                                + "Med Quantity: " + medicineCursor.getInt(2) + "\n"
                                + "Med Expiry Date: " + medicineCursor.getString(3) + "\n"
                                + "Med Note: " + medicineCursor.getString(4) + "\n" );
            }

            // Fa la stessa cosa del Log.i dentro il ciclo while
            // Stampa tutto il contenuto del cursore
            // DatabaseUtils.dumpCursor(medicineCursor);

        } finally {
            medicineCursor.close();
        }
        */
    }

    /**
     * ----------------------- Insert dummy medicine ---------------------
     * Helper method to insert hardcoded medicine data into the database.
     * For debugging purposes only.
     * -------------------------------------------------------------------
     */
    private void insertMedicine() {
        // Create a ContentValues object where column names are the keys,
        // and Momentdol medicine attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryMedEntry.COLUMN_MED_NAME, "Momentdol");
        values.put(InventoryMedEntry.COLUMN_MED_TYPE, InventoryMedEntry.TYPE_PASTICCHE);
        values.put(InventoryMedEntry.COLUMN_MED_QUANTITY, 30);
        values.put(InventoryMedEntry.COLUMN_MED_EXP_DATE, "2020/01/01");
        values.put(InventoryMedEntry.COLUMN_MED_NOTE, "Headache");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link InventoryMedEntry#CONTENT_URI} to indicate that we want to insert
        // into the medicines database table.
        Uri newUri = getContentResolver().insert(InventoryMedEntry.CONTENT_URI, values);
    }
}
