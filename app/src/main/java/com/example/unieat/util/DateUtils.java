package com.example.unieat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
        return sdf.format(date);
    }
}