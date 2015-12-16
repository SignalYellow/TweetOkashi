package com.signalyellow.tweetokashi.listener;

import android.util.Log;
import android.widget.AbsListView;

import com.signalyellow.tweetokashi.sub.TweetDataAdapter;

/**
 * Created by shohei on 15/12/16.
 */
public class AutoUpdateTimelineScrollListener implements AbsListView.OnScrollListener {

    private TweetDataAdapter mAdapter;

    private AutoUpdateTimelineScrollCheckable mScroll;

    public AutoUpdateTimelineScrollListener(AutoUpdateTimelineScrollCheckable scroll,TweetDataAdapter adapter) {
        mAdapter = adapter;
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
