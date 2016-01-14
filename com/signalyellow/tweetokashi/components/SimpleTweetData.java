package com.signalyellow.tweetokashi.components;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by shohei on 15/08/14.
 */
public class SimpleTweetData implements Serializable {
    String text;
    String userName;
    String userScreenName;
    String haiku;
    String imageURL;
    Date date;
    long tweetId;

    public SimpleTweetData(HaikuStatus status){
        this.text = status.getText();
        this.userName = status.getUserName();
        this.userScreenName = status.getScreenName();
        this.tweetId = status.getId();
        this.haiku = status.getHaikuText();
        this.imageURL = status.getProfileImageURL();
        this.date = status.getCreatedAt();

    }

    public SimpleTweetData(HaikuUserStatus status){
        this.haiku = status.getHaikuText();
        User user = status.getUser();
        this.imageURL = user.getProfileImageURL();
        this.userScreenName = user.getScreenName();
        this.userName = user.getName();
    }

    public String getDate() {
        return getDate(this.date);
    }
    public static String getDate(Date date){
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    public String setHaiku(String haiku) {
        return this.haiku = haiku;
    }

    public String getImageURL() {
        return imageURL;
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
    public static String getURL(Status status){
        return "https://twitter.com/" + status.getUser().getScreenName() +"/status/" + status.getId();
    }


}
