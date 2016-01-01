package com.signalyellow.tweetokashi.app;

import android.app.Application;

import com.signalyellow.tweetokashi.activity.HomeTimelineActivity;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.manager.HaikuManager;
import com.signalyellow.tweetokashi.manager.LoadBitmapManager;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.Twitter;

/**
 * Created by shohei on 15/12/15.
 * Application class
 * contains important managers
 */
public class TweetOkashiApplication extends Application{

    private LoadBitmapManager mLoadBitmapManger;
    private HaikuManager mHaikuManger;
    private UserData mUserData;
    private Twitter mTwitter;

    @Override
    public void onCreate() {
        super.onCreate();
        mHaikuManger = new HaikuManager();
        mLoadBitmapManger = new LoadBitmapManager();
    }

    public Twitter getTwitterInstance(){
        return mTwitter == null ? mTwitter = TwitterUtils.getTwitterInstance(this)
                                : mTwitter;
    }

    public LoadBitmapManager getLoadBitmapManger() {
        return mLoadBitmapManger;
    }

    public HaikuManager getHaikuManger() {
        return mHaikuManger;
    }

    public UserData getUserData() {
        return mUserData;
    }

    public void setUserData(UserData mUserData) {
        this.mUserData = mUserData;
    }
}
