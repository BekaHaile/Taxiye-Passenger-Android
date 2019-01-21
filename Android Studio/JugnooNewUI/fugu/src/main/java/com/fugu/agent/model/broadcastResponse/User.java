package com.fugu.agent.model.broadcastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 25/07/18.
 *
 * @author gurmail
 */
public class User {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("tag_id")
    @Expose
    private Integer tagId;

    private boolean isSelected;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }


    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
