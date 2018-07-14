package com.example.velis.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */

public class ContractValues {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ContractValues() {}

    //set up a string constant whose value is the same as that from the AndroidManifest:
    public static final String CONTENT_AUTHORITY = "com.example.velis.inventoryapp";

    //To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the Inventory database table.
     * Each entry in the table represents a single row/stock.
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /** Name of database table for books */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="Product_Name";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "Price";

        /**
         * Quantity of this product
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";

        /**
         * Supplier name for this product
         *
         * Type: String
         */
        public final static String COLUMN_SUPPLIER_NAME = "Supplier_Name";

        /**
         * Supplier phone number
         *
         * Type: String
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
    }
}
