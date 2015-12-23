package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.util.Log;

import com.signalyellow.tweetokashi.data.TweetData;

import twitter4j.Status;
import twitter4j.Twitter;

public class FavoriteAsyncTask extends AsyncTask<Void,Void, Status> {
    private static final String TAG = "FavoriteAsync";

    public enum FAVORITE_STATUS{
        FAVORITE, DELETE
    }

    Twitter mTwitter;
    TweetData mData;
    FAVORITE_STATUS mStatus;

    public FavoriteAsyncTask(Twitter twitter, TweetData data ,FAVORITE_STATUS status) {
        mTwitter = twitter;
        mData = data;
        mStatus = status;
    }

    @Override
    protected twitter4j.Status doInBackground(Void... voids) {
        try{
            switch (mStatus){
                case FAVORITE:
                    return mTwitter.createFavorite(mData.getTweetId());
                case DELETE:
                    return mTwitter.destroyFavorite(mData.getTweetId());
                default:
                    return null;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        if(status == null){
            Log.d(TAG,"status is null!");
            return;
        }

        switch (mStatus){
            case FAVORITE:
                mData.successFavorite();
                return;
            case DELETE:
                mData.successCancelFavorite();
                return;
            default:
                Log.e(TAG,"in switch" + mStatus);
        }
    }
}
