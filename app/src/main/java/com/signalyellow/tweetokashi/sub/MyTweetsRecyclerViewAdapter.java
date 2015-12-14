package com.signalyellow.tweetokashi.sub;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.sub.TweetsFragment.OnListFragmentInteractionListener;
import com.signalyellow.tweetokashi.sub.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTweetsRecyclerViewAdapter extends RecyclerView.Adapter<MyTweetsRecyclerViewAdapter.ViewHolder> {

    private final List<TweetData> mTweets;
    private final OnListFragmentInteractionListener mListener;

    public MyTweetsRecyclerViewAdapter(List<TweetData> items, OnListFragmentInteractionListener listener) {
        mTweets = items;
        mListener = listener;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tweet, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TweetData tweetData = mTweets.get(position);
        holder.mTweet = mTweets.get(position);
        holder.mNameView.setText(mTweets.get(position).name);
        holder.mScreenNameView.setText(mTweets.get(position).screenName);
        holder.mTextView.setText(mTweets.get(position).text);
        holder.mImageView.setImageUrl(mTweets.get(position).getProfileImageURL());
        holder.mHaikuView.setText(tweetData.getHaiku());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mTweet);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mScreenNameView;
        public final TextView mTextView;
        public final SmartImageView mImageView;
        public final TextView mHaikuView;
        public TweetData mTweet;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            this.mNameView = (TextView) view.findViewById(R.id.name);
            this.mScreenNameView = (TextView) view.findViewById(R.id.screen_name);
            this.mTextView = (TextView)view.findViewById(R.id.text);
            this.mImageView = (SmartImageView)view.findViewById(R.id.icon);
            this.mHaikuView = (TextView)view.findViewById(R.id.haikutext);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
