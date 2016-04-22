package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.PromotionsActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ReferDriverActivity;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;

/**
 * Created by shankar on 4/8/16.
 */
public class MenuBar {

	Activity activity;
	DrawerLayout drawerLayout;

	//menu bar 
	public LinearLayout menuLayout;

	public LinearLayout linearLayoutProfile;
	public ImageView imageViewProfile;
	public TextView textViewUserName, textViewViewAccount;

	public RelativeLayout relativeLayoutGamePredict;
	public ImageView imageViewGamePredict;
	public TextView textViewGamePredict, textViewGamePredictNew;

	public RelativeLayout relativeLayoutGetRide;
	public ImageView imageViewGetRide, imageViewFresh;
	public TextView textViewGetRide;

	public RelativeLayout relativeLayoutInvite;
	public TextView textViewInvite;

	public RelativeLayout relativeLayoutWallet;
	public TextView textViewWallet, textViewWalletValue;
	public ProgressWheel progressBarMenuPaytmWalletLoading;

	public RelativeLayout relativeLayoutPromotions;
	public TextView textViewPromotions, textViewPromotionsValue;

	public RelativeLayout relativeLayoutTransactions;
	public TextView textViewTransactions;
	public ImageView imageViewTransactions;

	public RelativeLayout relativeLayoutReferDriver;
	public TextView textViewReferDriver, textViewReferDriverNew;

	public RelativeLayout relativeLayoutSupport;
	public TextView textViewSupport;

	public RelativeLayout relativeLayoutAbout;
	public TextView textViewAbout;

	public MenuBar(Activity activity, DrawerLayout rootView){
		this.activity = activity;
		this.drawerLayout = rootView;
		initComponents();
	}


	private void initComponents(){
		//Swipe menu
		menuLayout = (LinearLayout) drawerLayout.findViewById(R.id.menuLayout);


		linearLayoutProfile = (LinearLayout) drawerLayout.findViewById(R.id.linearLayoutProfile);
		imageViewProfile = (ImageView) drawerLayout.findViewById(R.id.imageViewProfile);
		textViewUserName = (TextView) drawerLayout.findViewById(R.id.textViewUserName);
		textViewUserName.setTypeface(Fonts.mavenRegular(activity));
		textViewViewAccount = (TextView) drawerLayout.findViewById(R.id.textViewViewAccount);
		textViewViewAccount.setTypeface(Fonts.latoRegular(activity));

		relativeLayoutGamePredict = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutGamePredict);
		imageViewGamePredict = (ImageView) drawerLayout.findViewById(R.id.imageViewGamePredict);
		textViewGamePredict = (TextView)drawerLayout.findViewById(R.id.textViewGamePredict); textViewGamePredict.setTypeface(Fonts.mavenLight(activity));
		textViewGamePredictNew = (TextView)drawerLayout.findViewById(R.id.textViewGamePredictNew); textViewGamePredictNew.setTypeface(Fonts.mavenLight(activity));

		relativeLayoutGetRide = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutGetRide);
		textViewGetRide = (TextView) drawerLayout.findViewById(R.id.textViewGetRide);
		textViewGetRide.setTypeface(Fonts.mavenLight(activity));
		imageViewGetRide = (ImageView) drawerLayout.findViewById(R.id.imageViewGetRide);
		imageViewFresh = (ImageView) drawerLayout.findViewById(R.id.imageViewFresh);

		relativeLayoutInvite = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutInvite);
		textViewInvite = (TextView) drawerLayout.findViewById(R.id.textViewInvite);
		textViewInvite.setTypeface(Fonts.mavenLight(activity));

		relativeLayoutWallet = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutWallet);
		textViewWallet = (TextView) drawerLayout.findViewById(R.id.textViewWallet);
		textViewWallet.setTypeface(Fonts.mavenLight(activity));
		textViewWalletValue = (TextView) drawerLayout.findViewById(R.id.textViewWalletValue);
		textViewWalletValue.setTypeface(Fonts.latoRegular(activity));
		progressBarMenuPaytmWalletLoading = (ProgressWheel) drawerLayout.findViewById(R.id.progressBarMenuPaytmWalletLoading);
		progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);

		relativeLayoutPromotions = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutPromotions);
		textViewPromotions = (TextView) drawerLayout.findViewById(R.id.textViewPromotions);
		textViewPromotions.setTypeface(Fonts.mavenLight(activity));
		textViewPromotionsValue = (TextView) drawerLayout.findViewById(R.id.textViewPromotionsValue);
		textViewPromotionsValue.setTypeface(Fonts.latoRegular(activity));
		textViewPromotionsValue.setVisibility(View.GONE);

		relativeLayoutTransactions = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutTransactions);
		textViewTransactions = (TextView) drawerLayout.findViewById(R.id.textViewTransactions);
		textViewTransactions.setTypeface(Fonts.mavenLight(activity));
		imageViewTransactions = (ImageView) drawerLayout.findViewById(R.id.imageViewTransactions);

		relativeLayoutReferDriver = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutReferDriver);
		textViewReferDriver = (TextView) drawerLayout.findViewById(R.id.textViewReferDriver);
		textViewReferDriver.setTypeface(Fonts.mavenLight(activity));
		textViewReferDriverNew = (TextView) drawerLayout.findViewById(R.id.textViewReferDriverNew);
		textViewReferDriverNew.setTypeface(Fonts.mavenLight(activity));

		relativeLayoutSupport = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutSupport);
		textViewSupport = (TextView) drawerLayout.findViewById(R.id.textViewSupport);
		textViewSupport.setTypeface(Fonts.mavenLight(activity));

		relativeLayoutAbout = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutAbout);
		textViewAbout = (TextView) drawerLayout.findViewById(R.id.textViewAbout);
		textViewAbout.setTypeface(Fonts.mavenLight(activity));




		// menu events
		linearLayoutProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivity(new Intent(activity, AccountActivity.class));
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_ACCOUNT);
			}
		});

		relativeLayoutGamePredict.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Data.userData.getGamePredictEnable() == 1) {
					Intent intent = new Intent(activity, T20Activity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(FlurryEventNames.WORLD_CUP_MENU);
					NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_GAME_CLICKED, null);
				}
			}
		});

		relativeLayoutGetRide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity instanceof HomeActivity) {
					if(1 == Data.freshAvailable) {
						if (((HomeActivity) activity).map != null
								&& ((HomeActivity)activity).mapStateListener != null
								&& ((HomeActivity)activity).mapStateListener.isMapSettled()) {
							Data.latitude = ((HomeActivity) activity).map.getCameraPosition().target.latitude;
							Data.longitude = ((HomeActivity) activity).map.getCameraPosition().target.longitude;
						}
						activity.startActivity(new Intent(activity, FreshActivity.class));
						activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
						NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_JUGNOO_FRESH_CLICKED, null);

					} else{
						drawerLayout.closeDrawer(GravityCompat.START);
					}

				} else if(activity instanceof FreshActivity){
					activity.finish();
					activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
				}
			}
		});

		relativeLayoutInvite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ShareActivity.class);
				intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FREE_RIDES_CLICKED, null);
			}
		});

		relativeLayoutWallet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, PaymentActivity.class);
				intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(FlurryEventNames.WALLET_MENU);
				FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_WALLET);
				NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_WALLET_CLICKED, null);
			}
		});

		relativeLayoutPromotions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LatLng currLatLng = null;
				if(activity instanceof HomeActivity){
					currLatLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
				} else if(activity instanceof FreshActivity){
					currLatLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
				}
				if (currLatLng != null) {
					Data.latitude = currLatLng.latitude;
					Data.longitude = currLatLng.longitude;
					if(AppStatus.getInstance(activity).isOnline(activity)) {
						activity.startActivity(new Intent(activity, PromotionsActivity.class));
						activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
						FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PROMOTIONS_SCREEN);
					} else {
						DialogPopup.dialogNoInternet(activity,
								Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
								new Utils.AlertCallBackWithButtonsInterface() {
									@Override
									public void positiveClick(View v) {
										relativeLayoutPromotions.performClick();
									}

									@Override
									public void neutralClick(View v) {

									}

									@Override
									public void negativeClick(View v) {

									}
								});
					}
				} else {
					Toast.makeText(activity, activity.getResources().getString(R.string.waiting_for_location),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		relativeLayoutTransactions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof HomeActivity) {
					Intent intent = new Intent(activity, RideTransactionsActivity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(FlurryEventNames.RIDE_HISTORY);

				} else if(activity instanceof FreshActivity){
					((FreshActivity)activity).openOrderHistory();
					//drawerLayout.closeDrawer(menuLayout);
				}
			}
		});

		relativeLayoutReferDriver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.startActivity(new Intent(activity, ReferDriverActivity.class));
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		relativeLayoutSupport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity instanceof HomeActivity) {
					activity.startActivity(new Intent(activity, SupportActivity.class));
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else if(activity instanceof FreshActivity){
					((FreshActivity)activity).openSupport();
					//drawerLayout.closeDrawer(menuLayout);
				}
			}
		});

		relativeLayoutAbout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startActivity(new Intent(activity, AboutActivity.class));
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
			}
		});



		menuLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		try {
			if(Data.userData.getGamePredictEnable() == 1
					&& !"".equalsIgnoreCase(Data.userData.getGamePredictName())){
				relativeLayoutGamePredict.setVisibility(View.VISIBLE);
				textViewGamePredict.setText(Data.userData.getGamePredictName());
				if(!"".equalsIgnoreCase(Data.userData.getGamePredictNew())){
					textViewGamePredictNew.setText(Data.userData.getGamePredictNew());
				} else{
					textViewGamePredictNew.setVisibility(View.GONE);
				}
				try {
					if(!"".equalsIgnoreCase(Data.userData.getGamePredictIconUrl())){
						Picasso.with(activity)
								.load(Data.userData.getGamePredictIconUrl())
								.placeholder(R.drawable.ic_worldcup)
								.error(R.drawable.ic_worldcup)
								.into(imageViewGamePredict);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else{
				relativeLayoutGamePredict.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(activity instanceof HomeActivity){
			textViewGetRide.setText(activity.getResources().getString(R.string.jugnoo_fresh));
			imageViewGetRide.setVisibility(View.GONE);
			imageViewFresh.setVisibility(View.VISIBLE);
			relativeLayoutInvite.setVisibility(View.VISIBLE);
			relativeLayoutPromotions.setVisibility(View.VISIBLE);
			relativeLayoutAbout.setVisibility(View.VISIBLE);
			textViewTransactions.setText(activity.getResources().getString(R.string.ride_history));
			if(Data.userData.getcToDReferralEnabled() == 1){
				relativeLayoutReferDriver.setVisibility(View.VISIBLE);
			}else {
				relativeLayoutReferDriver.setVisibility(View.GONE);
			}


		} else if(activity instanceof FreshActivity){
			textViewGetRide.setText(activity.getResources().getString(R.string.get_a_ride));
			imageViewGetRide.setVisibility(View.VISIBLE);
			imageViewFresh.setVisibility(View.GONE);
			relativeLayoutInvite.setVisibility(View.GONE);
			relativeLayoutPromotions.setVisibility(View.GONE);
			relativeLayoutAbout.setVisibility(View.GONE);
			textViewTransactions.setText(activity.getResources().getString(R.string.order_history));
			relativeLayoutReferDriver.setVisibility(View.GONE);
		}

		setupFreshUI();

	}

	public void setUserData(){
		try {
			textViewUserName.setText(Data.userData.userName);

			if(Data.userData.numCouponsAvaliable > 0) {
				textViewPromotionsValue.setVisibility(View.VISIBLE);
				textViewPromotionsValue.setText("" + Data.userData.numCouponsAvaliable);
			}
			else{
				textViewPromotionsValue.setVisibility(View.GONE);
			}

			textViewWalletValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
					Utils.getMoneyDecimalFormat().format(Data.userData.getTotalWalletBalance())));

			Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
			try {
				if(activity instanceof HomeActivity && ((HomeActivity)activity).activityResumed){
					Picasso.with(activity).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfile);
				}
				else{
					Picasso.with(activity).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewProfile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dismissPaytmLoading(){
		try {
			progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);
			textViewWalletValue.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setupFreshUI(){
		try {
			if(activity instanceof HomeActivity) {
				if (1 == Data.freshAvailable) {
					textViewGetRide.setText(activity.getResources().getString(R.string.jugnoo_fresh));
					imageViewGetRide.setVisibility(View.GONE);
					imageViewFresh.setVisibility(View.VISIBLE);
				} else {
					textViewGetRide.setText(activity.getResources().getString(R.string.get_a_ride));
					imageViewGetRide.setVisibility(View.VISIBLE);
					imageViewFresh.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
