package com.signalyellow.tweetokashi.sub;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.async.TweetAsyncTask;
import com.signalyellow.tweetokashi.data.TweetData;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TweetFragment extends Fragment {

    private static final String TAG = "TweetFragment";

    static final int SELECT_PIC = 1;
    private File pic;


    FloatingActionButton tweetButton;
    FloatingActionButton imageButton;
    ImageView imageView;
    EditText tweetEditText;
    TextView countTextView;

    TweetOkashiApplication mApp;

    public TweetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        tweetButton = (FloatingActionButton)view.findViewById(R.id.tweet_button);
        imageButton = (FloatingActionButton)view.findViewById(R.id.img_add_button);
        tweetEditText = (EditText)view.findViewById(R.id.tweet_edit_text);
        imageView = (ImageView)view.findViewById(R.id.image);
        countTextView = (TextView)view.findViewById(R.id.count_text);
        tweetEditText.addTextChangedListener(new TextCountWatcher(countTextView));

        tweetButton.setOnClickListener(new OnTweetButtonClickListener());
        imageButton.setOnClickListener(new OnImageAddButtonClickListener());

        return view;
    }


    private class OnTweetButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG,"tweetButton");
            String text = tweetEditText.getText().toString();
            new TweetAsyncTask(mApp.getTwitterInstance()).execute(text);
        }
    }

    private class OnImageAddButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG,"image button");
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),SELECT_PIC);
        }
    }

    private class TextCountWatcher implements TextWatcher{

        TextView mCountTextView;

        public TextCountWatcher(TextView countTextView) {
            mCountTextView = countTextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCountTextView.setText(String.valueOf(s.length()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,requestCode +" " + requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == SELECT_PIC){
                Uri uri = data.getData();
                ContentResolver resolver = getActivity().getContentResolver();
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = resolver.query(uri,columns,null,null,null);
                if(cursor == null) return;
                cursor.moveToFirst();
                pic = new File(cursor.getString(0));
                imageView.setImageURI(uri);
                cursor.close();
            }
        }
    }


    private class SelfQuoteTweetAsyncTask extends AsyncTask<String,Void, Status>{

        Twitter mTwitter;

        public SelfQuoteTweetAsyncTask(Twitter twitter) {
            mTwitter = twitter;
        }

        @Override
        protected twitter4j.Status doInBackground(String... params) {
            if(params.length != 2) return null;

            String text = params[0];
            String haiku = params[1];

            try{
                twitter4j.Status status = mTwitter.updateStatus(text);
                String quote = " https://twitter.com/" + status.getUser().getScreenName() +"/status/" + status.getId();
                return mTwitter.updateStatus(haiku + TweetData.HAIKU_TAG + quote);
            }catch (TwitterException e){
                Log.e(TAG,e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {
            super.onPostExecute(status);
            if(status == null){
                Log.e(TAG,"error post execute");
                return;
            }
            Log.d(TAG,"success");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
