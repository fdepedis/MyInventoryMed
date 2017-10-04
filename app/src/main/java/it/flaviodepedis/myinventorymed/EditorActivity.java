package it.flaviodepedis.myinventorymed;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentMedUri;

    private static final int EXISTING_MED_LOADER = 0;

    private String mMedType = InventoryMedEntry.TYPE_UNKNOWN;

    /** Declare all view in this activity */
    @BindView(R.id.spinner_med_type) Spinner mMedSpinner;

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
}
