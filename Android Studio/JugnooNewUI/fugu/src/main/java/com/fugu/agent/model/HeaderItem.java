package com.fugu.agent.model;

import androidx.annotation.NonNull;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class HeaderItem extends ListItem {

    @NonNull
    private String date;

    public HeaderItem(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    // here getters and setters
    // for title and so on, built
    // using date

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}