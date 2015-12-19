package com.signalyellow.tweetokashi.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.activity.HomeTimelineActivity;

/**
 * Created by shohei on 15/12/19.
 */
public class HomeActionHandler implements ItemActionHandler<Void>{
    @Override
    public boolean handle(Context context, Void entity) {
        context.startActivity(new Intent(context, HomeTimelineActivity.class));
        return true;
    }
}
