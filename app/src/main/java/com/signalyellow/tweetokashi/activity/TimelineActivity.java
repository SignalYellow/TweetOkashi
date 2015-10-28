package com.signalyellow.tweetokashi.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.HaikuStatus;
import com.signalyellow.tweetokashi.components.HaikuTweetAdapter;
import com.signalyellow.tweetokashi.components.SettingUtils;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.signalyellow.haiku.*;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TimelineActivity extends Activity {

    Twitter mTwitter;
    String userScreenName;
    HaikuTweetAdapter adapter;
    boolean canCreateHaiku;
    private Button headerButton;
    private View footerView;

    static final String TAG="TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mTwitter = TwitterUtils.getTwitterInstance(getApplicationContext());

        SimpleTweetData data = (SimpleTweetData) getIntent().getSerializableExtra(TwitterUtils.INTENT_TAG_TWEETDATA);



        if (data == null) {
            showToast(getString(R.string.error_normal));
            finish();
        }

        if(data != null)
        userScreenName = data.getUserScreenName();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle("@"+userScreenName);
        }

        ListView listView = (ListView)findViewById(R.id.timeline_list_view);
        footerView = getLayoutInflater().inflate(R.layout.listview_footer,null);
        listView.addFooterView(footerView);
        adapter = new HaikuTweetAdapter(this);
        listView.setAdapter(adapter);


        final Button tailButton = (Button) footerView.findViewById(R.id.tail_button);
        tailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimelineAsyncTaskOfTail(adapter).execute();
            }
        });

        headerButton = (Button)findViewById(R.id.button_update);
        headerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimelineAsyncTask(adapter).execute();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == -1) return;

                final HaikuStatus status = (HaikuStatus) parent.getAdapter().getItem(position);
                showDialog(status);
            }
        });

        canCreateHaiku = SettingUtils.canCreateHaiku(this);
        new TimelineAsyncTask(adapter).execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        canCreateHaiku = SettingUtils.canCreateHaiku(this);
    }

    private class TimelineAsyncTask extends AsyncTask<Void,Void,List<HaikuStatus>>{

        boolean isThereTweetException = false;
        HaikuTweetAdapter mAdapter;

        public TimelineAsyncTask(HaikuTweetAdapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prepareTimelineUpdating();
        }

        @Override
        protected List<HaikuStatus> doInBackground(Void... params) {

            ResponseList<twitter4j.Status> timeline;

            try {
                timeline = mTwitter.getUserTimeline(userScreenName);
            } catch (TwitterException e) {
                Log.e("twitter timeline", e.toString());
                isThereTweetException = true;
                return null;
            }

            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));
            List<HaikuStatus> haikuStatusList = new ArrayList<HaikuStatus>();

            if (canCreateHaiku) {
                for (twitter4j.Status status : timeline) {
                    try {
                        List<Word> list = analyzer
                                .analyze(status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generate();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException e) {
                        Log.d("timeline generate", e.toString());
                        showToast(getString(R.string.error_normal));
                    }
                }
            } else {
                for (twitter4j.Status status : timeline) {
                    String haiku = "";
                    haikuStatusList.add(new HaikuStatus(haiku, status));
                }
            }


            return haikuStatusList;
        }

        /**
         * listview に取得したタイムラインを適用
         * @param list tweetList that contains haiku
         */
        @Override
        protected void onPostExecute(List<HaikuStatus> list) {

            if(isThereTweetException) {
                showToast(getString(R.string.error_tweet_timeline));
                completeTimelineUpdating();
                return;
            }

            if(list != null){
                mAdapter.clear();
                for(HaikuStatus s: list){
                    mAdapter.add(s);
                }
            }else{
                showToast(getString(R.string.toast_list_is_null));
            }

            completeTimelineUpdating();
        }
    }

    private void completeTimelineUpdating(){
        ProgressBar pb = (ProgressBar) footerView.findViewById(R.id.progress);
        Button tailButton = (Button)footerView.findViewById(R.id.tail_button);
        pb.setVisibility(View.GONE);
        tailButton.setVisibility(View.VISIBLE);

        headerButton.setEnabled(true);
        headerButton.setText(getString(R.string.btn_update));

    }

    private void prepareTimelineUpdating(){
        ProgressBar pb = (ProgressBar) footerView.findViewById(R.id.progress);
        Button tailButton = (Button)footerView.findViewById(R.id.tail_button);
        pb.setVisibility(View.VISIBLE);
        tailButton.setVisibility(View.GONE);

        headerButton.setEnabled(false);
        headerButton.setText(getString(R.string.btn_updating));
    }

    private class TimelineAsyncTaskOfTail extends AsyncTask<Void,Void,List<HaikuStatus>>{

        boolean isThereTweetException = false;
        final String TAG = "TimelineTail";
        HaikuTweetAdapter mAdapter;


        public TimelineAsyncTaskOfTail(HaikuTweetAdapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prepareTimelineUpdating();
        }


        @Override
        protected List<HaikuStatus> doInBackground(Void... params) {

            twitter4j.Status lastStatus = mAdapter.getItem(mAdapter.getCount()-1).getStatus();

            Paging paging = new Paging();
            paging.setMaxId(lastStatus.getId()-1);
            ResponseList<twitter4j.Status> timeline;

            try{
                timeline = mTwitter.getUserTimeline(userScreenName,paging);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                isThereTweetException = true;
                return null;
            }

            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));
            List<HaikuStatus> haikuStatusList = new ArrayList<>();

            if (canCreateHaiku) {
                for (twitter4j.Status status : timeline) {
                    try {
                        List<Word> list = analyzer
                                .analyze(status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generate();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException e) {
                        Log.d(TAG, e.toString());
                    }
                }
            } else {
                for (twitter4j.Status status : timeline) {
                    String haiku = "";
                    haikuStatusList.add(new HaikuStatus(haiku, status));
                }
            }

            return haikuStatusList;
        }


        @Override
        protected void onPostExecute(List<HaikuStatus> statuses) {

            if(isThereTweetException) {
                showToast(getString(R.string.error_tweet_timeline));
                completeTimelineUpdating();
                return;
            }

            if(statuses != null){
                for(HaikuStatus s: statuses){
                    mAdapter.add(s);
                }
            }else{
                showToast(getString(R.string.toast_list_is_null));
            }

            completeTimelineUpdating();
        }
    }


    private void showDialog(final HaikuStatus haikuStatus){

        if(canCreateHaiku){
            showDialogForHaiku(haikuStatus);
            return;
        }


        final String[] items = {getString(R.string.dialog_reply),
                getString(R.string.dialog_retweet)
        };
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;

                        switch (which) {
                            case 0:
                                intent = new Intent(getApplicationContext(),ReplyActivity.class);
                                break;
                            case 1:
                                intent = new Intent(getApplicationContext(), RetweetActivity.class);
                                break;
                            case 2:
                                intent = new Intent(getApplicationContext(), HaikuRetweetActivity.class);
                                break;
                            case 3:
                                intent = new Intent(getApplicationContext(), HaikuRegenerateActivity.class);
                                break;
                            default:
                                showToast(getString(R.string.error_normal));
                                return;
                        }
                        intent.putExtra(TwitterUtils.INTENT_TAG_TWEETDATA,
                                new SimpleTweetData(haikuStatus));
                        startActivity(intent);
                    }
                })
                .show();

    }

    private void showDialogForHaiku(final HaikuStatus haikuStatus){
        final String[] items = {getString(R.string.dialog_reply),
                getString(R.string.dialog_retweet),
                getString(R.string.dialog_haikuretweet),
                getString(R.string.dialog_regenerate)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(getApplicationContext(),ReplyActivity.class);
                                break;
                            case 1:
                                intent = new Intent(getApplicationContext(), RetweetActivity.class);
                                break;
                            case 2:
                                intent = new Intent(getApplicationContext(), HaikuRetweetActivity.class);
                                break;
                            case 3:
                                intent = new Intent(getApplicationContext(), HaikuRegenerateActivity.class);
                                break;
                            default:
                                showToast(getString(R.string.error_normal));
                                return;
                        }
                        intent.putExtra(TwitterUtils.INTENT_TAG_TWEETDATA,
                                new SimpleTweetData(haikuStatus));
                        startActivity(intent);
                    }
                })
                .show();
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


    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
