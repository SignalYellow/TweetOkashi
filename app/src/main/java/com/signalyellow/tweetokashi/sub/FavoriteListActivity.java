package com.signalyellow.tweetokashi.sub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.TweetDataAdapter;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollable;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.nav.NavigationItemAction;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class FavoriteListActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener,AutoUpdateTimelineScrollable, SwipeRefreshLayout.OnRefreshListener{

    private final String TAG = "FavoriteListActivity";

    private Twitter mTwitter;
    private TweetDataAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false;
    private boolean mIsScrollable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwitter = TwitterUtils.getTwitterInstance(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new TweetDataAdapter(this));
        listView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this, mAdapter));

        new FavoriteListAsyncTask().execute();
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
        if(!mIsScrollable) return;

        Paging paging = new Paging();
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        paging.setMaxId(lastData.getTweetId()-1);
        new FavoriteListAsyncTask(paging).execute();
    }

    @Override
    public void onRefresh() {
        new FavoriteListAsyncTask().execute();
    }

    private class FavoriteListAsyncTask extends AsyncTask<Void,Void, ResponseList<Status>>{
        Paging mPaging;

        public FavoriteListAsyncTask(){
            mPaging = null;
        }
        public FavoriteListAsyncTask(Paging paging) {
            this.mPaging = paging;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... voids) {
            try{
                return mPaging == null ? mTwitter.getFavorites() : mTwitter.getFavorites(mPaging);
            }catch (TwitterException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            if(statuses != null){
                if(statuses.size() == 0) mIsScrollable = false;
                if(mPaging == null ) mAdapter.clear();

                for(twitter4j.Status status:statuses){
                    mAdapter.add(new TweetData(status));
                }
            }
            setRefreshing(false);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavigationItemAction action = NavigationItemAction.valueOf(item);
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return action.getHandler().handle(this,null);
    }
}
