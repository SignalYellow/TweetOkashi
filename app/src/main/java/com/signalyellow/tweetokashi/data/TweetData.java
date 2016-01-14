package com.signalyellow.tweetokashi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class TweetData implements Serializable{

    public static final String HAIKU_TAG = " #ついいとおかし @tweetokashi";

    //basic
    private String name;
    private String screenName;
    private String profileImageURL;
    private long tweetId;
    public String text;
    private Date date;

    private Status status;
    public UserData userData;

    private long rawUserId;


    //favorite
    private boolean isFavoritedByMe;
    private int favoriteCount;

    //Retweet
    private long retweetId;
    private boolean isRetweetedByMe;
    private int retweetedCount;
    private String retweetUserName;
    private boolean isRetweeted;

    //Haiku
    private String haiku;
    private boolean isHaikuRetweet = false;

    //Quote
    private TweetData quotedTweetData;
    private long quotedStatusId;
    private boolean isQuoted = false;

    //Media & URL
    private String videoURL;
    private MediaEntity[] mediaURLs;
    private PictureData pictureData;

    private URLEntity[] urlEntities;

    public TweetData(Status status){

        //dont know when the user is null
        if( status.getUser() == null){
            this.name = "";
            this.screenName = "";
            this.date = status.getCreatedAt();
            this.text = status.getText();
            return;
        }

        //this.status = status;
        this.retweetId = status.getCurrentUserRetweetId();

        this.rawUserId = status.getUser().getId();

        
        if(status.getRetweetedStatus() != null){
            this.retweetId = status.getId();
            this.retweetUserName = status.getUser().getName();
            this.isRetweeted = true;
            status = status.getRetweetedStatus();
        }

        this.tweetId = status.getId();
        User user = status.getUser();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.profileImageURL = user.getProfileImageURL();
        this.retweetedCount = status.getRetweetCount();
        this.favoriteCount = status.getFavoriteCount();
        this.date = status.getCreatedAt();
        this.isRetweetedByMe = status.isRetweetedByMe();
        this.isFavoritedByMe = status.isFavorited();
        this.mediaURLs = status.getMediaEntities();
        if(status.getMediaEntities().length > 0){
            this.pictureData = new PictureData(status.getMediaEntities()[0]);
        }

        this.urlEntities = status.getURLEntities();
        this.quotedStatusId = status.getQuotedStatusId();


        this.text =  trimText(status.getText(), this.urlEntities, this.quotedStatusId);

        this.quotedTweetData = status.getQuotedStatus() == null && !isQuoted  ? null : new TweetData(status.getQuotedStatus()).setIsQuoted(true);

        this.userData = new UserData(status.getUser());
    }

    private String trimText(String text, URLEntity[] entities, Long quotedId){
        if(quotedId == null || quotedId <= 0) return text;

        if(text.contains(HAIKU_TAG)){
            text = text.replace(HAIKU_TAG,"");
            this.isHaikuRetweet = true;
        }

        for(URLEntity entity: entities){
            if(entity.getExpandedURL().contains(quotedId.toString())){
                return text.replace(entity.getURL(),"");
            }
        }
        return text;
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

    public String getAtScreenName(){
        return "@" + screenName;
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

    public PictureData getPictureData() {
        return pictureData;
    }

    public long getRetweetId() {
        return retweetId;
    }

    public boolean isFavoritedByMe() {
        return isFavoritedByMe;
    }

    public boolean isRetweetedByMe() {
        return isRetweetedByMe;
    }

    public boolean isHaikuRetweet() {
        return isHaikuRetweet;
    }

    public long getRawUserId() {
        return rawUserId;
    }

    public void successFavorite(){
        this.isFavoritedByMe = true;
        this.favoriteCount++;
    }

    public void successCancelFavorite(){
        this.isFavoritedByMe = false;
        this.favoriteCount--;
    }

    public void successRetweet(long retweetId){
        this.isRetweetedByMe = true;
        this.retweetedCount++;
        this.retweetId = retweetId;
    }

    public void successCancelRetweet(){
        this.isRetweetedByMe = false;
        this.retweetedCount--;
        this.retweetId = 0;
    }

    public String getUrlForQuote(){
        return "https://twitter.com/" + this.screenName +"/status/" + this.tweetId;
    }

    public void setHaiku(String haiku) {
        this.haiku = haiku;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public String getRetweetUserName() {
        return retweetUserName;
    }

    public String getHaikuRetweetText(){
        return haiku + HAIKU_TAG + " " + getUrlForQuote();
    }

    public UserData getUserData() {
        return userData;
    }


}
