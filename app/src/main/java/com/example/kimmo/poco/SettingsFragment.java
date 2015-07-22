package com.example.kimmo.poco;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnSettingsFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "POCODBG:Settings";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_INTERVAL = "ParamInterval";

    // TODO: Rename and change types of parameters
    private String mInterval = "30";
    private TextView textViewUnit = null;
    private TextView textViewInterval = null;
    private EditText editTextInterval = null;
    private Button btnIncrement = null;
    private Button btnDecrement = null;
    private Button btnAccept = null;
    private Button btnCancel = null;
    private Button btnSetInterval = null;
    private Button btnResetInterval = null;
    private RelativeLayout frameEditText = null;
    private Button btnTestGps = null;
    private Button btnTestWifi = null;
    private TextView textViewTestOutput = null;

    private OnSettingsFragmentInteraction mListener;

    private View thisFragment = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTERVAL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int param = getArguments().getInt(ARG_INTERVAL);
            if (param >= 0 && param < 1000) {
                mInterval = Integer.toString(param);
            } else {
                Log.e(TAG,"Bad ARG_INTERVAL "+param);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragment = inflater.inflate(R.layout.fragment_settings, container, false);

        frameEditText = (RelativeLayout) thisFragment.findViewById(R.id.frameEditText);

        btnSetInterval = (Button) thisFragment.findViewById(R.id.btnSetInterval);
        btnSetInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetInterval(v);
            }
        });
        textViewInterval = (TextView) thisFragment.findViewById(R.id.textViewInterval);
        textViewInterval.setText(mInterval);
        editTextInterval = (EditText) thisFragment.findViewById(R.id.editTextInterval);
        editTextInterval.setText(mInterval);
        btnIncrement = (Button) thisFragment.findViewById(R.id.btnIncrement);
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementInterval(v);
            }
        });
        btnDecrement = (Button) thisFragment.findViewById(R.id.btnDecrement);
        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementInterval(v);
            }
        });
        btnAccept = (Button) thisFragment.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptSetInterval(v);
            }
        });
        btnCancel = (Button) thisFragment.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSetInterval(v);
            }
        });
        btnResetInterval = (Button) thisFragment.findViewById(R.id.btnResetInterval);
        btnResetInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInterval(v);
            }
        });
        btnTestGps = (Button) thisFragment.findViewById(R.id.btnTestGps); // onClicks Activity
        btnTestWifi = (Button) thisFragment.findViewById(R.id.btnTestWifi); // onClicks Activity
        textViewTestOutput = (TextView) thisFragment.findViewById(R.id.textViewTestOutput);

        return thisFragment;
    }

    // THIS IS A TRICK TO GET FRAGMENT TO CHANGE ITS LAYOUT ACCORDING TO THE ORIENTATION
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Get old stuff
        TextView tv = (TextView) getView().findViewById(R.id.textViewTestOutput);
        String text = tv.getText().toString();

        /* Re-create view... */
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViewsInLayout();
        View view = onCreateView(getActivity().getLayoutInflater(), viewGroup, null); viewGroup.addView(view);

        // Update tv to new view and add old stuff back
        tv = (TextView) getView().findViewById(R.id.textViewTestOutput);
        tv.setText(text);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSettingsFragmentInteraction) activity;
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
    public interface OnSettingsFragmentInteraction {

        public void onSettingsFragmentInteraction(int cmd, String param);

    }

    /*
     * INTERVAL SETTING & SOFT INPUT METHODS
     *
     */

    // NOTE: Basically could do without mInterval and just use textViewInterval as storage.
    //
    // TODO: The set interval is lost when this Fragment is closed. Having it in Activity will eventually lose it too - unless stored to DB or such.

    private void updateInterval(String interval) {

        Log.d(TAG, "updateInterval " + interval);

        // Assumes valid interval
        mInterval = interval;
        textViewInterval.setText(mInterval);
        editTextInterval.setText(mInterval);
        // Inform Activity about change
        mListener.onSettingsFragmentInteraction(R.integer.cmd_update_interval, mInterval);
    }

    private void updateInterval(int gpsInterval) {

        Log.d(TAG, "updateInterval (validate) " + gpsInterval);

        // Assumes un-verified interval
        if (gpsInterval < 0) {
            resetInterval(null); // this is just plain wrong, reset to default
        } else {
            if (gpsInterval > 999) {
                gpsInterval = 999; // since we don't have any limit in EditText case, just use max.
            }
            mInterval = Integer.toString(gpsInterval);
            textViewInterval.setText(mInterval);
            editTextInterval.setText(mInterval);
            // Inform Activity about change
            mListener.onSettingsFragmentInteraction(R.integer.cmd_update_interval, mInterval);
        }
    }

    public void incrementInterval(View view) {
        int gpsInterval = Integer.parseInt(mInterval);
        gpsInterval++;
        updateInterval(gpsInterval);
    }

    public void decrementInterval(View view) {
        int gpsInterval = Integer.parseInt(mInterval);
        gpsInterval--;
        updateInterval(gpsInterval);
    }

    public void resetInterval(View view) {

        Log.d(TAG, "resetInterval to default");

        updateInterval(getResources().getInteger(R.integer.interval_default));
    }

    public void showSetInterval(View view) {

        Log.d(TAG, "showSetInterval " + mInterval);

        updateInterval(mInterval);
        frameEditText.setVisibility(View.VISIBLE);
    }

    private void acceptSetInterval(View view) {
        int gpsInterval = -1;
        try {
            gpsInterval = Integer.parseInt(editTextInterval.getText().toString());
        } catch (NumberFormatException e) {
            // will be reset since its still -1
        }

        Log.d(TAG, "acceptSetInterval " + gpsInterval);

        updateInterval(gpsInterval); // contains validity check & reset

        hideKeyboard();
        frameEditText.setVisibility(View.GONE);
    }

    private void hideSetInterval(View view) {

        Log.d(TAG, "hideSetInterval");

        hideKeyboard();
        frameEditText.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();

        Log.d(TAG, "hideKeyboard " + view);

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Activity can update text view

    public void appendText(String text) {
        textViewTestOutput.append(text);
    }

    public void setText(String text) {
        textViewTestOutput.setText(text);
    }
}
