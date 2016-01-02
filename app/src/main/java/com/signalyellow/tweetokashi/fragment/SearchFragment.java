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

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class SearchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,AutoUpdateTimelineScrollable, AdapterView.OnItemClickListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final String ARG_QUERY = "Query";
    private String mQuery;

    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TweetOkashiApplication mApp;
    private boolean mIsRefreshing = false;
    private boolean mIsScrollable = true;

    private OnTimelineFragmentListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String searchQuery) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY,searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_QUERY);
        }

        mApp= (TweetOkashiApplication)getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ListView mListView = (ListView)view.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter == null ? mAdapter = new TweetDataAdapter(getActivity().getApplicationContext()) : mAdapter);
        mListView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this));
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null && mAdapter.getCount() == 0 ) {
            mIsScrollable = true;
            new SearchAsyncTask(mQuery).execute();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimelineFragmentListener) {
            mListener = (OnTimelineFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimelineFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        mIsScrollable = true;
        new SearchAsyncTask(mQuery).execute();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TweetData data = (TweetData)adapterView.getItemAtPosition(position);
        mListener.onTimelineItemClick(data);
    }

    @Override
    public void scrolled() {
        if(!mIsScrollable) return;

        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        long maxId = lastData.getTweetId() - 1;
        new SearchAsyncTask(mQuery,maxId).execute();
    }

    public String getQuery() {
        return mQuery;
    }

    private class SearchAsyncTask extends AsyncTask<Void,Void,QueryResult> {

        Long mMaxId;
        Query mQuery;
        Twitter mTwitter;

        public SearchAsyncTask(String query){
            this(query,null);
        }

        public SearchAsyncTask(String query, Long maxId) {
            mMaxId = maxId;
            mQuery = new Query(query);
            if(maxId != null) mQuery.setMaxId(maxId);
            mTwitter = mApp.getTwitterInstance();
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected QueryResult doInBackground(Void... voids) {

            try {
                return mTwitter.search(mQuery);
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryResult result) {

            if(result != null){
                if(result.getTweets().size() <= 0) mIsScrollable = false;
                if(mMaxId == null) mAdapter.clear();

                for(twitter4j.Status s : result.getTweets()){
                    mAdapter.add(new TweetData(s));
                }
                setRefreshing(false);
            }
        }
    }


}
