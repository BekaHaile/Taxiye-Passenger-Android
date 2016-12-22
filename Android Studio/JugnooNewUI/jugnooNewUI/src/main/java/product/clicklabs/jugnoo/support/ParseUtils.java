package product.clicklabs.jugnoo.support;

import android.content.Context;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;

/**
 * Created by shankar on 8/23/16.
 */
public class ParseUtils {

	public ArrayList<ShowPanelResponse.Item> saveAndParseAllMenu(Context context, ShowPanelResponse showPanelResponse, int supportCategory){
		ArrayList<ShowPanelResponse.Item> itemsMain = null;
		Database2.getInstance(context).insertUpdateSupportData(1, showPanelResponse.getSupportData().getMenu1());
		Database2.getInstance(context).insertUpdateSupportData(2, showPanelResponse.getSupportData().getMenu2());
		Database2.getInstance(context).insertUpdateSupportData(3, showPanelResponse.getSupportData().getMenu3());
		Database2.getInstance(context).insertUpdateSupportData(4, showPanelResponse.getSupportData().getMenu4());
		Database2.getInstance(context).insertUpdateSupportData(5, showPanelResponse.getSupportData().getMenu5());
		Database2.getInstance(context).insertUpdateSupportData(6, showPanelResponse.getSupportData().getMenu6());
		Database2.getInstance(context).insertUpdateSupportData(7, showPanelResponse.getSupportData().getMenu7());
		Database2.getInstance(context).insertUpdateSupportData(8, showPanelResponse.getSupportData().getMenu8());
		Database2.getInstance(context).insertUpdateSupportData(9, showPanelResponse.getSupportData().getMenu9());
		Database2.getInstance(context).insertUpdateSupportData(10, showPanelResponse.getSupportData().getMenu10());
		Database2.getInstance(context).insertUpdateSupportData(11, showPanelResponse.getSupportData().getMenu11());
		Database2.getInstance(context).insertUpdateSupportData(12, showPanelResponse.getSupportData().getMenu12());
		Database2.getInstance(context).insertUpdateSupportData(13, showPanelResponse.getSupportData().getMenu13());
		Database2.getInstance(context).insertUpdateSupportData(14, showPanelResponse.getSupportData().getMenu14());
		Database2.getInstance(context).insertUpdateSupportData(15, showPanelResponse.getSupportData().getMenu15());
		Database2.getInstance(context).insertUpdateSupportData(16, showPanelResponse.getSupportData().getMenu16());
		Database2.getInstance(context).insertUpdateSupportData(17, showPanelResponse.getSupportData().getMenu17());
		Database2.getInstance(context).insertUpdateSupportData(18, showPanelResponse.getSupportData().getMenu18());
		Database2.getInstance(context).insertUpdateSupportData(19, showPanelResponse.getSupportData().getMenu19());
		Database2.getInstance(context).insertUpdateSupportData(20, showPanelResponse.getSupportData().getMenu20());
		Database2.getInstance(context).insertUpdateSupportData(21, showPanelResponse.getSupportData().getMenu21());
		Database2.getInstance(context).insertUpdateSupportData(22, showPanelResponse.getSupportData().getMenu22());
		Database2.getInstance(context).insertUpdateSupportData(23, showPanelResponse.getSupportData().getMenu23());
		Database2.getInstance(context).insertUpdateSupportData(24, showPanelResponse.getSupportData().getMenu24());
		Database2.getInstance(context).insertUpdateSupportData(25, showPanelResponse.getSupportData().getMenu25());
		Database2.getInstance(context).insertUpdateSupportData(26, showPanelResponse.getSupportData().getMenu26());
		Database2.getInstance(context).insertUpdateSupportData(27, showPanelResponse.getSupportData().getMenu27());
		Database2.getInstance(context).insertUpdateSupportData(28, showPanelResponse.getSupportData().getMenu28());
		Database2.getInstance(context).insertUpdateSupportData(29, showPanelResponse.getSupportData().getMenu29());
		Database2.getInstance(context).insertUpdateSupportData(30, showPanelResponse.getSupportData().getMenu30());

		switch(supportCategory){
			case 1:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu1();
				break;
			case 2:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu2();
				break;
			case 3:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu3();
				break;
			case 4:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu4();
				break;
			case 5:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu5();
				break;
			case 6:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu6();
				break;
			case 7:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu7();
				break;
			case 8:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu8();
				break;
			case 9:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu9();
				break;
			case 10:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu10();
				break;
			case 11:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu11();
				break;
			case 12:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu12();
				break;
			case 13:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu13();
				break;
			case 14:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu14();
				break;
			case 15:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu15();
				break;
			case 16:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu16();
				break;
			case 17:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu17();
				break;
			case 18:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu18();
				break;
			case 19:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu19();
				break;
			case 20:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu20();
				break;
			case 21:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu21();
				break;
			case 22:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu22();
				break;
			case 23:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu23();
				break;
			case 24:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu24();
				break;
			case 25:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu25();
				break;
			case 26:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu26();
				break;
			case 27:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu27();
				break;
			case 28:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu28();
				break;
			case 29:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu29();
				break;
			case 30:
				itemsMain = (ArrayList<ShowPanelResponse.Item>) showPanelResponse.getSupportData().getMenu30();
				break;
		}
		return itemsMain;
	}

}
