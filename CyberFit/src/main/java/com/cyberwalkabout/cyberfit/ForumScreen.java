package com.cyberwalkabout.cyberfit;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;


/**
 * @author Maria Dzyokh
 */
public class ForumScreen extends NavigationActivity {

    private WebView webView;

    @Override
    protected int getLayout() {
        return R.layout.forum_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.forum_screen_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
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
}
