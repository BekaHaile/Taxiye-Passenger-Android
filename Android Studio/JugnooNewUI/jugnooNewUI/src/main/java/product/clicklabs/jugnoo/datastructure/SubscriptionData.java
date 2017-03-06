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
    @SerializedName("subscription_benefits")
    @Expose
    private List<SubscriptionBenefits> subscriptionBenefits = null;
    @SerializedName("sub_text_autos")
    @Expose
    private String subTextAutos;
    @SerializedName("subscription_title")
    @Expose
    private String subscriptionTitle;
    @SerializedName("subscription_title_new")
    @Expose
    private String subscriptionTitleNew;
    @SerializedName("sub_text_meals")
    @Expose
    private String subTextMeals;
    @SerializedName("sub_text_fresh")
    @Expose
    private String subTextFresh;
    @SerializedName("sub_text_menus")
    @Expose
    private String subTextMenus;
    @SerializedName("sub_text_grocery")
    @Expose
    private String subTextGrocery;
    @SerializedName("subscribed_user")
    @Expose
    private Integer subscribedUser;

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
        if (subTextAutos == null)
            subTextAutos = "";
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

    public String getSubscriptionTitleNew() {
        if (subscriptionTitleNew == null)
            return subscriptionTitleNew = "";
        else
            return subscriptionTitleNew;
    }

    public void setSubscriptionTitleNew(String subscriptionTitleNew) {
        this.subscriptionTitleNew = subscriptionTitleNew;
    }

    public String getSubTextMeals() {
        if (subTextMeals == null)
            subTextMeals = "";
        return subTextMeals;
    }

    public void setSubTextMeals(String subTextMeals) {
        this.subTextMeals = subTextMeals;
    }

    public String getSubTextFresh() {
        if (subTextFresh == null)
            subTextFresh = "";
        return subTextFresh;
    }

    public void setSubTextFresh(String subTextFresh) {
        this.subTextFresh = subTextFresh;
    }

    public String getSubTextMenus() {
        if (subTextMenus == null)
            subTextMenus = "";
        return subTextMenus;
    }

    public void setSubTextMenus(String subTextMenus) {
        this.subTextMenus = subTextMenus;
    }

    public String getSubTextGrocery() {
        if (subTextGrocery == null)
            subTextGrocery = "";
        return subTextGrocery;
    }

    public void setSubTextGrocery(String subTextGrocery) {
        this.subTextGrocery = subTextGrocery;
    }

    public List<SubscriptionBenefits> getSubscriptionBenefits() {
        return subscriptionBenefits;
    }

    public void setSubscriptionBenefits(List<SubscriptionBenefits> subscriptionBenefits) {
        this.subscriptionBenefits = subscriptionBenefits;
    }

    public Integer getSubscribedUser() {
        if(subscribedUser == null){
            subscribedUser = 0;
        }
        return subscribedUser;
    }

    public void setSubscribedUser(Integer subscribedUser) {
        this.subscribedUser = subscribedUser;
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
            if (benefitIdAutos == null)
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
        @SerializedName("plan_string_new")
        @Expose
        private String planStringNew;
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
        @SerializedName("initial_amount_text")
        @Expose
        private String initialAmountText;
        @SerializedName("final_amount_text")
        @Expose
        private String finalAmountText;
        @SerializedName("cross_text")
        @Expose
        private String crossText;

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

        public String getFinalAmountText() {
            if (finalAmountText == null)
                return finalAmountText = "";
            else
                return finalAmountText;
        }

        public void setFinalAmountText(String finalAmountText) {
            this.finalAmountText = finalAmountText;
        }

        public String getInitialAmountText() {
            if (initialAmountText == null)
                return initialAmountText = "";
            else
                return initialAmountText;
        }

        public void setInitialAmountText(String initialAmountText) {
            this.initialAmountText = initialAmountText;
        }

        public String getPlanStringNew() {
            if (planStringNew == null)
                return planStringNew = "";
            else
                return planStringNew;
        }

        public void setPlanStringNew(String planStringNew) {
            this.planStringNew = planStringNew;
        }

        public String getCrossText() {
            return crossText;
        }

        public void setCrossText(String crossText) {
            this.crossText = crossText;
        }
    }

    public class SubscriptionBenefits {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("product_type")
        @Expose
        private Integer productType;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getProductType() {
            return productType;
        }

        public void setProductType(Integer productType) {
            this.productType = productType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}


