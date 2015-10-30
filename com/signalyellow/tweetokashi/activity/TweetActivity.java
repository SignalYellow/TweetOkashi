package com.signalyellow.tweetokashi.activity;
import jp.signalyellow.haiku.*;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.components.TwitterUtils.TWITTER_STATUS;

import org.w3c.dom.Text;

import java.util.List;


public class TweetActivity extends Activity  {

    static final String TAG = "TweetActivity";

    boolean hasCreateHaiku;

    Twitter mTwitter;
    User mUser;

    EditText inputEditText;
    TextView haikuResultText;
    HaikuGeneratorByGooAPI generator;
    String previousInput="A";

    Button haikuTweetButton;
    Button tweetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        android.app.ActionBar bar = getActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setSubtitle(R.string.tweet_activity_subtitle);
        }

        mTwitter = TwitterUtils.getTwitterInstance(this);
        mUser = (User)getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_USERDATA);
        prepareForTweet();

    }

    private void prepareForTweet(){
        if(mUser != null) {
            TextView userName = (TextView) findViewById(R.id.name);
            userName.setText(mUser.getName());
            SmartImageView imageView = (SmartImageView) findViewById(R.id.icon);
            imageView.setImageUrl(mUser.getProfileImageURL());
            TextView screenName = (TextView) findViewById(R.id.screen_name);
            screenName.setText("@" + mUser.getScreenName());
        }


        haikuTweetButton = (Button)findViewById(R.id.haiku_tweet);
        haikuResultText = (TextView)findViewById(R.id.haiku_result);
        final Button haikuGenerateButton = (Button)findViewById(R.id.haiku_button);
        tweetButton = (Button)findViewById(R.id.tweet_button);
        tweetButton.setEnabled(false);
        inputEditText = (EditText)findViewById(R.id.tweetEditText);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView = (TextView) findViewById(R.id.tweet_text_count);
                Integer length = s.length();
                textView.setText(length.toString());

                haikuTweetButton.setEnabled(true);
                tweetButton.setEnabled(true);

                if (length == 0 || length > 140) {
                    tweetButton.setEnabled(false);
                    haikuTweetButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tweetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputEditText.getText().toString();

                if (text.length() > 140) {
                    showToastShort(getString(R.string.error_too_long_text) + ":現在" + text.length() + "字");
                    return;
                }
                new TweetAsyncTask().execute(text);
            }
        });

        haikuGenerateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = inputEditText.getText().toString();
                if (temp.equals(previousInput)) {
                    haikuResultText.setText(generator.generate());
                    return;
                }

                new AsyncMorphologicalAnalysis().execute(previousInput = temp);
            }
        });
    }

    /**
     * 俳句生成後に利用可能なボタンの準備
     */
    private void prepareSubButtons(){

        final Button haikuOnly = (Button)findViewById(R.id.haiku_only_tweet);
        haikuOnly.setVisibility(View.VISIBLE);
        haikuOnly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TweetAsyncTask().execute(haikuResultText.getText().toString() + " " + getString(R.string.retweet_tag));
            }
        });

        Button haikuTweetButton = (Button)findViewById(R.id.haiku_tweet);
        haikuTweetButton.setVisibility(View.VISIBLE);
        haikuTweetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new HaikuTweetTask(haikuResultText.getText().toString(), inputEditText.getText().toString()).execute();

            }
        });
    }


    /**
     * 一度ツイートしてからそのツイートを包んで俳句をツイート(俳句リツイート)
     */
    public class HaikuTweetTask extends AsyncTask<Void,Void,TWITTER_STATUS>{
        String haiku;
        String text;

        public HaikuTweetTask(String haiku, String text){
            this.haiku = haiku;
            this.text = text;
        }

        @Override
        protected TWITTER_STATUS doInBackground(Void... params) {
            try {
                twitter4j.Status s = mTwitter.updateStatus(text);

                mTwitter.updateStatus(haiku + " " + getString(R.string.retweet_tag) + "\n" + SimpleTweetData.getURL(s));
                return TWITTER_STATUS.SUCCESS;
            } catch (TwitterException e) {
                return TWITTER_STATUS.ERROR;
            }
        }

        @Override
        protected void onPostExecute(TWITTER_STATUS twitter_status) {
            switch (twitter_status){
                case SUCCESS:
                    showToastShort(getString(R.string.success_tweet));
                    return;
                case ERROR:
                    showToastShort(getString(R.string.error_normal));
                    break;
            }
        }
    }


    /**
     * 形態素解析
     */
    public class AsyncMorphologicalAnalysis extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            List<Word> temp;
            try {
                MorphologicalAnalysisByGooAPI analyzor = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));
                temp = analyzor.analyze(params[0]);
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
                showToastShort(getString(R.string.error_normal));
                return;
            }
            if(!hasCreateHaiku){
                prepareSubButtons();
            }

            hasCreateHaiku = true;
            haikuResultText.setText(haiku);
        }
    }


    /**
     * ツイート処理
     */
    private class TweetAsyncTask extends AsyncTask<String,Void,TWITTER_STATUS>{
        @Override
        protected TWITTER_STATUS doInBackground(String... params) {
            try {
                mTwitter.updateStatus(params[0]);
                return TWITTER_STATUS.SUCCESS;
            } catch (TwitterException e) {
                return TWITTER_STATUS.ERROR;
            }
        }

        @Override
        protected void onPostExecute(TWITTER_STATUS twitter_status) {

            switch (twitter_status){
                case SUCCESS:
                    showToastShort(getString(R.string.success_tweet));
                    finish();
                    return;
                case ERROR:
                    showToastShort(getString(R.string.error_normal));
                    break;
            }
        }
    }






    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    private void showToastShort(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    private String getInputText(){
        EditText editText = (EditText)findViewById(R.id.tweetEditText);
        return editText.getText().toString();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
