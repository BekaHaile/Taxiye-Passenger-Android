package product.clicklabs.jugnoo.home;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;

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
				menuBar.menuAdapter.onClickAction(MenuInfoTags.HISTORY.getTag());
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

		} catch(Exception e){
			e.printStackTrace();
		}
		Data.deepLinkIndex = -1;
	}

}
