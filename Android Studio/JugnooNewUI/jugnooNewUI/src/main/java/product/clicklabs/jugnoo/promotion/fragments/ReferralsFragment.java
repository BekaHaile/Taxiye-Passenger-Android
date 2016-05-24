package product.clicklabs.jugnoo.promotion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.dialogs.ReferDriverDialog;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;


public class ReferralsFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot;

	private ImageView imageViewLogo;
	private TextView textViewCode, textViewDesc, textViewMoreInfo;
	private Button buttonInvite;
	private RelativeLayout relativeLayoutReferSingle, relativeLayoutMultipleTab;
	private LinearLayout linearLayoutLeaderBoard, linearLayoutRefer;
	private View rootView;
    private ShareActivity activity;

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

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Earn");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageViewLogo = (ImageView) rootView.findViewById(R.id.imageViewLogo);
		((TextView)rootView.findViewById(R.id.textViewShare)).setTypeface(Fonts.mavenRegular(activity));
		textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenMedium(activity));
		textViewMoreInfo = (TextView)rootView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenMedium(activity));

		textViewCode = (TextView)rootView.findViewById(R.id.textViewCode);textViewCode.setTypeface(Fonts.mavenMedium(activity));
		buttonInvite = (Button)rootView.findViewById(R.id.buttonInvite);buttonInvite.setTypeface(Fonts.mavenMedium(activity));

		relativeLayoutReferSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReferSingle);
		relativeLayoutMultipleTab = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMultipleTab);
		linearLayoutLeaderBoard = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeaderBoard);
		((TextView)rootView.findViewById(R.id.textViewLeaderboard)).setTypeface(Fonts.mavenMedium(activity));
		linearLayoutRefer = (LinearLayout)rootView.findViewById(R.id.linearLayoutRefer);
		((TextView)rootView.findViewById(R.id.textViewReferDriver)).setTypeface(Fonts.mavenMedium(activity));



		textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					FlurryEventLogger.event(FlurryEventNames.INVITE_EARN_MORE_INFO);
					FlurryEventLogger.eventGA(Constants.REFERRAL, "rides", "more info");
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

		buttonInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(AppStatus.getInstance(activity).isOnline(activity)) {
					FlurryEventLogger.eventGA(Constants.REFERRAL, "rides", "invite friends");
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

		linearLayoutLeaderBoard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openLeaderboardFragment();
			}
		});

		linearLayoutRefer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				relativeLayoutReferSingle.performClick();
			}
		});

		relativeLayoutReferSingle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getReferDriverDialog().show();
			}
		});

		try {
			textViewCode.setText(Data.userData.referralCode);
			textViewDesc.setText(Data.referralMessages.referralShortMessage);

			if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
				Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
						.placeholder(R.drawable.ic_promotions_friend_refer)
						.error(R.drawable.ic_promotions_friend_refer)
						.into(imageViewLogo);
			}
			FlurryEventLogger.event(activity, FlurryEventNames.WHO_VISITED_FREE_RIDE_SCREEN);


				if(Data.userData != null) {
					relativeLayoutMultipleTab.setVisibility(View.GONE);
					relativeLayoutReferSingle.setVisibility(View.GONE);
					if (Data.userData.getReferralLeaderboardEnabled() == 1 && Data.userData.getcToDReferralEnabled() == 1) {
						relativeLayoutMultipleTab.setVisibility(View.VISIBLE);
					} else if (Data.userData.getReferralLeaderboardEnabled() == 1 && Data.userData.getcToDReferralEnabled() != 1) {
						//relativeLayoutReferSingle.setVisibility(View.VISIBLE);
					} else if (Data.userData.getReferralLeaderboardEnabled() != 1 && Data.userData.getcToDReferralEnabled() == 1) {
						relativeLayoutReferSingle.setVisibility(View.VISIBLE);
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
