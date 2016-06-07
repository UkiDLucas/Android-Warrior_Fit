package com.cyberwalkabout.cyberfit.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseSession;
import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.util.ConvertUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Andrii Kovalov
 */
public class MapExerciseInfoSummaryViewAdapter {
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final String DISTANCE_FORMAT = "%.2f";
    public static final String ALTITUDE_FORMAT = "%.0f";
    public static final DecimalFormat SPEED_AND_PACE_FORMAT = new DecimalFormat("#0.0");

    private static final int ELEVATION = 16;
    public static final int ANIMATION_DURATION = 1500;

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    private final View root;

    private final TextView speedCurrent;
    private final TextView speedMax;
    private final TextView speedAvg;

    private final TextView paceCurrent;
    private final TextView paceMax;
    private final TextView paceAvg;

    private final TextView altitudeCurrent;
    private final TextView altitudeMax;
    private final TextView altitudeMin;

    private final TextView distanceCurrent;
    private final TextView timeCurrent;

    private final Context context;
    private final AppSettings appSettings;

    private double speed;
    private double pace;
    private ValueAnimator speedAnimator;
    private ValueAnimator paceAnimator;

    public MapExerciseInfoSummaryViewAdapter(View root) {
        this.root = root;
        context = root.getContext();
        appSettings = new AppSettings(context);

        ViewCompat.setElevation(root, ELEVATION);

        speedCurrent = (TextView) root.findViewById(R.id.speed_value);
        speedMax = (TextView) root.findViewById(R.id.speed_max);
        speedAvg = (TextView) root.findViewById(R.id.speed_avg);

        paceCurrent = (TextView) root.findViewById(R.id.pace_value);
        paceMax = (TextView) root.findViewById(R.id.pace_max);
        paceAvg = (TextView) root.findViewById(R.id.pace_avg);

        altitudeCurrent = (TextView) root.findViewById(R.id.altitude_value);
        altitudeMax = (TextView) root.findViewById(R.id.altitude_max);
        altitudeMin = (TextView) root.findViewById(R.id.altitude_min);

        distanceCurrent = (TextView) root.findViewById(R.id.distance);
        timeCurrent = (TextView) root.findViewById(R.id.time);
    }

    public void show(boolean animate) {
        if (animate) {
            ViewCompat.animate(root).alpha(1).setDuration(500).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    root.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();
        } else {
            root.setVisibility(View.VISIBLE);
        }
    }

    public void hide(boolean animate) {
        if (animate) {
            ViewCompat.animate(root).alpha(0).setDuration(500).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    root.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();
        } else {
            root.setVisibility(View.GONE);
        }
    }

    public void refresh(ExerciseSession exerciseSession) {
        Double avgSpeed = exerciseSession.getAvgSpeed();
        Double topSpeed = exerciseSession.getTopSpeed();
        Double minAltitude = exerciseSession.getLowestAltitude();
        Double topAltitude = exerciseSession.getTopAltitude();

        AppSettings.SystemOfMeasurement systemOfMeasurement = appSettings.getSystemOfMeasurement();

        setTime(exerciseSession.getTime());

        if (exerciseSession.getDistance() > 0) {
            setDistance(systemOfMeasurement == AppSettings.SystemOfMeasurement.METRIC ? exerciseSession.getDistance() : ConvertUtils.kmToMiles(exerciseSession.getDistance()));
        } else {
            setDistance(0);
        }

        if (avgSpeed != null && avgSpeed > 0 && avgSpeed < Double.POSITIVE_INFINITY) {
            setAvgSpeed(appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC ? avgSpeed : ConvertUtils.kmPerHourToMilesPerHour(avgSpeed));
            setAvgPace(ConvertUtils.speedToPace(systemOfMeasurement == AppSettings.SystemOfMeasurement.METRIC ? avgSpeed : ConvertUtils.kmPerHourToMilesPerHour(avgSpeed)));
        } else {
            setAvgSpeed(0);
            setAvgPace(0);
        }

        if (topSpeed != null && topSpeed > 0 && topSpeed < Double.POSITIVE_INFINITY) {
            setTopSpeed(appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC ? topSpeed : ConvertUtils.kmPerHourToMilesPerHour(topSpeed));
            setTopPace(ConvertUtils.speedToPace(systemOfMeasurement == AppSettings.SystemOfMeasurement.METRIC ? topSpeed : ConvertUtils.kmPerHourToMilesPerHour(topSpeed)));
        } else {
            setTopSpeed(0);
            setTopPace(0);
        }

        if (minAltitude != null) {
            setMinAltitude(systemOfMeasurement == AppSettings.SystemOfMeasurement.METRIC ? minAltitude : ConvertUtils.metersToFeets(minAltitude));
        }
        if (topAltitude != null) {
            setTopAltitude(systemOfMeasurement == AppSettings.SystemOfMeasurement.METRIC ? topAltitude : ConvertUtils.metersToFeets(topAltitude));
        }
    }

    public void refresh(LocationInfo locationInfo) {
        if (locationInfo != null) {
            if (locationInfo.getSpeed() != null) {
                double speed = locationInfo.getSpeed() > 0 ? locationInfo.getSpeed() : 0;
                if (speed > 0) {
                    speed = appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC ? speed : ConvertUtils.kmPerHourToMilesPerHour(speed);
                    setCurrentSpeed(speed);
                    setCurrentPace(ConvertUtils.speedToPace(speed));
                } else {
                    setCurrentPace(0);
                    setCurrentSpeed(0);
                }
            } else {
                setCurrentSpeedNotAvailable();
                setCurrentPaceNotAvailable();
            }

            if (locationInfo.getAltitude() != null) {
                double altitude = appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC ? locationInfo.getAltitude() : ConvertUtils.metersToFeets(locationInfo.getAltitude());
                setCurrentAltitude(altitude);
            } else {
                setCurrentAltitudeNotAvailable();
            }
        }
    }

    public void setTime(long time) {
        timeCurrent.setText(Html.fromHtml(String.format("%s: <strong>%s</strong>", context.getString(R.string.time), TIME_FORMAT.format(new Date(time)))));
    }

    // TODO: display meters/feets if less then 1 km/mile
    public void setDistance(double distance) {
        String prefix = context.getString(R.string.distance) + ": ";
        String value = String.format(DISTANCE_FORMAT, distance) + " ";

        String units = context.getString(appSettings.getSystemOfMeasurement().getDistanceUnitResource());

        SpannableString text = new SpannableString(prefix + value + units);
        text.setSpan(new RelativeSizeSpan(0.75f), prefix.length() + value.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), prefix.length(), text.length(), 0);

        distanceCurrent.setText(text);
    }

    public void setCurrentAltitude(double altitude) {
        String value = String.format(ALTITUDE_FORMAT, altitude) + " ";
        String units = context.getString(appSettings.getSystemOfMeasurement().getHeightPrimaryUnitResource());

        SpannableString text = new SpannableString(value + units);
        text.setSpan(new RelativeSizeSpan(0.75f), value.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), value.length(), text.length(), 0);

        altitudeCurrent.setText(text);
    }

    public void setCurrentAltitudeNotAvailable() {
        altitudeCurrent.setText(context.getString(R.string.abbr_not_available));
    }

    public void setCurrentAltitudeNotVisible() {
        altitudeCurrent.setVisibility(View.GONE);
    }

    public void setMinAltitude(double altitude) {
        String units = context.getString(appSettings.getSystemOfMeasurement().getHeightPrimaryUnitResource());
        altitudeMin.setText(String.format("%s %s %s", context.getString(R.string.text_low), String.format(ALTITUDE_FORMAT, altitude), units));
        altitudeMin.setVisibility(View.VISIBLE);
    }

    public void setTopAltitude(double altitude) {
        String unitStr = context.getString(appSettings.getSystemOfMeasurement().getHeightPrimaryUnitResource());
        altitudeMax.setText(String.format("%s %s %s", context.getString(R.string.text_top), String.format(ALTITUDE_FORMAT, altitude), unitStr));
        altitudeMax.setVisibility(View.VISIBLE);
    }

    public void setCurrentSpeed(final double speed) {
        if (this.speed == speed) {
            displayCurrentSpeed(speed);
        } else {
            if (speedAnimator != null) {
                speedAnimator.cancel();
            }
            speedAnimator = ValueAnimator.ofFloat((float) this.speed, (float) speed);
            speedAnimator.setDuration(ANIMATION_DURATION);
            speedAnimator.setInterpolator(new DecelerateInterpolator());
            speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float currentSpeed = (Float) animation.getAnimatedValue();
                    displayCurrentSpeed(currentSpeed);
                    MapExerciseInfoSummaryViewAdapter.this.speed = currentSpeed;
                }
            });
            speedAnimator.start();
        }
    }

    private void displayCurrentSpeed(double speed) {
        String value = SPEED_AND_PACE_FORMAT.format(speed) + " ";
        String units = context.getString(appSettings.getSystemOfMeasurement().getSpeedUnitResource());

        SpannableString text = new SpannableString(value + units);
        text.setSpan(new RelativeSizeSpan(0.75f), value.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), value.length(), text.length(), 0);

        speedCurrent.setText(text);
    }

    public void setCurrentSpeedNotAvailable() {
        speedCurrent.setText(context.getString(R.string.abbr_not_available));
    }

    public void setCurrentSpeedNotVisible() {
        speedCurrent.setVisibility(View.GONE);
    }

    public void setAvgSpeed(double speed) {
        String unitStr = context.getString(appSettings.getSystemOfMeasurement().getSpeedUnitResource());
        speedAvg.setText(String.format("%s %s %s", context.getString(R.string.text_avg), SPEED_AND_PACE_FORMAT.format(speed), unitStr));
        speedAvg.setVisibility(View.VISIBLE);
    }

    public void setTopSpeed(double speed) {
        String unitStr = context.getString(appSettings.getSystemOfMeasurement().getSpeedUnitResource());
        speedMax.setText(String.format("%s %s %s", context.getString(R.string.text_top), SPEED_AND_PACE_FORMAT.format(speed), unitStr));
        speedMax.setVisibility(View.VISIBLE);
    }

    public void setCurrentPace(final double pace) {
        if (this.pace == pace) {
            displayCurrentPace((float) pace);
        } else {
            if (paceAnimator != null) {
                paceAnimator.cancel();
            }

            paceAnimator = ValueAnimator.ofFloat((float) this.pace, (float) pace);
            paceAnimator.setDuration(ANIMATION_DURATION);
            paceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float currentPace = (Float) animation.getAnimatedValue();
                    displayCurrentPace(currentPace);
                    MapExerciseInfoSummaryViewAdapter.this.pace = currentPace;
                }
            });
            paceAnimator.start();
        }
    }

    private void displayCurrentPace(float pace) {
        String value = SPEED_AND_PACE_FORMAT.format(pace) + " ";
        String units = context.getString(appSettings.getSystemOfMeasurement().getPaceUnitResource());

        SpannableString text = new SpannableString(value + units);
        text.setSpan(new RelativeSizeSpan(0.75f), value.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), value.length(), text.length(), 0);
        paceCurrent.setText(text);
    }

    public void setCurrentPaceNotAvailable() {
        paceCurrent.setText(context.getString(R.string.abbr_not_available));
    }

    public void setCurrentPaceNotVisible() {
        paceCurrent.setVisibility(View.GONE);
    }

    public void setAvgPace(double pace) {
        String unitStr = context.getString(appSettings.getSystemOfMeasurement().getPaceUnitResource());
        paceAvg.setText(String.format("%s %s %s", context.getString(R.string.text_avg), SPEED_AND_PACE_FORMAT.format(pace), unitStr));
        paceAvg.setVisibility(View.VISIBLE);
    }

    public void setTopPace(double pace) {
        String unitStr = context.getString(appSettings.getSystemOfMeasurement().getPaceUnitResource());
        paceMax.setText(String.format("%s %s %s", context.getString(R.string.text_top), SPEED_AND_PACE_FORMAT.format(pace), unitStr));
        paceMax.setVisibility(View.VISIBLE);
    }
}
