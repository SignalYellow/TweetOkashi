package com.signalyellow.tweetokashi.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.manager.HaikuManager;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.Twitter;

/**
 * Created by shohei on 15/12/15.
 * Application class
 * contains important managers
 */
public class TweetOkashiApplication extends Application{

    private HaikuManager mHaikuManger;
    private UserData mUserData;
    private Twitter mTwitter;

    @Override
    public void onCreate() {
        super.onCreate();
        mHaikuManger = new HaikuManager();

    }


    public void logout(){
        TwitterUtils.deleteAccessToken(getApplicationContext());
        mHaikuManger = new HaikuManager();
        mUserData = null;
        mTwitter = null;
    }


    public Twitter getTwitterInstance(){
        return mTwitter == null ? mTwitter = TwitterUtils.getTwitterInstance(this)
                                : mTwitter;
    }

    public boolean doesMakeHaiku(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getBoolean(this.getString(R.string.pref_key_haiku_generate),false);
    }

    public boolean doesStream(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getBoolean(this.getString(R.string.pref_key_stream),false);
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
