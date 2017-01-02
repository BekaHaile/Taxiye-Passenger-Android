package product.clicklabs.jugnoo.datastructure;

/**
 * Created by ankit on 30/12/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SubscriptionData {

    @SerializedName("user_subscriptions")
    @Expose
    private List<UserSubscription> userSubscriptions = null;
    @SerializedName("subscriptions")
    @Expose
    private List<Subscription> subscriptions = null;
    @SerializedName("sub_text_autos")
    @Expose
    private String subTextAutos;
    @SerializedName("subscription_title")
    @Expose
    private String subscriptionTitle;
    @SerializedName("sub_text_meals")
    @Expose
    private String subTextMeals;
    @SerializedName("sub_text_fresh")
    @Expose
    private String subTextFresh;

    public List<UserSubscription> getUserSubscriptions() {
        return userSubscriptions;
    }

    public void setUserSubscriptions(List<UserSubscription> userSubscriptions) {
        this.userSubscriptions = userSubscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getSubTextAutos() {
        return subTextAutos;
    }

    public void setSubTextAutos(String subTextAutos) {
        this.subTextAutos = subTextAutos;
    }

    public String getSubscriptionTitle() {
        return subscriptionTitle;
    }

    public void setSubscriptionTitle(String subscriptionTitle) {
        this.subscriptionTitle = subscriptionTitle;
    }

    public String getSubTextMeals() {
        return subTextMeals;
    }

    public void setSubTextMeals(String subTextMeals) {
        this.subTextMeals = subTextMeals;
    }

    public String getSubTextFresh() {
        return subTextFresh;
    }

    public void setSubTextFresh(String subTextFresh) {
        this.subTextFresh = subTextFresh;
    }

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
            if(benefitIdAutos == null)
                benefitIdAutos = 0;
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

}


