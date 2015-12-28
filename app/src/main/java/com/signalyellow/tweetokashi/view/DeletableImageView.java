package com.signalyellow.tweetokashi.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.signalyellow.tweetokashi.R;


/**
 * Created by shohei on 15/12/27.
 */
public class DeletableImageView extends FrameLayout {

    private static String TAG = "deletableImage";

    private View mView;
    private ImageView mImageView;
    private Button mButton;
    private OnViewDeleteListener mListener;


    public DeletableImageView(Context context) {
        this(context,null);
    }

    public DeletableImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public DeletableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DeletableImageView,
                defStyleAttr, 0);

        Drawable d = a.getDrawable(R.styleable.DeletableImageView_src);
        a.recycle();

        mView = LayoutInflater.from(context).inflate(R.layout.deletable_image_view, this);
        mButton = (Button)mView.findViewById(R.id.delete_button);
        mButton.setOnClickListener(new OnDeleteButtonClickListener(this));
        mImageView = (ImageView)mView.findViewById(R.id.image_view_deletable);

        if(d != null) mImageView.setImageDrawable(d);
    }

    private class OnDeleteButtonClickListener implements OnClickListener{
        View mView;

        public OnDeleteButtonClickListener(View view) {
            mView = view;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) mListener.OnDelete(mView);
        }
    }

    public void setOnViewDeleteListener(OnViewDeleteListener listener){
        mListener = listener;
    }

    public void setImageURI(Uri uri){
        mImageView.setImageURI(uri);
    }


    public interface OnViewDeleteListener{
        void OnDelete(View v);
    }

}
