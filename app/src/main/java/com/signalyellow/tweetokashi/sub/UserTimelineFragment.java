package com.signalyellow.tweetokashi.sub;

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
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class UserTimelineFragment extends Fragment
        implements AutoUpdateTimelineScrollable,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener{

    private static final String ARG_USER_DATA = "USER_DATA";
    private static final String TAG = "UserTimeline";
    private UserData mUserData;
    TweetOkashiApplication mApp;
    Twitter mTwitter;
    TweetDataAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    boolean mIsRefreshing = false;

    private OnUserTimelineFragmentListener mListener;

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
        if (getArguments() != null) {
            mUserData = (UserData)getArguments().getSerializable(ARG_USER_DATA);
        }
        mApp= (TweetOkashiApplication)getActivity().getApplicationContext();
        mTwitter = TwitterUtils.getTwitterInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color, android.R.color.holo_orange_dark);
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
        if (context instanceof OnUserTimelineFragmentListener) {
            mListener = (OnUserTimelineFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new UserTimelineAsyncTask().execute();
    }

    private class UserTimelineAsyncTask extends AsyncTask<Void,Void, ResponseList<Status>>{
        Paging mPaging;

        public UserTimelineAsyncTask() {
            mPaging = null;
        }

        public UserTimelineAsyncTask(Paging paging) {
            mPaging = paging;
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
                if(mPaging == null) mAdapter.clear();
                for(twitter4j.Status s : statuses){
                    mAdapter.add(new TweetData(s));
                }
            }
            setRefreshing(false);
        }
    }


    @Override
    public void onRefresh() {
        new UserTimelineAsyncTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TweetData data = (TweetData)parent.getItemAtPosition(position);
        mListener.onUserTimelineItemClicked(data);
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
        paging.setMaxId(lastData.getTweetId() -1);
        new UserTimelineAsyncTask(paging).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUserTimelineFragmentListener {
        void onUserTimelineItemClicked(TweetData data);
    }
}
