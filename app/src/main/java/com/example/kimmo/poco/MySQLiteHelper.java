package com.example.kimmo.poco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = "SQLHELP";

    /*
    TODO: This is very bad database, loads of duplicate information! Rework needed!
    long   long      String long      long       double    double     double    String String
	pk_id, route_id, title, starttime, timestamp, latitude, longitude, altitude, ssid, rddi
    */

    public static final String TABLE_ROUTES = "routes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROUTE_ID = "route_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ALTITUDE = "altitude";
    public static final String COLUMN_SSID = "ssid";
    public static final String COLUMN_RDDI = "rddi";

    private static final String DATABASE_NAME = "poco.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ROUTES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ROUTE_ID + " integer not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_START_TIME + " integer not null, "
            + COLUMN_TIMESTAMP + " integer not null, "
            + COLUMN_LATITUDE + " real not null, "
            + COLUMN_LONGITUDE + " real not null, "
            + COLUMN_ALTITUDE + " real not null, "
            + COLUMN_SSID + " text, "
            + COLUMN_RDDI + " text);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // FOR DB DEBUG:
    public MySQLiteHelper(Context context, String path) {
        super(context, path, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        onCreate(db);
    }

}
