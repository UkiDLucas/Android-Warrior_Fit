package com.warriorfitapp.mobile.widget.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.warriorfitapp.mobile.R;

public class RepetitionsInputDialog extends NumberInputDialog {
    private OnRepetitionsSetListener listener;

    public static RepetitionsInputDialog create(FragmentActivity activity, int value) {
        RepetitionsInputDialog dialog = new RepetitionsInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.repetitions_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(value));
        dialog.setArguments(args);
        return dialog;
    }

    public static RepetitionsInputDialog create(FragmentActivity activity) {
        return create(activity, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnRepetitionsSetListener) {
            listener = (OnRepetitionsSetListener) targetFragment;
        } else if (getActivity() instanceof OnRepetitionsSetListener) {
            listener = (OnRepetitionsSetListener) getActivity();
        }
    }

    public void setListener(OnRepetitionsSetListener listener) {
        this.listener = listener;
    }

    private static Config createConfig(int value) {
        return new Config().setInput2Visible(false).setInput3Visible(false).setInput1MinMaxValues(0, 120).setInput1Value(value);
    }

    private static Config createConfig() {
        return createConfig(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                listener.onRepetitionsSet(view, view.getValue1());
            }
        }
        dismiss();
    }

    public interface OnRepetitionsSetListener {
        void onRepetitionsSet(View v, int repetitions);
    }
}
