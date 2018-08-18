package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Contract class used to define the schema of the database.
 */
public class BookContract {

    /** Private constructor to prevent instantiating the class **/
    private BookContract() {}

    /**
     * Inner class defining the table's contents.
     * Each entry is for one book.
     */
    public static abstract class BookEntry implements BaseColumns {

        /** Name of database table **/
        public static final String TABLE_NAME = "books";

        /** ID number used only by the database table - INTEGER **/
        public static final String _ID = BaseColumns._ID;

        /** Name of book - TEXT **/
        public static final String COLUMN_BOOK_NAME = "name";

        /** Price of book - INTEGER **/
        public static final String COLUMN_BOOK_PRICE = "price";

        /** Quantity of book in stock - INTEGER **/
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        /** Name of supplier - TEXT **/
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        /** Phone number of supplier - INTEGER **/
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";


    }
}
