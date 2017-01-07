package product.clicklabs.jugnoo.datastructure;

/**
 * Created by ankit on 30/12/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSubscription {

    @SerializedName("benefit_id_autos")
    @Expose
    private Integer benefitIdAutos;
    @SerializedName("benefit_id_fresh")
    @Expose
    private Integer benefitIdFresh;
    @SerializedName("benefit_id_meals")
    @Expose
    private Integer benefitIdMeals;
    @SerializedName("benefit_id_grocery")
    @Expose
    private Integer benefitIdGrocery;
    @SerializedName("benefit_id_delivery")
    @Expose
    private Integer benefitIdDelivery;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("valid_till")
    @Expose
    private String validTill;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("plan_string")
    @Expose
    private String planString;

    public Integer getBenefitIdAutos() {
        return benefitIdAutos;
    }

    public void setBenefitIdAutos(Integer benefitIdAutos) {
        this.benefitIdAutos = benefitIdAutos;
    }

    public Integer getBenefitIdFresh() {
        return benefitIdFresh;
    }

    public void setBenefitIdFresh(Integer benefitIdFresh) {
        this.benefitIdFresh = benefitIdFresh;
    }

    public Integer getBenefitIdMeals() {
        return benefitIdMeals;
    }

    public void setBenefitIdMeals(Integer benefitIdMeals) {
        this.benefitIdMeals = benefitIdMeals;
    }

    public Integer getBenefitIdGrocery() {
        return benefitIdGrocery;
    }

    public void setBenefitIdGrocery(Integer benefitIdGrocery) {
        this.benefitIdGrocery = benefitIdGrocery;
    }

    public Integer getBenefitIdDelivery() {
        return benefitIdDelivery;
    }

    public void setBenefitIdDelivery(Integer benefitIdDelivery) {
        this.benefitIdDelivery = benefitIdDelivery;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPlanString() {
        return planString;
    }

    public void setPlanString(String planString) {
        this.planString = planString;
    }

}