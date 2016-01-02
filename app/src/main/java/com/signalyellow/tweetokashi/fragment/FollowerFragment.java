package com.signalyellow.tweetokashi.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.data.UserDataAdapter;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FollowerFragment extends Fragment {
    private static final String TAG = "FollowerFrag";
    private UserDataAdapter mAdapter;
    private TweetOkashiApplication mApp;



    private OnFragmentResultListener mListener;

    public FollowerFragment() {
        // Required empty public constructor
    }


    public static FollowerFragment newInstance() {
        FollowerFragment fragment = new FollowerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow_user, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new UserDataAdapter(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FollowerAsyncTask(mApp.getTwitterInstance()).execute(mApp.getUserData().getUserId());
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

    private class FollowerAsyncTask extends AsyncTask<Long,Void, PagableResponseList<User>> {
        Twitter mTwitter;

        public FollowerAsyncTask(Twitter twitter){
            mTwitter = twitter;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Long... longs) {
            Long userId = longs[0];
            Long cursor = longs.length > 1 ? longs[1] : -1;

            try{
                return mTwitter.getFollowersList(userId,cursor);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);

            for(User user: users){
                mAdapter.add(new UserData(user));
            }
        }
    }



}
