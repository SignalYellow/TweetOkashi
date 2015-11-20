package com.signalyellow.tweetokashi.fragments;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.signalyellow.tweetokashi.activity.FavoriteActivity;
import com.signalyellow.tweetokashi.activity.HaikuRegenerateActivity;
import com.signalyellow.tweetokashi.activity.HaikuRetweetActivity;
import com.signalyellow.tweetokashi.activity.ReplyActivity;
import com.signalyellow.tweetokashi.activity.RetweetActivity;
import com.signalyellow.tweetokashi.activity.TimelineActivity;
import com.signalyellow.tweetokashi.components.HaikuStatus;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.HaikuTweetAdapter;
import com.signalyellow.tweetokashi.components.HaikuUserStatus;
import com.signalyellow.tweetokashi.components.SettingUtils;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.components.UserAdapter;


import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.signalyellow.haiku.*;
import jp.signalyellow.haiku.MorphologicalAnalysisByYahooAPI;
import jp.signalyellow.haiku.Word;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class SlidingTabsFragment extends Fragment {

    static final String TAG = "SlidingTabsFragment";

    private Twitter mTwitter;
    private HaikuUserStatus mUserStatus;
    boolean isUserSet=false;

    boolean canCreateHaiku = false;

    private Button headerButton;
    private View footerView;

    private Button searchButton;
    private View searchFooterView;
    private String searchQuery;



    long cursor = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_tabs, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        canCreateHaiku = SettingUtils.canCreateHaiku(getActivity());
        mTwitter = TwitterUtils.getTwitterInstance(getActivity());
        new UserAsyncTask().execute();

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new TabPagerAdapter());

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)view.findViewById(R.id.slidingtabs);
        slidingTabLayout.setViewPager(mViewPager);
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.glycine));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.glycine));

    }


    private class UserAsyncTask extends AsyncTask<Void,Void, HaikuUserStatus>{
        @Override
        protected HaikuUserStatus doInBackground(Void... params) {

            User user;
            try {
                user = mTwitter.verifyCredentials();
            } catch (TwitterException e) {
                return null;
            }
            MorphologicalAnalysisByGooAPI analyzer =
                    new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));

            if (canCreateHaiku) {
                try {
                    List<Word> list = analyzer
                            .analyze(user.getDescription());
                    String haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                    return new HaikuUserStatus(haiku, user);
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                    return new HaikuUserStatus("", user);
                }
            } else {
                return new HaikuUserStatus("", user);
            }
        }

        @Override
        protected void onPostExecute(HaikuUserStatus user) {
            if(user == null){
                mUserStatus = null;
            }

            mUserStatus = user;
        }
    }

    private class TimelineAsyncTask extends AsyncTask<Void,Void,List<HaikuStatus>>{

        boolean isThereTweetException = false;
        HaikuTweetAdapter mAdapter;

        public TimelineAsyncTask(HaikuTweetAdapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        protected List<HaikuStatus> doInBackground(Void... params) {

            ResponseList<twitter4j.Status> timeline;

            try {
                timeline = mTwitter.getHomeTimeline();
            } catch (TwitterException e) {
                Log.e("twitter timeline", e.toString());
                isThereTweetException = true;
                return null;
            }

            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));
            List<HaikuStatus> haikuStatusList = new ArrayList<>();

            if (canCreateHaiku) {
                for (twitter4j.Status status : timeline) {
                    try {

                        List<Word> list = analyzer
                                .analyze(status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT","") : status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException e) {
                        Log.d("timline generate1",status.getText());
                        Log.d("timline generate1",analyzer.removeURLText(status.getText()));
                        Log.d("timeline generate", e.toString());
                        //showToast(getString(R.string.error_normal));
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

    private class TimelineAsyncTaskOfTail extends AsyncTask<Void,Void,List<HaikuStatus>>{

        boolean isThereTweetException = false;
        final String TAG = "TimelineTail";
        HaikuTweetAdapter mAdapter;


        public TimelineAsyncTaskOfTail(HaikuTweetAdapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        protected List<HaikuStatus> doInBackground(Void... params) {

            twitter4j.Status lastStatus = mAdapter.getItem(mAdapter.getCount()-1).getStatus();

            Paging paging = new Paging();
            paging.setMaxId(lastStatus.getId()-1);
            ResponseList<twitter4j.Status> timeline;

            try{
                timeline = mTwitter.getHomeTimeline(paging);
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
                                .analyze(status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT","") : status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException e) {
                        Log.d(TAG,e.toString());
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

    private class SearchAsyncTask extends AsyncTask<String,Void, List<HaikuStatus>> {

        boolean isThereTweetException = false;
        HaikuTweetAdapter mAdapter;

        public SearchAsyncTask(HaikuTweetAdapter adapter){
            this.mAdapter = adapter;
        }

        @Override
        protected List<HaikuStatus> doInBackground(String... params) {
            String queryString = params[0];

            if (queryString.isEmpty() || queryString.equals("")) {
                return null;
            }

            Query query = new Query(queryString);
            QueryResult result;
            try {
                result = mTwitter.search(query);
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                isThereTweetException = true;
                return null;
            }

            List<HaikuStatus> haikuStatusList = new ArrayList<>();
            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));

            if (canCreateHaiku) {
                for (twitter4j.Status status : result.getTweets()) {
                    try {
                        List<Word> list = analyzer
                                .analyze(status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT","") : status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException e) {
                        Log.d(TAG, e.toString());
                    }
                }
                return haikuStatusList;
            } else {
                for (twitter4j.Status status : result.getTweets()) {
                    String haiku = "";
                    haikuStatusList.add(new HaikuStatus(haiku, status));
                }
                return haikuStatusList;
            }
        }


        @Override
        protected void onPostExecute(List<HaikuStatus> statuses) {

            if(isThereTweetException) {
                showToast(getString(R.string.error_tweet_timeline));
                completeSearchUpdating();
                return;
            }

            if(statuses != null){
                mAdapter.clear();
                for(HaikuStatus s: statuses){
                    mAdapter.add(s);
                }
            }else{
                showToast(getString(R.string.toast_list_is_null));
            }
            completeSearchUpdating();
        }
    }

    private class SearchAsyncTaskOfTail extends AsyncTask<String,Void, List<HaikuStatus>>{
        boolean isThereTweetException = false;
        HaikuTweetAdapter mAdapter;

        public SearchAsyncTaskOfTail(HaikuTweetAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        protected List<HaikuStatus> doInBackground(String... params) {
            String queryString = params[0];

            if (queryString.isEmpty() || queryString.equals("")) {
                return null;
            }

            twitter4j.Status lastStatus = mAdapter.getItem(mAdapter.getCount()-1).getStatus();

            Query query = new Query(queryString);
            query.setMaxId(lastStatus.getId()-1);
            QueryResult result;
            try {
                result = mTwitter.search(query);
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                isThereTweetException = true;
                return null;
            }

            List<HaikuStatus> haikuStatusList = new ArrayList<>();
            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));

            if (canCreateHaiku) {
                for (twitter4j.Status status : result.getTweets()) {
                    try {
                        List<Word> list = analyzer
                                .analyze(status.getRetweetedStatus() != null ? status.getText().replaceFirst("RT","") : status.getText());
                        String haiku = new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
                        haikuStatusList.add(new HaikuStatus(haiku, status));
                    } catch (IOException  e) {
                        Log.d(TAG, e.toString());
                    }
                }
                return haikuStatusList;
            } else {
                for (twitter4j.Status status : result.getTweets()) {
                    String haiku = "";
                    haikuStatusList.add(new HaikuStatus(haiku, status));
                }
                return haikuStatusList;
            }
        }

        @Override
        protected void onPostExecute(List<HaikuStatus> statuses) {
            if(isThereTweetException) {
                showToast(getString(R.string.error_tweet_timeline));
                completeSearchUpdating();
                return;
            }

            if(statuses != null){
                for(HaikuStatus s: statuses){
                    mAdapter.add(s);
                }
            }else{
                showToast(getString(R.string.toast_list_is_null));
            }
            completeSearchUpdating();

        }
    }



    /**
     * FollowしているUserの表示、説明文からの俳句生成
     */
    private class FollowAsyncTask extends AsyncTask<Void,Void,List<HaikuUserStatus>>{

        UserAdapter mAdapter;
        View mFooterView;

        public FollowAsyncTask(UserAdapter adapter,View footerView){
            this.mAdapter = adapter;
            this.mFooterView = footerView;
        }

        @Override
        protected void onPreExecute() {
            prepareUserUpdating();
        }

        @Override
        protected List<HaikuUserStatus> doInBackground(Void... params) {

            PagableResponseList<User> list;
            try {
                list = mTwitter.getFriendsList(-1,cursor);
            } catch (TwitterException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            List<HaikuUserStatus> userStatusList = new ArrayList<>();
            MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(getString(R.string.goo_id));

            if (canCreateHaiku) {
                for (User u:list) {
                    try {
                        List<Word> words = analyzer
                                .analyze(u.getDescription());
                        String haiku = new HaikuGeneratorByGooAPI(words).generateHaikuStrictly();
                        userStatusList.add(new HaikuUserStatus(haiku, u));
                    } catch (IOException e) {
                        Log.d(TAG, e.toString());
                    }
                }
                cursor = list.getNextCursor();
                return userStatusList;
            } else {
                for (User u:list) {
                    userStatusList.add(new HaikuUserStatus("", u));
                }
                cursor = list.getNextCursor();
                return userStatusList;
            }
        }

        @Override
        protected void onPostExecute(List<HaikuUserStatus> users) {

            Log.d(TAG,String.valueOf(cursor));
            if(cursor == 0){
                mFooterView.setVisibility(View.GONE);
            }


            if(users == null){
                completeUserUpdating();
                return;
            }

            if(!isUserSet){
                this.mAdapter.add(mUserStatus);
                isUserSet = true;
            }

            for(HaikuUserStatus u:users){
                this.mAdapter.add(u);
            }
            completeUserUpdating();
        }

        private void prepareUserUpdating(){

            ProgressBar pb = (ProgressBar) mFooterView.findViewById(R.id.progress);
            Button tailButton = (Button)mFooterView.findViewById(R.id.tail_button);
            pb.setVisibility(View.VISIBLE);
            tailButton.setVisibility(View.GONE);

        }
        private void completeUserUpdating(){

            ProgressBar pb = (ProgressBar) mFooterView.findViewById(R.id.progress);
            Button tailButton = (Button)mFooterView.findViewById(R.id.tail_button);
            pb.setVisibility(View.GONE);
            tailButton.setVisibility(View.VISIBLE);

        }
    }


    private void prepareTimelineUpdating(){
        ProgressBar pb = (ProgressBar) footerView.findViewById(R.id.progress);
        Button tailButton = (Button)footerView.findViewById(R.id.tail_button);
        pb.setVisibility(View.VISIBLE);
        tailButton.setVisibility(View.GONE);

        headerButton.setEnabled(false);
        headerButton.setText(getString(R.string.btn_updating));
    }

    private void completeTimelineUpdating(){
        ProgressBar pb = (ProgressBar) footerView.findViewById(R.id.progress);
        Button tailButton = (Button)footerView.findViewById(R.id.tail_button);
        pb.setVisibility(View.GONE);
        tailButton.setVisibility(View.VISIBLE);

        headerButton.setEnabled(true);
        headerButton.setText(getString(R.string.btn_update));

    }

    private void startSearchView(){
        Button tailButton = (Button)searchFooterView.findViewById(R.id.tail_button);
        tailButton.setVisibility(View.GONE);
    }

    private void prepareSearchUpdating(){
        ProgressBar pb = (ProgressBar)searchFooterView.findViewById(R.id.progress);
        Button tailButton = (Button)searchFooterView.findViewById(R.id.tail_button);
        pb.setVisibility(View.VISIBLE);
        tailButton.setVisibility(View.GONE);

        searchButton.setEnabled(false);
    }

    private void completeSearchUpdating(){
        ProgressBar pb = (ProgressBar)searchFooterView.findViewById(R.id.progress);
        Button tailButton = (Button)searchFooterView.findViewById(R.id.tail_button);
        pb.setVisibility(View.GONE);
        tailButton.setVisibility(View.VISIBLE);

        searchButton.setEnabled(true);
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
                    return createUsersView(container);
                default:
                    return createThirdView(container,position);
            }
        }

        private View createThirdView(ViewGroup container,int position){
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item
                    ,container, false);
            container.addView(view);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position + 1));
            //new FollowAsyncTask().execute();

            return view;
        }

        private View createUsersView(ViewGroup container){
            View view = getActivity().getLayoutInflater().inflate(R.layout.usersitem,container,false);
            container.addView(view);


            ListView lv = (ListView)view.findViewById(R.id.users_list_view);
            final View userFooterView = getActivity().getLayoutInflater().inflate(R.layout.listview_footer,null);
            lv.addFooterView(userFooterView);
            final UserAdapter adapter = new UserAdapter(getActivity());
            lv.setAdapter(adapter);


            new FollowAsyncTask(adapter,userFooterView).execute();

            Button tailButton = (Button)userFooterView.findViewById(R.id.tail_button);
            tailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FollowAsyncTask(adapter,userFooterView).execute();
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (id == -1) return;
                    HaikuUserStatus status = (HaikuUserStatus) parent.getAdapter().getItem(position);
                    showUserDialog(status);
                }
            });


            return view;
        }



        private View createSearchView(ViewGroup container){
            View searchView = getActivity().getLayoutInflater().inflate(R.layout.searchitem, container, false);
            container.addView(searchView);

            ListView lv = (ListView)searchView.findViewById(R.id.search_list_view);
            searchFooterView = getActivity().getLayoutInflater().inflate(R.layout.listview_footer,null);
            lv.addFooterView(searchFooterView);
            final HaikuTweetAdapter searchAdapter = new HaikuTweetAdapter(getActivity());
            lv.setAdapter(searchAdapter);


            final EditText editText = (EditText)searchView.findViewById(R.id.search_editText);

            startSearchView();

            //ボタン設定
            searchButton = (Button)searchView.findViewById(R.id.search_button);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = editText.getText().toString();
                    if (temp.isEmpty()) {
                        showToast(getString(R.string.error_empty_text));
                        return;
                    }

                    prepareSearchUpdating();
                    searchQuery = temp;
                    new SearchAsyncTask(searchAdapter).execute(searchQuery);
                }
            });

            Button tailButton = (Button) searchFooterView.findViewById(R.id.tail_button);
            tailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchQuery.isEmpty()) {
                        showToast(getString(R.string.error_empty_text));
                        return;
                    }

                    prepareSearchUpdating();
                    new SearchAsyncTaskOfTail(searchAdapter).execute(searchQuery);

                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (id == -1) return;

                    final HaikuStatus status = (HaikuStatus) parent.getAdapter().getItem(position);
                    showDialog(status);
                }
            });

            return searchView;
        }

        private View createTimelineView(ViewGroup container){

            View homeView = getActivity().getLayoutInflater().inflate(R.layout.homeitem, container, false);
            container.addView(homeView);

            ListView lv = (ListView)homeView.findViewById(R.id.home_list_view);
            footerView = getActivity().getLayoutInflater().inflate(R.layout.listview_footer,null);
            lv.addFooterView(footerView);
            final HaikuTweetAdapter timelineAdapter = new HaikuTweetAdapter(getActivity());
            lv.setAdapter(timelineAdapter);



            final Button tailButton = (Button) footerView.findViewById(R.id.tail_button);
            tailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prepareTimelineUpdating();
                    new TimelineAsyncTaskOfTail(timelineAdapter).execute();
                }
            });

            headerButton = (Button)homeView.findViewById(R.id.button_sub);
            headerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prepareTimelineUpdating();
                    new TimelineAsyncTask(timelineAdapter).execute();
                }
            });


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id == -1) return;

                    final HaikuStatus status = (HaikuStatus) parent.getAdapter().getItem(position);
                    showDialog(status);
                }
            });


            prepareTimelineUpdating();
            new TimelineAsyncTask(timelineAdapter).execute();

            return homeView;
        }

        private void showUserDialog(final HaikuUserStatus status){
            final String[] items = {
                    status.getUser().getName() + " " + getString(R.string.dialog_usertimeline)
            };
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dialog_title))
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;

                            switch (which) {
                                case 0:
                                    intent = new Intent(getActivity(), TimelineActivity.class);
                                    break;
                                default:
                                    showToast(getString(R.string.error_normal));
                                    return;
                            }
                            intent.putExtra(TwitterUtils.INTENT_TAG_TWEETDATA,
                                    new SimpleTweetData(status));
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        private void showDialog(final HaikuStatus haikuStatus){

            if(canCreateHaiku){
                showDialogForHaiku(haikuStatus);
                return;
            }


            final String[] items = {getString(R.string.dialog_reply),
                    getString(R.string.dialog_retweet),
                    haikuStatus.getUserName() + " " + getString(R.string.dialog_usertimeline),
                    getString(R.string.dialog_favorite)
                    };
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dialog_title))
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;

                            switch (which) {
                                case 0:
                                    intent = new Intent(getActivity(),ReplyActivity.class);
                                    break;
                                case 1:
                                    intent = new Intent(getActivity(), RetweetActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(getActivity(), TimelineActivity.class);
                                    break;
                                case 3:
                                    intent = new Intent(getActivity(),FavoriteActivity.class);
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
                    haikuStatus.getUserName() + " " + getString(R.string.dialog_usertimeline),
                    getString(R.string.dialog_haikuretweet),
                    getString(R.string.dialog_regenerate),
                    getString(R.string.dialog_favorite)};
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dialog_title))
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;
                            switch (which) {
                                case 0:
                                    intent = new Intent(getActivity(),ReplyActivity.class);
                                    break;
                                case 1:
                                    intent = new Intent(getActivity(), RetweetActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(getActivity(), TimelineActivity.class);
                                    break;
                                case 3:
                                    intent = new Intent(getActivity(), HaikuRetweetActivity.class);
                                    break;
                                case 4:
                                    intent = new Intent(getActivity(), HaikuRegenerateActivity.class);
                                    break;
                                case 5:
                                    intent = new Intent(getActivity(), FavoriteActivity.class);
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
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    return getString(R.string.sliding_tab_title_timeline);
                case 1:
                    return getString(R.string.sliding_tab_title_search);
                case 2:
                    return getString(R.string.sliding_tab_title_users);
            }
            return "Item" + (position + 1);
        }
    }

    private void showToast(String text){
        Toast.makeText(getActivity().getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }


}


