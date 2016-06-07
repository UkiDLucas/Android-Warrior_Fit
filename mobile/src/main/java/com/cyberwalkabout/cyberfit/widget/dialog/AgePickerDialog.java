package com.cyberwalkabout.cyberfit.widget.dialog;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cyberwalkabout.cyberfit.R;

import java.util.ArrayList;
import java.util.List;

public class AgePickerDialog extends NumberInputDialog {

    public interface OnAgeSetListener {
        void onAgeSet(View view, int age);
    }

    private OnAgeSetListener mCallback;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (mCallback != null) {
                view.clearFocus();
                String displayValue = getConfig().getInput1DisplayValues()[view.getValue1()];
                int value = Integer.valueOf(displayValue.split(" ")[0]);
                mCallback.onAgeSet(view, value);
            }
        }
        dismiss();
    }

    public static AgePickerDialog create(FragmentActivity activity, int value) {
        AgePickerDialog dialog = new AgePickerDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.age_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnAgeSetListener) {
            mCallback = (OnAgeSetListener) targetFragment;
        } else if (getActivity() instanceof OnAgeSetListener) {
            mCallback = (OnAgeSetListener) getActivity();
        }
    }

    public void setListener(OnAgeSetListener mCallback) {
        this.mCallback = mCallback;
    }

    private static Config createConfig(Context context, int value) {
        List<String> displayValuesList = new ArrayList<String>();
        int index = -1;
        int defaultValue = value > 0 ? value : 18;
        int selectedValueIndex = 0;


        for (int i = 5; i <= 100; i++) {
            displayValuesList.add(String.format("%s %s", String.valueOf(i), context.getString(R.string.unit_age)));
            index++;
            if (i == defaultValue) {
                selectedValueIndex = index;
            }

        }
        final String[] displayValues = displayValuesList.toArray(new String[displayValuesList.size()]);
        return new Config().setInput2Visible(false).setInput3Visible(false).setInput1DisplayValues(displayValues).setInput1Value(selectedValueIndex);
    }
}
