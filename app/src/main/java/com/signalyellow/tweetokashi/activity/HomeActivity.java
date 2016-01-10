package com.signalyellow.tweetokashi.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.async.DestroyAsyncTask;
import com.signalyellow.tweetokashi.async.FavoriteAsyncTask;
import com.signalyellow.tweetokashi.async.RetweetAsyncTask;
import com.signalyellow.tweetokashi.async.TweetAsyncTask;
import com.signalyellow.tweetokashi.async.UserAsyncTask;
import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.fragment.FavoriteListFragment;
import com.signalyellow.tweetokashi.fragment.FollowUserFragment;
import com.signalyellow.tweetokashi.fragment.FollowerFragment;
import com.signalyellow.tweetokashi.fragment.HomeTimelineFragment;
import com.signalyellow.tweetokashi.fragment.LogoutDialogFragment;
import com.signalyellow.tweetokashi.fragment.MentionFragment;
import com.signalyellow.tweetokashi.fragment.SearchFragment;
import com.signalyellow.tweetokashi.fragment.TweetDataDialogFragment;
import com.signalyellow.tweetokashi.fragment.TweetFragment;
import com.signalyellow.tweetokashi.fragment.UserDataDialogFragment;
import com.signalyellow.tweetokashi.fragment.UserTimelineFragment;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

import twitter4j.Twitter;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnFragmentResultListener {

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

        mApp = (TweetOkashiApplication) getApplicationContext();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mApp.getUserData() == null) {
            new UserAsyncTask(getApplicationContext(), new UserAsyncTask.AsyncTaskListener() {
                @Override
                public void onFinish(UserData data) {
                    if (data != null) setNavigationHeader(navigationView.getHeaderView(0));
                    else Toast.makeText(getApplicationContext(),"エラーが発生しました." ,Toast.LENGTH_LONG).show();
                }
            }).execute();
        } else {
            setNavigationHeader(navigationView.getHeaderView(0));
        }

        if (findViewById(R.id.fragment_container) != null
                && getSupportFragmentManager().findFragmentByTag(HomeTimelineFragment.class.getSimpleName()) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeTimelineFragment(), HomeTimelineFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();

            if (findViewById(R.id.fragment_container_sub) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_sub,new MentionFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TweetPostActivity.class));
            }
        });
    }

    private void setNavigationHeader(View headerView){

        //nav header layout
        RelativeLayout tweetCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_tweet_count_layout_group);
        RelativeLayout followCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follow_layout_group);
        RelativeLayout followerCountLayout = (RelativeLayout)headerView.findViewById(R.id.nav_follower_layout_group);

        tweetCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTimelineFragment fragment = (UserTimelineFragment)getSupportFragmentManager().findFragmentByTag(UserTimelineFragment.class.getSimpleName());
                if(fragment == null){
                    fragment = UserTimelineFragment.newInstance(mApp.getUserData());}
                replaceFragment(fragment, UserTimelineFragment.class.getSimpleName());
            }
        });

        followCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowUserFragment fragment = (FollowUserFragment)getSupportFragmentManager().findFragmentByTag(FollowUserFragment.class.getSimpleName());
                if(fragment == null){
                    fragment= FollowUserFragment.newInstance(mApp.getUserData());
                }
                replaceFragment(fragment, FollowUserFragment.class.getSimpleName());
            }
        });

        followerCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowerFragment fragment = (FollowerFragment)getSupportFragmentManager().findFragmentByTag(FollowerFragment.class.getSimpleName());
                if(fragment == null) {
                    fragment = FollowerFragment.newInstance(mApp.getUserData());
                }
                replaceFragment(fragment, FollowerFragment.class.getSimpleName());
            }
        });

        //profiles
        SmartImageView imageView = (SmartImageView)headerView.findViewById(R.id.nav_image);
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

        imageView.setImageUrl(user.getProfileImageURL());
    }

    private void replaceFragment(Fragment fragment, String tag){
        replaceFragment(fragment, tag, null);
    }

    private void replaceFragment(Fragment fragment, String tag, Fragment deleteFragment){
        mSearchView.setIconified(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        if(deleteFragment != null) transaction.remove(deleteFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onTimelineItemClick(TweetData data) {
        TweetDataDialogFragment.newInstance(data).show(getSupportFragmentManager(),TweetDataDialogFragment.class.getSimpleName());
    }

    @Override
    public void onUserItemClick(UserData data) {
        UserDataDialogFragment.newInstance(data).show(getSupportFragmentManager(),UserDataDialogFragment.class.getSimpleName());
    }

    @Override
    public void onResult(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTweetDataDialogResult(TweetData data, STATUS status) {

        Twitter twitter = mApp.getTwitterInstance();
        switch (status){
            case RETWEET:
                new RetweetAsyncTask(twitter,data,RetweetAsyncTask.RETWEET_STATUS.RETWEET).execute();
                break;
            case UNRETWEET:
                new RetweetAsyncTask(twitter,data,RetweetAsyncTask.RETWEET_STATUS.DELETE).execute();
                break;
            case HAIKURETWEET:
                new TweetAsyncTask(twitter,data.getHaikuRetweetText(),this).execute();
                break;
            case FAV:
                new FavoriteAsyncTask(twitter,data,FavoriteAsyncTask.FAVORITE_STATUS.FAVORITE).execute();
                break;
            case UNFAV:
                new FavoriteAsyncTask(twitter,data,FavoriteAsyncTask.FAVORITE_STATUS.DELETE).execute();
                break;
            case DELETE:
                new DestroyAsyncTask(twitter,data).execute();
                break;
            case USER_TIMELINE:
                replaceFragment(UserTimelineFragment.newInstance(data.getUserData()),UserTimelineFragment.class.getSimpleName() + data.getUserData().getScreenName());
                break;
            case REPLY:
                Intent intent = new Intent(getApplicationContext(),TweetPostActivity.class);
                intent.putExtra(TweetPostActivity.ARG_TWEET_DATA,data);
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    public void onUserDataDialogResult(UserData data, STATUS status) {
        switch (status){
            case USER_TIMELINE:
                replaceFragment(UserTimelineFragment.newInstance(data),UserTimelineFragment.class.getSimpleName() + data.getScreenName());
                break;
            case USER_FAVORITE:
                replaceFragment(FavoriteListFragment.newInstance(data),UserTimelineFragment.class.getSimpleName() + data.getScreenName());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
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
                if(query == null || query.equals("")){
                    Toast.makeText(getApplicationContext(),"検索文字を入力してください",Toast.LENGTH_SHORT).show();
                    return false;
                }

                String searchTag = SearchFragment.class.getSimpleName();
                SearchFragment fragment = (SearchFragment) getSupportFragmentManager()
                        .findFragmentByTag(searchTag);
                if (fragment == null) {
                    fragment = SearchFragment.newInstance(query);
                    replaceFragment(fragment, searchTag);
                    return false;
                }
                if (!fragment.getQuery().equals(query)) {
                    SearchFragment searchFragment = SearchFragment.newInstance(query);
                    replaceFragment(searchFragment,searchTag,fragment);
                    return false;
                }
                replaceFragment(fragment, searchTag);
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

        switch (id){
            case R.id.menu_help:
                break;
            case R.id.menu_setting:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                break;
            case R.id.menu_logout:
                new LogoutDialogFragment()
                        .show(getSupportFragmentManager(), LogoutDialogFragment.class.getSimpleName());
                break;
            case R.id.menu_credit:
                break;
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
                HomeTimelineFragment fragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentByTag(HomeTimelineFragment.class.getSimpleName());
                if(fragment == null){fragment = new HomeTimelineFragment();}
                replaceFragment(fragment, HomeTimelineFragment.class.getSimpleName());
                return true;

            case R.id.nav_favorite:
                String favTag = FavoriteListFragment.class.getSimpleName();
                FavoriteListFragment favoriteListFragment = (FavoriteListFragment)getSupportFragmentManager().findFragmentByTag(favTag);
                if(favoriteListFragment == null){favoriteListFragment = new FavoriteListFragment();}
                replaceFragment(favoriteListFragment, favTag);
                return true;

            case R.id.nav_tweet:
                String tweetTag = TweetFragment.class.getSimpleName();
                TweetFragment tweetFragment = (TweetFragment)getSupportFragmentManager().findFragmentByTag(tweetTag);
                if(tweetFragment == null){tweetFragment = new TweetFragment();}
                replaceFragment(tweetFragment, tweetTag);
                return true;

            case R.id.nav_search:
                String searchTag = SearchFragment.class.getSimpleName();
                SearchFragment searchFragment = (SearchFragment)getSupportFragmentManager()
                        .findFragmentByTag(searchTag);
                if(searchFragment != null){
                    replaceFragment(searchFragment, searchTag);
                    return true;}
                mSearchView.setIconified(false);
                return true;

            case R.id.nav_mention:
                String mentionTag = MentionFragment.class.getSimpleName();
                MentionFragment mentionFragment = (MentionFragment)getSupportFragmentManager().findFragmentByTag(mentionTag);
                if(mentionFragment == null){mentionFragment = new MentionFragment();}
                replaceFragment(mentionFragment, mentionTag);
                return true;

            case R.id.nav_setting:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                return true;

            case R.id.nav_logout:
                new LogoutDialogFragment()
                        .show(getSupportFragmentManager(), LogoutDialogFragment.class.getSimpleName());
                return true;
        }
        return false;
    }

}