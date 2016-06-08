package com.warriorfitapp.mobile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.bumptech.glide.Glide;
import com.warriorfitapp.mobile.content.ContentProviderAdapter;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.mobile.util.ShareUtils;

import java.lang.reflect.Field;

/**
 * @author Maria Dzyokh
 */
public abstract class NavigationActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = NavigationActivity.class.getSimpleName();

    protected String ACTION_NAVIGATE_TO = "com.cyberwalkabout.cyberfit.ACTION_NAVIGATE_TO";

    protected NavigationView navigationView;

    protected DrawerLayout drawerLayout;

    protected ImageView profileImageView;
    protected TextView usernameTextView;
    protected TextView displayNameTextView;
    protected TextView hintTextView;

    protected String currentTitle;

    protected ShareUtils shareUtils = new ShareUtils();

    protected abstract int getLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, Const.BUGSENSE_KEY);
        setContentView(getLayout());

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        profileImageView = (ImageView) navigationView.findViewById(R.id.profile_image);
        displayNameTextView = (TextView) navigationView.findViewById(R.id.txt_user_display_name);
        usernameTextView = (TextView) navigationView.findViewById(R.id.txt_username);
        hintTextView = (TextView) navigationView.findViewById(R.id.txt_hint);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        increaseDrawerDragMargin(drawerLayout);

        findViewById(R.id.edit_profile).setOnClickListener(this);

        getSupportLoaderManager().initLoader(ContentProviderAdapter.LOADER_USER, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BugSenseHandler.closeSession(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.share) {
            shareUtils.shareApp(this);
        } else if (v instanceof Button) {
            navigateTo(v.getId());
        }
    }

    public void navigateTo(int itemId) {
        Bundle data = new Bundle();
        data.putInt("id", itemId);
        data.putString("title", currentTitle);
        Intent resultIntent = new Intent(this, HomeScreen.class);
        resultIntent.setAction(ACTION_NAVIGATE_TO);
        resultIntent.putExtras(data);
        startActivity(resultIntent);
        finish();
    }

    private void increaseDrawerDragMargin(DrawerLayout mDrawerLayout) {
        try {
            Field dragger = mDrawerLayout.getClass().getDeclaredField("mLeftDragger");//mRightDragger for right obviously
            dragger.setAccessible(true);
            ViewDragHelper draggerObj = (ViewDragHelper) dragger.get(mDrawerLayout);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            int edge = mEdgeSize.getInt(draggerObj);

            mEdgeSize.setInt(draggerObj, edge + getResources().getDimensionPixelSize(R.dimen.navigation_drawer_left_margin));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ContentProviderAdapter.LOADER_USER == id) {
            return ContentProviderAdapter.getInstance().loaderUser(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContentProviderAdapter.LOADER_USER) {
            if (data != null && data.moveToFirst()) {
                String displayName = data.getString(data.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_DISPLAY_NAME));
                String username = data.getString(data.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_USERNAME));
                String imageUri = data.getString(data.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IMAGE_URI));

                boolean isMale = true;
                if (!data.isNull(data.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IS_MALE))) {
                    isMale = data.getInt(data.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IS_MALE)) == 1;
                }

                if (displayName != null) {
                    displayName = displayName.trim();
                    displayNameTextView.setText(displayName);
                    displayNameTextView.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(username)) {
                        usernameTextView.setVisibility(View.VISIBLE);
                        usernameTextView.setText(username);
                    }
                    hintTextView.setVisibility(View.GONE);
                } else {
                    usernameTextView.setVisibility(View.GONE);
                    displayNameTextView.setVisibility(View.GONE);
                    hintTextView.setVisibility(View.VISIBLE);
                }

                int genderDrawable = isMale ? R.drawable.profile_image_stub_male : R.drawable.profile_image_stub_female;
                if (!TextUtils.isEmpty(imageUri)) {
                    Glide.with(this)
                            .load(imageUri)
                            .dontAnimate()
                            .placeholder(genderDrawable)
                            .centerCrop()
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(genderDrawable);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
