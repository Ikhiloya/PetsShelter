package com.loya.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * The contract class allows you to use the same constants across all the other classes in the same package.
 * This lets you change a column name in one place and have it propagate throughout your code.
 */
public final class PetContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract() {

    }

    public static final String PATH_PETS = "pets";


    public static abstract class PetEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.loya.android.pets/pets");


        public static final String TABLE_NAME = "pets";

        //By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID
        // that some Android classes such as cursor adaptors will expect it to have.
        // It's not required, but this can help your database work harmoniously with the Android framework.
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final String CONTENT_AUTHORITY = "com.loya.android.pets";

        /**
         * possible values for the gender of the pet
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;


    }
}
