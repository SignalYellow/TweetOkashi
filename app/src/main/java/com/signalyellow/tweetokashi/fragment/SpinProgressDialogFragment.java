package com.signalyellow.tweetokashi.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by shohei on 16/01/14.
 */
public class SpinProgressDialogFragment extends DialogFragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_MESSAGE = "message";
    String mTitle;
    String mMessage;

    public static SpinProgressDialogFragment newInstance(String title, String message) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);
        args.putString(ARG_MESSAGE,message);
        SpinProgressDialogFragment fragment = new SpinProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mTitle = getArguments().getString(ARG_TITLE);
            mMessage = getArguments().getString(ARG_MESSAGE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        if(mTitle != null ) dialog.setTitle(mTitle);
        if(mMessage != null) dialog.setMessage(mMessage);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);
        return dialog;
    }
}
