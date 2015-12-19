package com.signalyellow.tweetokashi.nav;


import android.view.MenuItem;

import com.signalyellow.tweetokashi.R;

/**
 * Created by shohei on 15/12/16.
 * Actions for navigation menu
 */
public enum  NavigationItemAction
{
    HOME(R.id.nav_home,new HomeActionHandler()),
    TWEET(R.id.nav_tweet,new TweetActionHandler()),
    SEARCH(R.id.nav_search,new SearchActionHandler()),
    SETTING(R.id.nav_setting, new SettingsActionHandler()),
    UNKNOWN(-1,new UnKnownActionHandler());

    private final int mMenuId;
    private final ItemActionHandler mHandler;

    NavigationItemAction(final int menuId, final ItemActionHandler handler) {
        mMenuId = menuId;
        mHandler = handler;
    }

    public static NavigationItemAction valueOf(MenuItem item){
        for (NavigationItemAction action : values()) {
            if (action.getMenuId() == item.getItemId()) {
                return action;
            }
        }
        return UNKNOWN;
    }

    public int getMenuId() {
        return mMenuId;
    }

    public ItemActionHandler getHandler() {
        return mHandler;
    }
}
