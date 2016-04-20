package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import product.clicklabs.jugnoo.utils.NudgeClient;
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
	public Button buttonCheckServer;
	public ImageView imageViewHelp;
	public RelativeLayout relativeLayoutNotification;
	public TextView textViewNotificationValue;
	public ImageView imageViewBack, imageViewDelete;
	public TextView textViewAdd;

	public LinearLayout linearLayoutFreshSwapper;
	public ImageView imageViewAutoSwapper, imageViewFreshSwapper;

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
		imageViewHelp = (ImageView) drawerLayout.findViewById(R.id.imageViewHelp);
		relativeLayoutNotification = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutNotification);
		textViewNotificationValue = (TextView) drawerLayout.findViewById(R.id.textViewNotificationValue);
		textViewNotificationValue.setTypeface(Fonts.latoRegular(activity));
		textViewNotificationValue.setVisibility(View.GONE);

		imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
		imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);
		textViewAdd = (TextView) drawerLayout.findViewById(R.id.textViewAdd); textViewAdd.setTypeface(Fonts.mavenRegular(activity));
		linearLayoutFreshSwapper = (LinearLayout) drawerLayout.findViewById(R.id.linearLayoutFreshSwapper);
		imageViewAutoSwapper = (ImageView) drawerLayout.findViewById(R.id.imageViewAutoSwapper);
		imageViewFreshSwapper = (ImageView) drawerLayout.findViewById(R.id.imageViewFreshSwapper);


		//Top bar events
		topRl.setOnClickListener(topBarOnClickListener);
		imageViewMenu.setOnClickListener(topBarOnClickListener);
		buttonCheckServer.setOnClickListener(topBarOnClickListener);

		buttonCheckServer.setOnLongClickListener(new View.OnLongClickListener() {
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
		textViewAdd.setOnClickListener(topBarOnClickListener);
		imageViewAutoSwapper.setOnClickListener(topBarOnClickListener);
		imageViewFreshSwapper.setOnClickListener(topBarOnClickListener);

		if(activity instanceof FreshActivity){
			relativeLayoutNotification.setVisibility(View.VISIBLE);
			imageViewHelp.setVisibility(View.GONE);
			imageViewSearchCancel.setVisibility(View.GONE);
			title.setText(activity.getResources().getString(R.string.jugnoo_fresh));

			title.setVisibility(View.GONE);
			linearLayoutFreshSwapper.setVisibility(View.VISIBLE);
			imageViewAutoSwapper.setImageDrawable(getStateListDrawable(R.drawable.ic_swap_auto_alpha, R.drawable.ic_swap_auto));
			imageViewFreshSwapper.setImageResource(R.drawable.ic_swap_fresh);

		} else if(activity instanceof HomeActivity) {
			title.setText(activity.getResources().getString(R.string.app_name));

			title.setVisibility(View.GONE);
			linearLayoutFreshSwapper.setVisibility(View.VISIBLE);
			imageViewAutoSwapper.setImageResource(R.drawable.ic_swap_auto);
			imageViewFreshSwapper.setImageDrawable(getStateListDrawable(R.drawable.ic_swap_fresh_alpha, R.drawable.ic_swap_fresh));

		}

		setupFreshUI();

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
					NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_MENU_CLICKED, null);
					break;

				case R.id.buttonCheckServer:
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

				case R.id.textViewAdd:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).addAddress();
					}
					break;

				case R.id.imageViewAutoSwapper:
					if(activity instanceof FreshActivity){
						activity.finish();
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
						NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_BACK_TO_JUGNOO, null);
					}
					break;

				case R.id.imageViewFreshSwapper:
					if(activity instanceof HomeActivity) {
						if (((HomeActivity)activity).map != null
								&& ((HomeActivity)activity).mapStateListener != null
								&& ((HomeActivity)activity).mapStateListener.isMapSettled()) {
							Data.latitude = ((HomeActivity)activity).map.getCameraPosition().target.latitude;
							Data.longitude = ((HomeActivity)activity).map.getCameraPosition().target.longitude;
						}
						activity.startActivity(new Intent(activity, FreshActivity.class));
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
						NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_JUGNOO_FRESH_CLICKED, null);

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


	private StateListDrawable getStateListDrawable(int resourceNormal, int resourcePressed){
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
				activity.getResources().getDrawable(resourcePressed));
		stateListDrawable.addState(new int[]{},
				activity.getResources().getDrawable(resourceNormal));
		return stateListDrawable;
	}

	public void setupFreshUI(){
		try {
			if(1 == Data.freshAvailable){
				title.setVisibility(View.GONE);
				linearLayoutFreshSwapper.setVisibility(View.VISIBLE);
			} else{
				title.setVisibility(View.VISIBLE);
				linearLayoutFreshSwapper.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeFreshUI(){
		title.setVisibility(View.VISIBLE);
		linearLayoutFreshSwapper.setVisibility(View.GONE);
	}

}
