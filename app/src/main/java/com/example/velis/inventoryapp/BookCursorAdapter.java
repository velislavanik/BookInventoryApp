package com.example.velis.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.velis.inventoryapp.data.ContractValues;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Identifier for the pet data loader
     */
     private TextView nameTextView;

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button sale = view.findViewById(R.id.sale_btn);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ContractValues.InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ContractValues.InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ContractValues.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        final int columnIdIndex = cursor.getColumnIndex(ContractValues.InventoryEntry._ID);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final String productID = cursor.getString(columnIdIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);


        sale.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) context;
                activity.productSaleCount(Integer.valueOf(productID), Integer.valueOf(bookQuantity));
            }
        });

        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);
    }
}