package com.signalyellow.tweetokashi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.TweetData;

import jp.signalyellow.haiku.HaikuGeneratorByGooAPI;


public class HaikuRegenerateDialogFragment extends DialogFragment {

    private static final String TAG = HaikuRegenerateDialogFragment.class.getSimpleName();
    private static final String ARG_TWEET_DATA = "tweetData";

    private TweetData mData;
    private TweetOkashiApplication mApp;
    private HaikuGeneratorByGooAPI mGenerator;


    public HaikuRegenerateDialogFragment() {
        // Required empty public constructor
    }

    public static HaikuRegenerateDialogFragment newInstance(TweetData data) {
        HaikuRegenerateDialogFragment fragment = new HaikuRegenerateDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();
        if (getArguments() != null) {
            mData = (TweetData)getArguments().getSerializable(ARG_TWEET_DATA);
            if(mData != null)
            mGenerator = new HaikuGeneratorByGooAPI(mApp.getHaikuManger().getMorpholizedWordList(mData.getTweetId()));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


}
