package com.example.velis.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.velis.inventoryapp.data.ContractValues.InventoryEntry;
import com.example.velis.inventoryapp.data.ContractValues;
import com.example.velis.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertData();
        queryData();
    }

    private void insertData() {
        ProductDbHelper mDbHelper = new ProductDbHelper(this);
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ContractValues.InventoryEntry.COLUMN_PRODUCT_NAME, "Product");
        values.put(ContractValues.InventoryEntry.COLUMN_PRODUCT_PRICE, 24);
        values.put(ContractValues.InventoryEntry.COLUMN_PRODUCT_QUANTITY, 12);
        values.put(ContractValues.InventoryEntry.COLUMN_SUPPLIER_NAME, "Peter & Co");
        values.put(ContractValues.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "+359 52 111111");

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(ContractValues.InventoryEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Query the database.
     * Always close the cursor when you're done reading from it.
     * This releases all its resources and makes it invalid.
     */
    private Cursor queryData() {

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM books"
        // to get a Cursor that contains all rows from the pets table.

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                ContractValues.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );


        // Display the number of rows in the Cursor (which reflects the number of rows in the
        // books table in the database).
        TextView displayView = findViewById(R.id.db);

        try {
            // Create a header in the Text View that looks like this:
            // The books table contains <number of rows in Cursor> books.
            // _id - name -price - quantity - supplier name - supplier phone number
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText(this.getResources().getString(R.string.header1)+" " + cursor.getCount()+" " +this.getResources().getString(R.string.header2)+"\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + " - " +
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_NAME + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber
                ));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
        return cursor;
    }
}


