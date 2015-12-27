package com.signalyellow.tweetokashi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.fragment.HomeTimelineFragment;
import com.signalyellow.tweetokashi.activity.nav.NavigationItemAction;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.sub.TweetDataDialogFragment;
import com.signalyellow.tweetokashi.sub.TweetPostActivity;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.*;

public class HomeTimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , HomeTimelineFragment.OnHomeTimelineFragmentListener {

    private static final String TAG = "HomeTimeline";

    private Twitter mTwitter;
    private TweetOkashiApplication mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getString(R.string.app_name_ja));


        mApp= (TweetOkashiApplication)getApplicationContext();
        mTwitter = TwitterUtils.getTwitterInstance(this);

        if (findViewById(R.id.fragment_container) != null) {

            HomeTimelineFragment fragment = new HomeTimelineFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TweetPostActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        {
            ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_image);
            TextView textView1 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_main_text);
            TextView textView2 = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_sub_text);

            new UserAsyncTask(imageView, textView1, textView2, getApplicationContext()).execute();
        }

    }


    class UserAsyncTask extends AsyncTask<Void, Void, User> {
        private ImageView mImageView;
        private TextView mMainTextView;
        private TextView mSubTextView;
        private TweetOkashiApplication mApp;

        public UserAsyncTask(ImageView imageView, TextView mainTextView, TextView subTextView, Context context) {
            mImageView = imageView;
            mMainTextView = mainTextView;
            mSubTextView = subTextView;
            mApp = (TweetOkashiApplication) context;
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
                return;
            }

            mApp.setUserData(new UserData(user));

            mMainTextView.setText(user.getName());
            mSubTextView.setText("@" + user.getScreenName());
            Log.d(TAG, user.getProfileBackgroundColor() + " " + user.getProfileSidebarFillColor() + " " + user.getProfileTextColor());

            mImageView.setTag(user.getProfileImageURL());
            mApp.getLoadBitmapManger().downloadBitmap(mImageView, user.getProfileImageURL());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onTimelineItemClicked(TweetData data) {
        Log.d(TAG, data.getName());
        TweetDataDialogFragment.newInstance(data).show(getFragmentManager(), "dialog" + data.getTweetId());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        NavigationItemAction action = NavigationItemAction.valueOf(item);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return action.getHandler().handle(this, null);
    }
}