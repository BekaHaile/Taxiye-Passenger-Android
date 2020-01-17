package product.clicklabs.jugnoo.emergency;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.models.ContactBean;

/**
 * Created by shankar on 2/26/16.
 */
public class ContactsFetchAsync extends AsyncTask<String, Integer, String> {

	private Context activity;
	private Callback callback;
	private ArrayList<ContactBean> contactBeans;
	private boolean stopInterrupt = false;
	private ProgressDialog progressDialog;

	public ContactsFetchAsync(Context activity, ArrayList<ContactBean> contactBeans, Callback callback){
		this.activity = activity;
		this.contactBeans = contactBeans;
		this.callback = callback;
		this.stopInterrupt = false;
		progressDialog = new ProgressDialog(activity, R.style.MyProgressDialog);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showLoading();
		callback.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		fetchContacts();
		return "";
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		progressDialog.dismiss();
		if(!stopInterrupt) {
			callback.onPostExecute(contactBeans);
		}
	}

	private void fetchContacts() {
		ArrayList<ContactBean> contactBeans = new ArrayList<>();
		try {

			ContentResolver cr = activity.getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			if (cur.getCount() > 0) {
				progressDialog.setMax(cur.getCount());
				while (cur.moveToNext() && !stopInterrupt) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					String imageUri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
					Uri uri = null;

					if (imageUri != null) {
						try {
							uri = Uri.parse(imageUri);
						} catch (Exception e) { }
					}

					if (Integer.parseInt(hasPhoneNumber) > 0) {
						Cursor pCur = cr.query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
								new String[]{id}, null);

						while (pCur.moveToNext() && !stopInterrupt) {
							String phone = pCur
									.getString(pCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String type = getContactTypeString(activity, pCur.getString(
									pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));

							phone = phone.replace(" ","");
							phone = phone.replace("-", "");
							if (Utils.validPhoneNumber(phone)) {
								contactBeans.add(new ContactBean(name, phone,"", type, ContactBean.ContactBeanViewType.CONTACT, uri, null));
							}
						}
						pCur.close();
					}
					progressDialog.incrementProgressBy(1);
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
		if(!stopInterrupt) {
			Set set = new TreeSet(new Comparator<ContactBean>() {
				@Override
				public int compare(ContactBean o1, ContactBean o2) {
					if (o1.getPhoneNo().equalsIgnoreCase(o2.getPhoneNo())) {
						return 0;
					}
					return 1;
				}
			});

			set.addAll(list);

			final ArrayList<ContactBean> newList = new ArrayList<ContactBean>(set);
			Collections.sort(newList, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
			contactBeans.addAll(newList);
		}
	}


	private String getContactTypeString(Context context, String type){
		try {
			int typeInt = Integer.parseInt(type);
			if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME){
				return context.getString(R.string.home);
			} else if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
				return context.getString(R.string.mobile);
			} else if(typeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK){
				return context.getString(R.string.work);
			} else{
				return context.getString(R.string.other);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return context.getString(R.string.mobile);
		}
	}

	public void stop(){
		stopInterrupt = true;
		this.cancel(true);
		callback.onCancel();
	}


	public interface Callback{
		void onPreExecute();
		void onPostExecute(ArrayList<ContactBean> contactBeans);
		void onCancel();
	}

	private void showLoading(){
		progressDialog.setMessage("Loading contacts...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
			stop();
			progressDialog.dismiss();
		});

		progressDialog.show();
	}

}
