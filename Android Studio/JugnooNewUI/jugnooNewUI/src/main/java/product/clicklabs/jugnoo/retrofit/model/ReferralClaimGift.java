package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 09/02/17.
 */

public class ReferralClaimGift {
    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("referee_user_image")
    @Expose
    private String refereeUserImage;
    @SerializedName("claim_gift_title")
    @Expose
    private String claimGiftTitle;
    @SerializedName("claim_gift_text")
    @Expose
    private String claimGiftText;
    @SerializedName("claim_gift_button_text")
    @Expose
    private String claimGiftButtonText;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClaimGiftText() {
        return claimGiftText;
    }

    public void setClaimGiftText(String claimGiftText) {
        this.claimGiftText = claimGiftText;
    }

    public String getClaimGiftTitle() {
        return claimGiftTitle;
    }

    public void setClaimGiftTitle(String claimGiftTitle) {
        this.claimGiftTitle = claimGiftTitle;
    }

    public String getRefereeUserImage() {
        return refereeUserImage;
    }

    public void setRefereeUserImage(String refereeUserImage) {
        this.refereeUserImage = refereeUserImage;
    }

    public String getClaimGiftButtonText() {
        return claimGiftButtonText;
    }

    public void setClaimGiftButtonText(String claimGiftButtonText) {
        this.claimGiftButtonText = claimGiftButtonText;
    }
}
