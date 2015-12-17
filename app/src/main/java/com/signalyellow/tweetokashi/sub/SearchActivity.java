package com.signalyellow.tweetokashi.sub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ListView;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.TweetDataAdapter;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollCheckable;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.nav.NavigationItemAction;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AutoUpdateTimelineScrollCheckable, SwipeRefreshLayout.OnRefreshListener{

    static final String TAG = "SearchActivity";

    private Twitter mTwitter;
    private TweetDataAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private String mQueryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwitter = TwitterUtils.getTwitterInstance(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
    public void onRefresh() {
        if(mQueryString == null){
            Toast.makeText(getApplicationContext(),"検索ワードを入力してください",Toast.LENGTH_SHORT).show();
            setRefreshing(false);
            return;
        }
        new SearchAsyncTask(mQueryString).execute();
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
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        new SearchAsyncTask(mQueryString,lastData.getTweetId() - 1).execute();
    }

    private class SearchAsyncTask extends AsyncTask<Void,Void,QueryResult> {

        Long mMaxId;
        Query mQuery;

        public SearchAsyncTask(String query){
            mMaxId = null;
            mQuery = new Query(query);
        }

        public SearchAsyncTask(String query, Long maxId) {
            mMaxId = maxId;
            mQuery = new Query(query);
            mQuery.setMaxId(maxId);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected QueryResult doInBackground(Void... voids) {

            try {
                return mMaxId == null ? mTwitter.search(mQuery) : mTwitter.search(mQuery);
            } catch (TwitterException e) {
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryResult result) {

            if(result != null){
                if(mMaxId == null) mAdapter.clear();

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
        getMenuInflater().inflate(R.menu.menu_with_search, menu);
        SearchView  view = (SearchView)menu.findItem(R.id.menu_search).getActionView();
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchAsyncTask(mQueryString = query).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"text");
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
