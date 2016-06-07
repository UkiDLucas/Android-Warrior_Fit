package com.cyberwalkabout.cyberfit.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.WrapperListAdapter;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.ExerciseDetailsScreen;
import com.cyberwalkabout.cyberfit.MapExerciseScreen;
import com.cyberwalkabout.cyberfit.NavigationActivity;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.factory.ExerciseCursorFactory;
import com.cyberwalkabout.cyberfit.util.Const;
import com.cyberwalkabout.cyberfit.util.IFilterable;
import com.cyberwalkabout.cyberfit.youtube.YoutubeThumbnail;

import org.parceler.Parcels;

/**
 * @author Maria Dzyokh, Andrii Kovalov
 */
public class ExercisesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, ISimpleDialogListener, IFilterable, SharedPreferences.OnSharedPreferenceChangeListener, DrawerLayout.DrawerListener {
    private static final String TAG = ExercisesFragment.class.getSimpleName();
    private static final int REQUEST_GOALS_POPUP = 1;

    private String searchKeyword;
    private AppSettings appSettings;

    private Handler handler = new Handler();

    private TextView emptyText;
    private DrawerLayout programFilterDrawer;
    private ListView programFilterListView;

    private ExerciseCursorFactory exerciseCursorFactory = ExerciseCursorFactory.getInstance();
    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    private boolean favoritesOnly;

    public static ExercisesFragment newInstance() {
        return new ExercisesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSettings = new AppSettings(getActivity());
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            searchKeyword = savedInstanceState.getString(Const.SEARCH_KEYWORD, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exercises_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);

        favoritesOnly = appSettings.isFilterFavoritesOnly();

        emptyText = (TextView) getView().findViewById(R.id.empty_text);
        programFilterDrawer = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        programFilterListView = (ListView) programFilterDrawer.findViewById(R.id.filterProgramList);

        programFilterDrawer.setDrawerListener(this);

        initEmptyText();

        //showGoalsPopupIfRequired();

        Bundle bundle = new Bundle();
        bundle.putString(Const.SEARCH_KEYWORD, searchKeyword);
        bundle.putBoolean(Const.FAVORITES_ONLY, favoritesOnly);
        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_EXERCISES, bundle, this);
        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_PROGRAMS_FILTER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        appSettings.registerMainSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appSettings.unregisterMainSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.SEARCH_KEYWORD, searchKeyword);
    }

    private void initEmptyText() {
        emptyText.setText(favoritesOnly ? getString(R.string.no_favorite_exercise) : getString(R.string.no_exercise_for_selected_programs));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);

        Exercise exercise = exerciseCursorFactory.create(cursor);
        boolean isFavorite = !cursor.isNull(cursor.getColumnIndex(ExerciseTable.ALIAS_FAVORITE_EXERCISE_ID));

        Parcelable parcelableExercise = Parcels.wrap(exercise);

        Intent intent;
        if (exercise.isMapRequired()) {
            intent = new Intent(getActivity(), MapExerciseScreen.class);
        } else {
            intent = new Intent(getActivity(), ExerciseDetailsScreen.class);
        }

        intent.putExtra(Const.IS_FAVORITE, isFavorite);
        intent.putExtra(Const.EXERCISE, parcelableExercise);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exercises_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (programFilterDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    programFilterDrawer.closeDrawer(Gravity.RIGHT);
                } else {
                    programFilterDrawer.openDrawer(Gravity.RIGHT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_GOALS_POPUP) {
            ((NavigationActivity) getActivity()).navigateTo(R.id.goals);
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void filter(String searchKeyword) {
        if (isAdded()) {
            this.searchKeyword = searchKeyword;
            Bundle bundle = new Bundle();
            bundle.putString(Const.SEARCH_KEYWORD, searchKeyword);
            getLoaderManager().restartLoader(ContentProviderAdapter.LOADER_EXERCISES, bundle, this);

            if (programFilterDrawer.isDrawerOpen(Gravity.RIGHT)) {
                programFilterDrawer.closeDrawer(Gravity.RIGHT);
            }
        }
    }

    @Override
    public String getHint() {
        return getString(R.string.exercises_search_hint);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ContentProviderAdapter.LOADER_EXERCISES: {
                return ContentProviderAdapter.getInstance().loaderExercises(getActivity(), args);
            }
            case ContentProviderAdapter.LOADER_PROGRAMS_FILTER: {
                return ContentProviderAdapter.getInstance().loaderProgramsFilter(getActivity(), args);
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ContentProviderAdapter.LOADER_EXERCISES: {
                if (getListAdapter() == null) {
                    setListAdapter(new ExercisesAdapter(getActivity(), data));
                } else {
                    ((CursorAdapter) getListAdapter()).changeCursor(data);
                }
            }
            break;
            case ContentProviderAdapter.LOADER_PROGRAMS_FILTER: {
                ListAdapter listAdapter = programFilterListView.getAdapter();
                CursorAdapter adapter;
                if (listAdapter instanceof WrapperListAdapter) {
                    adapter = (CursorAdapter) ((WrapperListAdapter) listAdapter).getWrappedAdapter();
                } else {
                    adapter = (CursorAdapter) listAdapter;
                }
                if (adapter == null) {
                    initProgramsFilterList(data);
                } else {
                    adapter.changeCursor(data);
                }
            }
            break;
        }
    }

    private void initProgramsFilterList(Cursor data) {
        programFilterListView.setItemsCanFocus(false);
        programFilterListView.setAdapter(new ProgramsFilterAdapter(getActivity(), data));
        programFilterListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        programFilterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Checkable checkedView = (Checkable) view;
                if (position == 0) {
                    appSettings.setFilterFavoritesOnly(checkedView.isChecked());
                } else {
                    if (checkedView.isChecked()) {
                        ContentProviderAdapter.getInstance().selectedProgram(getActivity(), id);
                    } else {
                        ContentProviderAdapter.getInstance().unselectedProgram(getActivity(), id);
                    }
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showGoalsPopupIfRequired() {
        if (!appSettings.isGoalsPopupShown()) {
            appSettings.setGoalsPopupShown();
            SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                    .setMessage(R.string.see_goals_notice)
                    .setPositiveButtonText(R.string.see_goals)
                    .setNegativeButtonText(R.string.ok)
                    .setTargetFragment(this, REQUEST_GOALS_POPUP)
                    .show();
        }
    }

    @Override
    public void onNeutralButtonClicked(int i) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AppSettings.KEY_FILTER_FAVORITES_ONLY.equals(key)) {
            favoritesOnly = appSettings.isFilterFavoritesOnly();

            Bundle bundle = new Bundle();
            bundle.putString(Const.SEARCH_KEYWORD, searchKeyword);
            bundle.putBoolean(Const.FAVORITES_ONLY, favoritesOnly);
            getLoaderManager().restartLoader(ContentProviderAdapter.LOADER_EXERCISES, bundle, ExercisesFragment.this);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    private class ExercisesAdapter extends ResourceCursorAdapter {

        public ExercisesAdapter(Context context, Cursor c) {
            super(context, R.layout.exercise_list_item, c, true);
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            ViewHolder viewHolder;
            if (view.getTag() == null) {
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            view.setAlpha(1);
            view.setVisibility(View.VISIBLE);
            view.setTranslationY(0);

            final int position = cursor.getPosition();

            final String id = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_ID));
            String youtubeId = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_YOUTUBE_ID));
            String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DESCRIPTION));
            String programNames = cursor.getString(cursor.getColumnIndex(ExerciseTable.ALIAS_PROGRAM_NAMES));
            boolean isFavorite = !TextUtils.isEmpty(id) && id.equals(cursor.getString(cursor.getColumnIndex(ExerciseTable.ALIAS_FAVORITE_EXERCISE_ID)));
            boolean inProgress = cursor.getInt(cursor.getColumnIndex(ExerciseTable.ALIAS_EXERCISE_IN_PROGRESS)) > 0;
            long lastActivityTimestamp = cursor.getLong(cursor.getColumnIndex(ExerciseTable.ALIAS_LAST_ACTIVITY_TIMESTAMP));
            boolean mapRequired = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_MAP_REQUIRED)) == 1;

            if (!TextUtils.isEmpty(description)) {
                description = description.trim().replaceAll(" +", " ").replaceAll("\\n|\\r", "");
                viewHolder.description.setText(description);
            }

            if (!TextUtils.isEmpty(name)) {
                viewHolder.title.setText(name.trim());
            }

            if (DateUtils.isToday(lastActivityTimestamp)) {
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.highlight_gray));
            } else {
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }

            viewHolder.programName.setText(programNames);

            viewHolder.favoriteBtn.setOnCheckedChangeListener(null);
            viewHolder.favoriteBtn.setChecked(isFavorite);
            viewHolder.favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            return contentProviderAdapter.isExerciseFavorite(getActivity(), params[0]);
                        }

                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            final int animationDuration = 300;
                            // TODO: move db access to background thread
                            if (contentProviderAdapter.isExerciseFavorite(getActivity(), id)) {
                                if (favoritesOnly) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ViewCompat.animate(view)
                                                    .translationY(0)
                                                    .alpha(0.0f)
                                                    .setDuration(animationDuration)
                                                    .withEndAction(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            contentProviderAdapter.unfavoriteExercise(getActivity(), id);
                                                        }
                                                    }).start();
                                        }
                                    }, animationDuration);
                                } else {
                                    ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
                                    animator.setDuration(animationDuration);
                                    if (position < getCount() - 1) {
                                        animator.translationYBy(view.getHeight());
                                        animator.alpha(0f);
                                    }
                                    animator.withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            contentProviderAdapter.unfavoriteExercise(getActivity(), id);
                                        }
                                    });
                                    animator.start();
                                }
                            } else {
                                ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
                                animator.setDuration(animationDuration);
                                if (position > 0) {
                                    animator.translationYBy(-view.getHeight());
                                    animator.alpha(0f);
                                }
                                animator.withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        contentProviderAdapter.favoriteExercise(getActivity(), id);
                                    }
                                });
                                animator.start();
                            }
                        }
                    }.execute(id);
                }
            });

            viewHolder.inProgressIndicator.setVisibility(inProgress ? View.VISIBLE : View.GONE);

            String thumbnailUrl = YoutubeThumbnail.MQDEFAULT.toURL(youtubeId);

            Glide.with(ExercisesFragment.this)
                    .load(thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolder.image);
        }

        private class ViewHolder {
            private TextView title;
            private TextView description;
            private ImageView image;
            private TextView programName;
            private ToggleButton favoriteBtn;
            private View inProgressIndicator;
            private View rootView;

            public ViewHolder(View view) {
                title = (TextView) view.findViewById(R.id.exercise_name);
                description = (TextView) view.findViewById(R.id.exercise_description);
                image = (ImageView) view.findViewById(R.id.exercise_image);
                programName = (TextView) view.findViewById(R.id.program_name);
                favoriteBtn = (ToggleButton) view.findViewById(R.id.favorite);
                inProgressIndicator = view.findViewById(R.id.in_progress_indicator);
                rootView = view.findViewById(R.id.root_view);
            }
        }
    }

    private class ProgramsFilterAdapter extends ResourceCursorAdapter {

        public ProgramsFilterAdapter(Context context, Cursor c) {
            super(context, R.layout.program_filter_list_item, c, true);
        }

        @Override
        public long getItemId(int position) {
            if (position == 0) {
                return -1;
            } else {
                return super.getItemId(position - 1);
            }
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (!mDataValid) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            }

            CheckedTextView checkedTextView;
            if (convertView == null) {
                checkedTextView = (CheckedTextView) newView(mContext, mCursor, parent);
            } else {
                checkedTextView = (CheckedTextView) convertView;
            }

            if (position > 0) {
                moveCursorTo(position - 1);

                String programName = mCursor.getString(mCursor.getColumnIndex(ProgramTable.COLUMN_NAME));
                String authorName = mCursor.getString(mCursor.getColumnIndex(ProgramTable.ALIAS_AUTHOR_NAME));

                boolean isSelected = !mCursor.isNull(mCursor.getColumnIndex(ProgramTable.ALIAS_SELECTED_PROGRAM_ID));

                String authorLabel = "\n" + getString(R.string.by) + " " + authorName;

                SpannableString text = new SpannableString(programName + authorLabel);
                text.setSpan(new RelativeSizeSpan(0.75f), programName.length(), text.length(), 0);
                text.setSpan(new StyleSpan(Typeface.ITALIC), programName.length(), text.length(), 0);
                text.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.secondary_text_dark)), programName.length(), text.length(), 0);

                checkedTextView.setText(text);

                programFilterListView.setItemChecked(position, isSelected);
            } else {
                checkedTextView.setText(R.string.show_favorites_only);
                programFilterListView.setItemChecked(0, favoritesOnly);
            }

            return checkedTextView;
        }

        private void moveCursorTo(int position) {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
