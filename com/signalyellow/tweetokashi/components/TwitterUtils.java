package com.signalyellow.tweetokashi.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.signalyellow.tweetokashi.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class TwitterUtils {

    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    public static String INTENT_TAG_TWEETDATA = "Simple_Tweet_Data";
    public static String INTENT_TAG_USERDATA = "Credintial_User";


    public static enum TWITTER_STATUS{
        SUCCESS,
        ERROR
    }

    public static Twitter getTwitterInstance(Context context) {
        String consumerKey = context.getString(R.string.consumer_key);
        String consumerSecret = context.getString(R.string.consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if(hasAccessToken(context)){
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }

        return twitter;
    }

    public static void storeAccessToken(Context context, AccessToken accessToken){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken());
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    public static AccessToken loadAccessToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN, null);
        String tokenSecret = preferences.getString(TOKEN_SECRET,null);

        if(token != null && tokenSecret != null){
            return new AccessToken(token,tokenSecret);
        }else{
            return null;
        }
    }

    public static void deleteAccessToken(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE).edit();
        editor.remove(TOKEN);
        editor.remove(TOKEN_SECRET);
        editor.commit();
    }

    public static boolean hasAccessToken(Context context){
        return loadAccessToken(context) != null;
    }
}
