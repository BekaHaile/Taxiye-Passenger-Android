package com.jugnoo.pay.models;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */

import java.util.HashMap;
import java.util.Map;


public class SendMoneyResponse {

    private Integer flag;
    private String message;
    private TxnDetails txnDetails;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

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
     * The txnDetails
     */
    public TxnDetails getTxnDetails() {
        return txnDetails;
    }
    /**
     *
     * @param txnDetails
     * The txnDetails
     */
    public void setTxnDetails(TxnDetails txnDetails) {
        this.txnDetails = txnDetails;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


public class TxnDetails {

    private Integer order_id;
    private String mid;
    private String mkey;
    private String transaction_description;
    private Integer amount;
    private String currency;
    private String app_name;
    private String payment_type;
    private String transaction_type;
    private String payee_vpa,payee_phone_no;
    private String vpa;
    private String payer_vpa;
    private String payer_phone_no;
    private String mcc;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    // new variables added at 21-11-2016
    private String payeeAccntNo;
    private String payeeIFSC;
    private String payeeAadhaarNo;
    private String expiryTime;
    private String payerIFSC;
    private String payerAadhaarNo;
    private String subMerchantID;
    private String whitelistedAccnts;
    private String payerMMID;
    private String payeeMMID;
    private String refurl;
    private String payerAccntNo;

    public String getPayeeAccntNo() {
        if(payeeAccntNo == null){
            payeeAccntNo = "";
        }
        return payeeAccntNo;
    }
    public void setPayeeAccntNo(String payeeAccntNo) { this.payeeAccntNo = payeeAccntNo; }

    public String getPayeeIFSC() {
        if(payeeIFSC == null){
            payeeIFSC = "";
        }
        return payeeIFSC;
    }
    public void setPayeeIFSC(String payeeIFSC) { this.payeeIFSC = payeeIFSC; }

    public String getPayeeAadhaarNo() {
        if(payeeAadhaarNo == null){
            payeeAadhaarNo = "";
        }
        return payeeAadhaarNo;
    }
    public void setPayeeAadhaarNo(String payeeAadhaarNo) { this.payeeAadhaarNo = payeeAadhaarNo; }

    public String getExpiryTime() {
        if(expiryTime == null){
            expiryTime = "";
        }
        return expiryTime;
    }
    public void setExpiryTime(String expiryTime) { this.expiryTime = expiryTime; }

    public String getPayerIFSC() {
        if(payerIFSC == null){
            payerIFSC = "";
        }
        return payerIFSC;
    }
    public void setPayerIFSC(String payerIFSC) { this.payerIFSC = payerIFSC; }

    public String getPayerAadhaarNo() {
        if(payerAadhaarNo == null){
            payerAadhaarNo = "";
        }
        return payerAadhaarNo;
    }
    public void setPayerAadhaarNo(String payerAadhaarNo) { this.payerAadhaarNo = payerAadhaarNo; }

    public String getSubMerchantID() {
        if(subMerchantID == null){
            subMerchantID = "";
        }
        return subMerchantID;
    }
    public void setSubMerchantID(String subMerchantID) { this.subMerchantID = subMerchantID; }

    public String getWhitelistedAccnts() {
        if(whitelistedAccnts == null){
            whitelistedAccnts = "";
        }
        return whitelistedAccnts;
    }
    public void setWhitelistedAccnts(String whitelistedAccnts) { this.whitelistedAccnts= whitelistedAccnts; }

    public String getPayerMMID() {
        if(payerMMID == null){
            payerMMID = "";
        }
        return payerMMID;
    }
    public void setPayerMMID(String payerMMID) { this.payerMMID = payerMMID; }

    public String getPayeeMMID() {
        if(payeeMMID == null){
            payeeMMID = "";
        }
        return payeeMMID;
    }
    public void setPayeeMMID(String payeeMMID) { this.payeeMMID = payeeMMID; }

    public String getRefurl() {
        if(refurl == null){
            refurl = "";
        }
        return refurl;
    }
    public void setRefurl(String refurl) { this.refurl = refurl; }

    public String getPayerAccntNo() {
        if(payerAccntNo == null){
            payerAccntNo = "";
        }
        return payerAccntNo;
    }
    public void setPayerAccntNo(String payerAccntNo) { this.payerAccntNo = payerAccntNo; }

    //--------------------------------

    public String getPayer_vpa() {
        if(payer_vpa == null){
            payer_vpa = "";
        }
        return payer_vpa;
    }
    public void setPayer_vpa(String payer_vpa) {
        this.payer_vpa = payer_vpa;
    }

    public String getPayer_phone_no() {
        if(payer_phone_no == null){
            payer_phone_no = "";
        }
        return payer_phone_no;
    }
    public void setPayer_phone_no(String payer_phone_no) {
        this.payer_phone_no = payer_phone_no;
    }

    public String getPayee_phone_no() {
        if(payee_phone_no == null){
            payee_phone_no = "";
        }
        return payee_phone_no;
    }
    public void setPayee_phone_no(String payee_phone_no) {
        this.payee_phone_no = payee_phone_no;
    }

    public String getPayee_vpa() {
        if(payee_vpa == null){
            payee_vpa = "";
        }
        return payee_vpa;
    }
    public void setPayee_vpa(String payee_vpa) {
        this.payee_vpa = payee_vpa;
    }

    /**
     * @return The orderId
     */
    public Integer getOrderId() {
        if(order_id == null){
            return 0;
        }
        return order_id;
    }
    /**
     * @param orderId The order_id
     */
    public void setOrderId(Integer orderId) {
        this.order_id = orderId;
    }

    /**
     * @return The mid
     */
    public String getMid() {
        if(mid == null){
            mid = "";
        }
        return mid;
    }
    /**
     * @param mid The mid
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * @return The mkey
     */
    public String getMkey() {
        if(mkey == null){
            mkey = "";
        }
        return mkey;
    }
    /**
     * @param mkey The mkey
     */
    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    /**
     * @return The transactionDescription
     */
    public String getTransactionDescription() {
        if(transaction_description == null){
            transaction_description = "";
        }
        return transaction_description;
    }
    /**
     * @param transactionDescription The transaction_description
     */
    public void setTransactionDescription(String transactionDescription) {
        this.transaction_description = transactionDescription;
    }

    /**
     * @return The amount
     */
    public Integer getAmount() {
        if(amount == null){
            amount = 0;
        }
        return amount;
    }
    /**
     * @param amount The amount
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /**
     * @return The currency
     */
    public String getCurrency() {
        if(currency == null){
            currency = "";
        }
        return currency;
    }
    /**
     * @param currency The currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return The appName
     */
    public String getAppName() {
        if(app_name == null){
            app_name = "";
        }
        return app_name;
    }
    /**
     * @param appName The app_name
     */
    public void setAppName(String appName) {
        this.app_name = appName;
    }

    /**
     * @return The paymentType
     */
    public String getPaymentType() {
        if(payment_type == null){
            payment_type = "";
        }
        return payment_type;
    }
    /**
     * @param paymentType The payment_type
     */
    public void setPaymentType(String paymentType) {
        this.payment_type = paymentType;
    }

    /**
     * @return The transactionType
     */
    public String getTransactionType() {
        if(transaction_type == null){
            transaction_type = "";
        }
        return transaction_type;
    }
    /**
     * @param transactionType The transaction_type
     */
    public void setTransactionType(String transactionType) {
        this.transaction_type = transactionType;
    }

    /**
     * @return The mcc
     */
    public String getMcc() {
        if(mcc == null){
            mcc = "";
        }
        return mcc;
    }
    /**
     * @param mcc The mcc
     */
    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getVpa() {
        if(vpa == null){
            vpa = "";
        }
        return vpa;
    }
    public void setVpa(String vpa) {
        this.vpa = vpa;
    }
}
}