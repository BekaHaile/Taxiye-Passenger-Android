package com.jugnoo.pay.models;

import java.util.HashMap;
import java.util.Map;


public class TokenGeneratedResponse {

    private Integer flag;
    private String token;
    private String mid;
    private String mkey;

    public String getOtp_phone_no() {
        return otp_phone_no;
    }

    public void setOtp_phone_no(String otp_phone_no) {
        this.otp_phone_no = otp_phone_no;
    }

    private String message;
    private String otp_phone_no;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     * The token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     *
     * @return
     * The mid
     */
    public String getMid() {
        return mid;
    }

    /**
     *
     * @param mid
     * The mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     *
     * @return
     * The mkey
     */
    public String getMkey() {
        return mkey;
    }

    /**
     *
     * @param mkey
     * The mkey
     */
    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}