package com.cyberwalkabout.cyberfit.widget.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cyberwalkabout.cyberfit.R;

public class TimePickerDialog extends NumberInputDialog {

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view    The view associated with this listener.
         * @param hours   The hour that was set.
         * @param minutes The minutes that was set.
         */
        void onTimeSet(View view, int hours, int minutes, int seconds);
    }

    private OnTimeSetListener mCallback;

    public static TimePickerDialog create(FragmentActivity activity, int hours, int minutes, int seconds) {
        TimePickerDialog dialog = new TimePickerDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.time_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(hours, minutes, seconds));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnTimeSetListener) {
            mCallback = (OnTimeSetListener) targetFragment;
        } else if (getActivity() instanceof OnTimeSetListener) {
            mCallback = (OnTimeSetListener) getActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (mCallback != null) {
                view.clearFocus();
                mCallback.onTimeSet(view, view.getValue1(), view.getValue2(), view.getValue3());
            }
        }
        dismiss();
    }

    public void setListener(OnTimeSetListener mCallback) {
        this.mCallback = mCallback;
    }

    private static Config createConfig(int hours, int minutes, int seconds) {
        return new Config().setInput1MinMaxValues(0, 23).setInput2MinMaxValues(0, 59).setInput3MinMaxValues(0, 59)
                .setInput1Value(hours).setInput2Value(minutes).setInput3Value(seconds)
                .setInput1Format("%02dh").setInput2Format("%02dmin").setInput3Format("%02ds")
                .setInput1Value(hours).setInput2Value(minutes).setInput3Value(seconds);
    }
}

