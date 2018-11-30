package com.fugu.adapter;

/**
 * Created by Bhavya Rattan on 29/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int ITEM_TYPE_OTHER = 1;
    public static final int ITEM_TYPE_SELF = 2;
    public static final int ITEM_TYPE_RATING = 3;
    public  static final int FUGU_GALLERY_VIEW = -990;
    public  static final int FUGU_TEXT_VIEW = 15;
    public static final int FUGU_FORUM_VIEW = 17;
    public  static final int FUGU_QUICK_REPLY_VIEW = 16;

    abstract public int getType();

    public abstract boolean equals(Object obj);
}