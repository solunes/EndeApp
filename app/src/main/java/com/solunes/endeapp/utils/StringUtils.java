package com.solunes.endeapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtils {
    private static final String TAG = "StringUtils";

    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_FORMAT_1 = "dd/MM/yyyy";
    public static String HUMAN_DATE_FORMAT = "dd, MMM yyyy";
    public static String HUMAN_HOUR_FORMAT = "HH:mm";

    public static String formateDateFromstring(String outputFormat, Date inputDate) {
        String outputDate = "";
        SimpleDateFormat dfOutput = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
        outputDate = dfOutput.format(inputDate);
        return outputDate;
    }

    public static String getHumanDate(Date inputDate) {
        return formateDateFromstring(HUMAN_DATE_FORMAT, inputDate);
    }

    public static String getHumanHour(Date inputDate) {
        return formateDateFromstring(HUMAN_HOUR_FORMAT, inputDate);
    }
}
