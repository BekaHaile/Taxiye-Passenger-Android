package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.datastructure.UserContactObject;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 2/6/18.
 */

public class ContactResponseModel extends FeedCommonResponse {

    @SerializedName("contacts_list")
    private ArrayList<UserContactObject> contacts;

    public ArrayList<UserContactObject> getContacts() {
        return contacts;
    }

    public void setContacts(final ArrayList<UserContactObject> contacts) {
        this.contacts = contacts;
    }
}
