package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar {


	Activity activity;
	DrawerLayout drawerLayout;

	//Top RL
	public RelativeLayout topRl;
	public ImageView imageViewMenu, imageViewAppToggle, imageViewSearchIcon;
	public TextView title;
	public Button buttonCheckServer;
	public ImageView imageViewHelp;
	public ImageView imageViewBack, imageViewDelete;
	public TextView textViewAdd;

	public TopBar(Activity activity, DrawerLayout drawerLayout){
		this.activity = activity;
		this.drawerLayout = drawerLayout;
		setupTopBar();
	}

	private void setupTopBar(){
		topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
		imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
		imageViewAppToggle = (ImageView) drawerLayout.findViewById(R.id.imageViewAppToggle);
		imageViewSearchIcon = (ImageView) drawerLayout.findViewById(R.id.imageViewSearchIcon);
		title = (TextView) drawerLayout.findViewById(R.id.title);title.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		buttonCheckServer = (Button) drawerLayout.findViewById(R.id.buttonCheckServer);
		imageViewHelp = (ImageView) drawerLayout.findViewById(R.id.imageViewHelp);

		imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
		imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);
		textViewAdd = (TextView) drawerLayout.findViewById(R.id.textViewAdd); textViewAdd.setTypeface(Fonts.mavenRegular(activity));


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

		imageViewAppToggle.setOnClickListener(topBarOnClickListener);
		imageViewSearchIcon.setOnClickListener(topBarOnClickListener);
		imageViewHelp.setOnClickListener(topBarOnClickListener);
		imageViewBack.setOnClickListener(topBarOnClickListener);
		imageViewDelete.setOnClickListener(topBarOnClickListener);
		textViewAdd.setOnClickListener(topBarOnClickListener);

		LinearLayout.LayoutParams paramsAppToggle = (LinearLayout.LayoutParams) imageViewAppToggle.getLayoutParams();
		float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
		if(activity instanceof FreshActivity){
			imageViewHelp.setVisibility(View.GONE);
			imageViewAppToggle.setImageResource(R.drawable.ic_auto_white);
			paramsAppToggle.width = (int)(minRatio * 78f);
			paramsAppToggle.height = (int)(minRatio * 68f);
			imageViewAppToggle.setLayoutParams(paramsAppToggle);
			imageViewSearchIcon.setVisibility(View.GONE);

			title.setText(activity.getResources().getString(R.string.fresh));

		} else if(activity instanceof HomeActivity) {
			imageViewAppToggle.setImageResource(R.drawable.ic_fresh_white);
			paramsAppToggle.width = (int)(minRatio * 92f);
			paramsAppToggle.height = (int)(minRatio * 66f);
			imageViewAppToggle.setLayoutParams(paramsAppToggle);
			imageViewSearchIcon.setVisibility(View.GONE);

			title.setText(activity.getResources().getString(R.string.app_name));
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

				case R.id.imageViewHelp:
					if(activity instanceof HomeActivity) {
						((HomeActivity)activity).sosDialog(activity);
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

				case R.id.textViewAdd:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).addAddress();
					}
					break;

				case R.id.imageViewAppToggle:
					if(activity instanceof FreshActivity){
						activity.finish();
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
						NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_BACK_TO_JUGNOO, null);

					} else if(activity instanceof HomeActivity) {
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

				case R.id.imageViewSearchIcon:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).openSearch();
					}
					break;

			}
		}
	};



	public void setUserData(){
		try {
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
				imageViewAppToggle.setVisibility(View.VISIBLE);
			} else{
				imageViewAppToggle.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
