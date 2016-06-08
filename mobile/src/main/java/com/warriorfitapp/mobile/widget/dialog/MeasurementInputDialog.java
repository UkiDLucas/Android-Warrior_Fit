package com.warriorfitapp.mobile.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.warriorfitapp.mobile.AppSettings;
import com.warriorfitapp.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maria Dzyokh
 */
public class MeasurementInputDialog extends NumberInputDialog {

    private static float[] values2US = new float[]{0.0f, 0.25f, 0.5f, 0.75f};

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (mCallback != null) {
                AppSettings settings = new AppSettings(getActivity());
                AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();
                float result;
                String displayValue1 = getConfig().getInput1DisplayValues()[view.getValue1()];
                if (som == AppSettings.SystemOfMeasurement.METRIC) {
                    String[] valueUnit1 = displayValue1.split(" ");
                    result = Float.valueOf(valueUnit1[0]);
                } else {
                    result = Float.valueOf(displayValue1);
                    result += values2US[view.getValue2()];
                }
                mCallback.onMeasurementSet(view, result);
            }
        }
        dismiss();
    }

    public interface OnMeasurementSetListener {
        void onMeasurementSet(View view, float value);
    }

    private OnMeasurementSetListener mCallback;

    public static MeasurementInputDialog create(FragmentActivity activity, double value) {
        MeasurementInputDialog dialog = new MeasurementInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.measurement_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnMeasurementSetListener) {
            mCallback = (OnMeasurementSetListener) targetFragment;
        } else if (getActivity() instanceof OnMeasurementSetListener) {
            mCallback = (OnMeasurementSetListener) getActivity();
        }
    }

    public void setListener(OnMeasurementSetListener mCallback) {
        this.mCallback = mCallback;
    }

    private static Config createConfig(Context context, double value) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        int min = 0;
        int max = 0;

        switch (som) {
            case METRIC:
                min = 0;
                max = 272;
                break;
            case US:
                min = 0;
                max = 108;
                break;
        }

        int[] valueUnits = convertValueToUnits(value, settings.getSystemOfMeasurement());
        List<String> displayValuesList1 = new ArrayList<String>();
        int inputVale1 = 0;
        int index = -1;
        for (int i = min; i <= max; i++) {
            String displayValueStr = String.valueOf(i);
            if (som == AppSettings.SystemOfMeasurement.METRIC) {
                displayValueStr += " " + context.getString(som.getHeightSecondaryUnitResource());
            }
            displayValuesList1.add(displayValueStr);
            index++;
            if (valueUnits[0] == i) {
                inputVale1 = index;
            }
        }

        String[] displayValues1 = displayValuesList1.toArray(new String[displayValuesList1.size()]);

        Config config = new Config().setInput3Visible(false).setInput1DisplayValues(displayValues1).setInput1Value(inputVale1);
        if (som == AppSettings.SystemOfMeasurement.METRIC) {
            config.setInput2Visible(false);
        } else {
            config.setInput2DisplayValues(new String[]{"in", "1/4 in", "1/2 in", "3/4 in"}).setInput2Value(valueUnits[1]);
        }
        return config;
    }

    private static int[] convertValueToUnits(double value, AppSettings.SystemOfMeasurement som) {
        int[] result = new int[2];
        if (som == AppSettings.SystemOfMeasurement.METRIC) {
            result[0] = (int)value;
        } else {
            result[0] = (int)value;
            result[1] = getClosestValue2USPosition(value - result[0]);
        }
        return result;
    }

    private static int getClosestValue2USPosition(double of) {
        double min = 1.0d;
        int closestPosition = 0;
        if (of>0.0d) {
            for (int i=0; i<values2US.length; i++) {
                final double diff = Math.abs(values2US[i] - of);

                if (diff < min) {
                    min = diff;
                    closestPosition = i;
                }
            }
        }
        return closestPosition;
    }

}
