package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.activity.HomeTimelineActivity;

class HomeActionHandler implements ItemActionHandler<Void>{
    @Override
    public boolean handle(Context context, Void entity) {
        context.startActivity(new Intent(context, HomeTimelineActivity.class));
        return true;
    }
}
