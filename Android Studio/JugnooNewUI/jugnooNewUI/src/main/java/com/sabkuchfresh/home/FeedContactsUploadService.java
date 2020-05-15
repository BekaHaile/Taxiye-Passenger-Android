package com.sabkuchfresh.home;

/*import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ContactBean;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

*//**
 * Created by Shankar on 3/14/15.
 *//*
public class FeedContactsUploadService extends IntentService {

	private static final String TAG = FeedContactsUploadService.class.getSimpleName();
	private String accessToken = "";

	public FeedContactsUploadService() {
		this("FeedContactsUploadService");
	}

	public FeedContactsUploadService(String name) {
		super(name);
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			if (intent.hasExtra(Constants.KEY_ACCESS_TOKEN)) {
				accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
				Log.v("intent values are ", "--> " + accessToken);
				newQueueUpSyncs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void newQueueUpSyncs() {
		try {
			ArrayList<ContactBean> contactList = new ArrayList<>();
			ContentResolver cr = getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			if (cur != null && cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String email = "";
					if (Integer.parseInt(cur.getString(
							cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr.query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
								new String[]{id}, null);

						if (pCur != null) {
							while (pCur.moveToNext()) {
								ContactBean contactBean = new ContactBean();
								String phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								phone = phone.replace(" ", "");
								phone = phone.replace("-", "");

								contactBean.setName(name);
								contactBean.setPhone(phone);
								contactBean.setEmail(email);
								if (phone != null && Utils.validPhoneNumber(phone)) {
									contactList.add(contactBean);
								}
							}
							pCur.close();
						}
					}
				}
			}
			if (cur != null) {
				cur.close();
			}
			Log.v("size is ", "---> " + contactList.size());

			removeDuplicateFromList(contactList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void removeDuplicateFromList(ArrayList<ContactBean> list) {
		Set set = new TreeSet(new Comparator<ContactBean>() {
			@Override
			public int compare(ContactBean o1, ContactBean o2) {
				if (o1.getPhone().equalsIgnoreCase(o2.getPhone())) {
					return 0;
				}
				return 1;
			}
		});
		set.addAll(list);
		final ArrayList<ContactBean> newList = new ArrayList<ContactBean>(set);
		Log.v("newList size", "--->" + newList.size());

		try {
			JSONArray jsonArray = new JSONArray();

			for (int i = 0; i < newList.size(); i++) {
				JSONObject json = new JSONObject();
				json.put("n", newList.get(i).getName());
				json.put("p", newList.get(i).getPhone());
				jsonArray.put(json);
			}

			// Call Api
			uploadContactsApi(jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadContactsApi(String allContacts) {
		HashMap<String, String> params = new HashMap<>();
		boolean uploaded = false;
		if (MyApplication.getInstance().isOnline()) {

			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			try {
				if (allContacts != null) {
					params.put("contacts", allContacts);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.i("params request_dup_registration", "=" + params);

			try {
				new HomeUtil().putDefaultParams(params);
				Response response = RestClient.getFeedApiService().syncContacts(params);
				String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
				Log.i(TAG, "referAllContactsSync response = " + responseStr);

				try {
					JSONObject jObj = new JSONObject(responseStr);
					int flag = jObj.getInt("flag");
					String message = JSONParser.getServerMessage(jObj);
					if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
						// contacts uploaded
						uploaded = true;
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		doneWithSync(uploaded);
	}

	private void doneWithSync(boolean uploaded) {
		Intent intent = new Intent(Constants.ACTION_CONTACTS_UPLOADED);
		intent.putExtra(Constants.KEY_UPLOADED, uploaded);
		sendBroadcast(intent);
		stopSelf();
	}
}*/
