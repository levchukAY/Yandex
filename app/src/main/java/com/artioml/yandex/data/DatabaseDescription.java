package com.artioml.yandex.data;

import android.provider.BaseColumns;

public class DatabaseDescription {

    public static final class RecentRequests implements BaseColumns {
        public final static String TABLE_NAME = "RecentRequests";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_REQUEST = "request";
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