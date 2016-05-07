package com.cyberwalkabout.cyberfit.widget;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseSession;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ExerciseHistoryRow extends FrameLayout {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final String DECIMAL_FORMAT = "%.2f";

    private DecimalFormat decimalFormat = new DecimalFormat(".##");

    private final TextView repetitionText;
    private final TextView timeOfDayText;
    private final TextView weightText;
    private final TextView distanceText;
    private final TextView timeText;
    private final TextView dateHeader;

    private final SimpleDateFormat headerDateFormant;
    private final String headerDateTodayText;

    private AppSettings appSettings;

    public ExerciseHistoryRow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.exercise_history_row, this);
        appSettings = new AppSettings(context);
        timeOfDayText = (TextView) findViewById(R.id.time_of_day);
        repetitionText = (TextView) findViewById(R.id.repetition);
        weightText = (TextView) findViewById(R.id.weight);
        distanceText = (TextView) findViewById(R.id.distance);
        timeText = (TextView) findViewById(R.id.time);
        dateHeader = (TextView) findViewById(R.id.date_header);
        headerDateFormant = new SimpleDateFormat(context.getString(appSettings.getDateFormat().getFormatResource()));
        headerDateTodayText = context.getString(R.string.exercise_history_header_text_today);
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void updateData(Exercise exercise, ExerciseSession exerciseSession) {
        initTimeOfDay(exerciseSession);
        initRepetitions(exercise, exerciseSession);
        initWeight(exercise, exerciseSession);
        initDistance(exercise, exerciseSession);
        initTime(exercise, exerciseSession);
    }

    public void showDateHeader(Date date) {
        dateHeader.setText(android.text.format.DateUtils.isToday(date.getTime()) ? headerDateTodayText : headerDateFormant.format(date.getTime()));
        dateHeader.setVisibility(View.VISIBLE);
    }

    public void hideDateHeader() {
        dateHeader.setVisibility(View.GONE);
    }

    private void initDistance(Exercise exercise, ExerciseSession exerciseSession) {
        if (exercise.isTrackDistance()) {
            initCell(distanceText, getContext().getString(R.string.exercise_history_record_distance), String.format(DECIMAL_FORMAT, exerciseSession.getDistance()));
            distanceText.setVisibility(VISIBLE);
        } else {
            distanceText.setVisibility(GONE);
        }
    }

    private void initWeight(Exercise exercise, ExerciseSession exerciseSession) {
        if (exercise.isTrackWeight()) {
            initCell(weightText, getContext().getString(R.string.exercise_history_record_weight), String.valueOf(String.format(DECIMAL_FORMAT, exerciseSession.getWeight()) + " " + getContext().getString(appSettings.getSystemOfMeasurement().getWeightUnitResource())));
            weightText.setVisibility(VISIBLE);
        } else {
            weightText.setVisibility(GONE);
        }
    }

    private void initTimeOfDay(ExerciseSession exerciseSession) {
        initCell(timeOfDayText, getContext().getString(R.string.exercise_history_record_time_of_day), dateFormat.format(new Date(exerciseSession.getTimestampCompleted())));
    }

    private void initRepetitions(Exercise exercise, ExerciseSession exerciseSession) {
        if (exercise.isTrackRepetitions()) {
            initCell(repetitionText, getContext().getString(R.string.exercise_history_record_reps), String.valueOf(exerciseSession.getRepetitions()));
            repetitionText.setVisibility(VISIBLE);
        } else {
            repetitionText.setVisibility(GONE);
        }
    }

    private void initTime(Exercise exercise, ExerciseSession exerciseSession) {
        if (exercise.isTrackWeight() && exercise.isTrackRepetitions()) {
            timeText.setVisibility(View.GONE);
        } else {
            initCell(timeText, getContext().getString(R.string.exercise_history_record_time), timeFormat.format(exerciseSession.getTime()));
            timeText.setVisibility(VISIBLE);
        }
    }

    private void initCell(TextView textView, String label, String value) {
        textView.setText("");
        textView.append(label);
        textView.append("\n");
        textView.append(Html.fromHtml("</br><b>" + value + "</b>"));
    }
}
