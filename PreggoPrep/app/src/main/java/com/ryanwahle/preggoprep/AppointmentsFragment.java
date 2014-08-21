package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AppointmentsFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static AppointmentsFragment newInstance(int sectionNumber) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AppointmentsFragment() {        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);

        setHasOptionsMenu(true);

        ArrayList<HashMap<String, String>> appointmentsArrayList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> appointmentHashMap = new HashMap<String, String>();
        appointmentHashMap.put("date", "12/23/2014");
        appointmentHashMap.put("time", "5:00 AM");
        appointmentHashMap.put("doctor_name", "Dr. Johnstone");
        appointmentHashMap.put("location", "815 E Rose Lane, Phoenix, AZ 85020");
        appointmentsArrayList.add(appointmentHashMap);

        appointmentHashMap = new HashMap<String, String>();
        appointmentHashMap.put("date", "12/25/2014");
        appointmentHashMap.put("time", "9:00 AM");
        appointmentHashMap.put("doctor_name", "Dr. Christmas");
        appointmentHashMap.put("location", "1649 San Esteban Circle, Roseville, CA 95747");
        appointmentsArrayList.add(appointmentHashMap);

        String[] mapFrom = { "date", "time", "doctor_name", "location" };
        int[] mapTo = { R.id.appointments_textViewDate, R.id.appointments_textViewTime, R.id.appointments_textViewDoctorName, R.id.appointments_textViewLocation };

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), appointmentsArrayList, R.layout.fragment_appointments_listview_item, mapFrom, mapTo);

        setListAdapter(adapter);

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
        Log.v("appointmentsfragment", "on menu created");
        inflater.inflate(R.menu.fragment_appointments, menu);
    }

    // When user clicks the Action Bar Item to add a new Blood Pressure Entry
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AppointmentsNewEntryDialog appointmentsNewEntryDialog = new AppointmentsNewEntryDialog();
        appointmentsNewEntryDialog.show(getFragmentManager(), "New Appointment Entry");

        return true;
    }
}
