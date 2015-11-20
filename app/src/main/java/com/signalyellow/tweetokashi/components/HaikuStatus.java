package com.signalyellow.tweetokashi.components;

import java.util.Date;

import twitter4j.Status;

/**
 * Created by shohei on 15/08/12.
 */
public class HaikuStatus {
    String haikuText;
    Status status;

    public HaikuStatus(String haikuText, Status status) {
        this.haikuText = haikuText;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getUserName() {
        if (status.getRetweetedStatus() != null) {
            return status.getRetweetedStatus().getUser().getName();
        } else {
            return status.getUser().getName();
        }
    }

    public String getScreenName() {
        if (status.getRetweetedStatus() != null) {
            return status.getRetweetedStatus().getUser().getScreenName();
        } else {
            return status.getUser().getScreenName();
        }
    }

    public String getText(){

        if(status.getRetweetedStatus() != null){
            return status.getText().replaceFirst("RT", "");
        }
        return status.getText();
    }

    public String getProfileImageURL(){
        if(status.getRetweetedStatus() != null){
            return status.getRetweetedStatus().getUser().getProfileImageURL();
        }else{
            return status.getUser().getProfileImageURL();
        }
    }

    public Date getCreatedAt(){
        if(status.getRetweetedStatus() != null){
            return status.getRetweetedStatus().getCreatedAt();
        }else {
            return status.getCreatedAt();
        }
    }

    public boolean isRetweetedByMe(){
        if(status.getRetweetedStatus() != null) {
            return status.getRetweetedStatus().isRetweetedByMe();
        }
        return false;
    }

    public boolean isRetweet(){
        if(status.getRetweetedStatus() != null){
            return true;
        }
        return false;
    }

    public int getRetweetCount(){
        return status.getRetweetCount();
    }

    public int getFavoriteCount(){
        if(status.getRetweetedStatus() != null){
            return status.getRetweetedStatus().getFavoriteCount();
        }

        return status.getFavoriteCount();
    }
    public long getId(){
        return status.getId();
    }
    public String getHaikuText() {
        return haikuText;
    }

}
