package com.jugnoo.pay.models;

/**
 * Created by ankit on 06/12/16.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchPaymentAddressResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("vpa_list")
    @Expose
    private List<VpaList> vpaList = null;

    /**
     *
     * @return
     * The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     *
     * @param flag
     * The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     *
     * @return
     * The vpaList
     */
    public List<VpaList> getVpaList() {
        return vpaList;
    }

    /**
     *
     * @param vpaList
     * The vpa_list
     */
    public void setVpaList(List<VpaList> vpaList) {
        this.vpaList = vpaList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class VpaList {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("vpa")
        @Expose
        private String vpa;

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The vpa
         */
        public String getVpa() {
            return vpa;
        }

        /**
         *
         * @param vpa
         * The vpa
         */
        public void setVpa(String vpa) {
            this.vpa = vpa;
        }

    }

}


