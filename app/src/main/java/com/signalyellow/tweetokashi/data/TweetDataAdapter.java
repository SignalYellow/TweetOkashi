package com.signalyellow.tweetokashi.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;

import twitter4j.MediaEntity;


/**
 * Created by shohei on 15/12/14.
 * adapter which contains TweetData
 */
public class TweetDataAdapter extends ArrayAdapter<TweetData>{

    private TweetOkashiApplication mApp;

    public TweetDataAdapter(Context context){
        super(context, R.layout.item_tweet);
        mApp = (TweetOkashiApplication)context.getApplicationContext();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final TweetData data = getItem(position);

        TweetDataViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            RelativeLayout root = new RelativeLayout(getContext());
            view = inflater.inflate(R.layout.item_tweet,root);
            viewHolder = new TweetDataViewHolder();

            viewHolder.textUserName = (TextView)view.findViewById(R.id.name);
            viewHolder.textScreenName = (TextView)view.findViewById(R.id.screen_name);
            viewHolder.textContent = (TextView)view.findViewById(R.id.text);
            viewHolder.textHaiku = (TextView)view.findViewById(R.id.haiku_text);
            viewHolder.imageThumbnail = (SmartImageView)view.findViewById(R.id.icon);
            viewHolder.textDate = (TextView)view.findViewById(R.id.datetime);
            viewHolder.setQuotedTweetView((ViewGroup) view.findViewById(R.id.quoted_tweet_layout));
            viewHolder.imageGroupLayout = (ViewGroup)view.findViewById(R.id.picture_group_layout);
            viewHolder.imageView = (SmartImageView)view.findViewById(R.id.imageView1);
            viewHolder.setRTbyView((ViewGroup)view.findViewById(R.id.RTbyTextGroup));
            viewHolder.setRTViewGroup((ViewGroup) view.findViewById(R.id.RTGroup));
            viewHolder.setFAVViewGroup((ViewGroup)view.findViewById(R.id.FavGroup));

            view.setTag(viewHolder);
        }else{
            viewHolder = (TweetDataViewHolder)view.getTag();
        }

        viewHolder.textScreenName.setText(data.getAtScreenName());
        viewHolder.textUserName.setText(data.getName());
        viewHolder.textContent.setText(data.getText());
        viewHolder.textDate.setText(TimeUtils.getRelativeTime(data.getDate()));

        setQuotedTweetData(data, viewHolder);
        setRetweetedCount(data, viewHolder);
        setFavoritedCount(data, viewHolder);
        setPictures(data, viewHolder);
        setHaiku(data, viewHolder);
        setRetweetedByText(data, viewHolder);

        viewHolder.imageThumbnail.setImageUrl(data.getProfileImageURL());

        return view;
    }

    private void setQuotedTweetData(TweetData data, TweetDataViewHolder holder){

        TweetData q = data.getQuotedTweetData();
        if(q == null){
            holder.quotedTweetLayout.setVisibility(View.GONE);
        }else{
            holder.textQuotedText.setText(q.getText());
            holder.textQuotedDate.setText(TimeUtils.getRelativeTime(q.getDate()));
            holder.textQuotedUserName.setText(q.getName());
            holder.textQuotedScreenName.setText(q.getAtScreenName());
            holder.quotedTweetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setRetweetedByText(TweetData data, TweetDataViewHolder holder){

        if(data.isRetweeted()){
            holder.RTbyTextGroup.setVisibility(View.VISIBLE);
            holder.textRTbyUserName.setText(data.getRetweetUserName());
            return;
        }
        holder.RTbyTextGroup.setVisibility(View.GONE);

    }

    private void setRetweetedCount(TweetData data, TweetDataViewHolder holder){
        int count = data.getRetweetedCount();

        if(count > 0) {
            holder.RTViewGroup.setVisibility(View.VISIBLE);
            if(data.isRetweetedByMe()){

                holder.textRetweetTitle.setText(R.string.title_retweet_count_done);
                holder.textRetweetTitle.setTextColor(Color.RED);
            }else{
                holder.textRetweetTitle.setText(R.string.title_retweet_count);
                holder.textRetweetTitle.setTextColor(Color.GRAY);
            }
            holder.textRetweetedCount.setText(String.valueOf(count));
            return;
        }

        holder.RTViewGroup.setVisibility(View.GONE);
    }

    private void setFavoritedCount(TweetData data, TweetDataViewHolder holder){
        int count = data.getFavoriteCount();

        if( count > 0 ){
            holder.FAVViewGroup.setVisibility(View.VISIBLE);
            if(data.isFavoritedByMe()) {
                holder.textFavoritedTitle.setText(R.string.title_fav_count_done);
                holder.textFavoritedTitle.setTextColor(Color.RED);
            }else {
                holder.textFavoritedTitle.setText(R.string.title_fav_count);
                holder.textFavoritedTitle.setTextColor(Color.GRAY);
            }
            holder.textFavoritedCount.setText(String.valueOf(count));
            return;
        }

        holder.FAVViewGroup.setVisibility(View.GONE);
    }

    private void setPictures(TweetData data, TweetDataViewHolder holder){
        if(data.getMediaURLs() == null || data.getMediaURLs().length == 0){
            holder.imageView.setVisibility(View.GONE);
            return;
        }

        holder.imageView.setVisibility(View.VISIBLE);

        MediaEntity[] entities  = data.getMediaURLs();
        MediaEntity entity = entities[0];
        holder.imageView.setImageUrl(entity.getMediaURL());
    }

    private void setHaiku(TweetData data, TweetDataViewHolder holder){

        if(!mApp.doesMakeHaiku() || data.isHaikuRetweet()){
            holder.textHaiku.setVisibility(View.GONE);
            return;
        }
        holder.textHaiku.setTag(data.getTweetId());
        mApp.getHaikuManger().createHaiku(holder.textHaiku,data);

    }
}
