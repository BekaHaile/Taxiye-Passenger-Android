package product.clicklabs.jugnoo.emergency.fragments;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.emergency.EmergencyModeActivity;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;


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

	private EditText editTextContacts;
	private RecyclerView recyclerViewContacts;
	private ContactsListAdapter contactsListAdapter;
	private ArrayList<ContactBean> contactBeans;

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

		editTextContacts = (EditText) rootView.findViewById(R.id.editTextContacts);
		editTextContacts.setTypeface(Fonts.mavenLight(activity));

		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact);
		recyclerViewContacts.setAdapter(contactsListAdapter);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);

		new ContactsFetchAsync().execute();


		return rootView;
	}


	private void performBackPressed() {
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

			contactsListAdapter.notifyDataSetChanged();
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
								phone = phone.replace(" ","");
								phone = phone.replace("-", "");
								if (phone != null && (phone.length() >= 10)) {
									contactBeans.add(new ContactBean(name, phone, "", ""));
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

		public void loadList(ArrayList<ContactBean> list) {

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

	}


}
