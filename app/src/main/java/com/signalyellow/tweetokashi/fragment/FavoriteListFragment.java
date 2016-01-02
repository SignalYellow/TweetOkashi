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
import android.widget.FrameLayout;
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


public class FavoriteListFragment extends Fragment
implements SwipeRefreshLayout.OnRefreshListener,AutoUpdateTimelineScrollable,AdapterView.OnItemClickListener{

    private static final String TAG = "FavoriteListFra";

    private static final String ARG_USER_DATA = "USER_DATA";
    private UserData mUserData;

    private TweetDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TweetOkashiApplication mApp;
    private boolean mIsRefreshing = false;
    protected boolean mIsScrollable = true;
    private Twitter mTwitter;

    private OnFragmentResultListener mListener;

    public FavoriteListFragment() {
        // Required empty public constructor
    }


    public static FavoriteListFragment newInstance(UserData data) {
        FavoriteListFragment fragment = new FavoriteListFragment();
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
        mTwitter = mApp.getTwitterInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout root = new FrameLayout(getActivity());
        View view = inflater.inflate(R.layout.fragment_home_timeline,root);

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
        if (context instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeTimelineFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new FavoriteListAsyncTask().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        new FavoriteListAsyncTask(paging).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TweetData data = (TweetData)adapterView.getItemAtPosition(position);
        mListener.onTimelineItemClick(data);
    }

    @Override
    public void onRefresh() {
        new FavoriteListAsyncTask().execute();
    }

    private class FavoriteListAsyncTask extends AsyncTask<Void,Void, ResponseList<Status>> {
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
            }catch (Exception e){
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            Log.d(TAG,"debug");
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


}
