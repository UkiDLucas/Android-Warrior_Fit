package com.cyberwalkabout.cyberfit.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.TrainingProgramDetailsScreen;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.model.v2.Program;
import com.cyberwalkabout.cyberfit.model.v2.factory.ProgramCursorFactory;
import com.cyberwalkabout.cyberfit.util.Const;
import com.cyberwalkabout.cyberfit.util.IFilterable;

import org.parceler.Parcels;

/**
 * @author Maria Dzyokh, Uki D. Lucas, Andrii Kovalov
 */
public class TrainingProgramsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, IFilterable {
    private ProgramCursorFactory programCursorFactory = ProgramCursorFactory.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListShown(true);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_PROGRAMS, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CursorAdapter cursorAdapter = (CursorAdapter) parent.getAdapter();
        Cursor cursor = (Cursor) cursorAdapter.getItem(position);

        Program program = programCursorFactory.create(cursor);

        Parcelable parcelable = Parcels.wrap(program);

        Intent intent = new Intent(getActivity(), TrainingProgramDetailsScreen.class);
        intent.putExtra(Const.PROGRAM, parcelable);
        startActivity(intent);
    }

    @Override
    public void filter(String constraint) {
        if (isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString(Const.SEARCH_KEYWORD, constraint);
            getLoaderManager().restartLoader(ContentProviderAdapter.LOADER_PROGRAMS, bundle, this);
        }
    }

    @Override
    public String getHint() {
        return getString(R.string.training_programs_search_hint);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContentProviderAdapter.LOADER_PROGRAMS) {
            return ContentProviderAdapter.getInstance().loaderProgramsWithAuthorNames(getActivity(), args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getListAdapter() == null) {
            setListAdapter(new TrainingProgramsAdapter(getActivity(), data));
        } else {
            ((CursorAdapter) getListAdapter()).changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private static class TrainingProgramsAdapter extends ResourceCursorAdapter {

        public TrainingProgramsAdapter(Context context, Cursor cursor) {
            super(context, R.layout.training_program_list_item, cursor, true);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder;
            if (view.getTag() == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            long id = cursor.getLong(cursor.getColumnIndex(ProgramTable.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_DESCRIPTION));
            String authorName = cursor.getString(cursor.getColumnIndex(ProgramTable.ALIAS_AUTHOR_NAME));
            boolean isSubscribed = id == cursor.getLong(cursor.getColumnIndex(ProgramTable.ALIAS_SUBSCRIBED_PROGRAM_ID));

            // TODO: fix the data in xml
            description = description.trim().replaceAll(" +", " ").replaceAll("\\n|\\r", "");

            holder.name.setText(name);
            holder.description.setText(description);
            // TODO: move to strings
            holder.author.setText("By " + authorName);
        }

        private class ViewHolder {
            private TextView name;
            private TextView description;
            private TextView author;

            public ViewHolder(View view) {
                name = (TextView) view.findViewById(R.id.display_name);
                description = (TextView) view.findViewById(R.id.description);
                author = (TextView) view.findViewById(R.id.author);
            }
        }
    }

}
