package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * @author Maria Dzyokh
 */
public class PermissionsScreen extends NavigationActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXME: 8/11/15
        // CyberFitFriend friend = (CyberFitFriend)getIntent().getSerializableExtra("friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle(getString(R.string.permissions_activity_title, friend.getDisplayName()));
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
        return R.layout.permissions_screen;
    }
}
