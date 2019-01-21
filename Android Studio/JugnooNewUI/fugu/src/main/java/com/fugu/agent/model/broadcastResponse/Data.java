package com.fugu.agent.model.broadcastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 25/07/18.
 *
 * @author gurmail
 */
public class Data {

    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
