package com.sabkuchfresh.retrofit.model.feed;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleInfo implements Parcelable {

    @SerializedName("type")
    @Expose
    public int type;

    @SerializedName("region_id")
    @Expose
    public int regionId;

    @SerializedName("ride_type")
    @Expose
    public int rideType;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("images")
    @Expose
    public Images images;

    public int getType() {
        return type;
    }

    public int getRegionId() {
        return regionId;
    }

    public int getRideType() {
        return rideType;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Images getImages() {
        return images;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.regionId);
        dest.writeInt(this.rideType);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeParcelable(this.images, flags);
    }

    public VehicleInfo() {
    }

    protected VehicleInfo(Parcel in) {
        this.type = in.readInt();
        this.regionId = in.readInt();
        this.rideType = in.readInt();
        this.name = in.readString();
        this.image = in.readString();
        this.images = in.readParcelable(Images.class.getClassLoader());
    }

    public static final Parcelable.Creator<VehicleInfo> CREATOR = new Parcelable.Creator<VehicleInfo>() {
        @Override
        public VehicleInfo createFromParcel(Parcel source) {
            return new VehicleInfo(source);
        }

        @Override
        public VehicleInfo[] newArray(int size) {
            return new VehicleInfo[size];
        }
    };
}

