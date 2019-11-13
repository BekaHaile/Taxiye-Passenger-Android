package product.clicklabs.jugnoo.stripe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 23/05/18.
 */
public class StripeCardData  {




    @Expose
    @SerializedName("id")
    private String cardId;
    @Expose
    @SerializedName("last_4")
    private String last4;
    @Expose
    @SerializedName("exp_month")
    private String expiryMonth;
    @Expose
    @SerializedName("exp_year")
    private String expiryYear;
    @Expose
    @SerializedName("_cvv")
    private String _cvv;
    @Expose
    @SerializedName("brand")
    private String brand;

    public Boolean selected;

    public StripeCardData(String cardId, String last4, String brand) {
        this.cardId = cardId;
        this.last4 = last4;
        this.brand = brand;
    }

    public String getCardId() {
        return cardId;
    }

    public String getLast4() {
        return last4;
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

