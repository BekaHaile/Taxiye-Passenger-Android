package com.jugnoo.pay.models;

import java.io.Serializable;

/**
 * Created by socomo on 11/28/2016.
 */

public class AccountManagementResponse implements Serializable
{
    private Integer flag;
    private String mid;
    private String mkey;
    private String token;
    private String vpa;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }
}
