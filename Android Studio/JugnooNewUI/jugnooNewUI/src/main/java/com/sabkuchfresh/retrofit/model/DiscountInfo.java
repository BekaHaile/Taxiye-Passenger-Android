package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 10/07/17.
 */

public  class DiscountInfo {



    @SerializedName("start_time")
    private String discountStartTime;

    @SerializedName("end_time")
    private String discountEndTime;

    @SerializedName("discount")
    private long discountPrice;

    @SerializedName("text")
    private String textToDisplay;

    @SerializedName("is_active")
    private int isActive;

    @SerializedName("current_time_stamp")
    private String currentTimeStamp;

    @SerializedName("threshold_time")
    private Double threshHoldTime;

    public String getDiscountStartTime() {
        return discountStartTime;
    }

    public String getDiscountEndTime() {
        return discountEndTime;
    }

    public Long getDiscountPrice() {
        return discountPrice;
    }

    public String getTextToDisplay() {
        return textToDisplay;
    }

    public boolean getIsActive() {
        return isActive==1;
    }

    public String getCurrentDate() {
        return currentTimeStamp;
    }

    /**
     *
     * @return Time in mins to show counter
     */
    public Double getThreshHoldTime() {
        if(threshHoldTime==null)
            return  60.0;
        return threshHoldTime;
    }
}
