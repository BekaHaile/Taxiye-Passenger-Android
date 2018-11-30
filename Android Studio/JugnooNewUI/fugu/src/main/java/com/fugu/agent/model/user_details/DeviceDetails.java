
package com.fugu.agent.model.user_details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceDetails implements Parcelable {

    @SerializedName("android_app_version")
    @Expose
    private Integer androidAppVersion;
    @SerializedName("android_manufacturer")
    @Expose
    private String androidManufacturer;
    @SerializedName("android_model")
    @Expose
    private String androidModel;
    @SerializedName("android_operating_system")
    @Expose
    private String androidOperatingSystem;
    @SerializedName("android_os_version")
    @Expose
    private String androidOsVersion;

    public Integer getAndroidAppVersion() {
        return androidAppVersion;
    }

    public void setAndroidAppVersion(Integer androidAppVersion) {
        this.androidAppVersion = androidAppVersion;
    }

    public String getAndroidManufacturer() {
        return androidManufacturer;
    }

    public void setAndroidManufacturer(String androidManufacturer) {
        this.androidManufacturer = androidManufacturer;
    }

    public String getAndroidModel() {
        return androidModel;
    }

    public void setAndroidModel(String androidModel) {
        this.androidModel = androidModel;
    }

    public String getAndroidOperatingSystem() {
        return androidOperatingSystem;
    }

    public void setAndroidOperatingSystem(String androidOperatingSystem) {
        this.androidOperatingSystem = androidOperatingSystem;
    }

    public String getAndroidOsVersion() {
        return androidOsVersion;
    }

    public void setAndroidOsVersion(String androidOsVersion) {
        this.androidOsVersion = androidOsVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.androidAppVersion);
        dest.writeString(this.androidManufacturer);
        dest.writeString(this.androidModel);
        dest.writeString(this.androidOperatingSystem);
        dest.writeString(this.androidOsVersion);
    }

    public DeviceDetails() {
    }

    protected DeviceDetails(Parcel in) {
        this.androidAppVersion = (Integer) in.readValue(Integer.class.getClassLoader());
        this.androidManufacturer = in.readString();
        this.androidModel = in.readString();
        this.androidOperatingSystem = in.readString();
        this.androidOsVersion = in.readString();
    }

    public static final Parcelable.Creator<DeviceDetails> CREATOR = new Parcelable.Creator<DeviceDetails>() {
        @Override
        public DeviceDetails createFromParcel(Parcel source) {
            return new DeviceDetails(source);
        }

        @Override
        public DeviceDetails[] newArray(int size) {
            return new DeviceDetails[size];
        }
    };
}
