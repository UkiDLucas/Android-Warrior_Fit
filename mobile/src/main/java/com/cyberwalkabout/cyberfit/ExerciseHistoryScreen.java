package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.cyberwalkabout.cyberfit.fragment.ExerciseHistoryFragment;

/**
 * @author Maria Dzyokh, Andrii Kovalov
 */
public class ExerciseHistoryScreen extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new ExerciseHistoryFragment()).commit();
        initActionBar();
    }

    private void initActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //supportActionBar.setTitle(getString(R.string.exercise_history_activity_title));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayout() {
        return R.layout.exercise_history_screen;
    }
}
