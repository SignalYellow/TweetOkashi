package com.signalyellow.tweetokashi.components;

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


    public String getHaikuText() {
        return haikuText;
    }

}
