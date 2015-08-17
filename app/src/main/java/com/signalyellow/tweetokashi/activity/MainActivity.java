package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.TwitterUtils;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;



public class MainActivity extends ListActivity {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_main);

        if(!TwitterUtils.hasAccessToken(this)) {
            Log.d("debug_check", "start");
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }else{

            startActivity(new Intent(this, HomeActivity.class));

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.menu_refresh:
                reloadTimeLine();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class TweetAdapter extends ArrayAdapter<twitter4j.Status>{

        private LayoutInflater mInflater;


        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = mInflater.inflate(R.layout.list_item_tweet,null);
            }

            Status item = getItem(position);

            TextView name = (TextView)convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());

            TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());


            SmartImageView icon = (SmartImageView)convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return convertView;
        }
    }

    private void reloadTimeLine(){
        AsyncTask<Void,Void,List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {


            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try{
                    ResponseList<twitter4j.Status> timeline = mTwitter.getHomeTimeline();
                    ArrayList<String> list = new ArrayList<String>();
                    for(twitter4j.Status status: timeline){
                        list.add(status.getText());
                    }

                    return timeline;
                }catch (TwitterException e){
                    Log.e("twitter timeline",e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> list) {
                if(list != null){
                    mAdapter.clear();
                    for(twitter4j.Status s: list){
                        mAdapter.add(s);
                    }
                    getListView().setSelection(0);

                }
                else{
                    showToast("失敗");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG);
    }

}
