package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.util.Log;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.listener.OnAsyncResultListener;

import twitter4j.Status;
import twitter4j.Twitter;

public class RetweetAsyncTask extends AsyncTask<Void,Void, Status> {
    private static final String TAG = "RetweetAsync";

    public enum RETWEET_STATUS{
        RETWEET,
        DELETE
    }

    Twitter mTwitter;
    TweetData mData;
    RETWEET_STATUS mStatus;
    OnAsyncResultListener mListener;

    public RetweetAsyncTask(Twitter twitter, TweetData data, RETWEET_STATUS status, OnAsyncResultListener listener) {
        mData = data;
        mTwitter = twitter;
        mStatus = status;
        mListener = listener;
    }

    @Override
    protected twitter4j.Status doInBackground(Void... voids) {
        try {
            switch (mStatus){
                case RETWEET:
                    return mTwitter.retweetStatus(mData.getTweetId());
                case DELETE:
                    return mTwitter.destroyStatus(mData.getRetweetId());
                default:
                    return null;
            }
        }catch (Exception e){
            Log.d(TAG,e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        if(status == null) {
            Log.e(TAG,"status is null!");
            mListener.onResult("エラーが発生しました");
            return;
        }

        switch (mStatus){
            case RETWEET:
                mData.successRetweet(status.getId());
                mListener.onResult("リツイートしました");
                return;
            case DELETE:
                mData.successCancelRetweet();
                mListener.onResult("リツイート解除しました");
                return;
            default:
                Log.e(TAG,"in switch" + mStatus);
        }
    }
}
