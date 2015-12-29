package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.activity.TweetPostActivity;

/**
 * Created by shohei on 15/12/16.
 */
class TweetActionHandler implements ItemActionHandler<Void>{
    @Override
    public boolean handle(Context context, Void entity) {
        Intent intent = new Intent(context, TweetPostActivity.class);
        context.startActivity(intent);
        return true;
    }
}
