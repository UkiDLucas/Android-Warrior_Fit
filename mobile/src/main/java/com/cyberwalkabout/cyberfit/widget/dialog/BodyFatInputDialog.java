package com.cyberwalkabout.cyberfit.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cyberwalkabout.cyberfit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maria Dzyokh
 */
public class BodyFatInputDialog extends NumberInputDialog {

    private static final int DEFALT_VALUE = 15;

    public interface OnBodyFatSetListener {
        void onBodyFatSet(View v, float bodyFat);
    }

    private OnBodyFatSetListener listener;

    public static BodyFatInputDialog create(FragmentActivity activity, float value) {
        BodyFatInputDialog dialog = new BodyFatInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.body_fat_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    public static void show(FragmentActivity activity, float value) {
        create(activity, value).show(activity.getSupportFragmentManager(), TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnBodyFatSetListener) {
            listener = (OnBodyFatSetListener) targetFragment;
        } else if (getActivity() instanceof OnBodyFatSetListener) {
            listener = (OnBodyFatSetListener) getActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                String displayValue1 = getConfig().getInput1DisplayValues()[view.getValue1()];
                float value = Integer.valueOf(displayValue1);
                String displayValue2 = getConfig().getInput2DisplayValues()[view.getValue2()];
                String[] displayValue2Units = displayValue2.split(" ");
                float value2f = Float.valueOf(displayValue2Units[0]);
                value += value2f;
                listener.onBodyFatSet(view, value);
            }
        }
        dismiss();
    }

    public void setListener(OnBodyFatSetListener listener) {
        this.listener = listener;
    }

    private static Config createConfig(Context context, float value) {

        int valueUnit1 = (int) value;
        int valueUnit2 = Math.round((value - valueUnit1) * 10);

        int min1 = 5;
        int max1 = 100;

        int default1 = (valueUnit1 > 0 ? valueUnit1 : DEFALT_VALUE) - min1;
        int default2 = valueUnit2;


        List<String> displayValues1List = new ArrayList<String>();
        for (int i = min1; i <= max1; i++) {
            displayValues1List.add(String.valueOf(i));
        }

        List<String> displayValues2List = new ArrayList<String>();
        for (int i = 0; i <= 9; i++) {
            displayValues2List.add("." + String.valueOf(i) + " " + context.getString(R.string.unit_percentage));
        }

        final String[] displayValues1 = displayValues1List.toArray(new String[displayValues1List.size()]);
        final String[] displayValues2 = displayValues2List.toArray(new String[displayValues2List.size()]);
        return new Config().setInput3Visible(false).setInput1DisplayValues(displayValues1).setInput2DisplayValues(displayValues2).setInput1Value(default1).setInput2Value(default2);
    }
}
