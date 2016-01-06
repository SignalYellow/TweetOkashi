package com.signalyellow.tweetokashi.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.data.DialogItemAdapter;
import com.signalyellow.tweetokashi.data.STATUS;
import com.signalyellow.tweetokashi.data.UserData;
import com.signalyellow.tweetokashi.listener.OnFragmentResultListener;

public class UserDataDialogFragment extends DialogFragment {

    private static final String TAG = UserDataDialogFragment.class.getSimpleName();
    private static final String ARG_USER_DATA = "USER_DATA";

    private UserData mUserData;

    private OnFragmentResultListener mListener;

    public UserDataDialogFragment() {
        // should be empty
    }

    public static UserDataDialogFragment newInstance(UserData data) {
        UserDataDialogFragment fragment = new UserDataDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserData = (UserData)getArguments().getSerializable(ARG_USER_DATA);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_tweet_data_dialog, new FrameLayout(getActivity().getApplicationContext()), false);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        DialogItemAdapter mAdapter = new DialogItemAdapter(getActivity().getApplicationContext());
        listView.setAdapter(mAdapter);
        setDataToAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                STATUS item = (STATUS) parent.getItemAtPosition(position);
                mListener.onUserDataDialogResult(mUserData, item);
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle(mUserData.getUserName());
        builder.setView(view);
        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeTimelineFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setDataToAdapter(DialogItemAdapter adapter){
        adapter.add(STATUS.USER_TIMELINE);
        adapter.add(STATUS.USER_FAVORITE);
    }
}
