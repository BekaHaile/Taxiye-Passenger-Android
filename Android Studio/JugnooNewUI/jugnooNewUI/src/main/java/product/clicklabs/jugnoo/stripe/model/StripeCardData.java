package product.clicklabs.jugnoo.stripe.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

/**
 * Created by Parminder Saini on 23/05/18.
 */
public class StripeCardData  implements Parcelable {




    @Expose
    @SerializedName("card_number")
    private String cardNumber;
    @Expose
    @SerializedName("expiry_month")
    private String expiryMonth;
    @Expose
    @SerializedName("expiry_year")
    private String expiryYear;
    @Expose
    @SerializedName("_cvv")
    private String _cvv;
    @Expose
    @SerializedName("brand")
    private String brand;


    protected StripeCardData(Parcel in) {
        cardNumber = in.readString();
        expiryMonth = in.readString();
        expiryYear = in.readString();
        _cvv = in.readString();
        brand = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardNumber);
        dest.writeString(expiryMonth);
        dest.writeString(expiryYear);
        dest.writeString(_cvv);
        dest.writeString(brand);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StripeCardData> CREATOR = new Creator<StripeCardData>() {
        @Override
        public StripeCardData createFromParcel(Parcel in) {
            return new StripeCardData(in);
        }

        @Override
        public StripeCardData[] newArray(int size) {
            return new StripeCardData[size];
        }
    };

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String get_cvv() {
        return _cvv;
    }

    public String getBrand() {
        return brand;
    }
}
