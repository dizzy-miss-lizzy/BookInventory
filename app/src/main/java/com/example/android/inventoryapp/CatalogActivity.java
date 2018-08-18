package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

/**
 * Main screen of the app that displays a list of books that are entered by the user
 * and then stored in the database. Information includes name, price, quantity, and
 * supplier name and phone number.
 *
 * References: Udacity's Pets app and Android documentation: https://developer.android.com/training/data-storage/sqlite
 */
public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getName();

    /** Database helper that provides access to the database **/
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

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

        // Instantiates the database helper and passes the activity context
        mDbHelper = new BookDbHelper(this);
    }

    /**
     * Called when the user has input data from {@link EditActivity} and adds it to the list.
     */
    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Calls onStart()");
        super.onStart();
        queryData();
    }

    /**
     * Cursor method that reads information from the database and displays it in a TextView.
     */
    private Cursor queryData() {
        Log.i(LOG_TAG, "Calls queryData()");
        // Creates a database or reads from one already created
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Defines a projection with the constant values used in the query
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE };

        // Performs the query
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,  // Table to query
                projection,            // Columns to return, including all from the books table
                null,                  // Columns for the WHERE clause
                null,                  // Values for the WHERE clause
                null,                  // Doesn't group the rows
                null,                  // Doesn't filter by row groups
                null);                 // Doesn't specify sort order

        // Finds TextView to display data to screen
        TextView displayInfo = (TextView) findViewById(R.id.book_text_view);

        try {
            // Creates a header:
            //
            // The books table contains <row number in Cursor> books.
            // _id - name - price - quantity - supplier_name - supplier_phone
            assert displayInfo != null;
            displayInfo.setText(getString(R.string.header_text_view_one) + cursor.getCount() + getString(R.string.header_text_view_two));
            displayInfo.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_BOOK_NAME + " - " +
                    BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + "\n");

            // Gets the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Moves through every row and returns data in cursor
            while (cursor.moveToNext()) {
                Log.i(LOG_TAG, "While loop is executed.");
                // Uses the column index to extract the String and int values for the current row
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

                // Appends the values of the current row to the TextView
                displayInfo.append("\n" + currentID + " - " +
                        currentName + " - " + "$" + currentPrice + " - " +
                        currentQuantity + " - " + currentSupplierName + " - " + currentSupplierPhone);
            }
        } finally {
            // Closes the cursor when finished reading from it.
            Log.i(LOG_TAG, "Cursor is closed.");
            cursor.close();
        }
        return cursor;
    }

    /**
     * Helper method used for debugging by inserting dummy data into the database.
     */
    private void insertBook() {
        Log.i(LOG_TAG, "Calls insertBook()");
        // Gets the database in writable mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Creates a ContentValues object and sets the column keys to hardcoded values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, getString(R.string.dummy_data_book_name));
        values.put(BookEntry.COLUMN_BOOK_PRICE, 10);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 20);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.dummy_data_supplier_name));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, 5557777);

        // Inserts a new row for the dummy data
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
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
            // Inserts the dummy data and displays it in the TextView
            case R.id.insert_dummy_data:
                insertBook();
                queryData();
                return true;
            // Deletes all entries. Currently, does nothing
            case R.id.delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
