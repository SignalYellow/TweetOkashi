package com.signalyellow.tweetokashi.fragments;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.activity.TimelineActivity;
import com.signalyellow.tweetokashi.components.HaikuStatus;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.activity.TweetActivity;
import com.signalyellow.tweetokashi.components.HaikuTweetAdapter;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;


import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.signalyellow.HaikuGenerator;
import jp.signalyellow.MorphologicalAnalysisByYahooAPI;
import jp.signalyellow.Word;
import jp.signalyellow.view.*;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SlidingTabsFragment extends Fragment {

    static final String TAG = "SlidingTabsFragment";

    private Twitter mTwitter;

    boolean isUpdating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_tabs, container, false);

    }
    @Override
    public void onStart() {
        super.onStart();
        mTwitter = TwitterUtils.getTwitterInstance(getActivity());
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new TabPagerAdapter());

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)view.findViewById(R.id.slidingtabs);
        slidingTabLayout.setViewPager(mViewPager);

    }

    private class TimelineAsyncTask extends AsyncTask<Void,Void,List<HaikuStatus>>{

        @Override
        protected List<HaikuStatus> doInBackground(Void... params) {
            try{
                ResponseList<twitter4j.Status> timeline = mTwitter.getHomeTimeline();
                MorphologicalAnalysisByYahooAPI analyzor = new MorphologicalAnalysisByYahooAPI(getString(R.string.yahoo_application_id));
                List<HaikuStatus> haikuStatusList = new ArrayList<HaikuStatus>();
                for (twitter4j.Status status : timeline) {

                    try {
                        List<Word> list = analyzor
                                .analyze(status.getText());
                        String haiku = new HaikuGenerator(list).generate();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    }
                    catch (IOException e){
                        Log.d(TAG,"こんにちは"+ e.toString() + e.getLocalizedMessage());

                    }catch (XmlPullParserException e2){

                        Log.d(TAG,"こんにちは"+ e2.toString() + e2.getLocalizedMessage());
                    }
                }

                return haikuStatusList;
            }catch (TwitterException e){
                Log.e("twitter timeline",e.toString());
            }
            return null;
        }

        /**
         * listview に取得したタイムラインを適用
         * @param list
         */
        @Override
        protected void onPostExecute(List<HaikuStatus> list) {
            if(list != null){
                ListView lv  = (ListView)getActivity().findViewById(R.id.home_list_view);
                HaikuTweetAdapter tweetAdapter = new HaikuTweetAdapter(getActivity());
                for(HaikuStatus s: list){
                    tweetAdapter.add(s);
                }
                lv.setAdapter(tweetAdapter);

            }else{
                showToast(getString(R.string.toast_list_is_null));
            }
        }
    }
    private class TimelineAsyncTaskOfTail extends AsyncTask<Void,Void,List<twitter4j.Status>>{
        ListView lv;
        HaikuTweetAdapter mAdapter;

        @Override
        protected void onPreExecute() {
            lv = (ListView)getActivity().findViewById(R.id.home_list_view);
            mAdapter = (HaikuTweetAdapter)lv.getAdapter();
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {

            twitter4j.Status lastStatus = mAdapter.getItem(mAdapter.getCount()-1).getStatus();
            Log.d(TAG,lastStatus.getText());

            Paging paging = new Paging();
            paging.setMaxId(lastStatus.getId()-1);

            try{
                return mTwitter.getHomeTimeline(paging);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            if(statuses != null){
                for(twitter4j.Status status: statuses){
                    mAdapter.add(new HaikuStatus("追加",status));
                }
                isUpdating = false;
            }
        }
    }

    private class SearchHaikuAsyncTask extends AsyncTask<String,Void, List<HaikuStatus>> {

        @Override
        protected List<HaikuStatus> doInBackground(String... params) {
            String queryString = params[0];

            if (queryString.isEmpty() || queryString.equals("")) {
                return null;
            }

            Query query = new Query(queryString);

            List<HaikuStatus> haikuStatusList = new ArrayList<HaikuStatus>();
            try {
                QueryResult result = mTwitter.search(query);
                MorphologicalAnalysisByYahooAPI analyzor = new MorphologicalAnalysisByYahooAPI(getString(R.string.yahoo_application_id));

                for (twitter4j.Status status : result.getTweets()) {

                     try {
                        List<Word> list = analyzor
                                .analyze(status.getText());
                         Log.d("haiku-list","ok");
                        String haiku = new HaikuGenerator(list).generate();
                        //String haiku = "aaa";
                         Log.d("haiku-list","ok2");

                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } /*catch(IllegalArgumentException e) {
                        Log.d(TAG,"こんにちは"+e.toString());
                         e.printStackTrace();

                         return haikuStatusList;
                    }
                    catch (Exception e){
                        haikuStatusList.add(new HaikuStatus("えらー", status));
                        Log.d(TAG, "エラー俳句" + e.toString());

                        return haikuStatusList;

                    }*/
                    catch (IOException e){
                        Log.d(TAG,"こんにちは"+ e.toString() + e.getLocalizedMessage());

                    }catch (XmlPullParserException e2){

                         Log.d(TAG,"こんにちは"+ e2.toString() + e2.getLocalizedMessage());
                     }
                }


                return haikuStatusList;
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                return haikuStatusList;
            }

            //return null;
        }

        @Override
        protected void onPostExecute(List<HaikuStatus> haikuStatuses) {

            Log.d(TAG,"onpost");

            if (haikuStatuses != null) {
                ListView lv = (ListView) getActivity().findViewById(R.id.search_list_view);
                HaikuTweetAdapter adapter = new HaikuTweetAdapter(getActivity());
                for (HaikuStatus status : haikuStatuses) {
                    adapter.add(status);
                }
                lv.setAdapter(adapter);
            }
        }
    }

    private class TabPagerAdapter extends PagerAdapter{


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            switch (position){
                case 0:
                    return createTimelineView(container);
                case 1:
                    return createSearchView(container);
                case 2:
                    return createThirdView(container,position);
                default:
                    return createThirdView(container,position);
            }
        }

        private View createThirdView(ViewGroup container,int position){
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item
                    ,container, false);
            container.addView(view);
            TextView title = (TextView ) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position+1));

            return view;
        }

        private View createSearchView(ViewGroup container){
            View searchView = getActivity().getLayoutInflater().inflate(R.layout.searchitem, container, false);
            container.addView(searchView);

            Button button = (Button)searchView.findViewById(R.id.search_button);
            ListView listView = (ListView)searchView.findViewById(R.id.search_list_view);
            final EditText editText = (EditText)searchView.findViewById(R.id.search_editText);

            //ボタン設定
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = editText.getText().toString();

                    new SearchHaikuAsyncTask().execute(query);

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TextView tv = (TextView)view.findViewById(R.id.text);


                    Log.d(TAG, "a");
                }
            });



            return searchView;
        }

        private View createTimelineView(ViewGroup container){

            View homeView = getActivity().getLayoutInflater().inflate(R.layout.homeitem, container, false);
            container.addView(homeView);

            new TimelineAsyncTask().execute();

            Button b = (Button)homeView.findViewById(R.id.button_sub);
            ListView lv = (ListView)homeView.findViewById(R.id.home_list_view);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final HaikuStatus status = (HaikuStatus)parent.getAdapter().getItem(position);
                    Log.d(TAG,status.getStatus().getText());

                    final String[] items = {"このツイートに返信", "俳句リツイート","リツイート",status.getStatus().getUser().getName()};
                    new AlertDialog.Builder(getActivity())
                            .setTitle("ツイートめにゅー")
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    if(which == 3){
                                        intent = new Intent(getActivity(),TimelineActivity.class);
                                    }else {
                                        intent = new Intent(getActivity(), TweetActivity.class);

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

            lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (!isUpdating && totalItemCount != 0 && totalItemCount - 3 < firstVisibleItem + visibleItemCount) {
                        isUpdating = true;
                        Log.d(TAG, "first:" + firstVisibleItem + " visible:" + visibleItemCount + " total:" + totalItemCount + " update:" + isUpdating);
                        new TimelineAsyncTaskOfTail().execute();
                    }
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimelineAsyncTask().execute();
                }
            });
            return homeView;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    return "HOME";
                case 1:
                    return "SEARCH";
                case 2:
                    return "TWEET";
            }
            return "Item" + (position + 1);
        }
    }

    private void showToast(String text){
        Toast.makeText(getActivity().getApplicationContext(),text,Toast.LENGTH_LONG);
    }


}



/*
    private class SearchAsyncTask extends AsyncTask<String,Void, QueryResult>{

        @Override
        protected QueryResult doInBackground(String... params) {
            String queryString = params[0];

            if(queryString.isEmpty() || queryString.equals("")){
                return null;
            }

            Query query = new Query(queryString);

            try{
                QueryResult result = mTwitter.search(query);
                return result;
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {

            if(queryResult != null && queryResult.getTweets() != null) {
                ListView lv = (ListView) getActivity().findViewById(R.id.search_list_view);
                TweetAdapter adapter = new TweetAdapter(getActivity());
                //HaikuTweetAdapter adapter = new HaikuTweetAdapter(getActivity());
                for (twitter4j.Status status : queryResult.getTweets()) {
                    adapter.add(status);
                }
                lv.setAdapter(adapter);
            }

        }
    }*/