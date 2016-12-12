package com.jugnoo.pay.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 12/12/16.
 */
public class AccountMgmtCallbackRequest {
    @SerializedName("pgMeTrnRefNo")
    @Expose
    private String pgMeTrnRefNo;
    @SerializedName("yblRefId")
    @Expose
    private String yblRefId;
    @SerializedName("virtualAddress")
    @Expose
    private String virtualAddress;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusdesc")
    @Expose
    private String statusdesc;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("accountNo")
    @Expose
    private String accountNo;
    @SerializedName("ifsc")
    @Expose
    private String ifsc;
    @SerializedName("accName")
    @Expose
    private String accName;

    public AccountMgmtCallbackRequest(String pgMeTrnRefNo, String yblRefId, String virtualAddress, String status, String statusdesc, String date, String accountNo, String ifsc, String accName) {
        this.pgMeTrnRefNo = pgMeTrnRefNo;
        this.yblRefId = yblRefId;
        this.virtualAddress = virtualAddress;
        this.status = status;
        this.statusdesc = statusdesc;
        this.date = date;
        this.accountNo = accountNo;
        this.ifsc = ifsc;
        this.accName = accName;
    }

    public String getPgMeTrnRefNo() {
        return pgMeTrnRefNo;
    }

    public void setPgMeTrnRefNo(String pgMeTrnRefNo) {
        this.pgMeTrnRefNo = pgMeTrnRefNo;
    }

    public String getYblRefId() {
        return yblRefId;
    }

    public void setYblRefId(String yblRefId) {
        this.yblRefId = yblRefId;
    }

    public String getVirtualAddress() {
        return virtualAddress;
    }

    public void setVirtualAddress(String virtualAddress) {
        this.virtualAddress = virtualAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusdesc() {
        return statusdesc;
    }

    public void setStatusdesc(String statusdesc) {
        this.statusdesc = statusdesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, AccountMgmtCallbackRequest.class);
    }
}
