package com.artioml.yandex.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.artioml.yandex.data.DatabaseDescription.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KILauncherDB";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String RECENT_REQUESTS_TABLE_SQL = "CREATE TABLE " + RecentRequests.TABLE_NAME + "(" +
                RecentRequests._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecentRequests.COLUMN_REQUEST + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(RECENT_REQUESTS_TABLE_SQL);

        final String FAVORITES_TABLE_SQL = "CREATE TABLE " + Favorites.TABLE_NAME +"(" +
                Favorites._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Favorites.COLUMN_APP + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(FAVORITES_TABLE_SQL);

        final String POPULAR_APPS_TABLE_SQL = "CREATE TABLE " + PopularApps.TABLE_NAME +"(" +
                PopularApps._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PopularApps.COLUMN_APP + " TEXT NOT NULL, " +
                PopularApps.COLUMN_COUNT + " INTEGER);";
        sqLiteDatabase.execSQL(POPULAR_APPS_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

}