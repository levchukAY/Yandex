package com.artioml.yandex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {

    // Имя ContentProvider: обычно совпадает с именем пакета
    public static final String AUTHORITY = "com.artioml.yandex.data";

    // Базовый URI для взаимодействия с ContentProvider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class RecentRequests implements BaseColumns {

        public final static String TABLE_NAME = "RecentRequests";

        // Объект Uri для таблицы contacts
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_REQUEST = "request";

        // Создание Uri для конкретного контакта
        public static Uri buildRequestUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Favorites implements BaseColumns {
        public final static String TABLE_NAME = "Favorites";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_APP = "app";
    }

    public static final class PopularApps implements BaseColumns {
        public final static String TABLE_NAME = "PopularApps";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_APP = "app";
        public final static String COLUMN_COUNT = "count";
    }

}