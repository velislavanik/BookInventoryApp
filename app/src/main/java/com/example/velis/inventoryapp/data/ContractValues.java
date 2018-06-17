package com.example.velis.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */

public class ContractValues {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ContractValues() {
    }

    /**
     * Inner class that defines constant values for the Inventory database table.
     * Each entry in the table represents a single row/stock.
     */
    public static final class InventoryEntry implements BaseColumns {

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
