package com.signalyellow.tweetokashi.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.manager.HaikuManager;
import com.signalyellow.tweetokashi.manager.LoadBitmapManager;


/**
 * Created by shohei on 15/12/14.
 * adapter which contains TweetData
 */
public class TweetDataAdapter extends ArrayAdapter<TweetData>{

    private LoadBitmapManager mLoadBitmapManager;
    private HaikuManager mHaikuManager;

    public TweetDataAdapter(Context context){
        super(context, R.layout.list_item_tweet);

        TweetOkashiApplication application = (TweetOkashiApplication)context.getApplicationContext();
        mLoadBitmapManager = application.getLoadBitmapManger();
        mHaikuManager = application.getHaikuManger();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final TweetData data = getItem(position);

        ListItemViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_tweet_2,null);
            viewHolder = new ListItemViewHolder();

            viewHolder.textUserName = (TextView)view.findViewById(R.id.name);
            viewHolder.textScreenName = (TextView)view.findViewById(R.id.screen_name);
            viewHolder.textContent = (TextView)view.findViewById(R.id.text);
            viewHolder.textHaiku = (TextView)view.findViewById(R.id.haikutext);
            viewHolder.imageThumbnail = (ImageView)view.findViewById(R.id.icon);
            viewHolder.textDate = (TextView)view.findViewById(R.id.datetime);
            viewHolder.textRetweetedCount = (TextView)view.findViewById(R.id.RTcount);
            viewHolder.textFavoritedCount = (TextView)view.findViewById(R.id.FAVCount);
            viewHolder.setQuotedTweetView((ViewGroup) view.findViewById(R.id.quoted_tweet_layout));
            viewHolder.imageGroupLayout = (ViewGroup)view.findViewById(R.id.picture_group_layout);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.imageView1);



            view.setTag(viewHolder);
        }else{
            viewHolder = (ListItemViewHolder)view.getTag();
        }

        viewHolder.textScreenName.setText("@" + data.getScreenName());
        viewHolder.textUserName.setText(data.getName());
        viewHolder.textContent.setText(data.getText());
        viewHolder.textDate.setText(TimeUtils.getRelativeTime(data.getDate()));

        setQuotedTweetData(data, viewHolder);
        setRetweetedCount(data, viewHolder);
        setFavoritedCount(data, viewHolder);
        setPictures(data, viewHolder);

        viewHolder.imageThumbnail.setTag(data.getProfileImageURL());
        mLoadBitmapManager.downloadBitmap(viewHolder.imageThumbnail, data.getProfileImageURL());

        viewHolder.textHaiku.setTag(data.getTweetId());
        mHaikuManager.createHaiku(viewHolder.textHaiku,data);

        return view;
    }

    private void setQuotedTweetData(TweetData data, ListItemViewHolder holder){

        TweetData q = data.getQuotedTweetData();
        if(q == null){
            holder.quotedTweetLayout.setVisibility(View.GONE);
        }else{
            holder.setQuotedTweet(q);
            holder.quotedTweetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setRetweetedCount(TweetData data, ListItemViewHolder holder){
        int count = data.getRetweetedCount();

        if(count > 0) {
            holder.textRetweetedCount.setText(String.valueOf(count));
            holder.textRetweetedCount.setVisibility(View.VISIBLE);
            return;
        }

        holder.textRetweetedCount.setVisibility(View.GONE);
    }

    private void setFavoritedCount(TweetData data, ListItemViewHolder holder){
        int count = data.getFavoriteCount();

        if(count > 0){
            holder.textFavoritedCount.setText(String.valueOf(count));
            holder.textFavoritedCount.setVisibility(View.VISIBLE);
            return;
        }

        holder.textFavoritedCount.setVisibility(View.GONE);
    }

    private void setPictures(TweetData data, ListItemViewHolder holder){
        if(data.getMediaURLs() == null || data.getMediaURLs().length == 0){
            holder.imageView.setVisibility(View.GONE);
            return;
        }

        holder.imageView.setVisibility(View.VISIBLE);
        String[] urls  = data.getMediaURLs();

        holder.imageView.setTag(urls[0]);
        mLoadBitmapManager.downloadBitmap(holder.imageView,urls[0]);


    }

}
