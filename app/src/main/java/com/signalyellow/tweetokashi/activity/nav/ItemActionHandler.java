package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;

/**
 * Created by shohei on 15/12/16.
 */
public interface ItemActionHandler<E> {
    boolean handle(Context context, E entity);
}
