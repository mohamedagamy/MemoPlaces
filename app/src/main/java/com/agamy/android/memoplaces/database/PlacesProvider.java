package com.agamy.android.memoplaces.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;

/**
 * Created by agamy on 12/24/2017.
 */

public class PlacesProvider extends ContentProvider {


    public static final int PLACES = 100;
    public static final int PLACE_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private PlacesDbHelper mDbHelper;

    static {

        sUriMatcher.addURI(PlacesContract.CONTENT_AUTHORITY, PlacesContract.PATH_PLACES, PLACES);
        sUriMatcher.addURI(PlacesContract.CONTENT_AUTHORITY, PlacesContract.PATH_PLACES + "/#", PLACE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PlacesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                cursor = database.query(PlacesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLACE_ID:
                selection = PlacesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PlacesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        if(cursor != null) {
            if(getContext() != null)
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            cursor.moveToFirst();
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return insertPlace(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        //int match = sUriMatcher.match(uri);
         int match = (s == null) ? PLACES : PLACE_ID;
        int rowid = 0;
        switch (match) {
            case PLACES:


                rowid =  deleteAllPlaces(uri);
                return rowid;
            case  PLACE_ID:
                rowid = deleteOnePlace(uri , strings);
                return rowid;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    //*********************************************
    private Uri insertPlace(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(PlacesEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e("", "Failed to insert row for " + uri);
            return null;
        }

        if(id > 0) {
            if(getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private int deleteOnePlace(Uri uri, String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String whereClause= PlacesEntry._ID+" = ?";
        int rowId = db.delete(PlacesEntry.TABLE_NAME,whereClause,strings);

        if(rowId > 0) {
            if(getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowId;
    }

    private int deleteAllPlaces(Uri uri) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowId = db.delete(PlacesEntry.TABLE_NAME,null,null);

        if(rowId > 0){
            if(getContext() != null)
                getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowId;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return PlacesEntry.CONTENT_LIST_TYPE;
            case PLACE_ID:
                return PlacesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
