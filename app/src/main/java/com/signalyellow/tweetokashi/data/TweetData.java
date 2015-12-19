package com.signalyellow.tweetokashi.data;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by shohei on 15/11/21.
 */
public class TweetData implements Serializable{

    private long userId;
    private String name;
    private String screenName;
    private String profileImageURL;

    private long tweetId;
    private String text;
    private int retweetedCount;
    private boolean isRetweeted;
    private int favoriteCount;
    private Date date;
    private TweetData quotedTweetData;
    private String videoURL;
    private MediaEntity[] mediaURLs;

    private boolean isDeletable;
    private boolean isRetweetable;

    private boolean isQuoted = false;

    String haiku;

    public TweetData(Status status){

        if(status.getRetweetedStatus() != null){
            status = status.getRetweetedStatus();
        }
        User user = status.getUser();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.retweetedCount = status.getRetweetCount();
        this.favoriteCount = status.getFavoriteCount();
        this.date = status.getCreatedAt();
        this.tweetId = status.getId();
        this.isRetweeted = status.isRetweeted();
        this.text =  status.getText();
        this.quotedTweetData = status.getQuotedStatus() == null && !isQuoted  ? null : new TweetData(status.getQuotedStatus()).setIsQuoted(true);
        this.isRetweetable = !status.isRetweetedByMe();
        this.mediaURLs = status.getMediaEntities();
    }

    public TweetData setIsQuoted(boolean isQuoted) {
        this.isQuoted = isQuoted;
        return this;
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

    public TweetData getQuotedTweetData() {
        return quotedTweetData;
    }

    public MediaEntity[] getMediaURLs() {
        return mediaURLs;
    }
}
