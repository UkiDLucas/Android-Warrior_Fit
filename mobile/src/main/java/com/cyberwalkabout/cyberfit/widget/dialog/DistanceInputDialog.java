package com.cyberwalkabout.cyberfit.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;

import java.util.ArrayList;
import java.util.List;

public class DistanceInputDialog extends NumberInputDialog {

    private OnDistanceSetListener listener;

    public static Config createConfig(Context context) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        // TODO: probably this data can be static
        List<String> displayValuesList = new ArrayList<String>();

        int max1 = 0;

        int max2 = 99;
        int step2 = 1;

        switch (som) {
            case METRIC:
                max1 = 100;
                break;
            case US:
                max1 = 30;
                break;
        }

        for (int i = 0; i <= max2; i += step2) {
            displayValuesList.add("."+String.valueOf(i) + " " + context.getString(som.getDistanceUnitResource()));
        }
        final String[] displayValues = displayValuesList.toArray(new String[displayValuesList.size()]);
        return new Config().setInput3Visible(false).setInput1MinMaxValues(0, max1).setInput2DisplayValues(displayValues);
    }

    public static DistanceInputDialog create(FragmentActivity activity) {
        DistanceInputDialog dialog = new DistanceInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.distance_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnDistanceSetListener) {
            listener = (OnDistanceSetListener) targetFragment;
        } else if (getActivity() instanceof OnDistanceSetListener) {
            listener = (OnDistanceSetListener) getActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                float distance = view.getValue1();
                String displayValue2 = getConfig().getInput2DisplayValues()[view.getValue2()];
                String[] valueUnit = displayValue2.split("[. ]");
                float value2 = Float.valueOf(valueUnit[1]);
                String unit = valueUnit[2];
                distance += value2/10;
                listener.onDistanceSet(view, distance, unit);
            }
        }
        dismiss();
    }

    public void setListener(OnDistanceSetListener listener) {
        this.listener = listener;
    }

    public interface OnDistanceSetListener {
        void onDistanceSet(View v, float distance, String unit);
    }
}
