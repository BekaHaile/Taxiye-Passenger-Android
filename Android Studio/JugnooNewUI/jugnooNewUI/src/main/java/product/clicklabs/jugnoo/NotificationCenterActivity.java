package product.clicklabs.jugnoo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.ContactBean;
import product.clicklabs.jugnoo.utils.ContactsEntityBean;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.EventsHolder;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationData> notificationList;
	private LinearLayout linearLayoutNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);

		linearLayoutNoNotifications = (LinearLayout) findViewById(R.id.linearLayoutNoNotifications);
		linearLayoutNoNotifications.setVisibility(View.GONE);
		((TextView)findViewById(R.id.textViewWhoops)).setTypeface(Fonts.mavenRegular(this));
		((TextView)findViewById(R.id.textViewNoNotifications)).setTypeface(Fonts.mavenLight(this));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this, R.layout.notification_list_item);
		recyclerViewNotification.setAdapter(myNotificationAdapter);


        imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		new GetNotificationsAsync().execute();

		//new GetAllContacts().execute();

		/*Intent syncContactsIntent = new Intent(NotificationCenterActivity.this, ContactsUploadService.class);
		startService(syncContactsIntent);*/
    }



	class GetNotificationsAsync extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogPopup.showLoadingDialog(NotificationCenterActivity.this, "Loading...");
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				notificationList.clear();
				notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
				Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			DialogPopup.dismissLoadingDialog();
			if(notificationList.size() > 0){
				linearLayoutNoNotifications.setVisibility(View.GONE);
			} else{
				linearLayoutNoNotifications.setVisibility(View.VISIBLE);
			}
			myNotificationAdapter.notifyDataSetChanged();
		}
	}

	class GetAllContacts extends AsyncTask<String, String, ArrayList<ContactBean>>{
		ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
		@Override
		protected ArrayList<ContactBean> doInBackground(String... strings) {
			try {
				ContentResolver cr = getContentResolver();
				Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

				if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                    new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                //String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                //Log.v("Name : ", "--> " + name + ", Phone No: ---> " + phoneNo);

								ContactBean contactBean = new ContactBean();

								// Log.e("Name :", name);
								String phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								phone = phone.replace(" ","");
								// Log.e("Email", email);


								contactBean.setName(name);
								contactBean.setPhone(phone);
								if (phone != null) {
									contactList.add(contactBean);
								}
                            }
                            pCur.close();
                        }
                    }
                }



				Log.v("size is ","---> "+contactList.size());
				for(int i=0; i<contactList.size(); i++){
					Log.v("name ", contactList.get(i).getName().toString()+", Phone "+contactList.get(i).getPhone().toString());
				}

				loadList(contactList);

				//getContactDetails();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contactList;
		}

		@Override
		protected void onPostExecute(ArrayList<ContactBean> s) {
			super.onPostExecute(s);

			Log.v("task", "complete");
			//textViewTitle.setText(contactList.get(0).getName().toString());

		}
	}

	public void loadList(ArrayList<ContactBean> list) {
		Set set = new TreeSet(new Comparator<ContactBean>() {
			@Override
			public int compare(ContactBean o1, ContactBean o2) {
				if(o1.getName().toString().equalsIgnoreCase(o2.getName().toString())){
					return 0;
				}
				return 1;
			}
		});

		set.addAll(list);

		System.out.println("\n***** After removing duplicates *******\n");

		final ArrayList<ContactBean> newList = new ArrayList<ContactBean>(set);
		Log.v("newList size","--->"+newList.size());

		for(int i=0; i<newList.size(); i++){
			Log.v("name ", newList.get(i).getName().toString()+", Phone "+newList.get(i).getPhone().toString());
		}


	}

	public ArrayList<ContactsEntityBean> getContactDetails() {
		ArrayList<ContactsEntityBean> contactList = new ArrayList<ContactsEntityBean>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cur1 = cr.query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " = ?", new String[] {
								id
						}, null);
				while (cur1.moveToNext()) {
					ContactsEntityBean contactsEntityBean = new ContactsEntityBean();
					// to get the contact names
					String name = cur1
							.getString(cur1
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

					// Log.e("Name :", name);
					String email = cur1
							.getString(cur1
									.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					// Log.e("Email", email);


					contactsEntityBean.setPhones(name);
					contactsEntityBean.setEmails(email);
					if (email != null) {
						contactList.add(contactsEntityBean);
					}
				}
				cur1.close();
			}
		}

		Log.v("size is ","---> "+contactList.size());
		for(int i=0; i<contactList.size(); i++){
			Log.v("name ", contactList.get(i).getPhones()+", email "+contactList.get(i).getEmails());
		}
		return contactList;
	}

    public void performBackPressed(){
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(root);
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new GetNotificationsAsync().execute();
			}
		});
	}

}
