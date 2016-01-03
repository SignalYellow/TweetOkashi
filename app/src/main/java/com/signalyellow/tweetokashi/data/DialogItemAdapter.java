package com.signalyellow.tweetokashi.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.signalyellow.tweetokashi.R;

public class DialogItemAdapter extends ArrayAdapter<STATUS>{

    public DialogItemAdapter(Context context){
        this(context, R.layout.item_tweet);
    }

    public DialogItemAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final STATUS item = getItem(position);

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout root = new LinearLayout(getContext());
        view = inflater.inflate(R.layout.item_dialog,root);

        TextView textView = (TextView)view.findViewById(R.id.text);
        ImageView imageView = (ImageView)view.findViewById(R.id.icon);

        textView.setText(item.getText());
        Drawable drawable = new ResourcesCompat().getDrawable(getContext().getResources(),item.getDrawableId(),null);
        imageView.setImageDrawable(drawable);

        return view;
    }
}
