package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferTransaction {
    @SerializedName("flag")
    private int flag;

    @SerializedName("message")
    private ArrayList<Transaction> message;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ArrayList<Transaction> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<Transaction> message) {
        this.message = message;
    }
}

