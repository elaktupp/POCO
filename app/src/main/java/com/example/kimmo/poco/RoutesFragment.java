package com.example.kimmo.poco;

import android.app.Activity;
import android.content.res.Configuration;
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
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnRoutesInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoutesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutesFragment extends Fragment {

    private static final String TAG = "POCODBG:Routes";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnRoutesInteractionListener mListener;

    private ArrayList<String> gpsCoords = new ArrayList<String>();;
    private WebView webViewRoute = null;
    private RoutesDataSource datasource = null;
    private List<Long> routes = null;
    private Spinner spinnerRoutes = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoutesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoutesFragment newInstance(String param1, String param2) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RoutesFragment() {
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
        View thisFragment = inflater.inflate(R.layout.fragment_routes, container, false);

        // WebView for the map image
        webViewRoute = (WebView) thisFragment.findViewById(R.id.webViewRoute);

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
                long route_id = Long.parseLong(parent.getItemAtPosition(position).toString());
                List<Route>selectedRoute = datasource.getRoute(route_id);
                if (selectedRoute.size() > 0) {
                    gpsCoords.clear();
                    for (int i = 0; i < selectedRoute.size(); i++) {
                        gpsCoords.add(Double.toString(selectedRoute.get(i).getLatitude())
                                + "," + Double.toString(selectedRoute.get(i).getLongitude()));
                    }
                }
                // Get the map from Google to WebView
                getMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "NOTHING SELECTED");
            }
        });

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

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRoutesInteractionListener) activity;
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
    public interface OnRoutesInteractionListener {
        // TODO: Update argument type and name
        public void onRoutesFragmentInteraction(int cmd, String param);
    }

    /*
     *
     */

    private void getMap() {
        /* BASE: https://maps.googleapis.com/maps/api/staticmap?parameters
         *
         * For example: https://maps.googleapis.com/maps/api/staticmap?
         * center=Brooklyn+Bridge,New+York,NY&zoom=13&size=300x300&
         * maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&
         * markers=color:green%7Clabel:G%7C40.711614,-74.012318&
         * markers=color:red%7Clabel:C%7C40.718217,-73.998284
         *
         *
         * center (required if markers not present) defines the center of the map, equidistant
         * from all edges of the map. This parameter takes a location as either a comma-separated
         * {latitude,longitude} pair (e.g. "40.714728,-73.998672") or a string address
         * (e.g. "city hall, new york, ny") identifying a unique location on the face of the earth.
         *
         * zoom (required if markers not present) defines the zoom level of the map, which
         * determines the magnification level of the map. This parameter takes a numerical value
         * corresponding to the zoom level of the region desired.
         *
         * size (required) defines the rectangular dimensions of the map image. This parameter takes
         * a string of the form {horizontal_value}x{vertical_value}. For example, 500x400 defines
         * a map 500 pixels wide by 400 pixels high. Maps smaller than 180 pixels in width will
         * display a reduced-size Google logo. This parameter is affected by the scale parameter,
         * described below; the final output size is the product of the size and scale values.
         *
         * markers (optional) define one or more markers to attach to the image at specified locations.
         * This parameter takes a single marker definition with parameters separated by the pipe
         * character (|). Multiple markers may be placed within the same markers parameter as long
         * as they exhibit the same style; you may add additional markers of differing styles by
         * adding additional markers parameters. Note that if you supply markers for a map, you do
         * not need to specify the (normally required) center and zoom parameters.
         *
         * For example:
         * Kiviharjunlenkki 1, Oulu: @65.0100389,25.5054301
         * Sateenkaari 5, Kempele: @64.913923,25.570026
         *
         */
        String mapSize = Integer.toString(getResources().getInteger(R.integer.map_width)) + "x" + Integer.toString(getResources().getInteger(R.integer.map_height ));
        String url = "https://maps.googleapis.com/maps/api/staticmap?&size="+mapSize+"&maptype=roadmap";
        String points = "&markers=size:tiny%7Ccolor:red"; // as Markers
        //String points = "&path=weight:3|color:red"; // as Path

        // Must limit the number of markers on map to keep the url length in check.
        //
        // Latitudes range from -90 to 90. Longitudes range from -180 to 180.
        // Both have X.12345678, eight digits.
        // "-90.12345678,-180.12345678" has maximum length of 26 characters
        // but we need to remember the pipe character "%7C", so lets say 29.
        // So according to that the maximum number of marker coordinates
        // is roughly 69 (2000/29).
        // Keeping that as a guideline we get url of 106 + 2001 characters.

        int amount = gpsCoords.size();
        Log.d(TAG, "amount "+amount);
        if (amount > 69) {
            // Must reduce the number of coordinates
            // But always keep first and last
            amount -= 2;
            int excess = amount - 67;
            // Always remove first and last of remainings
            amount -= 2;
            excess -= 2;
            double eraser = (double)excess / (double)amount;
            double drop = 0;

            // Weird reduction thingy, drops indexes that match integer value of eraser multiples

            int temp = 1;
            ArrayList<Boolean> toBeRemoved = new ArrayList<Boolean>();
            toBeRemoved.add(false);
            toBeRemoved.add(true);
            for (int i = 2; i < (gpsCoords.size() - 2); i++) {
                drop += eraser;
                if (temp < (int)Math.floor(drop)) { // temp is used to count when next new integer is reached
                    toBeRemoved.add(true);
                    temp++;
                } else {
                    toBeRemoved.add(false);
                }
            }
            toBeRemoved.add(true);
            toBeRemoved.add(false);

            for (int i = 0; i < toBeRemoved.size(); i++) {
                if (toBeRemoved.get(i)) {
                    //Log.d(TAG,"dropped "+i);
                } else {
                    points = points.concat("%7C");
                    points = points.concat(gpsCoords.get(i));
                }
            }

        } else {
            // All coordinates should fit into url just fine...
            for (int i = 0; i < gpsCoords.size(); i++) {
                points = points.concat("%7C");
                points = points.concat(gpsCoords.get(i));
            }
        }

        url = url.concat(points);
        webViewRoute.loadUrl(url);
    }
}
