package com.signalyellow.tweetokashi.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.async.TweetAsyncTask;
import com.signalyellow.tweetokashi.view.DeletableImageView;


import java.util.ArrayList;
import java.util.List;

public class TweetFragment extends Fragment implements DeletableImageView.OnViewDeleteListener{

    private static final String TAG = "TweetFragment";

    static final int SELECT_PIC = 1;

    FloatingActionButton tweetButton;
    FloatingActionButton imageButton;

    EditText tweetEditText;
    TextView countTextView;
    LinearLayout parentLayout;

    List<String> imgStringList = new ArrayList<>();

    TweetOkashiApplication mApp;

    public TweetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (TweetOkashiApplication)getActivity().getApplicationContext();
        Log.d(TAG,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        tweetButton = (FloatingActionButton)view.findViewById(R.id.tweet_button);
        imageButton = (FloatingActionButton)view.findViewById(R.id.img_add_button);
        tweetEditText = (EditText)view.findViewById(R.id.tweet_edit_text);
        countTextView = (TextView)view.findViewById(R.id.count_text);
        tweetEditText.addTextChangedListener(new TextCountWatcher(countTextView));
        parentLayout = (LinearLayout)view.findViewById(R.id.parent_layout);

        tweetButton.setOnClickListener(new OnTweetButtonClickListener());
        imageButton.setOnClickListener(new OnImageAddButtonClickListener());

        return view;
    }


    private class OnTweetButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG,"onClick");
            String text = tweetEditText.getText().toString();

            if(imgStringList.size() > 4){
                Toast.makeText(getActivity(),"写真は4枚までです",Toast.LENGTH_LONG).show();
            }

            if(text.length() > 140){
                Toast.makeText(getActivity(),"文字数オーバー(140字までです)",Toast.LENGTH_LONG).show();
                return;
            }
            if(text.length() <= 0){
                Toast.makeText(getActivity(),"文字を入力してください",Toast.LENGTH_SHORT).show();
                return;
            }

            TweetAsyncTask tweetAsyncTask = new TweetAsyncTask(mApp.getTwitterInstance(), text);

            if(imgStringList.size() == 0) {
                tweetAsyncTask.execute();
            }else{
                tweetAsyncTask.execute((String[])imgStringList.toArray(new String[0]));
            }
        }
    }

    private class OnImageAddButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_PIC);

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

                DeletableImageView imageView = new DeletableImageView(getActivity());
                imageView.setImageURI(uri);
                imageView.setOnViewDeleteListener(this);

                String path = cursor.getString(0);
                imgStringList.add(path);
                imageView.setTag(path);
                parentLayout.addView(imageView);
                cursor.close();
            }
        }
    }

    @Override
    public void OnDelete(View v) {
        String tag = (String)v.getTag();
        imgStringList.remove(tag);
        parentLayout.removeView(v);
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
