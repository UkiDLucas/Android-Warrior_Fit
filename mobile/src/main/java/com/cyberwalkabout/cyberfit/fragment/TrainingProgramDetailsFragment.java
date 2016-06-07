package com.cyberwalkabout.cyberfit.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.WrapperListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cyberwalkabout.cyberfit.ExerciseDetailsScreen;
import com.cyberwalkabout.cyberfit.MapExerciseScreen;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.Author;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.Program;
import com.cyberwalkabout.cyberfit.model.v2.factory.ExerciseCursorFactory;
import com.cyberwalkabout.cyberfit.util.Const;
import com.cyberwalkabout.cyberfit.util.IFilterable;
import com.cyberwalkabout.cyberfit.util.ShareUtils;
import com.cyberwalkabout.cyberfit.youtube.YoutubeThumbnail;

import org.parceler.Parcels;

public class TrainingProgramDetailsFragment extends ListFragment implements AdapterView.OnItemClickListener, IFilterable, LoaderManager.LoaderCallbacks<Cursor>, CompoundButton.OnCheckedChangeListener {

    private Program program;

    private CheckBox checkBoxSubscribe;

    private ExerciseCursorFactory exerciseCursorFactory = ExerciseCursorFactory.getInstance();
    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    private ShareUtils shareUtils = new ShareUtils();

    private FlurryAdapter flurryAdapter = FlurryAdapter.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.training_program_details_header, getListView(), false));
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        program = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(Const.PROGRAM));
        if (program == null) {
            getActivity().finish();
        } else {

            TextView titleText = (TextView) getView().findViewById(R.id.title);
            titleText.setText(program.getName());

            TextView descriptionText = (TextView) getView().findViewById(R.id.description);
            String description = program.getDescription();
            // cleanup description
            if (!TextUtils.isEmpty(description)) {
                description = description.trim().replaceAll(" +", " ").replaceAll("\\n|\\r", "");
            }
            descriptionText.setText(description);

            checkBoxSubscribe = (CheckBox) getView().findViewById(R.id.checkbox_subscribe);
            checkBoxSubscribe.setOnCheckedChangeListener(this);

            Bundle bundle = new Bundle();
            bundle.putLong(Const.PROGRAM_ID, program.getId());
            getLoaderManager().initLoader(ContentProviderAdapter.LOADER_EXERCISES_BY_PROGRAM_ID, bundle, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return contentProviderAdapter.isProgramSubscribed(getActivity(), program.getId());
            }

            @Override
            protected void onPostExecute(Boolean isSubscribed) {
                checkBoxSubscribe.setChecked(isSubscribed);
            }
        }.execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO: move db access to separate thread
        if (isChecked) {
            contentProviderAdapter.subscribeProgram(getActivity(), program.getId());
        } else {
            contentProviderAdapter.unsubscribeProgram(getActivity(), program.getId());
            // TODO: uncomment
            /*if (appSettings.getSubscribedPrograms().size() > 1) {
                appSettings.unsubscribeProgram(program.getId());

                btnSubscribe.setText(R.string.subscribe);

                flurryAdapter.programUnsubscribe(program);
            } else {
                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.program_details_should_be_subscribed_notice)
                        .setPositiveButtonText(android.R.string.ok).show();
            }*/
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
        Cursor cursor = (Cursor) adapter.getItem(position);

        Exercise exercise = exerciseCursorFactory.create(cursor);
        Parcelable parcelable = Parcels.wrap(exercise);

        startActivity(new Intent(getActivity(), exercise.isMapRequired() ? MapExerciseScreen.class : ExerciseDetailsScreen.class).putExtra(Const.EXERCISE, parcelable));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            new AsyncTask<Long, Void, Author>() {
                @Override
                protected Author doInBackground(Long... params) {
                    return contentProviderAdapter.getAuthorById(getActivity(), params[0]);
                }

                @Override
                protected void onPostExecute(Author author) {
                    shareUtils.shareText(getActivity(), getString(R.string.share_training_program_message, program.getName(), author.getName()));
                }
            }.execute(program.getAuthorId());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void filter(String constraint) {
        if (isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString(Const.SEARCH_KEYWORD, constraint);
            bundle.putLong(Const.PROGRAM_ID, program.getAuthorId());
        }
    }

    @Override
    public String getHint() {
        return getString(R.string.exercises_search_hint);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContentProviderAdapter.LOADER_EXERCISES_BY_PROGRAM_ID) {
            return ContentProviderAdapter.getInstance().loaderExercisesByProgramId(getActivity(), args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isAdded()) {
            if (loader.getId() == ContentProviderAdapter.LOADER_EXERCISES_BY_PROGRAM_ID) {
                ListAdapter adapter = getListView().getAdapter();
                if (adapter == null) {
                    getListView().setAdapter(new ExercisesAdapter(getActivity(), data));
                } else {
                    CursorAdapter cursorAdapter;
                    if (adapter instanceof WrapperListAdapter) {
                        cursorAdapter = (CursorAdapter) ((WrapperListAdapter) adapter).getWrappedAdapter();
                    } else {
                        cursorAdapter = (CursorAdapter) adapter;
                    }
                    cursorAdapter.changeCursor(data);
                }

                setListShown(true);

                flurryAdapter.programOpened(program, checkBoxSubscribe.isChecked(), data.getCount());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class ExercisesAdapter extends ResourceCursorAdapter {

        public ExercisesAdapter(Context context, Cursor c) {
            super(context, R.layout.exercise_list_item, c, true);
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            final ViewHolder viewHolder;
            if (view.getTag() == null) {
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(R.id.exercise_name);
                viewHolder.description = (TextView) view.findViewById(R.id.exercise_description);
                viewHolder.image = (ImageView) view.findViewById(R.id.exercise_image);
                viewHolder.programName = (TextView) view.findViewById(R.id.program_name);
                viewHolder.favoriteBtn = (ToggleButton) view.findViewById(R.id.favorite);
                viewHolder.inProgressIndicator = view.findViewById(R.id.in_progress_indicator);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final String id = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DESCRIPTION));
            if (!TextUtils.isEmpty(description)) {
                description = description.trim().replaceAll(" +", " ").replaceAll("\\n|\\r", "");
            }

            boolean isFavorite = !TextUtils.isEmpty(id) && id.equals(cursor.getString(cursor.getColumnIndex(ExerciseTable.ALIAS_FAVORITE_EXERCISE_ID)));

            viewHolder.title.setText(name);
            viewHolder.description.setText(description);

            viewHolder.programName.setVisibility(View.GONE);
            viewHolder.favoriteBtn.setOnCheckedChangeListener(null);

            viewHolder.favoriteBtn.setOnCheckedChangeListener(null);
            viewHolder.favoriteBtn.setChecked(isFavorite);
            viewHolder.favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            if (contentProviderAdapter.isExerciseFavorite(getActivity(), id)) {
                                contentProviderAdapter.unfavoriteExercise(getActivity(), id);
                            } else {
                                contentProviderAdapter.favoriteExercise(getActivity(), id);
                            }
                            return null;
                        }
                    }.execute();
                }
            });

            // TODO: implement progress indicator
            /*if (inProgress.contains(cursor.getServerId())) {
                viewHolder.inProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                viewHolder.inProgressIndicator.setVisibility(View.GONE);
            }*/

            String youtubeId = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_YOUTUBE_ID));
            String thumbnailUrl = YoutubeThumbnail.MQDEFAULT.toURL(youtubeId);

            Glide.with(TrainingProgramDetailsFragment.this)
                    .load(thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolder.image);
        }


        private class ViewHolder {
            TextView title;
            TextView description;
            ImageView image;
            TextView programName;
            ToggleButton favoriteBtn;
            View inProgressIndicator;
        }
    }
}
