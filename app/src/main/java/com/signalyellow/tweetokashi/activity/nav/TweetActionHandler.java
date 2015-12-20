package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.sub.TweetPostActivity;

/**
 * Created by shohei on 15/12/16.
 */
public class TweetActionHandler implements ItemActionHandler<Void>{
    @Override
    public boolean handle(Context context, Void entity) {
        Intent intent = new Intent(context, TweetPostActivity.class);
        context.startActivity(intent);
        return true;
    }
}