package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.silencedut.expandablelayout.ExpandableLayout;

import java.util.HashSet;
import java.util.List;

import static com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * This adapter uses a {@link Cursor} to populate a RecyclerView with data.
 * It also handles functions for the Sale, Order, Edit, and Delete buttons
 * and the ExpandableLayout for each item.
 *
 * Reference for ExpandableLayout: https://github.com/SilenceDut/ExpandableLayout
 */
public class BookCursorAdapter extends RecyclerView.Adapter<BookCursorAdapter.BookHolder> {

    /** Context of the app **/
    private Context context;

    /** Cursor to contain data **/
    private CursorAdapter cursor;

    /** Determines if the layout is expanded or not **/
    private HashSet<Integer> mExpandedPositionSet = new HashSet<>();

    /**
     * Constructor that takes in the context and cursor to retrieve data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        this.context = context;
        this.cursor = new CursorAdapter(context, c, 0) {

            /**
             * Inflates the layout but doesn't provide data yet.
             */
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            }

            /**
             * Handles setting the cursor data on the appropriate Views.
             */
            @Override
            public void bindView(View view, final Context context, Cursor cursor) {
                // Finds views in the list item layout
                TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
                TextView authorTextView = (TextView) view.findViewById(R.id.author_text_view);
                TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
                final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
                TextView supplierNameTextView = (TextView) view.findViewById(R.id.supplier_name_text_view);
                final TextView supplierPhoneTextView = (TextView) view.findViewById(R.id.supplier_phone_text_view);
                Button saleButton = (Button) view.findViewById(R.id.sale_button);
                Button orderButton = (Button) view.findViewById(R.id.order_button);
                ImageView dropDownArrow = (ImageView) view.findViewById(R.id.drop_down_arrow);

                // Finds the column index of the data
                int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
                int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
                int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

                // Extracts the data at the column index and assigns to Strings
                String bookName = cursor.getString(nameColumnIndex);
                String bookAuthor = cursor.getString(authorColumnIndex);
                String bookPrice = cursor.getString(priceColumnIndex);
                String bookQuantity = cursor.getString(quantityColumnIndex);
                String bookSupplierName = cursor.getString(supplierNameColumnIndex);
                String bookSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Sets the TextViews to the current book data
                nameTextView.setText(bookName);
                authorTextView.setText(bookAuthor);
                priceTextView.setText(bookPrice);
                quantityTextView.setText(bookQuantity);
                supplierNameTextView.setText(bookSupplierName);
                dropDownArrow.setImageResource(R.drawable.ic_arrow_drop_down_24dp);

                // Creates String to display phone number such as "(222) 222-2222"
                // Reference: https://stackoverflow.com/questions/14692764/format-edittext-view-for-phone-numbers
                String longPhone = "(" + bookSupplierPhone.substring(0, 3) + ") " + bookSupplierPhone.substring(3, 6) + "-" + bookSupplierPhone.substring(6);
                supplierPhoneTextView.setText(longPhone);

                // Gets ID of the current item in the cursor and appends it to CONTENT_URI
                // Reference: https://stackoverflow.com/questions/44034208/updating-listview-with-cursoradapter-after-an-onclick-changes-a-value-in-sqlite
                int currentId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
                final Uri contentUri = Uri.withAppendedPath(BookEntry.CONTENT_URI, Integer.toString(currentId));

                // Sets a click listener on the Sale button that decreases the quantity by 1
                // An error message displays when the user tries to decrease quantity below 0
                saleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = Integer.valueOf(quantityTextView.getText().toString());
                        if (quantity > 0) {
                            quantity -= 1;
                        } else {
                            Toast.makeText(context, R.string.neg_quantity_error_msg, Toast.LENGTH_SHORT).show();
                        }
                        // Updates the data with the new quantity value
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                        context.getContentResolver().update(contentUri, values, null, null);
                    }
                });

                // Sets a click listener on the Order button that opens an implicit intent with the item's phone number
                // Reference: https://www.youtube.com/watch?v=_NxJQcTZSxc&index=13&t=1s&list=PLkEQVaGC6GTa7I34fAF1mDxKTAsfeJgKW
                orderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        String phoneNumber = supplierPhoneTextView.getText().toString();
                        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
                        PackageManager packageManager = view.getContext().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(phoneIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        boolean isIntentSafe = activities.size() > 0;
                        if (isIntentSafe) {
                            context.startActivity(phoneIntent);
                        }
                    }
                });
            }
        };
    }

    /**
     * Handles swapping the cursor in the RecyclerView.
     */
    public void swapCursor(Cursor data) {
        cursor.swapCursor(data);
        notifyDataSetChanged();
    }

    /**
     * Class containing ExpandableLayout, Buttons and ImageView.
     */
    class BookHolder extends RecyclerView.ViewHolder {
        private ExpandableLayout expandableLayout;
        Button editButton;
        Button deleteButton;
        ImageView dropDownArrow;

        public BookHolder(View itemView) {
            super(itemView);
            // Finds the views
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_item);
            editButton = (Button) itemView.findViewById(R.id.edit_button);
            deleteButton = (Button) itemView.findViewById(R.id.delete_button);
            dropDownArrow = (ImageView) itemView.findViewById(R.id.drop_down_arrow);
        }

        /**
         * Determines whether or not the layout is expanded and responds to clicks to open/close.
         * The drop down arrow is changed to indicate the layout position.
         */
        private void registerExpand(int position) {
            if (mExpandedPositionSet.contains(position)) {
                mExpandedPositionSet.remove(position);
                dropDownArrow.setImageResource(R.drawable.ic_arrow_drop_down_24dp);
            } else {
                mExpandedPositionSet.add(position);
                dropDownArrow.setImageResource(R.drawable.ic_arrow_drop_up_24dp);
            }
        }

        /**
         * Sets a listener on the ExpandableLayout and updates item according to user clicks.
         */
        private void updateItem(final int position) {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onExpand(boolean b) {
                    registerExpand(position);
                }
            });
            expandableLayout.setExpand(mExpandedPositionSet.contains(position));
        }
    }

    /**
     * Creates the ViewHolder and calls newView() to inflate the layout.
     */
    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = cursor.newView(context, cursor.getCursor(), parent);
        BookHolder bookHolder = new BookHolder(view);
        return bookHolder;
    }

    /**
     * Binds the ViewHolder and calls bindView() to set data on the TextViews.
     * It also handles the Edit and Delete buttons.
     */
    @Override
    public void onBindViewHolder(BookHolder holder, final int position) {
        cursor.getCursor().moveToPosition(position);
        cursor.bindView(holder.itemView, context, cursor.getCursor());
        holder.updateItem(position);
        final long currentUri = cursor.getItemId(position);
        final Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentUri);

        // Sets a click listener on the Delete button that displays a dialog message to delete the selected book
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.delete_book_dialog_msg);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        // Deletes the current URI if the user clicks "Delete"
                        int rowsDeleted = context.getContentResolver().delete(currentBookUri, null, null);

                        // If deleting data was unsuccessful, an error message displays
                        // Else, if it is successful, a "Book deleted" message displays
                        if (rowsDeleted == 0) {
                            Toast.makeText(context, R.string.delete_error_msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.delete_success_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            // Dismisses the dialog message if the user clicks "Cancel"
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Sets a click listener on the Edit button that sends an explicit intent to the EditActivity
        // Reference: https://stackoverflow.com/questions/28767413/how-to-open-a-different-activity-on-recyclerview-item-onclick
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.setData(currentBookUri);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Returns items in the cursor.
     */
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * Gets ID of item at the current position.
     */
    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.getCursor().moveToPosition(position)) {
                return cursor.getCursor().getColumnIndexOrThrow(BookEntry._ID);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
