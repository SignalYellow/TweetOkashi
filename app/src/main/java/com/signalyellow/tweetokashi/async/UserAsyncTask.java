package com.signalyellow.tweetokashi.async;

import android.content.Context;
import android.os.AsyncTask;

import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.UserData;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserAsyncTask extends AsyncTask<Void, Void, User> {

    private TweetOkashiApplication mApp;
    private Twitter mTwitter;
    private AsyncTaskListener mListener;

    public UserAsyncTask(Context context, AsyncTaskListener listener) {
        mApp = (TweetOkashiApplication) context;
        mTwitter = mApp.getTwitterInstance();
        mListener = listener;
    }

    @Override
    protected User doInBackground(Void... params) {
        try {
            return mTwitter.verifyCredentials();
        } catch (TwitterException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        if (user == null) {
            mListener.onFinish(null);
            return;
        }
        UserData userData =new UserData(user);
        mApp.setUserData(userData);
        mListener.onFinish(userData);
    }

    public interface AsyncTaskListener {
        void onFinish(UserData data);
    }
}