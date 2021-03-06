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
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollable;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class MentionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,AutoUpdateTimelineScrollable, AdapterView.OnItemClickListener {

    private static final String TAG = MentionFragment.class.getSimpleName();

    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false;
    private boolean mIsScrollable = true;
    private Twitter mTwitter;

    private OnFragmentResultListener mListener;

    public MentionFragment() {
        //empty constrasctor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TweetOkashiApplication app= (TweetOkashiApplication)getActivity().getApplicationContext();
        mTwitter = app.getTwitterInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_orange_dark);
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
        mIsScrollable = true;
        if (mAdapter != null && mAdapter.getCount() == 0 ) {

            new MentionAsyncTask(mTwitter).execute();
        }
        mListener.onFragmentStart("あなたへのツイート");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) context;
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
        new MentionAsyncTask(mTwitter);
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
        Paging paging = new Paging();
        TweetData lastData = mAdapter.getItem(mAdapter.getCount()-1);
        paging.setMaxId(lastData.getTweetId() -1);
        new MentionAsyncTask(mTwitter,paging).execute();
    }


    private class MentionAsyncTask extends AsyncTask<Void,Void, ResponseList<Status>> {

        Twitter mTwitter;
        Paging mPaging;

        public MentionAsyncTask(Twitter twitter) {
            this(twitter,null);
        }

        public MentionAsyncTask(Twitter twitter, Paging paging){
            mTwitter = twitter;
            mPaging = paging;
        }



        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... voids) {

            try {
                return mPaging == null ? mTwitter.getMentionsTimeline() : mTwitter.getMentionsTimeline(mPaging);
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> result) {

            if(result != null){
                if(result.size() <= 0){
                    mIsScrollable = false;
                    mListener.onResult(getString(R.string.end_of_list));
                }

                for(twitter4j.Status s : result){
                    mAdapter.add(new TweetData(s));
                }
            }else {
                mIsScrollable = false;
                mListener.onResult(getString(R.string.error_twitter_exception));
            }
            setRefreshing(false);
        }
    }


}
