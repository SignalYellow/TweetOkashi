package com.signalyellow.tweetokashi.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.DialogItem;
import com.signalyellow.tweetokashi.data.DialogItemAdapter;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;


public class TweetDataDialogFragment extends DialogFragment {

    private static final String TAG = "TweetDataDialogFragment";
    private static final String ARG_TWEET_DATA = "tweetData";

    private TweetData mData;
    private TweetOkashiApplication mApp;
    private DialogItemAdapter mAdapter;

    private DIALOG_STATUS[] statuses_list;

    enum DIALOG_STATUS{
        REPLY,
        RETWEET,
        UNRETWEET,
        HAIKU_RETWEET,
        FAVORITE,
        UNFAVORITE,
        DELETE,


    }

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

        CharSequence[] items = {"リツイート","いいね","俳句リツイート","詳細"};
        if(mData.isRetweetedByMe()){
            items[0] = "リツイート解除";
        }
        if(mData.isFavoritedByMe()){
            items[1] = "いいね取り消し";
        }

        final Twitter twitter = TwitterUtils.getTwitterInstance(getActivity());

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                builder
                .setTitle(mData.getName());
        ListView listView = new ListView(getActivity().getApplicationContext());

        mAdapter = new DialogItemAdapter(getActivity().getApplicationContext());


        setDataToAdapter(mAdapter);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogItem item = (DialogItem) parent.getItemAtPosition(position);
                Log.d(TAG, position + item.getText());

            }
        });


        builder.setView(listView);


        return  builder.create();
    }

    private void setDataToAdapter(DialogItemAdapter adapter){


        if(mData.getRawUserId() == mApp.getUserData().getUserId()){
            // my tweet
            adapter.add(new DialogItem(DialogItem.STATUS.DELETE));

        }else{
            // not my tweet
            if(mData.isRetweetedByMe()){
                adapter.add(new DialogItem(DialogItem.STATUS.UNRETWEET));
            }else {
                adapter.add(new DialogItem(DialogItem.STATUS.RETWEET));
            }
        }

        if(mData.isFavoritedByMe()){
            adapter.add(new DialogItem(DialogItem.STATUS.UNFAV));
        }else {
            adapter.add(new DialogItem(DialogItem.STATUS.FAV));
        }
        adapter.add(new DialogItem(DialogItem.STATUS.REPLY));
        adapter.add(new DialogItem(DialogItem.STATUS.DETAIL));



    }


}
