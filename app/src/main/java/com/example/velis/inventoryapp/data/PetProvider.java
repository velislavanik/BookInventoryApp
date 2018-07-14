package com.example.velis.inventoryapp.data;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.velis.inventoryapp.R;
import com.example.velis.inventoryapp.data.ContractValues.InventoryEntry;

import java.util.Objects;

/**
 * {@link ContentProvider} for Books app.
 */
public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOKS_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(ContractValues.CONTENT_AUTHORITY, ContractValues.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(ContractValues.CONTENT_AUTHORITY, ContractValues.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Database helper object
     */
    private ProductDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a PetDbHelper object to gain access to the books database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.

                cursor = database.query(ContractValues.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ContractValues.InventoryEntry._ID + getContext().getResources().getString(R.string.ravno);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ContractValues.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set notificationURI on the cursor
        //so we know what content uri the cursor was created for
        //if the data of this uri changes, than we know we need to update the cursor
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.not_supported) + uri);
        }
    }

    @SuppressLint("NewApi")
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(ContractValues.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name.isEmpty()) {
            Toast toast;

            toast = Toast.makeText(getContext(), R.string.required_name, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();

            return null;
        }

        // Check that the price is valid
        Integer price = values.getAsInteger(ContractValues.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {

            Toast toast;

            toast = Toast.makeText(getContext(), R.string.price_required, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);
            toast.show();
            return null;
        }

        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            //  Toast.makeText(getContext(), R.string.quantity, Toast.LENGTH_SHORT).show();
            Toast toast;

            toast = Toast.makeText(getContext(), R.string.quantity, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast

            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return null;
        }

        // Check that the name is not null
        String supplierName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName.isEmpty()) {
            //not good to just throw exception, better show message to the user and then stop
            Toast toast;

            toast = Toast.makeText(getContext(), R.string.supplier_name, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast

            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return null;
        }

        // Check that the name is not null
        String phoneName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (phoneName.isEmpty()) {

            Toast toast;

            toast = Toast.makeText(getContext(), R.string.supplier_phone, Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return null;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(ContractValues.InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, getContext().getResources().getString(R.string.Failed_to) + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the book content uri
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ContractValues.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //Notify all listeners that the data has changed for the book content uri
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.Update_is_not_supported) + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Perform the update on the database and get the number of rows affected
        // If the {@link InventoryEntry#COLUMN_BOOK_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ContractValues.InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ContractValues.InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.requires_name));
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Notify all listeners that the data has changed for the pet content uri
        // Returns the number of database rows affected by the update statement
        // return database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ContractValues.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return ContractValues.InventoryEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return ContractValues.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

