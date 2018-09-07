package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

import static com.example.android.inventoryapp.data.BookContract.BookEntry.CONTENT_URI;

/**
 * Main screen of the app that displays a list of books that are entered by the user
 * and then stored in the database. Information includes name, author, price, quantity,
 * supplier name and phone number.
 *
 * References: Udacity's Pets app and Android documentation: https://developer.android.com/training/data-storage/sqlite
 * Material icons: https://material.io/tools/icons/?style=baseline
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Adapter for the list of books **/
    BookCursorAdapter mCursorAdapter;

    /** Static value for the book loader ID **/
    private static final int BOOK_LOADER = 0;

    /** RecyclerView for inventory list **/
    RecyclerView recyclerView;

    /** TextView and ImageView for Empty State **/
    TextView emptyView;
    ImageView emptyViewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Finds Empty State views and RecyclerView
        emptyView = (TextView) findViewById(R.id.empty_view);
        emptyViewImage = (ImageView) findViewById(R.id.empty_view_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Finds the floating action button and sets an OnClickListener()
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sends an explicit intent to open the activity that handles inputting data
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        setUpRecyclerView();
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Checks whether or not the adapter contains items and sets the Empty State accordingly
     * Reference: http://www.tutorialforandroid.com/2014/09/recyclerview-setemptyview.html
     */
    private void checkAdapterIsEmpty() {
        if (mCursorAdapter.getItemCount() == 0) {
            emptyViewImage.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyViewImage.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Sets up the Adapter and checks if it is empty.
     * If not, the RecyclerView is set on the Adapter.
     */
    protected void setUpRecyclerView() {
        mCursorAdapter = new BookCursorAdapter(this, null);
        mCursorAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mCursorAdapter);
        checkAdapterIsEmpty();
    }

    /**
     * Helper method used for debugging by inserting dummy data into the database.
     */
    private void insertBook() {
        // Creates a ContentValues object and sets the column keys to hardcoded values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, getString(R.string.dummy_data_book_name));
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, getString(R.string.dummy_data_author));
        values.put(BookEntry.COLUMN_BOOK_PRICE, 10.99);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 20);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.dummy_data_supplier_name));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, 2100601414);

        // Inserts a new row for the dummy data and notifies the RecyclerView
        getContentResolver().insert(CONTENT_URI, values);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Inflates the menu option with the catalog_menu file and adds it to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    /**
     * Sets a listener for the actions performed when a menu item is selected.
     */
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                // Inserts the dummy data and sets the Empty State to gone
                insertBook();
                emptyView.setVisibility(View.GONE);
                emptyViewImage.setVisibility(View.GONE);
                return true;
            case R.id.delete_all_entries:
                // Calls the dialog message to delete all entries
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Dialog message that displays "Delete all books?"
     * and "Delete" or "Cancel" buttons
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Handles deleting all books when the "Delete" button in the dialog message is clicked.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }

    /**
     * Creates a loader that declares a projection taking input from all database table columns.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE };

        return new CursorLoader(this,  // Activity context
                BookEntry.CONTENT_URI, // URI to query
                projection,            // Columns to return
                null,                  // No selection
                null,                  // No selection arguments
                null);                 // No defined sort order
    }

    /**
     * Updates the cursor adapter with new book data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    /**
     * Clears the cursor of data if the loader is reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
