package com.signalyellow.tweetokashi.sub;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.components.HaikuStatus;
import com.signalyellow.tweetokashi.components.SimpleTweetData;
import com.signalyellow.tweetokashi.sub.HaikuTextAsyncTask;
import com.signalyellow.tweetokashi.sub.ListItemViewHolder;
import com.signalyellow.tweetokashi.sub.TweetData;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by shohei on 15/12/14.
 */
public class TweetDataAdapter extends ArrayAdapter<TweetData>{

    private LayoutInflater mInflater;

    public TweetDataAdapter(Context context){
        super(context, R.layout.list_item_tweet);
        mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final TweetData data = getItem(position);
        ListItemViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_tweet,null);
            viewHolder = new ListItemViewHolder();

            viewHolder.textUserName = (TextView)view.findViewById(R.id.name);
            viewHolder.textScreenName = (TextView)view.findViewById(R.id.screen_name);
            viewHolder.textContent = (TextView)view.findViewById(R.id.text);
            viewHolder.textHaiku = (TextView)view.findViewById(R.id.haikutext);
            viewHolder.imageThumbnail = (SmartImageView)view.findViewById(R.id.icon);
            viewHolder.textDate = (TextView)view.findViewById(R.id.datetime);
            viewHolder.textRetweetedCount = (TextView)view.findViewById(R.id.RTcount);
            viewHolder.textFavoritedCount = (TextView)view.findViewById(R.id.FAVCount);
            new HaikuTextAsyncTask(viewHolder.textHaiku,getContext()).execute(data.getText());

            view.setTag(viewHolder);
        }else{
            viewHolder = (ListItemViewHolder)view.getTag();
        }

        viewHolder.textScreenName.setText("@" + data.getScreenName());
        viewHolder.textUserName.setText(data.getName());
        viewHolder.textContent.setText(data.getText());
        viewHolder.imageThumbnail.setImageUrl(data.getProfileImageURL());

        return view;
    }


}
