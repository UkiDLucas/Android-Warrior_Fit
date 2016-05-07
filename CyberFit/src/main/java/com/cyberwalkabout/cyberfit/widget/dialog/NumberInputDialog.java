package com.cyberwalkabout.cyberfit.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.cyberwalkabout.cyberfit.R;

import net.simonvt.numberpicker.NumberPicker;

import java.io.Serializable;

public abstract class NumberInputDialog extends BaseDialogFragment implements View.OnClickListener {

    public static final String TAG = NumberInputDialog.class.getSimpleName();
    protected static final String ARG_TITLE = "title";
    protected static final String ARG_CONFIG = "config";

    protected NumberInputView view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        return dialog;
    }

    @Override
    public Builder build(Builder builder) {
        builder.setTitle(getTitle());

        view = new NumberInputView(getActivity());

        Config config = getConfig();
        // TODO: if min && max == 0 the hide input
        view.input1.setVisibility(config.isInput1Visible ? View.VISIBLE : View.GONE);
        view.input2.setVisibility(config.isInput2Visible ? View.VISIBLE : View.GONE);
        view.input3.setVisibility(config.isInput3Visible ? View.VISIBLE : View.GONE);

        if (config.input1DisplayValues != null) {
            view.input1.setDisplayedValues(config.input1DisplayValues);

            view.input1.setMinValue(0);
            view.input1.setMaxValue(config.input1DisplayValues.length - 1);
        } else {
            view.input1.setMinValue(config.input1MinMaxValues[0]);
            view.input1.setMaxValue(config.input1MinMaxValues[1]);
        }

        if (config.input2DisplayValues != null) {
            view.input2.setDisplayedValues(config.input2DisplayValues);

            view.input2.setMinValue(0);
            view.input2.setMaxValue(config.input2DisplayValues.length - 1);
        } else {
            view.input2.setMinValue(config.input2MinMaxValues[0]);
            view.input2.setMaxValue(config.input2MinMaxValues[1]);
        }

        if (config.input3DisplayValues != null) {
            view.input3.setDisplayedValues(config.input3DisplayValues);

            view.input3.setMinValue(0);
            view.input3.setMaxValue(config.input3DisplayValues.length - 1);
        } else {
            view.input3.setMinValue(config.input3MinMaxValues[0]);
            view.input3.setMaxValue(config.input3MinMaxValues[1]);
        }

        if (!TextUtils.isEmpty(config.input1Format)) {
            view.input1.setFormatter(new SimpleFormatter(config.input1Format));
        }

        if (!TextUtils.isEmpty(config.input2Format)) {
            view.input2.setFormatter(new SimpleFormatter(config.input2Format));
        }

        if (!TextUtils.isEmpty(config.input3Format)) {
            view.input3.setFormatter(new SimpleFormatter(config.input3Format));
        }

        view.input1.setValue(config.input1Value);
        view.input2.setValue(config.input2Value);
        view.input3.setValue(config.input3Value);


        builder.setPositiveButton(getActivity().getString(R.string.ok), this);
        builder.setNegativeButton(getActivity().getString(R.string.cancel), this);
        builder.setView(view);
        return builder;
    }

    protected String getTitle() {
        return getArguments().getString(ARG_TITLE);
    }

    protected Config getConfig() {
        return (Config) getArguments().getSerializable(ARG_CONFIG);
    }

    static class Config implements Serializable {
        private int input1Value;
        private int input2Value;
        private int input3Value;

        private boolean isInput1Visible = true;
        private boolean isInput2Visible = true;
        private boolean isInput3Visible = true;

        private int[] input1MinMaxValues = new int[]{0, 0};
        private int[] input2MinMaxValues = new int[]{0, 0};
        private int[] input3MinMaxValues = new int[]{0, 0};

        private String[] input1DisplayValues;
        private String[] input2DisplayValues;
        private String[] input3DisplayValues;

        private String input1Format;
        private String input2Format;
        private String input3Format;

        public Config setInput1Visible(boolean isInput1Visible) {
            this.isInput1Visible = isInput1Visible;
            return this;
        }

        public Config setInput2Visible(boolean isInput2Visible) {
            this.isInput2Visible = isInput2Visible;
            return this;
        }

        public Config setInput3Visible(boolean isInput3Visible) {
            this.isInput3Visible = isInput3Visible;
            return this;
        }

        public Config setInput1MinMaxValues(int min, int max) {
            this.input1MinMaxValues[0] = min;
            this.input1MinMaxValues[1] = max;
            return this;
        }

        public Config setInput2MinMaxValues(int min, int max) {
            this.input2MinMaxValues[0] = min;
            this.input2MinMaxValues[1] = max;
            return this;
        }

        public Config setInput3MinMaxValues(int min, int max) {
            this.input3MinMaxValues[0] = min;
            this.input3MinMaxValues[1] = max;
            return this;
        }

        public Config setInput1Value(int input1Value) {
            this.input1Value = input1Value;
            return this;
        }

        public Config setInput2Value(int input2Value) {
            this.input2Value = input2Value;
            return this;
        }

        public Config setInput3Value(int input3Value) {
            this.input3Value = input3Value;
            return this;
        }

        public Config setInput1Format(String input1Format) {
            this.input1Format = input1Format;
            return this;
        }

        public Config setInput2Format(String input2Format) {
            this.input2Format = input2Format;
            return this;
        }

        public Config setInput3Format(String input3Format) {
            this.input3Format = input3Format;
            return this;
        }

        public Config setInput1DisplayValues(String[] input1DisplayValues) {
            this.input1DisplayValues = input1DisplayValues;
            return this;
        }

        public Config setInput2DisplayValues(String[] input2DisplayValues) {
            this.input2DisplayValues = input2DisplayValues;
            return this;
        }

        public Config setInput3DisplayValues(String[] input3DisplayValues) {
            this.input3DisplayValues = input3DisplayValues;
            return this;
        }

        public int getInput1Value() {
            return input1Value;
        }

        public int getInput2Value() {
            return input2Value;
        }

        public int getInput3Value() {
            return input3Value;
        }

        public boolean isInput1Visible() {
            return isInput1Visible;
        }

        public boolean isInput2Visible() {
            return isInput2Visible;
        }

        public boolean isInput3Visible() {
            return isInput3Visible;
        }

        public int[] getInput1MinMaxValues() {
            return input1MinMaxValues;
        }

        public int[] getInput2MinMaxValues() {
            return input2MinMaxValues;
        }

        public int[] getInput3MinMaxValues() {
            return input3MinMaxValues;
        }

        public String[] getInput1DisplayValues() {
            return input1DisplayValues;
        }

        public String[] getInput2DisplayValues() {
            return input2DisplayValues;
        }

        public String[] getInput3DisplayValues() {
            return input3DisplayValues;
        }

        public String getInput1Format() {
            return input1Format;
        }

        public String getInput2Format() {
            return input2Format;
        }

        public String getInput3Format() {
            return input3Format;
        }
    }

    static class NumberInputView extends LinearLayout {
        private NumberPicker input1;
        private NumberPicker input2;
        private NumberPicker input3;

        public NumberInputView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.multiple_number_input, this);
            input1 = (NumberPicker) findViewById(R.id.input1);
            input2 = (NumberPicker) findViewById(R.id.input2);
            input3 = (NumberPicker) findViewById(R.id.input3);


            input1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            input2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            input3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }

        public int getValue1() {
            return input1.getValue();
        }

        public int getValue2() {
            return input2.getValue();
        }

        public int getValue3() {
            return input3.getValue();
        }

        public void setValue1(int value) {
            input1.setValue(value);
        }

        public void setValue2(int value) {
            input2.setValue(value);
        }

        public void setValue3(int value) {
            input3.setValue(value);
        }

        public NumberPicker getInput1() {
            return input1;
        }

        public NumberPicker getInput2() {
            return input2;
        }

        public NumberPicker getInput3() {
            return input3;
        }
    }

    private static class SimpleFormatter implements NumberPicker.Formatter {
        private final String format;

        private SimpleFormatter(String format) {
            this.format = format;
        }

        @Override
        public String format(int value) {
            return String.format(format, value);
        }
    }
}
