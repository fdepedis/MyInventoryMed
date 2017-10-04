package it.flaviodepedis.myinventorymed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

/**
 * Created by flavio.depedis on 03/10/2017.
 */
public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * ----------------------- Delete All dummy medicines ---------------------
     * Helper method to delete all medicine data into the database.
     * ------------------------------------------------------------------------
     */
    public static void deleteAllMedicines(Context context) {
        int rowsDeleted = context.getContentResolver().delete(InventoryMedEntry.CONTENT_URI, null, null);
        if (rowsDeleted > 0) {
            Toast.makeText(context, R.string.label_all_medicines_deleted,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.error_delete_all_medicines,
                    Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, String.valueOf(R.string.error_delete_all_medicines));
        }
    }

    /**
     * ------------------ Delete medicine in Details Activity -----------------
     * Helper method to delete the current medicine data into the database.
     * ------------------------------------------------------------------------
     */
    public static void deleteMedicine(final Activity context, Uri mCurrentMedUri) {

        // Only perform the delete if this is an existing medicine.
        if (mCurrentMedUri != null) {
            // Call the ContentResolver to delete the medicine at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentMedUri
            // content URI already identifies the medicine that we want.
            int rowsDeleted = context.getContentResolver().delete(mCurrentMedUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(context, R.string.error_delete_med_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(context, R.string.error_delete_med_successful,
                        Toast.LENGTH_SHORT).show();
            }
            context.finish();
        }
    }

    /**
     * ----------------------- Insert dummy medicine ---------------------
     * Helper method to insert hardcoded medicine data into the database.
     * For debugging purposes only.
     * -------------------------------------------------------------------
     */
    public static void insertMedicine(Context context) {
        // Create a ContentValues object where column names are the keys,
        // and Momentdol medicine attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryMedEntry.COLUMN_MED_NAME, "Momentdol - 30mg");
        values.put(InventoryMedEntry.COLUMN_MED_TYPE, InventoryMedEntry.TYPE_PASTICCHE);
        values.put(InventoryMedEntry.COLUMN_MED_QUANTITY, 30);
        values.put(InventoryMedEntry.COLUMN_MED_EXP_DATE, "2023/01/01");
        values.put(InventoryMedEntry.COLUMN_MED_PRICE, 120.00);
        values.put(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT, 0.00);
        //values.put(InventoryMedEntry.COLUMN_MED_IMAGE, "");
        values.put(InventoryMedEntry.COLUMN_MED_NOTE, "Headache");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link InventoryMedEntry#CONTENT_URI} to indicate that we want to insert
        // into the medicines database table.
        Uri newUri = context.getContentResolver().insert(InventoryMedEntry.CONTENT_URI, values);
    }

    /**
     * Show message before delete current medicine in inventory
     */
    public static void showMessageDelete(final Activity context, final Uri mCurrentProductUri) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.label_title_show_message);
        builder.setMessage(R.string.label_message_delete_med);
        builder.setPositiveButton(R.string.label_positive_show_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteMedicine(context, mCurrentProductUri);
            }
        });
        builder.setNegativeButton(R.string.label_negative_show_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.label_negative_show_message,
                        Toast.LENGTH_LONG).show();
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show message before delete all medicines in inventory
     */
    public static void showMessageDeleteAll(final Context context) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.label_title_show_message);
        builder.setMessage(R.string.label_message_delete_all_med);
        builder.setPositiveButton(R.string.label_positive_show_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAllMedicines(context);
            }
        });
        builder.setNegativeButton(R.string.label_negative_show_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, R.string.label_negative_show_message,
                        Toast.LENGTH_LONG).show();
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void adjustInventory(
            Context context, Uri mCurrentProductUri, String previousValue, int variance){

        int currQuantity;

        // if greater than 1
        if(variance > 0){
            currQuantity = Integer.parseInt(previousValue) + variance;
        } else {
            currQuantity = Integer.parseInt(previousValue) + variance;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryMedEntry.COLUMN_MED_QUANTITY, currQuantity);
        context.getContentResolver().update(mCurrentProductUri, values, null, null);

    }
}
