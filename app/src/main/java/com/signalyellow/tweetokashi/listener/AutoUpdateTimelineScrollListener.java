package com.signalyellow.tweetokashi.listener;

import android.widget.AbsListView;

public class AutoUpdateTimelineScrollListener implements AbsListView.OnScrollListener {

    private AutoUpdateTimelineScrollable mScroll;

    public AutoUpdateTimelineScrollListener(AutoUpdateTimelineScrollable scroll) {
        mScroll = scroll;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount > visibleItemCount &&
                totalItemCount == firstVisibleItem + visibleItemCount && !mScroll.isRefreshing()){
            mScroll.scrolled();
        }
    }
}
