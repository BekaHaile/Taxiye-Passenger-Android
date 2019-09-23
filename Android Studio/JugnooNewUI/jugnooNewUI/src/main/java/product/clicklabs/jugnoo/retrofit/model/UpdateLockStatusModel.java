package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateLockStatusModel {

    @SerializedName("flag")
    @Expose
    private Integer flag;

    @SerializedName("message")
    @Expose
    private String message;

    public Integer getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
