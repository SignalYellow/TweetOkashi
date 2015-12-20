package com.signalyellow.tweetokashi.activity.nav;


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
    FAVORITE_LIST(R.id.nav_favorite, new FavoriteListActionHandler()),
    SETTING(R.id.nav_setting, new SettingsActionHandler()),
    LOGOUT(R.id.nav_logout, new LogoutActionHandler()),
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