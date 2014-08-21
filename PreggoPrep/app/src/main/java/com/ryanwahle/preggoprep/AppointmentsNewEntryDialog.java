package com.ryanwahle.preggoprep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AppointmentsNewEntryDialog extends DialogFragment {
    private AlertDialog newEntryDialog = null;

    private TextView entryDateTextView = null;
    private TextView entryTimeTextView = null;
    private TextView entryDoctorTextView = null;
    private TextView entryLocationTextView = null;

    private boolean isDateSet = false;
    private boolean isTimeSet = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder newEntryDialogBuilder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_appointments_new_entry, null);

        // Setup the interface references
        entryDateTextView = (TextView) rootView.findViewById(R.id.appointments_newEntry_textViewSetDate);
        entryTimeTextView = (TextView) rootView.findViewById(R.id.appointments_newEntry_textViewSetTime);
        entryDoctorTextView = (TextView) rootView.findViewById(R.id.appointments_newEntry_textViewDoctor);
        entryLocationTextView = (TextView) rootView.findViewById(R.id.appointments_newEntry_textViewLocation);

        // Setup the dialog
        newEntryDialogBuilder.setView(rootView);
        newEntryDialogBuilder.setTitle("New Appointment");

        newEntryDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v("New BP Entry", "Date: " + entryDateTextView.getText() + "\tTime: " + entryTimeTextView.getText() + "\tDoctor: " + entryDoctorTextView.getText() + "\tAddress: " + entryLocationTextView.getText());
            }
        });

        newEntryDialogBuilder.setNegativeButton("Cancel", null);
        newEntryDialog = newEntryDialogBuilder.create();

        // Disable the OK button until Date, Time, Doctor, and Location have been selected/entered
        newEntryDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                newEntryDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        // Setup the date and time pickers
        entryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        entryDateTextView.setText((month + 1) + "/" + day + "/" + year);
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

        // Continuously monitor the text in the Doctor and Location to see if there is
        // a value so that we can enable the OK button
        entryDoctorTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                resetDialogPositiveButton();
            }
        });

        entryLocationTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                resetDialogPositiveButton();
            }


        });

        return newEntryDialog;
    }

    private void resetDialogPositiveButton () {
        if (isDateSet && isTimeSet && !entryDoctorTextView.getText().toString().isEmpty() && !entryLocationTextView.getText().toString().isEmpty()) {
            newEntryDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }
}
