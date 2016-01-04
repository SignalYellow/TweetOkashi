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
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.data.UserDataAdapter;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollListener;
import com.signalyellow.tweetokashi.listener.AutoUpdateTimelineScrollable;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FollowerFragment extends Fragment implements AutoUpdateTimelineScrollable,SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener{
    private static final String TAG = "FollowerFrag";

    private static final String ARG_USER_DATA = "USER_DATA";
    private UserData mUserData;

    private UserDataAdapter mAdapter;
    private TweetOkashiApplication mApp;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private boolean mIsScrollable = true;

    private long mCursor;


    private OnFragmentResultListener mListener;

    public FollowerFragment() {
        // Required empty public constructor
    }

    public static FollowerFragment newInstance(UserData data) {
        FollowerFragment fragment = new FollowerFragment();
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
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home_timeline, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new UserDataAdapter(getActivity()));
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new AutoUpdateTimelineScrollListener(this));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FollowerAsyncTask(mApp.getTwitterInstance(),mUserData).execute();
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
    public void onRefresh() {
        setRefreshing(false);
    }

    @Override
    public void scrolled() {
        if(!mIsScrollable || mCursor == 0) return;

        new FollowerAsyncTask(mApp.getTwitterInstance(),mUserData,mCursor).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserData data = (UserData)parent.getItemAtPosition(position);
        mListener.onUserItemClick(data);
    }

    private class FollowerAsyncTask extends AsyncTask<Void,Void, PagableResponseList<User>> {
        Twitter mTwitter;
        UserData mUserData;
        long cursor;


        public FollowerAsyncTask(Twitter twitter,UserData userData) {
            this(twitter,userData,-1);
        }

        public FollowerAsyncTask(Twitter twitter,UserData userData,long cursor){
            mTwitter = twitter;
            mUserData = userData;
            this.cursor = cursor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected PagableResponseList<User> doInBackground(Void... voids) {

            try{
                return mTwitter.getFollowersList(mUserData.getUserId(),cursor);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);

            if(users != null) {
                if (cursor == -1) mAdapter.clear();
                if (users.size() == 0) mIsScrollable = false;

                mCursor = users.getNextCursor();
                for (User user : users) {
                    mAdapter.add(new UserData(user));
                }
                Log.d("onPost", mCursor + "");
            }else{
                Log.e(TAG,"null!");
            }
            setRefreshing(false);
        }
    }
}
