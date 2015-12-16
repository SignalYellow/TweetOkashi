package com.signalyellow.tweetokashi.twitter;

import android.content.Context;

import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.sub.TweetData;
import com.signalyellow.tweetokashi.sub.TweetDataAdapter;

import twitter4j.AsyncTwitter;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;

/**
 * Created by shohei on 15/12/16.
 */
public class TwitterApi {

    public TwitterApi() {

    }

    public void getHomeTimeline(Context context, final TweetDataAdapter adapter){
        AsyncTwitter twitter = TwitterUtils.getAsyncTwitterInstance(context);
        twitter.addListener(new TwitterAdapter(){
            @Override
            public void gotHomeTimeline(ResponseList<Status> statuses) {
                 for(Status s:statuses){
                    adapter.add(new TweetData(s));
                }
            }
        });

    }
}
