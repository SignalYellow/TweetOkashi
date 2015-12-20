package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.signalyellow.tweetokashi.activity.MainActivity;
import com.signalyellow.tweetokashi.app.TweetOkashiApplication;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

public class LogoutActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context,Void entity ) {
        TwitterUtils.deleteAccessToken(context);
        Toast.makeText(context,"ログアウトしました",Toast.LENGTH_LONG).show();
        TweetOkashiApplication app = (TweetOkashiApplication)context.getApplicationContext();
        app.getHomeActivity().finish();
        context.startActivity(new Intent(context, MainActivity.class));
        return true;
    }
}
