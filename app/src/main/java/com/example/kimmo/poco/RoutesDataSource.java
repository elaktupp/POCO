package com.example.kimmo.poco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RoutesDataSource {

    public static final String TAG = "RoutesDataSource";
    /*
    TODO: This is very bad database, loads of duplicate information! Rework needed!
    long   long      String long       long       double    double     double    String String
	pk_id, route_id, title, starttime, timestamp, latitude, longitude, altitude, ssid, rddi
    */

    /* Route contains 1 - n Legs that at the moment are just identified by route_id
     * same route_id means the row is part of same Route.
     */

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ROUTE_ID,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_START_TIME,
            MySQLiteHelper.COLUMN_TIMESTAMP,
            MySQLiteHelper.COLUMN_LATITUDE,
            MySQLiteHelper.COLUMN_LONGITUDE,
            MySQLiteHelper.COLUMN_ALTITUDE,
            MySQLiteHelper.COLUMN_SSID,
            MySQLiteHelper.COLUMN_RDDI
    };

    public RoutesDataSource(Context context) {
            dbHelper = new MySQLiteHelper(context);
    }

    // FOR DB DEBUG: Stores the file to the memory card
    public RoutesDataSource(Context context, String path) {
        path = path.concat("/poco.db"); // DATABASE_NAME from MySQLiteHelper, change to public?
        Log.d(TAG, "DEBUG, db to path: " + path);
        dbHelper = new MySQLiteHelper(context, path);
    }

    public void open() throws SQLException {
        Log.d(TAG,"open");
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.d(TAG,"close");
        dbHelper.close();
    }

    public Route createRoute(Route route) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ROUTE_ID, route.getRouteId());
        values.put(MySQLiteHelper.COLUMN_TITLE, route.getTitle());
        values.put(MySQLiteHelper.COLUMN_START_TIME, route.getStartTime());
        values.put(MySQLiteHelper.COLUMN_TIMESTAMP, route.getTimestamp());
        values.put(MySQLiteHelper.COLUMN_LATITUDE, route.getLatitude());
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, route.getLongitude());
        values.put(MySQLiteHelper.COLUMN_ALTITUDE, route.getAltitude());
        values.put(MySQLiteHelper.COLUMN_SSID, route.getSsid());
        values.put(MySQLiteHelper.COLUMN_RDDI, route.getRddi());

        long insertId = database.insert(MySQLiteHelper.TABLE_ROUTES, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUTES, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        Route newRoute = cursorToRoute(cursor);
        cursor.close();
        return newRoute;
    }

    public void deleteRoute(Route route) {
        long id = route.getId();
        System.out.println("Route deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ROUTES, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<Route>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Route route = cursorToRoute(cursor);
            routes.add(route);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return routes;
    }

    public List<Long> getRouteIds() {
        List<Long> route_ids = new ArrayList<Long>();

        Log.i("DATA", "getRouteIds");

        String sql="SELECT "
                + MySQLiteHelper.COLUMN_ID +","
                + MySQLiteHelper.COLUMN_ROUTE_ID + " FROM "
                + MySQLiteHelper.TABLE_ROUTES + " GROUP BY "
                + MySQLiteHelper.COLUMN_ROUTE_ID +";";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            route_ids.add(cursor.getLong(1));
            Log.i("DATA", "route_id " + cursor.getLong(1));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return route_ids;

    }

    public ArrayList<Route> getRoute(long route_id) {
        ArrayList<Route> routes = new ArrayList<Route>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ROUTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // Pick the route rows
            if (cursor.getLong(1) == route_id) {
                Route route = cursorToRoute(cursor);
                routes.add(route);
            }
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return routes;
    }

    private Route cursorToRoute(Cursor cursor) {
        Route route = new Route();
        route.setId(cursor.getLong(0));
        route.setRouteId(cursor.getLong(1));
        route.setTitle(cursor.getString(2));
        route.setStartTime(cursor.getLong(3));
        route.setTimestamp(cursor.getLong(4));
        route.setLatitude(cursor.getDouble(5));
        route.setLongitude(cursor.getDouble(6));
        route.setAltitude(cursor.getDouble(7));
        route.setSsid(cursor.getString(8));
        route.setRddi(cursor.getString(9));
        return route;
    }

}
