package com.cyberwalkabout.cyberfit.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;

import java.text.SimpleDateFormat;

/**
 * @author Maria Dzyokh
 */
public class MeasurementHistory extends LinearLayout {

    private MeasurementHistoryAdapter adapter;
    private LinearLayout rowsContainer;
    private TextView headerEmpty;
    private View headerNotEmpty;

    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            refreshViewsFromAdapter();
        }

        @Override
        public void onInvalidated() {
            rowsContainer.removeAllViews();
        }
    };

    public MeasurementHistory(Context context) {
        super(context);
        init();
    }

    public MeasurementHistory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final Context context = getContext();
        LayoutInflater.from(context).inflate(R.layout.measurements_history, this);
        rowsContainer = (LinearLayout) findViewById(R.id.rows_container);
        headerNotEmpty = findViewById(R.id.header_not_empty);
        headerEmpty = (TextView) findViewById(R.id.header_empty);
        SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(R.string.measurements_history_header_empty));
        Bitmap plusIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_plus);
        ssb.setSpan(new ImageSpan(context, plusIcon, DynamicDrawableSpan.ALIGN_BASELINE), 47, 48, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        headerEmpty.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    public MeasurementHistoryAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MeasurementHistoryAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
        if (adapter.getCount()>0) {
            headerNotEmpty.setVisibility(View.VISIBLE);
            headerEmpty.setVisibility(View.GONE);
        } else {
            headerNotEmpty.setVisibility(View.GONE);
            headerEmpty.setVisibility(View.VISIBLE);
        }
        initViewsFromAdapter();
    }

    public void hideEmptyHeader() {
        if (adapter.getCount() == 0) {
            headerEmpty.setVisibility(View.GONE);
        }
    }

    public void showEmptyHeader() {
        if (adapter.getCount() == 0) {
            headerEmpty.setVisibility(View.VISIBLE);
        }
    }

    protected void initViewsFromAdapter() {
        rowsContainer.removeAllViews();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                rowsContainer.addView(adapter.getView(i, null, this), i);
            }
        }
    }

    protected void refreshViewsFromAdapter() {
        int childCount = getChildCount();
        int adapterSize = adapter.getCount();
        int reuseCount = Math.min(childCount, adapterSize);

        for (int i = 0; i < reuseCount; i++) {
            adapter.getView(i, getChildAt(i), this);
        }

        if (childCount < adapterSize) {
            for (int i = childCount; i < adapterSize; i++) {
                rowsContainer.addView(adapter.getView(i, null, this), i);
            }
        } else if (childCount > adapterSize) {
            rowsContainer.removeViews(adapterSize, childCount);
        }
    }

    // FIXME: 8/11/15
    public static class MeasurementHistoryAdapter extends BaseAdapter {
        private  LayoutInflater li;
        // private  List<BodyMeasurement> bodyMeasurements;
        private  SimpleDateFormat dateFormat;
        private  AppSettings appSettings;
        private  String unitStr;

        /*public MeasurementHistoryAdapter(Context context, List<BodyMeasurement> bodyMeasurements) {
            this.li = LayoutInflater.from(context);
            this.bodyMeasurements = bodyMeasurements;
            this.appSettings = new AppSettings(context);
            this.dateFormat = new SimpleDateFormat(context.getString(appSettings.getDateFormat().getFormatResource()));
            this.unitStr = context.getString(appSettings.getSystemOfMeasurement().getHeightSecondaryUnitResource());
        }*/

        @Override
        public int getCount() {
            // return bodyMeasurements == null ? 0 : bodyMeasurements.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // return bodyMeasurements.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            // return getItem(position).getId();
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = li.inflate(R.layout.measurement_history_row, null);
                holder = new ViewHolder();
                holder.txtDate = (TextView) convertView.findViewById(R.id.date);
                holder.txtMeasurement = (TextView) convertView.findViewById(R.id.measurement);
                holder.txtDifference = (TextView) convertView.findViewById(R.id.difference);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            /*BodyMeasurement bodyMeasurement = getItem(position);
            holder.txtDate.setText(dateFormat.format(bodyMeasurement.getDate()));

            double measurementValue = bodyMeasurement.getValue();
            double difference = position==getCount()-1?0:bodyMeasurement.getValue()-getItem(position+1).getValue();

            if (appSettings.getSystemOfMeasurement()==AppSettings.SystemOfMeasurement.EU) {
                measurementValue = ConvertUtils.inchToCm(measurementValue);
                difference = ConvertUtils.inchToCm(difference);
            }

            holder.txtMeasurement.setText(formatValue(measurementValue));
            holder.txtDifference.setText(formatDifferenceValue(difference));*/

            return convertView;
        }

        private String formatDifferenceValue(double value) {
            String result;
            if (value>0) {
                result = "Gained "+formatValue(Math.abs(value));
            } else if (value<0) {
                result = "Lost "+formatValue(Math.abs(value));
            } else {
                result = "0";
            }
            return result;
        }

        private String formatValue(double value) {
            return String.format("%.1f " + unitStr, value);
        }

        static class ViewHolder {
            TextView txtDate;
            TextView txtMeasurement;
            TextView txtDifference;
        }
    }
}
