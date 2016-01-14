package com.signalyellow.tweetokashi.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

public class DeleteDialogFragment extends DialogFragment{

    static OnFragmentResultListener mListener;
    TweetOkashiApplication mApp;
    TweetData mData;

    private static final String ARG_TWEET_DATA="TWEET_DATA";

    public static DeleteDialogFragment newInstance(TweetData data) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET_DATA, data);
        DeleteDialogFragment fragment = new DeleteDialogFragment();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnTimelineFragmentListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ツイートを削除しますか？")
                .setMessage(mData.getText().replace("\n"," "))
                .setPositiveButton("　はい　", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onTweetDataDialogResult(mData,STATUS.DELETE_DONE);
                    }
                })
                .setNegativeButton("　いいえ　", null);

        return builder.create();
    }

}
