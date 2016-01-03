package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.util.Log;

import com.signalyellow.tweetokashi.data.TweetData;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class DestroyAsyncTask extends AsyncTask<Void,Void, Status>{

    private static final String TAG ="DestroyAsync";

    Twitter mTwitter;
    TweetData mData;

    public DestroyAsyncTask(Twitter twitter, TweetData data) {
        this.mTwitter = twitter;
        this.mData = data;
    }

    @Override
    protected twitter4j.Status doInBackground(Void... params) {
        try{
            return mTwitter.destroyStatus(mData.getTweetId());
        }catch (TwitterException e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        super.onPostExecute(status);
        if(status == null){
            Log.d(TAG, "status is null!");
            return;
        }

        Log.d(TAG,"success delete");
    }
}
