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

public class WaistInputDialog extends NumberInputDialog {

    public interface OnWaistSetListener {
        void onWaistSet(View view, int waist);
    }

    private OnWaistSetListener mCallback;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (mCallback != null) {
                view.clearFocus();
                String displayValue = getConfig().getInput1DisplayValues()[view.getValue1()];
                int value = Integer.valueOf(displayValue.split(" ")[0]);

                /*AppSettings settings = new AppSettings(getActivity());
                if (settings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.US) {
                    value = (int) ConvertUtils.inchToCm(value);
                }*/

                mCallback.onWaistSet(view, value);
            }
        }
        dismiss();
    }

    public static WaistInputDialog create(FragmentActivity activity, int value) {
        WaistInputDialog dialog = new WaistInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.waist_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnWaistSetListener) {
            mCallback = (OnWaistSetListener) targetFragment;
        } else if (getActivity() instanceof OnWaistSetListener) {
            mCallback = (OnWaistSetListener) getActivity();
        }
    }

    public void setListener(OnWaistSetListener mCallback) {
        this.mCallback = mCallback;
    }

    private static Config createConfig(Context context, int value) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        int min = 0;
        int max = 0;
        int defaultValue = 0;

        switch (som) {
            case METRIC:
                min = 55;
                max = 182;
                defaultValue = value > 0 ? value : 65;
                break;
            case US:
                min = 22;
                max = 72;
                defaultValue = value > 0 ? value : 30;
                break;
        }

        List<String> displayValuesList = new ArrayList<String>();
        int index = -1;
        int selectedValueIndex = 0;
        for (int i = min; i <= max; i++) {
            String units = som == AppSettings.SystemOfMeasurement.US ? context.getString(R.string.unit_inches) : context.getString(R.string.unit_cm);
            displayValuesList.add(String.format("%d %s", i, units));
            index++;
            if (i == defaultValue) {
                selectedValueIndex = index;
            }

        }
        final String[] displayValues = displayValuesList.toArray(new String[displayValuesList.size()]);
        return new Config().setInput2Visible(false).setInput3Visible(false).setInput1DisplayValues(displayValues).setInput1Value(selectedValueIndex);
    }
}
