package com.signalyellow.tweetokashi.sub;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.signalyellow.tweetokashi.activity.FavoriteListActivity;
import com.signalyellow.tweetokashi.async.FavoriteAsyncTask;
import com.signalyellow.tweetokashi.async.RetweetAsyncTask;
import com.signalyellow.tweetokashi.data.TweetData;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TweetDataDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TweetDataDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetDataDialogFragment extends DialogFragment {

    private static final String TAG = "TweetDialog";
    private static final String ARG_TWEET_DATA = "tweetData1";

    private TweetData mData;
    private OnFragmentInteractionListener mListener;

    public TweetDataDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param data TweetData
     * @return A new instance of fragment TweetDataDialogFragment.
     */
    public static TweetDataDialogFragment newInstance(TweetData data) {
        TweetDataDialogFragment fragment = new TweetDataDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET_DATA,data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = (TweetData)getArguments().getSerializable(ARG_TWEET_DATA);
        }
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tweet_data_dialog, container, false);
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        CharSequence[] items = {"リツイート","いいね","マイリストに保存","詳細"};
        if(!mData.isRetweetable()){
            items[0] = "リツイート解除";
        }
        if(mData.isFavoritedByMe()){
            items[1] = "いいね取り消し";
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(mData.getName())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Log.d(TAG,"リツイート");
                                new RetweetAsyncTask(TwitterUtils.getTwitterInstance(getActivity()),
                                        mData,
                                        !mData.isRetweetable() ? RetweetAsyncTask.RETWEET_STATUS.DELETE : RetweetAsyncTask.RETWEET_STATUS.RETWEET).execute();
                                break;
                            case 1:
                                Log.d(TAG,"いいね");
                                new FavoriteAsyncTask(TwitterUtils.getTwitterInstance(getActivity()),
                                        mData,
                                        mData.isFavoritedByMe() ? FavoriteAsyncTask.FAVORITE_STATUS.DELETE : FavoriteAsyncTask.FAVORITE_STATUS.FAVORITE).execute();
                                break;
                            case 2:
                                Log.d(TAG,"リスト追加");
                                break;
                            case 3:
                                Log.d(TAG,"詳細");
                                break;
                            default:
                                Log.e(TAG,"Error!");
                                break;
                        }
                    }
                })
                .create();
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
