package com.signalyellow.tweetokashi.activity.nav;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.signalyellow.tweetokashi.activity.MainActivity;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

public class LogoutActionHandler implements ItemActionHandler<Void> {
    @Override
    public boolean handle(Context context,Void entity ) {
        TwitterUtils.deleteAccessToken(context);
        Toast.makeText(context,"ログアウトしました",Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context, MainActivity.class));
        return true;
    }
}
