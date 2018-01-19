package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

/**
 * Created by shankar on 8/31/16.
 */
public class DeepLinkAction {

	public  static void openDeepLink(Activity activity, LatLng latlng){
		try{
			if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.FREE_RIDES.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.WALLET.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.PROMOTIONS.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.HISTORY.getTag(), Data.deepLinkOrderId, Data.deepLinkProductType,activity,latlng);
			}
			else if(AppLinkIndex.SUPPORT.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.SUPPORT.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.ABOUT.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.ABOUT.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.ACCOUNT.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.accountClick(activity);
			}
			else if(AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.INBOX.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.GAME_PAGE.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.GAME.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.FRESH_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getFreshEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.FRESH.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.MEAL_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getMealsEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.MEALS.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.DELIVERY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getDeliveryEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.DELIVERY.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.AUTO_PAGE.getOrdinal() == Data.deepLinkIndex){
					MenuAdapter.onClickAction(MenuInfoTags.GET_A_RIDE.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.GROCERY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getGroceryEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.GROCERY.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.CHAT_PAGE.getOrdinal() == Data.deepLinkIndex) {
				if (Data.autoData.getAssignedDriverInfo() != null && Data.autoData.getAssignedDriverInfo().getChatEnabled() == 1) {
					openChat(activity);
				}
			}
			else if(AppLinkIndex.MENUS_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getMenusEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.MENUS.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getDeliveryCustomerEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.DELIVERY_CUSTOMER.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.PAY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getPayEnabled() == 1) {
					MenuAdapter.onClickAction(MenuInfoTags.PAY.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.JUGNOO_STAR.getOrdinal() == Data.deepLinkIndex){
				activity.startActivity(new Intent(activity, JugnooStarSubscribedActivity.class));
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else if(AppLinkIndex.SUBSCRIPTION_PLAN_OPTION_SCREEN.getOrdinal() == Data.deepLinkIndex){
				MenuAdapter.onClickAction(MenuInfoTags.JUGNOO_STAR.getTag(),0,0,activity,latlng);
			}
			else if(AppLinkIndex.WALLET_TRANSACTIONS.getOrdinal() == Data.deepLinkIndex){
				Intent intent = new Intent();
				intent.setClass(activity, PaymentActivity.class);
				intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
				intent.putExtra(Constants.KEY_WALLET_TRANSACTIONS, 1);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else if(AppLinkIndex.FEED_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getFeedEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.FEED.getTag(),0,0,activity,latlng);
				}
			}
			else if(AppLinkIndex.PROS_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getProsEnabled() == 1 || Data.userData.isRidesAndFatafatEnabled()) {
					MenuAdapter.onClickAction(MenuInfoTags.PROS.getTag(),0,0,activity,latlng);
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}
		Data.deepLinkIndex = -1;
	}

	public static void openChat(Activity activity){
		if(activity instanceof  HomeActivity){
			((HomeActivity)activity).openChatScreen();

		}
	}

}
