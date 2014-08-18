package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BloodPressureFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static BloodPressureFragment newInstance(int sectionNumber) {
        BloodPressureFragment fragment = new BloodPressureFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public BloodPressureFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    // Inflate Action Bar Items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_blood_pressure, menu);
    }

    // When user clicks the Action Bar Item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Add the new entry logic

        Toast.makeText(getActivity().getApplicationContext(), "<TEMPORARY> Add new blood pressure recording", Toast.LENGTH_LONG).show();

        return true;
    }
}
