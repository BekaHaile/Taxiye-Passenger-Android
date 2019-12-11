package product.clicklabs.jugnoo.emergency.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiEmergencyAlert;
import product.clicklabs.jugnoo.apis.ApiEmergencyDisable;
import product.clicklabs.jugnoo.base.BaseFragment;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.FragTransUtils;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * For emergency mode enabled fragment
 * Shows Call Police, Call Emergency Contacts
 * and Disable emergency mode options
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
public class EmergencyModeEnabledFragment extends BaseFragment {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack;

	private TextView textViewEmergencyModeEnabledTitle, textViewEmergencyModeEnabledMessage;
	private Button buttonCallPolice, buttonCallEmergencyContact, buttonDisableEmergencyMode;
	private LinearLayout linearLayoutDisableEmergencyMode;

	private View rootView;
    private FragmentActivity activity;
	private Location location;

	private String driverId, engagementId;

	public EmergencyModeEnabledFragment(){}

	public static EmergencyModeEnabledFragment newInstance(String driverId, String engagementId){
		EmergencyModeEnabledFragment fragment = new EmergencyModeEnabledFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.KEY_DRIVER_ID, driverId);
		bundle.putString(Constants.KEY_ENGAGEMENT_ID, engagementId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(activity);
		getLocationFetcher().connect(this, 10000);
	}

	@Override
	public void locationChanged(Location location) {
		super.locationChanged(location);
		EmergencyModeEnabledFragment.this.location = location;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_mode_enabled, container, false);

		driverId = getArguments().getString(Constants.KEY_DRIVER_ID);
		engagementId = getArguments().getString(Constants.KEY_ENGAGEMENT_ID);

        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

		textViewEmergencyModeEnabledTitle = (TextView) rootView.findViewById(R.id.textViewEmergencyModeEnabledTitle);
		textViewEmergencyModeEnabledTitle.setTypeface(Fonts.mavenRegular(activity));
		textViewEmergencyModeEnabledMessage = (TextView) rootView.findViewById(R.id.textViewEmergencyModeEnabledMessage);
		textViewEmergencyModeEnabledMessage.setTypeface(Fonts.mavenRegular(activity));
		textViewEmergencyModeEnabledMessage.setText(getString(R.string.emergency_mode_enabled_message, getString(R.string.app_name)));
		((TextView)rootView.findViewById(R.id.textViewOr)).setTypeface(Fonts.mavenLight(activity));

		buttonCallPolice = (Button) rootView.findViewById(R.id.buttonCallPolice);
		buttonCallPolice.setTypeface(Fonts.mavenRegular(activity));
		buttonCallEmergencyContact = (Button) rootView.findViewById(R.id.buttonCallEmergencyContact);
		buttonCallEmergencyContact.setTypeface(Fonts.mavenRegular(activity));
		buttonDisableEmergencyMode = (Button) rootView.findViewById(R.id.buttonDisableEmergencyMode);
		buttonDisableEmergencyMode.setTypeface(Fonts.mavenRegular(activity));

		textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));

		linearLayoutDisableEmergencyMode = (LinearLayout) rootView.findViewById(R.id.linearLayoutDisableEmergencyMode);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

					case R.id.buttonCallPolice:
						Utils.openCallIntent(activity, Prefs.with(activity).getString(Constants.KEY_EMERGENCY_NO, getString(R.string.police_number)));
						break;

					case R.id.buttonCallEmergencyContact:
						if(activity instanceof EmergencyActivity) {
							new FragTransUtils().openEmergencyContactsOperationsFragment(activity,
									((EmergencyActivity)activity).getContainer(), engagementId,
									ContactsListAdapter.ListMode.CALL_CONTACTS);
						}
						break;

					case R.id.buttonDisableEmergencyMode:
						disableEmergencyMode();
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		buttonCallPolice.setOnClickListener(onClickListener);
		buttonCallEmergencyContact.setOnClickListener(onClickListener);
		buttonDisableEmergencyMode.setOnClickListener(onClickListener);



		requestLocationPermissionExplicit();





		return rootView;
	}

	@Override
	public void permissionGranted(int requestCode) {
		if(requestCode==REQUEST_CODE_PERMISSION_LOCATION){
			callEmergencyAlert();
		}

	}

	public void callEmergencyAlert(){
		int modeEnabled = Prefs.with(activity).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
		if(modeEnabled == 0) {
			linearLayoutDisableEmergencyMode.setVisibility(View.GONE);
			new ApiEmergencyAlert(activity, new ApiEmergencyAlert.Callback() {
				@Override
				public void onSuccess() {
					linearLayoutDisableEmergencyMode.setVisibility(View.VISIBLE);
					Prefs.with(activity).save(Constants.SP_EMERGENCY_MODE_ENABLED, 1);
					HomeActivity.localModeEnabled = 1;
				}

				@Override
				public void onFailure() {

				}

				@Override
				public double getSavedLatitude() {
					return LocationFetcher.getSavedLatFromSP(activity);
				}

				@Override
				public double getSavedLongitude() {
					return LocationFetcher.getSavedLngFromSP(activity);
				}
			}).raiseEmergencyAlertAPI(getLocation(), "", driverId, engagementId);
		} else{
			linearLayoutDisableEmergencyMode.setVisibility(View.VISIBLE);
		}
	}

	public void disableEmergencyMode(){
		new ApiEmergencyDisable(activity, new ApiEmergencyDisable.Callback() {
			@Override
			public void onSuccess() {
				performBackPressed();
			}

			@Override
			public void onFailure() {

			}

			@Override
			public void onRetry(View view) {
				disableEmergencyMode();
			}

			@Override
			public void onNoRetry(View view) {

			}
		}).emergencyDisable(engagementId);
	}


	private void performBackPressed() {
		if(activity instanceof EmergencyActivity){
			((EmergencyActivity)activity).performBackPressed();
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
