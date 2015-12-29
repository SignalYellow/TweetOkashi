package com.signalyellow.tweetokashi.data;

import android.graphics.Bitmap;

import java.io.Serializable;

import twitter4j.User;

/**
 * Created by shohei on 15/12/23.
 */
public class UserData implements Serializable {
    private long userId;
    private String userName;
    private String screenName;
    private String profileImageURL;
    private Bitmap bitmap;
    private String description;

    public UserData(User user) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.screenName = user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.description = user.getDescription();
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

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
