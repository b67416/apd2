package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AppointmentsFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SQLiteDatabase preggoPrepDatabase = null;
    private ArrayList<HashMap<String, String>> appointmentsArrayList = null;

    public static AppointmentsFragment newInstance(int sectionNumber) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AppointmentsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);

        setHasOptionsMenu(true);

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);
        preggoPrepDatabase.execSQL("CREATE TABLE IF NOT EXISTS appointments (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, time TEXT, name TEXT, location TEXT)");

        return rootView;
    }

    private void getAppointmentsFromDB () {
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM appointments", new String[0]);

        appointmentsArrayList = new ArrayList<HashMap<String, String>>();

        while (cursor.moveToNext()) {
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));
            String entryDate = cursor.getString(cursor.getColumnIndex("date"));
            String entryTime = cursor.getString(cursor.getColumnIndex("time"));
            String entryName = cursor.getString(cursor.getColumnIndex("name"));
            String entryLocation = cursor.getString(cursor.getColumnIndex("location"));

            HashMap<String, String> appointmentHashMap = new HashMap<String, String>();
            appointmentHashMap.put("_id", rowID.toString());
            appointmentHashMap.put("date", entryDate);
            appointmentHashMap.put("time", entryTime);
            appointmentHashMap.put("name", entryName);
            appointmentHashMap.put("location", entryLocation);

            appointmentsArrayList.add(appointmentHashMap);

            //Log.v("Kick Time Entry Found", "ID: " + rowID + "\tStart Timestamp: " + startTimeStamp + "\tStop Timestamp: " + stopTimeStamp + "\tKick Count: " + numOfKicksInteger);
        }

        String[] mapFrom = { "date", "time", "name", "location" };
        int[] mapTo = { R.id.appointments_textViewDate, R.id.appointments_textViewTime, R.id.appointments_textViewDoctorName, R.id.appointments_textViewLocation };

        final SimpleAdapter adapter = new SimpleAdapter(getActivity(), appointmentsArrayList, R.layout.fragment_appointments_listview_item, mapFrom, mapTo);
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int position = pos;
                final HashMap<String, String> appointmentHashMap = appointmentsArrayList.get(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Delete Appointment");
                alertDialog.setMessage("Are you sure you want delete this appointment?");
                alertDialog.setNegativeButton("No", null);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        preggoPrepDatabase.execSQL("DELETE FROM appointments WHERE _id = " + appointmentHashMap.get("_id"));
                        appointmentsArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                alertDialog.show();

                return true;
            }
        });


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        getAppointmentsFromDB();
    }

    // Inflate Action Bar Items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_appointments, menu);
    }

    // When user clicks the Action Bar Item to add a new Blood Pressure Entry
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppointmentsNewEntryDialog appointmentsNewEntryDialog = new AppointmentsNewEntryDialog();
        appointmentsNewEntryDialog.setLoadingFragment(this);
        appointmentsNewEntryDialog.setCancelable(false);
        appointmentsNewEntryDialog.show(getFragmentManager(), "New Appointment Entry");

        return true;
    }
}
