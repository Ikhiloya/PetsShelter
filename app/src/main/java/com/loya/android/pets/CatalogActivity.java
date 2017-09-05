package com.loya.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loya.android.pets.data.PetContract;
import com.loya.android.pets.data.PetContract.PetEntry;
import com.loya.android.pets.data.PetCursorAdapter;
import com.loya.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //integer Loader constant for the loader, could be any number
    private static final int PET_LOADER = 0;

    //adapter to be used for the list view
    PetCursorAdapter mCursorAdapter;

    // private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        //find the ListView which will be populated with the pet data

        ListView petListView = (ListView) findViewById(R.id.list);

        //find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        //set up an adapter to create a list item for each row of pet data in the Cursor.
        //There is no pet data yet (until the loader finishes) so pass in the null for the Cursor
        mCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        //setup item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //create new intent to go to the EditorActivity
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                //form the content URI that represents the specific pet that was clicked on,
                //by appending the "id" (passed as input to this method) onto the PetEntry.CONTENT_URI
                //For example, the URI would be "content://com.loya.android.pets/pets/2"
                //if the pet with ID 2 was clicked on
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

                //set the Uri on the data field on the intent
                intent.setData(currentPetUri);

                //Launch the EditorActivity to display the data for the current pet
                startActivity(intent);


            }
        });

        //kick-off the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);


        // mDbHelper = new PetDbHelper(this);
        //  displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //displays the database info when the CatalogActivity is started
        //displayDatabaseInfo();
    }
//
//    /**
//     * Temporary helper method to display information in the onscreen TextView about the state of
//     * the pets database.
//     */
//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//
//
////        // Create and/or open a database to read from it
////        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                PetEntry._ID,
//                PetEntry.COLUMN_PET_NAME,
//                PetEntry.COLUMN_PET_BREED,
//                PetEntry.COLUMN_PET_GENDER,
//                PetEntry.COLUMN_PET_WEIGHT
//        };
//        /**   // Perform  query on pets table
//         Cursor cursor = db.query(
//         PetEntry.TABLE_NAME, //The table to query
//         projection,          //The column to return
//         null,                //The column for the WHERE clause
//         null,                //The values for the WHERE clause
//         null,                //don't group the rows
//         null,                //don't filter the row groups
//         null);               //The sort order
//
//         **/
////
////        // Perform this raw SQL query "SELECT * FROM pets"
////        // to get a Cursor that contains all rows from the pets table.
////        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
//
////
////        try {
////            // Display the number of rows in the Cursor (which reflects the number of rows in the
////            // pets table in the database).
////            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
////            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
////        } finally {
////            // Always close the cursor when you're done reading from it. This releases all its
////            // resources and makes it invalid.
////            cursor.close();
////        }
//
//        //query the database through the ContentProvider(PetProvider)
//        //this is done through th getContentResolver() method
//        Cursor cursor = getContentResolver().query(
//                PetEntry.CONTENT_URI,                                         //the content uri
//                projection,                                                 //the columns to return for each row
//                null,                                                       //selection criteria
//                null,                                                       //selection criteria
//                null                                                        //sort order for returned rows
//        );
//
//        // Find ListView to populate
//        ListView lvItems = (ListView) findViewById(R.id.list);
//        // Setup cursor adapter using cursor from last step
//        PetCursorAdapter petCursorAdapter = new PetCursorAdapter(this, cursor);
//        // Attach cursor adapter to the ListView
//        lvItems.setAdapter(petCursorAdapter);
////        assert cursor != null;
////        cursor.close();
//
//        //   TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//
////        try {
////            // Create a header in the Text View that looks like this:
////            //
////            // The pets table contains <number of rows in Cursor> pets.
////            // _id - name - breed - gender - weight
////            //
////            // In the while loop below, iterate through the rows of the cursor and display
////            // the information from each column in this order.
////           // displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
////           // displayView.append(PetEntry._ID + " - " +
////              //      PetEntry.COLUMN_PET_NAME + " - " + PetEntry.COLUMN_PET_BREED + " - " +
////              //      PetEntry.COLUMN_PET_GENDER + " - " + PetEntry.COLUMN_PET_WEIGHT + "\n");
////
////            // Figure out the index of each column
////            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
////            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
////            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
////            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
////            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
////
////
////            // Iterate through all the returned rows in the cursor
////            while (cursor.moveToNext()) {
////                // Use that index to extract the String or Int value of the word
////                // at the current row the cursor is on.
////                int currentID = cursor.getInt(idColumnIndex);
////                String currentName = cursor.getString(nameColumnIndex);
////                String currentBreed = cursor.getString(breedColumnIndex);
////                int currentGender = cursor.getInt(genderColumnIndex);
////                int currentWeight = cursor.getInt(weightColumnIndex);
////
////                // Display the values from each column of the current row in the cursor in the TextView
////               // displayView.append(("\n" + currentID + " - " +
////                        currentName + " - " + currentBreed + " - " + currentGender + " - " + currentWeight));
//////            }
//////        } finally {
//////            // Always close the cursor when you're done reading from it. This releases all its
//////            // resources and makes it invalid.
//////            assert cursor != null;
//////            cursor.close();
//////        }
//
//
//    }

    private void insertPet() {


//        // Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);


        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        //insert a new row for Toto in the database , returning the ID of that new row,
        //The first argument for db.insert() is the pets table name
        //The second argument provides the name of the column in which the framework
        //can insert NULL in the event that the ContentValues is empty (if this is set to "null"
        // then the Content will not insert a row when there are no values)
        //The third argument is the ContentValues object containing the info for Toto

//        // Insert the new row, returning the primary key value of the new row
//        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);

        // Log.v("CatalogActivity", "new row id" + newRowId);
//        Log.v("CatalogActivity", "dummy pet added " + uri);

        Toast.makeText(CatalogActivity.this, "new pet added with uri: " + newUri, Toast.LENGTH_SHORT).show();


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
                // Do nothing for now
                insertPet();
                // displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  //parent activity context
                PetEntry.CONTENT_URI, //provider content URI to query
                projection,           //columns to include in the resulting Cursor
                null,                 //no selection clause
                null,                  //no selection arguments
                null);                  //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update PetCursorAdapter with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_pets_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Perform the deletion of all pets in the database.
     */
    private void deletePet() {

        // Do nothing for now
        int rowNum = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Toast.makeText(CatalogActivity.this, R.string.catalog_all_pets_deleted, Toast.LENGTH_SHORT).show();
       // finish();

//
//        // Only perform the delete if this is an existing pet.
//        if (mCurrentPetUri != null) {
//            // Call the ContentResolver to delete the pet at the given content URI.
//            // Pass in null for the selection and selection args because the mCurrentPetUri
//            // content URI already identifies the pet that we want.
//            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
//
//            // Show a toast message depending on whether or not the delete was successful.
//            if (rowsDeleted == 0) {
//                // If no rows were deleted, then there was an error with the delete.
//                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                // Otherwise, the delete was successful and we can display a toast.
//                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
        // Close the activity

    }
}
