package com.sabkuchfresh.home;

import android.app.Activity;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements FlurryEventNames {


	Activity activity;
	DrawerLayout drawerLayout;

	//Top RL
	public RelativeLayout topRl;
	public ImageView imageViewMenu, imageViewSearchCancel, below_shadow;
	public TextView title;
	public Button buttonCheckServer;
	public RelativeLayout relativeLayoutNotification;
	public TextView textViewNotificationValue;
	public ImageView imageViewBack, imageViewDelete, imageViewNotification, imageViewShadow, imageViewSearchCross;//, imageViewSearch;
	public EditText editTextDeliveryAddress;


	public TopBar(Activity activity, DrawerLayout drawerLayout){
		this.activity = activity;
		this.drawerLayout = drawerLayout;
		setupTopBar();
	}

	private void setupTopBar(){
		topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
		imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
		imageViewSearchCancel = (ImageView) drawerLayout.findViewById(R.id.imageViewSearchCancel);
		title = (TextView) drawerLayout.findViewById(R.id.title);title.setTypeface(Fonts.mavenRegular(activity));

		buttonCheckServer = (Button) drawerLayout.findViewById(R.id.buttonCheckServer);
		relativeLayoutNotification = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutNotification);
		textViewNotificationValue = (TextView) drawerLayout.findViewById(R.id.textViewNotificationValue);
		textViewNotificationValue.setTypeface(Fonts.latoRegular(activity));
		textViewNotificationValue.setVisibility(View.GONE);

		below_shadow = (ImageView) drawerLayout.findViewById(R.id.below_shadow);
		imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
		imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);
        imageViewNotification = (ImageView) drawerLayout.findViewById(R.id.imageViewNotification);
		editTextDeliveryAddress = (EditText) drawerLayout.findViewById(R.id.editTextDeliveryAddress);
		editTextDeliveryAddress.setTypeface(Fonts.mavenLight(activity));
		imageViewSearchCross = (ImageView) drawerLayout.findViewById(R.id.imageViewSearchCross);
//		imageViewSearch = (ImageView)drawerLayout.findViewById(R.id.imageViewSearch);

		//Top bar events
		topRl.setOnClickListener(topBarOnClickListener);
		imageViewMenu.setOnClickListener(topBarOnClickListener);
		buttonCheckServer.setOnClickListener(topBarOnClickListener);
//		imageViewSearch.setOnClickListener(topBarOnClickListener);

		buttonCheckServer.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(activity, Config.getFreshServerUrlName(), Toast.LENGTH_SHORT).show();
				FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
				return false;
			}
		});

		imageViewSearchCancel.setOnClickListener(topBarOnClickListener);
		relativeLayoutNotification.setOnClickListener(topBarOnClickListener);
		imageViewBack.setOnClickListener(topBarOnClickListener);
		imageViewDelete.setOnClickListener(topBarOnClickListener);


		if(activity instanceof FreshActivity){
			relativeLayoutNotification.setVisibility(View.VISIBLE);
			imageViewSearchCancel.setVisibility(View.GONE);
			title.setText(activity.getResources().getString(R.string.app_name).toUpperCase());
		} else {
			relativeLayoutNotification.setVisibility(View.GONE);
		}

//        try {
//            if(Data.getFreshData().stores.size()>1) {
//                relativeLayoutNotification.setVisibility(View.GONE);
//            } else {
//                relativeLayoutNotification.setVisibility(View.VISIBLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

	}

//	public ImageView getImageViewSearch() {
//		return imageViewSearch;
//	}

	private View.OnClickListener topBarOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.topRl:
					break;

				case R.id.imageViewMenu:

					drawerLayout.openDrawer(GravityCompat.START);
					NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_MENU_CLICKED, null);
					break;

				case R.id.buttonCheckServer:
					break;

                case R.id.relativeLayoutNotification:
                    if(activity instanceof FreshActivity) {
                        ((FreshActivity)activity).openNotification();
                    }
                    break;

				case R.id.imageViewBack:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).performBackPressed();
					}
					break;

				case R.id.imageViewDelete:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).deleteCart();
					}
					break;

//				case R.id.imageViewSearch:
//					if(activity instanceof FreshActivity){
//						((FreshActivity)activity).searchItem();
//					}
//					break;

			}
		}
	};



	public void setUserData(){
		try {
			int unreadNotificationsCount = Prefs.with(activity).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			if(unreadNotificationsCount > 0){
				textViewNotificationValue.setVisibility(View.VISIBLE);
				textViewNotificationValue.setText(String.valueOf(unreadNotificationsCount));
			}
			else{
				textViewNotificationValue.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private StateListDrawable getStateListDrawable(int resourceNormal, int resourcePressed){
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
				activity.getResources().getDrawable(resourcePressed));
		stateListDrawable.addState(new int[]{},
				activity.getResources().getDrawable(resourceNormal));
		return stateListDrawable;
	}


}
