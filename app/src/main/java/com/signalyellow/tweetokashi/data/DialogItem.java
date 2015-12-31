package com.signalyellow.tweetokashi.data;

import android.os.Bundle;

import com.signalyellow.tweetokashi.R;

/**
 * Created by shohei on 15/12/31.
 */
public class DialogItem {

    public enum STATUS {
        RETWEET,
        UNRETWEET,
        REPLY,
        DELETE,
        MEDIA_URL,
        WEB_URL,
        FAV,
        UNFAV,
        DETAIL,
    }

    STATUS mStatus;

    public DialogItem( STATUS status) {
        mStatus = status;
    }

    public static String getTitleByStatus(STATUS status){
        switch (status){
            case REPLY:
                return "返信する";
            case RETWEET:
                return "リツイート";
            case UNRETWEET:
                return "リツイートを取り消す";
            case FAV:
                return "いいね";
            case UNFAV:
                return "いいね取り消し";
            case DETAIL:
                return "ツイート詳細";
            case DELETE:
                return "ツイート削除";
            default:
                return null;
        }
    }

    public static int getDrawableIdByStatus(STATUS status){
        switch (status){
            case REPLY:
                return R.drawable.icon_delete;
            case RETWEET:
                return R.drawable.icon_retweet;
            case UNRETWEET:
                return R.drawable.icon_delete;
            case FAV:
                return R.drawable.icon_heart;
            case UNFAV:
                return R.drawable.icon_delete;
            case DETAIL:
                return R.drawable.ic_menu_manage;
            case DELETE:
                return R.drawable.icon_delete;
            default:
                return -1;
        }
    }

    public String getText(){
        return getTitleByStatus(mStatus);
    }

    public int getDrawableId(){
        return getDrawableIdByStatus(mStatus);
    }

    public STATUS getStatus() {
        return mStatus;
    }


}
