package product.clicklabs.jugnoo.datastructure;

public interface PromotionDialogEventHandler {
	public void onNoCouponsAvailable();
	public void onOkPressed(PromoCoupon promoCoupon);
	public void onCancelPressed();
}
