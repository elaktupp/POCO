package com.example.kimmo.poco;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatabaseFragment.OnDatabaseFragmentListener} interface
 * to handle interaction events.
 * Use the {@link DatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatabaseFragment extends Fragment {

    public static final String TAG = "POCODBG:Database";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDatabaseFragmentListener mListener;

    private ArrayList<String> gpsCoords = new ArrayList<String>();;
    private RoutesDataSource datasource = null;
    private List<Long> routes = null;
    private Spinner spinnerRoutes = null;
    private long selectedRouteId = 0L;

    private TextView textViewData =  null;
    private ToggleButton btnToggleGps = null;
    private boolean checkedToggleGps = false;
    private ToggleButton btnToggleWifi = null;
    private boolean checkedToggleWifi = false;
    private ToggleButton btnToggleTime = null;
    private boolean checkedToggleTime = false;
    private ToggleButton btnToggleDistance = null;
    private boolean checkedToggleDistance = false;
    private ToggleButton btnToggleLeg = null;
    private boolean checkedToggleLeg = false;
    private ToggleButton btnToggleAltitude = null;
    private boolean checkedToggleAltitude = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatabaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatabaseFragment newInstance(String param1, String param2) {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View thisFragment = inflater.inflate(R.layout.fragment_database, container, false);

        // BUTTONS
        btnToggleGps = (ToggleButton) thisFragment.findViewById(R.id.toggleCoords);
        btnToggleGps.setChecked(checkedToggleGps);
        btnToggleGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleGps = btnToggleGps.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });
        btnToggleWifi = (ToggleButton) thisFragment.findViewById(R.id.toggleWifi);
        btnToggleWifi.setChecked(checkedToggleWifi);
        btnToggleWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleWifi = btnToggleWifi.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });
        btnToggleTime = (ToggleButton) thisFragment.findViewById(R.id.toggleTime);
        btnToggleTime.setChecked(checkedToggleTime);
        btnToggleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleTime = btnToggleTime.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });
        btnToggleDistance = (ToggleButton) thisFragment.findViewById(R.id.toggleDistance);
        btnToggleDistance.setChecked(checkedToggleDistance);
        btnToggleDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleDistance = btnToggleDistance.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });
        btnToggleLeg = (ToggleButton) thisFragment.findViewById(R.id.toggleLeg);
        btnToggleLeg.setChecked(checkedToggleLeg);
        btnToggleLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleLeg = btnToggleLeg.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });
        btnToggleAltitude = (ToggleButton) thisFragment.findViewById(R.id.toggleAltitude);
        btnToggleAltitude.setChecked(checkedToggleAltitude);
        btnToggleAltitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedToggleAltitude = btnToggleAltitude.isChecked(); // remember value for orientation change
                updateDataView(selectedRouteId);
            }
        });

        // ROUTES
        datasource = new RoutesDataSource(getActivity());
        datasource.open();
        routes = datasource.getRouteIds();

        List<String> spinnerItems = new ArrayList<String>();
        for (int i = 0; i < routes.size(); i++) {
            Log.i(TAG, "[" + i + "] " + routes.get(i).toString());
            spinnerItems.add(routes.get(i).toString());
        }

        spinnerRoutes = (Spinner) thisFragment.findViewById(R.id.spinnerRoutes);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerRoutes.setAdapter(dataAdapter);

        spinnerRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the coordinates of selected route to gpsCoords
                selectedRouteId = Long.parseLong(parent.getItemAtPosition(position).toString());
                updateDataView(selectedRouteId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "NOTHING SELECTED");
            }
        });

        textViewData = (TextView) thisFragment.findViewById(R.id.textViewData);

        return thisFragment;
    }

    // THIS IS A TRICK TO GET FRAGMENT TO CHANGE ITS LAYOUT ACCORDING TO THE ORIENTATION
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /* Re-create view... */
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViewsInLayout();
        View view = onCreateView(getActivity().getLayoutInflater(), viewGroup, null); viewGroup.addView(view);

        if (selectedRouteId != 0) {
            updateDataView(selectedRouteId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDatabaseFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDatabaseFragmentListener {
        // TODO: Update argument type and name
        public void onDatabaseFragmentInteraction(int cmd, String param);
    }

    /* ROUTE: <id>
     * LAt: <lat> LON: <lon> ALT: <alt>
     *  + Wifi
     *
     */

    private void updateDataView(long route_id) {
        List<Route> selectedRoute = datasource.getRoute(route_id);
        if (selectedRoute.size() > 0) {
            long start_ms = selectedRoute.get(0).getStartTime();
            long ms = start_ms;
            double prevAlt = 0;
            double prevLat = 0;
            double prevLon = 0;
            float[] results = new float[3]; // distance, initial bearing, final bearing
            double thisAlt = 0;
            double thisLat = 0;
            double thisLon = 0;
            long distance = 0;
            SimpleDateFormat formatter =  new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(ms));
            textViewData.setText("ROUTE: "+route_id+" at "+dateString);
            for (int i = 0; i < selectedRoute.size(); i++) {

                // DISTANCE: travelled 10 meters (5 m) <- cumulative distance, distance from previous point to current point

                // LEG: change 5 m, bearing 87 degrees <- distance from previous point to current point, direction of movement



                if (checkedToggleTime) {
                    ms = selectedRoute.get(i).getTimestamp();
                    formatter = new SimpleDateFormat("hh:mm:ss");
                    dateString = formatter.format(new Date(ms));
                    textViewData.append("\nTIME: " + dateString + ", elapsed (" + getElapsedTime(start_ms,ms) + ")");
                }

                if (i == 0) { // First point, distance is 0
                    // First one, distance is 0
                    prevLat = selectedRoute.get(i).getLatitude();
                    prevLon = selectedRoute.get(i).getLongitude();
                } else {
                    thisLat = selectedRoute.get(i).getLatitude();
                    thisLon = selectedRoute.get(i).getLongitude();
                    if (checkedToggleDistance) { // Cumulative distance
                        Location.distanceBetween(prevLat, prevLon, thisLat, thisLon, results);
                        distance += Math.round(results[0]);
                        textViewData.append("\nDISTANCE: travelled " + distance + " meters (" + Math.round(results[0]) + " m)");
                    }
                    if (checkedToggleLeg) {
                        Location.distanceBetween(prevLat, prevLon, thisLat, thisLon, results);
                        String bearing = "";
                        if (results[0] > 2) { // [1] initial bearing, [2] final bearing
                            bearing = ", bearing " + Math.round(((results[0] > 3) ? (results[2]) : (results[1]))) + "\u00B0";
                        }
                        textViewData.append("\nLEG: change " + Math.round(results[0]) + " meters" + bearing);
                    }
                    prevLat = thisLat;
                    prevLon = thisLon;
                }
                if (checkedToggleAltitude) {
                    double change = 0;
                    if (i == 0) {
                        // First one, distance is 0
                        prevAlt = selectedRoute.get(i).getAltitude();
                    } else {
                        thisAlt = selectedRoute.get(i).getAltitude();
                        change = prevAlt - thisAlt;
                        prevAlt = thisAlt;
                    }
                    String terrain = "";
                    if (change > 1) {
                        terrain = " uphill";
                    } else if (change < -1){
                        terrain = " downhill";
                    } else {
                        terrain = ", quite flat";
                    }
                    textViewData.append("\nALTITUDE: change "+Math.abs(change)+" meters" + terrain);
                }
                if (checkedToggleGps) {
                    textViewData.append("\nGPS: "
                            + Double.toString(selectedRoute.get(i).getLatitude()) + ", "
                            + Double.toString(selectedRoute.get(i).getLongitude()) + ", "
                            + Double.toString(selectedRoute.get(i).getAltitude()));
                }
                if (checkedToggleWifi) {
                    String ssid = selectedRoute.get(i).getSsid();
                    String rddi = selectedRoute.get(i).getRddi();
                    if (ssid != null && ssid.length() > 0) {
                        textViewData.append("\nSSID: " + selectedRoute.get(i).getSsid());
                    } else {
                        ssid = null;
                    }
                    if (rddi != null && rddi.length() > 0) {
                        textViewData.append("\nRDDI: " + selectedRoute.get(i).getRddi());
                    } else {
                        rddi = null;
                    }
                    if (ssid == null && rddi == null) {
                        textViewData.append("\nSSID/RDDI: none found");
                    }
                }
            }
        }
    }

    private String getElapsedTime(long start_ms, long current_ms) {
        // Log.d(TAG,"getElapsedTime " + (current_ms - start_ms) + " -> " + ((current_ms - start_ms) / 1000));
        long seconds = (current_ms - start_ms) / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds - (hours * 3600)) / 60;
        seconds = (seconds - (hours * 3600) - (minutes * 60));

        return String.format("%02d:%02d:%02d" ,hours, minutes, seconds);
    }
}
