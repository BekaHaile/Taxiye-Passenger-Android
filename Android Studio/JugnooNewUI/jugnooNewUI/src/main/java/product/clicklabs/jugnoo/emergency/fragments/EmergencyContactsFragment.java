package product.clicklabs.jugnoo.emergency.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiEmergencyContactsList;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.FragTransUtils;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * For displaying emergency contacts
 * and options to add or delete them
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
public class EmergencyContactsFragment extends Fragment  {

	public static final int REQUEST_CODE_CONTACT = 1000;
	private final String TAG = EmergencyContactsFragment.class.getSimpleName();
	private RelativeLayout relative;

	private TextView textViewTitle, textViewEdit;
	private ImageView imageViewBack;

	private LinearLayout linearLayoutContactsList;
	private RecyclerView recyclerViewContacts;

	private LinearLayout linearLayoutNoContacts;

	private Button buttonAddContact;
	private ContactsListAdapter contactsListAdapter;
	private ArrayList<ContactBean> contactBeans;

	private View rootView;
    private FragmentActivity activity;
    private PermissionCommon mPermissionCommon;

	@Override
	public void onStart() {
		super.onStart();
//		FlurryAgent.init(activity, Config.getFlurryKey());
//		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//		FlurryAgent.onEvent(EmergencyContactsFragment.class.getSimpleName() + " started");
	}

	@Override
	public void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(activity);
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        activity = getActivity();

        mPermissionCommon = new PermissionCommon(this).setCallback(new PermissionCommon.PermissionListener() {
			@Override
			public void permissionGranted(int requestCode) {
				openAddEmergencyContactsFragments();
			}

			@Override
			public boolean permissionDenied(int requestCode, boolean neverAsk) {
				return true;
			}

			@Override
			public void onRationalRequestIntercepted(int requestCode) {

			}
		});

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewEdit = (TextView) rootView.findViewById(R.id.textViewEdit); textViewEdit.setTypeface(Fonts.mavenRegular(activity));

		linearLayoutContactsList = (LinearLayout) rootView.findViewById(R.id.linearLayoutContactsList);
		((TextView)rootView.findViewById(R.id.textViewContacts)).setTypeface(Fonts.mavenMedium(activity));
		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		linearLayoutNoContacts = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoContacts);
		((TextView)rootView.findViewById(R.id.textViewConfigureContacts)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewInformYourFriends)).setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewInformYourFriends)).setText(getString(R.string.inform_your_friends, getString(R.string.app_name)));

		textViewEdit.setVisibility(View.VISIBLE);
		linearLayoutContactsList.setVisibility(View.VISIBLE);
		linearLayoutNoContacts.setVisibility(View.GONE);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));


		buttonAddContact = (Button) rootView.findViewById(R.id.buttonAddContact);
		buttonAddContact.setTypeface(Fonts.mavenRegular(activity));

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, final ContactBean contactBean) {
						if(ContactsListAdapter.ListMode.DELETE_CONTACTS == contactsListAdapter.getListMode()){
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
									activity.getResources().getString(R.string.delete_emergency_contact_message),
									activity.getResources().getString(R.string.delete),
									activity.getResources().getString(R.string.cancel),
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											deleteEmergencyContactAPI(activity, contactBean.getId());
										}
									},
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}, ContactsListAdapter.ListMode.EMERGENCY_CONTACTS);
		recyclerViewContacts.setAdapter(contactsListAdapter);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

					case R.id.textViewEdit:
						if(ContactsListAdapter.ListMode.EMERGENCY_CONTACTS == contactsListAdapter.getListMode()) {
							contactsListAdapter.setListMode(ContactsListAdapter.ListMode.DELETE_CONTACTS);
							contactsListAdapter.setCountAndNotify();
							textViewEdit.setText(activity.getResources().getString(R.string.done));
						}
						else if(ContactsListAdapter.ListMode.DELETE_CONTACTS == contactsListAdapter.getListMode()) {
							contactsListAdapter.setListMode(ContactsListAdapter.ListMode.EMERGENCY_CONTACTS);
							contactsListAdapter.setCountAndNotify();
							textViewEdit.setText(activity.getResources().getString(R.string.edit));
						}
						break;

					case R.id.buttonAddContact:
						mPermissionCommon.getPermission(REQUEST_CODE_CONTACT, Manifest.permission.READ_CONTACTS);
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewEdit.setOnClickListener(onClickListener);
		textViewEdit.setText(activity.getResources().getString(R.string.edit));
		buttonAddContact.setOnClickListener(onClickListener);


		setEmergencyContactsToList();


		return rootView;
	}

	private void openAddEmergencyContactsFragments() {
		new FragTransUtils().openAddEmergencyContactsFragment(activity,
				((EmergencyActivity)activity).getContainer());
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mPermissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
	}

	private void performBackPressed() {
		if(activity instanceof EmergencyActivity){
			((EmergencyActivity)activity).performBackPressed();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rootView);
        System.gc();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			getAllEmergencyContacts(activity);
			if(ContactsListAdapter.ListMode.DELETE_CONTACTS == contactsListAdapter.getListMode()) {
				textViewEdit.performClick();
			}
		}
	}

	public void getAllEmergencyContacts(final Activity activity) {
		new ApiEmergencyContactsList(activity, new ApiEmergencyContactsList.Callback() {
			@Override
			public void onSuccess() {
				setEmergencyContactsToList();
			}

			@Override
			public void onFailure() {

			}

			@Override
			public void onRetry(View view) {

			}

			@Override
			public void onNoRetry(View view) {

			}
		}).emergencyContactsList();
	}

	private void setEmergencyContactsToList(){
		if(Data.userData.getEmergencyContactsList() != null) {
			contactBeans.clear();
			for (EmergencyContact emergencyContact : Data.userData.getEmergencyContactsList()) {
				ContactBean contactBean = new ContactBean(emergencyContact.name, emergencyContact.phoneNo,emergencyContact.countryCode, "",
						ContactBean.ContactBeanViewType.CONTACT, null, null);
				contactBean.setId(emergencyContact.id);
				contactBeans.add(contactBean);
			}
			notifyListAndShowAddContactsButton();
			EmergencyActivity.setEmergencyContactsAllowedToAdd();

			if(contactBeans.size() > 0) {
				textViewEdit.setVisibility(View.VISIBLE);
				linearLayoutContactsList.setVisibility(View.VISIBLE);
				linearLayoutNoContacts.setVisibility(View.GONE);
			} else{
				textViewEdit.setVisibility(View.GONE);
				linearLayoutContactsList.setVisibility(View.GONE);
				linearLayoutNoContacts.setVisibility(View.VISIBLE);
			}
		}
	}

	private void notifyListAndShowAddContactsButton(){
		contactsListAdapter.notifyDataSetChanged();
		if(contactBeans.size() < 5){
			buttonAddContact.setVisibility(View.VISIBLE);
		} else{
			buttonAddContact.setVisibility(View.GONE);
		}
	}



	public void deleteEmergencyContactAPI(final Activity activity, int id) {
		try {
			if(MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();

				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ID, String.valueOf(id));

				Log.i("params", "=" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().emergencyContactsDelete(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "emergencyContactsDelete response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
								if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
									DialogPopup.dialogBanner(activity, message);
								} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									DialogPopup.dialogBanner(activity, message);
									getAllEmergencyContacts(activity);
								} else {
									DialogPopup.dialogBanner(activity, message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
				});
			}
			else {
				DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
