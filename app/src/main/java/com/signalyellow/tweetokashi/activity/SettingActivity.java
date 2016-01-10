package com.signalyellow.tweetokashi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragment_container) != null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SettingFragment(),SettingFragment.class.getSimpleName())
                    .commit();
        }
    }
}
