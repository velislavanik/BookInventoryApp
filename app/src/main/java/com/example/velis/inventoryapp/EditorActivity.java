package com.example.velis.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.velis.inventoryapp.data.ContractValues.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    /**
     * variable to set default quantity=0
     **/
    String def = "";
    /**
     * adapter for our listView
     */
    BookCursorAdapter mCursorAdapter;
    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;
    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;
    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;
    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierName;
    /**
     * EditText field to enter the supplier's phone
     */
    private EditText mSupplierPhone;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;
    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * set quantity to one if in add new book mode
     */
    private int quantity = 1;

    private String supplierPhone;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        /*Use getIntent() and getDate() to get the associated URI
         SET the title of Editor activity depending on which situation we have
         If the Editor activity was opened using the ListView item, than we will
         have uri of book, so change app bar to say "Edit book"
         Otherwise, if this is a new book, uri is null, so change app bar to say "Add a book" */

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        //setup an adapter to create a list item for each row
        mCursorAdapter = new BookCursorAdapter(this, null);

        //If the intent does not contain a book contain uri, than we know that we are creating a new book
        if (mCurrentBookUri == null) {
            //this is a new book, so change the app bar to say "Add a book"
            setTitle(getString(R.string.add_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
            def = getString(R.string.edno);
            mQuantityEditText = findViewById(R.id.edit_product_quantity);
            mQuantityEditText.setText(def);

        } else {
            //Otherwise this is an existing book, so change app bar to sa "Edit bok"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierName = findViewById(R.id.edit_supplier_name);
        mSupplierPhone = findViewById(R.id.edit_supplier_phone);


       /* Setup OnTouchListeners on all the input fields, so we can determine if the user
         has touched or modified them. This will let us know if there are unsaved changes
         or not, if the user tries to leave the editor without saving. */

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);

        Button phoneButton = findViewById(R.id.call_btn);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = String.valueOf(supplierPhone);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        Button productDecreaseButton = findViewById(R.id.up);
        productDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0)
                    quantity -= 1;
                displayQuantity(quantity);
                decreaseCount(quantity);
            }
        });

        Button productIncreaseButton = findViewById(R.id.down);
        productIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;
                displayQuantity(quantity);
                increaseCount(quantity);
            }
        });
    }

    public void decreaseCount(int productQuantity) {

        if (productQuantity >= 0) {
            updateProduct(productQuantity);
        }
    }

    public void increaseCount(int productQuantity) {

        if (productQuantity >= 0) {

            updateProduct(productQuantity);
        }
    }

    /**
     * Displays the given quantity
     */
    public void displayQuantity(int quantity) {
        TextView scoreView = findViewById(R.id.edit_product_quantity);
        scoreView.setText(String.valueOf(quantity));
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't been changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private boolean saveBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if ((mCurrentBookUri == null) && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString))
            return false;
        else if (TextUtils.isEmpty(nameString)) {

            Toast toast = Toast.makeText(this, getString(R.string.required_name), Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();

            return false;
        } else if (TextUtils.isEmpty(priceString)) {

            Toast toast = Toast.makeText(this, getString(R.string.price_required), Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return false;
        } else if (TextUtils.isEmpty(supplierNameString)) {

            Toast toast = Toast.makeText(this, getString(R.string.supplier_name), Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return false;
        } else if (TextUtils.isEmpty(supplierPhoneString)) {

            Toast toast = Toast.makeText(this, getString(R.string.supplier_phone), Toast.LENGTH_SHORT);

            View view = toast.getView();

            //To change the Background of Toast
            view.setBackgroundColor(Color.rgb(153, 51, 51));

            view.setPadding(10, 5, 10, 5);

            toast.show();
            return false;
        } else {

            // Create a ContentValues object where column names are the keys,
            // and book attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);


            // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
            if (mCurrentBookUri == null) {
                // This is a NEW book, so insert a new book into the provider,
                // returning the content URI for the new book.

                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast toast = Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT);

                    View view = toast.getView();

                    //To change the Background of Toast
                    view.setBackgroundColor(Color.rgb(153, 51, 51));

                    view.setPadding(10, 5, 10, 5);

                    toast.show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast toast = Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT);

                    View view = toast.getView();

                    //To change the Background of Toast
                    view.setBackgroundColor(Color.rgb(128, 153, 51));

                    view.setPadding(10, 5, 10, 5);

                    toast.show();
                }
            } else {
            /* Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
               and pass in the new ContentValues. Pass in null for the selection and selection args
               because mCurrentBookUri will already identify the correct row in the database that
               we want to modify. */
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast toast = Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT);

                    View view = toast.getView();

                    //To change the Background of Toast
                    view.setBackgroundColor(Color.rgb(153, 51, 51));

                    view.setPadding(10, 5, 10, 5);

                    toast.show();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                if (saveBook()) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                /* Navigate back to parent activity (CatalogActivity)
                   If the book hasn't changed, continue with navigating up to parent activity
                   which is the {@link CatalogActivity}. */
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        // This loader will execute the ContentProvider's query method on a background thread
        if (mCurrentBookUri != null) {
            return new CursorLoader(this,   // Parent activity context
                    mCurrentBookUri,               // Query the content URI for the current pet
                    projection,                    // Columns to include in the resulting Cursor
                    null,                  // No selection clause
                    null,               // No selection arguments
                    null);                 // Default sort order
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String quantity2 = Integer.toString(quantity);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity2);
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("1");
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }

    private void updateProduct(int productQuantity) {

        if (mCurrentBookUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast toast = Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT);

                View view = toast.getView();

                //To change the Background of Toast
                view.setBackgroundColor(Color.rgb(128, 153, 51));

                view.setPadding(10, 5, 10, 5);

                toast.show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {

                Toast toast = Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT);

                View view = toast.getView();

                //To change the Background of Toast
                view.setBackgroundColor(Color.rgb(153, 51, 51));

                view.setPadding(10, 5, 10, 5);

                toast.show();
            } else {
                Toast toast = Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT);

                View view = toast.getView();

                //To change the Background of Toast
                view.setBackgroundColor(Color.rgb(128, 153, 51));

                view.setPadding(10, 5, 10, 5);

                toast.show();
            }
        }
    }
}
