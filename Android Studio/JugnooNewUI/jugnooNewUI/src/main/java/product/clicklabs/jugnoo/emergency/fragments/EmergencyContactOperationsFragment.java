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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.tokenautocomplete.FilteredArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiEmergencyContactsList;
import product.clicklabs.jugnoo.apis.ApiEmergencySendRideStatus;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.FragTransUtils;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * For
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
public class EmergencyContactOperationsFragment extends Fragment {

	private RelativeLayout relative;

	private TextView textViewTitle, textViewSend;
	private ImageView imageViewBack;

	private LinearLayout linearLayoutMain;
	private TextView textViewScroll;

	private LinearLayout linearLayoutEmergencyContacts;
	private RecyclerView recyclerViewEmergencyContacts;
	private ImageView imageViewEmergencyContactsSep;
	private RelativeLayout relativeLayoutOr;
	private TextView textViewOr;
	private Button buttonAddContact;

	private AutoCompleteTextView editTextPhoneContacts;
	private RecyclerView recyclerViewPhoneContacts;

	private ContactsListAdapter emergencyContactsListAdapter, phoneContactsListAdapter;
	private ArrayList<ContactBean> emergencyContactBeans, phoneContactBeans;
	private ArrayAdapter<ContactBean> phoneContactsArrayAdapter;

	private String engagementId;
	private ContactsListAdapter.ListMode listMode;

	private View rootView;
    private FragmentActivity activity;


	public EmergencyContactOperationsFragment(String engagementId, ContactsListAdapter.ListMode listMode){
		this.engagementId = engagementId;
		this.listMode = listMode;
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Config.getFlurryKey());
		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(EmergencyContactOperationsFragment.class.getSimpleName() + " started");
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
	public void onPause() {
		super.onPause();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_emergency_contacts_operations, container, false);


        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewSend = (TextView) rootView.findViewById(R.id.textViewSend); textViewSend.setTypeface(Fonts.mavenRegular(activity));

		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

		linearLayoutEmergencyContacts = (LinearLayout) rootView.findViewById(R.id.linearLayoutEmergencyContacts);
		((TextView) rootView.findViewById(R.id.textViewEmergencyContacts)).setTypeface(Fonts.mavenLight(activity));
		imageViewEmergencyContactsSep = (ImageView) rootView.findViewById(R.id.imageViewEmergencyContactsSep);
		relativeLayoutOr = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOr);
		textViewOr = (TextView) rootView.findViewById(R.id.textViewOr); textViewOr.setTypeface(Fonts.mavenLight(activity));
		buttonAddContact = (Button) rootView.findViewById(R.id.buttonAddContact); buttonAddContact.setTypeface(Fonts.mavenRegular(activity));
		buttonAddContact.setVisibility(View.GONE);
		relativeLayoutOr.setVisibility(View.GONE);

		recyclerViewEmergencyContacts = (RecyclerView) rootView.findViewById(R.id.recyclerViewEmergencyContacts);
		recyclerViewEmergencyContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewEmergencyContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewEmergencyContacts.setHasFixedSize(false);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));

		emergencyContactBeans = new ArrayList<>();
		emergencyContactsListAdapter = new ContactsListAdapter(emergencyContactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {
						contactCalledAccToListMode(contactBean);
					}
				}, listMode);
		recyclerViewEmergencyContacts.setAdapter(emergencyContactsListAdapter);



		((TextView)rootView.findViewById(R.id.textViewPhoneContacts)).setTypeface(Fonts.mavenLight(activity));
		editTextPhoneContacts = (AutoCompleteTextView) rootView.findViewById(R.id.editTextPhoneContacts);
		editTextPhoneContacts.setTypeface(Fonts.mavenLight(activity));
		recyclerViewPhoneContacts = (RecyclerView) rootView.findViewById(R.id.recyclerViewPhoneContacts);
		recyclerViewPhoneContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewPhoneContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewPhoneContacts.setHasFixedSize(false);

		phoneContactBeans = new ArrayList<>();
		phoneContactsListAdapter = new ContactsListAdapter(phoneContactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {
						contactCalledAccToListMode(contactBean);
					}
				}, listMode);
		recyclerViewPhoneContacts.setAdapter(phoneContactsListAdapter);

		phoneContactsArrayAdapter = new FilteredArrayAdapter<ContactBean>(this.getContext(), R.layout.list_item_contact,
				((List<ContactBean>)phoneContactBeans)) {
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

				convertView.findViewById(R.id.relative).setTag(position);
				convertView.findViewById(R.id.relative).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							int position = (int) v.getTag();
							ContactBean p = getItem(position);
							editTextPhoneContacts.dismissDropDown();
							if(!contactCalledAccToListMode(p)) {
								setSelectedObject(true, p);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});


				return convertView;
			}

			@Override
			protected boolean keepObject(ContactBean person, String mask) {
				mask = mask.toLowerCase();
				boolean matched = person.getName().toLowerCase().startsWith(mask)
						|| person.getPhoneNo().toLowerCase().startsWith(mask);
				return matched;
			}

		};
		editTextPhoneContacts.setAdapter(phoneContactsArrayAdapter);



		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						if(ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode) {
							FlurryEventLogger.eventGA(Constants.HELP, "send ride status screen", "close");
						}else{
							FlurryEventLogger.eventGA(Constants.HELP, "call your contacts", "close");
						}
						performBackPressed();
						break;

					case R.id.textViewSend:
						if(ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode) {
							FlurryEventLogger.eventGA(Constants.HELP, "send ride status screen", "send");
							clickOnSend();
						}
						break;

					case R.id.linearLayoutMain:
						Utils.hideSoftKeyboard(activity, editTextPhoneContacts);
						break;

					case R.id.buttonAddContact:
						new FragTransUtils().openAddEmergencyContactsFragment(activity,
								((EmergencyActivity)activity).getContainer());
						break;
				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewSend.setOnClickListener(onClickListener);
		linearLayoutMain.setOnClickListener(onClickListener);
		buttonAddContact.setOnClickListener(onClickListener);

		setEmergencyContactsToList();

		new ContactsFetchAsync(activity, phoneContactBeans, new ContactsFetchAsync.Callback() {
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute(ArrayList<ContactBean> contactBeans) {
				phoneContactsListAdapter.notifyDataSetChanged();
			}
		}).execute();


		KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
				new KeyboardLayoutListener.KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {
				linearLayoutEmergencyContacts.setVisibility(View.GONE);
			}

			@Override
			public void keyBoardClosed() {
				linearLayoutEmergencyContacts.setVisibility(View.VISIBLE);
			}
		});
		keyboardLayoutListener.setResizeTextView(false);
		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);


		if(ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode){
			textViewSend.setVisibility(View.VISIBLE);
			textViewTitle.setText(activity.getResources().getString(R.string.send_ride_status));
		} else if(ContactsListAdapter.ListMode.CALL_CONTACTS == listMode){
			textViewSend.setVisibility(View.GONE);
			textViewTitle.setText(activity.getResources().getString(R.string.call_your_contacts));
		}

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

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			getAllEmergencyContacts();
		}
	}


	private void setEmergencyContactsToList(){
		if(Data.userData.getEmergencyContactsList() != null) {
			emergencyContactBeans.clear();
			for (EmergencyContact emergencyContact : Data.userData.getEmergencyContactsList()) {
				ContactBean contactBean = new ContactBean(emergencyContact.name, emergencyContact.phoneNo, "",
						ContactBean.ContactBeanViewType.CONTACT);
				contactBean.setId(emergencyContact.id);
				emergencyContactBeans.add(contactBean);
			}
			emergencyContactsListAdapter.notifyDataSetChanged();
			if(emergencyContactBeans.size() > 0){
				imageViewEmergencyContactsSep.setVisibility(View.VISIBLE);
				relativeLayoutOr.setVisibility(View.GONE);
				buttonAddContact.setVisibility(View.GONE);
			} else{
				imageViewEmergencyContactsSep.setVisibility(View.GONE);
				relativeLayoutOr.setVisibility(View.VISIBLE);
				buttonAddContact.setVisibility(View.VISIBLE);
				if(listMode == ContactsListAdapter.ListMode.SEND_RIDE_STATUS){
					textViewOr.setText(activity.getResources().getString(R.string.or_send_directly));
				} else if(listMode == ContactsListAdapter.ListMode.CALL_CONTACTS){
					textViewOr.setText(activity.getResources().getString(R.string.or_call_directly));
				}
			}
		} else{
			imageViewEmergencyContactsSep.setVisibility(View.GONE);
			relativeLayoutOr.setVisibility(View.VISIBLE);
			buttonAddContact.setVisibility(View.VISIBLE);
			if(listMode == ContactsListAdapter.ListMode.SEND_RIDE_STATUS){
				textViewOr.setText(activity.getResources().getString(R.string.or_send_directly));
			} else if(listMode == ContactsListAdapter.ListMode.CALL_CONTACTS){
				textViewOr.setText(activity.getResources().getString(R.string.or_call_directly));
			}
		}
	}

	private void setSelectedObject(boolean selected, ContactBean contactBean){
		try{
			int index = phoneContactBeans.indexOf(new ContactBean(contactBean.getName(),
					contactBean.getPhoneNo(), contactBean.getType(), ContactBean.ContactBeanViewType.CONTACT));
			phoneContactBeans.get(index).setSelected(selected);
			phoneContactsListAdapter.notifyDataSetChanged();
			((LinearLayoutManager)recyclerViewPhoneContacts.getLayoutManager()).scrollToPositionWithOffset(index, 20);
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private void getAllEmergencyContacts() {
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
				getAllEmergencyContacts();
			}

			@Override
			public void onNoRetry(View view) {

			}
		}).emergencyContactsList();
	}

	private void clickOnSend(){
		final ArrayList<String> contacts = new ArrayList<>();
		for(ContactBean contactBean : emergencyContactBeans){
			if(contactBean.isSelected()){
				contacts.add(contactBean.getPhoneNo());
			}
		}
		for(ContactBean contactBean : phoneContactBeans){
			if(contactBean.isSelected()){
				contacts.add(contactBean.getPhoneNo());
			}
		}

		if(contacts.size() == 0){
			DialogPopup.alertPopup(activity, "",
					activity.getResources().getString(R.string.send_ride_status_no_contacts_message));
		} else if(contacts.size() > 10){
			DialogPopup.alertPopupTwoButtonsWithListeners(activity,
					"",
					String.format(activity.getResources()
									.getString(R.string.send_ride_status_more_contacts_message_format),
							"" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS,
							"" + EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS),
					activity.getResources().getString(R.string.ok),
					activity.getResources().getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							sendRideStatusApi(engagementId, contacts);
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}, true, false);
		} else{
			sendRideStatusApi(engagementId, contacts);
		}
	}

	private void sendRideStatusApi(final String engagementId, final ArrayList<String> contacts){
		new ApiEmergencySendRideStatus(activity, new ApiEmergencySendRideStatus.Callback() {
			@Override
			public void onSuccess(String message) {
				DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						performBackPressed();
					}
				});
			}

			@Override
			public void onFailure() {

			}

			@Override
			public void onRetry(View view) {
				sendRideStatusApi(engagementId, contacts);
			}

			@Override
			public void onNoRetry(View view) {

			}
		}).emergencySendRideStatusMessage(engagementId, contacts);
	}


	private boolean contactCalledAccToListMode(ContactBean contactBean){
		if(ContactsListAdapter.ListMode.CALL_CONTACTS == listMode){
			Utils.openCallIntent(activity, contactBean.getPhoneNo());
			return true;
		} else{
			return false;
		}
	}





}
