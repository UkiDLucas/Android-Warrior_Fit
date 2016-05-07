package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;

import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;

/**
 * @author Maria Dzyokh
 */
public class InvitePersonScreen extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.invite_friend));
    }

    @Override
    protected int getLayout() {
        return R.layout.invite_person_screen;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
