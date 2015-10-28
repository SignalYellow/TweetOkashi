package com.signalyellow.tweetokashi.components;

import twitter4j.User;

/**
 * Created by shohei on 15/08/22.
 */
public class HaikuUserStatus {
    String haikuText;
    User user;

    public HaikuUserStatus(String haikuText, User user){
        this.haikuText = haikuText;
        this.user = user;
    }

    public String getHaikuText() {
        return haikuText;
    }

    public User getUser() {
        return user;
    }
}
