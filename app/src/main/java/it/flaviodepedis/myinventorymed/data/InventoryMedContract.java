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

        /**
         * Name of constants for medicines
         */
        public static final String TYPE_LIQUIDO = "Liquido";
        public static final String TYPE_SUPPOSTE = "Supposte";
        public static final String TYPE_PASTICCHE = "Pasticche";
        public static final String TYPE_SCIROPPO = "Sciroppo";
        public static final String TYPE_CREMA = "Crema";
        public static final String TYPE_GEL = "Gel";
        public static final String TYPE_UNKNOWN = "Sconosciuto";

        /**
         * Returns whether or not the given type medicine value is valid
         */
        public static boolean isValidMedicineType(String type) {
            if (type.equals(TYPE_LIQUIDO) || type.equals(TYPE_SUPPOSTE) ||
                    type.equals(TYPE_PASTICCHE) || type.equals(TYPE_SCIROPPO) ||
                    type.equals(TYPE_CREMA) || type.equals(TYPE_GEL) || type.equals(TYPE_UNKNOWN)) {
                return true;
            }
            return false;
        }
    }
}

