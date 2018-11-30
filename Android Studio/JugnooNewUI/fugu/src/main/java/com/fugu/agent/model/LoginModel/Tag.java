package com.fugu.agent.model.LoginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */
public class Tag {
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
}
