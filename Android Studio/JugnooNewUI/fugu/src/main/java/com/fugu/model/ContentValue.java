package com.fugu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Content value.
 */
public class ContentValue {
    @SerializedName("button_id")
    @Expose
    private String buttonId;
    @SerializedName("button_type")
    @Expose
    private String buttonType;
    @SerializedName("button_title")
    @Expose
    private String buttonTitle;
    @SerializedName("payload")
    @Expose
    private String payload;
    @SerializedName("business_id")
    @Expose
    private int businessId;
    @SerializedName("bot_id")
    @Expose
    private String botId;
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("action_id")
    @Expose
    private String actionId;
    @SerializedName("params")
    @Expose
    private List<String> params = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("data_type")
    @Expose
    private ArrayList<String> data_type;
    @SerializedName("questions")
    @Expose
    private ArrayList<String> questions;

    private String textValue;

    private String countryCode;

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<String> getData_type() {
        return data_type;
    }

    public void setData_type(ArrayList<String> data_type) {
        this.data_type = data_type;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public boolean isDeleted(boolean isDeleted) {
        return this.isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


}
