package com.signalyellow.tweetokashi.listener;

/**
 * Created by shohei on 15/12/16.
 */
public interface Refreshable {
    boolean isRefreshing();
    void setRefreshing(boolean refreshing);
}
