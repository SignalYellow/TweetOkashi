package com.signalyellow.tweetokashi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.fragment.TweetFragment;


public class TweetPostActivity extends AppCompatActivity {
    public static final String ARG_TWEET_DATA = "ARG_TWEET_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final TweetData data = (TweetData)getIntent().getSerializableExtra(ARG_TWEET_DATA);

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragment_container) != null
                    && getFragmentManager().findFragmentByTag(TweetFragment.class.getSimpleName()) == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container,data == null ? new TweetFragment() : TweetFragment.newInstance(data), TweetFragment.class.getSimpleName())
                        .commit();
            }
        }

    }
}
