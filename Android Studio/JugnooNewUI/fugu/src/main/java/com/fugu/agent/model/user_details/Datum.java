
package com.fugu.agent.model.user_details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("first_seen")
    @Expose
    private String firstSeen;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("last_contacted")
    @Expose
    private String lastContacted;
    @SerializedName("last_heard_from")
    @Expose
    private String lastHeardFrom;
    @SerializedName("user_channel")
    @Expose
    private Integer userChannel;
//    @SerializedName("device_details")
//    @Expose
//    private DeviceDetails deviceDetails;
//    @SerializedName("android_details")
//    @Expose
//    private AndroidDetails androidDetails;
    @SerializedName("last_seen_android")
    @Expose
    private String lastSeenAndroid;
    @SerializedName("last_seen_ios")
    @Expose
    private String lastSeenIos;
    @SerializedName("browser_details")
    @Expose
    private Object browserDetails;
    @SerializedName("last_seen_browser")
    @Expose
    private String lastSeenBrowser;
    @SerializedName("full_address")
    @Expose
    private String fullAddress;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(String firstSeen) {
        this.firstSeen = firstSeen;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getLastContacted() {
        return lastContacted;
    }

    public void setLastContacted(String lastContacted) {
        this.lastContacted = lastContacted;
    }

    public String getLastHeardFrom() {
        return lastHeardFrom;
    }

    public void setLastHeardFrom(String lastHeardFrom) {
        this.lastHeardFrom = lastHeardFrom;
    }

    public Integer getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(Integer userChannel) {
        this.userChannel = userChannel;
    }

//    public DeviceDetails getDeviceDetails() {
//        return deviceDetails;
//    }
//
//    public void setDeviceDetails(DeviceDetails deviceDetails) {
//        this.deviceDetails = deviceDetails;
//    }

//    public AndroidDetails getAndroidDetails() {
//        return androidDetails;
//    }
//
//    public void setAndroidDetails(AndroidDetails androidDetails) {
//        this.androidDetails = androidDetails;
//    }

    public String getLastSeenAndroid() {
        return lastSeenAndroid;
    }

    public void setLastSeenAndroid(String lastSeenAndroid) {
        this.lastSeenAndroid = lastSeenAndroid;
    }

    public String getLastSeenIos() {
        return lastSeenIos;
    }

    public void setLastSeenIos(String lastSeenIos) {
        this.lastSeenIos = lastSeenIos;
    }

    public Object getBrowserDetails() {
        return browserDetails;
    }

    public void setBrowserDetails(Object browserDetails) {
        this.browserDetails = browserDetails;
    }

    public String getLastSeenBrowser() {
        return lastSeenBrowser;
    }

    public void setLastSeenBrowser(String lastSeenBrowser) {
        this.lastSeenBrowser = lastSeenBrowser;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.fullName);
        dest.writeString(this.userName);
        dest.writeString(this.firstSeen);
        dest.writeString(this.lastSeen);
        dest.writeString(this.lastContacted);
        dest.writeString(this.lastHeardFrom);
        dest.writeValue(this.userChannel);
       // dest.writeParcelable(this.deviceDetails, flags);
      //  dest.writeParcelable(this.androidDetails, flags);
        dest.writeString(this.lastSeenAndroid);
        dest.writeString(this.lastSeenIos);
        dest.writeString(this.lastSeenBrowser);
        dest.writeString(this.fullAddress);
    }

    public Datum() {
    }

    protected Datum(Parcel in) {
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.fullName = in.readString();
        this.userName = in.readString();
        this.firstSeen = in.readString();
        this.lastSeen = in.readString();
        this.lastContacted = in.readString();
        this.lastHeardFrom = in.readString();
        this.userChannel = (Integer) in.readValue(Integer.class.getClassLoader());
       // this.deviceDetails = in.readParcelable(DeviceDetails.class.getClassLoader());
      //  this.androidDetails = in.readParcelable(AndroidDetails.class.getClassLoader());
        this.lastSeenAndroid = in.readString();
        this.lastSeenIos = in.readString();
        this.lastSeenBrowser = in.readString();
        this.fullAddress = in.readString();
    }

    public static final Parcelable.Creator<Datum> CREATOR = new Parcelable.Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel source) {
            return new Datum(source);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };
}
