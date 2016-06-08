package com.warriorfitapp.mobile;

import android.os.Bundle;
import android.view.MenuItem;

import com.warriorfitapp.mobile.fragment.ScheduleFragment;

/**
 * @author Maria Dzyokh
 * Deprecated by Uki D. Lucas on May 19, 2016 - this screen may be used in the future again
 */
@Deprecated
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
