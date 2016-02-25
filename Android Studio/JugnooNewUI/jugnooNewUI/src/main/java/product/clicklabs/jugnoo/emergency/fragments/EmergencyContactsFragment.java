package product.clicklabs.jugnoo.emergency.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.FragTransUtils;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutLayoutManagerResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
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
public class EmergencyContactsFragment extends Fragment {

	private final String TAG = EmergencyContactsFragment.class.getSimpleName();
	private RelativeLayout relative;

	private TextView textViewTitle, textViewEdit;
	private ImageView imageViewBack;

	private ScrollView scrollView;
	private RecyclerView recyclerViewContacts;
	private Button buttonAddContact;
	private ContactsListAdapter contactsListAdapter;
	private ArrayList<ContactBean> contactBeans;

	private View rootView;
    private FragmentActivity activity;

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Config.getFlurryKey());
		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(EmergencyContactsFragment.class.getSimpleName() + " started");
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
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);


        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewEdit = (TextView) rootView.findViewById(R.id.textViewEdit); textViewEdit.setTypeface(Fonts.mavenRegular(activity));

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

		((TextView)rootView.findViewById(R.id.textViewContacts)).setTypeface(Fonts.mavenLight(activity));

		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutLayoutManagerResizableRecyclerView(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		buttonAddContact = (Button) rootView.findViewById(R.id.buttonAddContact);
		buttonAddContact.setTypeface(Fonts.mavenRegular(activity));

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {

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
							buttonAddContact.setText(activity.getResources().getString(R.string.done));
						}
						else{
							contactsListAdapter.setListMode(ContactsListAdapter.ListMode.EMERGENCY_CONTACTS);
							contactsListAdapter.setCountAndNotify();
							buttonAddContact.setText(activity.getResources().getString(R.string.edit));
						}
						break;

					case R.id.buttonAddContact:
						new FragTransUtils().openAddEmergencyContactsFragment(activity,
								((EmergencyActivity)activity).getContainer());
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewEdit.setOnClickListener(onClickListener);
		textViewEdit.setText(activity.getResources().getString(R.string.edit));
		buttonAddContact.setOnClickListener(onClickListener);


		getAllEmergencyContacts(activity);


		return rootView;
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



	public void getAllEmergencyContacts(final Activity activity) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			Log.i("params", "=" + params.toString());

			RestClient.getApiServices().emergencyContactsList(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "emergencyContactsList response = " + responseStr);
					DialogPopup.dismissLoadingDialog();
					try {
						JSONObject jObj = new JSONObject(responseStr);
						String message = JSONParser.getServerMessage(jObj);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.EMERGENCY_CONTACTS.getOrdinal() == flag) {
								if (Data.emergencyContactsList == null) {
									Data.emergencyContactsList = new ArrayList<>();
								}
								Data.emergencyContactsList.clear();
								Data.emergencyContactsList.addAll(JSONParser.parseEmergencyContacts(jObj));
								setEmergencyContactsToList();
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "emergencyContactsList error"+error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	private void setEmergencyContactsToList(){
		if(Data.emergencyContactsList != null){
			contactBeans.clear();
			for(EmergencyContact emergencyContact : Data.emergencyContactsList){
				contactBeans.add(new ContactBean(emergencyContact.name, emergencyContact.phoneNo, ""));
			}
			contactsListAdapter.notifyDataSetChanged();
		}
	}

}
