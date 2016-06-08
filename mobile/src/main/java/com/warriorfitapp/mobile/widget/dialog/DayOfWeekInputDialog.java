package com.warriorfitapp.mobile.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.warriorfitapp.mobile.R;

import java.util.Calendar;

public class DayOfWeekInputDialog extends NumberInputDialog {
    private OnDayOfWeekSetListener listener;

    public static DayOfWeekInputDialog create(FragmentActivity activity, int value) {
        DayOfWeekInputDialog dialog = new DayOfWeekInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, "select day of week");
        args.putSerializable(ARG_CONFIG, createConfig(activity, value));
        dialog.setArguments(args);
        return dialog;
    }

    public interface OnDayOfWeekSetListener {
        void onDayOfWeekSet(int dayOfWeek);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (listener != null) {
                listener.onDayOfWeekSet(view.getValue1());
            }
        }
        dismiss();
    }

    public void setListener(OnDayOfWeekSetListener listener) {
        this.listener = listener;
    }

    private static Config createConfig(Context ctx, int value) {
        if (value == -1) {
            Calendar now = Calendar.getInstance();
            value = now.get(Calendar.DAY_OF_WEEK);
            // modidy value to match iOS version (in iOS monday index is 0, in android monday index is 2)
            if (value == 1) { // in android week starts with sunday
                value = 6;
            } else {
                value -=2;
            }
        }
        String[] displayValues = ctx.getResources().getStringArray(R.array.day_of_week);
        return new Config().setInput2Visible(false).setInput3Visible(false).setInput1DisplayValues(displayValues).setInput1Value(value);
    }
}
