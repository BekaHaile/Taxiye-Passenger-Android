package product.clicklabs.jugnoo.emergency.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.emergency.EmergencyModeActivity;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.ContactsCompletionView;


/**
 * For adding contacts to emergency contacts
 *
 * Created by shankar on 2/22/16.
 */
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
		FlurryAgent.init(activity, Config.getFlurryKey());
		FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(AddEmergencyContactsFragment.class.getSimpleName() + " started");
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
        rootView = inflater.inflate(R.layout.fragment_add_emergency_contacts, container, false);


        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			new ASSL(activity, relative, 1134, 720, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewAdd = (TextView) rootView.findViewById(R.id.textViewAdd); textViewAdd.setTypeface(Fonts.mavenRegular(activity));

		((TextView)rootView.findViewById(R.id.textViewAddContacts)).setTypeface(Fonts.mavenLight(activity));

		editTextContacts = (ContactsCompletionView) rootView.findViewById(R.id.editTextContacts);
		editTextContacts.setTypeface(Fonts.mavenLight(activity));

		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactSelected(boolean selected, ContactBean contactBean) {
						if(selected){
							editTextContacts.addObject(contactBean);
						} else{
							editTextContacts.removeObject(contactBean);
						}
					}
				});
		recyclerViewContacts.setAdapter(contactsListAdapter);

		contactsArrayAdapter = new FilteredArrayAdapter<ContactBean>(this.getContext(), R.layout.list_item_contact,
				((List<ContactBean>)contactBeans)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					convertView = l.inflate(R.layout.list_item_contact, parent, false);
				}

				ContactBean p = getItem(position);
				((TextView)convertView.findViewById(R.id.textViewContactName)).setText(p.getName());
				((TextView)convertView.findViewById(R.id.textViewContactNumberType)).setText(p.getPhoneNo()+" "+p.getType());
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
		editTextContacts.setTokenLimit(Constants.MAX_EMERGENCY_CONTACTS_ALLOWED);
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
						for(ContactBean contactBean : contactBeans){
							if(contactBean.isSelected()){
								Log.i(TAG, "contact selected="+contactBean);
							}
						}
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewAdd.setOnClickListener(onClickListener);

		new ContactsFetchAsync().execute();


		return rootView;
	}


	private void performBackPressed() {
		Utils.hideSoftKeyboard(activity, editTextContacts);
		if(activity instanceof EmergencyModeActivity){
			((EmergencyModeActivity)activity).performBackPressed();
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
					contactBean.getPhoneNo(), contactBean.getType()))).setSelected(selected);

			contactsListAdapter.setCountAndNotify();
		} catch(Exception e){
			e.printStackTrace();
		}
	}



	class ContactsFetchAsync extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
		}

		@Override
		protected String doInBackground(String... params) {
			fetchContacts();
			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			contactsListAdapter.setCountAndNotify();
			contactsArrayAdapter.notifyDataSetChanged();
			DialogPopup.dismissLoadingDialog();
		}

		private void fetchContacts() {
			ArrayList<ContactBean> contactBeans = new ArrayList<>();
			try {

				ContentResolver cr = activity.getContentResolver();
				Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

				if (cur.getCount() > 0) {
					while (cur.moveToNext()) {
						String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
						String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						String hasPhoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						if (Integer.parseInt(hasPhoneNumber) > 0) {
							Cursor pCur = cr.query(
									ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
									new String[]{id}, null);

							while (pCur.moveToNext()) {
								String phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String type = getContactTypeString(pCur.getString(
										pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));

								phone = phone.replace(" ","");
								phone = phone.replace("-", "");
								if (phone != null && (phone.length() >= 10)) {
									contactBeans.add(new ContactBean(name, phone, type));
								}
							}
							pCur.close();
						}
					}
				}
				cur.close();

				loadList(contactBeans);


				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		private void loadList(ArrayList<ContactBean> list) {

			Set set = new TreeSet(new Comparator<ContactBean>() {
				@Override
				public int compare(ContactBean o1, ContactBean o2) {
					if (o1.getPhoneNo().toString().equalsIgnoreCase(o2.getPhoneNo().toString())) {
						return 0;
					}
					return 1;
				}
			});

			set.addAll(list);

			final ArrayList<ContactBean> newList = new ArrayList<ContactBean>(set);
			contactBeans.clear();
			contactBeans.addAll(newList);

		}


		private String getContactTypeString(String type){
			try {
				int typeInt = Integer.parseInt(type);
				if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME){
					return "Home";
				} else if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
					return "Mobile";
				} else if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK){
					return "Work";
				} else{
					return "Other";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "Mobile";
			}
		}

	}


}
