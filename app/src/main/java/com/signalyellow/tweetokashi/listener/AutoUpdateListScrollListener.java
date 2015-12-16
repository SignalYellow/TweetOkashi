package com.signalyellow.tweetokashi.listener;

import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by shohei on 15/12/16.
 */
public class AutoUpdateListScrollListener implements AbsListView.OnScrollListener {

    public AutoUpdateListScrollListener() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d("changed",String.valueOf(scrollState));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("onScroll",firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
    }
}
