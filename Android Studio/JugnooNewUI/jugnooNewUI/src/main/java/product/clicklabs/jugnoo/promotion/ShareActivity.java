package product.clicklabs.jugnoo.promotion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.promotion.adapters.PromotionsFragmentAdapter;
import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralLeaderboardFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ShareActivity extends BaseFragmentActivity {
	
	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	ViewPager viewPager;
	PromotionsFragmentAdapter promotionsFragmentAdapter;
	PagerSlidingTabStrip tabs;
    private CallbackManager callbackManager;
	public boolean fromDeepLink = false;

	public LeaderboardResponse leaderboardResponse;
	public LeaderboardActivityResponse leaderboardActivityResponse;


	public CallbackManager getCallbackManager(){
		return callbackManager;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		try {
			if(getIntent().hasExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK)){
				fromDeepLink = getIntent().getBooleanExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(ShareActivity.this, relative, 1134, 720, false);

        callbackManager = CallbackManager.Factory.create();

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		promotionsFragmentAdapter = new PromotionsFragmentAdapter(this, getSupportFragmentManager());
		viewPager.setAdapter(promotionsFragmentAdapter);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setTextColorResource(R.color.text_color, R.color.text_color);
		tabs.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		tabs.setTextSize(28);
		tabs.setViewPager(viewPager);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		getLeaderboardCall();
		getLeaderboardActivityCall();

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_INVITE_FRIENDS);
				} else if(position == 1){
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_OFFERS);
				} else if(position == 2){
					FlurryEventLogger.event(ShareActivity.this, Data.userData.getReferralLeaderboardEnabled() == 1 ?
							FlurryEventNames.WHO_CLICKED_ON_LEADERBOARD : FlurryEventNames.WHO_CLICKED_ON_ACTIVITY);
				} else if(position == 3){
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_ACTIVITY);
				}
				Utils.hideSoftKeyboard(ShareActivity.this, imageViewBack);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performbackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performbackPressed();
		super.onBackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		try {
			ASSL.closeActivity(relative);
			System.gc();
			super.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getLeaderboardCall() {
		try {
			if(!HomeActivity.checkIfUserDataNull(this) && AppStatus.getInstance(this).isOnline(this)) {
				if(Data.userData.getReferralLeaderboardEnabled() == 1) {
					DialogPopup.showLoadingDialog(this, "Loading...");
					RestClient.getApiServices().leaderboardServerCall(Data.userData.accessToken, Config.getClientId(),
							new Callback<LeaderboardResponse>() {
								@Override
								public void success(LeaderboardResponse leaderboardResponse, Response response) {
									DialogPopup.dismissLoadingDialog();
									try {
										String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
										JSONObject jObj;
										jObj = new JSONObject(jsonString);
										int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
										String message = JSONParser.getServerMessage(jObj);
										if (!SplashNewActivity.checkIfTrivialAPIErrors(ShareActivity.this, jObj)) {
											if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
												Log.v("success at", "leaderboeard");
												ShareActivity.this.leaderboardResponse = leaderboardResponse;
												updateFragment(2);
											} else {
												retryLeaderboardDialog(message);
											}
										}
									} catch (Exception exception) {
										exception.printStackTrace();
										retryLeaderboardDialog(Data.SERVER_ERROR_MSG);
									}
								}

								@Override
								public void failure(RetrofitError error) {
									DialogPopup.dismissLoadingDialog();
									retryLeaderboardDialog(Data.SERVER_NOT_RESOPNDING_MSG);
								}
							});
				}
			} else {
				//retryLeaderboardDialog(Data.CHECK_INTERNET_MSG);
				DialogPopup.dialogNoInternet(this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View v) {
						getLeaderboardCall();
					}

					@Override
					public void neutralClick(View v) {

					}

					@Override
					public void negativeClick(View v) {

					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateFragment(int pos) {
		Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + pos);
		if (page != null) {
			if(pos == 2 && Data.userData.getReferralLeaderboardEnabled() == 1){
				((ReferralLeaderboardFragment) page).update();

			} else if(pos == 2 && Data.userData.getReferralLeaderboardEnabled() != 1){
				((ReferralActivityFragment) page).update();

			} else if(pos == 3){
				((ReferralActivityFragment) page).update();
			}
		}
	}

	public void retryLeaderboardDialog(String message){
		DialogPopup.alertPopupTwoButtonsWithListeners(this, "", message,
				getResources().getString(R.string.retry),
				getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLeaderboardCall();
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						performbackPressed();
					}
				}, true, false);
	}

	public void getLeaderboardActivityCall() {
		try {
			if(!HomeActivity.checkIfUserDataNull(this) && AppStatus.getInstance(this).isOnline(this)) {
				if(Data.userData.getReferralActivityEnabled() == 1) {
					DialogPopup.showLoadingDialog(this, "Loading...");
					RestClient.getApiServices().leaderboardActivityServerCall(Data.userData.accessToken, Config.getClientId(),
							new Callback<LeaderboardActivityResponse>() {
								@Override
								public void success(LeaderboardActivityResponse leaderboardActivityResponse, Response response) {
									DialogPopup.dismissLoadingDialog();
									try {
										String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
										JSONObject jObj;
										jObj = new JSONObject(jsonString);
										int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
										String message = JSONParser.getServerMessage(jObj);
										if (!SplashNewActivity.checkIfTrivialAPIErrors(ShareActivity.this, jObj)) {
											if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
												ShareActivity.this.leaderboardActivityResponse = leaderboardActivityResponse;
												updateFragment(Data.userData.getReferralLeaderboardEnabled() == 1 ? 3 : 2);
												Log.v("success at", "leaderboeard");
											} else {
												DialogPopup.alertPopup(ShareActivity.this, "", message);
											}
										}
									} catch (Exception exception) {
										exception.printStackTrace();
									}
								}

								@Override
								public void failure(RetrofitError error) {
									DialogPopup.dismissLoadingDialog();
								}
							});
				}
			} else {
				//retryLeaderboardDialog(Data.CHECK_INTERNET_MSG);
				DialogPopup.dialogNoInternet(this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View v) {
						getLeaderboardActivityCall();
					}

					@Override
					public void neutralClick(View v) {

					}

					@Override
					public void negativeClick(View v) {

					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
