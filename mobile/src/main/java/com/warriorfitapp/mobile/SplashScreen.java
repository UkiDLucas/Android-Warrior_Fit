package com.warriorfitapp.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.warriorfitapp.mobile.flurry.FlurryAdapter;
import com.warriorfitapp.mobile.util.Const;


/**
 * @author Maria Dzyokh
 */
public class SplashScreen extends Activity {

    private AppSettings appSettings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BugSenseHandler.initAndStartSession(this, Const.BUGSENSE_KEY);
        setContentView(R.layout.splash_screen);

        ((TextView) findViewById(R.id.text1)).setText(Html.fromHtml(getString(R.string.splash_screen_text1)));

        appSettings = new AppSettings(SplashScreen.this);

        launchApp();
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
    protected void onDestroy() {
        super.onDestroy();
        BugSenseHandler.closeSession(this);
    }

    private void launchApp() {
        if (!appSettings.isTutorialShown()) {
            startActivity(new Intent(SplashScreen.this, TutorialScreen.class));
        } else {
            startActivity(new Intent(SplashScreen.this, HomeScreen.class));
        }
        finish();
    }
}
