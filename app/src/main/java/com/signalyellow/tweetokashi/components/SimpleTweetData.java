package com.signalyellow.tweetokashi.components;

import java.io.Serializable;

import twitter4j.Status;

/**
 * Created by shohei on 15/08/14.
 */
public class SimpleTweetData implements Serializable {
    String text;
    String userName;
    String userScreenName;
    String haiku;
    long tweetId;

    public SimpleTweetData(HaikuStatus haikuStatus){
        Status status = haikuStatus.getStatus();
        this.text = status.getText();
        this.userName = status.getUser().getName();
        this.userScreenName = status.getUser().getScreenName();
        this.tweetId = status.getId();

    }

    public String getText() {
        return text;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserScreenName() {
        return userScreenName;
    }
    public long getTweetId() {
        return tweetId;
    }
    public String getHaiku() {
        return haiku;
    }
    public String getURL(){
        return "https://twitter.com/" + userScreenName +"/status/" + tweetId;
    }
}
