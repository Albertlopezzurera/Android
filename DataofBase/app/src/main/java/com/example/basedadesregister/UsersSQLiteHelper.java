package com.example.basedadesregister;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersSQLiteHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "students.db";

    private static final String SQL_CREATE_TABLES =
            "CREATE TABLE " + UserDataBaseContract.UsersTable.TABLE + " ("
                    + UserDataBaseContract.UsersTable.COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + UserDataBaseContract.UsersTable.COLUMN_NAME + " TEXT, " +
                    UserDataBaseContract.UsersTable.COLUMN_SURNAME + " TEXT, " +
                    UserDataBaseContract.UsersTable.COLUMN_CYCLE + " TEXT, " +
                    UserDataBaseContract.UsersTable.COLUMN_COURSE + " TEXT)";

    private static final String SQL_DROP_TABLES =
            "DROP TABLE IF EXISTS " + UserDataBaseContract.UsersTable.TABLE;

    public UsersSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLES);
        onCreate(db);
    }
}
