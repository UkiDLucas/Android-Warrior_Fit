package com.cyberwalkabout.cyberfit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cyberwalkabout.cyberfit.R;

public class ScheduleList extends LinearLayout {

    // FIXME: 8/11/15
    // private ScheduleAdapter adapter;
    private LinearLayout rowsContainer;
    private View header;

    /*private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            initViewsFromAdapter();
        }

        @Override
        public void onInvalidated() {
            rowsContainer.removeAllViews();
        }
    };*/

    public ScheduleList(Context context) {
        super(context);
        init();
    }

    public ScheduleList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final Context context = getContext();
        LayoutInflater.from(context).inflate(R.layout.schedule_list, this);
        rowsContainer = (LinearLayout) findViewById(R.id.rows_container);
        header = findViewById(R.id.header);
    }

    // FIXME: 8/11/15
    /*public ScheduleAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ScheduleAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
        initViewsFromAdapter();
    }*/

    // FIXME: 8/11/15
    /*protected void initViewsFromAdapter() {
        rowsContainer.removeAllViews();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                rowsContainer.addView(adapter.getView(i, null, this), i);
            }
            if (adapter.getCount() > 0) {
                header.setVisibility(VISIBLE);
            } else {
                header.setVisibility(GONE);
            }
        } else {
            header.setVisibility(GONE);
        }
    }*/

    // FIXME: 8/11/15
    /*protected void refreshViewsFromAdapter() {
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
    }*/

    // FIXME: 8/11/15
    /*public static class ScheduleAdapter extends BaseAdapter {

        private OnDeleteListener listener;
        // FIXME: 8/11/15
        // private final List<ScheduleEntry> scheduleEntries;
        private final String[] daysOfWeek;
        private final LayoutInflater inflater;
        private final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");
        private final Context context;

        private final SwipeDismissTouchListener.OnDismissCallback onDismissCallback = new SwipeDismissTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(View view, Object token) {
                if (listener != null) {
                    int position = (Integer) token;
                    listener.onDelete(getItem(position), position);
                }
            }
        };

        public ScheduleAdapter(Context context, List<ScheduleEntry> scheduleEntries, OnDeleteListener listener) {
            this.scheduleEntries = scheduleEntries;
            this.inflater = LayoutInflater.from(context);
            this.daysOfWeek = context.getResources().getStringArray(R.array.day_of_week);
            this.context = context;
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return scheduleEntries == null ? 0 : scheduleEntries.size();
        }

        @Override
        public ScheduleEntry getItem(int position) {
            return scheduleEntries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.schedule_list_row, parent, false);
            }

            ScheduleEntry entry = getItem(position);

            TextView dayText = (TextView) convertView.findViewById(R.id.day);
            TextView timeText = (TextView) convertView.findViewById(R.id.time);
            TextView occurrenceText = (TextView) convertView.findViewById(R.id.occurrence);


            dayText.setText(daysOfWeek[entry.getDayOfWeek()]);
            timeText.setText(timeFormatter.format(entry.getStartTime()) + " - " + timeFormatter.format(entry.getEndTime()));
            occurrenceText.setText(entry.isRepeat() ? "Every Week" : "One Time");

            convertView.findViewById(R.id.btn_delete).setOnTouchListener(new SwipeDismissTouchListener(convertView, position, onDismissCallback, null));

            return convertView;
        }

        public void remove(int position) {
            scheduleEntries.remove(position);
            notifyDataSetChanged();
        }

    }

    public interface OnDeleteListener {
        public void onDelete(ScheduleEntry entry, int position);
    }*/

}
