package com.signalyellow.tweetokashi.data;

import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;

class TweetDataViewHolder {
    //BASE
    public TextView textUserName;
    public TextView textScreenName;
    public TextView textDate;
    public TextView textContent;
    public SmartImageView imageThumbnail;

    //RT by
    public ViewGroup RTbyTextGroup;
    public TextView textRTbyUserName;
    //RT
    public ViewGroup RTViewGroup;
    public TextView textRetweetTitle;
    public TextView textRetweetedCount;
    //FAV
    public ViewGroup FAVViewGroup;
    public TextView textFavoritedTitle;
    public TextView textFavoritedCount;
    //Haiku
    public TextView textHaiku;

    //Quote
    public ViewGroup quotedTweetLayout;
    public TextView textQuotedUserName;
    public TextView textQuotedScreenName;
    public TextView textQuotedDate;
    public TextView textQuotedText;

    //Image
    public ViewGroup imageGroupLayout;
    public SmartImageView imageView;


    public void setRTbyView(ViewGroup viewGroup){
        this.RTbyTextGroup = viewGroup;
        this.textRTbyUserName = (TextView)viewGroup.findViewById(R.id.RTbyUserNameText);
    }

    public void setRTViewGroup(ViewGroup viewGroup){
        this.RTViewGroup = viewGroup;
        this.textRetweetTitle = (TextView)viewGroup.findViewById(R.id.RTtitle);
        this.textRetweetedCount = (TextView)viewGroup.findViewById(R.id.RTcount);
    }

    public void setFAVViewGroup(ViewGroup viewGroup){
        this.FAVViewGroup = viewGroup;
        this.textFavoritedTitle = (TextView)viewGroup.findViewById(R.id.FAVtitle);
        this.textFavoritedCount = (TextView)viewGroup.findViewById(R.id.FAVcount);
    }

    public ViewGroup setQuotedTweetView(ViewGroup viewGroup){
        this.quotedTweetLayout = viewGroup;
        this.textQuotedUserName = (TextView)viewGroup.findViewById(R.id.quoted_user_name);
        this.textQuotedScreenName = (TextView)viewGroup.findViewById(R.id.quoted_screen_name);
        this.textQuotedDate = (TextView)viewGroup.findViewById(R.id.quoted_date);
        this.textQuotedText = (TextView)viewGroup.findViewById(R.id.quoted_text);

        return viewGroup;
    }

}
