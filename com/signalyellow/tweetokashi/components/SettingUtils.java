package com.signalyellow.tweetokashi.components;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by shohei on 15/08/17.
 */
public class SettingUtils {
    private static final String PREF_NAME = "application_settings";
    private static final String CAN_HAIKU = "haiku_create";

    public static void storeHaikuCreationBool(Context context, boolean possibility){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CAN_HAIKU,possibility);
        editor.commit();
    }

    public static boolean canCreateHaiku(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return  preferences.getBoolean(CAN_HAIKU,true);
    }
}
