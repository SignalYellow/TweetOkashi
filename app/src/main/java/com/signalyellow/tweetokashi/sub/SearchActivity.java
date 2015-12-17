package com.signalyellow.tweetokashi.sub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.TweetDataAdapter;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollCheckable;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.nav.NavigationItemAction;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AutoUpdateTimelineScrollCheckable {

    static final String TAG = "SearchActivity";

    private Twitter mTwitter;
    private TweetDataAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mTwitter = TwitterUtils.getTwitterInstance(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TweetOkashiApplication app = (TweetOkashiApplication) getApplicationContext();
                app.getHaikuManger().refresh();
                new SearchAsyncTask("京都").execute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ListView mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(mAdapter = new TweetDataAdapter(getApplicationContext()));
        mListView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this, mAdapter));
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
        Paging paging = new Paging();
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        paging.setMaxId(lastData.getTweetId() - 1);

    }

    private class SearchAsyncTask extends AsyncTask<Void,Void,QueryResult> {

        Paging mPaging;
        Query mQuery;

        public SearchAsyncTask(String query){
            mPaging = null;
            mQuery = new Query(query);
        }

        public SearchAsyncTask(String query,Paging paging) {
            mPaging = paging;
            mQuery = new Query(query);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected QueryResult doInBackground(Void... voids) {

            try {
                return mPaging == null ? mTwitter.search(mQuery) : mTwitter.search(mQuery);
            } catch (TwitterException e) {
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryResult result) {

            if(result != null){
                if(mPaging == null) mAdapter.clear();

                for(twitter4j.Status s : result.getTweets()){
                    mAdapter.add(new TweetData(s));
                }
                setRefreshing(false);
            }
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
