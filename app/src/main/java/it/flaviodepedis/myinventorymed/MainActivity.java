package it.flaviodepedis.myinventorymed;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
        //InventoryMedDbHelper mDbHelper = new InventoryMedDbHelper(this);

        /** Insert dummy records to test */
        //insertMedicine();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the medicine data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        /** Read the table through a cursor */
        /*
        Cursor medicineCursor = mDbHelper.queryMedicine();



            // Fa la stessa cosa del Log.i dentro il ciclo while
            // Stampa tutto il contenuto del cursore
            // DatabaseUtils.dumpCursor(medicineCursor);

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
