package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.jugnoo.pay.activities.MainActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by shankar on 4/8/16.
 */
public class MenuBar {

	private Activity activity;
	private DrawerLayout drawerLayout;

	public LinearLayout menuLayout;

	private RecyclerView recyclerViewMenu;

	public MenuAdapter getMenuAdapter() {
		return menuAdapter;
	}

	public MenuAdapter menuAdapter;

	public MenuBar(Activity activity, DrawerLayout rootView){
		this.activity = activity;
		this.drawerLayout = rootView;
		initComponents();
	}


	private void initComponents(){
		menuLayout = (LinearLayout) drawerLayout.findViewById(R.id.menuLayout);
		if(activity instanceof MainActivity){
			new ASSL(activity, menuLayout, 1134, 720, false);
		}
		recyclerViewMenu = (RecyclerView) drawerLayout.findViewById(R.id.recyclerViewMenu);
		recyclerViewMenu.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
		recyclerViewMenu.setHasFixedSize(false);

		try {
			ArrayList<MenuInfo> itemsToShow = new ArrayList<>();
			for(MenuInfo menuInfo: Data.userData.getMenuInfoList())
			{
				if(!menuInfo.getShowInAccount()){
					itemsToShow.add(menuInfo);
				}
			}

			menuAdapter = new MenuAdapter(itemsToShow, activity, drawerLayout);


			recyclerViewMenu.setAdapter(menuAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setActivityNames();

		menuLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		setupFreshUI();
	}

	public void openChat(){
		((HomeActivity)activity).openChatScreen();
	}

	private void setActivityNames(){
		try {
			for(int i=0; i<Data.userData.getMenuInfoList().size(); i++){
                if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.GAME.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_PLAY = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.FREE_RIDES.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_FREE_RIDES = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.WALLET.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_WALLET = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.INBOX.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_INBOX = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.OFFERS.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_PROMOTIONS = Data.userData.getMenuInfoList().get(i).getName();
                }else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.OFFERS.getTag())){
					MyApplication.getInstance().ACTIVITY_NAME_OFFERS = Data.userData.getMenuInfoList().get(i).getName();
				} else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.HISTORY.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_HISTORY = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.REFER_A_DRIVER.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_REFER_A_DRIVER = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.SUPPORT.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_SUPPORT = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.ABOUT.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_ABOUT = Data.userData.getMenuInfoList().get(i).getName();
                } else if(Data.userData.getMenuInfoList().get(i).getTag().equalsIgnoreCase(MenuInfoTags.JUGNOO_STAR.getTag())){
					MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR = Data.userData.getMenuInfoList().get(i).getName();
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUserData(){
		try {
			menuAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setupFreshUI(){
		try {
			menuAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Activity getActivity(){
		return activity;
	}

}
