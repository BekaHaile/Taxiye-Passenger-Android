package com.fugu.agent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */
public class TagData {
    @SerializedName("tag_id")
    @Expose
    private String tag_id;
    @SerializedName("tag_name")
    @Expose
    private String tag_name;
    @SerializedName("color_code")
    @Expose
    private String color_code;
    @SerializedName("is_enabled")
    @Expose
    private String is_enabled;

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(String is_enabled) {
        this.is_enabled = is_enabled;
    }

}
