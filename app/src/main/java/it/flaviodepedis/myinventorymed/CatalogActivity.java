package it.flaviodepedis.myinventorymed;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import it.flaviodepedis.myinventorymed.data.InventoryMedContract.*;

/**
 * Created by flavio.depedis on 28/09/2017.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int MED_LOADER_ID = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /** Insert dummy records to test */
        //insertMedicine();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the medicine data
        ListView medListView = (ListView) findViewById(R.id.list);

        medListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(InventoryMedEntry.CONTENT_URI,id);

                Log.w("CatalogActivity", InventoryMedEntry.CONTENT_URI + "/" + id );

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        medListView.setEmptyView(emptyView);

        // Creates adaptor which makes list item for every row of data in table
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        medListView.setAdapter(mCursorAdapter);

        // Start the loader
        getLoaderManager().initLoader(MED_LOADER_ID, null, this);

        // Stampa tutto il contenuto del cursore
        // DatabaseUtils.dumpCursor(medicineCursor);
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
        values.put(InventoryMedEntry.COLUMN_MED_PRICE, 20.00);
        values.put(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT, 15.00);
        //values.put(InventoryMedEntry.COLUMN_MED_IMAGE, "");
        values.put(InventoryMedEntry.COLUMN_MED_NOTE, "Headache");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link InventoryMedEntry#CONTENT_URI} to indicate that we want to insert
        // into the medicines database table.
        Uri newUri = getContentResolver().insert(InventoryMedEntry.CONTENT_URI, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryMedEntry._ID,
                InventoryMedEntry.COLUMN_MED_NAME,
                InventoryMedEntry.COLUMN_MED_TYPE,
                InventoryMedEntry.COLUMN_MED_QUANTITY,
                InventoryMedEntry.COLUMN_MED_EXP_DATE,
                InventoryMedEntry.COLUMN_MED_PRICE,
                InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT
        };

        return new CursorLoader(this,               // Parent activity context
                InventoryMedEntry.CONTENT_URI,      // Provider content URI to query
                projection,                         // Columns to include in the resulting Cursor
                null,                               // No Where clause
                null,                               // No Where arguments clause
                null);                              // No Order by
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor cursor data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no longer using it.
        mCursorAdapter.swapCursor(null);
    }
}
