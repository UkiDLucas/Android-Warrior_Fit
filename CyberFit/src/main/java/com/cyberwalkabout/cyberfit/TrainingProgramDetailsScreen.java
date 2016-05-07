package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.util.IFilterable;

public class TrainingProgramDetailsScreen extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView searchView;

    private IFilterable programDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_program_details_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.program_details_screen_title));
        programDetailsFragment = (IFilterable) getSupportFragmentManager().findFragmentById(R.id.program_details_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.activity_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.exercises_search_hint));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!searchView.isIconified()) {
                clearSearchView();
                return true;
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAdapter.getInstance().startSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAdapter.getInstance().endSession(this);
    }

    private void clearSearchView() {
        searchView.setQuery("", true);
        searchView.setIconified(true);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        programDetailsFragment.filter(s);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        programDetailsFragment.filter(s);
        return true;
    }
}
