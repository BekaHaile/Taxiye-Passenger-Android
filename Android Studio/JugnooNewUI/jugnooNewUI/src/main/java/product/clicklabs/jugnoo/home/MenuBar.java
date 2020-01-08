package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.activities.MainActivity;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.AppMenuTagNames;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 4/8/16.
 */
public class MenuBar {

	private Activity activity;
	private DrawerLayout drawerLayout;

	public LinearLayout menuLayout;

	public RecyclerView getRecyclerViewMenu() {
		return recyclerViewMenu;
	}

	private RecyclerView recyclerViewMenu;

	public MenuAdapter getMenuAdapter() {
		return menuAdapter;
	}

	public MenuAdapter menuAdapter;
	private ImageView imageViewProfile;
	private TextView textViewUserName,textViewViewPhone;
	private View viewStarIcon;
	private RelativeLayout relativeLayout;

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
		imageViewProfile = (ImageView) drawerLayout.findViewById(R.id.imageViewProfile);//textViewUserName

		textViewUserName = (TextView) drawerLayout.findViewById(R.id.textViewUserName); textViewUserName.setTypeface(Fonts.mavenMedium(activity));
		textViewViewPhone = (TextView) drawerLayout.findViewById(R.id.textViewViewPhone); textViewViewPhone.setTypeface(Fonts.mavenRegular(activity));
		viewStarIcon = (View) drawerLayout.findViewById(R.id.viewStarIcon);
		relativeLayout = (RelativeLayout) drawerLayout.findViewById(R.id.rlTopContainer);
		try {



			ArrayList<MenuInfo> itemsToShow = getSideMenuList();

			menuAdapter = new MenuAdapter(itemsToShow, activity, drawerLayout);


			recyclerViewMenu.setAdapter(menuAdapter);
			setProfileData();

			setActivityNames(itemsToShow);
		} catch (Exception e) {
			e.printStackTrace();
		}


		menuLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

	}



	public void setProfileData() {

		if(Data.userData.getSubscriptionData().getUserSubscriptions() != null && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0){

            viewStarIcon.setVisibility(View.VISIBLE);
        } else{

            viewStarIcon.setVisibility(View.GONE);
        }
		if(!TextUtils.isEmpty(Data.userData.userName)
                && (!Data.userData.userName.equalsIgnoreCase("User"))) {
            textViewUserName.setVisibility(View.VISIBLE);
            textViewUserName.setText(Data.userData.userName);
        } else{
            textViewUserName.setVisibility(View.GONE);
        }
		textViewViewPhone.setText(Data.userData.phoneNo);
		float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
		if(activity instanceof HomeActivity && ((HomeActivity)activity).activityResumed){
            if(!"".equalsIgnoreCase(Data.userData.userImage)) {
                Picasso.with(activity).load(Data.userData.userImage)
						.placeholder(ContextCompat.getDrawable(activity, R.drawable.ic_profile_img_placeholder))
						.transform(new CircleTransform())
						.error(ContextCompat.getDrawable(activity, R.drawable.ic_profile_img_placeholder))
                        .resize((int)(160f * minRatio), (int)(160f * minRatio)).centerCrop()
                        .into(imageViewProfile);
            }else{
                imageViewProfile.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_profile_img_placeholder));
            }
        }
        else{
            if(!"".equalsIgnoreCase(Data.userData.userImage)) {
                Picasso.with(activity).load(Data.userData.userImage)
						.placeholder(ContextCompat.getDrawable(activity, R.drawable.ic_profile_img_placeholder))
						.transform(new CircleTransform())
						.error(ContextCompat.getDrawable(activity,R.drawable.ic_profile_img_placeholder))
                        .resize((int)(160f * minRatio), (int)(160f * minRatio)).centerCrop()
                        .into(imageViewProfile);
            }
            else {
                imageViewProfile.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_profile_img_placeholder));
            }
        }



		relativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accountClick();

			}
		});
	}
	public void accountClick(){
		activity.startActivity(new Intent(activity, AccountActivity.class));
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	private void setActivityNames(ArrayList<MenuInfo> menuInfos){
		try {
			for(MenuInfo menuInfo : menuInfos){
                if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.GAME.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_PLAY = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.FREE_RIDES.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_FREE_RIDES = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.FREE_RIDES_NEW.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_FREE_RIDES = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.WALLET.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_WALLET = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.INBOX.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_INBOX = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.OFFERS.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_PROMOTIONS = menuInfo.getName();
                }else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.OFFERS.getTag())){
					MyApplication.getInstance().ACTIVITY_NAME_OFFERS = menuInfo.getName();
				} else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.HISTORY.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_HISTORY = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.REFER_A_DRIVER.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_REFER_A_DRIVER = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.SUPPORT.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_SUPPORT = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.ABOUT.getTag())){
                    MyApplication.getInstance().ACTIVITY_NAME_ABOUT = menuInfo.getName();
                } else if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.JUGNOO_STAR.getTag())){
					MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR = menuInfo.getName();
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUserData(){
		try {
			menuAdapter.setList(getSideMenuList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<MenuInfo> getSideMenuList(){
		ArrayList<MenuInfo> itemsToShow = new ArrayList<>();
		for(MenuInfo menuInfo: Data.userData.getMenuInfoList()) {
			if(!menuInfo.getShowInAccount()){
				itemsToShow.add(menuInfo);
			}
			if(AppMenuTagNames.APP_TAG_NAMES.containsKey(menuInfo.getTag())){

				menuInfo.setName(AppMenuTagNames.APP_TAG_NAMES.get(menuInfo.getTag()));
			}
		}


		return itemsToShow;
	}


	public void setupFreshUI(){
		try {
			menuAdapter.setList(getSideMenuList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Activity getActivity(){
		return activity;
	}

	public DrawerLayout getDrawerLayout() {
		return drawerLayout;
	}
}
