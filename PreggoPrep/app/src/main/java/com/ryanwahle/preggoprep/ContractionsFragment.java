package com.ryanwahle.preggoprep;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;

public class ContractionsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SQLiteDatabase preggoPrepDatabase = null;
    private String contractionStartTimeStampFromDBString = null;

    private RelativeLayout graphViewLayout = null;
    private GraphView graphView = null;

    public static ContractionsFragment newInstance(int sectionNumber) {
        ContractionsFragment fragment = new ContractionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ContractionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractions, container, false);

        // Setup both buttons onClick
        final Button contractionStartButton = (Button) rootView.findViewById(R.id.buttonContractionStart);
        final Button contractionFinishButton = (Button) rootView.findViewById(R.id.buttonContractionFinish);

        contractionStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked Start button, so disable it and enable the stop button
                contractionStartButton.setEnabled(false);
                contractionFinishButton.setEnabled(true);

                // Get the SQL current timestamp so we can enter it when the user selects the stop button
                Cursor cursor = preggoPrepDatabase.rawQuery("SELECT CURRENT_TIMESTAMP as dbTimeStamp", new String[0]);
                cursor.moveToFirst();
                contractionStartTimeStampFromDBString = cursor.getString(cursor.getColumnIndex("dbTimeStamp"));
            }
        });

        contractionFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked Stop button, so disable it and enable the start button
                contractionStartButton.setEnabled(true);
                contractionFinishButton.setEnabled(false);

                // Enter a new entry into the database for the contraction
                preggoPrepDatabase.execSQL("INSERT INTO contractions (start, stop) values ('" + contractionStartTimeStampFromDBString + "', CURRENT_TIMESTAMP)");
                Toast.makeText(getActivity(), "New Contraction Saved!", Toast.LENGTH_LONG).show();

                contractionStartTimeStampFromDBString = null;

                getContractionsFromDB();
            }
        });

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);
        preggoPrepDatabase.execSQL("CREATE TABLE IF NOT EXISTS contractions (_id INTEGER PRIMARY KEY AUTOINCREMENT, start TIMESTAMP, stop TIMESTAMP)");

        getContractionsFromDB();

        // Setup the graph view
        graphViewLayout = (RelativeLayout) rootView.findViewById(R.id.contractions_chartPlaceholder);
        graphView = new LineGraphView(getActivity(), "");

        graphView.getGraphViewStyle().setNumHorizontalLabels(1);
        graphView.getGraphViewStyle().setNumVerticalLabels(1);

        graphViewLayout.addView(graphView);


        return rootView;
    }

    private void getContractionsFromDB () {
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT * FROM contractions", new String[0]);

        while (cursor.moveToNext()) {
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));
            String startContractionTimeStamp = cursor.getString(cursor.getColumnIndex("start"));
            String stopContractionTimeStamp = cursor.getString(cursor.getColumnIndex("stop"));

            Log.v("Contraction Entry Found", "ID: " + rowID + "\tStart Timestamp: " + startContractionTimeStamp + "\tStop Timestamp: " + stopContractionTimeStamp);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDestroyView() {
        preggoPrepDatabase.close();
        super.onDestroyView();
    }

}
