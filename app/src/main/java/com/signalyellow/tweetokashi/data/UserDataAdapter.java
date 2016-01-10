package com.signalyellow.tweetokashi.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.manager.HaikuManager;
import com.signalyellow.tweetokashi.manager.LoadBitmapManager;


public class UserDataAdapter extends ArrayAdapter<UserData>{


    private HaikuManager mHaikuManager;

    public UserDataAdapter(Context context){
        this(context, R.layout.item_user);
    }

    public UserDataAdapter(Context context, int resource) {
        super(context, resource);

        TweetOkashiApplication app = (TweetOkashiApplication)context.getApplicationContext();
        mHaikuManager = app.getHaikuManger();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final UserData data  = getItem(position);

        UserDataViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            RelativeLayout root = new RelativeLayout(getContext());
            view = inflater.inflate(R.layout.item_user,root);
            viewHolder = new UserDataViewHolder();

            viewHolder.textUserName = (TextView)view.findViewById(R.id.name);
            viewHolder.textScreenName = (TextView)view.findViewById(R.id.screen_name);
            viewHolder.imageThumbnail = (SmartImageView)view.findViewById(R.id.icon);
            viewHolder.textDescription = (TextView)view.findViewById(R.id.introduction);
            viewHolder.textHaiku  = (TextView)view.findViewById(R.id.haiku_text);

            view.setTag(viewHolder);
        }else{
            viewHolder = (UserDataViewHolder)view.getTag();
        }

        viewHolder.textUserName.setText(data.getUserName());
        viewHolder.textScreenName.setText(data.getAtScreenName());
        viewHolder.textDescription.setText(data.getDescription());

        viewHolder.imageThumbnail.setImageResource(R.drawable.icon_reload);
        viewHolder.imageThumbnail.setImageUrl(data.getProfileImageURL());

        viewHolder.textHaiku.setVisibility(View.GONE);

        return view;
    }
}
