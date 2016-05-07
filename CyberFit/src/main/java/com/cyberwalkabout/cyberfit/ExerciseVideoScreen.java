package com.cyberwalkabout.cyberfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.util.Const;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.parceler.Parcels;

/**
 * @author Maria Dzyokh
 */
public class ExerciseVideoScreen extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    private TextView exerciseDescription;

    private Exercise exercise;

    private boolean fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercise_video_screen);

        exercise = Parcels.unwrap(getIntent().getParcelableExtra(Const.EXERCISE));

        initActionBar();

        /*exerciseDescription = (TextView) findViewById(R.id.exercise_description);
        exerciseDescription.setText(exercise.getDescription());*/

        youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

        try {
            youTubePlayerSupportFragment.initialize(getString(R.string.google_apis_key), this);
        } catch (IllegalArgumentException e) {
            if (isAppInstalled("com.google.android.youtube")) {
                String videoId = exercise.getYoutubeId();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.putExtra("VIDEO_ID", videoId);
                intent.putExtra("force_fullscreen", true);
                startActivity(intent);
                ExerciseVideoScreen.this.finish();
            }
        }

    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (exercise != null) {
                supportActionBar.setTitle(exercise.getName());
            }
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            youTubePlayerSupportFragment.initialize(getString(R.string.google_apis_key), this);
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.setOnFullscreenListener(this);
            player.cueVideo(exercise.getYoutubeId());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setMessage(errorMessage)
                    .setPositiveButtonText(android.R.string.ok).show();
        }
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLayout() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (fullscreen) {
                //exerciseDescription.setVisibility(View.GONE);
                actionBar.hide();
            } else {
                //exerciseDescription.setVisibility(View.VISIBLE);
                actionBar.show();
            }
        }
    }

    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }
}
