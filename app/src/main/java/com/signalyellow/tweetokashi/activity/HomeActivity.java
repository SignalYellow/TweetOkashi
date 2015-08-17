package com.signalyellow.tweetokashi.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.fragments.SlidingTabsFragment;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HomeActivity extends FragmentActivity{

    static final String TAG = "HOME_ACTIVITY";

    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
            transaction.replace(R.id.tab_main_fragment, fragment);
            transaction.commit();
        }
        mAdapter = new TweetAdapter(this);

        mTwitter = TwitterUtils.getTwitterInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_tweet:
                startTweetActivity();
                return true;
            case R.id.action_settings:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startTweetActivity(){
        Intent i = new Intent(getApplicationContext(), TweetActivity.class);
        i.putExtra(TweetActivity.INTENT_TAG_JOBKIND,TweetActivity.TWEET_ACTIVITY.TWEET);
        startActivity(i);
    }

    public Twitter onButtonClicked()
    {
        Log.d(TAG,"hello butom");
        return mTwitter;
    }

    private class TweetAdapter extends ArrayAdapter<Status> {

        private LayoutInflater mInflater;


        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = mInflater.inflate(R.layout.list_item_tweet,null);
            }

            Status item = getItem(position);

            TextView name = (TextView)convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());

            TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());


            SmartImageView icon = (SmartImageView)convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return convertView;
        }
    }






    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}
