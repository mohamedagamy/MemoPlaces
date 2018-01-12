package com.agamy.android.memoplaces.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;
/**
 * Created by agamy on 12/24/2017.
 */

public class PlacesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "places.db";
    public static final int DATABASE_VERSION = 3;


    public PlacesDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PlacesEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(PlacesEntry.SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
