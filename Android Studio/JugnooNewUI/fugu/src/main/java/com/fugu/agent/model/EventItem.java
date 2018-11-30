package com.fugu.agent.model;

import android.support.annotation.NonNull;

import static com.fugu.constant.FuguAppConstant.ASSIGNMENT_MESSAGE;

public class EventItem extends ListItem {

    @NonNull
    private Message event;

    public EventItem(@NonNull Message event) {
        this.event = event;
    }

    @NonNull
    public Message getEvent() {
        return event;
    }

    @Override
    public int getType() {
        if(event.getMessageType() == ASSIGNMENT_MESSAGE)
            return ITEM_ASSIGNMENT;
        else if (event.isSelf())
            return ITEM_TYPE_SELF;
        else if(event.getUserType() == 0 || event.getUserType() == 2)
            return ITEM_TYPE_SELF;
        else
            return ITEM_TYPE_OTHER;
    }
}
