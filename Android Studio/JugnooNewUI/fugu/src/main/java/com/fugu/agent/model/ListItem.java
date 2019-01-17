package com.fugu.agent.model;

/**
 * Created by Bhavya Rattan on 29/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int ITEM_TYPE_OTHER = 1;
    public static final int ITEM_TYPE_SELF = 2;
    public static final int ITEM_ASSIGNMENT = 3;

    public static final int FUGU_VIDEO_CALL_VIEW = 18;
    public static final int FUGU_OTHER_VIDEO_CALL_VIEW = 19;


    abstract public int getType();
}