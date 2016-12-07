package com.jugnoo.pay.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SelectUser implements Parcelable{
    String name;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    String thumb;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;

    public Boolean getCheckedBox() {
        return checkedBox;
    }

    public void setCheckedBox(Boolean checkedBox) {
        this.checkedBox = checkedBox;
    }

    Boolean checkedBox = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SelectUser(){}
    public SelectUser(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.name = data[0];
        this.phone = data[1];
        this.amount = data[2];
        this.orderId = data[3];
        this.thumb = data[4];
        this.message = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] { this.name ,this.phone, this.amount, this.orderId, this.thumb, this.message });
         // Serialize bitmap as Parcelable:
       // parcel.writeParcelable(this.thumb,i);
    }

    public static final Creator CREATOR = new Creator() {
        public SelectUser createFromParcel(Parcel in) {
            return new SelectUser(in);
        }

        public SelectUser[] newArray(int size) {
            return new SelectUser[size];
        }
    };
}