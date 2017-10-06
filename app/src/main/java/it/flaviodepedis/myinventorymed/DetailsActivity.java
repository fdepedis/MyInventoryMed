package it.flaviodepedis.myinventorymed;

import android.app.LoaderManager;
import com.squareup.picasso.Picasso;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private Uri mCurrentMedUri;

    private String medTypeString;

    private static final int MED_LOADER_ID = 0;
    private static final int MED_INC = 1;
    private static final int MED_DEC = -1;

    /** Declare all view in this activity */
    // TextView for label
    @BindView(R.id.tv_label_med_name) TextView tvLabelMedName;
    @BindView(R.id.tv_label_med_type) TextView tvLabelMedType;
    @BindView(R.id.tv_label_med_quantity) TextView tvLabelMedQuantity;
    @BindView(R.id.tv_label_med_price) TextView tvLabelMedPrice;
    @BindView(R.id.tv_label_med_discount) TextView tvLabelMedDiscountPrice;
    @BindView(R.id.tv_label_med_exp_date) TextView tvLabelMedExpDate;
    @BindView(R.id.tv_label_med_note) TextView tvLabelMedNote;
    @BindView(R.id.tv_label_med_sup_name) TextView tvLabelMedSupName;
    @BindView(R.id.tv_label_med_sup_phone) TextView tvLabelMedSupPhone;
    @BindView(R.id.tv_label_med_sup_email) TextView tvLabelMedSupEmail;

    // TextView for values
    @BindView(R.id.tv_value_med_name) TextView tvValueMedName;
    @BindView(R.id.tv_value_med_type) TextView tvValueMedType;
    @BindView(R.id.tv_value_med_quantity) TextView tvValueMedQuantity;
    @BindView(R.id.tv_value_med_price) TextView tvValueMedPrice;
    @BindView(R.id.tv_value_med_discount) TextView tvValueMedDiscountPrice;
    @BindView(R.id.tv_value_med_exp_date) TextView tvValueMedExpDate;
    @BindView(R.id.tv_value_med_note) TextView tvValueMedNote;
    @BindView(R.id.tv_value_med_sup_name) TextView tvValueMedSupName;
    @BindView(R.id.tv_value_med_sup_phone) TextView tvValueMedSupPhone;
    @BindView(R.id.tv_value_med_sup_email) TextView tvValueMedSupEmail;

    // ImageView and ImageButton
    @BindView(R.id.img_med_inc) ImageButton imgBtnAdd;
    @BindView(R.id.img_med_dec) ImageButton imgBtnRemove;
    @BindView(R.id.img_medicine) ImageView imgMedicine;
    @BindView(R.id.img_sup_phone) ImageView imgSupPhone;
    @BindView(R.id.img_sup_email) ImageView imgSupEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();
        if(mCurrentMedUri != null) {
            getLoaderManager().initLoader(MED_LOADER_ID, null, this);
        }

        imgBtnAdd.setOnClickListener(this);
        imgBtnRemove.setOnClickListener(this);
        imgSupPhone.setOnClickListener(this);
        imgSupEmail.setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Get all column from database respect to current mCurrentMedUri and
        // no projection needed, because retrieve all column
        return new CursorLoader(
                this,                       // Parent activity context
                mCurrentMedUri,             // Provider content URI to query
                null,                       // Columns to include in the resulting Cursor
                null,                       // No Where clause
                null,                       // No Where arguments clause
                null);                      // No Order by
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /**
         * ------------------------------------------------------------------------------
         * Prints all cursor content (Thank you at my last reviewer on the last project)
         * ------------------------------------------------------------------------------
         */
        DatabaseUtils.dumpCursor(cursor);

        Log.i(LOG_TAG, "into - onLoadFinished");

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // When you receive a Cursor result from the loader, remember to move the cursor to
        // the 0th position, before you start extracting out column values from it.
        if (cursor.moveToFirst()) {
            // Find the columns of medicine attributes that we're interested in
            int medNameColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_NAME);
            int medTypeColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_TYPE);
            int medQuantityColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_QUANTITY);
            int medExpDateColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_EXP_DATE);
            int medPriceColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_PRICE);
            int medPriceDiscountColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT);
            int medImageColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_IMAGE);
            int medSupNameColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_SUP_NAME);
            int medSupPhoneColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_SUP_PHONE);
            int medSupEmailColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_SUP_EMAIL);
            int medNoteColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_NOTE);

            // Extract out the value from the Cursor for the given column index
            String medName = cursor.getString(medNameColumnIndex);
            Integer medType = cursor.getInt(medTypeColumnIndex);
            Integer medQuantity = cursor.getInt(medQuantityColumnIndex);
            String medExpDate = cursor.getString(medExpDateColumnIndex);
            Double medPrice = cursor.getDouble(medPriceColumnIndex);
            Double medPriceDiscount = cursor.getDouble(medPriceDiscountColumnIndex);
            String image = cursor.getString(medImageColumnIndex);
            String medSupName = cursor.getString(medSupNameColumnIndex);
            String medSupPhone = cursor.getString(medSupPhoneColumnIndex);
            String medSupEmail = cursor.getString(medSupEmailColumnIndex);
            String medNote = cursor.getString(medNoteColumnIndex);

            // Set TextView text with the value from the database
            tvValueMedName.setText(medName);

            // Set spinner
            switch (medType) {
                case InventoryMedEntry.TYPE_LIQUIDO:
                    medTypeString = getString(R.string.label_type_med_liquido);
                    break;
                case InventoryMedEntry.TYPE_SUPPOSTE:
                    medTypeString = getString(R.string.label_type_med_supposte);
                    break;
                case InventoryMedEntry.TYPE_PASTICCHE:
                    medTypeString = getString(R.string.label_type_med_pasticche);
                    break;
                case InventoryMedEntry.TYPE_SCIROPPO:
                    medTypeString = getString(R.string.label_type_med_sciroppo);
                    break;
                case InventoryMedEntry.TYPE_CREMA:
                    medTypeString = getString(R.string.label_type_med_crema);
                    break;
                case InventoryMedEntry.TYPE_GEL:
                    medTypeString = getString(R.string.label_type_med_gel);
                    break;
                default:
                    medTypeString = getString(R.string.label_type_med_unknown);
                    break;
            }
            tvValueMedType.setText(medTypeString);

            tvValueMedQuantity.setText(String.valueOf(medQuantity));
            tvValueMedPrice.setText(String.valueOf(medPrice));

            if(medPriceDiscount > 0){
                tvValueMedDiscountPrice.setText(String.valueOf(medPriceDiscount));
                tvValueMedDiscountPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else {
                tvValueMedDiscountPrice.setText(getString(R.string.empty_discount_price_details));
            }

            // verify if image medicine exist
            if(image == null || image.equals("")) {
                Picasso.with(getApplicationContext()).load(R.drawable.ic_image_not_found).into(imgMedicine);
            } else {
                Uri uri = Uri.parse(image);
                imgMedicine.setImageURI(uri);
            }
            tvValueMedExpDate.setText(medExpDate);
            tvValueMedSupName.setText(medSupName);
            tvValueMedSupPhone.setText(medSupPhone);
            tvValueMedSupEmail.setText(medSupEmail);
            tvValueMedNote.setText(medNote);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        tvValueMedName.setText("");
        tvValueMedType.setText("");
        tvValueMedQuantity.setText("");
        tvValueMedPrice.setText("");
        tvValueMedDiscountPrice.setText("");
        imgMedicine.setImageURI(null);
        tvValueMedExpDate.setText("");
        tvValueMedSupPhone.setText("");
        tvValueMedSupEmail.setText("");
        tvValueMedNote.setText("");
        tvValueMedNote.setText("");
    }

    /**
     * Get the view and define which action submit
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.img_med_inc:
                Utils.adjustInventory(
                        this,
                        mCurrentMedUri,
                        String.valueOf(tvValueMedQuantity.getText()),
                        MED_INC);
                break;
            case R.id.img_med_dec:
                Utils.adjustInventory(
                        this,
                        mCurrentMedUri,
                        String.valueOf(tvValueMedQuantity.getText()),
                        MED_DEC);
                break;
            case R.id.img_sup_phone:
                Utils.callSupplierPhone(
                        this,
                        mCurrentMedUri,
                        String.valueOf(tvValueMedSupPhone.getText()));
                break;
            case R.id.img_sup_email:
                Utils.sendSupplierEmail(
                        this,
                        mCurrentMedUri,
                        String.valueOf(tvValueMedSupEmail.getText()));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_details.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to a click on Delete menu icon
            case R.id.delete_med:
                Utils.showMessageDelete(this, mCurrentMedUri);
                return true;
            // Respond to a click on Edit menu icon
            case R.id.edit_med:
                openEditorActivity();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to open EditorActivity
     */
    private void openEditorActivity() {
        Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
        intent.setData(mCurrentMedUri);
        startActivity(intent);
    }
}
