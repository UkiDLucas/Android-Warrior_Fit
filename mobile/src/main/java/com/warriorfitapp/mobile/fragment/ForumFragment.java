package com.warriorfitapp.mobile.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warriorfitapp.mobile.R;

/**
 * @author Maria Dzyokh
 */
public class ForumFragment extends Fragment {

    private WebView webView;

    public static ForumFragment newInstance() {
        return new ForumFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forum_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView = (WebView) getView().findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new ForumWebViewClient());
        webView.loadUrl(getString(R.string.forum_url));
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.stopLoading();
    }

    class ForumWebViewClient extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        }

        public void onPageFinished(WebView view, String url) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        }
    }
}
