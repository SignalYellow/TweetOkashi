package com.signalyellow.tweetokashi.listener;

import android.widget.AbsListView;

import com.signalyellow.tweetokashi.data.TweetDataAdapter;

/**
 * Created by shohei on 15/12/16.
 */
public class AutoUpdateTimelineScrollListener implements AbsListView.OnScrollListener {

    private TweetDataAdapter mAdapter;

    private AutoUpdateTimelineScrollable mScroll;

    public AutoUpdateTimelineScrollListener(AutoUpdateTimelineScrollable scroll,TweetDataAdapter adapter) {
        mAdapter = adapter;
        mScroll = scroll;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount > visibleItemCount &&
                totalItemCount == firstVisibleItem + visibleItemCount && !mScroll.isRefreshing() ){
            mScroll.scrolled();
        }
    }
}
