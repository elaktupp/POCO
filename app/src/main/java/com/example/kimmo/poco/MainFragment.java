package com.example.kimmo.poco;

import android.app.Activity;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnMainFragmentListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment { //implements View.OnClickListener {

    public static final String TAG = "POCODBG:Main";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INTERVAL = "ParamInterval";

    private MainFragment self = null;
    private int mInterval = 0;
    private boolean isActive = false;

    private OnMainFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(int param) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INTERVAL, param);
        fragment.setArguments(args);

        Log.d(TAG,"newInstance "+param);

        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInterval = getArguments().getInt(ARG_INTERVAL);
        }

        Log.d(TAG,"onCreate "+this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView " + this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // THIS IS A TRICK TO GET FRAGMENT TO CHANGE ITS LAYOUT ACCORDING TO THE ORIENTATION
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Get old stuff
        TextView tv = (TextView) getView().findViewById(R.id.textViewMain);
        String text = tv.getText().toString();

        /* Re-create view... */
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViewsInLayout();
        View view = onCreateView(getActivity().getLayoutInflater(), viewGroup, null);
        viewGroup.addView(view);

        // Update tv to new view and add old stuff back
        tv = (TextView) getView().findViewById(R.id.textViewMain);
        tv.setText(text);

        pocoActive(); // set UI according to isActive
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentListener) activity;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView "+this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy "+this);
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
    public interface OnMainFragmentListener {
        public void onMainFragmentInteraction(int cmd, String param);
    }

    public void pocoActive(boolean active) {
        isActive = active;
        pocoActive();
    }

    private void pocoActive() {
        ImageButton btnData = (ImageButton) getActivity().findViewById(R.id.btnData);
        ImageButton btnStart = (ImageButton) getActivity().findViewById(R.id.btnStart);
        ImageButton btnSettings = (ImageButton) getActivity().findViewById(R.id.btnSettings);
        ImageButton btnRoutes = (ImageButton) getActivity().findViewById(R.id.btnRoutes);
        if (isActive) {
            btnData.setEnabled(false);
            btnSettings.setEnabled(false);
            btnRoutes.setEnabled(false);
            btnStart.setImageResource(R.drawable.btn_stop);
        } else {
            btnData.setEnabled(true);
            btnSettings.setEnabled(true);
            btnRoutes.setEnabled(true);
            btnStart.setImageResource(R.drawable.btn_start);
        }
    }

    public void appendText(String text) {
        TextView textView = (TextView) getActivity().findViewById(R.id.textViewMain);
        textView.append(text);
    }

    public void setText(String text) {
        TextView textView = (TextView) getActivity().findViewById(R.id.textViewMain);
        textView.setText(text);
    }
}
