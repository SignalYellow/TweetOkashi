package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ReplyActivity extends Activity {


    Twitter mTwitter;

    EditText tweetInputText;
    Long replyId;
    Button tweetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);


        android.app.ActionBar bar = getActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setSubtitle(R.string.reply_activity_subtitle);
        }

        mTwitter = TwitterUtils.getTwitterInstance(this);
        SimpleTweetData data = (SimpleTweetData)getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_TWEETDATA);

        if(data == null){
            showToastShort(getString(R.string.error_normal));
            finish();
            return;
        }

        prepareForReply(data);

    }

    private void prepareForReply(SimpleTweetData data) {

        TextView haikuTextView = (TextView)findViewById(R.id.haikutext);
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


        tweetInputText = (EditText)findViewById(R.id.tweetEditText);
        tweetInputText.setText("@" + data.getUserScreenName() + " ");
        replyId = data.getTweetId();

        tweetButton = (Button) findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tweetInputText.getText().toString();
                if (text.length() > 140 ) {
                    showToastShort(getString(R.string.error_too_long_text));
                    return;
                }

                if(text.isEmpty()){
                    showToastShort(getString(R.string.error_empty_text));
                    return;
                }
                new ReplyAsyncTask().execute(text);
            }
        });

        tweetInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView = (TextView) findViewById(R.id.tweet_text_count);
                Integer length = s.length();
                textView.setText(length.toString());

                tweetButton.setEnabled(true);

                if (length == 0 || length > 140) {
                    tweetButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private class ReplyAsyncTask extends AsyncTask<String,Void,TwitterUtils.TWITTER_STATUS> {

        @Override
        protected TwitterUtils.TWITTER_STATUS doInBackground(String... params) {
            try {
                mTwitter.updateStatus(new StatusUpdate(params[0])
                        .inReplyToStatusId(replyId));
                return TwitterUtils.TWITTER_STATUS.SUCCESS;
            } catch (TwitterException e) {
                return TwitterUtils.TWITTER_STATUS.ERROR;
            }
        }

        @Override
        protected void onPostExecute(TwitterUtils.TWITTER_STATUS twitter_status) {
            switch (twitter_status) {
                case SUCCESS:
                    showToastShort(getString(R.string.success_reply));
                    finish();
                    return;
                case ERROR:
                    showToastShort(getString(R.string.error_normal));
                    break;
            }
        }
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

    private void showToastShort(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
