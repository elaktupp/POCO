package com.example.kimmo.poco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends FragmentActivity
        implements DatabaseFragment.OnDatabaseFragmentListener, MainFragment.OnMainFragmentListener,
        SettingsFragment.OnSettingsFragmentInteraction, RoutesFragment.OnRoutesInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    public static final String TAG = "POCODBG:MainActivity";

    public static final int MainFrag = 0;
    public static final int RoutesFrag = 1;
    public static final int DatabaseFrag = 2;
    public static final int SettingsFrag = 3;

    private int gpsInterval = 0; // seconds

    // 0 Main, 1 Routes, 2 Database, 3 Settings & Tests

    private MainFragment fragmentMain = null;
    private RoutesFragment fragmentRoutes = null;
    private DatabaseFragment fragmentDatabase = null;
    private SettingsFragment fragmentSettings = null;

    private boolean isCollecting = false;
    private boolean isGpsWifiTest = false;
    private RoutesDataSource datasource;
    private boolean gpsActive = false;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private boolean wifiActive = false;
    private WifiManager mainWifi = null;
    private WifiReceiver receiverWifi = null;
    private Route routeLegData = null;
    private long routeId = 0;
    private long routeStartTime = 0;

    private int backstack = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsInterval = getResources().getInteger(R.integer.interval_default);

        changeFragment(MainFrag);

        // POINTLESS COLLECTING...

        // Collected data will be stored here
        datasource = new RoutesDataSource(this); // FOR DB DEBUG: (this, "/storage/sdcard1");
        datasource.open(); // open "DB" so it is ready for service right util the end

        /* 1. GPS location received -> Route
         * 2. Initiate WiFi scan -> Route
         * 3. Wait for next location...
         */

        // GPS (or other) LOCATION

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Got new location from the provider
                Log.i(TAG, "GOT LOCATION: " + location.toString());
                routeLegData = new Route();
                routeLegData.setRouteId(routeId);
                routeLegData.setTitle(Long.toString(routeId));
                routeLegData.setRouteId(routeId);
                routeLegData.setLatitude(location.getLatitude());
                routeLegData.setLongitude(location.getLongitude());
                routeLegData.setAltitude(location.getAltitude());
                routeLegData.setStartTime(routeId); // TODO: yes yes, bad database!
                routeLegData.setTimestamp(GregorianCalendar.getInstance().getTimeInMillis());

                if (isGpsWifiTest) {
                    // This was test, so just one round and no Wifi
                    toggleGPS(null);
                    if (fragmentSettings != null) {
                        fragmentSettings.appendText("\nGPS test completed: " +
                                Double.toString(location.getLatitude()) +
                                "," + Double.toString(location.getLongitude()));
                    }
                } else {
                    fragmentMain.appendText(".");
                    // Real thing, continue with Wifi
                    toggleWIFI(null);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { Log.i(TAG, "STATUS: " + provider + ", " + status); }

            public void onProviderEnabled(String provider) {  Log.i(TAG, "ENABLED: " + provider); }

            public void onProviderDisabled(String provider) { Log.i(TAG, "DISABLED: " + provider); }
        };

        // WIFI SCAN
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // BACK STACK
        getSupportFragmentManager().addOnBackStackChangedListener(this);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiverWifi); // don't leave it hanging on
        datasource.close(); // close "DB" when it is not needed, goes through onPause when Activity is closed
        super.onDestroy();
    }

    private void updateActiveFragment(int frag, Fragment object) {
        switch(frag) {
            case MainActivity.MainFrag:
                fragmentMain = (MainFragment) object;
                fragmentRoutes = null;
                fragmentDatabase = null;
                fragmentSettings = null;
                break;
            case MainActivity.RoutesFrag:
                fragmentMain = null;
                fragmentRoutes = (RoutesFragment) object;
                fragmentDatabase = null;
                fragmentSettings = null;
                break;
            case MainActivity.DatabaseFrag:
                fragmentMain = null;
                fragmentRoutes = null;
                fragmentDatabase = (DatabaseFragment) object;
                fragmentSettings = null;
                break;
            case MainActivity.SettingsFrag:
                fragmentMain = null;
                fragmentRoutes = null;
                fragmentDatabase = null;
                fragmentSettings = (SettingsFragment) object;
                break;
        }
    }

    private void changeFragment(int frag) {

        Fragment newFragment = null;
        Bundle args = null;

        Log.d(TAG, "changeFragment " + frag);

        switch(frag) {
            case MainActivity.MainFrag:
                newFragment = new MainFragment();
                args = new Bundle();
                args.putInt(SettingsFragment.ARG_INTERVAL, gpsInterval);
                newFragment.setArguments(args);
                break;
            case MainActivity.RoutesFrag:
                newFragment = new RoutesFragment();
                break;
            case MainActivity.DatabaseFrag:
                newFragment = new DatabaseFragment();
                break;
            case MainActivity.SettingsFrag:
                // Create fragment and give it an argument specifying the article it should show
                newFragment = new SettingsFragment();
                args = new Bundle();
                args.putInt(SettingsFragment.ARG_INTERVAL, gpsInterval);
                newFragment.setArguments(args);
                break;
        }

        // Prepare a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment.
        transaction.replace(R.id.fragmentContainer, newFragment);
        // Add the transaction to the back stack so the user can navigate back.
        // transaction.addToBackStack(null); <- not needed in this case

        // TODO: This is a bit odd, should probably rethink and figure out the back stack.
        /*
         * In this case the Back Stack should contain only 1 fragment that is MainFrag.
         * The backstack keeps count of fragments in the Back Stack and allows adding
         * only if it is empty.
         * If the current fragment is changed to a MainFrag then the old MainFrag must
         * be removed from the Back Stack. On next change the new MainFrag is added to
         * now empty Back Stack.
         */
        if (backstack == 0 && frag != MainActivity.MainFrag) {
            Log.d(TAG, "PUSH to BACK STACK");
            transaction.addToBackStack("main");
        } else if (frag == MainActivity.MainFrag) {
            Log.d(TAG, "POP from BACK STACK");
            // pop the old MainFrag away, next change will push new one
            getSupportFragmentManager().popBackStackImmediate("main", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // NOTE! Have to use Immediate, otherwise this back stack thingy will fail. If the pop is asyncronous
            // then the onBackStackChange will come after updateActiveFragment and the fragmentMain gets the
            // old value...
        }

        // Have reference to active Fragment to be able to use its methods
        updateActiveFragment(frag, newFragment);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onBackStackChanged() {
        backstack = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "onBackStackChanged " + backstack);
        if (backstack == 0) {
            fragmentMain = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            Log.d(TAG, "fragmentMain " + fragmentMain);
        }
    }

    @Override
    public void onDatabaseFragmentInteraction(int cmd, String param) {

        Log.d(TAG, "onDatabaseFragmentInteraction " + cmd + " \"" + param + "\"");
        /*
        switch (cmd) {
            case R.integer.????:
                break;
            default:
                Log.e(TAG,"onDatabaseFragmentInteraction: Unexpected command "+cmd+" with "+param);
        }
        */
    }

    @Override
    public void onMainFragmentInteraction(int cmd, String param) {

        Log.d(TAG, "onMainFragmentInteraction " + cmd + " \"" + param + "\"");
        /*
        switch (cmd) {
            case R.integer.cmd_to_fragment:
                changeFragment(Integer.parseInt(param));
                break;
            default:
                Log.e(TAG,"onMainFragmentInteraction: Unexpected command "+cmd+" with "+param);
        }
        */
    }

    @Override
    public void onSettingsFragmentInteraction(int cmd, String param) {

        Log.d(TAG, "onSettingsFragmentInteraction " + cmd + " \"" + param + "\"");

        switch (cmd) {
            case R.integer.cmd_update_interval:
                gpsInterval = Integer.parseInt(param); // Interval was changed in SettingsFragment
                break;
            default:
                Log.e(TAG,"onSettingsFragmentInteraction: Unexpected command "+cmd+" with "+param);
        }
    }

    @Override
    public void onRoutesFragmentInteraction(int cmd, String param) {

        Log.d(TAG, "onRoutesFragmentInteraction " + cmd + " \"" + param + "\"");
        /*
        switch (cmd) {
            case R.integer.????:
                break;
            default:
                Log.e(TAG,"onRoutesFragmentInteraction: Unexpected command "+cmd+" with "+param);
        }
        */
    }

    /*
     * Pointless Collecting methods
     */

    // GPS
    public void toggleGPS(View view) {

        Log.d(TAG, "toggleGPS, current "+gpsActive+", test "+isGpsWifiTest);

        if (view != null) {
            // If there is view this is called by dedicated button i.e. Test button
            isGpsWifiTest = true;
            // Check null in case user left settings.
            if (fragmentSettings != null) {
                fragmentSettings.setText("GPS test started.");
            }
        }

        if (gpsActive) {
            gpsActive = false;
            // Remove the listener you previously added
            locationManager.removeUpdates(locationListener);
        } else {
            gpsActive = true;
            // Register the listener with the Location Manager to receive location updates
            // params: provider, interval in ms, distance in meters, listener
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (gpsInterval * 1000), 0, locationListener);
            // Id for this particular route. TODO: yes the database is bad - normalization needed!
            routeId = GregorianCalendar.getInstance().getTimeInMillis();
        }
    }

    // WIFI
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {

            Log.d(TAG, "Got WIFI result, which is mine "+wifiActive);

            if (wifiActive) {
                // Received result of Wifi scan that was activated by POCO
                StringBuilder sbSSID = new StringBuilder();
                StringBuilder sbRDDI = new StringBuilder();
                wifiActive = false;
                List<ScanResult> wifiList = mainWifi.getScanResults();
                for (int i = 0; i < wifiList.size(); i++) {
                    sbSSID.append((wifiList.get(i).SSID).toString());
                    sbRDDI.append(Integer.toString(wifiList.get(i).level) + "dBm");
                }

                if (isGpsWifiTest) {
                    // This was test, so just one round, no GPS running. Check null in case user left settings.
                    if (fragmentSettings != null) {
                        fragmentSettings.appendText("\nWifi test completed, found " + wifiList.size());
                    }
                } else {
                    // Real thing, routeLegData created on GPS location receiver

                    routeLegData.setSsid(sbSSID.toString());
                    routeLegData.setRddi(sbRDDI.toString());

                    datasource.createRoute(routeLegData); // Save to DB

                    // And now just wait for next GPS location update...
                    routeLegData = null;

                }
            }
        }
    }

    public void toggleWIFI(View view) {

        Log.d(TAG, "toggleWIFI, current "+wifiActive+", test "+isGpsWifiTest);

        if (view != null) {
            // If there is view this is called by dedicated button i.e. Test button
            isGpsWifiTest = true;
            fragmentSettings.setText("Wifi test started.");
        }

        if (!wifiActive) {
            wifiActive = true;
            mainWifi.startScan();
        }
    }

    /*
     * Methods to handle fragment button clicks. // TODO: Should this go through Listeners instead?
     */

    public void toData(View view) {
        Log.w(TAG,"toData "+view);
        changeFragment(MainActivity.DatabaseFrag);
    }

    public void toMain(View view) {
        Log.w(TAG,"toMain "+view);
        changeFragment(MainActivity.MainFrag);
    }

    public void toRoutes(View view) {
        Log.w(TAG,"toRoutes "+view);
        changeFragment(MainActivity.RoutesFrag);
    }

    public void toSettings(View view) {
        Log.w(TAG,"toSettings "+view);
        changeFragment(MainActivity.SettingsFrag);
    }

    // THE ACTUAL POCO-OPERATION IS STARTED HERE

    public void toStart(View view) {
        Log.w(TAG, "toStart " + view + ", interval " + gpsInterval);

        toggleGPS(null);

        if (gpsActive) {
            fragmentMain.pocoActive(true);
            fragmentMain.setText("GPS started, interval "+gpsInterval+" seconds.\n");
        } else {
            fragmentMain.pocoActive(false);
            fragmentMain.appendText("\nGPS stopped.\n");
        }

    }
}
