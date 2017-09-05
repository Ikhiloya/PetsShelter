package com.loya.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.loya.android.pets.data.PetContract.PetEntry;

/**
 * One of the main principles of SQL databases is the schema: a formal declaration of how the database is organized.
 * The schema is reflected in the SQL statements that you use to create your database.
 * You may find it helpful to create a companion class, known as a contract class,
 * which explicitly specifies the layout of your schema in a systematic and self-documenting way.
 * <p/>
 * A useful set of APIs is available in the SQLiteOpenHelper class.
 * When you use this class to obtain references to your database, the system performs the potentially long-running
 * operations of creating and updating the database only when needed and not during app startup.
 * All you need to do is call getWritableDatabase() or getReadableDatabase().
 */
public class PetDbHelper extends SQLiteOpenHelper {

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    /**
     * name of the Database file
     **/
    public static final String DATABASE_NAME = "pets.db";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE TABLE pets (id INTEGER PRIMARY KEY, name TEXT, weight INTEGER);
        //create a String that contains the SQ
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PetEntry.TABLE_NAME + "("
                        + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                        + PetEntry.COLUMN_PET_BREED + " TEXT, "
                        + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                        + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }
}
