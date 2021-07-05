package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class OfferTransaction {
    @SerializedName("voucher_number")
    private String voucherNumber;

    @SerializedName("amount")
    private int amount;

    @SerializedName("date")
    private String date;

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
