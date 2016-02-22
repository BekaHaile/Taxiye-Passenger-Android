package product.clicklabs.jugnoo.emergency.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiEmergencyAlert;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.emergency.EmergencyModeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * For emergency mode enabled fragment
 * Shows Call Police, Call Emergency Contacts
 * and Disable emergency mode options
 *
 * Created by shankar on 2/22/16.
 */
public class EmergencyModeEnabledFragment extends Fragment {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack;

	private TextView textViewEmergencyModeEnabledMessage;
	private Button buttonCallPolice, buttonCallEmergencyContact, buttonDisableEmergencyMode;

	private View rootView;
    private FragmentActivity activity;
	private LocationFetcher locationFetcher;
	private Location location;

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Config.getFlurryKey());
		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(EmergencyModeEnabledFragment.class.getSimpleName() + " started");
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(activity);
		locationFetcher.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		locationFetcher.destroy();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_mode_enabled, container, false);


        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

		textViewEmergencyModeEnabledMessage = (TextView) rootView.findViewById(R.id.textViewEmergencyModeEnabledMessage);
		textViewEmergencyModeEnabledMessage.setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOr)).setTypeface(Fonts.mavenLight(activity));

		buttonCallPolice = (Button) rootView.findViewById(R.id.buttonCallPolice);
		buttonCallPolice.setTypeface(Fonts.mavenRegular(activity));
		buttonCallEmergencyContact = (Button) rootView.findViewById(R.id.buttonCallEmergencyContact);
		buttonCallEmergencyContact.setTypeface(Fonts.mavenRegular(activity));
		buttonDisableEmergencyMode = (Button) rootView.findViewById(R.id.buttonDisableEmergencyMode);
		buttonDisableEmergencyMode.setTypeface(Fonts.mavenRegular(activity));


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

					case R.id.buttonCallPolice:
						Utils.openCallIntent(activity, "100");
						new ApiEmergencyAlert(activity).raiseEmergencyAlertAPI(getLocation(), Constants.EMERGENCY_CALL_100);
						break;

					case R.id.buttonCallEmergencyContact:

						break;

					case R.id.buttonDisableEmergencyMode:
						performBackPressed();
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		buttonCallPolice.setOnClickListener(onClickListener);
		buttonCallEmergencyContact.setOnClickListener(onClickListener);
		buttonDisableEmergencyMode.setOnClickListener(onClickListener);

		locationFetcher = new LocationFetcher(activity, new LocationUpdate() {
			@Override
			public void onLocationChanged(Location location, int priority) {
				EmergencyModeEnabledFragment.this.location = location;
			}
		}, 1000, 2);


		return rootView;
	}


	private void performBackPressed() {
		if(activity instanceof EmergencyModeActivity){
			((EmergencyModeActivity)activity).performBackPressed();
		}
	}

	private Location getLocation(){
		if(location != null){
			return location;
		} else{
			return HomeActivity.myLocation;
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rootView);
        System.gc();
	}


}
