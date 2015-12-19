package com.signalyellow.tweetokashi.activity;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ListView;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.sub.UiHandler;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollCheckable;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.nav.NavigationItemAction;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.TweetDataAdapter;
import com.signalyellow.tweetokashi.sub.TweetPostActivity;

import twitter4j.*;

public class HomeTimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AutoUpdateTimelineScrollCheckable {

    private static final String TAG = "HomeTimeline";

    private Twitter mTwitter;
    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TwitterStream mStream;
    private TweetOkashiApplication app;

    private boolean mIsRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getString(R.string.app_name_ja));

        app= (TweetOkashiApplication)getApplicationContext();
        mTwitter = TwitterUtils.getTwitterInstance(this);



        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TweetOkashiApplication app = (TweetOkashiApplication) getApplicationContext();
                app.getHaikuManger().refresh();
                new TimelineAsyncTask().execute();
            }
        });


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


            ImageView imageView = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_image);
            TextView textView1 = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_main_text);
            TextView textView2 = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_sub_text);

            new UserAsyncTask(imageView,textView1,textView2,getApplicationContext()).execute();


        ListView mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(mAdapter = new TweetDataAdapter(getApplicationContext()));
        mListView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this, mAdapter));




        new TimelineAsyncTask().execute();

        mStream = TwitterUtils.getTwitterStreamInstance(getApplicationContext());
        mStream.addListener(new MyUserStreamAdapter());
        mStream.user();
    }



    class UserAsyncTask extends AsyncTask<Void,Void, User>{
        private ImageView mImageView;
        private TextView mMainTextView;
        private TextView mSubTextView;
        private TweetOkashiApplication mApp;

        public UserAsyncTask(ImageView imageView, TextView mainTextView, TextView subTextView, Context context) {
            mImageView = imageView;
            mMainTextView = mainTextView;
            mSubTextView = subTextView;
            mApp = (TweetOkashiApplication)context;
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
            if(user == null){
                return;
            }

            mMainTextView.setText(user.getName());
            mSubTextView.setText("@" + user.getScreenName());
            Log.d(TAG, user.getProfileBackgroundColor() + " " + user.getProfileSidebarFillColor() + " " + user.getProfileTextColor());

            mImageView.setTag(user.getProfileImageURL());
            mApp.getLoadBitmapManger().downloadBitmap(mImageView,user.getProfileImageURL());



        }
    }



    private class TimelineAsyncTask extends AsyncTask<Void,Void,ResponseList<twitter4j.Status>>{

        Paging mPaging;

        public TimelineAsyncTask(){
            mPaging = null;
        }

        public TimelineAsyncTask(Paging paging) {
            mPaging = paging;
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... voids) {

            try {
                return mPaging == null ? mTwitter.getHomeTimeline() : mTwitter.getHomeTimeline(mPaging);
            } catch (TwitterException e) {
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {

            if(statuses != null){
                if(mPaging == null) mAdapter.clear();
                for(twitter4j.Status s : statuses){
                    mAdapter.add(new TweetData(s));
                }
            }
            setRefreshing(false);
        }
    }


    class MyUserStreamAdapter extends UserStreamAdapter{
        @Override
        public void onStatus(final Status status) {
            super.onStatus(status);
            Log.d(TAG, status.getUser().getName() + " " + status.getText());
            new UiHandler(){
                @Override
                public void run() {
                    mAdapter.insert(new TweetData(status), 0);

                }
            }.post();

        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mIsRefreshing = refreshing;
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void scrolled() {
        Log.d(TAG, "scrolled");

        Paging paging = new Paging();
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        paging.setMaxId(lastData.getTweetId() -1);
        new TimelineAsyncTask(paging).execute();

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
        return action.getHandler().handle(this,null);
    }
}
