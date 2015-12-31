package com.signalyellow.tweetokashi.activity.nav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.signalyellow.tweetokashi.activity.MainActivity;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

class LogoutActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context, Void entity ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("確認　--　ログアウト")
                .setCancelable(true)
                .setMessage("ログアウトします")
                .setPositiveButton("Yes", new PositiveButtonListener(context))
                .setNegativeButton("Cancel", new NegativeButtonListener(context));
        builder.create().show();


        return true;
    }

    class PositiveButtonListener implements DialogInterface.OnClickListener{

        private Context context;

        public PositiveButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            TwitterUtils.deleteAccessToken(context);
            Toast.makeText(context,"ログアウトしました",Toast.LENGTH_SHORT).show();


            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

    class NegativeButtonListener implements DialogInterface.OnClickListener{
        private Context context;

        public NegativeButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    }
}
