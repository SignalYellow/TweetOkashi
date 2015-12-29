package com.signalyellow.tweetokashi.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;


/**
 * Created by shohei on 15/08/17.
 */
public class SettingUtils {
    private static final String PREF_NAME = "application_settings";
    private static final String CAN_HAIKU = "haiku_create";

    public static boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void storeHaikuCreationBool(Context context, boolean possibility){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CAN_HAIKU,possibility);
        editor.apply();
    }

    public static boolean canCreateHaiku(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return  preferences.getBoolean(CAN_HAIKU,true);
    }
}
