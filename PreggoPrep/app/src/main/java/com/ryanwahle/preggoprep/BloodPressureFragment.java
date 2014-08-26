package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class BloodPressureFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private SQLiteDatabase preggoPrepDatabase = null;

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

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);
        preggoPrepDatabase.execSQL("CREATE TABLE IF NOT EXISTS blood_pressure (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, time TEXT, systolic INTEGER, diastolic INTEGER)");

        return rootView;
    }

    // Get the blood pressure readings from the database
    private void getBloodPressureFromDB () {
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM blood_pressure", new String[0]);

        while (cursor.moveToNext()) {
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));
            String entryDate = cursor.getString(cursor.getColumnIndex("date"));
            String entryTime = cursor.getString(cursor.getColumnIndex("time"));
            Integer entrySystolic = cursor.getInt(cursor.getColumnIndex("systolic"));
            Integer entryDiastolic = cursor.getInt(cursor.getColumnIndex("diastolic"));

            Log.v("Blood Pressure Entry Found", "ID: " + rowID + "\tDate: " + entryDate + "\tTime: " + entryTime + "\tSystolic: " + entrySystolic + "\tDiastolic: " + entryDiastolic);
        }
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

    // When user clicks the Action Bar Item to add a new Blood Pressure Entry
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BloodPressureNewEntryDialog bloodPressureNewEntryDialog = new BloodPressureNewEntryDialog();
        bloodPressureNewEntryDialog.setLoadingFragment(this);
        bloodPressureNewEntryDialog.setCancelable(false);
        bloodPressureNewEntryDialog.show(getFragmentManager(), "New Blood Pressure Entry");

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBloodPressureFromDB();
    }
}

