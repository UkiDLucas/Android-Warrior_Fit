package com.warriorfitapp.mobile.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warriorfitapp.mobile.ForumScreen;
import com.warriorfitapp.mobile.flurry.FlurryAdapter;
import com.warriorfitapp.mobile.util.ShareUtils;

import com.warriorfitapp.mobile.R;
import com.warriorfitapp.mobile.TutorialScreen;

public class InfoFragment extends Fragment implements View.OnClickListener {
    private static final String FACEBOOK_APP = "facebook";
    private static final String MARKET_URL_FORMAT = "market://details?id=%1s";

    private static final SparseArray<String> infoTypes = new SparseArray<>();

    static {
        infoTypes.put(R.id.btn_feedback, "email_us");
        infoTypes.put(R.id.btn_rate_this_app, "rate_app");
        infoTypes.put(R.id.btn_share_this_app, "share_app");
        infoTypes.put(R.id.btn_tutorial, "tutorial");
        infoTypes.put(R.id.btn_blog, "our_page");
    }

    private String[] shareApps = new String[]{"gmail", "twitter", FACEBOOK_APP};
    private String packageName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getView().findViewById(R.id.website).setOnClickListener(this);
        getView().findViewById(R.id.btn_feedback).setOnClickListener(this);
        getView().findViewById(R.id.btn_share_this_app).setOnClickListener(this);
        getView().findViewById(R.id.btn_rate_this_app).setOnClickListener(this);
        getView().findViewById(R.id.btn_tutorial).setOnClickListener(this);
        getView().findViewById(R.id.btn_feedback).setOnClickListener(this);
        getView().findViewById(R.id.btn_blog).setOnClickListener(this);

        packageName = getActivity().getPackageName();

        FlurryAdapter.getInstance().infoOpened();

        if (getResources().getBoolean(R.bool.debug)) {
            //showVersionInfo();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_feedback) {
            sendFeedback();
        } else if (v.getId() == R.id.btn_rate_this_app) {
            rateApp();
        } else if (v.getId() == R.id.btn_share_this_app) {
            new ShareUtils().shareApp(getActivity());
        } else if (v.getId() == R.id.btn_tutorial) {
            startActivity(new Intent(getActivity(), TutorialScreen.class).putExtra("class_name", InfoFragment.class.getSimpleName()));
        } else if (v.getId() == R.id.btn_blog) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_page_url)));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(getActivity(), ForumScreen.class));
            }
        } /*else if (v.getId() == R.id.website) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.cyberwalkabout_url))));
        }*/

        String type = infoTypes.get(v.getId(), null);
        if (type != null) {
            FlurryAdapter.getInstance().infoOpened(type);
        }
    }

    private void rateApp() {
        Uri uri = Uri.parse(String.format(MARKET_URL_FORMAT, packageName));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.market_app_not_found_notice)).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    private void sendFeedback() {
        String versionName = "";
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(InfoFragment.class.getSimpleName(), e.getMessage(), e);
        }
        new ShareUtils().sendMail(getActivity(), getString(R.string.cyberwalkabout_email), getString(R.string.feedback_email_subject, versionName), "");
    }

    /*private void showVersionInfo() {
        TextView versionInfo = (TextView) getView().findViewById(R.id.version_info);
        try {
            versionInfo.setText(getString(R.string.version_info, getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName, "offline"));
            versionInfo.setVisibility(View.VISIBLE);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(InfoFragment.class.getSimpleName(), e.getMessage(), e);
        }
    }*/

}
