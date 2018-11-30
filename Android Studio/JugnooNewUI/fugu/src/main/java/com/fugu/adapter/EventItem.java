package com.fugu.adapter;

import android.support.annotation.NonNull;

import com.fugu.model.Message;


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

    // here getters and setters
    // for title and so on, built
    // using event

    @Override
    public int getType() {
        if (event.getMessageType() == FUGU_FORUM_VIEW)
            return FUGU_FORUM_VIEW;
        else if (event.getMessageType() == FUGU_GALLERY_VIEW)
            return FUGU_GALLERY_VIEW;
        else if (event.getMessageType() == FUGU_QUICK_REPLY_VIEW && (event.getValues() == null || event.getValues().size() == 0))
            return FUGU_QUICK_REPLY_VIEW;
        else if (event.getMessageType() == FUGU_TEXT_VIEW)
            return ITEM_TYPE_OTHER;
        else if (event.isSelf())
            return ITEM_TYPE_SELF;
        else if (event.isRating())
            return ITEM_TYPE_RATING;
        else
            return ITEM_TYPE_OTHER;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventItem) {
            Message message = ((EventItem) obj).getEvent();
//            if (message.getId() != null && getEvent().getId() != null) {

            try {
                if (message.getId() != null && getEvent().getId() != null) {
                    return message.getId().equals(getEvent().getId());
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return false;
    }
}
