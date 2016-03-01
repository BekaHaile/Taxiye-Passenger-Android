package product.clicklabs.jugnoo.emergency.fragments;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

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


/**
 * For
 *
 * Created by shankar on 2/22/16.
 */
public class EmergencyContactOperationsFragment extends Fragment {

	private RelativeLayout relative;

	private TextView textViewTitle, textViewSend;
	private ImageView imageViewBack;
	private RecyclerView recyclerViewContacts;
	private ContactsListAdapter contactsListAdapter;
	private ArrayList<ContactBean> contactBeans;
	private ArrayAdapter<ContactBean> contactsArrayAdapter;
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

		recyclerViewContacts = (RecyclerView)rootView.findViewById(R.id.recyclerViewContacts);
		recyclerViewContacts.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
		recyclerViewContacts.setHasFixedSize(false);

		contactBeans = new ArrayList<>();
		contactsListAdapter = new ContactsListAdapter(contactBeans, activity, R.layout.list_item_contact,
				new ContactsListAdapter.Callback() {
					@Override
					public void contactClicked(int position, ContactBean contactBean) {

					}
				}, listMode);
		recyclerViewContacts.setAdapter(contactsListAdapter);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

					case R.id.textViewSend:
						break;

				}
			}
		};


		imageViewBack.setOnClickListener(onClickListener);
		textViewSend.setOnClickListener(onClickListener);

		setEmergencyContactsToList();

		new ContactsFetchAsync(activity, contactBeans, new ContactsFetchAsync.Callback() {
			@Override
			public void onPreExecute() {
				contactBeans.add(new ContactBean("", "", "", ContactBean.ContactBeanViewType.PHONE_CONTACTS));
			}

			@Override
			public void onPostExecute(ArrayList<ContactBean> contactBeans) {
				contactsListAdapter.notifyDataSetChanged();
			}
		}).execute();


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
			contactBeans.clear();
			contactBeans.add(new ContactBean("", "", "", ContactBean.ContactBeanViewType.EMERGENCY_CONTACTS));
			for (EmergencyContact emergencyContact : Data.emergencyContactsList) {
				ContactBean contactBean = new ContactBean(emergencyContact.name, emergencyContact.phoneNo, "",
						ContactBean.ContactBeanViewType.CONTACT);
				contactBean.setId(emergencyContact.id);
				contactBeans.add(contactBean);
			}
			contactsListAdapter.notifyDataSetChanged();
		}
	}


}
