package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;
import com.signalyellow.tweetokashi.keys.Key;


import java.util.List;

import jp.signalyellow.haiku.*;
import twitter4j.Twitter;

public class HaikuRegenerateActivity extends Activity {

    static final String TAG = "HaikuRegenerate";

    HaikuGeneratorByGooAPI generator;

    SimpleTweetData data;

    Twitter mTwitter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haiku_regenerate);


        mTwitter = TwitterUtils.getTwitterInstance(this);

        data = (SimpleTweetData)getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_TWEETDATA);
        prepareRegenerate();
    }



    private void prepareRegenerate(){

        android.app.ActionBar bar = getActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setSubtitle(R.string.regenerate_activity_subtitle);
        }

        final TextView haikuTextView = (TextView)findViewById(R.id.haiku_result);

        haikuTextView.setText(data.getHaiku());
        TextView originalText = (TextView)findViewById(R.id.text);
        originalText.setText(data.getText());
        TextView nameText = (TextView)findViewById(R.id.name);
        nameText.setText(data.getUserName());
        TextView screenName = (TextView)findViewById(R.id.screen_name);
        screenName.setText("@" + data.getUserScreenName());
        SmartImageView icon = (SmartImageView)findViewById(R.id.icon);
        icon.setImageUrl(data.getImageURL());
        TextView date = (TextView)findViewById(R.id.datetime);
        date.setText(data.getDate());

        new AsyncMorphologicalAnalysis().execute(originalText.getText().toString());


        Button regenerateButton = (Button)findViewById(R.id.haiku_regenerate_button);
        regenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generator != null)
                haikuTextView.setText(data.setHaiku(generator.generate()));
            }
        });

        Button haikuRetweetButton = (Button)findViewById(R.id.haiku_retweet_button);
        haikuRetweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHaikuRetweet();
            }
        });
    }

    private void doHaikuRetweet(){
    }

    /**
     * 形態素解析後HaikuGeneratorの生成
     */
    public class AsyncMorphologicalAnalysis extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            List<Word> temp;
            try {
                MorphologicalAnalysisByGooAPI analyzor = new MorphologicalAnalysisByGooAPI(Key.getGooId());
                temp = analyzor.analyze(params[0]);
                Log.d(TAG,"done analysis");
            }catch (Exception e){
                Log.d(TAG, e.toString());
                return null;
            }

            generator = new HaikuGeneratorByGooAPI(temp);
            return generator.generate();

        }

        @Override
        protected void onPostExecute(String haiku) {
            if(haiku == null){
                showToast(getString(R.string.error_normal));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}
