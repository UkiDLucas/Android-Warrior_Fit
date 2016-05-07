package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * @author Maria Dzyokh
 */
public class MeasurementsScreen extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.measurement_types_activity_title));
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
        return R.layout.measurements_screen;
    }
}
