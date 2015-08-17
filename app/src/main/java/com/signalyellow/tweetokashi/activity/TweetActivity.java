package com.signalyellow.tweetokashi.activity;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;

public class TweetActivity extends Activity  {

    public static final String INTENT_TAG_TWEETDATA = "Simple_Tweet_Data";
    public static final String INTENT_TAG_JOBKIND = "JOB_KIND";

    static final String TAG = "TweetActivity";

    Twitter mTwitter;

    enum TWITTER_STATUS{
        SUCCESS,
        ERROR
    }

    public enum TWEET_ACTIVITY{
        TWEET,
        RETWEET,
        REPLY,
        HAIKU_RETWEET,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        mTwitter = TwitterUtils.getTwitterInstance(this);

        SimpleTweetData data = (SimpleTweetData)getIntent().getSerializableExtra(INTENT_TAG_TWEETDATA);
        TWEET_ACTIVITY kind = (TWEET_ACTIVITY)getIntent().getSerializableExtra(INTENT_TAG_JOBKIND);

        switch (kind){
            case HAIKU_RETWEET:
                doHaikuRetweet("",data);
                return;
            case TWEET:
                prepareForTweet(data);
                break;
            case REPLY:
                prepareForReply(data);
                break;
            case RETWEET:
                doRetweet(data.getTweetId());
                break;
            default:
                showToast("エラー");
                finish();
                return;
        }

    }

    private void prepareForTweet(SimpleTweetData data){

        Button btn = (Button)findViewById(R.id.tweet_button);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doTweet(getInputText());
            }
        });
    }
    private void prepareForReply(SimpleTweetData data){
        TextView originalText = (TextView)findViewById(R.id.original_text);
        originalText.setVisibility(View.VISIBLE);

        EditText editText = (EditText)findViewById(R.id.tweetEditText);
        editText.setText("@" + data.getUserScreenName() + " ");

        final String tweetId = String.valueOf(data.getTweetId());

        Button btn = (Button)findViewById(R.id.tweet_button);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doReply(getInputText(), tweetId);
            }
        });
    }

    private void doTweet(String text){

        if(text.isEmpty()){
            showToast("文章を入力してください");
            return;
        }

        new AsyncTask<String,Void,TWITTER_STATUS>(){
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
                if(twitter_status == TWITTER_STATUS.ERROR){
                    showToast("エラー　が起きました");
                }

                if(twitter_status == TWITTER_STATUS.SUCCESS){
                    showToast("ツイートに成功しました");
                    finish();
                }
            }
        }.execute(text);
    }
    private void doHaikuRetweet(String text, SimpleTweetData data){

        String TAG = getString(R.string.retweet_tag);

        if(data.getHaiku().isEmpty()){
            return;
        }

        String tweetText =   data.getHaiku() + TAG + "\n"  + text + " \n" +  data.getURL();

        new AsyncTask<String,Void,TWITTER_STATUS>(){
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
                        showToast("ツイートに成功しました");
                        finish();
                        break;
                    case ERROR:
                        showToast("エラー　が起きました");
                        break;
                }
            }
        }.execute(tweetText);
    }
    private void doReply(String text, String tweetId){

        new AsyncTask<String,Void,TWITTER_STATUS>(){
            @Override
            protected TWITTER_STATUS doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(new StatusUpdate(params[0])
                            .inReplyToStatusId(Long.parseLong(params[1])));
                    return TWITTER_STATUS.SUCCESS;
                } catch (TwitterException e) {
                    return TWITTER_STATUS.ERROR;
                }
            }

            @Override
            protected void onPostExecute(TWITTER_STATUS twitter_status) {
                switch (twitter_status){
                    case SUCCESS:
                        showToast("ツイートに成功しました");
                        finish();
                    case ERROR:
                        showToast("エラー　が起きました");
                        break;
                }
            }
        }.execute(text,String.valueOf(tweetId));
    }
    private void doRetweet(Long tweetId){
        new AsyncTask<Long,Void,TWITTER_STATUS>(){
            @Override
            protected TWITTER_STATUS doInBackground(Long... params) {
                try {
                    mTwitter.retweetStatus(params[0]);
                    return TWITTER_STATUS.SUCCESS;
                } catch (TwitterException e) {
                    return TWITTER_STATUS.ERROR;
                }

            }

            @Override
            protected void onPostExecute(TWITTER_STATUS twitter_status) {
                switch (twitter_status){
                    case SUCCESS:
                        showToast("リツイートに成功しました");
                        finish();
                        break;
                    case ERROR:
                        showToast("エラー　が起きました");
                        break;
                }
            }
        }.execute(tweetId);
    }

    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
    private String getInputText(){
        EditText editText = (EditText)findViewById(R.id.tweetEditText);
        String text = editText.getText().toString();
        return text;
    }


}
