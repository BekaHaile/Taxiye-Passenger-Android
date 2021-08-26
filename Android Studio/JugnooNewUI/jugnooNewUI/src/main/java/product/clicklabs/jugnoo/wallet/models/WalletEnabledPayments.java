package product.clicklabs.jugnoo.wallet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletEnabledPayments<T> {

    @Expose
    @SerializedName("amount")
    private int amount;

    @Expose
    @SerializedName("payment_mode_config_data")
    private T[] data;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public T[] getData() {
        return data;
    }

    public void setData(T[] data) {
        this.data = data;
    }
}

