package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_AUTHOR;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE;
import static com.example.android.inventoryapp.data.BookContract.BookEntry.CONTENT_URI;
import static com.example.android.inventoryapp.data.BookContract.BookEntry._ID;

/**
 * This activity handles allowing the user to add a new book or edit a book.
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifies the data loader **/
    private static final int EXISTING_BOOK_LOADER = 0;

    /** Content URI for the current URI (null if a new book is added) **/
    private Uri mCurrentBookUri;

    /** EditText fields to enter book information **/
    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    /** Quantity for decrease and increase buttons used to edit a book **/
    int quantity = 0;

    /** Contains whether or not the EditText data has been changed **/
    private boolean mBookHasChanged = false;

    /**
     * Handles listening for touches by the user on the EditText fields.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    /**
     * Handles decreasing the quantity when the "-" button is clicked.
     * The user is not allowed to decrease the quantity below 0.
     * Reference: https://stackoverflow.com/questions/41330793/how-to-increase-or-decrease-textview-number-through-button-in-android
     */
    private View.OnClickListener mDecreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            String stringValue;
            if (quantity > 0) {
                quantity -= 1;
                stringValue = String.valueOf(quantity);
                mQuantityEditText.setText(stringValue);
            } else {
                Toast.makeText(EditActivity.this, R.string.neg_quantity_error_msg, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Handles increasing the quantity when the "+" button is clicked.
     */
    private View.OnClickListener mIncreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            String stringValue;
            quantity += 1;
            stringValue = String.valueOf(quantity);
            mQuantityEditText.setText(stringValue);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Finds all EditText and Button views
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone);
        Button decreaseQuantity = (Button) findViewById(R.id.decrease_quantity);
        Button increaseQuantity = (Button) findViewById(R.id.increase_quantity);

        // Calls data from the intent that contains the current URI
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Determines if the user is adding a new book or editing an existing book
        // and sets the App bar title accordingly
        // The quantity buttons are only displayed when editing a book
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add_activity_title));
            invalidateOptionsMenu();
            decreaseQuantity.setVisibility(View.GONE);
            increaseQuantity.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_activity_title));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Sets touch and click listeners on the EditText and Button views
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        decreaseQuantity.setOnClickListener(mDecreaseListener);
        increaseQuantity.setOnClickListener(mIncreaseListener);
    }

    /**
     * Handles getting user input and saving it to the database.
     */
    private void saveBook() {
        // Reads input from the EditText views and trims white space
        String nameString = mNameEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Checks if the user is adding a new book and returns if nothing is added to any EditText fields
        if (mCurrentBookUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)) {
            return;
        }

        // Performs sanity checks
        // If an EditText field is empty, a toast message displays prompting the user to input data
        // When all fields contain data, the book can be saved
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, R.string.include_name_msg, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(authorString)) {
            Toast.makeText(this, R.string.include_author_msg, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.include_price_msg, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, R.string.include_quantity_msg, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, R.string.include_supplier_name_msg, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, R.string.include_phone_msg, Toast.LENGTH_SHORT).show();
        } else if (supplierPhoneString.length() < 10) {
            Toast.makeText(this, R.string.include_valid_phone_msg, Toast.LENGTH_SHORT).show();
        } else {
            // Converts price, quantity and supplier phone from Strings to their own data type
            double price = Double.parseDouble(priceString);
            int quantity = Integer.parseInt(quantityString);
            long supplierPhone = Long.parseLong(supplierPhoneString);

            // Creates a ContentValues object and sets the column keys to the EditText input
            ContentValues values = new ContentValues();
            values.put(COLUMN_BOOK_NAME, nameString);
            values.put(COLUMN_BOOK_AUTHOR, authorString);
            values.put(COLUMN_BOOK_PRICE, price);
            values.put(COLUMN_BOOK_QUANTITY, quantity);
            values.put(COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
            values.put(COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);

            // Checks if a new book is being saved and inserts data
            // Else, the existing book is updated
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(CONTENT_URI, values);

                // If inserting data was unsuccessful, an error message displays
                // Else, if it is successful, a "Book saved" message displays
                if (newUri == null) {
                    Toast.makeText(this, R.string.add_error_msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.add_success_msg, Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // If updating data was unsuccessful, an error message displays
                // Else, if it is successful, a "Book updated" message displays
                if (rowsAffected == 0) {
                    Toast.makeText(this, R.string.update_error_msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.update_success_msg, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
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
     * Hides the menu item if a new book is being added.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Sets a listener for the actions performed when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
            // If the check-mark icon is selected, save the book to the database
                saveBook();
                return true;
            case R.id.delete:
                // Displays dialog to delete book
                showDeleteConfirmationDialog();
                return true;
            // Provides back arrow to navigate back to CatalogActivity
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Displays a dialog message telling the user that there are unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Navigates back to the parent activity when the user clicks "Discard"
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the system back button is clicked.
     */
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Displays a dialog message telling the user that there are unsaved changes
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Closes the activity when the user clicks "Discard"
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Handles building the dialog message to warn the user about discarding unsaved changes.
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    // Dismisses the dialog message when the user clicks "Keep Editing"
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Handles building the dialog message to ask the user about deleting a book.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deletes the book when the user clicks "Delete"
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    // Dismisses the dialog message when the user clicks "Cancel"
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Deletes the current book if it already exists in the database.
     */
    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // If deleting data was unsuccessful, an error message displays
            // Else, if it is successful, a "Book deleted" message displays
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_error_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_success_msg, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Creates a loader that declares a projection taking input from all database table columns.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                COLUMN_BOOK_NAME,
                COLUMN_BOOK_AUTHOR,
                COLUMN_BOOK_PRICE,
                COLUMN_BOOK_QUANTITY,
                COLUMN_BOOK_SUPPLIER_NAME,
                COLUMN_BOOK_SUPPLIER_PHONE };

        // Performs the query method on a background thread
        return new CursorLoader(this,  // Activity context
                mCurrentBookUri,       // URI for current book
                projection,            // Columns to return
                null,                  // No selection
                null,                  // No selection arguments
                null);                 // No defined sort order
    }

    /**
     * Updates the cursor with new book data and handles setting the EditText fields.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Returns early if there is no data in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Reads data from the cursor and finds the columns for each input field
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(COLUMN_BOOK_NAME);
            int authorColumnIndex = data.getColumnIndex(COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = data.getColumnIndex(COLUMN_BOOK_PRICE);
            int quantityColumnIndex = data.getColumnIndex(COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(COLUMN_BOOK_SUPPLIER_PHONE);

            // Extracts the data at the column index and assigns to appropriate data type
            String name = data.getString(nameColumnIndex);
            String author = data.getString(authorColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            long supplierPhone = data.getLong(supplierPhoneColumnIndex);

            // Sets the EditText fields to the current book data
            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(Long.toString(supplierPhone));
        }
    }

    /**
     * Clears the EditText input fields of data if the loader is reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}
