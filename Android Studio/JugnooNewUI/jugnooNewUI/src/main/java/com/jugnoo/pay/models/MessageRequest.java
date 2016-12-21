package com.jugnoo.pay.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class MessageRequest implements Serializable{
    String pgMeTrnRefNo;
    String orderNo;
    String txnAmount;
    String tranAuthdate;
    String status;
    String statusDesc;
    String responsecode;
    String approvalCode;
    String payerVA;
    String npciTxnId;
    String refId;

    // new code added on 21-11-2016
    String payerAccountNo;
    String payerIfsc;
    String payerAccName;

    public String getPayerAccountNo() { return payerAccountNo; }
    public void setPayerAccountNo(String payerAccountNo) { this.payerAccountNo = payerAccountNo; }

    public String getPayerIfsc() { return payerIfsc; }
    public void setPayerIfsc(String payerIfsc) { this.payerIfsc = payerIfsc; }

    public String getPayerAccName() { return payerAccName; }
    public void setPayerAccName(String payerAccName) { this.payerAccName = payerAccName; }
    //--------------------

    public String getPgMeTrnRefNo() {
        return pgMeTrnRefNo;
    }

    public void setPgMeTrnRefNo(String pgMeTrnRefNo) {
        this.pgMeTrnRefNo = pgMeTrnRefNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getTranAuthdate() {
        return tranAuthdate;
    }

    public void setTranAuthdate(String tranAuthdate) {
        this.tranAuthdate = tranAuthdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getPayerVA() {
        return payerVA;
    }

    public void setPayerVA(String payerVA) {
        this.payerVA = payerVA;
    }

    public String getNpciTxnId() {
        return npciTxnId;
    }

    public void setNpciTxnId(String npciTxnId) {
        this.npciTxnId = npciTxnId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("approvalCode", approvalCode);
            jsonObject.put("npciTxnId", npciTxnId);
            jsonObject.put("orderNo", orderNo);
            jsonObject.put("payerVA", payerVA);
            jsonObject.put("pgMeTrnRefNo", pgMeTrnRefNo);
            jsonObject.put("refId", refId);
            jsonObject.put("responsecode", responsecode);
            jsonObject.put("status", status);
            jsonObject.put("statusDesc", statusDesc);
            jsonObject.put("tranAuthdate", tranAuthdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject.toString();
    }
}
