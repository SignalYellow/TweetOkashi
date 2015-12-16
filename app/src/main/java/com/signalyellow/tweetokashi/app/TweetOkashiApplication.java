package com.signalyellow.tweetokashi.app;

import android.app.Application;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.manager.HaikuManager;
import com.signalyellow.tweetokashi.manager.LoadBitmapManager;

/**
 * Created by shohei on 15/12/15.
 * Application class
 * contains important managers
 */
public class TweetOkashiApplication extends Application{

    private LoadBitmapManager mLoadBitmapManger;
    private HaikuManager mHaikuManger;

    @Override
    public void onCreate() {
        super.onCreate();
        mHaikuManger = new HaikuManager(getApplicationContext().getString(R.string.goo_id));
        mLoadBitmapManger = new LoadBitmapManager();
    }

    public LoadBitmapManager getLoadBitmapManger() {
        return mLoadBitmapManger;
    }

    public HaikuManager getHaikuManger() {
        return mHaikuManger;
    }
}
