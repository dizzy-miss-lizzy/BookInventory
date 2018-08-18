# Book Inventory

Stage 1 of the book inventory app.

* Built for API 15 or higher
* Two activities: CatalogActivity and EditActivity
* Uses SQLite

## Basic structure:

A TextView displays a header showing the number of rows in the database.

The user can click on the floating action button to open the EditActivity.

EditText fields take input such as name, price, quantity, supplier name, and supplier phone number.

Data is appended to the TextView whether it is manually input by the user or the "Insert Dummy Data" menu option is selected.

Contract and SQLiteOpenHelper files provide the schema and methods containing SQL statements.

## Reference:

https://developer.android.com/training/data-storage/sqlite