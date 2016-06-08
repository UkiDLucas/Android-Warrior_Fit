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

public class UserWeightInputDialog extends NumberInputDialog {
    public static final String TAG = UserWeightInputDialog.class.getSimpleName();

    public interface OnWeightSetListener {
        void onWeightSet(View v, float weight, String unit);
    }

    private OnWeightSetListener listener;

    protected static NumberInputDialog.Config createConfig(Context context, int value) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        // TODO: probably this data can be static
        List<String> displayValuesList = new ArrayList<String>();

        int min = 0;
        int max = 0;
        int defaultValue = 0;

        switch (som) {
            case METRIC:
                min = 25;
                max = 198;
                defaultValue = (value > 0 ? value : 59) - min;
                break;
            case US:
                min = 55;
                max = 249;
                defaultValue = (value > 0 ? value : 130) - min;
                break;
        }


        for (int i = min; i <= max; i++) {
            displayValuesList.add(String.valueOf(i) + " " + context.getString(som.getWeightUnitResource()));
        }

        final String[] displayValues = displayValuesList.toArray(new String[displayValuesList.size()]);
        return new NumberInputDialog.Config().setInput2Visible(false).setInput3Visible(false).setInput1DisplayValues(displayValues).setInput1Value(defaultValue);
    }

    public static UserWeightInputDialog create(FragmentActivity activity) {
        return create(activity, 0);
    }

    public static UserWeightInputDialog create(FragmentActivity activity, int value) {
        UserWeightInputDialog dialog = new UserWeightInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.weight_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnWeightSetListener) {
            listener = (OnWeightSetListener) targetFragment;
        } else if (getActivity() instanceof OnWeightSetListener) {
            listener = (OnWeightSetListener) getActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                String displayValue = getConfig().getInput1DisplayValues()[view.getValue1()];
                String[] valueUnit = displayValue.split(" ");
                int value = Integer.valueOf(valueUnit[0]);
                String unit = valueUnit[1];
                listener.onWeightSet(view, value, unit);
            }
        }
        dismiss();
    }

    public void setListener(OnWeightSetListener listener) {
        this.listener = listener;
    }

}
