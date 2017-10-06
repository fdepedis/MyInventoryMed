package it.flaviodepedis.myinventorymed;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;
import it.flaviodepedis.myinventorymed.util.Utils;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Initialize the spinner to TYPE_UNKNOWN
     */
    private int mMedType = InventoryMedEntry.TYPE_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the medicine has been edited (true) or not (false)
     */
    private boolean mMedHasChanged = false;
    private Uri mPickedImage;
    private Uri mCurrentMedUri;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int EXISTING_MED_LOADER = 0;

    /**
     * Variables to check data from input fields
     */
    private String nameMed;
    private int typeMed;
    private String quantityMed;
    private String priceMed;
    private String discountPriceMed;
    private String expDateMed;
    private String noteMed;
    private String image = null;
    private Double price;
    private Double discountPrice;
    private String supNameMed;
    private String supPhoneMed;
    private String supEmailMed;

    /**
     * Declare all view in this activity
     */
    @BindView(R.id.et_value_editor_med_name) EditText etMedName;
    @BindView(R.id.spinner_med_type) Spinner mMedSpinner;
    @BindView(R.id.et_value_editor_med_quantity) EditText etMedQuantity;
    @BindView(R.id.et_value_editor_med_price) EditText etMedPrice;
    @BindView(R.id.et_value_editor_med_discount) EditText etMedDiscountPrice;
    @BindView(R.id.et_value_editor_med_exp_date) EditText etMedExpDate;
    @BindView(R.id.image_editor_med) ImageView imageMed;
    @BindView(R.id.et_value_editor_med_sup_name) EditText etMedSupName;
    @BindView(R.id.et_value_editor_med_sup_phone) EditText etMedSupPhone;
    @BindView(R.id.et_value_editor_med_sup_email) EditText etMedSupEmail;
    @BindView(R.id.et_value_editor_med_note) EditText etMedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();

        if (mCurrentMedUri == null) {
            setTitle(getResources().getString(R.string.label_editor_activity_title_new_med));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a medicine that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getResources().getString(R.string.label_editor_activity_title_edit_med));

            getLoaderManager().initLoader(EXISTING_MED_LOADER, null, this);
        }

        setupSpinner();

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        etMedName.setOnTouchListener(mTouchListener);
        mMedSpinner.setOnTouchListener(mTouchListener);
        etMedQuantity.setOnTouchListener(mTouchListener);
        etMedPrice.setOnTouchListener(mTouchListener);
        etMedDiscountPrice.setOnTouchListener(mTouchListener);
        etMedExpDate.setOnTouchListener(mTouchListener);
        imageMed.setOnTouchListener(mTouchListener);
        etMedNote.setOnTouchListener(mTouchListener);
        etMedSupName.setOnTouchListener(mTouchListener);
        etMedSupPhone.setOnTouchListener(mTouchListener);
        etMedSupEmail.setOnTouchListener(mTouchListener);

        //Image picker intent
        imageMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, getString(R.string.label_create_chooser_activity)), IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        // Since the editor shows all medicine attributes, define a projection that contains
        // all columns from the medicines table
        String[] projection = {
                InventoryMedEntry._ID,
                InventoryMedEntry.COLUMN_MED_NAME,
                InventoryMedEntry.COLUMN_MED_TYPE,
                InventoryMedEntry.COLUMN_MED_QUANTITY,
                InventoryMedEntry.COLUMN_MED_PRICE,
                InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT,
                InventoryMedEntry.COLUMN_MED_IMAGE,
                InventoryMedEntry.COLUMN_MED_SUP_NAME,
                InventoryMedEntry.COLUMN_MED_SUP_PHONE,
                InventoryMedEntry.COLUMN_MED_SUP_EMAIL,
                InventoryMedEntry.COLUMN_MED_EXP_DATE,
                InventoryMedEntry.COLUMN_MED_NOTE
        };

        return new CursorLoader(this,               // Parent activity context
                mCurrentMedUri,                     // Provider content URI to query
                projection,                         // Columns to include in the resulting Cursor
                null,                               // No Where clause
                null,                               // No Where arguments clause
                null);                              // No Order by
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /**
         * ------------------------------------------------------------------------------
         * Prints all cursor content (Thank you at my last reviewer on the last project)
         * ------------------------------------------------------------------------------
         */
        DatabaseUtils.dumpCursor(cursor);

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
            int medQuantity = cursor.getInt(medQuantityColumnIndex);
            String medExpDate = cursor.getString(medExpDateColumnIndex);
            Double medPrice = cursor.getDouble(medPriceColumnIndex);
            Double medPriceDiscount = cursor.getDouble(medPriceDiscountColumnIndex);
            String medImage = cursor.getString(medImageColumnIndex);
            String medSupName = cursor.getString(medSupNameColumnIndex);
            String medSupPhone = cursor.getString(medSupPhoneColumnIndex);
            String medSupEmail = cursor.getString(medSupEmailColumnIndex);
            String medNote = cursor.getString(medNoteColumnIndex);

            // Set TextView text with the value from the database
            etMedName.setText(medName);
            mMedSpinner.setSelection(medType);
            etMedQuantity.setText(String.valueOf(medQuantity));
            etMedPrice.setText(String.valueOf(medPrice));

            if (medPriceDiscount > 0) {
                etMedDiscountPrice.setText(String.valueOf(medPriceDiscount));
                etMedDiscountPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else {
                etMedDiscountPrice.setText(String.valueOf(0.00));
            }

            // verify if image medicine exist
            if (medImage == null || medImage.equals("")) {
                Picasso.with(getApplicationContext()).load(R.drawable.ic_image_not_found).into(imageMed);
            } else {
                Uri uri = Uri.parse(medImage);
                imageMed.setImageURI(uri);
                mPickedImage = uri;
            }
            etMedExpDate.setText(medExpDate);
            etMedSupName.setText(medSupName);
            etMedSupPhone.setText(medSupPhone);
            etMedSupEmail.setText(medSupEmail);
            etMedNote.setText(medNote);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        etMedName.setText("");
        mMedSpinner.setSelection(InventoryMedEntry.TYPE_UNKNOWN);
        etMedQuantity.setText("");
        etMedPrice.setText("");
        etMedDiscountPrice.setText("");
        etMedExpDate.setText("");
        imageMed.setImageURI(null);
        etMedSupName.setText("");
        etMedSupPhone.setText("");
        etMedSupEmail.setText("");
        etMedNote.setText("");
    }

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mMedHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mMedHasChanged = true;
            return false;
        }
    };

    /**
     * Executes when user returns from Image picker intent
     *
     * @param requestCode
     * @param resultCode
     * @param data        contains the URI of image the user picked
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //Return early and show message if there was error during picking image
            if (data == null) {
                Toast.makeText(this, getString(R.string.error_picking_image), Toast.LENGTH_SHORT).show();
                return;
            }
            //Store the URI of picked image
            mPickedImage = data.getData();
            //Set it on the ImageView
            imageMed.setImageURI(mPickedImage);
            //Change scaleType from centerInside to centerCrop
            imageMed.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the type of the medicine.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter medSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_med_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        medSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mMedSpinner.setAdapter(medSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mMedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.label_type_med_liquido))) {
                        mMedType = InventoryMedEntry.TYPE_LIQUIDO;                      // Liquido
                    } else if (selection.equals(getString(R.string.label_type_med_supposte))) {
                        mMedType = InventoryMedEntry.TYPE_SUPPOSTE;                     // Supposte
                    } else if (selection.equals(getString(R.string.label_type_med_pasticche))) {
                        mMedType = InventoryMedEntry.TYPE_PASTICCHE;                    // Pasticche
                    } else if (selection.equals(getString(R.string.label_type_med_sciroppo))) {
                        mMedType = InventoryMedEntry.TYPE_SCIROPPO;                     // Sciroppo
                    } else if (selection.equals(getString(R.string.label_type_med_crema))) {
                        mMedType = InventoryMedEntry.TYPE_CREMA;                        // Crema
                    } else if (selection.equals(getString(R.string.label_type_med_gel))) {
                        mMedType = InventoryMedEntry.TYPE_GEL;                          // Gel
                    } else {
                        mMedType = InventoryMedEntry.TYPE_UNKNOWN;                      // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMedType = InventoryMedEntry.TYPE_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_details.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     * <p>
     * Consente di nascondere delle parti di menu in base alle esigenze. In questo caso,
     * se si sta inserendo un nuovo medicinale il menu "Delete" viene nascosto.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new medicine, hide the "Delete" menu item.
        if (mCurrentMedUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_med);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_med:
                Utils.showMessageDelete(this, mCurrentMedUri);
                return true;
            case R.id.save_med:
                saveMedicine();
                //finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the medicine hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mMedHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                Utils.showUnsavedChangesDialog(this, discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the medicine hasn't changed, continue with handling back button press
        if (!mMedHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        Utils.showUnsavedChangesDialog(this, discardButtonClickListener);
    }

    /**
     * ------------------ Save medicine data into the database -----------------
     * Helper method to save medicine in the database.
     * -------------------------------------------------------------------------
     */
    private void saveMedicine() {

        nameMed = etMedName.getText().toString().trim();
        typeMed = mMedType;
        quantityMed = etMedQuantity.getText().toString().trim();
        priceMed = etMedPrice.getText().toString().trim();
        discountPriceMed = etMedDiscountPrice.getText().toString().trim();
        expDateMed = etMedExpDate.getText().toString().trim();
        supNameMed = etMedSupName.getText().toString().trim();
        supPhoneMed = etMedSupPhone.getText().toString().trim();
        supEmailMed = etMedSupEmail.getText().toString().trim();
        noteMed = etMedNote.getText().toString().trim();

        try {
            price = Double.parseDouble(priceMed);
        } catch (NumberFormatException e) {
            price = 0.00;
        }
        try {
            discountPrice = Double.parseDouble(discountPriceMed);
        } catch (NumberFormatException e) {
            discountPrice = 0.00;
        }

        // if is a new mCurrentMedUri
        if (mCurrentMedUri == null) {
            if (mPickedImage != null) {
                image = mPickedImage.toString();
            }
        } else {
            if (mPickedImage == null) {
                Toast.makeText(this, getString(R.string.empty_image), Toast.LENGTH_LONG).show();
            } else {
                image = mPickedImage.toString();
            }
        }

        // Check if this is supposed to be a new medicine
        // and check if all the fields required in the editor are blank (at least one)
        // ONLY FOR NOT NULL FIELDS
        if (mCurrentMedUri == null &&
                TextUtils.isEmpty(nameMed) && TextUtils.isEmpty(quantityMed) &&
                TextUtils.isEmpty(priceMed) && TextUtils.isEmpty(expDateMed) &&
                mMedType == InventoryMedEntry.TYPE_UNKNOWN &&
                (TextUtils.isEmpty(image) || image.equals("")) ){

            Toast.makeText(this, getString(R.string.empty_info_med), Toast.LENGTH_LONG).show();

            // Since no fields were modified, we can return early without creating a new medicine.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Check if name exist
        if (TextUtils.isEmpty(nameMed)) {
            etMedName.requestFocus();
            etMedName.setError(getString(R.string.error_empty_name));
            return;
        }

        // Check if quantity exist
        if (TextUtils.isEmpty(quantityMed)) {
            etMedQuantity.requestFocus();
            etMedQuantity.setError(getString(R.string.error_empty_quantity));
            return;
        }

        // Check if price exist
        if (TextUtils.isEmpty(priceMed)) {
            etMedPrice.requestFocus();
            etMedPrice.setError(getString(R.string.error_empty_price));
            return;
        }

        // Check if Exp Date exist
        if (TextUtils.isEmpty(expDateMed)) {
            etMedExpDate.requestFocus();
            etMedExpDate.setError(getString(R.string.error_empty_exp_date));
            return;
        }

        // Check if image exist
        if (TextUtils.isEmpty(image) || image.equals("")) {
            Toast.makeText(this, getString(R.string.empty_image), Toast.LENGTH_LONG).show();
            return;
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryMedEntry.COLUMN_MED_NAME, nameMed);
        values.put(InventoryMedEntry.COLUMN_MED_TYPE, typeMed);
        values.put(InventoryMedEntry.COLUMN_MED_QUANTITY, quantityMed);
        values.put(InventoryMedEntry.COLUMN_MED_EXP_DATE, expDateMed);
        values.put(InventoryMedEntry.COLUMN_MED_PRICE, price);
        values.put(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT, discountPrice);
        values.put(InventoryMedEntry.COLUMN_MED_IMAGE, image);
        values.put(InventoryMedEntry.COLUMN_MED_SUP_NAME, supNameMed);
        values.put(InventoryMedEntry.COLUMN_MED_SUP_PHONE, supPhoneMed);
        values.put(InventoryMedEntry.COLUMN_MED_SUP_EMAIL, supEmailMed);
        values.put(InventoryMedEntry.COLUMN_MED_NOTE, noteMed);

        if (mCurrentMedUri == null) {
            // Insert the new row, returning the primary key value of the new row
            Uri newUri = getContentResolver().insert(InventoryMedEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_insert_med_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.label_insert_med_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(
                    mCurrentMedUri,                     // the user dictionary content URI
                    values,                             // the columns to update
                    null,                               // the column to select on
                    null                                // the value to compare to
            );

            // Show a toast message depending on whether or not the update was successful.
            if (rowAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_update_med_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.label_update_med_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
