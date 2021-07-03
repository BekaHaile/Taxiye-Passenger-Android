package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class OfferMenu {

    @SerializedName("airtime")
    private Boolean airtime;

    @SerializedName("transfer")
    private Boolean transfer;

    @SerializedName("promotions")
    private Boolean promotions;

    @SerializedName("wallet_balance")
    private double walletBalance;

    public Boolean getAirtime() {
        return airtime;
    }

    public void setAirtime(Boolean airtime) {
        this.airtime = airtime;
    }

    public Boolean getTransfer() {
        return transfer;
    }

    public void setTransfer(Boolean transfer) {
        this.transfer = transfer;
    }

    public Boolean getPromotions() {
        return promotions;
    }

    public void setPromotions(Boolean promotions) {
        this.promotions = promotions;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }
}
