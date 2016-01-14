package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.util.Log;

import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.listener.OnAsyncResultListener;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class DestroyAsyncTask extends AsyncTask<Void,Void, Status>{

    private static final String TAG ="DestroyAsync";

    Twitter mTwitter;
    TweetData mData;
    OnAsyncResultListener mListener;


    public static String ERROR = "削除に失敗しました";
    public static String SUCCESS = "削除しました";

    public DestroyAsyncTask(Twitter twitter, TweetData data,OnAsyncResultListener listener) {
        this.mTwitter = twitter;
        this.mData = data;
        this.mListener = listener;
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
            mListener.onResult(ERROR);
            return;
        }
        Log.d(TAG,"delete");
        mListener.onResult(SUCCESS);
    }
}
