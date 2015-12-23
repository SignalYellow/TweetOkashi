package com.signalyellow.tweetokashi.data;

import java.io.Serializable;
import java.util.Date;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class TweetData implements Serializable{

    private static final String HAIKU_TAG = " @tweetokashi #ついいとおかし ";

    private String name;
    private String screenName;
    private String profileImageURL;

    private long tweetId;
    public String text;
    private int retweetedCount;
    private boolean isRetweeted;
    private int favoriteCount;
    private Date date;
    private TweetData quotedTweetData;
    private long quotedStatusId;
    private String videoURL;
    private MediaEntity[] mediaURLs;
    private URLEntity[] urlEntities;
    private Status status;

    private long retweetId;

    private boolean isFavoritedByMe;
    private boolean isRetweetedByMe;
    private boolean isHaikuRetweet = false;

    private boolean isQuoted = false;

    private String haiku;

    public TweetData(Status status){

        this.status = status;
        this.retweetId = status.getCurrentUserRetweetId();
        if(status.getRetweetedStatus() != null){
            this.retweetId = status.getId();
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
        this.isRetweeted = status.isRetweeted();
        this.isRetweetedByMe = status.isRetweetedByMe();
        this.isFavoritedByMe = status.isFavorited();
        this.mediaURLs = status.getMediaEntities();
        this.urlEntities = status.getURLEntities();
        this.quotedStatusId = status.getQuotedStatusId();


        this.text =  trimText(status.getText(),this.urlEntities,this.quotedStatusId);

        this.quotedTweetData = status.getQuotedStatus() == null && !isQuoted  ? null : new TweetData(status.getQuotedStatus()).setIsQuoted(true);

    }

    private String trimText(String text, URLEntity[] entities, Long quotedId){
        if(quotedId == null || quotedId <= 0) return text;

        if(text.contains(HAIKU_TAG)){
            this.text = text.replace(HAIKU_TAG,"");
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

    public String getHaikuRetweetText(){
        return haiku + HAIKU_TAG + getUrlForQuote();
    }
}
