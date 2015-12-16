package com.signalyellow.tweetokashi.sub;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.keys.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.signalyellow.haiku.HaikuGeneratorByGooAPI;
import jp.signalyellow.haiku.MorphologicalAnalysisByGooAPI;
import jp.signalyellow.haiku.Word;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by shohei on 15/11/22.
 *
 */
public class TimelineTweetsLoader extends AsyncTaskLoader<List<TweetData>> {

    Context mContext;
    Twitter mTwitter;
    boolean canCreateHaiku=true;


    public TimelineTweetsLoader(Context context, Twitter twitter){
        super(context);
        this.mContext = context;
        mTwitter = twitter;
    }

    @Override
    public List<TweetData> loadInBackground() {
        ResponseList<Status> timeline;
        List<TweetData> dataList = new ArrayList<>();
        MorphologicalAnalysisByGooAPI analyzer =
                new MorphologicalAnalysisByGooAPI(Key.getGooId());

        try {
            timeline = mTwitter.getHomeTimeline();
            for(Status status:timeline){
                String haiku="";
                if(canCreateHaiku) {
                    try {
                        List<Word> list = analyzer
                                .analyze(status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT", "") : status.getText());

                        haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                    } catch (IOException e_1) {
                        continue;
                    }
                }
                dataList.add(new TweetData(status));
            }
            return dataList;
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }


}
