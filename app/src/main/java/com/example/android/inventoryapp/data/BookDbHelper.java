package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * Database helper. Handles creating and upgrading the database table,
 * managing the version number, and specifying data types and keywords for each column.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    /** Log messages tag **/
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /** Database file name **/
    public static final String DATABASE_NAME = "BookStore.db";

    /** Version number if the database schema is ever changed **/
    public static final int DATABASE_VERSION = 1;

    /** String containing the SQL statement to create the table **/
    private static final String SQL_CREATE_BOOKS_TABLE =
            "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL," +
                    BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL," +
                    BookEntry.COLUMN_BOOK_PRICE + " DOUBLE NOT NULL," +
                    BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 1," +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL," +
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " LONG NOT NULL)";

    /** String containing the SQL statement to delete the table **/
    private static final String SQL_DELETE_BOOKS_TABLE =
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    /**
     * Constructor that takes in the context of the app.
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first created and executes the SQL statement.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Database is created.");
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded by deleting it and creating a new one.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "Database is deleted.");
        db.execSQL(SQL_DELETE_BOOKS_TABLE);
        onCreate(db);
    }
}
