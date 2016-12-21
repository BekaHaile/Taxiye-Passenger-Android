package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shankar on 4/29/16.
 */
public class JeanieIntroDialogContent {

    @SerializedName("autos_text")
    @Expose
    private String autosText;
    @SerializedName("meals_text")
    @Expose
    private String mealsText;
    @SerializedName("fresh_text")
    @Expose
    private String freshText;
    @SerializedName("delivery_text")
    @Expose
    private String deliveryText;

    public String getAutosText() {
        return autosText;
    }

    public void setAutosText(String autosText) {
        this.autosText = autosText;
    }

    public String getMealsText() {
        return mealsText;
    }

    public void setMealsText(String mealsText) {
        this.mealsText = mealsText;
    }

    public String getFreshText() {
        return freshText;
    }

    public void setFreshText(String freshText) {
        this.freshText = freshText;
    }

    public String getDeliveryText() {
        return deliveryText;
    }

    public void setDeliveryText(String deliveryText) {
        this.deliveryText = deliveryText;
    }
}
