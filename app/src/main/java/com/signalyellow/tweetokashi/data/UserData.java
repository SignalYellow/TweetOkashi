package com.signalyellow.tweetokashi.data;


import java.io.Serializable;

import twitter4j.User;


public class UserData implements Serializable {
    private long userId;
    private String userName;
    private String screenName;
    private String profileImageURL;
    private int tweetCount;
    private int followCount;
    private int followerCount;
    private String description;

    public UserData(User user) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.screenName =  user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.description = user.getDescription();
        this.tweetCount = user.getStatusesCount();
        this.followCount = user.getFriendsCount();
        this.followerCount = user.getFollowersCount();
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAtScreenName(){return "@" + screenName;}

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getDescription() {
        return description;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

}
