package com.signalyellow.tweetokashi.manager;


import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;
import com.signalyellow.tweetokashi.sub.TweetData;
import java.util.List;
import jp.signalyellow.haiku.HaikuGeneratorByGooAPI;
import jp.signalyellow.haiku.MorphologicalAnalysisByGooAPI;
import jp.signalyellow.haiku.Word;

/**
 * Created by shohei on 15/12/15.
 * Manager class which manipulate and memorize haiku data
 */
public class HaikuManager {

    static final int MAX_HAIKU = 1000; //count of max memorized haiku
    static final String TAG = "HaikuManager";

    private String makeNoHaikuMessage = "できませんでした"; // message when manager can't make a haiku.

    MorphologicalAnalysisByGooAPI mAnalyzer;
    LruCache<Long,String> mCache;


    public HaikuManager(String gooId){
        mCache = new LruCache<>(MAX_HAIKU);
        mAnalyzer = new MorphologicalAnalysisByGooAPI(gooId);
    }

    public void refresh(){
        mCache = new LruCache<>(MAX_HAIKU);
    }

    public void createHaiku(TextView textView,TweetData data){
        String haiku = mCache.get(data.getTweetId());

        if(haiku == null){
            new HaikuAsyncTask(textView,data.getTweetId()).execute(data.getText());
            return;
        }

        if(haiku.equals(makeNoHaikuMessage)){
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(haiku);
    }

    class HaikuAsyncTask extends AsyncTask<String,Void,String>{

        long id;
        TextView mTextView;

        public HaikuAsyncTask(TextView textView,Long id){
            mCache.put(this.id = id, "");
            mTextView = textView;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                List<Word> list = mAnalyzer.analyze(strings[0]);
                return new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null){
                mCache.put(id,makeNoHaikuMessage);
                mTextView.setVisibility(View.GONE);
                return;
            }else {
                mCache.put(id, s);
                mTextView.setVisibility(View.VISIBLE);
            }
            mTextView.setText(mCache.get(id));
        }
    }


}
