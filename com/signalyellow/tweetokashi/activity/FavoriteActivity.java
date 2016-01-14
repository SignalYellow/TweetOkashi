package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class FavoriteActivity extends Activity {

    Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        SimpleTweetData data = (SimpleTweetData)getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_TWEETDATA);
        new FavoriteAsyncTask().execute(data.getTweetId());
        finish();
    }

    private class FavoriteAsyncTask extends AsyncTask<Long,Void,TwitterUtils.TWITTER_STATUS>{
        @Override
        protected TwitterUtils.TWITTER_STATUS doInBackground(Long... params) {
            try {
                mTwitter.createFavorite(params[0]);
                return TwitterUtils.TWITTER_STATUS.SUCCESS;
            } catch (TwitterException e) {
                return TwitterUtils.TWITTER_STATUS.ERROR;
            }

        }

        @Override
        protected void onPostExecute(TwitterUtils.TWITTER_STATUS twitter_status) {
            switch (twitter_status){
                case SUCCESS:
                    showToast(getString(R.string.success_fav));
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
