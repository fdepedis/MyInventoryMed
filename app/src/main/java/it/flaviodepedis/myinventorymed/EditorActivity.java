package it.flaviodepedis.myinventorymed;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentMedUri;

    private static final int EXISTING_MED_LOADER = 0;

    /**
     * Initialize the spinner to TYPE_UNKNOWN
     */
    private String mMedType = InventoryMedEntry.TYPE_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the medicine has been edited (true) or not (false)
     */
    private boolean mMedHasChanged = false;

    //Stores the URI of image
    private Uri mPickedImage;
    private static final int IMAGE_REQUEST_CODE = 0;

    /** Declare all view in this activity */
    @BindView(R.id.et_value_editor_med_name) EditText etMedName;
    @BindView(R.id.spinner_med_type) Spinner mMedSpinner;
    @BindView(R.id.et_value_editor_med_quantity) EditText etMedQuantity;
    @BindView(R.id.et_value_editor_med_price) EditText etMedPrice;
    @BindView(R.id.et_value_editor_med_discount) EditText etMedDiscountPrice;
    @BindView(R.id.et_value_editor_med_exp_date) EditText etMedExpDate;
    @BindView(R.id.image_editor_med) ImageView imageMed;
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
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
     * @param data contains the URI of image the user picked
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
                    }else if (selection.equals(getString(R.string.label_type_med_sciroppo))) {
                        mMedType = InventoryMedEntry.TYPE_SCIROPPO;                     // Sciroppo
                    }else if (selection.equals(getString(R.string.label_type_med_crema))) {
                        mMedType = InventoryMedEntry.TYPE_CREMA;                        // Crema
                    }else if (selection.equals(getString(R.string.label_type_med_gel))) {
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
                //saveMedicine();
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
}
