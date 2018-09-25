package product.clicklabs.jugnoo;

import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.MenuInfoTags;

public class AppMenuTagNames {

	public static final HashMap<String,String> APP_TAG_NAMES = new HashMap<>();
	static {
		if(BuildConfig.FLAVOR.equals("urcab")){
			APP_TAG_NAMES.put(MenuInfoTags.INBOX.getTag(),"Notifications");
			APP_TAG_NAMES.put(MenuInfoTags.HISTORY.getTag(),"Past trips");
		}

	}

}
