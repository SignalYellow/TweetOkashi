package com.signalyellow.tweetokashi.data;

import java.io.Serializable;

import twitter4j.MediaEntity;


public class PictureData implements Serializable{
    private String url;
    private int sizeX;
    private int sizeY;

    public PictureData(MediaEntity entity) {
        url = entity.getMediaURL();
        sizeX = entity.getSizes().get(2).getWidth();
        sizeY = entity.getSizes().get(2).getHeight();
    }

    public String getUrl() {
        return url;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}