package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;
import com.signalyellow.tweetokashi.twitter.TwitterUtils.TWITTER_STATUS;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HaikuRetweetActivity extends Activity {

    Twitter mTwitter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        SimpleTweetData data = (SimpleTweetData)getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_TWEETDATA);

        doHaikuRetweet(data);
    }

    private void doHaikuRetweet(SimpleTweetData data){

        String TAG = getString(R.string.retweet_tag) + " " + getString(R.string.at_twitter_account);

        String tweetText = data.getHaiku() + " " + TAG +  "\n" + data.getURL();

        new TweetAsyncTask().execute(tweetText);
        finish();
    }


    private class TweetAsyncTask extends AsyncTask<String,Void,TWITTER_STATUS> {
        @Override
        protected TWITTER_STATUS doInBackground(String... params) {
            try {
                mTwitter.updateStatus(params[0]);
                return TWITTER_STATUS.SUCCESS;
            } catch (TwitterException e) {
                return TWITTER_STATUS.ERROR;
            }
        }

        @Override
        protected void onPostExecute(TWITTER_STATUS twitter_status) {

            switch (twitter_status){
                case SUCCESS:
                    showToast(getString(R.string.success_tweet));
                    return;
                case ERROR:
                    showToast(getString(R.string.error_normal));
                    break;
            }
        }
    }

    private void showToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
