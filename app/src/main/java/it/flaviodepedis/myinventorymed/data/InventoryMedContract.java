package it.flaviodepedis.myinventorymed.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by flavio.depedis on 28/09/2017.
 */

public class InventoryMedContract {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryMedContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "it.flaviodepedis.myinventorymed";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * This constants stores the path for table "medicines"
     */
    public static final String PATH_MEDICINES = "medicines";

    public static final class InventoryMedEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEDICINES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of medicines.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single medicines.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINES;

        /**
         * Name of database table for medicines
         */
        public static final String TABLE_NAME = "medicines";

        /**
         * Name of columns table for medicines
         */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_MED_NAME = "med_name";                    // Type: TEXT
        public static final String COLUMN_MED_TYPE = "med_type";                    // Type: TEXT
        public static final String COLUMN_MED_QUANTITY = "med_quantity";            // Type: INTEGER
        public static final String COLUMN_MED_EXP_DATE = "med_expiry_date";         // Type: TEXT
        public static final String COLUMN_MED_PRICE = "med_price";                  // Type: REAL
        public static final String COLUMN_MED_PRICE_DISCOUNT = "med_price_discount";// Type: REAL
        public static final String COLUMN_MED_IMAGE = "med_image";                  // Type: TEXT
        public static final String COLUMN_MED_NOTE = "med_note";                    // Type: TEXT
        public static final String COLUMN_MED_SUP_NAME = "med_sup_name";            // Type: TEXT
        public static final String COLUMN_MED_SUP_PHONE = "med_sup_phone";          // Type: TEXT
        public static final String COLUMN_MED_SUP_EMAIL = "med_sup_email";          // Type: TEXT

        /**
         * Name of constants for medicines
         */
        public static final int TYPE_UNKNOWN = 0;                                //"Sconosciuto"
        public static final int TYPE_LIQUIDO = 1;                                //"Liquido"
        public static final int TYPE_SUPPOSTE = 2;                               //"Supposte"
        public static final int TYPE_PASTICCHE = 3;                              //"Pasticche"
        public static final int TYPE_SCIROPPO = 4;                               //"Sciroppo"
        public static final int TYPE_CREMA = 5;                                  //"Crema"
        public static final int TYPE_GEL = 6;                                    //"Gel"

        /**
         * Returns whether or not the given type medicine value is valid
         */
        public static boolean isValidMedicineType(int type) {
            if (type == TYPE_LIQUIDO || type == TYPE_SUPPOSTE || type == TYPE_PASTICCHE ||
                    type == TYPE_SCIROPPO || type == TYPE_CREMA || type == TYPE_GEL ||
                    type == TYPE_UNKNOWN) {
                return true;
            }
            return false;
        }
    }
}

