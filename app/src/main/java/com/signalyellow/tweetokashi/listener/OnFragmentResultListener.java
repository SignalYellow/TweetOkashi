package com.signalyellow.tweetokashi.listener;

import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.UserData;

/**
 * Created by shohei on 16/01/03.
 */
public interface OnFragmentResultListener {

    void onTimelineItemClick(TweetData data);
    void onUserItemClick(UserData data);
    void onDialogResult();
}
