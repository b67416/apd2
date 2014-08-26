package com.ryanwahle.preggoprep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class BloodPressureNewEntryDialog extends DialogFragment {
    private AlertDialog newEntryDialog = null;
    private Fragment loadingFragment = null;

    private SQLiteDatabase preggoPrepDatabase = null;

    private NumberPicker systolicNumberPicker = null;
    private NumberPicker diastolicNumberPicker = null;
    private TextView entryDateTextView = null;
    private TextView entryTimeTextView = null;

    private int dateMonth = -1;
    private int dateDay = -1;
    private int dateYear = -1;

    private int timeHour = -1;
    private int timeMinute = -1;

    private boolean isDateSet = false;
    private boolean isTimeSet = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder newEntryDialogBuilder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_blood_pressure_new_entry, null);

        // Setup the SQLite Database
        preggoPrepDatabase = getActivity().openOrCreateDatabase("preggoprep", Context.MODE_PRIVATE, null);

        // Setup the dialog
        newEntryDialogBuilder.setView(rootView);
        newEntryDialogBuilder.setTitle("New Blood Pressure Entry");

        newEntryDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preggoPrepDatabase.execSQL("INSERT INTO blood_pressure (date, time, systolic, diastolic) VALUES ('" + entryDateTextView.getText() + "', '" + entryTimeTextView.getText() + "', " + systolicNumberPicker.getValue() + ", " + diastolicNumberPicker.getValue() + ")");
                Toast.makeText(getActivity(), "New Blood Pressure Entry Saved!", Toast.LENGTH_LONG).show();
                //Log.v("New BP Entry", "Date: " + entryDateTextView.getText() + "\tTime: " + entryTimeTextView.getText() + "\tSystolic: " + systolicNumberPicker.getValue() + "\tDiastolic: " + diastolicNumberPicker.getValue());
            }
        });

        newEntryDialogBuilder.setNegativeButton("Cancel", null);
        newEntryDialog = newEntryDialogBuilder.create();

        // Disable the OK button until both Date and Time have been selected
        newEntryDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                newEntryDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        // Setup the interface references
        systolicNumberPicker = (NumberPicker) rootView.findViewById(R.id.systolicNumberPicker);
        diastolicNumberPicker = (NumberPicker) rootView.findViewById(R.id.diastolicNumberPicker);
        entryDateTextView = (TextView) rootView.findViewById(R.id.textViewSetDate);
        entryTimeTextView = (TextView) rootView.findViewById(R.id.textViewSetTime);

        // Setup the number pickers for the blood pressure.
        systolicNumberPicker.setMinValue(0);
        systolicNumberPicker.setMaxValue(300);
        systolicNumberPicker.setValue(120);
        systolicNumberPicker.setWrapSelectorWheel(false);

        diastolicNumberPicker.setMinValue(0);
        diastolicNumberPicker.setMaxValue(300);
        diastolicNumberPicker.setValue(80);
        diastolicNumberPicker.setWrapSelectorWheel(false);

        // Setup the date and time pickers
        entryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        entryDateTextView.setText(year + "-" + (month + 1) + "-" + day);
                        isDateSet = true;
                        resetDialogPositiveButton();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        entryTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        entryTimeTextView.setText(hour + ":" + minute);
                        isTimeSet = true;
                        resetDialogPositiveButton();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });

        return newEntryDialog;
    }

    private void resetDialogPositiveButton () {
        if (isDateSet && isTimeSet) {
            newEntryDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        }
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
