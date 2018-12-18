package product.clicklabs.jugnoo.promotion.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.dialogs.ReferDriverDialog;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class ReferralsFragment extends Fragment implements  GACategory, GAAction{

	private RelativeLayout relativeLayoutRoot;

	private ImageView imageViewLogo;
	private TextView textViewDesc, textViewLeaderboardSingle;
	private RelativeLayout relativeLayoutReferContainer, relativeLayoutReferSingle, relativeLayoutMultipleTab, relativeLayoutLeaderboardSingle;
	private LinearLayout linearLayoutLeaderBoard, linearLayoutRefer;
	private View rootView;
    private ShareActivity activity;
    private Bundle bundle;

	private LinearLayout llWhatsappShare, llReferralCode;
	private TextView tvMoreSharingOptions, tvReferralCodeValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_referrals, container, false);


        activity = (ShareActivity) getActivity();
        bundle = new Bundle();
		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GAUtils.trackScreenView(REFERRAL+HOME);

		imageViewLogo = (ImageView) rootView.findViewById(R.id.imageViewLogo);
		textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenMedium(activity));

		textViewLeaderboardSingle = (TextView)rootView.findViewById(R.id.textViewLeaderboardSingle);textViewLeaderboardSingle.setTypeface(Fonts.mavenMedium(activity));

		relativeLayoutReferContainer = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReferContainer);
		relativeLayoutReferSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReferSingle);
		relativeLayoutMultipleTab = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMultipleTab);
		relativeLayoutLeaderboardSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLeaderboardSingle);
		linearLayoutLeaderBoard = (LinearLayout) rootView.findViewById(R.id.linearLayoutLeaderBoard);
		((TextView)rootView.findViewById(R.id.textViewLeaderboard)).setTypeface(Fonts.mavenMedium(activity));
		linearLayoutRefer = (LinearLayout)rootView.findViewById(R.id.linearLayoutRefer);
		((TextView)rootView.findViewById(R.id.textViewReferDriver)).setTypeface(Fonts.mavenMedium(activity));

		llWhatsappShare = (LinearLayout) rootView.findViewById(R.id.llWhatsappShare);
		llReferralCode = (LinearLayout) rootView.findViewById(R.id.llReferralCode);
		((TextView) rootView.findViewById(R.id.tvWhatsapp)).setTypeface(Fonts.mavenMedium(activity));
		tvMoreSharingOptions = (TextView) rootView.findViewById(R.id.tvMoreSharingOptions);
		tvMoreSharingOptions.setTypeface(Fonts.mavenRegular(activity));
		Utils.setTextUnderline(tvMoreSharingOptions, activity.getString(R.string.view_more_sharing_options));
		((TextView)rootView.findViewById(R.id.tvYourReferralCode)).setTypeface(Fonts.mavenRegular(activity));
		tvReferralCodeValue = (TextView) rootView.findViewById(R.id.tvReferralCodeValue);
		tvReferralCodeValue.setTypeface(Fonts.mavenMedium(activity));


		tvMoreSharingOptions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(MyApplication.getInstance().isOnline()) {
					ReferralActions.openGenericShareIntent(activity, activity.getCallbackManager());
					try {
						if(activity.fromDeepLink){
						} else{
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					GAUtils.event(SIDE_MENU, FREE_GIFT, MORE_SHARING_OPTIONS+CLICKED);
				} else{
					DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
				}
			}
		});


		llWhatsappShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Utils.appInstalledOrNot(activity, "com.whatsapp")){
					ReferralActions.shareToWhatsapp(activity);
				} else {
					ReferralActions.openGenericShareIntent(activity, null);
				}
				GAUtils.event(SIDE_MENU, FREE_GIFT, GAAction.WHATSAPP+INVITE+CLICKED);
			}
		});

		llReferralCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText(tvReferralCodeValue.getText().toString(),
							tvReferralCodeValue.getText().toString());
					clipboard.setPrimaryClip(clip);
					Utils.showToast(activity, activity.getString(R.string.referral_code_copied));
					GAUtils.event(SIDE_MENU, FREE_GIFT, GAAction.REFERRAL+CODE_COPIED);
				} catch (Exception e) {}
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

		relativeLayoutLeaderboardSingle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linearLayoutLeaderBoard.performClick();
			}
		});

		relativeLayoutReferSingle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getReferDriverDialog().show();
			}
		});

		try {
			tvReferralCodeValue.setText(Data.userData.referralCode);
			textViewDesc.setText(Data.userData.getReferralMessages().referralShortMessage+" "+getString(R.string.details));

			SpannableString ss = new SpannableString(Data.userData.getReferralMessages().referralShortMessage+"\n "+getString(R.string.details));
			ClickableSpan clickableSpan = new ClickableSpan() {
				@Override
				public void onClick(View textView) {
					try {
						boolean html = Utils.DetectHtml.isHtml(Data.userData.getReferralMessages().referralMoreInfoMessage);
						DialogPopup.alertPopupLeftOriented(activity, "",
								Data.userData.getReferralMessages().referralMoreInfoMessage, true, true, html);
						GAUtils.event(SIDE_MENU, FREE_GIFT, GAAction.DETAILS+CLICKED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			ss.setSpan(clickableSpan, (textViewDesc.getText().length() - 6), textViewDesc.getText().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			textViewDesc.setText(ss);
			textViewDesc.setMovementMethod(LinkMovementMethod.getInstance());

			if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
				Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
						.into(imageViewLogo);
			}

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
