package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class BuyAirtime {
    @SerializedName("flag")
    private String flag;

    @SerializedName("message")
    private String message;

    @SerializedName("voucher_number")
    private String voucherNumber;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
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
