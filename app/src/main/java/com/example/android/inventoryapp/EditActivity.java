package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract;
import com.example.android.inventoryapp.data.BookDbHelper;

/**
 * This activity handles allowing the user to add a new book to the database.
 */
public class EditActivity extends AppCompatActivity {

    public static final String LOG_TAG = EditActivity.class.getName();

    /** EditText fields to enter book information **/
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Finds all EditText views
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone);
    }

    /**
     * Handles getting user input and saving it to the database.
     */
    private void insertBook() {
        Log.i(LOG_TAG, "insertBook() is called.");
        // Reads input from the EditText views and trims white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Converts price, quantity and supplier phone from Strings to ints
        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);
        int supplierPhone = Integer.parseInt(supplierPhoneString);

        // Creates an object of the database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Gets the database in writable mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Creates a ContentValues object and sets the column keys to the EditText input
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);

        // Inserts a new row in the database containing values from the user input
        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        // If inserting data was unsuccessful,
        if (newRowId == -1) {
            // displays a toast message stating there was an error.
            Toast.makeText(this, R.string.edit_error_message, Toast.LENGTH_SHORT).show();
        } else {
            // Else, if it is succesful, display a toast message with the ID of the new row.
            Toast.makeText(this, getString(R.string.edit_success_message) + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Inflates the menu option with the edit_menu file and adds it to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    /**
     * Sets a listener for the actions performed when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If the check-mark icon is selected, insert the book to the database and exit activity
            case R.id.save:
                insertBook();
                finish();
                return true;
            // Deletes entry. Currently, does nothing
            case R.id.delete:
                return true;
            // Provides back arrow to navigate back to CatalogActivity
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
