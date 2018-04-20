package product.clicklabs.jugnoo.emergency.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.ContactsCompletionView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * For adding contacts to emergency contacts
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
public class AddEmergencyContactsFragment extends Fragment {

	private final String TAG = AddEmergencyContactsFragment.class.getSimpleName();
	private RelativeLayout relative;

	private TextView textViewTitle, textViewAdd;
	private ImageView imageViewBack;

	private ContactsCompletionView editTextContacts;
	private RecyclerView recyclerViewContacts;
	private ContactsListAdapter contactsListAdapter;
	private ArrayList<ContactBean> contactBeans;
	private ArrayAdapter<ContactBean> contactsArrayAdapter;

	private View rootView;
    private FragmentActivity activity;

	@Override
	public void onStart() {
		super.onStart();
//		FlurryAgent.init(activity, Config.getFlurryKey());
//		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//		FlurryAgent.onEvent(AddEmergencyContactsFragment.class.getSimpleName() + " started");
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
        rootView = inflater.inflate(R.layout.fragment_add_emergency_contacts, container, false);


        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewAdd = (TextView) rootView.findViewById(R.id.textViewAdd); textViewAdd.setTypeface(Fonts.mavenRegular(activity));

		((TextView)rootView.findViewById(R.id.textViewAddContacts)).setTypeface(Fonts.mavenMedium(activity));

		editTextContacts = (ContactsCompletionView) rootView.findViewById(R.id.editTextContacts);
		editTextContacts.setTypeface(Fonts.mavenLight(activity));

		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {
						if(contactBean.isSelected()){
							editTextContacts.addObject(contactBean);
						} else{
							editTextContacts.removeObject(contactBean);
						}
					}
				}, ContactsListAdapter.ListMode.ADD_CONTACTS);
		recyclerViewContacts.setAdapter(contactsListAdapter);

		contactsArrayAdapter = new FilteredArrayAdapter<ContactBean>(this.getContext(), R.layout.list_item_contact,
				((List<ContactBean>)contactBeans)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					convertView = l.inflate(R.layout.list_item_contact, parent, false);
					ListView.LayoutParams layoutParams = new ListView.LayoutParams(640, 128);
					convertView.setLayoutParams(layoutParams);

					ASSL.DoMagic(convertView);
				}


				ContactBean p = getItem(position);
				((TextView)convertView.findViewById(R.id.textViewContactName)).setTypeface(Fonts.mavenLight(activity));
				((TextView)convertView.findViewById(R.id.textViewContactNumberType)).setTypeface(Fonts.mavenLight(activity));
				((TextView)convertView.findViewById(R.id.textViewContactName)).setText(p.getName());
				((TextView)convertView.findViewById(R.id.textViewContactNumberType)).setText(p.getPhoneNo() + " " + p.getType());
				convertView.findViewById(R.id.imageViewOption).setVisibility(View.GONE);

				return convertView;
			}

			@Override
			protected boolean keepObject(ContactBean person, String mask) {
				mask = mask.toLowerCase();
				return person.getName().toLowerCase().startsWith(mask)
						|| person.getPhoneNo().toLowerCase().startsWith(mask);
			}
		};

		editTextContacts.setAdapter(contactsArrayAdapter);
		editTextContacts.allowDuplicates(false);
		editTextContacts.setTokenLimit(EmergencyActivity.EMERGENCY_CONTACTS_ALLOWED_TO_ADD);
		editTextContacts.setTokenListener(new TokenCompleteTextView.TokenListener<ContactBean>() {
			@Override
			public void onTokenAdded(ContactBean token) {
				setSelectedObject(true, token);
			}

			@Override
			public void onTokenRemoved(ContactBean token) {
				setSelectedObject(false, token);
			}
		});


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:

						performBackPressed();
						break;

					case R.id.textViewAdd:
						try {
							JSONArray jsonArray = new JSONArray();
							for(ContactBean contactBean : contactBeans){
								if(contactBean.isSelected()){
									JSONObject jsonObject = new JSONObject();
									jsonObject.put(Constants.KEY_NAME, contactBean.getName());
									jsonObject.put(Constants.KEY_PHONE_NO, contactBean.getPhoneNo());
									jsonArray.put(jsonObject);
								}
							}
							if(jsonArray.length() > 0) {
								addEmergencyContactsAPI(activity, jsonArray.toString());
							} else{
								Utils.showToast(activity, activity.getResources().getString(R.string.please_select_some_contacts_first));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewAdd.setOnClickListener(onClickListener);

		new ContactsFetchAsync(activity, contactBeans, new ContactsFetchAsync.Callback() {
			@Override
			public void onPreExecute() {

			}

			@Override
			public void onPostExecute(ArrayList<ContactBean> contactBeans) {
				contactsListAdapter.setCountAndNotify();
				contactsArrayAdapter.notifyDataSetChanged();
			}
		}).execute();

		return rootView;
	}


	private void performBackPressed() {
		Utils.hideSoftKeyboard(activity, editTextContacts);
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

	private void setSelectedObject(boolean selected, ContactBean contactBean){
		try{
			contactBeans.get(contactBeans.indexOf(new ContactBean(contactBean.getName(),
					contactBean.getPhoneNo(), contactBean.getType(), ContactBean.ContactBeanViewType.CONTACT))).setSelected(selected);
			contactsListAdapter.setCountAndNotify();
		} catch(Exception e){
			e.printStackTrace();
		}
	}





	public void addEmergencyContactsAPI(final Activity activity, String jsonArray) {
		try {
			if(MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_EMERGENCY_CONTACTS, jsonArray);

				Log.e("params", "=" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().emergencyContactsAddMultiple(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "response = " + responseStr);
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
									performBackPressed();
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
