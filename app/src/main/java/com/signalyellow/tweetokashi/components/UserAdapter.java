package com.signalyellow.tweetokashi.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;

import twitter4j.User;

/**
 * Created by shohei on 15/08/22.
 */
public class UserAdapter extends ArrayAdapter<HaikuUserStatus>{
    private LayoutInflater mInflater;

    public UserAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_user,null);
        }

        HaikuUserStatus status = getItem(position);
        User user = status.getUser();

        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(user.getName());

        TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
        screenName.setText("@" + user.getScreenName());

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(user.getDescription());


        SmartImageView icon = (SmartImageView)convertView.findViewById(R.id.icon);
        icon.setImageUrl(user.getProfileImageURL());

        TextView haikuText = (TextView)convertView.findViewById(R.id.haiku_text);
        haikuText.setText(status.getHaikuText());

        return convertView;
    }
}
