package com.cyberwalkabout.cyberfit.util;

import android.content.Context;

import com.cyberwalkabout.cyberfit.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final TimeZone utc = TimeZone.getTimeZone("UTC");

    public static int javaDayOfWeekToJoda(int dayOfWeek) {
        dayOfWeek += 1;
        if (dayOfWeek < DateTimeConstants.MONDAY) {
            dayOfWeek = DateTimeConstants.SUNDAY;
        }
        return dayOfWeek;
    }

    public static int jodaDayOfWeekToJava(int dayOfWeek) {
        dayOfWeek += 1;
        if (dayOfWeek > Calendar.SATURDAY) {
            dayOfWeek = Calendar.SUNDAY;
        }
        return dayOfWeek;
    }

    public static LocalDate dateTimeWithDayOfWeek(Date time, int dayOfWeek) {
        DateTime dateTime = new DateTime(time);
        int scheduledDayOfWeek = DateUtils.javaDayOfWeekToJoda(dayOfWeek);
        return dateTime.withDayOfWeek(scheduledDayOfWeek).toLocalDate();
    }

    public static LocalDate getNearestDateForDayOfWeekAndTime(Date time, int dayOfWeek) {
        DateTime dateTime = new DateTime(time);
        DateTime now = DateTime.now();
        final int scheduledDayOfWeek = DateUtils.javaDayOfWeekToJoda(dayOfWeek);
        if (now.getDayOfWeek() > scheduledDayOfWeek) {
            now = now.plusWeeks(1).withDayOfWeek(scheduledDayOfWeek);
        } else if (now.getDayOfWeek() < scheduledDayOfWeek) {
            now = now.withDayOfWeek(scheduledDayOfWeek);
        } else if (now.getDayOfWeek() == scheduledDayOfWeek) {
            if (now.getHourOfDay() <= dateTime.getHourOfDay() && now.getMinuteOfHour() <= dateTime.getMinuteOfHour()) {
                now = now.withDayOfWeek(scheduledDayOfWeek);
            } else {
                now = now.plusWeeks(1).withDayOfWeek(scheduledDayOfWeek);
            }
        }
        return now.toLocalDate();
    }

    public static DateTime getDateTime(Date date, Date time) {
        LocalDate localDate = LocalDate.fromDateFields(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        DateTime dateTime = new DateTime();
        dateTime = dateTime.withDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
        dateTime = dateTime.withTime(timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), 0, 0);

        return dateTime;
    }

    public static int getHourOfDay(Date date) {
        return getHourOfDay(date, TimeZone.getDefault());
    }

    public static int getHourOfDay(Date date, TimeZone timeZone) {
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.setTimeZone(timeZone);
        calendar1.clear();
        calendar1.setTime(date);
        return calendar1.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteOfHour(Date date) {
        return getMinuteOfHour(date, TimeZone.getDefault());
    }

    public static int getMinuteOfHour(Date date, TimeZone timeZone) {
        Calendar calendar1 = Calendar.getInstance(timeZone);
        calendar1.setTimeZone(timeZone);
        calendar1.clear();
        calendar1.setTime(date);
        return calendar1.get(Calendar.MINUTE);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static String formatTime(long time) {
        long minutes = time / (60 * 1000);
        long seconds = (time / 1000) % 60;

        String timeFormatted = String.format("%d:%02d", minutes, seconds);
        return timeFormatted;
    }

    public static String prettyTime(Context context, long time) {
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(hours));

        int seconds = (int) ((time / 1000) % 60);

        StringBuilder timeStringBuilder = new StringBuilder();
        if (hours > 0) {
            timeStringBuilder.append(hours).append(" ").append(context.getString(R.string.hr)).append(".");
        }
        if (minutes > 0) {
            if (timeStringBuilder.length() > 0) {
                timeStringBuilder.append(" ");
            }
            timeStringBuilder.append(minutes).append(" ").append(context.getString(R.string.min)).append(".");
        }
        if (seconds > 0) {
            if (timeStringBuilder.length() > 0) {
                timeStringBuilder.append(" ");
            }
            timeStringBuilder.append(seconds).append(" ").append(context.getString(R.string.sec)).append(".");
        }

        return timeStringBuilder.toString();
    }

}
