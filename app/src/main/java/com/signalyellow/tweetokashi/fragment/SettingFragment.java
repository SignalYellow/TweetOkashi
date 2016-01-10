package com.signalyellow.tweetokashi.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.signalyellow.tweetokashi.R;


public class SettingFragment extends PreferenceFragment{


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_haiku);
    }
}
