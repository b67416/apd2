package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
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

public class KickTimesFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

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

        ArrayList<HashMap<String, String>> kicktimesArrayList = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> kicktimesHashMap = new HashMap<String, String>();
        kicktimesHashMap.put("num_of_kicks", "15");
        kicktimesHashMap.put("start_time", "1/23/14 @ 10:01 AM");
        kicktimesHashMap.put("stop_time", "1/23/14 @ 11:01 AM");
        kicktimesArrayList.add(kicktimesHashMap);

        kicktimesHashMap = new HashMap<String, String>();
        kicktimesHashMap.put("num_of_kicks", "9");
        kicktimesHashMap.put("start_time", "1/25/14 @ 9:45 PM");
        kicktimesHashMap.put("stop_time", "1/25/14 @ 10:45 PM");
        kicktimesArrayList.add(kicktimesHashMap);

        String[] mapFrom = { "num_of_kicks", "start_time", "stop_time" };
        int[] mapTo = { R.id.kick_times_textView_numberOfKicks, R.id.kick_times_textView_startTime, R.id.kick_times_textView_stopTime };

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), kicktimesArrayList, R.layout.fragment_kick_times_listview_item, mapFrom, mapTo);
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
        inflater.inflate(R.menu.fragment_kick_times, menu);
    }

    // When user clicks the Action Bar Item to add a new Blood Pressure Entry
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // FragmentManager fragmentManager = getFragmentManager();

      //  fragmentManager.beginTransaction()
       //         .replace(this.getId(), new KickTimesTrackerFragment())
       //         .commit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        KickTimesTrackerFragment kickTimesTrackerFragment = new KickTimesTrackerFragment();
        kickTimesTrackerFragment.show(getFragmentManager(), "New Kick Time Entry");

        return true;
    }

}
