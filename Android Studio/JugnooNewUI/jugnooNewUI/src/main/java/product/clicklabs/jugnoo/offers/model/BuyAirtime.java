package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class BuyAirtime {
    @SerializedName("flag")
    private int flag;

    @SerializedName("message")
    private String message;

    @SerializedName("voucher_number")
    private String voucherNumber;

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

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucher_number(String voucher_number) {
        this.voucherNumber = voucher_number;
    }
}
