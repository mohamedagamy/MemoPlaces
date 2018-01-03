package com.agamy.android.memoplaces.database;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by agamy on 12/24/2017.
 */

public final class PlacesContract{

    public static final String CONTENT_AUTHORITY = "com.agamy.android.memoplaces";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLACES = "places";


    public static final class PlacesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLACES);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;


        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = _ID;
        public static final String COLUMN_PLACE_ADDRESS = "address";
        public static final String COLUMN_PLACE_COUNTRY = "country";
        public static final String COLUMN_PLACE_LATITUDE = "lat";
        public static final String COLUMN_PLACE_LONGITUDE = "lng";


        public static final int COLUMN_PLACE_ID_INDEX = 0;
        public static final int COLUMN_PLACE_ADDRESS_INDEX = 1;
        public static final int COLUMN_PLACE_COUNTRY_INDEX = 2;
        public static final int COLUMN_PLACE_LATITUDE_INDEX = 3;
        public static final int COLUMN_PLACE_LONGITUDE_INDEX = 4;

        public static final String SQL_CREATE_TABLE = " CREATE TABLE "+TABLE_NAME+" ( "
                +COLUMN_PLACE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_PLACE_ADDRESS+" TEXT, "
                +COLUMN_PLACE_COUNTRY+" TEXT, "
                +COLUMN_PLACE_LATITUDE+" REAL, "
                +COLUMN_PLACE_LONGITUDE+" REAL "+" ); ";
        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;


    }
}
