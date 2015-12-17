package com.signalyellow.tweetokashi.activity;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;
import com.signalyellow.tweetokashi.fragments.SlidingTabsFragment;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class HomeActivity extends FragmentActivity{

    Twitter mTwitter;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(true) {
            startActivity(new Intent(getApplicationContext(), HomeTimelineActivity.class));
            finish();
        }


        ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.setSubtitle(R.string.app_name_ja);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
            transaction.replace(R.id.tab_main_fragment, fragment);
            transaction.commit();
        }

        mTwitter = TwitterUtils.getTwitterInstance(this);
        new UserAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_tweet:
                Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
                intent.putExtra(TwitterUtils.INTENT_TAG_USERDATA,
                        mUser);
                startActivity(intent);
                return true;
            case R.id.menu_setting:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                return true;
            case R.id.menu_logout:
                showLogoutDialog();
                return true;
            case R.id.menu_help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                return true;
            case R.id.menu_credit:
                startActivity(new Intent(getApplicationContext(),CreditActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_confirm_logout_title))
                .setMessage(getString(R.string.dialog_confirm_logout_message))
                .setPositiveButton(getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(getString(R.string.text_cancel), null)
                .show();
    }

    class UserAsyncTask extends AsyncTask<Void,Void, User>{
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
            if(user == null){
                mUser = null;
                return;
            }

            mUser = user;

        }
    }

    private void logout(){
        TwitterUtils.deleteAccessToken(getApplicationContext());
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }





}
