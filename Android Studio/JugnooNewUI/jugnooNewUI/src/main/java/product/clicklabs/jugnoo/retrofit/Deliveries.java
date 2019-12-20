package product.clicklabs.jugnoo.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deliveries implements Parcelable {

    public static final String ORDER_COMPLETE = "1";
    public static final String ORDER_ONGOING = "4";

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("is_completed")
    @Expose
    private String is_completed;

    @SerializedName("notes")
    @Expose
    private String notes;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public boolean isCompleted() {
        return is_completed.equals(ORDER_COMPLETE);
    }

    public String getNotes() { return notes; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
        dest.writeString(this.is_completed);
        dest.writeString(this.notes);
    }

    public Deliveries() {
    }

    protected Deliveries(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
        this.is_completed = in.readString();
        this.notes = in.readString();
    }

    public static final Parcelable.Creator<Deliveries> CREATOR = new Parcelable.Creator<Deliveries>() {
        @Override
        public Deliveries createFromParcel(Parcel source) {
            return new Deliveries(source);
        }

        @Override
        public Deliveries[] newArray(int size) {
            return new Deliveries[size];
        }
    };
}
