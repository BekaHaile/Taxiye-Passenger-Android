package product.clicklabs.jugnoo.home;

import android.content.Intent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

/**
 * Created by shankar on 8/31/16.
 */
public class DeepLinkAction {

	public void openDeepLink(MenuBar menuBar){
		try{
			if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.FREE_RIDES.getTag());
			}
			else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.WALLET.getTag());
			}
			else if(AppLinkIndex.PROMOTIONS.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag());
			}
			else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.HISTORY.getTag(), Data.deepLinkOrderId, Data.deepLinkProductType);
			}
			else if(AppLinkIndex.SUPPORT.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.SUPPORT.getTag());
			}
			else if(AppLinkIndex.ABOUT.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.ABOUT.getTag());
			}
			else if(AppLinkIndex.ACCOUNT.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.accountClick();
			}
			else if(AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.INBOX.getTag());
			}
			else if(AppLinkIndex.GAME_PAGE.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.GAME.getTag());
			}
			else if(AppLinkIndex.FRESH_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getFreshEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.FRESH.getTag());
				}
			}
			else if(AppLinkIndex.MEAL_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getMealsEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.MEALS.getTag());
				}
			}
			else if(AppLinkIndex.DELIVERY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getDeliveryEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.DELIVERY.getTag());
				}
			}
			else if(AppLinkIndex.AUTO_PAGE.getOrdinal() == Data.deepLinkIndex){
					menuBar.menuAdapter.onClickAction(MenuInfoTags.GET_A_RIDE.getTag());
			}
			else if(AppLinkIndex.GROCERY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getGroceryEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.GROCERY.getTag());
				}
			}
			else if(AppLinkIndex.CHAT_PAGE.getOrdinal() == Data.deepLinkIndex) {
				if (Data.autoData.getAssignedDriverInfo() != null && Data.autoData.getAssignedDriverInfo().getChatEnabled() == 1) {
					menuBar.openChat();
				}
			}
			else if(AppLinkIndex.MENUS_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getMenusEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.MENUS.getTag());
				}
			}
			else if(AppLinkIndex.PAY_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getPayEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.PAY.getTag());
				}
			}
			else if(AppLinkIndex.JUGNOO_STAR.getOrdinal() == Data.deepLinkIndex){
				menuBar.getActivity().startActivity(new Intent(menuBar.getActivity(), JugnooStarSubscribedActivity.class));
				menuBar.getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else if(AppLinkIndex.SUBSCRIPTION_PLAN_OPTION_SCREEN.getOrdinal() == Data.deepLinkIndex){
				menuBar.menuAdapter.onClickAction(MenuInfoTags.JUGNOO_STAR.getTag());
			}
			else if(AppLinkIndex.WALLET_TRANSACTIONS.getOrdinal() == Data.deepLinkIndex){
				Intent intent = new Intent();
				intent.setClass(menuBar.getActivity(), PaymentActivity.class);
				intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
				intent.putExtra(Constants.KEY_WALLET_TRANSACTIONS, 1);
				menuBar.getActivity().startActivity(intent);
				menuBar.getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else if(AppLinkIndex.FEED_PAGE.getOrdinal() == Data.deepLinkIndex){
				if(Data.userData.getFeedEnabled() == 1) {
					menuBar.menuAdapter.onClickAction(MenuInfoTags.FEED.getTag());
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}
		Data.deepLinkIndex = -1;
	}

}
