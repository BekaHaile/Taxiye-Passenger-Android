package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar {

	Activity activity;
	DrawerLayout drawerLayout;

	//Top RL
	public RelativeLayout topRl;
	public ImageView imageViewMenu, imageViewSearchCancel;
	public TextView title;
	public Button checkServerBtn;
	public ImageView imageViewHelp;
	public RelativeLayout relativeLayoutNotification;
	public TextView textViewNotificationValue;
	public ImageView imageViewBack, imageViewDelete;

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
		checkServerBtn = (Button) drawerLayout.findViewById(R.id.checkServerBtn);
		imageViewHelp = (ImageView) drawerLayout.findViewById(R.id.imageViewHelp);
		relativeLayoutNotification = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutNotification);
		textViewNotificationValue = (TextView) drawerLayout.findViewById(R.id.textViewNotificationValue);
		textViewNotificationValue.setTypeface(Fonts.latoRegular(activity));
		textViewNotificationValue.setVisibility(View.GONE);

		imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
		imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);

		//Top bar events
		topRl.setOnClickListener(topBarOnClickListener);
		imageViewMenu.setOnClickListener(topBarOnClickListener);
		checkServerBtn.setOnClickListener(topBarOnClickListener);

		checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(activity, Config.getServerUrlName(), Toast.LENGTH_SHORT).show();
				FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
				return false;
			}
		});

		imageViewSearchCancel.setOnClickListener(topBarOnClickListener);
		imageViewHelp.setOnClickListener(topBarOnClickListener);
		relativeLayoutNotification.setOnClickListener(topBarOnClickListener);
		imageViewBack.setOnClickListener(topBarOnClickListener);
		imageViewDelete.setOnClickListener(topBarOnClickListener);

		if(activity instanceof FreshActivity){
			relativeLayoutNotification.setVisibility(View.VISIBLE);
			imageViewHelp.setVisibility(View.GONE);
			imageViewSearchCancel.setVisibility(View.GONE);
			title.setText(activity.getResources().getString(R.string.jugnoo_fresh));

		} else if(activity instanceof HomeActivity){
			title.setText(activity.getResources().getString(R.string.app_name));
		}

	}

	private View.OnClickListener topBarOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.topRl:
					break;

				case R.id.imageViewMenu:
					drawerLayout.openDrawer(GravityCompat.START);
					FlurryEventLogger.event(FlurryEventNames.MENU_LOOKUP);
					break;

				case R.id.checkServerBtn:
					if(activity instanceof HomeActivity) {
						if (((HomeActivity)activity).map != null) {
							Data.latitude = ((HomeActivity)activity).map.getCameraPosition().target.latitude;
							Data.longitude = ((HomeActivity)activity).map.getCameraPosition().target.longitude;
						}
						activity.startActivity(new Intent(activity, FreshActivity.class));
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);

					} else if(activity instanceof FreshActivity){
						activity.finish();
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
					}
					break;


				case R.id.imageViewSearchCancel:
					if(activity instanceof HomeActivity){
						((HomeActivity)activity).onClickSearchCancel();
					}
					break;

				case R.id.imageViewHelp:
					if(activity instanceof HomeActivity) {
						((HomeActivity)activity).sosDialog(activity);
					}
					break;

				case R.id.relativeLayoutNotification:
					LatLng currLatLng = null;
					if(activity instanceof HomeActivity){
						currLatLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
					} else if(activity instanceof FreshActivity){
						currLatLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
					}
					if(currLatLng != null){
						Data.latitude = currLatLng.latitude;
						Data.longitude = currLatLng.longitude;
					}
					activity.startActivity(new Intent(activity, NotificationCenterActivity.class));
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(FlurryEventNames.NOTIFICATION_ICON);
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


}
