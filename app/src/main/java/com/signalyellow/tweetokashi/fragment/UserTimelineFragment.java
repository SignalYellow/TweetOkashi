package com.signalyellow.tweetokashi.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollable;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;


import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class UserTimelineFragment extends Fragment
        implements AutoUpdateTimelineScrollable,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener{

    private static final String TAG = UserTimelineFragment.class.getSimpleName();

    private static final String ARG_USER_DATA = "USER_DATA";
    private UserData mUserData;

    private TweetOkashiApplication mApp;
    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private boolean mIsScrollable = true;

    private OnFragmentResultListener mListener;

    public UserTimelineFragment() {
        // Required empty public constructor
    }


    public static UserTimelineFragment newInstance(UserData data) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_DATA,data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mUserData = (UserData)getArguments().getSerializable(ARG_USER_DATA);
        }
        mApp= (TweetOkashiApplication)getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ListView mListView = (ListView)view.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter = new TweetDataAdapter(getActivity()));
        mListView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this));
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null && mAdapter.getCount() == 0) {
            new UserTimelineAsyncTask(mApp.getTwitterInstance()).execute();
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
        new UserTimelineAsyncTask(mApp.getTwitterInstance()).execute();
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
        paging.setMaxId(lastData.getTweetId() - 1);
        new UserTimelineAsyncTask(mApp.getTwitterInstance(), paging).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TweetData data = (TweetData)parent.getItemAtPosition(position);
        mListener.onTimelineItemClick(data);
    }

    private class UserTimelineAsyncTask extends AsyncTask<Void,Void,ResponseList<Status>>{
        Paging mPaging;
        Twitter mTwitter;

        public UserTimelineAsyncTask(Twitter twitter) {
            this(twitter,null);
        }

        public UserTimelineAsyncTask(Twitter twitter, Paging paging) {
            mPaging = paging;
            mTwitter = twitter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
            try {
                return mPaging == null ? mTwitter.getUserTimeline(mUserData.getUserId())
                        : mTwitter.getUserTimeline(mUserData.getUserId(),mPaging);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            if(statuses != null){
                if(statuses.size() == 0) mIsRefreshing = false;
                if(mPaging == null) mAdapter.clear();
                for(twitter4j.Status s : statuses){
                    mAdapter.add(new TweetData(s));
                }
            }else{
                mIsScrollable = false;
                mListener.onResult(getString(R.string.error_twitter_exception));
            }
            setRefreshing(false);
        }
    }


}
