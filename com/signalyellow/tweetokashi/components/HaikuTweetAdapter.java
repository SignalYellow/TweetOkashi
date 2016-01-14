package com.signalyellow.tweetokashi.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;

import org.w3c.dom.Text;

import twitter4j.Status;

/**
 * Created by shohei on 15/08/15.
 */
public class HaikuTweetAdapter extends ArrayAdapter<HaikuStatus> {
    private LayoutInflater mInflater;

    public HaikuTweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tweet, null);
        }

        HaikuStatus status = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(status.getUserName());

        TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
        screenName.setText("@" + status.getScreenName());

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(status.getText());


        SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
        icon.setImageUrl(status.getProfileImageURL());

        TextView haikuText = (TextView) convertView.findViewById(R.id.haikutext);
        haikuText.setText(status.getHaikuText());

        TextView dateText = (TextView) convertView.findViewById(R.id.datetime);
        dateText.setText(SimpleTweetData.getDate(status.getCreatedAt()));


        TextView RTCount = (TextView) convertView.findViewById(R.id.RTcount);
        RTCount.setText(String.valueOf(status.getRetweetCount()));

        TextView FAVCount = (TextView)convertView.findViewById(R.id.FAVCount);
        FAVCount.setText(String.valueOf(status.getFavoriteCount()));

        return convertView;
    }
}