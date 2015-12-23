package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Status;
import twitter4j.Twitter;

public class TweetAsyncTask extends AsyncTask<String,Void, Status> {
    static final String TAG = "TweetAsync";

    private Twitter mTwitter;

    public TweetAsyncTask(Twitter twitter) {
        mTwitter = twitter;
    }

    @Override
    protected twitter4j.Status doInBackground(String... strings) {
        String text = strings[0];
        if(text.length() > 140) return null;

        try{
            return mTwitter.updateStatus(text);
        }catch (Exception e){
            Log.e(TAG,e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        if(status == null){
            Log.e(TAG,"error!");
            return;
        }
        Log.d(TAG,"success tweet");
    }
}
