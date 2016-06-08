package com.warriorfitapp.mobile.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.warriorfitapp.mobile.R;
import com.warriorfitapp.mobile.util.DateUtils;
import com.warriorfitapp.model.v2.Exercise;
import com.warriorfitapp.model.v2.ExerciseSession;

import java.util.Date;
import java.util.List;

public class ExerciseHistoryView extends LinearLayout {
    private ExerciseHistoryAdapter adapter;
    private LinearLayout rowsContainer;
    private TextView headerText;
    private View header;
    private ImageButton btnNotice;

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

    public ExerciseHistoryView(Context context) {
        super(context);
        init();
    }

    public ExerciseHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final Context context = getContext();
        LayoutInflater.from(context).inflate(R.layout.exercise_details_history, this);
        rowsContainer = (LinearLayout) findViewById(R.id.rows_container);
        header = findViewById(R.id.header);
        headerText = (TextView) findViewById(R.id.header_text);
        btnNotice = (ImageButton) findViewById(R.id.btn_notice);
    }

    public void setNoticeButtonCLickListener(OnClickListener listener) {
        btnNotice.setOnClickListener(listener);
    }

    public ExerciseHistoryAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ExerciseHistoryAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
        header.setVisibility(adapter.getCount() > 0 ? GONE : VISIBLE);
        btnNotice.setVisibility(adapter.getCount() > 0 ? View.GONE : View.VISIBLE);
        initViewsFromAdapter();
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

    public static class ExerciseHistoryAdapter extends BaseAdapter {
        private final Exercise exercise;
        private final Context context;
        private final List<ExerciseSession> exerciseSessions;

        public ExerciseHistoryAdapter(Context context, Exercise exercise, List<ExerciseSession> exerciseSessions) {
            this.context = context;
            this.exerciseSessions = exerciseSessions;
            this.exercise = exercise;
        }

        @Override
        public int getCount() {
            return exerciseSessions == null ? 0 : exerciseSessions.size();
        }

        @Override
        public ExerciseSession getItem(int position) {
            return exerciseSessions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ExerciseHistoryRow exerciseHistoryRow = new ExerciseHistoryRow(context);
            exerciseHistoryRow.updateData(exercise, getItem(position));
            Date timestamp = new Date(getItem(position).getTimestampCompleted());
            if (position == 0 || !DateUtils.isSameDay(timestamp, new Date(getItem(position - 1).getTimestampCompleted()))) {
                exerciseHistoryRow.showDateHeader(timestamp);
            } else {
                exerciseHistoryRow.hideDateHeader();
            }
            return exerciseHistoryRow;
        }
    }
}
