package com.ryanwahle.preggoprep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class KickTimesNewEntryDialog extends DialogFragment {

    private Integer numberOfKicks = 0;
    private String startTimeStampFromDBString = null;

    private Button buttonKicked = null;
    private TextView numberOfKicksTextView = null;
    private Chronometer elapsedTimeChronometer = null;

    private SQLiteDatabase preggoPrepDatabase = null;
    private Fragment loadingFragment = null;

    public KickTimesNewEntryDialog() { }

    @Override
    public Dialog  onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder newTrackerDialogBuilder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_kick_times_new_tracking, null);

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);

        // Get the current timestamp
        getCurrentTimeFromDB();

        // Links to user interface
        buttonKicked = (Button) rootView.findViewById(R.id.kick_times_tracker_button_kicked);
        numberOfKicksTextView = (TextView) rootView.findViewById(R.id.kick_times_tracker_textView_numberOfKicks);
        elapsedTimeChronometer = (Chronometer) rootView.findViewById(R.id.kick_times_tracker_chronometer_elapsedTime);

        // Setup the dialog
        newTrackerDialogBuilder.setView(rootView);
        newTrackerDialogBuilder.setTitle("New Kick Tracker");
        newTrackerDialogBuilder.setNegativeButton("Cancel Tracking", null);

        // Save entry to database when user saves.
        newTrackerDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preggoPrepDatabase.execSQL("INSERT INTO kick_times (start, stop, num_of_kicks) values ('" + startTimeStampFromDBString + "', CURRENT_TIMESTAMP, " + numberOfKicks + ")");
                Toast.makeText(getActivity(), "New Kick Time Entry Saved!", Toast.LENGTH_LONG).show();
            }
        });

        // Start the elapsed time
        elapsedTimeChronometer.start();

        // Logic for kick button
        buttonKicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfKicks = numberOfKicks + 1;
                updateNumberOfKicksUI();
            }
        });

        AlertDialog newTrackerDialog = newTrackerDialogBuilder.create();

        return newTrackerDialog;
    }

    private void updateNumberOfKicksUI() {
        numberOfKicksTextView.setText(numberOfKicks.toString());
    }

    private void getCurrentTimeFromDB() {
        // Get the SQL current timestamp so we can enter it when the user selects the stop button
        Cursor cursor = preggoPrepDatabase.rawQuery("SELECT CURRENT_TIMESTAMP as dbTimeStamp", new String[0]);
        cursor.moveToFirst();
        startTimeStampFromDBString = cursor.getString(cursor.getColumnIndex("dbTimeStamp"));
    }

    public void setLoadingFragment(Fragment fragment) {
        loadingFragment = fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        preggoPrepDatabase.close();
        loadingFragment.onResume();
    }
}
