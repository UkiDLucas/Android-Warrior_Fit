package com.cyberwalkabout.cyberfit.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

import com.cyberwalkabout.cyberfit.CyberFitApp;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.util.Const;

/*import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;*/

/**
 * @author Maria Dzyokh
 */

// TODO: cleanup pull to refresh
public abstract class AbstractListFragment extends ListFragment /*implements OnRefreshListener*/ {

    private BroadcastReceiver populateDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populateList();
        }
    };
    //   private PullToRefreshLayout mPullToRefreshLayout;
    protected Handler handler = new Handler();

    protected abstract void populateList();

    protected CyberFitApp getApp() {
        return (CyberFitApp) getActivity().getApplication();
    }

    protected void hideProgress() {
        if (getActivity() != null) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    protected void showProgress() {
        if (getActivity() != null) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
      /*mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

      // We can now setup the PullToRefreshLayout
      ActionBarPullToRefresh.from(getActivity())

         // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
         .insertLayoutInto(viewGroup)

            // We need to mark the ListView and it's Empty View as pullable
            // This is because they are not dirent children of the ViewGroup
         .theseChildrenArePullable(getListView(), getListView().getEmptyView())

            // We can now complete the setup as desired
         .listener(this)
            //        .options(...)
         .setup(mPullToRefreshLayout);*/

        getListView().setDivider(getResources().getDrawable(R.drawable.listview_divider));
        getListView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen.listview_divider_height));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(populateDataReceiver, new IntentFilter(Const.BROADCAST_DATA_LOADED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(populateDataReceiver);
    }

    protected boolean hasView() {
        return getView() != null;
    }

    public void refreshCompleted() {
      /*if (mPullToRefreshLayout != null) {
         mPullToRefreshLayout.setRefreshComplete();
      }*/
    }
}
