package com.signalyellow.tweetokashi.sub;

import java.io.Serializable;
import java.util.Date;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by shohei on 15/11/21.
 */
public class TweetData implements Serializable{
    long tweetId;
    String name;
    String screenName;
    String profileImageURL;
    String text;
    int retweetedCount;
    boolean isRetweeted;
    int favoriteCount;
    Date date;

    String haiku;



    public TweetData(Status status){
        User user = status.getUser();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.retweetedCount = status.getRetweetCount();
        this.favoriteCount = status.getFavoriteCount();
        this.date = status.getCreatedAt();
        this.tweetId = status.getId();
        this.isRetweeted = status.isRetweeted();
        this.text = status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT",""): status.getText();

    }

    public long getTweetId() {
        return tweetId;
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

    public int getRetweetedCount() {
        return retweetedCount;
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
