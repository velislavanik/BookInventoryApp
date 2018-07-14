package com.example.velis.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.velis.inventoryapp.data.ContractValues.InventoryEntry;

import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //initialize the loader
    private static final int BOOK_LOADER = 0;
    //adapter for our listView
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //set empty view which shows only when database is empty
        ListView bookListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        //setup an adapter to create a list item for each row
        mCursorAdapter = new BookCursorAdapter(this, null);
        //and attache it to the listView
        bookListView.setAdapter(mCursorAdapter);

        //setup item click Listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                /*Form the content URI that represents the specific book that was clicked on
                by appending the "id" passed as input to this method) onto the
                @link INVENTORY_ENTRY#CONTENT_URI}.
                For example the URI would be "content://com.example.android.books.books/2"
                if the book with id 2 was clicked on
                */
                final Uri currentBookUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                //Set the Uri on the data field of the intent
                intent.setData(currentBookUri);
                //Launch the {@link EditorActivity} to display the data of the current book
                //Setup sale_btn listener
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };
        /*This loader will execute the ContentProvider's query method on a background thread
        Now create and return a CursorLoader that will take care of
        creating a Cursor for the data being displayed. */
        return new CursorLoader(this, // parent activity context
                InventoryEntry.CONTENT_URI, //provider content URI to query
                projection, //columns to include in the resulting cursor
                null, // no selection clause
                null, // no selection arguments
                null); // default sort order
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void productSaleCount(int productID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            Uri updateUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productID);
            getContentResolver().update(updateUri, values, null, null);

            Toast toast;

            toast = Toast.makeText(this, R.string.quantity_was_changed, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast

            view.setBackgroundColor(Color.rgb(128, 153, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();

            } else {

            Toast toast;

            toast = Toast.makeText(this, R.string.product_finished, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast

            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the old cursor once we return.)
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no longer using it.
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, getString(R.string.product));
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 24);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 12);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, getString(R.string.peter_co));
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.tel_number));

        // Receive the new content URI that will allow us to access this product's data in the future.
        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.e(getString(R.string.main_activity), rowsDeleted + getString(R.string.rows_were_deleted));
    }
}


