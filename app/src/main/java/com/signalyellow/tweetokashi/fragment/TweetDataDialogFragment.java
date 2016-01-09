package com.signalyellow.tweetokashi.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.DialogItemAdapter;
import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;


public class TweetDataDialogFragment extends DialogFragment {

    private static final String TAG = TweetDataDialogFragment.class.getSimpleName();
    private static final String ARG_TWEET_DATA = "tweetData";

    private TweetData mData;
    private TweetOkashiApplication mApp;

    private OnFragmentResultListener mListener;

    public TweetDataDialogFragment() {
        // Required empty public constructor
    }

    public static TweetDataDialogFragment newInstance(TweetData data) {
        TweetDataDialogFragment fragment = new TweetDataDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = (TweetData)getArguments().getSerializable(ARG_TWEET_DATA);
        }
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_tweet_data_dialog, new FrameLayout(getActivity().getApplicationContext()), false);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        DialogItemAdapter mAdapter = new DialogItemAdapter(getActivity().getApplicationContext());
        listView.setAdapter(mAdapter);
        setDataToAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                STATUS item = (STATUS) parent.getItemAtPosition(position);
                mListener.onTweetDataDialogResult(mData, item);
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle(mData.getName());
        builder.setView(view);
        return  builder.create();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setDataToAdapter(DialogItemAdapter adapter){

        if(mApp.doesMakeHaiku() && mData.getHaiku() != null && !mData.getHaiku().equals(mApp.getHaikuManger().MAKE_NO_HAIKU_MESSAGE)){
            adapter.add(STATUS.HAIKURETWEET);
        }
        adapter.add(STATUS.REPLY);

        if(mData.isFavoritedByMe()){
            adapter.add(STATUS.UNFAV);
        }else {
            adapter.add(STATUS.FAV);
        }

        if(mData.getRawUserId() == mApp.getUserData().getUserId()){
            // my tweet
            if (mData.isRetweeted()){
                adapter.add(STATUS.UNRETWEET);
            }else {
                adapter.add(STATUS.DELETE);
            }
        }else{
            // not my tweet
            if(mData.isRetweetedByMe()){
                adapter.add(STATUS.UNRETWEET);
            }else {
                adapter.add(STATUS.RETWEET);
            }
        }

        adapter.add(STATUS.USER_TIMELINE);
    }
}
