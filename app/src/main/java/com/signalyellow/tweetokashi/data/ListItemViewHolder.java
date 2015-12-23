package com.signalyellow.tweetokashi.data;

import android.media.Image;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;


/**
 * Created by shohei on 15/12/14.
 */
class ListItemViewHolder {
    public TextView textUserName;
    public TextView textScreenName;
    public TextView textDate;
    public TextView textContent;
    public TextView textRetweetedCount;
    public TextView textFavoritedCount;
    public TextView textHaiku;
    public ImageView imageThumbnail;
    public ViewGroup quotedTweetLayout;
    public TextView textQuotedUserName;
    public TextView textQuotedScreenName;
    public TextView textQuotedDate;
    public TextView textQuotedText;
    public ViewGroup imageGroupLayout;
    public ImageView imageView;



    public ViewGroup setQuotedTweetView(ViewGroup viewGroup){
        this.quotedTweetLayout = viewGroup;
        this.textQuotedUserName = (TextView)viewGroup.findViewById(R.id.quoted_user_name);
        this.textQuotedScreenName = (TextView)viewGroup.findViewById(R.id.quoted_screen_name);
        this.textQuotedDate = (TextView)viewGroup.findViewById(R.id.quoted_date);
        this.textQuotedText = (TextView)viewGroup.findViewById(R.id.quoted_text);

        return viewGroup;
    }

    public void setQuotedTweet(TweetData data){
        this.textQuotedText.setText(data.getText());
        this.textQuotedDate.setText(TimeUtils.getRelativeTime(data.getDate()));
        this.textQuotedUserName.setText(data.getName());
        this.textQuotedScreenName.setText("@" + data.getScreenName());
    }


}
