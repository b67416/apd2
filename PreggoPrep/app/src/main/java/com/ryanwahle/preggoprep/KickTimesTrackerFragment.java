package com.ryanwahle.preggoprep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class KickTimesTrackerFragment extends DialogFragment {

    private Integer numberOfKicks = 0;

    private Button buttonKicked = null;
    private TextView numberOfKicksTextView = null;
    private Chronometer elapsedTimeChronometer = null;

    public KickTimesTrackerFragment() { }

    @Override
    public Dialog  onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder newTrackerDialogBuilder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_kick_times_new_tracking, null);

        // Links to user interface
        buttonKicked = (Button) rootView.findViewById(R.id.kick_times_tracker_button_kicked);
        numberOfKicksTextView = (TextView) rootView.findViewById(R.id.kick_times_tracker_textView_numberOfKicks);
        elapsedTimeChronometer = (Chronometer) rootView.findViewById(R.id.kick_times_tracker_chronometer_elapsedTime);

        newTrackerDialogBuilder.setView(rootView);
        newTrackerDialogBuilder.setTitle("New Kick Tracker");

        newTrackerDialogBuilder.setPositiveButton("Save", null);
        newTrackerDialogBuilder.setNegativeButton("Cancel Tracking", null);

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
}
