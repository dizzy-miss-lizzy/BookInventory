package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class used to define the schema of the database.
 */
public class BookContract {

    /** Private constructor to prevent instantiating the class **/
    private BookContract() {}

    /** Name for the content provider, which uses the app's package name **/
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /** Uses CONTENT_AUTHORITY as the base for all URIs used in the app **/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Path that is appended to BASE_CONTENT_URI to access URI data **/
    public static final String PATH_INVENTORY = "inventoryapp";

    /**
     * Inner class defining the table's contents.
     * Each entry is for one book.
     */
    public static abstract class BookEntry implements BaseColumns {

        /** URI used in {@link BookProvider} to access data **/
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /** MIME type of the CONTENT_URI to access the whole list of data **/
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** MIME type of the CONTENT_URI to access a particular item **/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table **/
        public static final String TABLE_NAME = "books";

        /** ID number used only by the database table - INTEGER **/
        public static final String _ID = BaseColumns._ID;

        /** Name of book - TEXT **/
        public static final String COLUMN_BOOK_NAME = "name";

        /** Author of book - TEXT **/
        public static final String COLUMN_BOOK_AUTHOR = "author";

        /** Price of book - DOUBLE **/
        public static final String COLUMN_BOOK_PRICE = "price";

        /** Quantity of book in stock - INTEGER **/
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        /** Name of supplier - TEXT **/
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        /** Phone number of supplier - LONG **/
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";
    }
}
