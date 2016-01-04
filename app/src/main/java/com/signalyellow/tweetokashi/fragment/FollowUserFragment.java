package com.signalyellow.tweetokashi.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class FollowUserFragment extends Fragment
        implements AdapterView.OnItemClickListener{

    private static final String TAG = "FollowUserFragment";

    private OnFragmentResultListener mListener;

    TweetOkashiApplication mApp;
    private UserDataAdapter mAdapter;


    public FollowUserFragment() {
        // Required empty public constructor
    }

    public static FollowUserFragment newInstance() {
        FollowUserFragment fragment = new FollowUserFragment();
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

        View view =inflater.inflate(R.layout.fragment_follow_user, container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new UserDataAdapter(getActivity()));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FollowUserAsyncTask(mApp.getTwitterInstance()).execute(mApp.getUserData().getUserId());
    }

    private class FollowUserAsyncTask extends AsyncTask<Long,Void, PagableResponseList<User>>{

        Twitter mTwitter;

        public FollowUserAsyncTask(Twitter twitter) {
            mTwitter = twitter;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Long... longs) {

            Long userId = longs[0];
            Long cursor = longs.length > 1 ? longs[1] : -1;

            try{
                return mTwitter.getFriendsList(userId,cursor);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            for (User user: users){
                mAdapter.add(new UserData(user));
            }
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onItemClick");
        UserData data = (UserData)parent.getItemAtPosition(position);
        mListener.onUserItemClick(data);
    }
}
