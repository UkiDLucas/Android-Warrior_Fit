package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * @author Maria Dzyokh
 *         <p/>
 *         Deprecated by Andrii Kovalov at July 8 2015 because we do not want to keep goals for now
 */
@Deprecated
public class GoalsScreen extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.goals_activity_title));
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
        return R.layout.goals_screen;
    }
}
