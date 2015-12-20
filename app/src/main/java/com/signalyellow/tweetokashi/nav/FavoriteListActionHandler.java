package com.signalyellow.tweetokashi.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.sub.FavoriteListActivity;

/**
 * Created by shohei on 15/12/20.
 */
public class FavoriteListActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context, Void entity) {
        context.startActivity(new Intent(context, FavoriteListActivity.class));
        return true;
    }
}