package com.jugnoo.pay.models;

import java.io.Serializable;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SendMoneyCallback implements Serializable{
    private String  access_token, amount, phone_no, order_id="";
    private MessageRequest message;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public MessageRequest getMessage() {
        return message;
    }

    public void setMessage(MessageRequest message) {
        this.message = message;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }
}
