package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.inventoryapp.data.BookContract.BookEntry;
import static com.example.android.inventoryapp.data.BookContract.CONTENT_AUTHORITY;
import static com.example.android.inventoryapp.data.BookContract.PATH_INVENTORY;

/**
 * {@link ContentProvider} for Book Inventory app.
 * Handles querying, inserting, updating and deleting data.
 */
public class BookProvider extends ContentProvider {

    /** Log messages tag **/
    public static final String LOG_TAG = BookProvider.class.getName();

    /** Database helper object **/
    private BookDbHelper mDbHelper;

    /** URI matcher code for the entire table **/
    private static final int BOOKS = 0;

    /** URI matcher code for a particular item **/
    private static final int BOOK_ID = 1;

    /** UriMatcher object used to match the CONTENT_URI with a matcher code **/
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Contains the URI paths for the entire database or a particular item **/
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY + "/#", BOOK_ID);
    }

    /**
     * Initializes BookProvider and database helper.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Handles querying the data for a URI.
     * Uses projection (the table columns), selection (specific book ID),
     * selectionArgs (actual book ID), and sortOrder.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Reads the database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Cursor to hold the query result
        Cursor cursor;

        // Matches the URI to BOOKS or BOOK_ID matcher code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Queries the table containing the parameters
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                // Queries a particular item
                // selection and selectionArgs extract the book ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Sets a notification URI on the cursor for the given URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Handles inserting data for a URI by calling insertBook().
     * Since only new data can be inserted, only the BOOKS matcher code is used.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Matches the URI to BOOKS or BOOK_ID matcher code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Inserts a new book into the database with the given content values.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Checks if the book name is null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }

        // Checks if the author is null
        String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Book requires an author");
        }

        // Checks if the price is null or negative
        Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Book requires a valid price");
        }

        // Checks if the quantity is null or negative
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Book requires a valid quantity");
        }

        // Checks if the supplier name is null
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a supplier name");
        }

        // Checks if the supplier phone number is null or negative
        Long supplierPhone = values.getAsLong(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        if (supplierPhone == null || supplierPhone < 0) {
            throw new IllegalArgumentException("Book requires a valid supplier phone number");
        }

        // Writes to the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Inserts the book into the database
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        // Prints a log message if the insertion failed
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notifies listeners that data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the new URI and ID
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Handles updating the data for a URI by calling updateBook().
     * Uses ContentValues (data input), selection (specific book ID), and selectionArgs (actual book ID).
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Matches the URI to BOOKS or BOOK_ID matcher code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Updates the table containing the parameters
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                // Updates a particular item
                // selection and selectionArgs extract the book ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Updates a book in the database with the given content values.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Checks if the book name is null
        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // Checks if the author is null
        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Book requires an author");
            }
        }

        // Checks if the price is null or negative
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        // Checks if the quantity is null or negative
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Book requires a valid quantity");
            }
        }

        // Checks if the supplier name is null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier name");
            }
        }

        // Checks if the supplier phone number is null or negative
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)) {
            Long supplierPhone = values.getAsLong(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            if (supplierPhone == null || supplierPhone < 0) {
                throw new IllegalArgumentException("Book requires a valid supplier phone number");
            }
        }

        // If there are no values updated, return
        if (values.size() == 0) {
            return 0;
        }

        // Writes to the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Updates the database and assigns the number of rows updated
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If there are rows updated, notify the listeners that data has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Handles deleting the data for a URI.
     * Uses selection (specific book ID) and selectionArgs (actual book ID).
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Writes to the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Variable to keep track of deleted rows
        int rowsDeleted;

        // Matches the URI to BOOKS or BOOK_ID matcher code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Deletes the table containing the parameters
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Deletes a particular item
                // selection and selectionArgs extract the book ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If there are rows deleted, notify the listeners that data has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        // Matches the URI to BOOKS or BOOK_ID matcher code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}