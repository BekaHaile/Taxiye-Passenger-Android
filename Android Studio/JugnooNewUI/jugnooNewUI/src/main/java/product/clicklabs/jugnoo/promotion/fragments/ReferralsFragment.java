package product.clicklabs.jugnoo.promotion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppPackage;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.dialogs.ReferDriverDialog;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;


public class ReferralsFragment extends Fragment implements FirebaseEvents{

	private RelativeLayout relativeLayoutRoot;

	private ImageView imageViewLogo, imageViewMore, imageViewFbMessanger, imageViewWhatsapp, imageViewMessage, imageViewEmail;
	private TextView textViewCode, textViewDesc, textViewMoreInfo, textViewLeaderboardSingle;
	private Button buttonInvite;
	private RelativeLayout relativeLayoutReferContainer, relativeLayoutReferSingle, relativeLayoutMultipleTab, relativeLayoutLeaderboardSingle,
			shareiconContainer;
	private LinearLayout linearLayoutLeaderBoard, linearLayoutRefer;
	private View rootView;
    private ShareActivity activity;
    private Bundle bundle;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(ReferralsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_referrals, container, false);


        activity = (ShareActivity) getActivity();
        bundle = new Bundle();
		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "Promotions", "Referrals");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageViewLogo = (ImageView) rootView.findViewById(R.id.imageViewLogo);
		((TextView)rootView.findViewById(R.id.textViewShare)).setTypeface(Fonts.mavenRegular(activity));
		textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenMedium(activity));
		textViewMoreInfo = (TextView)rootView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenMedium(activity));

		textViewCode = (TextView)rootView.findViewById(R.id.textViewCode);textViewCode.setTypeface(Fonts.mavenMedium(activity));
		textViewLeaderboardSingle = (TextView)rootView.findViewById(R.id.textViewLeaderboardSingle);textViewLeaderboardSingle.setTypeface(Fonts.mavenMedium(activity));
		buttonInvite = (Button)rootView.findViewById(R.id.buttonInvite);buttonInvite.setTypeface(Fonts.mavenMedium(activity));

		relativeLayoutReferContainer = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReferContainer);
		relativeLayoutReferSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReferSingle);
		relativeLayoutMultipleTab = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMultipleTab);
		relativeLayoutLeaderboardSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLeaderboardSingle);
		linearLayoutLeaderBoard = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeaderBoard);
		((TextView)rootView.findViewById(R.id.textViewLeaderboard)).setTypeface(Fonts.mavenMedium(activity));
		linearLayoutRefer = (LinearLayout)rootView.findViewById(R.id.linearLayoutRefer);
		((TextView)rootView.findViewById(R.id.textViewReferDriver)).setTypeface(Fonts.mavenMedium(activity));
		imageViewMore = (ImageView) rootView.findViewById(R.id.imageViewMore);
		imageViewFbMessanger = (ImageView) rootView.findViewById(R.id.imageViewFbMessanger);
		imageViewWhatsapp = (ImageView) rootView.findViewById(R.id.imageViewWhatsapp);
		imageViewMessage = (ImageView) rootView.findViewById(R.id.imageViewMessage);
		imageViewEmail = (ImageView) rootView.findViewById(R.id.imageViewEmail);
		shareiconContainer = (RelativeLayout) rootView.findViewById(R.id.shareiconContainer);

		ArrayList<AppPackage> appPackages = new ArrayList<>();
		appPackages.add(new AppPackage(0, "com.whatsapp"));
		Utils.checkAppsArrayInstall(activity, appPackages);
		if (appPackages.get(0).getInstalled() == 1) {
			imageViewWhatsapp.setVisibility(View.VISIBLE);
			imageViewEmail.setVisibility(View.GONE);
		} else{
			imageViewEmail.setVisibility(View.VISIBLE);
			imageViewWhatsapp.setVisibility(View.GONE);
		}

		textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					FlurryEventLogger.event(FlurryEventNames.INVITE_EARN_MORE_INFO);
					FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Details");
                    MyApplication.getInstance().logEvent(REFERRAL+"_"+DETAILS, bundle);
					DialogPopup.alertPopupWithListener(activity, "", Data.referralMessages.referralMoreInfoMessage, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {
			if(Data.userData.getInviteFriendButton() == 1){
                buttonInvite.setVisibility(View.VISIBLE);
            } else{
                buttonInvite.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textViewDesc.getLayoutParams();
                params.setMargins(0, 100, 0, 0);
                textViewDesc.setLayoutParams(params);

				LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) shareiconContainer.getLayoutParams();
				params1.setMargins(0, 100, 0, 0);
				shareiconContainer.setLayoutParams(params1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(AppStatus.getInstance(activity).isOnline(activity)) {
					FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "invite friends");
					ReferralActions.openGenericShareIntent(activity, activity.getCallbackManager());
					try {
						if(activity.fromDeepLink){
							FlurryEventLogger.event(activity, FlurryEventNames.INVITE_SHARE_GENERIC_THROUGH_PUSH);
						} else{
							FlurryEventLogger.event(FlurryEventNames.INVITE_GENERIC);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_FRIENDS, null);
				} else{
					DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		imageViewMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonInvite.performClick();
                MyApplication.getInstance().logEvent(REFERRAL+"_"+THREEDOTS, bundle);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "Others");
			}
		});

		imageViewFbMessanger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReferralActions.shareToFacebook(activity, true, activity.getCallbackManager());
                MyApplication.getInstance().logEvent(REFERRAL+"_"+OUTSIDE_FB_MESSENGER, bundle);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "Facebook");
			}
		});

		imageViewWhatsapp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReferralActions.shareToWhatsapp(activity);
                MyApplication.getInstance().logEvent(REFERRAL+"_"+OUTSIDE_WHATSAPP, bundle);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "WhatsApp");
			}
		});

		imageViewMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReferralActions.sendSMSIntent(activity);
                MyApplication.getInstance().logEvent(REFERRAL+"_"+OUTSIDE_MOBILE_SMS, bundle);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "Message");
			}
		});

		imageViewEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReferralActions.openMailIntent(activity);
				MyApplication.getInstance().logEvent(REFERRAL+"_"+OUTSIDE_EMAIL, bundle);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up", "Email");
			}
		});

		linearLayoutLeaderBoard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openLeaderboardFragment();
				FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Leaderboard");
			}
		});

		linearLayoutRefer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				relativeLayoutReferSingle.performClick();
			}
		});

		relativeLayoutLeaderboardSingle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linearLayoutLeaderBoard.performClick();
			}
		});

		relativeLayoutReferSingle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Refer a Driver");
				getReferDriverDialog().show();
			}
		});

		try {
			textViewCode.setText(Data.userData.referralCode);
			textViewDesc.setText(Data.referralMessages.referralShortMessage+ "Details");

			SpannableString ss = new SpannableString(Data.referralMessages.referralShortMessage+" Details");
			ClickableSpan clickableSpan = new ClickableSpan() {
				@Override
				public void onClick(View textView) {
					textViewMoreInfo.performClick();
				}
			};
			ss.setSpan(clickableSpan, (textViewDesc.getText().length() - 6), textViewDesc.getText().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			textViewDesc.setText(ss);
			textViewDesc.setMovementMethod(LinkMovementMethod.getInstance());

			if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
				Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
						.placeholder(R.drawable.ic_promotions_friend_refer)
						.error(R.drawable.ic_promotions_friend_refer)
						.into(imageViewLogo);
			}
			FlurryEventLogger.event(activity, FlurryEventNames.WHO_VISITED_FREE_RIDE_SCREEN);

			relativeLayoutReferContainer.setVisibility(View.GONE);
			if (Data.userData != null) {
				relativeLayoutMultipleTab.setVisibility(View.GONE);
				relativeLayoutReferSingle.setVisibility(View.GONE);
				relativeLayoutLeaderboardSingle.setVisibility(View.GONE);
				if (Data.userData.getReferralLeaderboardEnabled() == 1 && Data.userData.getcToDReferralEnabled() == 1) {
					relativeLayoutMultipleTab.setVisibility(View.VISIBLE);
					relativeLayoutReferContainer.setVisibility(View.VISIBLE);
				}
				else if (Data.userData.getReferralLeaderboardEnabled() == 1 && Data.userData.getcToDReferralEnabled() != 1) {
					relativeLayoutLeaderboardSingle.setVisibility(View.VISIBLE);
					relativeLayoutReferContainer.setVisibility(View.VISIBLE);
				}
				else if (Data.userData.getReferralLeaderboardEnabled() != 1 && Data.userData.getcToDReferralEnabled() == 1) {
					relativeLayoutReferSingle.setVisibility(View.VISIBLE);
					relativeLayoutReferContainer.setVisibility(View.VISIBLE);
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}


		return rootView;
	}

	private ReferDriverDialog referDriverDialog;
	private ReferDriverDialog getReferDriverDialog(){
		if(referDriverDialog == null){
			referDriverDialog = new ReferDriverDialog(activity);
		}
		return referDriverDialog;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
        System.gc();
	}


}
