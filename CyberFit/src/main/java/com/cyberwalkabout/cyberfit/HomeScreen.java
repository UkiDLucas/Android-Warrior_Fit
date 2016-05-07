package com.cyberwalkabout.cyberfit;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.cyberwalkabout.cyberfit.fragment.ExerciseHistoryFragment;
import com.cyberwalkabout.cyberfit.fragment.ExercisesFragment;
import com.cyberwalkabout.cyberfit.fragment.InfoFragment;
import com.cyberwalkabout.cyberfit.fragment.MeasurementsFragment;
import com.cyberwalkabout.cyberfit.fragment.PhotoComparisonFragment;
import com.cyberwalkabout.cyberfit.fragment.ScheduleFragment;
import com.cyberwalkabout.cyberfit.fragment.TrainingProgramsFragment;
import com.cyberwalkabout.cyberfit.util.IFilterable;
import com.cyberwalkabout.cyberfit.util.ShareUtils;

public class HomeScreen extends NavigationActivity implements SearchView.OnQueryTextListener, ISimpleDialogListener {

    private static final int ONE_SECOND_DELAY = 1000;

    //private Map<Integer, Fragment> fragments = new HashMap<Integer, Fragment>();
    private Fragment currentFragment;

    private SearchView searchView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void navigateTo(int itemId) {
        if (itemId == R.id.edit_profile) {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, MyProfileScreen.class));
        } else {
            setCurrentFragment(itemId);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.home_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                /*//Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);*/

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                currentTitle = menuItem.getTitle().toString();
                setCurrentFragment(menuItem.getItemId());
//                navigateTo(menuItem.getItemId());

                return true;
            }
        });
        navigationView.setCheckedItem(R.id.exercises);

        MenuItem currentItem = navigationView.getMenu().getItem(0);
        currentTitle = currentItem.getTitle().toString();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawerLayout closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawerLayout open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                if (!searchView.isIconified()) {
                    clearSearchView();
                }
            }
        };

        //Setting the actionbarToggle to drawerLayout layout
        drawerLayout.setDrawerListener(drawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        drawerToggle.syncState();

        //initFragments();

        setCurrentFragment(R.id.exercises);

        /*if (!appSettings.isDrawerOpened()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introduceDrawer = true;
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }, ONE_SECOND_DELAY);
        }*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home && !searchView.isIconified()) {
            clearSearchView();
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ShareUtils.SHARE_ACTIVITY_REQ == requestCode) {
            shareUtils.deleteTempImageIfExists(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_NAVIGATE_TO)) {
            setCurrentFragment(intent.getExtras().getInt("id"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.activity_action_search);
        boolean b = currentFragment instanceof IFilterable;
        searchItem.setVisible(b);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint = "";
                if (currentFragment instanceof IFilterable) {
                    hint = ((IFilterable) currentFragment).getHint();
                }
                searchView.setQueryHint(hint);
                drawerToggle.setDrawerIndicatorEnabled(false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                drawerToggle.setDrawerIndicatorEnabled(true);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (currentFragment instanceof IFilterable) {
            ((IFilterable) currentFragment).filter(s);
        }
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (currentFragment instanceof IFilterable) {
            ((IFilterable) currentFragment).filter(s);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            clearSearchView();
        } else if (drawerLayout.isDrawerOpen(Gravity.LEFT) || drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private Fragment createFragment(int id) {
        switch (id) {
            case R.id.exercise_history:
                return ExerciseHistoryFragment.newInstance(false);
            case R.id.exercise_history_with_popup:
                return ExerciseHistoryFragment.newInstance(true);
            case R.id.schedule:
                return new ScheduleFragment();
            case R.id.photo_comparison:
                return new PhotoComparisonFragment();
            case R.id.measurements:
                return new MeasurementsFragment();
            case R.id.training_programs:
                return new TrainingProgramsFragment();
            case R.id.exercises:
                return ExercisesFragment.newInstance();
            case R.id.favorite_exercises:
                return ExercisesFragment.newInstance();
            case R.id.info:
                return new InfoFragment();
            default:
                return null;
        }
    }

    private void setCurrentFragment(int itemId) {
        //currentFragment = fragments.get(itemId);
        currentFragment = createFragment(itemId);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment).commit();

        /*if (itemId == R.id.exercise_history_with_popup) {
            itemId = R.id.exercise_history;
        }*/

        // override exercise history action bar title
        if (itemId == R.id.exercise_history) {
            currentTitle = getString(R.string.app_name) + ": " + getString(R.string.exercise_history);
        }

        drawerLayout.closeDrawers();

        getSupportActionBar().setTitle(currentTitle);
        supportInvalidateOptionsMenu();
    }

    private void clearSearchView() {
        searchView.setQuery("", true);
        searchView.setIconified(true);
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        finish();
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
    }

    @Override
    public void onNeutralButtonClicked(int i) {

    }
}
