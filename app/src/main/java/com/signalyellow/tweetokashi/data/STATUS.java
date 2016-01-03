package com.signalyellow.tweetokashi.data;

import com.signalyellow.tweetokashi.R;

public enum STATUS {
    RETWEET("リツイート", R.drawable.icon_retweet_64),
    UNRETWEET("リツイートを取り消す",R.drawable.icon_not_retweet_64),
    HAIKURETWEET("俳句リツイート",R.drawable.icon_haiku_retweet_64),
    REPLY("返信する",R.drawable.icon_reply_64),
    DELETE("削除する",R.drawable.icon_delete_64),
    FAV("いいね",R.drawable.icon_favorite_64),
    UNFAV("いいね取り消し",R.drawable.icon_not_favorite_64),
    DETAIL("詳細",R.drawable.icon_books);

    private final String text;
    private final int drawableId;

    STATUS(String text, int drawableId) {
        this.text = text;
        this.drawableId = drawableId;
    }

    public String getText() {
        return text;
    }

    public int getDrawableId() {
        return drawableId;
    }
}
