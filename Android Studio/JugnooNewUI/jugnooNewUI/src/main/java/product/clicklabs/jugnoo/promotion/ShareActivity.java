package product.clicklabs.jugnoo.promotion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.promotion.adapters.PromotionsFragmentAdapter;
import product.clicklabs.jugnoo.promotion.fragments.PromotionsFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralLeaderboardFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
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
	RelativeLayout relativeLayoutFragContainer;
	View viewGreyBG;

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
		tabs.setTextSize((int) (ASSL.Xscale() * 32f));
		tabs.setTextColorResource(R.color.text_color, R.color.text_color_light);
		tabs.setTypeface(Fonts.mavenMedium(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);

		relativeLayoutFragContainer = (RelativeLayout) findViewById(R.id.relativeLayoutFragContainer);
		relativeLayoutFragContainer.setVisibility(View.GONE);
		viewGreyBG = findViewById(R.id.viewGreyBG);
		viewGreyBG.setVisibility(View.GONE);
		getSupportFragmentManager().beginTransaction()
				.add(relativeLayoutFragContainer.getId(),
						new ReferralLeaderboardFragment(),
						ReferralLeaderboardFragment.class.getName())
				.addToBackStack(ReferralLeaderboardFragment.class.getName())
				.commitAllowingStateLoss();


		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_FREE_RIDES);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		getLeaderboardCall();
		getLeaderboardActivityCall();

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Back");
				performBackPressed();
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_INVITE_FRIENDS);
				} else if (position == 1) {
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_OFFERS);
				} else if (position == 2) {
					FlurryEventLogger.event(ShareActivity.this, Data.userData.getReferralLeaderboardEnabled() == 1 ?
							FlurryEventNames.WHO_CLICKED_ON_LEADERBOARD : FlurryEventNames.WHO_CLICKED_ON_ACTIVITY);
				} else if (position == 3) {
					FlurryEventLogger.event(ShareActivity.this, FlurryEventNames.WHO_CLICKED_ON_ACTIVITY);
				}
				Utils.hideSoftKeyboard(ShareActivity.this, imageViewBack);
				clearError();
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		relativeLayoutFragContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

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


	public void performBackPressed(){
		if(relativeLayoutFragContainer.getVisibility() == View.VISIBLE){
			closeLeaderboardFragment();
		} else {
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
        Bundle bundle = new Bundle();
        MyApplication.getInstance().logEvent(FirebaseEvents.REFERRAL+"_"+FirebaseEvents.BACK, bundle);

	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
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
					RestClient.getApiServices().leaderboardServerCall(Data.userData.accessToken, Config.getAutosClientId(),
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
												updateLeaderboardFragment();
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
			if(pos == 2){
				((ReferralActivityFragment) page).update();
			}
		}
	}

	public void clearError(){
		try {
			Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
			((PromotionsFragment)page).clearErrorForEditText();
		} catch (Exception e) {
			e.printStackTrace();
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
						performBackPressed();
					}
				}, true, false);
	}

	public void getLeaderboardActivityCall() {
		try {
			if(!HomeActivity.checkIfUserDataNull(this) && AppStatus.getInstance(this).isOnline(this)) {
				if(Data.userData.getReferralActivityEnabled() == 1) {
					DialogPopup.showLoadingDialog(this, "Loading...");
					RestClient.getApiServices().leaderboardActivityServerCall(Data.userData.accessToken, Config.getAutosClientId(),
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
												updateFragment(2);
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

	public void openLeaderboardFragment(){
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
		Animation alphaSlow = new AlphaAnimation(0.0f, 1.0f);
		alphaSlow.setDuration(400);

		relativeLayoutFragContainer.setVisibility(View.VISIBLE);
		relativeLayoutFragContainer.clearAnimation();
		viewGreyBG.setVisibility(View.VISIBLE);
		viewGreyBG.clearAnimation();

		relativeLayoutFragContainer.startAnimation(anim);
		viewGreyBG.startAnimation(alphaSlow);

	}

	public void closeLeaderboardFragment(){
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
		anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				relativeLayoutFragContainer.setVisibility(View.GONE);
				viewGreyBG.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		Animation alphaSlow = new AlphaAnimation(1.0f, 0.0f);
		alphaSlow.setDuration(400);

		relativeLayoutFragContainer.clearAnimation();
		viewGreyBG.clearAnimation();
		relativeLayoutFragContainer.startAnimation(anim);
		viewGreyBG.startAnimation(alphaSlow);

	}

	private void updateLeaderboardFragment(){
		try {
			Fragment page = getSupportFragmentManager().findFragmentByTag(ReferralLeaderboardFragment.class.getName());
			if(page != null){
                ((ReferralLeaderboardFragment)page).update();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
