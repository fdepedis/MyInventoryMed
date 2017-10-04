package it.flaviodepedis.myinventorymed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private Uri mCurrentProductUri;

    private static final int MED_LOADER_ID = 0;

    /** Declare all TextView in this activity */
    // TextView for label
    @BindView(R.id.tv_label_med_name) TextView tvLabelMedName;
    @BindView(R.id.tv_label_med_type) TextView tvLabelMedType;
    @BindView(R.id.tv_label_med_quantity) TextView tvLabelMedQuantity;
    @BindView(R.id.tv_label_med_price) TextView tvLabelMedPrice;
    @BindView(R.id.tv_label_med_discount) TextView tvLabelMedDiscountPrice;
    @BindView(R.id.tv_label_med_exp_date) TextView tvLabelMedExpDate;
    @BindView(R.id.tv_label_med_note) TextView tvLabelMedNote;

    // TextView for values
    @BindView(R.id.tv_value_med_name) TextView tvValueMedName;
    @BindView(R.id.tv_value_med_type) TextView tvValueMedType;
    @BindView(R.id.tv_value_med_quantity) TextView tvValueMedQuantity;
    @BindView(R.id.tv_value_med_price) TextView tvValueMedPrice;
    @BindView(R.id.tv_value_med_discount) TextView tvValueMedDiscountPrice;
    @BindView(R.id.tv_value_med_exp_date) TextView tvValueMedExpDate;
    @BindView(R.id.tv_value_med_note) TextView tvValueMedNote;

    // ImageView and ImageButton
    @BindView(R.id.img_med_inc) ImageButton imgBtnAdd;
    @BindView(R.id.img_med_dec) ImageButton imgBtnRemove;
    @BindView(R.id.image_product) ImageView imgMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if(mCurrentProductUri != null) {
            getLoaderManager().initLoader(MED_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,                       // Parent activity context
                mCurrentProductUri,         // Provider content URI to query
                null,                       // Columns to include in the resulting Cursor
                null,                       // No Where clause
                null,                       // No Where arguments clause
                null);                      // No Order by
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

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
            int medNoteColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_NOTE);

            // Extract out the value from the Cursor for the given column index
            String medName = cursor.getString(medNameColumnIndex);
            String medType = cursor.getString(medTypeColumnIndex);
            int medQuantity = cursor.getInt(medQuantityColumnIndex);
            String medExpDate = cursor.getString(medExpDateColumnIndex);
            Double medPrice = cursor.getDouble(medPriceColumnIndex);
            Double medPriceDiscount = cursor.getDouble(medPriceDiscountColumnIndex);
            String medNote = cursor.getString(medNoteColumnIndex);

            // Set TextView text with the value from the database
            tvValueMedName.setText(medName);
            tvValueMedType.setText(medType);
            tvValueMedQuantity.setText(String.valueOf(medQuantity));
            tvValueMedPrice.setText(String.valueOf(medPrice));
            //tvValueMedDiscountPrice.setText(String.valueOf(medPriceDiscount));
            if(medPriceDiscount > 0){
                tvValueMedDiscountPrice.setText(String.valueOf(medPriceDiscount));
                tvValueMedDiscountPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else {
                tvValueMedDiscountPrice.setText(getString(R.string.empty_discount_price_details));
            }
            tvValueMedExpDate.setText(medExpDate);
            tvValueMedNote.setText(medNote);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
