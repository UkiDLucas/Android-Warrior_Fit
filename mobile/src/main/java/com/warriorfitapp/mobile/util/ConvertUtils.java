package com.warriorfitapp.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {
    // required for distanceToSpeed
    private static final float[] DISTANCE_HOLDER = new float[1];

    public static double lbsToKg(double lbs) {
        return lbs * 0.454f;
    }

    public static double kgToLbs(double kgs) {
        return kgs / 0.454f;
    }

    public static double kmToMiles(double km) {
        return km * 0.62137;
    }

    public static double metersToKm(double meters) {
        return meters / 1000;
    }

    public static double milesToKm(double miles) {
        return miles / 0.62137;
    }

    public static double inchToCm(double inches) {
        return inches * 2.54;
    }

    public static double inchToMeters(double inches) {
        return inches * 0.0254;
    }


    public static double cmToInch(double cm) {
        // return BigDecimal.valueOf(cm).divide(BigDecimal.valueOf(2.54), BigDecimal.ROUND_UP).doubleValue();
        // return cm * 0.393701;
        return cm / 2.54;
    }

    public static double cmToMeters(double cm) {
        return cm / 100;
    }

    public static double metersToCm(double meters) {
        return meters * 100;
    }

    public static double metersToFeets(double m) {
        return m * 3.2808;
    }

    public static double metersToInches(double m) {
        return m * 39.3701;
    }

    public static double inchesToFeets(double inches) {
        return inches / 12;
    }

    public static double feetsToInches(double feets) {
        return feets * 12;
    }

    public static double feetsToMeters(double ft) {
        return ft / 3.2808;
    }

    public static double kmPerHourToMilesPerHour(double kmPerHour) {
        return kmPerHour * 0.621371;
    }

    public static double metersPerSecToKmPerHour(double metersPerSecond) {
        return metersPerSecond * 3.6;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static long toMillis(int hourOfDay, int minute) {
        return (hourOfDay * 60 * 60 * 1000) + (minute * 60 * 1000);
    }

    public static String dateTimeToServerFormat(long dateTimeMillis) {
        long seconds = dateTimeMillis / 1000;
        long millis = dateTimeMillis % 1000;
        return seconds + "." + millis;
    }

    public static long dateTimeFromServerFormat(String serverDateTime) {
        String[] units = serverDateTime.split("\\.");
        return Long.parseLong(units[0]) * 1000 + Long.parseLong(units[1]);
    }

    public static long dateTimeFromServerFormatMicroSec(String serverDateTime) {
        long datetime = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        try {
            datetime = sdf.parse(serverDateTime).getTime();
        } catch (ParseException e) {
            Log.e(ConvertUtils.class.getSimpleName(), e.getMessage(), e);
        }
        return datetime;
    }

    /**
     * Converts km/h or mi/h to m/km or m/mi
     *
     * @param speed
     * @return
     */
    public static double speedToPace(double speed) {
        return 60 / speed;
    }

    public static long dateToUnixTimestamp(long dateMillis) {
        return dateMillis / 1000;
    }

    public static long dateToUnixTimestamp(Date date) {
        return date.getTime() / 1000;
    }

    public static long unixTimestampToMillis(long unixTimestamp) {
        return unixTimestamp * 1000;
    }

    public static double minuteKmToMinuteMile(double minuteKm) {
        return minuteKm * 0.621371192;
    }

    public static double distanceToSpeed(double latStart, double lngStart, long timestampStart, double latEnd, double lngEnd, long timestampEnd, float[] distanceHolder) {
        if (distanceHolder == null) {
            distanceHolder = DISTANCE_HOLDER;
        }
        Location.distanceBetween(latStart, lngStart, latEnd, lngEnd, distanceHolder);
        double distanceToPreviousLocation = distanceHolder[0]; //distance between current location and previous location in meters
        return ConvertUtils.metersPerSecToKmPerHour(distanceToPreviousLocation / ((timestampEnd - timestampStart) / 1000));
    }
}
