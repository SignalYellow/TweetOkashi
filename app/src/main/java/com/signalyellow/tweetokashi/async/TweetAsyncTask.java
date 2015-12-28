package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.UploadedMedia;

public class TweetAsyncTask extends AsyncTask<String ,Void, Status> {
    static final String TAG = "TweetAsync";

    private Twitter mTwitter;
    private String mText;

    public TweetAsyncTask(Twitter twitter, String text) {
        mTwitter = twitter;
        mText = text;
    }

    @Override
    protected twitter4j.Status doInBackground(@Nullable String... fileStrings) {
        if(mText.length() > 140) return null;

        if(fileStrings != null) Log.d(TAG, fileStrings.length + "");

        try{
            StatusUpdate statusUpdate = new StatusUpdate(mText);
            if(fileStrings != null && fileStrings.length != 0){
                long[] mediaIds = new long[fileStrings.length];
                for(int i=0;i<fileStrings.length;i++){
                    UploadedMedia media = mTwitter.uploadMedia(new File(fileStrings[i]));
                    mediaIds[i] = media.getMediaId();
                }
                statusUpdate.setMediaIds(mediaIds);
            }
            return mTwitter.updateStatus(statusUpdate);
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
