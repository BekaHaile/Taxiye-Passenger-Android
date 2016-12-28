package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 28/12/16.
 */

public class Subscription {
    @SerializedName("plan_duration")
    @Expose
    private Integer planDuration;
    @SerializedName("plan_string")
    @Expose
    private String planString;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("initial_amount")
    @Expose
    private Integer initialAmount;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sub_id")
    @Expose
    private Integer subId;

    public Integer getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(Integer planDuration) {
        this.planDuration = planDuration;
    }

    public String getPlanString() {
        return planString;
    }

    public void setPlanString(String planString) {
        this.planString = planString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Integer initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }
}
