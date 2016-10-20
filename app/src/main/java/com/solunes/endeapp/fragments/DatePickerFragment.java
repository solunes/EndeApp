package com.solunes.endeapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";
    private DatePickerDialog dialog;
    private String dateString;
    private OnFinishDialog onFinishDialog;

    public DatePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        onFinishDialog = (OnFinishDialog) getActivity();
        dialog = new DatePickerDialog(getContext(),
                this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        String newMonth = String.valueOf(month), newDay = String.valueOf(day);
        if (month < 10) newMonth = "0" + month;
        if (day < 10) newDay = "0" + day;
        dateString = year + "-" + newMonth + "-" + newDay;
    }

    public interface OnFinishDialog {
        void onSelectedDate(String date);
    }
}