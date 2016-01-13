package com.signalyellow.tweetokashi.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.data.DialogItemAdapter;
import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.TweetData;

import jp.signalyellow.haiku.HaikuGeneratorByGooAPI;


public class HaikuRegenerateDialogFragment extends DialogFragment {

    private static final String TAG = HaikuRegenerateDialogFragment.class.getSimpleName();
    private static final String ARG_TWEET_DATA = "tweetData";

    private TweetData mData;
    private TweetOkashiApplication mApp;
    private HaikuGeneratorByGooAPI mGenerator;
    private String mHaiku;


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


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_haiku_regenerate, new FrameLayout(getActivity().getApplicationContext()), false);

        final TextView haikuTextView = (TextView)view.findViewById(R.id.haiku_text);
        haikuTextView.setText(mData.getHaiku());
        SmartImageView imageView = (SmartImageView)view.findViewById(R.id.icon);
        imageView.setImageUrl(mData.getProfileImageURL());
        TextView textViewText = (TextView)view.findViewById(R.id.text);
        TextView screenNameText = (TextView)view.findViewById(R.id.screen_name);
        TextView nameText = (TextView)view.findViewById(R.id.name);
        String t = mData.getText();
        textViewText.setText(t.replace("\n", " "));
        screenNameText.setText(mData.getAtScreenName());
        nameText.setText(mData.getName());

        Button haikuRegenerateButton = (Button)view.findViewById(R.id.haiku_regenerate_button);
        Button haikuRetweetButton = (Button)view.findViewById(R.id.haiku_retweet_button);

        haikuRegenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haikuTextView.setText(mHaiku = mGenerator.generate());
                mData.setHaiku(mHaiku);

            }
        });

        haikuRetweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"haikuretweet");
            }
        });


        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach" + mHaiku);
        if(mHaiku != null)
        mApp.getHaikuManger().setHaikuCache(mData.getTweetId(),mHaiku);
        //mListener = null;
    }



}
