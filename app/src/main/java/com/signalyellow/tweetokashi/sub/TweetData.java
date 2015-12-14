package com.signalyellow.tweetokashi.sub;

import java.io.Serializable;
import java.util.Date;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by shohei on 15/11/21.
 */
public class TweetData implements Serializable{
    String name;
    String screenName;
    String profileImageURL;
    String text;
    int reTweetedCount;
    int favoriteCount;
    Date date;

    String haiku;

    public TweetData(Status status, String haiku){
        User user = status.getUser();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.text = status.getText();
        this.reTweetedCount = status.getRetweetCount();
        this.favoriteCount = status.getFavoriteCount();
        this.date = status.getCreatedAt();

        this.haiku = haiku;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getText() {
        return text;
    }

    public int getReTweetedCount() {
        return reTweetedCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public String getHaiku() {
        return haiku;
    }

    public Date getDate() {
        return date;
    }
}
