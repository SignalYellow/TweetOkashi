package com.signalyellow.tweetokashi.activity;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.SearchView;
import android.util.Log;
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
import com.signalyellow.tweetokashi.fragment.FavoriteListFragment;
import com.signalyellow.tweetokashi.fragment.FollowUserFragment;
import com.signalyellow.tweetokashi.fragment.FollowerFragment;
import com.signalyellow.tweetokashi.fragment.HaikuSettingFragment;
import com.signalyellow.tweetokashi.fragment.HomeTimelineFragment;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.fragment.SearchFragment;
import com.signalyellow.tweetokashi.fragment.TweetDataDialogFragment;
import com.signalyellow.tweetokashi.fragment.TweetFragment;
import com.signalyellow.tweetokashi.fragment.UserTimelineFragment;
import com.signalyellow.tweetokashi.fragment.listener.OnTimelineFragmentListener;

import twitter4j.*;

public class HomeTimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnTimelineFragmentListener {

    private static final String TAG = "HomeTimeline";
    private TweetOkashiApplication mApp;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getString(R.string.app_name_ja));

        mApp= (TweetOkashiApplication)getApplicationContext();

        if (findViewById(R.id.fragment_container) != null) {
            HomeTimelineFragment fragment = new HomeTimelineFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, HomeTimelineFragment.class.getSimpleName())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        new UserAsyncTask(getApplicationContext(),headerView).execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetFragment fragment = new TweetFragment();
                replaceFragment(fragment, TweetFragment.class.getSimpleName());
            }
        });
    }

    private void setNavigationHeader(View headerView){
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "a");

            }
        });

        //nav header layout
        RelativeLayout tweetCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_tweet_count_layout_group);
        RelativeLayout followCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follow_layout_group);
        RelativeLayout followerCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follower_layout_group);

        tweetCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTimelineFragment fragment = UserTimelineFragment.newInstance(mApp.getUserData());
                replaceFragment(fragment, UserTimelineFragment.class.getSimpleName() + mApp.getUserData().getScreenName());
            }
        });

        followCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowUserFragment fragment = new FollowUserFragment();
                replaceFragment(fragment, FollowUserFragment.class.getSimpleName());
            }
        });

        followerCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowerFragment fragment = new FollowerFragment();
                replaceFragment(fragment, FollowerFragment.class.getSimpleName());
            }
        });

        //profiles
        ImageView imageView = (ImageView)headerView.findViewById(R.id.nav_image);
        TextView textViewName = (TextView)headerView.findViewById(R.id.nav_text_name);
        TextView textViewScreenName = (TextView)headerView.findViewById(R.id.nav_text_screen_name);
        TextView textViewTweetCount = (TextView)headerView.findViewById(R.id.nav_text_tweet_count);
        TextView textViewFollowCount = (TextView)headerView.findViewById(R.id.nav_text_follow_count);
        TextView textViewFollowerCount = (TextView)headerView.findViewById(R.id.nav_text_follower_count);

        UserData user = mApp.getUserData();
        textViewName.setText(user.getUserName());
        textViewScreenName.setText(user.getAtScreenName());
        textViewFollowCount.setText(String.valueOf(user.getFollowCount()));
        textViewFollowerCount.setText(String.valueOf(user.getFollowerCount()));
        textViewTweetCount.setText(String.valueOf(user.getTweetCount()));
        imageView.setTag(user.getProfileImageURL());
        mApp.getLoadBitmapManger().downloadBitmap(imageView, user.getProfileImageURL());
    }



    private void replaceFragment(Fragment fragment, String tag){
        if(!tag.equals(SearchFragment.class.getSimpleName())){
            mSearchView.setIconified(true);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(getFragmentManager().findFragmentByTag(tag) != null){
            fragment = getFragmentManager().findFragmentByTag(tag);
        }

        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    class UserAsyncTask extends AsyncTask<Void, Void, User> {

        private TweetOkashiApplication mApp;
        private View mHeaderView;
        private Twitter mTwitter;

        public UserAsyncTask(Context context, View headerView) {
            mApp = (TweetOkashiApplication) context;
            mHeaderView = headerView;
            mTwitter = mApp.getTwitterInstance();
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
        getMenuInflater().inflate(R.menu.menu_with_search, menu);
        mSearchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                replaceFragment(SearchFragment.newInstance(query), SearchFragment.class.getSimpleName());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()){
            case R.id.nav_home:
                HomeTimelineFragment fragment = (HomeTimelineFragment)getFragmentManager().findFragmentByTag(HomeTimelineFragment.class.getSimpleName());
                if(fragment == null){
                    fragment = new HomeTimelineFragment();
                }
                replaceFragment(fragment, HomeTimelineFragment.class.getSimpleName());
                return true;
            case R.id.nav_favorite:
                replaceFragment(new FavoriteListFragment(), FavoriteListFragment.class.getSimpleName());
                return true;
            case R.id.nav_tweet:
                replaceFragment(new TweetFragment(), TweetFragment.class.getSimpleName());
                return true;
            case R.id.nav_search:
                SearchFragment searchFragment = (SearchFragment)getFragmentManager().findFragmentByTag(SearchFragment.class.getSimpleName());
                if(searchFragment != null){
                    replaceFragment(searchFragment, SearchFragment.class.getSimpleName());
                }

                if(mSearchView.getQuery().toString().equals("")){
                    mSearchView.setIconified(false);
                }
                return true;
            case R.id.nav_setting:
                replaceFragment(new HaikuSettingFragment(),HaikuSettingFragment.class.getSimpleName());
                return true;
        }
        return false;
    }

}