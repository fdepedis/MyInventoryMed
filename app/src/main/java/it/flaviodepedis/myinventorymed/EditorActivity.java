package it.flaviodepedis.myinventorymed;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
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
