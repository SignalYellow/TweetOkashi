package com.signalyellow.tweetokashi.manager;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by shohei on 15/12/15.
 */
class LoadBitmapItem {

    private ImageView mImageView;
    private String mUrl;
    private Bitmap mBitmap;

    public void setImgView(ImageView imageView) {
        mImageView = imageView;
    }

    public ImageView getImgView() {
        return mImageView;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

}

