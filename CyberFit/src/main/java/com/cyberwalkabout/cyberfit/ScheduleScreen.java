package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;

import com.cyberwalkabout.cyberfit.fragment.ScheduleFragment;

/**
 * @author Maria Dzyokh
 */
public class ScheduleScreen extends NavigationActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.schedule_activity_title));
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new ScheduleFragment()).commit();
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
        return R.layout.schedule_screen;
    }
}
