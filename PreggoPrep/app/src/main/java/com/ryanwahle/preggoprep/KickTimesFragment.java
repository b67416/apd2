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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class KickTimesFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SQLiteDatabase preggoPrepDatabase = null;

    public static KickTimesFragment newInstance(int sectionNumber) {
        KickTimesFragment fragment = new KickTimesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public KickTimesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kick_times, container, false);

        setHasOptionsMenu(true);

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);
        preggoPrepDatabase.execSQL("CREATE TABLE IF NOT EXISTS kick_times (_id INTEGER PRIMARY KEY AUTOINCREMENT, start TIMESTAMP, stop TIMESTAMP, num_of_kicks INTEGER)");

        return rootView;
    }

    private void getKickTimesFromDB () {
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM kick_times", new String[0]);

        ArrayList<HashMap<String, String>> kicktimesArrayList = new ArrayList<HashMap<String, String>>();

        while (cursor.moveToNext()) {
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));
            String startTimeStamp = cursor.getString(cursor.getColumnIndex("start"));
            String stopTimeStamp = cursor.getString(cursor.getColumnIndex("stop"));
            Integer numOfKicksInteger = cursor.getInt(cursor.getColumnIndex("num_of_kicks"));

            HashMap<String, String> kicktimeesHashMap = new HashMap<String, String>();
            kicktimeesHashMap.put("num_of_kicks", numOfKicksInteger.toString());
            kicktimeesHashMap.put("start_time", startTimeStamp);
            kicktimeesHashMap.put("stop_time", stopTimeStamp);

            kicktimesArrayList.add(kicktimeesHashMap);

            //Log.v("Kick Time Entry Found", "ID: " + rowID + "\tStart Timestamp: " + startTimeStamp + "\tStop Timestamp: " + stopTimeStamp + "\tKick Count: " + numOfKicksInteger);
        }

        String[] mapFrom = { "num_of_kicks", "start_time", "stop_time" };
        int[] mapTo = { R.id.kick_times_textView_numberOfKicks, R.id.kick_times_textView_startTime, R.id.kick_times_textView_stopTime };

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), kicktimesArrayList, R.layout.fragment_kick_times_listview_item, mapFrom, mapTo);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        preggoPrepDatabase.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        getKickTimesFromDB();
    }

    // Inflate Action Bar Items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_kick_times, menu);
    }

    // When user clicks the Action Bar Item to add a new Blood Pressure Entry
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        KickTimesNewEntryDialog kickTimesNewEntryDialog = new KickTimesNewEntryDialog();
        kickTimesNewEntryDialog.setLoadingFragment(this);
        kickTimesNewEntryDialog.setCancelable(false);
        kickTimesNewEntryDialog.show(getFragmentManager(), "New Kick Time Entry");

        return true;
    }
}
