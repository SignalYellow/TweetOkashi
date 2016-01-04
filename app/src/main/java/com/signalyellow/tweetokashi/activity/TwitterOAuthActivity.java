package com.signalyellow.tweetokashi.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.twitter.TwitterUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class TwitterOAuthActivity extends AppCompatActivity {

    private static final String TAG = "OauthActivity";

    private String mCallbackURL;
    private Twitter mTwitter;
    private static RequestToken mRequestToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_oauth);

        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setSubtitle(R.string.login_activity_subtitle);
        }
        mCallbackURL = getString(R.string.twitter_callback_url);
        prepareOauth();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        prepareOauth();
    }

    private void prepareOauth(){

        mTwitter = TwitterUtils.getTwitterInstance(this);
        mTwitter.setOAuthAccessToken(null);
        findViewById(R.id.oauthButton).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startAuthorize();
                    }
                }
        );
    }

    private void startAuthorize(){
        AsyncTask<Void,Void,String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try{
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    return  mRequestToken.getAuthorizationURL();
                }catch (TwitterException e){
                    Log.d(TAG,e.toString());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String url) {
                if(url != null){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }else {
                    showToast(getString(R.string.error_normal));
                }
            }
        };

        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            startActivity(new Intent(getApplicationContext(),TwitterOAuthActivity.class));
            finish();
            return;
        }

        //Log.d(TAG,intent.getData().toString());
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        if (verifier == null) {
            showToast(getString(R.string.error_normal));
            startActivity(new Intent(getApplicationContext(), TwitterOAuthActivity.class));
            finish();
            return;
        }

        new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    Log.d(TAG, e.toString());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    showToast(getString(R.string.success_login));
                    successOAuth(accessToken);
                } else {
                    showToast(getString(R.string.error_normal));
                }
            }
        }.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken){
        TwitterUtils.storeAccessToken(this, accessToken);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text , Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_twitter_oauth, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
