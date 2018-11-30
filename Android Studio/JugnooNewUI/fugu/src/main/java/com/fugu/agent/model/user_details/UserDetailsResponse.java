
package com.fugu.agent.model.user_details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsResponse implements Parcelable {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.statusCode);
        dest.writeString(this.message);
        dest.writeList(this.data);
    }

    public UserDetailsResponse() {
    }

    protected UserDetailsResponse(Parcel in) {
        this.statusCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.message = in.readString();
        this.data = new ArrayList<Datum>();
        in.readList(this.data, Datum.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserDetailsResponse> CREATOR = new Parcelable.Creator<UserDetailsResponse>() {
        @Override
        public UserDetailsResponse createFromParcel(Parcel source) {
            return new UserDetailsResponse(source);
        }

        @Override
        public UserDetailsResponse[] newArray(int size) {
            return new UserDetailsResponse[size];
        }
    };
}
