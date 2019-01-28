package com.fugu.agent.model.broadcastResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 25/07/18.
 *
 * @author gurmail
 */
public class Tag {

    @SerializedName("tag_id")
    @Expose
    private Integer tagId;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("tag_type")
    @Expose
    private String tagType;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    private boolean isSelected;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
