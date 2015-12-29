package com.signalyellow.tweetokashi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.fragment.FollowUserFragment;
import com.signalyellow.tweetokashi.fragment.FollowerFragment;
import com.signalyellow.tweetokashi.fragment.HomeTimelineFragment;
import com.signalyellow.tweetokashi.activity.nav.NavigationItemAction;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.fragment.TweetDataDialogFragment;
import com.signalyellow.tweetokashi.fragment.UserTimelineFragment;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.*;

public class HomeTimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , HomeTimelineFragment.OnHomeTimelineFragmentListener {

    private static final String TAG = "HomeTimeline";

    private Twitter mTwitter;
    private TweetOkashiApplication mApp;
    private View mHeaderView;


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

                FollowUserFragment fragment = new FollowUserFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                //startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHeaderView = navigationView.getHeaderView(0);
        new UserAsyncTask(getApplicationContext()).execute();


    }

    private void setNavigationHeader(View headerView){
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"a");

            }
        });

        ImageView imageView = (ImageView)headerView.findViewById(R.id.nav_image);
        TextView textViewName = (TextView)headerView.findViewById(R.id.nav_text_name);
        TextView textViewScreenName = (TextView)headerView.findViewById(R.id.nav_text_screen_name);

        RelativeLayout tweetCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_tweet_count_layout_group);
        RelativeLayout followCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follow_layout_group);
        RelativeLayout followerCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follower_layout_group);

        tweetCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"tweet layout");

                UserTimelineFragment fragment = UserTimelineFragment.newInstance(mApp.getUserData());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        followCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"follow");
                FollowUserFragment fragment = new FollowUserFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        followerCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"follower");
                FollowerFragment fragment = new FollowerFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        UserData user = mApp.getUserData();
        textViewName.setText(user.getUserName());
        textViewScreenName.setText(user.getScreenName());
        imageView.setTag(user.getProfileImageURL());
        mApp.getLoadBitmapManger().downloadBitmap(imageView, user.getProfileImageURL());
    }


    class UserAsyncTask extends AsyncTask<Void, Void, User> {

        private TweetOkashiApplication mApp;

        public UserAsyncTask(Context context) {
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
            setNavigationHeader(mHeaderView);
        }
    }

    @Override
    public void onTimelineItemClicked(TweetData data) {
        TweetDataDialogFragment.newInstance(data).show(getFragmentManager(), "dialog" + data.getTweetId());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

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