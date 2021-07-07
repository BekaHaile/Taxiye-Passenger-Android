package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class OfferTransfer {
    @SerializedName("flag")
    private int flag;

    @SerializedName("message")
    private String message;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
