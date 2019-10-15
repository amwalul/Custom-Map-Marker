package com.amwa.custommapmarker.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            String loc;
            if (obj.getSubLocality() != null) {
                loc = obj.getSubLocality();
            } else if (obj.getLocality() != null) {
                loc = obj.getLocality();
            } else if (obj.getThoroughfare() != null) {
                loc = obj.getThoroughfare();
            } else {
                loc = lat + "," + lng;
            }

            return loc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(String dateTime) {
        String formattedDate;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(dateTime);

            dateFormat.applyPattern("E, dd MMM yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            formattedDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return formattedDate;
    }

    public static String formatTime(String dateTime) {
        String formattedTime;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(dateTime);

            dateFormat.applyPattern("K:mm a");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            formattedTime = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return formattedTime;
    }

    public static String meterToKilometer(int distance, boolean withUnit) {
        int result = distance / 1000;
        String unit = "km";

        if (result == 0) {
            result = distance;
            unit = "m";
        }

        if (withUnit) {
            return result + " " + unit;
        } else {
            return String.valueOf(result);
        }
    }

    public static String secondToMinute(int time, boolean withUnit) {
        int result = time / 60;
        String unit = "mins";

        if (result == 0) {
            result = time;
            unit = "s";
        }

        if (withUnit) {
            return result + " " + unit;
        } else {
            return String.valueOf(result);
        }
    }
}
