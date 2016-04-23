package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ReferralActions;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;


public class ShareEarnFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private ImageView imageViewLogo;
	private TextView textViewCode, textViewDesc, textViewMoreInfo;
	private Button buttonInvite;

	private View rootView;
    private ShareActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(ShareEarnFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_earn, container, false);


        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageViewLogo = (ImageView) rootView.findViewById(R.id.imageViewLogo);
		((TextView)rootView.findViewById(R.id.textViewShare)).setTypeface(Fonts.mavenLight(activity));
		textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenLight(activity));
		textViewMoreInfo = (TextView)rootView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenLight(activity));

		textViewCode = (TextView)rootView.findViewById(R.id.textViewCode);textViewCode.setTypeface(Fonts.mavenLight(activity));
		buttonInvite = (Button)rootView.findViewById(R.id.buttonInvite);buttonInvite.setTypeface(Fonts.mavenRegular(activity));

		textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					FlurryEventLogger.event(FlurryEventNames.INVITE_EARN_MORE_INFO);
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

		try {
			textViewCode.setText(Data.userData.referralCode);
			textViewDesc.setText(Data.referralMessages.referralShortMessage);

			if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
				Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
						.placeholder(R.drawable.free_rides_pic_new).error(R.drawable.free_rides_pic_new)
						.into(imageViewLogo);
			}
			FlurryEventLogger.event(activity, FlurryEventNames.WHO_VISITED_FREE_RIDE_SCREEN);

		} catch(Exception e){
			e.printStackTrace();
		}


		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
