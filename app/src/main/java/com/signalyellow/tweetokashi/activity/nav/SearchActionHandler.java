package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.activity.SearchActivity;

/**
 * Created by shohei on 15/12/17.
 */
public class SearchActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context, Void entity) {
        context.startActivity(new Intent(context,SearchActivity.class));
        return true;
    }
}
