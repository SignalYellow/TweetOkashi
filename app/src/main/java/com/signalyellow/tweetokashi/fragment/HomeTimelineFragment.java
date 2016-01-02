package com.signalyellow.tweetokashi.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.data.TweetDataAdapter;
import com.signalyellow.tweetokashi.fragment.listener.OnTimelineFragmentListener;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollable;
import com.signalyellow.tweetokashi.sub.UiHandler;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.*;


public class HomeTimelineFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,AutoUpdateTimelineScrollable, AdapterView.OnItemClickListener{

    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    private Twitter mTwitter;
    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TwitterStream mStream;
    private TweetOkashiApplication mApp;
    private boolean mIsRefreshing = false;
    protected boolean mIsScrollable = true;

    private OnTimelineFragmentListener mListener;

    public HomeTimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        mApp= (TweetOkashiApplication)getActivity().getApplicationContext();
        mTwitter = TwitterUtils.getTwitterInstance(getActivity());

        if(mStream == null) {
            mStream = TwitterUtils.getTwitterStreamInstance(getActivity());
            mStream.addListener(new MyUserStreamAdapter());
            mStream.user();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_timeline, container, false);
        Log.d(TAG,"onCreateView");
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ListView mListView = (ListView)view.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter == null ? mAdapter = new TweetDataAdapter(getActivity()) : mAdapter);
        mListView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this));
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnTimelineFragmentListener) {
            mListener = (OnTimelineFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeTimelineFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if(mAdapter != null && mAdapter.getCount() == 0)
        new TimelineAsyncTask().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        mListener = null;
        if(mStream != null) mStream.shutdown();
    }

    @Override
    public void onRefresh() {
        mIsScrollable = true;
        new TimelineAsyncTask().execute();
    }

    private class TimelineAsyncTask extends AsyncTask<Void,Void,ResponseList<Status>> {

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
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {

            if(statuses != null){
                if(statuses.size() == 0) mIsScrollable = false;
                if(mPaging == null) mAdapter.clear();

                for(twitter4j.Status s : statuses){
                    mAdapter.add(new TweetData(s));
                }
            }
            setRefreshing(false);
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
        if(!mIsScrollable) return;

        Paging paging = new Paging();
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        paging.setMaxId(lastData.getTweetId() -1);
        new TimelineAsyncTask(paging).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TweetData data = (TweetData)adapterView.getItemAtPosition(position);
        mListener.onTimelineItemClicked(data);
    }

    class MyUserStreamAdapter extends UserStreamAdapter {
        @Override
        public void onStatus(final Status status) {
            super.onStatus(status);
            Log.d(TAG, status.getUser().getName() + " " + status.getText());
            if(status.getRetweetedStatus() != null  && mApp.getUserData() != null
                    && mApp.getUserData().getUserId() == status.getUser().getId()){
                return;
            }
            new UiHandler(){
                @Override
                public void run() {
                    mAdapter.insert(new TweetData(status), 0);
                }
            }.post();

        }
    }


}
