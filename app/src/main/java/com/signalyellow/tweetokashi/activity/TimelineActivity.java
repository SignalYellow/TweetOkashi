package com.signalyellow.tweetokashi.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.HaikuStatus;
import com.signalyellow.tweetokashi.components.HaikuTweetAdapter;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TimelineActivity extends ListActivity {

    Twitter mTwitter;

    static final String TAG="TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mTwitter = TwitterUtils.getTwitterInstance(getApplicationContext());

        SimpleTweetData data = (SimpleTweetData)getIntent().getSerializableExtra(TweetActivity.INTENT_TAG_TWEETDATA);

        if(data != null) {
            new UserTimelineAsyncTask().execute(data.getUserScreenName());
        }else{
            showToast("エラーが発生しました");
            finish();
        }

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final HaikuStatus status = (HaikuStatus)parent.getAdapter().getItem(position);
                Log.d(TAG,status.getStatus().getText());

                final String[] items = {"このツイートに返信", "俳句リツイート","リツイート",status.getStatus().getUser().getName()};
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("ツイートめにゅー")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                if(which == 3){
                                    intent = new Intent(getApplicationContext(),TimelineActivity.class);
                                }else {
                                    intent = new Intent(getApplicationContext(), TweetActivity.class);

                                    switch (which) {
                                        case 0:
                                            intent.putExtra(TweetActivity.INTENT_TAG_JOBKIND,
                                                    TweetActivity.TWEET_ACTIVITY.REPLY);
                                            break;
                                        case 1:
                                            intent.putExtra(TweetActivity.INTENT_TAG_JOBKIND,
                                                    TweetActivity.TWEET_ACTIVITY.HAIKU_RETWEET);
                                            break;
                                        case 2:
                                            intent.putExtra(TweetActivity.INTENT_TAG_JOBKIND,
                                                    TweetActivity.TWEET_ACTIVITY.RETWEET);
                                    }
                                }
                                intent.putExtra(TweetActivity.INTENT_TAG_TWEETDATA,
                                        new SimpleTweetData(status));
                                startActivity(intent);


                            }
                        })
                        .show();

            }
        });
    }

    private class UserTimelineAsyncTask extends AsyncTask<String,Void,List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(String... params) {
            try{
                ResponseList<twitter4j.Status> timeline = mTwitter.getUserTimeline(params[0]);

                return timeline;
            }catch (TwitterException e){
                Log.e("twitter timeline", e.toString());
            }
            return null;
        }

        /**
         * listview に取得したタイムラインを適用
         * @param list
         */
        @Override
        protected void onPostExecute(List<twitter4j.Status> list) {

            if(list != null){
                ListView lv  = getListView();

                HaikuTweetAdapter tweetAdapter = new HaikuTweetAdapter(getApplicationContext());
                for(twitter4j.Status s: list){
                    tweetAdapter.add(new HaikuStatus("sub",s));
                }
                lv.setAdapter(tweetAdapter);

            }else{
                Log.e(TAG,"failed");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}
