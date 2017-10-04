package it.flaviodepedis.myinventorymed;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentMedUri;

    private static final int EXISTING_MED_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();

        if (mCurrentMedUri == null) {
            setTitle(getResources().getString(R.string.label_editor_activity_title_new_med));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getResources().getString(R.string.label_editor_activity_title_edit_med));

            getLoaderManager().initLoader(EXISTING_MED_LOADER, null, this);
        }

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
}
