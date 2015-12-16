package com.signalyellow.tweetokashi.nav;

import android.content.Context;

/**
 * Created by shohei on 15/12/16.
 */
public class UnKnownActionHandler implements ItemActionHandler<Void>{
    @Override
    public boolean handle(Context context, Void entity) {
        return false;
    }
}
