package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.HashMap;

public class BloodPressureFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private SQLiteDatabase preggoPrepDatabase = null;
    private ArrayList<HashMap<String, Integer>> bloodPressureArrayList = null;

    private RelativeLayout graphViewLayout = null;
    private GraphView graphView = null;

    private TextView dateTimeTextView = null;
    private TextView bloodPressureTextView = null;
    private Button deleteButton = null;

    private Integer currentEntryID = 0;

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

        // Setup the graph view
        graphViewLayout = (RelativeLayout) rootView.findViewById(R.id.blood_pressure_chartPlaceholder);
        graphView = new LineGraphView(getActivity(), "");

        graphView.getGraphViewStyle().setNumHorizontalLabels(1);
        graphView.getGraphViewStyle().setNumVerticalLabels(1);

        graphViewLayout.addView(graphView);

        // Setup some interface links
        dateTimeTextView = (TextView) rootView.findViewById(R.id.blood_pressure_textViewEntryDateTime);
        bloodPressureTextView = (TextView) rootView.findViewById(R.id.blood_pressure_textViewEntryBloodPressure);
        deleteButton = (Button) rootView.findViewById(R.id.blood_pressure_buttonDelete);

        // Setup the delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preggoPrepDatabase.execSQL("DELETE FROM blood_pressure WHERE _id = " + currentEntryID);
                getBloodPressureFromDB();
            }
        });


        return rootView;
    }

    // Get the blood pressure readings from the database
    private void getBloodPressureFromDB () {

        bloodPressureArrayList = new ArrayList<HashMap<String, Integer>>();
        ArrayList<GraphView.GraphViewData> systolicGraphViewDataArrayList = new ArrayList<GraphView.GraphViewData>();
        ArrayList<GraphView.GraphViewData> diastolicGraphViewDataArrayList = new ArrayList<GraphView.GraphViewData>();

        // Create an empty entry for the blood pressure readings
        HashMap<String, Integer> emptyBloodPressureHashMap = new HashMap<String, Integer>();
        emptyBloodPressureHashMap.put("_id", 0);
        emptyBloodPressureHashMap.put("systolic", 120);
        emptyBloodPressureHashMap.put("diastolic", 80);
        bloodPressureArrayList.add(emptyBloodPressureHashMap);

        systolicGraphViewDataArrayList.add(new GraphView.GraphViewData(0, 120));
        diastolicGraphViewDataArrayList.add(new GraphView.GraphViewData(0, 80));

        // Now get the rest from the database
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM blood_pressure", new String[0]);

        int index = 1;
        while (cursor.moveToNext()) {
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));
            Integer entrySystolic = cursor.getInt(cursor.getColumnIndex("systolic"));
            Integer entryDiastolic = cursor.getInt(cursor.getColumnIndex("diastolic"));

            HashMap<String, Integer> bloodPressureHashMap = new HashMap<String, Integer>();
            bloodPressureHashMap.put("_id", rowID);
            bloodPressureHashMap.put("systolic", entrySystolic);
            bloodPressureHashMap.put("diastolic", entryDiastolic);
            bloodPressureArrayList.add(bloodPressureHashMap);

            systolicGraphViewDataArrayList.add(new GraphView.GraphViewData(index, entrySystolic));
            diastolicGraphViewDataArrayList.add(new GraphView.GraphViewData(index, entryDiastolic));

            index = index + 1;
        }

        GraphViewSeries systolicGraphViewSeries = new GraphViewSeries(
                "Systolic",
                new GraphViewSeries.GraphViewSeriesStyle(Color.RED, 5),
                systolicGraphViewDataArrayList.toArray(new GraphView.GraphViewData[systolicGraphViewDataArrayList.size()]));


        GraphViewSeries diastolicGraphViewSeries = new GraphViewSeries(
                "Diastolic",
                new GraphViewSeries.GraphViewSeriesStyle(Color.BLUE, 5),
                diastolicGraphViewDataArrayList.toArray(new GraphView.GraphViewData[diastolicGraphViewDataArrayList.size()]));


        graphView.removeAllSeries();
        currentEntryID = 0;

        int sizeOfGraph = bloodPressureArrayList.size();

        // Set initial TextView text and disable remove button
        if (sizeOfGraph == 1) {
            deleteButton.setEnabled(false);
            dateTimeTextView.setText("");
            bloodPressureTextView.setText("Add an Entry");
        } else {
            deleteButton.setEnabled(false);
            dateTimeTextView.setText("");
            bloodPressureTextView.setText("Select an Entry");
        }

        //Log.v("SIZEOFGRAPH=", ""+sizeOfGraph);

        graphView.getGraphViewStyle().setNumVerticalLabels(sizeOfGraph);
        graphView.getGraphViewStyle().setNumHorizontalLabels(sizeOfGraph);

        graphView.setShowLegend(true);
        graphView.getGraphViewStyle().setLegendWidth(200);

        graphView.setViewPort(0, sizeOfGraph - 1);

        graphView.addSeries(systolicGraphViewSeries);
        graphView.addSeries(diastolicGraphViewSeries);

        graphViewLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    float x = event.getX(event.getActionIndex()); //the location of the touch on the graphview
                    double vp = graphView.getViewportSize(); //the boundries of what you are viewing from the function you just added
                    int width = graphView.getWidth(); //the width of the graphview
                    Double xValue = 0 + (x/width) * vp; //the x-Value of the graph where you touched

                    Log.v("touch:", "arraySize=" + bloodPressureArrayList.size() + " xValue=" + xValue.intValue());

                    if ( (xValue.intValue() > 0) && (xValue.intValue() <= bloodPressureArrayList.size()) ) {
                        HashMap<String, Integer> bloodPressureHashMap = bloodPressureArrayList.get(xValue.intValue());

                        currentEntryID = bloodPressureHashMap.get("_id");
                        if (currentEntryID != 0) {
                            Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM blood_pressure WHERE _id = " + currentEntryID, new String[0]);
                            cursor.moveToFirst();

                            String entryDate = cursor.getString(cursor.getColumnIndex("date"));
                            String entryTime = cursor.getString(cursor.getColumnIndex("time"));

                            Integer entrySystolic = cursor.getInt(cursor.getColumnIndex("systolic"));
                            Integer entryDiastolic = cursor.getInt(cursor.getColumnIndex("diastolic"));

                            Log.v("layout", "xValue: " + xValue.intValue() + "\tid: " + currentEntryID + "\tsys: " + entrySystolic);

                            dateTimeTextView.setText(entryDate + " " + entryTime);
                            bloodPressureTextView.setText(entrySystolic.toString() + " / " + entryDiastolic.toString());

                            deleteButton.setEnabled(true);
                        }
                    }
                }
                return true;
            }
        });

        if (sizeOfGraph == 1) {
            graphViewLayout.setOnTouchListener(null);
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

