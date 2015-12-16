package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.signalyellow.tweetokashi.components.TwitterUtils;
import com.signalyellow.tweetokashi.sub.HomeTimelineActivity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }else{
            startActivity(new Intent(this, HomeTimelineActivity.class));
            finish();
        }
    }
}
