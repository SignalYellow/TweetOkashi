package com.signalyellow.tweetokashi.activity.nav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.activity.SettingsActivity;

/**
 * Created by shohei on 15/12/19.
 */
class SettingsActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context, Void entity) {
        context.startActivity(new Intent(context, SettingsActivity.class));
        return true;
    }
}
