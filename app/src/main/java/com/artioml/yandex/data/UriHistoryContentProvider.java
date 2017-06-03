package com.artioml.yandex.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import com.artioml.yandex.R;
import com.artioml.yandex.data.DatabaseDescription.RecentRequests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UriHistoryContentProvider extends ContentProvider {

    // Используется для обращения к базе данных
    private DatabaseHelper dbHelper;

    // UriMatcher помогает ContentProvider определить выполняемую операцию
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Константы, используемые для определения выполняемой операции
            private static final int LAST_REQUEST = 1;
    private static final int REQUESTS = 2;
    private static final int TODAY_REQUESTS = 3;

    // Статический блок для настройки UriMatcher объекта ContentProvider
    static {
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                RecentRequests.TABLE_NAME + "/last", LAST_REQUEST);

        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                RecentRequests.TABLE_NAME, REQUESTS);

        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                RecentRequests.TABLE_NAME + "/today", TODAY_REQUESTS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecentRequests.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            /*case LAST_REQUEST:
                queryBuilder.appendWhere(DatabaseDescription.RecentRequests._ID + "=" + uri.getLastPathSegment());
                break;*/
            case REQUESTS:
                break;
            /*case TODAY_REQUESTS:
                break;*/
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.err_invalid_query_uri) + uri);
        }

        // Выполнить запрос для получения одного или всех контактов
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // Настройка отслеживания изменений в контенте
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        /*int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case LAST_REQUEST:
                // Получение из URI идентификатора контакта
                String id = uri.getLastPathSegment();

                // Удаление контакта
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Contact.TABLE_NAME, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // Оповестить наблюдателей об изменениях в базе данных
        if (numberOfRowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numberOfRowsDeleted;*/
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri newContactUri = null;
        DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        switch (uriMatcher.match(uri)) {
            case REQUESTS:
                // При успехе возвращается идентификатор записи нового контакта
                long rowId = dbHelper.getWritableDatabase().insert(
                        RecentRequests.TABLE_NAME, null, values);

                // Если контакт был вставлен, создать подходящий Uri;
                // в противном случае выдать исключение
                if (rowId > 0) { // SQLite row IDs start at 1
                    newContactUri = RecentRequests.buildRequestUri(rowId);
                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(getContext().getString(R.string.err_insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.err_invalid_insert_uri) + uri);
        }

        return newContactUri;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        /*int numberOfRowsUpdated; // 1, если обновление успешно; 0 при неудаче

        switch (uriMatcher.match(uri)) {
            case LAST_REQUEST:
                // Получение идентификатора контакта из Uri
                String id = uri.getLastPathSegment();

                // Обновление контакта
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Contact.TABLE_NAME, values, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // Если были внесены изменения, оповестить наблюдателей
        if (numberOfRowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numberOfRowsUpdated;*/
        return 0;
    }
}
