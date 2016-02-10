package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
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


public class ShareEarnFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout linearLayoutRoot;

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

		((TextView)rootView.findViewById(R.id.textViewShare)).setTypeface(Fonts.mavenLight(activity));
		textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenLight(activity));
		textViewMoreInfo = (TextView)rootView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenLight(activity));

		textViewCode = (TextView)rootView.findViewById(R.id.textViewCode);textViewCode.setTypeface(Fonts.mavenLight(activity));
		buttonInvite = (Button)rootView.findViewById(R.id.buttonInvite);buttonInvite.setTypeface(Fonts.mavenRegular(activity));

		textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					FlurryEventLogger.event(INVITE_EARN_MORE_INFO);
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
							HashMap<String, String> map = new HashMap<>();
							map.put(KEY_USER_ID, Data.userData.userIdentifier);
							FlurryEventLogger.event(INVITE_SHARE_GENERIC_THROUGH_PUSH, map);
						} else{
							FlurryEventLogger.event(INVITE_GENERIC);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else{
					DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		try {
			textViewCode.setText(Data.userData.referralCode);
			textViewDesc.setText(Data.referralMessages.referralShortMessage);
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
