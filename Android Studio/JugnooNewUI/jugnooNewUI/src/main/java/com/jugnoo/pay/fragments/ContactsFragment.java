package com.jugnoo.pay.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jugnoo.pay.activities.SelectContactActivity;
import com.jugnoo.pay.activities.SendMoneyActivity;
import com.jugnoo.pay.adapters.ContactsListAdapter;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 05/12/16.
 */

public class ContactsFragment extends Fragment implements RecyclerViewClickListener {

    private View rootView;
    private RecyclerView contactsRecycler;
    private ArrayList<SelectUser> selectUsers;
    private ContactsListAdapter adapter;
    private Cursor phones;
    private ContentResolver resolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsRecycler = (RecyclerView) rootView.findViewById(R.id.contacts_recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        contactsRecycler.setLayoutManager(mLayoutManager);
        contactsRecycler.setItemAnimator(new DefaultItemAnimator());

        selectUsers = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();


        if((Prefs.with(getActivity()).getString(SharedPreferencesName.USER_CONTACTS, "") != null)
                && (!Prefs.with(getActivity()).getString(SharedPreferencesName.USER_CONTACTS, "").equalsIgnoreCase(""))){
            Gson gson = new Gson();
            String json = Prefs.with(getActivity()).getString(SharedPreferencesName.USER_CONTACTS, "");
            Type type = new TypeToken<ArrayList<SelectUser>>() {}.getType();
            selectUsers = gson.fromJson(json, type);
            adapter = new ContactsListAdapter(getActivity(), selectUsers, this);
            contactsRecycler.setAdapter(adapter);
        } else {
            phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }



        ((SelectContactActivity)getActivity()).getIvToolbarRefreshContacts().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SelectContactActivity)getActivity()).getSearchET().setText("");

                fetchUserContacts();
            }
        });

        return rootView;
    }

    public ContactsListAdapter getAdapter() {
        return adapter;
    }

    private void fetchUserContacts(){
        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        SelectUser data = selectUsers.get(position);
        data.setAmount("");
        data.setOrderId("0");
        System.out.println("contact num=== "+ CommonMethods.extractNumber(data.getPhone()));
        String num = data.getPhone();
        num = num.replace(" ", "");
        num = num.replace("-", "");
        if(CommonMethods.extractNumber(num).length()>=10) {
            Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
            intent.putExtra(AppConstant.REQUEST_STATUS, ((SelectContactActivity)getActivity()).isRequestStatus());
            Bundle bun =new Bundle();
            bun.putParcelable(AppConstant.CONTACT_DATA, data);
            intent.putExtras( bun);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
            getActivity().finish();
        }
        else if(new Validator().validateEmail(((SelectContactActivity)getActivity()).getSearchET().getText().toString()))
        {
            SelectUser newData = new SelectUser();
            newData.setName(getString(R.string.vpa_address));
            newData.setPhone(((SelectContactActivity)getActivity()).getSearchET().getText().toString());
            newData.setAmount("");
            data.setOrderId("0");
            Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
            intent.putExtra(AppConstant.REQUEST_STATUS, ((SelectContactActivity)getActivity()).isRequestStatus());
            Bundle bun =new Bundle();
            bun.putParcelable(AppConstant.CONTACT_DATA, newData);
            intent.putExtras( bun);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
            getActivity().finish();
        }
        else {

            if (((SelectContactActivity)getActivity()).isRequestStatus())
                Utils.showToast(getActivity(), getString(R.string.please_enter_valid_phone));
            else
                Utils.showToast(getActivity(), getString(R.string.please_enter_valid_address_or_phone));
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int viewType) {

    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectUsers.clear();
            Prefs.with(getActivity()).save(SharedPreferencesName.USER_CONTACTS, "");
            CallProgressWheel.showLoadingDialog(getActivity(), getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            try {
                if (phones != null) {
					Log.e("count", "" + phones.getCount());
					if (phones.getCount() == 0) {
	//                    Toast.makeText(SelectContactActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
					} else {
						while (phones.moveToNext()) {
							Bitmap bit_thumb = null;
							String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
							String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
							String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
							/*try {
								if (image_thumb != null) {
									bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
								} else {
									Log.e("No Image Thumb", "--------------");
								}
							} catch (IOException e) {
								e.printStackTrace();
							}*/

							SelectUser selectUser = new SelectUser();
							selectUser.setThumb(image_thumb);
							selectUser.setName(name);
							selectUser.setPhone(phoneNumber.replace(" ", "").trim());
							selectUser.setEmail(id);
							selectUser.setCheckedBox(false);
							selectUsers.add(selectUser);
						}
						Set set = new TreeSet(new Comparator<SelectUser>() {
							@Override
							public int compare(SelectUser o1, SelectUser o2) {
								if(o1.getPhone().toString().equalsIgnoreCase(o2.getPhone().toString())){
									return 0;
								}
								return 1;
							}
						});

						set.addAll(selectUsers);
						selectUsers.clear();
						selectUsers = new ArrayList<SelectUser>(set);
						Gson gson = new Gson();
						String json = gson.toJson(selectUsers);
						Prefs.with(getActivity()).save(SharedPreferencesName.USER_CONTACTS, json);
					}
				} else {
					Log.e("Cursor close 1", "----------------");
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new ContactsListAdapter(getActivity(), selectUsers, ContactsFragment.this);
            contactsRecycler.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            CallProgressWheel.dismissLoadingDialog();

        }
    }
}
