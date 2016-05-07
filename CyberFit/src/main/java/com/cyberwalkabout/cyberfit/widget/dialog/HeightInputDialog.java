package com.cyberwalkabout.cyberfit.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maria Dzyokh
 */
public class HeightInputDialog extends NumberInputDialog {
    public static final String TAG = HeightInputDialog.class.getSimpleName();

    // TODO: this data can be in config
    private static float[] values3US = new float[]{0.0f, 0.25f, 0.5f, 0.75f};

    public interface OnHeightSetListener {
        void onHeightSet(View view, double height);
    }

    private OnHeightSetListener listener;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                AppSettings settings = new AppSettings(getActivity());
                AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

                double result; // height in cm or inches
                Config config = getConfig();
                String displayValue1 = config.getInput1DisplayValues()[view.getValue1()];
                String[] valueUnit1 = displayValue1.split(" ");
                int value1 = Integer.valueOf(valueUnit1[0]);

                String displayValue2 = config.getInput2DisplayValues()[view.getValue2()];
                String[] valueUnit2 = displayValue2.split(" ");
                int value2 = Integer.valueOf(valueUnit2[0]);

                if (som == AppSettings.SystemOfMeasurement.US) {
                    float value3 = values3US[view.getValue3()];
                    result = ConvertUtils.feetsToInches(value1) + value2 + value3;
                } else {
                    result = ConvertUtils.metersToCm(value1) + value2;
                }
                listener.onHeightSet(view, result);
            }
        }
        dismiss();
    }


    public static HeightInputDialog create(FragmentActivity activity, double value) {
        HeightInputDialog dialog = new HeightInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.height_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnHeightSetListener) {
            listener = (OnHeightSetListener) targetFragment;
        } else if (getActivity() instanceof OnHeightSetListener) {
            listener = (OnHeightSetListener) getActivity();
        }
    }

    public void setListener(OnHeightSetListener listener) {
        this.listener = listener;
    }

    private static Config createConfig(Context context, double value) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        int[] valueUnits = convertValueToUnits(value, settings.getSystemOfMeasurement());

        int min1 = 0;
        int max1 = 0;
        int min2 = 0;
        int max2 = 0;
        int default1 = 0;
        int default2 = 0;

        switch (som) {
            case METRIC:

                min1 = 0;
                max1 = 2;
                min2 = 0;
                max2 = 99;
                default1 = (valueUnits[0] > 0 ? valueUnits[0] : 1) - min1;
                default2 = (valueUnits[1] > 0 ? valueUnits[1] : 70) - min2;
                break;
            case US:

                min1 = 1;
                min2 = 0;
                max1 = 8;
                max2 = 11;
                default1 = (valueUnits[0] > 0 ? valueUnits[0] : 5) - min1;
                default2 = (valueUnits[1] > 0 ? valueUnits[1] : 1) - min2;
                break;
        }


        List<String> displayValuesList1 = new ArrayList<String>();
        for (int i = min1; i <= max1; i++) {
            displayValuesList1.add(String.valueOf(i) + " " + context.getString(som.getHeightPrimaryUnitResource()));
        }
        List<String> displayValuesList2 = new ArrayList<String>();
        for (int i = min2; i <= max2; i++) {
            String displayValueStr = String.valueOf(i);
            if (som == AppSettings.SystemOfMeasurement.METRIC) {
                displayValueStr += " " + context.getString(som.getHeightSecondaryUnitResource());
            }
            displayValuesList2.add(displayValueStr);
        }
        String[] displayValues1 = displayValuesList1.toArray(new String[displayValuesList1.size()]);
        String[] displayValues2 = displayValuesList2.toArray(new String[displayValuesList2.size()]);

        Config config = new Config().setInput1DisplayValues(displayValues1).setInput2DisplayValues(displayValues2).setInput1Value(default1).setInput2Value(default2);

        if (som == AppSettings.SystemOfMeasurement.US) {
            String[] displayValues3 = new String[]{"in", "1/4 in", "1/2 in", "3/4 in"};
            config.setInput3DisplayValues(displayValues3);
            config.setInput3Value(valueUnits[2]);
        } else {
            config.setInput3Visible(false);
        }
        return config;
    }

    private static int[] convertValueToUnits(double value, AppSettings.SystemOfMeasurement som) {
        int[] result = new int[3];
        if (som == AppSettings.SystemOfMeasurement.METRIC) {
            double meters = ConvertUtils.cmToMeters(value);
            result[0] = (int) meters;
            result[1] = (int) Math.round(ConvertUtils.metersToCm(meters - result[0]));
        } else {
            double feets = ConvertUtils.inchesToFeets(value);
            result[0] = (int) feets;
            double inches = ConvertUtils.feetsToInches(feets - result[0]);
            result[1] = (int) Math.round(inches);
            result[2] = getClosestValue3USPosition(inches - result[1]);
        }
        return result;
    }


    private static int getClosestValue3USPosition(double of) {
        double min = 1.0d;
        int closestPosition = 0;
        if (of > 0.0d) {
            for (int i = 0; i < values3US.length; i++) {
                final double diff = Math.abs(values3US[i] - of);

                if (diff < min) {
                    min = diff;
                    closestPosition = i;
                }
            }
        }
        return closestPosition;
    }
}
