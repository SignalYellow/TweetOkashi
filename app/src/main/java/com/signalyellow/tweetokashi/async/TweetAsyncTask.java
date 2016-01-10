package com.signalyellow.tweetokashi.async;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.listener.OnAsyncResultListener;

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
    private Long mReplyId;
    OnAsyncResultListener mListener;

    public static String ERROR = "ツイートに失敗しました";
    public static String SUCCESS = "ツイートに成功しました";

    public TweetAsyncTask(Twitter twitter, String text, OnAsyncResultListener listener) {
        this(twitter,text,null,listener);
    }

    public TweetAsyncTask(Twitter twitter,String text,Long replyId, OnAsyncResultListener listener){
        mTwitter = twitter;
        mText = text;
        mReplyId = replyId;
        mListener = listener;

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
            if(mReplyId != null) statusUpdate.setInReplyToStatusId(mReplyId);
            return mTwitter.updateStatus(statusUpdate);
        }catch (Exception e){
            Log.e(TAG,e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        if(status == null){
            mListener.onResult(ERROR);
            return;
        }else{
            mListener.onResult(SUCCESS);

        }
        Log.d(TAG,"success tweet");
    }


}
