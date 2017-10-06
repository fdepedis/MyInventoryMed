package it.flaviodepedis.myinventorymed.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import it.flaviodepedis.myinventorymed.data.InventoryMedContract.*;

/**
 * Created by flavio.depedis on 28/09/2017.
 */
public class InventoryMedProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryMedProvider.class.getSimpleName();

    /**
     * Database helper object
     */
    private InventoryMedDbHelper mDbHelper;

    /**
     * URI matcher code for the content URI for the medicines table
     */
    private static final int MEDICINES = 100;

    /**
     * URI matcher code for the content URI for a single medicine in the medicines table
     */
    private static final int MEDICINE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(InventoryMedContract.CONTENT_AUTHORITY, InventoryMedContract.PATH_MEDICINES, MEDICINES);
        sUriMatcher.addURI(InventoryMedContract.CONTENT_AUTHORITY, InventoryMedContract.PATH_MEDICINES + "/#", MEDICINE_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryMedDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                cursor = database.query(InventoryMedEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case MEDICINE_ID:
                selection = InventoryMedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryMedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //If the data at this URI changes, then update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return insertMedicine(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryMedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDICINE_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryMedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryMedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            // Notify all listners that the data has changed for the medicine content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows affected.
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return updateMedicine(uri, values, selection, selectionArgs);
            case MEDICINE_ID:
                // For the MEDICINE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryMedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMedicine(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return InventoryMedEntry.CONTENT_LIST_TYPE;
            case MEDICINE_ID:
                return InventoryMedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert a medicine into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMedicine(Uri uri, ContentValues values) {

        // Check valid insert data
        sanityCheck(values);

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new medicine with the given values
        long id = database.insert(InventoryMedEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the medicine content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update medicines in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more medicines).
     * Return the number of rows that were successfully updated.
     */
    private int updateMedicine(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check valid modified data
        sanityCheck(values);

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryMedEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            // Notify all listeners that the data has changed for the medicine content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows affected.
        return rowsUpdated;
    }

    /**
     * Method to sanityCheck the values in ContentValues object.
     * This method is called from insertMedicine() and updateMedicine() method.
     *
     * @param values key-value pairs of data
     */
    private void sanityCheck(ContentValues values) {

        // Check valid data for column name
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_NAME)) {
            // Check that the name is not null
            String name = values.getAsString(InventoryMedEntry.COLUMN_MED_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Medicine requires a name");
            }
        }

        // Check valid data for column type
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_TYPE)) {
            // Check that the medicine type is valid
            Integer type = values.getAsInteger(InventoryMedEntry.COLUMN_MED_TYPE);
            if (type == null || !InventoryMedEntry.isValidMedicineType(type)) {
                throw new IllegalArgumentException("Medicine requires valid type");
            }
        }

        // Check valid data for column quantity
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_QUANTITY)) {
            // If the quantity is provided, check that it's greater than or equal to 0
            Integer quantity = values.getAsInteger(InventoryMedEntry.COLUMN_MED_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Medicine requires valid quantity");
            }
        }

        // Check valid data for column expiry date
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_EXP_DATE)) {
            // Check that the expiry date is not null
            String date = values.getAsString(InventoryMedEntry.COLUMN_MED_EXP_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Medicine requires a valid expiry date");
            }
        }

        // Check valid data for column price
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_PRICE)) {
            // Check that the expiry date is not null
            Double price = values.getAsDouble(InventoryMedEntry.COLUMN_MED_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Medicine requires a valid price");
            }
        }

        // Check valid data for column price discount
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT)) {
            // Check that the expiry date is not null
            Double price_discount = values.getAsDouble(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT);
            if (price_discount == null || price_discount < 0) {
                throw new IllegalArgumentException("Medicine requires a valid price discount");
            }
        }

        // Check valid data for column supplier name
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_SUP_NAME)) {
            // Check that the name is not null
            String name = values.getAsString(InventoryMedEntry.COLUMN_MED_SUP_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Medicine requires a supplier name");
            }
        }

        // Check valid data for column supplier phone
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_SUP_PHONE)) {
            // Check that the name is not null
            String name = values.getAsString(InventoryMedEntry.COLUMN_MED_SUP_PHONE);
            if (name == null) {
                throw new IllegalArgumentException("Medicine requires a supplier phone");
            }
        }

        // Check valid data for column supplier email
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_SUP_EMAIL)) {
            // Check that the name is not null
            String name = values.getAsString(InventoryMedEntry.COLUMN_MED_SUP_EMAIL);
            if (name == null) {
                throw new IllegalArgumentException("Medicine requires a supplier email");
            }
        }

        // Check valid data for column note
        if (values.containsKey(InventoryMedEntry.COLUMN_MED_NOTE)) {
            // Check that the expiry date is not null
            String date = values.getAsString(InventoryMedEntry.COLUMN_MED_NOTE);
            if (date == null) {
                throw new IllegalArgumentException("Medicine requires a valid note");
            }
        }
    }
}

