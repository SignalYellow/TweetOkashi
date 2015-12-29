package com.signalyellow.tweetokashi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.signalyellow.tweetokashi.R;
import com.signalyellow.tweetokashi.data.SettingUtils;

public class SettingActivity extends Activity {
    static final String TAG = "SettingActivity";

    boolean isCheckBoxChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        android.app.ActionBar bar = getActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setSubtitle(R.string.setting_activity_subtitle);
        }

        final CheckBox checkBox = (CheckBox)findViewById(R.id.setting_checkBox_haiku);
        checkBox.setChecked(!SettingUtils.canCreateHaiku(getApplicationContext()));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckBoxChanged = true;
            }
        });

        Button btn_save = (Button)findViewById(R.id.setting_btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheckBoxChanged){
                    boolean canCreate = !checkBox.isChecked();
                    SettingUtils.storeHaikuCreationBool(getApplicationContext(),canCreate);
                }
                finish();
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.setting_btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"ondestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
