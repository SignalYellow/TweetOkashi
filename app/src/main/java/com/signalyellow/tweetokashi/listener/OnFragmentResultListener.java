package com.signalyellow.tweetokashi.listener;

import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.UserData;

public interface OnFragmentResultListener extends OnAsyncResultListener{
    void onTimelineItemClick(TweetData data);
    void onUserItemClick(UserData data);
    void onTweetDataDialogResult(TweetData data, STATUS status);
    void onUserDataDialogResult(UserData data, STATUS status);
    void onFragmentStart(String subtitle);
}
