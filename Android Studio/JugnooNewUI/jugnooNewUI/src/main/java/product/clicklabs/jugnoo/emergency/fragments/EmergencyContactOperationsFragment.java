package product.clicklabs.jugnoo.emergency.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.tokenautocomplete.FilteredArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * For
 *
 * Created by shankar on 2/22/16.
 */
public class EmergencyContactOperationsFragment extends Fragment {

	private RelativeLayout relative;

	private TextView textViewTitle, textViewSend;
	private ImageView imageViewBack;

	private LinearLayout linearLayoutMain;
	private TextView textViewScroll;

	private LinearLayout linearLayoutEmergencyContacts;
	private RecyclerView recyclerViewEmergencyContacts;

	private AutoCompleteTextView editTextPhoneContacts;
	private RecyclerView recyclerViewPhoneContacts;

	private ContactsListAdapter emergencyContactsListAdapter, phoneContactsListAdapter;
	private ArrayList<ContactBean> emergencyContactBeans, phoneContactBeans;
	private ArrayAdapter<ContactBean> phoneContactsArrayAdapter;

	private ContactsListAdapter.ListMode listMode;

	private View rootView;
    private FragmentActivity activity;


	public EmergencyContactOperationsFragment(ContactsListAdapter.ListMode listMode){
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

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewSend = (TextView) rootView.findViewById(R.id.textViewSend); textViewSend.setTypeface(Fonts.mavenRegular(activity));

		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

		linearLayoutEmergencyContacts = (LinearLayout) rootView.findViewById(R.id.linearLayoutEmergencyContacts);
		((TextView) rootView.findViewById(R.id.textViewEmergencyContacts)).setTypeface(Fonts.mavenLight(activity));
		recyclerViewEmergencyContacts = (RecyclerView) rootView.findViewById(R.id.recyclerViewEmergencyContacts);
		recyclerViewEmergencyContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewEmergencyContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewEmergencyContacts.setHasFixedSize(false);

		emergencyContactBeans = new ArrayList<>();
		emergencyContactsListAdapter = new ContactsListAdapter(emergencyContactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {

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
							setSelectedObject(true, p);
							editTextPhoneContacts.dismissDropDown();
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
						performBackPressed();
						break;

					case R.id.textViewSend:
						break;

					case R.id.linearLayoutMain:
						Utils.hideSoftKeyboard(activity, editTextPhoneContacts);
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewSend.setOnClickListener(onClickListener);
		linearLayoutMain.setOnClickListener(onClickListener);

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


	private void setEmergencyContactsToList(){
		if(Data.emergencyContactsList != null) {
			emergencyContactBeans.clear();
			for (EmergencyContact emergencyContact : Data.emergencyContactsList) {
				ContactBean contactBean = new ContactBean(emergencyContact.name, emergencyContact.phoneNo, "",
						ContactBean.ContactBeanViewType.CONTACT);
				contactBean.setId(emergencyContact.id);
				emergencyContactBeans.add(contactBean);
			}
			emergencyContactsListAdapter.notifyDataSetChanged();
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


}
