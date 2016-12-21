package com.jugnoo.pay.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ankit on 30/09/16.
 */
public class VerifyRegisterResponse {

    private String pgMeTrnRefNo;
    private String yblRefId;
    private String virtualAddress;
    private String status;
    private String statusdesc;
    private String registrationDate;

    // new variables for new Yes bank SDK
    private  String accountNo;
    private  String ifsc;
    private  String accName;
    //-----------------

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPgMeTrnRefNo() {
        return pgMeTrnRefNo;
    }

    public void setPgMeTrnRefNo(String pgMeTrnRefNo) {
        this.pgMeTrnRefNo = pgMeTrnRefNo;
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

    public String getVirtualAddress() {
        return virtualAddress;
    }

    public void setVirtualAddress(String virtualAddress) {
        this.virtualAddress = virtualAddress;
    }

    public String getYblRefId() {
        return yblRefId;
    }

    public void setYblRefId(String yblRefId) {
        this.yblRefId = yblRefId;
    }

    // new variables get-set methods
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }

    public String getIfsc() { return ifsc; }
    public void setIfsc(String ifsc) { this.ifsc = ifsc; }

    public String getAccName() { return accName; }
    public void setAccName(String accName) { this.accName = accName; }
    //-------------------------

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pgMeTrnRefNo", pgMeTrnRefNo);
            jsonObject.put("yblRefId", yblRefId);
            jsonObject.put("virtualAddress", virtualAddress);
            jsonObject.put("status", status);
            jsonObject.put("statusdesc", statusdesc);
            jsonObject.put("registrationDate", registrationDate);

            // new variables for new yes bank SDK
            jsonObject.put("accountNo", accountNo);
            jsonObject.put("ifsc", ifsc);
            jsonObject.put("accName", accName);
            //------------

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
