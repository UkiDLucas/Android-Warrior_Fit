package com.warriorfitapp.mobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.bumptech.glide.Glide;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.mobile.AppSettings;
import com.warriorfitapp.mobile.ExerciseDetailsScreen;
import com.warriorfitapp.mobile.MapExerciseSummaryScreen;
import com.warriorfitapp.mobile.R;
import com.warriorfitapp.mobile.content.ContentProviderAdapter;
import com.warriorfitapp.mobile.flurry.FlurryAdapter;
import com.warriorfitapp.mobile.model.v2.factory.ExerciseSessionCursorFactory;
import com.warriorfitapp.mobile.model.v2.factory.UserCursorFactory;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.mobile.util.ConvertUtils;
import com.warriorfitapp.mobile.util.DateUtils;
import com.warriorfitapp.mobile.util.ShareUtils;
import com.warriorfitapp.mobile.util.SwipeDismissTouchListener;
import com.warriorfitapp.model.v2.Exercise;
import com.warriorfitapp.model.v2.ExerciseSession;
import com.warriorfitapp.model.v2.ExerciseState;

import org.parceler.Parcels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ExerciseHistoryFragment extends Fragment implements ISimpleDialogListener, LoaderManager.LoaderCallbacks<Cursor>, ShareUtils.OnShareListener {
    private static final String TAG = ExerciseHistoryFragment.class.getSimpleName();

    private static final int REQUEST_POPUP_SHARE_HISTORY = 1;
    private static final int REQUEST_CODE_NO_EXERCISES_COMPLETED_TODAY = 2;

    private static final long MIN_TIME = 5000;
    private static final String DISTANCE_FORMAT = "%.2f";
    private static final String WEIGHT_FORMAT = "%.1f";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    private StickyListHeadersListView listView;
    private TextView emptyTextView;
    private AppSettings appSettings;
    private View userHeader;
    private ShareUtils shareUtils = new ShareUtils(this);

    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    private boolean showSharePopup = false;

    private com.warriorfitapp.model.v2.User currentUser;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView exerciseCompletedNotice;

    public static ExerciseHistoryFragment newInstance(boolean showSharePopup) {
        ExerciseHistoryFragment fragment = new ExerciseHistoryFragment();
        Bundle args = new Bundle();
        args.putBoolean("show_popup", showSharePopup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        appSettings = new AppSettings(getActivity());
        if (getArguments() != null && getArguments().containsKey("show_popup")) {
            showSharePopup = getArguments().getBoolean("show_popup");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exercise_history_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: hide user header for now (in future display for logged in users
        userHeader = getView().findViewById(R.id.user_header);
        profileImageView = (ImageView) userHeader.findViewById(R.id.profile_image);
        usernameTextView = (TextView) userHeader.findViewById(R.id.display_name);
        exerciseCompletedNotice = (TextView) userHeader.findViewById(R.id.exercise_completed_notice);

        //emptyTextView = (TextView) getView().findViewById(R.id.empty_text);
        listView = (StickyListHeadersListView) getView().findViewById(R.id.exercise_history_list);
        //listView.setEmptyView(emptyTextView);

        if (showSharePopup) {
            SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.share_exercises_notice)
                    .setPositiveButtonText(android.R.string.yes)
                    .setNegativeButtonText(android.R.string.no).setTargetFragment(ExerciseHistoryFragment.this, REQUEST_POPUP_SHARE_HISTORY).show();
        }

        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_EXERCISE_HISTORY, null, this);
        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_USER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareHistory() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder message = new StringBuilder();

                Cursor cursor = null;
                try {
                    cursor = contentProviderAdapter.cursorExerciseSessionsCompletedToday(getActivity());
                    if (cursor.getCount() > 0) {
                        AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();

                        while (cursor.moveToNext()) {
                            String exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
                            boolean isTrackDistance = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_DISTANCE)) == 1;
                            boolean isTrackRepetitions = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_REPETITIONS)) == 1;
                            boolean isTrackWeight = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_WEIGHT)) == 1;
                            boolean isTrackTime = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_TIME)) == 1;

                            long time = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIME));
                            double distance = cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_DISTANCE));
                            int repetitions = cursor.getInt(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_REPETITIONS));
                            double weight = cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_WEIGHT));

                            message.append("- ").append(exerciseName).append(" ");

                            if (isTrackDistance) {
                                message.append(String.format(DISTANCE_FORMAT, distance)).append(" ").append(getActivity().getString(som.getDistanceUnitResource()));
                                if (time >= MIN_TIME) {
                                    message.append(" ");
                                    message.append(getString(R.string.in)).append(" ").append(DateUtils.prettyTime(getActivity(), time));
                                }
                            } else if (isTrackRepetitions) {
                                message.append(getString(R.string.n_repetitions, repetitions));
                                if (isTrackWeight) {
                                    message.append(" ");
                                    message.append(String.format(DISTANCE_FORMAT, weight)).append(" ").append(getActivity().getString(som.getWeightUnitResource()));
                                }
                                if (time >= MIN_TIME) {
                                    message.append(" ");
                                    message.append(getString(R.string.in)).append(" ").append(DateUtils.prettyTime(getActivity(), time));
                                }
                            } else if (isTrackTime) {
                                message.append(" ");
                                message.append(getString(R.string.in)).append(" ").append(DateUtils.prettyTime(getActivity(), time));
                            }
                            message.append("\n");
                        }
                        return message.toString();
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String username = usernameTextView.getText().toString();

                                if (TextUtils.isEmpty(username)) {
                                    username = getString(R.string.user);
                                }

                                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                                        .setMessage(getString(R.string.user_not_finished_any_exercises_today, username))
                                        .setTargetFragment(ExerciseHistoryFragment.this, REQUEST_CODE_NO_EXERCISES_COMPLETED_TODAY)
                                        .setPositiveButtonText(android.R.string.ok).setCancelableOnTouchOutside(true).show();
                            }
                        });
                    }
                } finally {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Exception ignore) {
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String message) {
                if (!TextUtils.isEmpty(message)) {
                    File screenshotFile = shareUtils.takeScreenshotAsFile(getActivity());
                    if (screenshotFile != null && screenshotFile.exists()) {
                        shareUtils.shareTextWithImage(getActivity(), getString(R.string.share_exercise_history_message, message), screenshotFile);
                    } else {
                        Toast.makeText(getActivity(), "Failed to take screenshot", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_POPUP_SHARE_HISTORY) {
            shareHistory();
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContentProviderAdapter.LOADER_EXERCISE_HISTORY) {
            return contentProviderAdapter.loaderExerciseSessions(getActivity(), ExerciseState.DONE);
        } else if (id == ContentProviderAdapter.LOADER_USER) {
            return contentProviderAdapter.loaderUser(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContentProviderAdapter.LOADER_EXERCISE_HISTORY) {
            StickyListHeadersAdapter adapter = listView.getAdapter();

            if (adapter == null) {
                listView.setAdapter(new ExerciseHistoryAdapter(getActivity(), data));
            } else {
                ((CursorAdapter) adapter).changeCursor(data);
            }

            final int total = data.getCount();

            flurryExerciseLogOpenedAsync(total);
        } else if (loader.getId() == ContentProviderAdapter.LOADER_USER) {
            if (data != null && data.moveToFirst()) {
                currentUser = UserCursorFactory.getInstance().create(data);

                if (TextUtils.isEmpty(currentUser.getDisplayName()) && TextUtils.isEmpty(currentUser.getImageUri())) {
                    userHeader.setVisibility(View.GONE);
                } else {
                    userHeader.setVisibility(View.VISIBLE);
                    usernameTextView.setText(currentUser.getDisplayName());

                    int defaultDrawable = currentUser.isMale() ? R.drawable.profile_image_stub_male : R.drawable.profile_image_stub_female;
                    String imageUri = currentUser.getImageUri();

                    if (!TextUtils.isEmpty(imageUri)) {
                        Glide.with(getActivity())
                                .load(imageUri)
                                .dontAnimate()
                                .placeholder(defaultDrawable)
                                .centerCrop()
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(defaultDrawable);
                    }

                    loadNumberOfExerciseCompletedToday();
                }
            }
        }
    }

    private void flurryExerciseLogOpenedAsync(final int total) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                com.warriorfitapp.model.v2.User currentUser = contentProviderAdapter.getCurrentUser(getContext());
                if (currentUser != null) {
                    return contentProviderAdapter.getNumberOfExercisesCompletedToday(getActivity(), currentUser.getId());
                } else {
                    return 0;
                }
            }

            @Override
            protected void onPostExecute(Integer num) {
                FlurryAdapter.getInstance().exerciseLogOpened(total, num);
            }
        }.execute();
    }

    // TODO: implement as loader
    private void loadNumberOfExerciseCompletedToday() {
        new AsyncTask<Long, Void, Integer>() {
            @Override
            protected Integer doInBackground(Long... params) {
                return contentProviderAdapter.getNumberOfExercisesCompletedToday(getActivity(), params[0]);
            }

            @Override
            protected void onPostExecute(Integer num) {
                View view = getView();
                if (view != null) {
                    if (num == null || num == 0) {
                        exerciseCompletedNotice.setText(R.string.no_exercises_completed_today);
                        exerciseCompletedNotice.setVisibility(View.VISIBLE);
                    } else {
                        String notice = getString(R.string.completed_capitalized) + " " + num + " " + getResources().getQuantityString(R.plurals.exercises, num) + " " + getString(R.string.today);
                        exerciseCompletedNotice.setText(notice);
                        exerciseCompletedNotice.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute(currentUser.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onNeutralButtonClicked(int i) {

    }

    // TODO: figure out how to share text + image via facebook (not used for now)
    @Override
    public boolean onShare(final Intent intent) {
        new ShareExerciseLogFlurryEvent().execute(intent);
        return false;
    }

    private class ExerciseHistoryAdapter extends ResourceCursorAdapter implements StickyListHeadersAdapter {
        private SimpleDateFormat headerDateFormat = new SimpleDateFormat("EEEEEE " + getString(appSettings.getDateFormat().getFormatResource()));
        private final AppSettings.SystemOfMeasurement som;

        // TODO: consider undo action
        private final SwipeDismissTouchListener.OnDismissCallback onDismissCallback = new SwipeDismissTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(View view, Object token) {
                view.setVisibility(View.GONE);

                Cursor cursor = (Cursor) getItem((Integer) token);

                long id = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_ID));
                String exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));

                contentProviderAdapter.deleteExerciseSessionById(getActivity(), id);

                FlurryAdapter.getInstance().deleteExerciseSession(id, exerciseName);

                getLoaderManager().restartLoader(ContentProviderAdapter.LOADER_EXERCISE_HISTORY, null, ExerciseHistoryFragment.this);
                loadNumberOfExerciseCompletedToday();
            }
        };

        private final View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = (Cursor) getItem((Integer) v.getTag());

                final ExerciseSession exerciseSession = ExerciseSessionCursorFactory.getInstance().create(cursor);

                if (exerciseSession != null) {
                    new AsyncTask<Long, Void, Exercise>() {
                        @Override
                        protected Exercise doInBackground(Long... params) {
                            return ContentProviderAdapter.getInstance().getExerciseById(getActivity(), exerciseSession.getExerciseId());
                        }

                        @Override
                        protected void onPostExecute(Exercise exercise) {
                            if (exercise != null) {
                                Intent intent;
                                if (exercise.isMapRequired()) {
                                    intent = new Intent(getActivity(), MapExerciseSummaryScreen.class);
                                    intent.putExtra(Const.EXERCISE, Parcels.wrap(exercise));
                                    intent.putExtra(Const.EXERCISE_SESSION, Parcels.wrap(exerciseSession));
                                } else {
                                    intent = new Intent(getActivity(), ExerciseDetailsScreen.class);
                                    intent.putExtra(Const.EXERCISE, Parcels.wrap(exercise));
                                }
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Exercise not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }.execute();
                } else {
                    Log.w(ExerciseHistoryFragment.class.getSimpleName(), "Couldn't construct " + ExerciseSession.class.getSimpleName());
                }
            }
        };

        public ExerciseHistoryAdapter(Context context, Cursor c) {
            super(context, R.layout.exercise_history_list_item, c, true);
            som = appSettings.getSystemOfMeasurement();
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setVisibility(View.VISIBLE);

            ViewHolder holder;
            if (view.getTag() == null) {
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // exercise data
            String exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
            boolean isTrackDistance = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_DISTANCE)) == 1;
            boolean isTrackRepetitions = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_REPETITIONS)) == 1;
            boolean isTrackWeight = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_WEIGHT)) == 1;
            boolean isTrackTime = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_TIME)) == 1;

            holder.tertiaryValueText.setVisibility(View.GONE);
            holder.tertiaryValueIcon.setVisibility(View.GONE);

            long time = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIME));

            holder.exerciseNameText.setText(exerciseName);

            try {
                if (isTrackDistance) {
                    double distance = cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_DISTANCE));
                    if (som == AppSettings.SystemOfMeasurement.US) {
                        distance = ConvertUtils.kmToMiles(distance);
                    }
                    holder.primaryValueText.setText(String.format(DISTANCE_FORMAT + " %s", distance, getActivity().getString(som.getDistanceUnitResource())));

                    if (time >= MIN_TIME) {
                        holder.secondaryValueText.setText(TIME_FORMAT.format(new Date(time)));
                        holder.secondaryValueIcon.setImageResource(R.drawable.ic_time_small);

                        holder.secondaryValueText.setVisibility(View.VISIBLE);
                        holder.secondaryValueIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.secondaryValueText.setVisibility(View.GONE);
                        holder.secondaryValueIcon.setVisibility(View.GONE);
                    }
                } else if (isTrackRepetitions) {
                    int repetitions = cursor.getInt(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_REPETITIONS));

                    holder.primaryValueText.setText(getString(R.string.n_repetitions, repetitions));

                    if (isTrackWeight) {
                        double weight = cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_WEIGHT));
                        if (som == AppSettings.SystemOfMeasurement.US) {
                            weight = ConvertUtils.kgToLbs(weight);
                        }

                        holder.secondaryValueText.setText(String.format(WEIGHT_FORMAT + " %s", weight, getString(som.getWeightUnitResource())));
                        holder.secondaryValueIcon.setImageResource(R.drawable.ic_weight_small);
                        holder.secondaryValueText.setVisibility(View.VISIBLE);
                        holder.secondaryValueIcon.setVisibility(View.VISIBLE);

                        if (time >= MIN_TIME) {
                            holder.tertiaryValueText.setText(TIME_FORMAT.format(new Date(time)));
                            holder.tertiaryValueIcon.setImageResource(R.drawable.ic_time_small);
                            holder.tertiaryValueText.setVisibility(View.VISIBLE);
                            holder.tertiaryValueIcon.setVisibility(View.VISIBLE);
                        }

                    } else {

                        if (time >= MIN_TIME) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);

                            Date date = new Date();
                            date.setTime(time);

                            holder.secondaryValueText.setText(TIME_FORMAT.format(calendar.getTime()));
                            holder.secondaryValueIcon.setImageResource(R.drawable.ic_time_small);
                            holder.secondaryValueText.setVisibility(View.VISIBLE);
                            holder.secondaryValueIcon.setVisibility(View.VISIBLE);
                        } else {
                            holder.secondaryValueText.setVisibility(View.GONE);
                            holder.secondaryValueIcon.setVisibility(View.GONE);
                        }
                    }
                } else if (isTrackTime) {
                    holder.primaryValueText.setText("-");

                    holder.secondaryValueText.setText(TIME_FORMAT.format(new Date(time)));
                    holder.secondaryValueIcon.setImageResource(R.drawable.ic_time_small);
                    holder.secondaryValueText.setVisibility(View.VISIBLE);
                    holder.secondaryValueIcon.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                // TODO: fix it
                /*Map<String, String> args = new HashMap<String, String>();
                args.put("exercise_id", String.valueOf(exerciseHistoryRecord.getExerciseId()));
                FlurryAgent.logEvent("exercise_history_fragment", args);*/
            }

            holder.draggableButton.setTag(cursor.getPosition());
            holder.draggableButton.setOnTouchListener(new SwipeDismissTouchListener(view, cursor.getPosition(), onDismissCallback, itemClickListener, false));
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.exercise_history_group_header, null);
                viewHolder = new HeaderViewHolder();
                viewHolder.header = (TextView) convertView.findViewById(R.id.header_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HeaderViewHolder) convertView.getTag();
            }

            Cursor cursor = (Cursor) getItem(position);
            long timestamp = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED));

            viewHolder.header.setText(headerDateFormat.format(new Date(timestamp)));
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            Cursor cursor = (Cursor) getItem(position);
            long timestamp = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(timestamp));
            return calendar.get(Calendar.DAY_OF_YEAR);
        }
    }

    static class HeaderViewHolder {
        TextView header;
    }

    static class ViewHolder {
        TextView exerciseNameText;
        TextView primaryValueText;
        TextView secondaryValueText;
        ImageView secondaryValueIcon;
        TextView tertiaryValueText;
        ImageView tertiaryValueIcon;
        Button draggableButton;

        public ViewHolder(View view) {
            this.exerciseNameText = (TextView) view.findViewById(R.id.exercise_name);
            this.primaryValueText = (TextView) view.findViewById(R.id.primary_value);
            this.secondaryValueIcon = (ImageView) view.findViewById(R.id.secondary_value_ic);
            this.secondaryValueText = (TextView) view.findViewById(R.id.secondary_value);
            this.tertiaryValueIcon = (ImageView) view.findViewById(R.id.tertiary_value_ic);
            this.tertiaryValueText = (TextView) view.findViewById(R.id.tertiary_value);
            this.draggableButton = (Button) view.findViewById(R.id.draggable_button);
            view.setTag(this);
        }
    }

    private class ShareExerciseLogFlurryEvent extends AsyncTask<Intent, Void, Void> {
        @Override
        protected Void doInBackground(Intent... params) {
            int count = 0;
            Cursor cursor = null;
            try {
                cursor = contentProviderAdapter.cursorExerciseSessionsCompletedToday(getActivity());
                if (cursor != null) {
                    count = cursor.getCount();
                }
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception ignore) {
                    }
                }
            }

            FlurryAdapter.getInstance().shareExerciseHistory(params[0].getPackage(), count);
            return null;
        }
    }
}
