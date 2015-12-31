package com.signalyellow.tweetokashi.activity.nav;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.activity.FavoriteListActivity;
import com.signalyellow.tweetokashi.fragment.FavoriteListFragment;
import com.signalyellow.tweetokashi.fragment.FollowerFragment;

class FavoriteListActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context, Void entity) {
        return true;
    }
}
