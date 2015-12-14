package com.signalyellow.tweetokashi.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.HaikuStatus;

import java.io.IOException;
import java.util.List;

import jp.signalyellow.haiku.HaikuGeneratorByGooAPI;
import jp.signalyellow.haiku.MorphologicalAnalysisByGooAPI;
import jp.signalyellow.haiku.Word;

/**
 * Created by shohei on 15/12/14.
 */
public class HaikuTextAsyncTask extends AsyncTask<String,Void,String>{

    static final String TAG = "HaikuTextAsyncTask";

    TextView mTextView;
    Context mContext;
    public HaikuTextAsyncTask(TextView view,Context context) {
        mTextView = view;
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String text = strings[0];

        MorphologicalAnalysisByGooAPI analyzer = new MorphologicalAnalysisByGooAPI(mContext.getString(R.string.goo_id));
        try {
            List<Word> list = analyzer.analyze(text);
            return new HaikuGeneratorByGooAPI(list).generateHaikuStrictly();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s == null){
            mTextView.setText("できませんでした");
            return;
        }
        mTextView.setText(s);
    }
}
