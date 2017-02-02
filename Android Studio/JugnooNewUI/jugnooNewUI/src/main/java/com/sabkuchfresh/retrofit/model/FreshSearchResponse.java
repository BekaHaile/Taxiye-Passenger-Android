package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ankit on 23/01/17.
 */

public class FreshSearchResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("item_info")
    @Expose
    private List<SuperCategoriesData.SuperCategory> superCategories = null;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SuperCategoriesData.SuperCategory> getSuperCategories() {
        return superCategories;
    }

    public void setSuperCategories(List<SuperCategoriesData.SuperCategory> superCategories) {
        this.superCategories = superCategories;
    }
}
