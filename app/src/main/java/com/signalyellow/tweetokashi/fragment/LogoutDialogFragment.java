package com.signalyellow.tweetokashi.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.signalyellow.tweetokashi.activity.MainActivity;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;


public class LogoutDialogFragment extends DialogFragment {

    TweetOkashiApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ログアウトしますか？")
                .setPositiveButton("　はい　", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mApp.logout();
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));

                    }
                })
                .setNegativeButton("　いいえ　",null);

        return builder.create();
    }
}
