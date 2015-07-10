package product.clicklabs.jugnoo.adapters;

public interface PromotionListEventHandler {
    void onDismiss();
    void onPromoListFetched(int totalPromoCoupons);
}
