package com.solunes.endeapp.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "DatePickerFragment";
    private TimePickerDialog dialog;
    private OnFinishDialog onFinishDialog;

    public TimePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        onFinishDialog = (OnFinishDialog) getActivity();
        dialog = new TimePickerDialog(getContext(), this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Log.e(TAG, "onTimeSet: " + hour + " - " + minute);
    }

    public interface OnFinishDialog {
        void onSelectedTime(String time);
    }
}