package com.signalyellow.tweetokashi.app;

import android.app.Application;

import com.signalyellow.tweetokashi.activity.HomeTimelineActivity;
import com.signalyellow.tweetokashi.keys.Key;
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
    private HomeTimelineActivity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        mHaikuManger = new HaikuManager();
        mLoadBitmapManger = new LoadBitmapManager();
    }

    public LoadBitmapManager getLoadBitmapManger() {
        return mLoadBitmapManger;
    }

    public HaikuManager getHaikuManger() {
        return mHaikuManger;
    }

    public void setActivity(HomeTimelineActivity activity) {
        this.mActivity = activity;
    }

    public HomeTimelineActivity getActivity() {
        return mActivity;
    }
}
