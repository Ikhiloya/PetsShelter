package com.loya.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loya.android.pets.data.PetContract.PetEntry;

/**
 * ContentProvider for the Pets App
 */
public class PetProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    /**
     * Database Helper object
     */
    private PetDbHelper mDbHelper;

    /**
     * URI matcher code forthe content URI for the pets table
     */
    private static final int PETS = 100;
    /**
     * URI matcher code forthe content URI for a single pet in the pets table
     */
    private static final int PET_ID = 101;


    /**
     * UriMatcher object to match a a content URI to a corresponding code.
     * The input passed into the constructor represents the code  to return for the root URI.\
     * It is common to use the NO_MATCH as the input for this case
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer. This is called the first time anything is called from this class
    static {
        /*
        *The calls to the addURI() goes here, for all of the content URI patterns that the provider
        * should recognize. All paths added to the UriMatcher have a corresponding code to return
        * when a match is found
         */

        /**
         * The content Uri of the form "content://com.loya.android.pets/pets" will map to the
         * integer code PETS. this URI is used to provide access to multiple rows of the pets table
         */
        // sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetContract.PATH_PETS , PETS);
        sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);


        /**
         * The content Uri of the form "content://com.loya.android.pets/pets/#" will map to the
         * integer code PETS. this URI is used to provide access to a single row of the pets table
         */
        //sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
        sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }


    /**
     * initialize the provider and the database helper method
     * * @return
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());

        return true;
    }


    /**
     * performs the query for the given Uri, use the given projection, selection, selection arguments and sort order
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor willhold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //for the PETS code, Query the pets table directly with the given
                //projection, selection, selection arguments, and sort order.
                //The cursor could contain multiple rows of the pets table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                //For the PET_ID code, Query the pets table directly with the given
                //for an example URI such as "content://com.loya.android.pets/pets/3",
                //the selection will be "_id=?" and the selection argument will be a
                //String array containing the actual ID of 3 in this case.
                //
                //For every "?" in the selection, we need to have an element in the selection
                //arguments that will fill in the "?". Since we have 1 question mark in
                //the selection, we have 1 String in the selection arguments' String array
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //This will perform a query on the pets table where the _id equals 3 to return
                // a cursor containing that row of the pets table
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown URI " + uri);

        }
        //set notification URI to the Cursor, so we know what content URI the Cursor was created for.
        //if the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //return the cursor
        return cursor;
    }

    /**
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * insert new data into the provider with the given ContentVAlues
     *
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("insertion is not supported for this " + uri);
        }


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //notify all listeners that the data has been deleted for the pet content URI
                getContext().getContentResolver().notifyChange(uri, null);

                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //notify all listeners that the data has been deleted for the pet content URI
                getContext().getContentResolver().notifyChange(uri, null);

                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * updates the data at the given selection and selection arguments with the mew ContentValues
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        // Check that the breed is not null
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        if (breed == null) {
            throw new IllegalArgumentException("Pet requires a breed");
        }

        // Check that the weight is not less than or equal to 0
        int weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight < 0) {
            Toast.makeText(getContext(), "weight must not be less than zero ", Toast.LENGTH_SHORT).show();
        }

    // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(PetEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //notify all listeners that the data been updated for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of database rows affected by the update statement
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
    }


}
