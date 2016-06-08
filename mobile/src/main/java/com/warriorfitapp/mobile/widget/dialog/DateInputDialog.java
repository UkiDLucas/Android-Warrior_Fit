package com.warriorfitapp.mobile.widget.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.warriorfitapp.mobile.R;

import net.simonvt.numberpicker.NumberPicker;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Mearia Dzyokh
 */
public class DateInputDialog extends NumberInputDialog implements NumberPicker.OnValueChangeListener {

    public static DateInputDialog create(FragmentActivity activity, Date date) {
        DateInputDialog dialog = new DateInputDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, activity.getString(R.string.date_input_popup_title));
        args.putSerializable(ARG_CONFIG, createConfig(date));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && targetFragment instanceof OnDateSetListener) {
            mCallback = (OnDateSetListener) targetFragment;
        } else if (getActivity() instanceof OnDateSetListener) {
            mCallback = (OnDateSetListener) getActivity();
        }
        view.getInput1().setOnValueChangedListener(this);
        view.getInput3().setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, view.getValue3());
        calendar.set(Calendar.MONTH, view.getValue1());
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        view.getInput2().setMaxValue(numDays);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sdl_button_positive) {
            if (mCallback != null) {
                String displayValue1 = getConfig().getInput1DisplayValues()[view.getValue1()];
                String displayValue2 = Integer.toString(view.getValue2());
                String displayValue3 = Integer.toString(view.getValue3());
                SimpleDateFormat format = new SimpleDateFormat("MMMMM-dd-yyyy");
                try {
                    Date date = format.parse(displayValue1 + "-" + displayValue2 + "-" + displayValue3);
                    Calendar now = Calendar.getInstance();
                    Calendar dateDal = Calendar.getInstance();
                    dateDal.setTime(date);
                    dateDal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
                    dateDal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
                    dateDal.set(Calendar.SECOND, now.get(Calendar.SECOND));
                    dateDal.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
                    mCallback.onDateSet(dateDal.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        dismiss();
    }

    public void setListener(OnDateSetListener mCallback) {
        this.mCallback = mCallback;
    }

    public interface OnDateSetListener {
        void onDateSet(Date date);
    }

    private OnDateSetListener mCallback;


    private static Config createConfig(Date value) {
        Calendar date = Calendar.getInstance();
        if (value != null) {
            date.setTime(value);
        }
        final String[] displayValues1 = new DateFormatSymbols().getMonths();

        int yearMin = 2000;
        int yearMax = date.get(Calendar.YEAR);

        int daysMin = 1;
        int daysMax = date.getActualMaximum(Calendar.DAY_OF_MONTH);

        return new Config().setInput1DisplayValues(displayValues1).setInput2MinMaxValues(daysMin, daysMax).setInput3MinMaxValues(yearMin, yearMax).setInput1Value(date.get(Calendar.MONTH)).setInput2Value(date.get(Calendar.DAY_OF_MONTH)).setInput3Value(yearMax);
    }
}
