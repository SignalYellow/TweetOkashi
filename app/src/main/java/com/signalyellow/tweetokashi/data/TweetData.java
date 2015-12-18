package com.signalyellow.tweetokashi.data;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

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
    private String[] mediaURLs;

    private boolean isDeletable;
    private boolean isRetweetable;


    String haiku;



    public TweetData(Status status){
        if(status.getRetweetedStatus() != null){
            Status s = status.getRetweetedStatus();
            User user = s.getUser();
            this.name = user.getName();
            this.screenName = user.getScreenName();
            this.profileImageURL = user.getProfileImageURL();
            this.retweetedCount = s.getRetweetCount();
            this.favoriteCount = s.getFavoriteCount();
            this.date = s.getCreatedAt();
            this.tweetId = s.getId();
            this.isRetweeted = s.isRetweeted();
            this.text = s.getText();
            this.quotedTweetData = s.getQuotedStatus() == null ? null : new TweetData(s.getQuotedStatus());
            this.isRetweetable = !s.isRetweetedByMe();
        }else {
            User user = status.getUser();
            this.name = user.getName();
            this.screenName = user.getScreenName();
            this.profileImageURL = user.getProfileImageURL();
            this.retweetedCount = status.getRetweetCount();
            this.favoriteCount = status.getFavoriteCount();
            this.date = status.getCreatedAt();
            this.tweetId = status.getId();
            this.isRetweeted = status.isRetweeted();
            this.text = /*status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT", "") :*/ status.getText();
            this.quotedTweetData = status.getQuotedStatus() == null ? null : new TweetData(status.getQuotedStatus());
            this.isRetweetable = !status.isRetweetedByMe();
        }

        Log.d("TweetData",isRetweetable + "retweet");


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
}
