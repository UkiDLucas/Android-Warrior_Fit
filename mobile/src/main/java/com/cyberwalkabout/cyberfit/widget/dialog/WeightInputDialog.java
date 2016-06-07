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

/**
 * @author Maria Dzyokh
 */
public class WeightInputDialog extends NumberInputDialog {
    public static final String TAG = WeightInputDialog.class.getSimpleName();

    public interface OnWeightSetListener {
        void onWeightSet(View v, float weight, String unit);
    }

    private OnWeightSetListener listener;

    protected static int[] getMinMaxVales(AppSettings.SystemOfMeasurement som) {
        int[] values = new int[2];
        switch (som) {
            case METRIC:
                values[0] = 0;
                values[1] = 198;
                break;
            case US:
                values[0] = 0;
                values[1] = 495;
                break;
        }
        return values;
    }

    protected static NumberInputDialog.Config createConfig(Context context, int value) {
        AppSettings settings = new AppSettings(context);
        AppSettings.SystemOfMeasurement som = settings.getSystemOfMeasurement();

        // TODO: probably this data can be static
        List<String> displayValuesList = new ArrayList<String>();

        int[] values = getMinMaxVales(settings.getSystemOfMeasurement());
        int min = values[0];
        int max = values[1];
        int defaultValue = value;

        for (int i = min; i <= max; i++) {
            displayValuesList.add(String.valueOf(i) + " " + context.getString(som.getWeightUnitResource()));
        }

        final String[] displayValues = displayValuesList.toArray(new String[displayValuesList.size()]);
        return new NumberInputDialog.Config().setInput2Visible(false).setInput3Visible(false).setInput1DisplayValues(displayValues).setInput1Value(defaultValue);
    }

    public static WeightInputDialog create(FragmentActivity activity) {
        return create(activity, 0);
    }

    public static WeightInputDialog create(FragmentActivity activity, int value) {
        WeightInputDialog dialog = new WeightInputDialog();
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
