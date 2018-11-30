package com.fugu.agent.model.LoginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */
class UserProperties {
    @SerializedName("terms_and_conditions")
    @Expose
    private Integer termsAndConditions;

    /**
     * Gets terms and conditions.
     *
     * @return the terms and conditions
     */
    public Integer getTermsAndConditions() {
        return termsAndConditions;
    }

    /**
     * Sets terms and conditions.
     *
     * @param termsAndConditions the terms and conditions
     */
    public void setTermsAndConditions(Integer termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
