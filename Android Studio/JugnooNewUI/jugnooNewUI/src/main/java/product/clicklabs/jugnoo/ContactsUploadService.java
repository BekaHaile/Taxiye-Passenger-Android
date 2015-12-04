package product.clicklabs.jugnoo;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.ContactBean;
import product.clicklabs.jugnoo.utils.ContactsEntityBean;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by socomo on 11/19/15.
 */
public class ContactsUploadService extends IntentService {

    private static final String TAG = "ContactUploadService";
    private static int UPLOAD_BATCH_SIZE = 100;
    private ArrayDeque<ContactSyncEntry> mSyncQueue;
    private String accessToken = "", sessionId = "", engagementId = "";

    public ContactsUploadService() {
        this("Contact Upload Service");
    }

    public ContactsUploadService(String name) {
        super(name);
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.hasExtra("access_token")){
            accessToken = intent.getExtras().get("access_token").toString();
            sessionId = intent.getExtras().get("session_id").toString();
            engagementId = intent.getExtras().get("engagement_id").toString();
        }

        Log.v("intent values are ","--> "+accessToken+", "+sessionId+", "+engagementId);

        final Cursor contacts = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        if (contacts.getCount() > 0) {
            mSyncQueue = new ArrayDeque<>(contacts.getCount() / UPLOAD_BATCH_SIZE + 1);
            //queueUpSyncs(contacts);
            newQueueUpSyncs();
        } else {
            mSyncQueue = new ArrayDeque<>();
        }
        contacts.close();



    }

    /**
     * Method that hashes contacts and syncs them to server
     *
     * @param contactsCursor The cursor for contacts
     */
    private void queueUpSyncs(final Cursor contactsCursor) {

        ContactSyncEntry syncEntry = null;
        while (contactsCursor.moveToNext()) {

            if (syncEntry == null) {
                syncEntry = new ContactSyncEntry(UPLOAD_BATCH_SIZE);
            }

            final String number = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            final String name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            Log.d(TAG, name+" = "+number);

            if (!TextUtils.isEmpty(number)) {
                syncEntry.addNumber(number);
                syncEntry.addName(name);

                if (syncEntry.isFull()) {
                    Log.d(TAG, "Adding sync entry to queue");
                    mSyncQueue.add(syncEntry);
                    syncEntry = null;
                }
            }
        }

        Log.d(TAG, "mSyncQueue size = " + mSyncQueue.size());

    }

    private void newQueueUpSyncs() {
        try {
            ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String email = "null";
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            ContactBean contactBean = new ContactBean();

                            // Log.e("Name :", name);
                            String phone = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = phone.replace(" ","");

                            contactBean.setName(name);
                            contactBean.setPhone(phone);
                            contactBean.setEmail(email);
                            if (phone != null && (phone.length() >= 10)) {
                                contactList.add(contactBean);
                            }
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();

            /*ContentResolver cr1 = getContentResolver();
            Cursor emailCur = cr1.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);

            if (emailCur.getCount() > 0) {
                while (emailCur.moveToNext()) {
                    String id = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor cur1 = cr1.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = ?", new String[]{
                                    id
                            }, null);
                    String phone = "null";
                    while (cur1.moveToNext()) {
                        ContactBean contactBean = new ContactBean();
                        // to get the contact names
                        String name = cur1
                                .getString(cur1
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        String email = cur1
                                .getString(cur1
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        contactBean.setName(name);
                        contactBean.setPhone(phone);
                        contactBean.setEmail(email);
                        if (email != null) {
                            contactList.add(contactBean);
                        }
                    }
                    cur1.close();
                }
            }
            emailCur.close();*/


            Log.v("size is ", "---> " + contactList.size());
            /*for(int i=0; i<contactList.size(); i++){
                Log.v("name ", contactList.get(i).getName().toString()+", Phone "+contactList.get(i).getPhone().toString()
                +", Email "+contactList.get(i).getEmail().toString());
            }*/

            loadList(contactList);

            //getContactDetails();
        } catch (Exception e) {
            e.printStackTrace();
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
        Log.v("newList size", "--->" + newList.size());
        UPLOAD_BATCH_SIZE = newList.size(); // here I set size of upload batch. right now its full length

        ContactSyncEntry syncEntry = null;
        for(int i=0; i<newList.size(); i++){
            Log.v("name ", newList.get(i).getName().toString()+", Phone "+newList.get(i).getPhone().toString()+", Email "+newList.get(i).getEmail().toString());

            if (syncEntry == null) {
                syncEntry = new ContactSyncEntry(UPLOAD_BATCH_SIZE);
            }

            final String number = newList.get(i).getPhone();
            final String name = newList.get(i).getName();
            final String email = newList.get(i).getEmail();

            if (!TextUtils.isEmpty(number)) {
                syncEntry.addNumber(number);
                syncEntry.addName(name);
                syncEntry.addEmail(email);

                if (syncEntry.isFull() || (i == (newList.size()-1))) {
                    Log.d(TAG, "Adding sync entry to queue");
                    mSyncQueue.add(syncEntry);
                    syncEntry = null;
                }
            }
        }

        Log.d(TAG, "mSyncQueue size = " + mSyncQueue.size());
        syncQueue();
    }

    /**
     * Checks if all the pending syncs were successful
     */
    private void checkIfAllSynced() {

        boolean completedSync = true;
        for (ContactSyncEntry entry : mSyncQueue) {
            completedSync &= entry.isSynced();
        }

        if (completedSync) {
            Log.d(TAG, "SYNCED");
        }
    }

    private void doneWithSync() {
        Log.d(TAG, "STOP SERVICE");
        stopSelf();
    }

    /**
     * Syncs the queued up syncs with the server
     */
    private void syncQueue() {
        Log.d(TAG, "Pending Syncs: %d " + (mSyncQueue != null ? mSyncQueue.size() : 0));
        if (mSyncQueue != null && !mSyncQueue.isEmpty()) {
            try {
            for (ContactSyncEntry currentSyncEntry : mSyncQueue) {
                //currentSyncEntry.setSynced(true);
                Log.d(TAG, "Numbers to sync: %d "+ currentSyncEntry.numbersToSync.size());

                String contactName = "";
                String phoneNumber = "";
                String emailAddress = "";
                List<ContactBean> contactsList = new ArrayList<ContactBean>();
                Log.d(TAG, "Email size %d " + currentSyncEntry.emailToSync.size());

                for (int i = 0; i < currentSyncEntry.numbersToSync.size(); i++) {

                    ContactBean contacts = new ContactBean();
                    phoneNumber = currentSyncEntry.numbersToSync.get(i);


                    if (i < currentSyncEntry.nameToSync.size()){
                        contactName = currentSyncEntry.nameToSync.get(i);
                        emailAddress = currentSyncEntry.emailToSync.get(i);
                    }



                    if (!TextUtils.isEmpty(phoneNumber)) {
                        contacts.setPhone(phoneNumber);
                        contacts.setName(contactName);
                        contacts.setEmail(emailAddress);
                    }
                    contactsList.add(contacts);
                }


                Log.v("size is ","---> "+contactsList.size());
                /*for(int i=0; i<contactsList.size(); i++){
                    Log.v("name ", contactsList.get(i).getName().toString() + ", Phone " + contactsList.get(i).getPhone().toString()
                            + ", Email " + contactsList.get(i).getEmail().toString());
                }*/


                if(contactsList.size() > 10) {
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < contactsList.size(); i++) {
                        /*JSONObject json = new JSONObject();
                        json.put("name", contactsList.get(i).getName().toString());
                        json.put("phone", contactsList.get(i).getPhone().toString());
                        json.put("email", contactsList.get(i).getEmail().toString());
                        jsonArray.put(json);*/
                        jsonArray.put(contactsList.get(i).getPhone());
                    }

                    JSONObject contactsObj = new JSONObject();
                    contactsObj.put("all_contacts", jsonArray);

                    String jsonStr = contactsObj.toString();
                    System.out.println("jsonString: " + jsonStr);


                    /*JSONObject fetchObj = new JSONObject(jsonStr);
                    JSONArray contactsArray = fetchObj.getJSONArray("all_contacts");
                    Log.d("length of contacts", "---> "+contactsArray.length());
                    for(int i=0; i<contactsArray.length(); i++){
                        //JSONObject obj = contactsArray.getJSONObject(i);
                        Log.d("contact is","--> "+contactsArray.get(i));
//                        Log.d("contact is ","--> "+obj.getString("name")+", "+obj.getString("phone")+", "+obj.getString("email"));
                    }*/


                    // Call Api
                    uploadContactsApi(jsonStr, currentSyncEntry);
                    //doneWithSync();
                }
                else{
                    Log.e("soryy ","Your contacts are less than 10");
                    Utils.notificationManager(ContactsUploadService.this, getApplicationContext().getResources().getString(R.string.upload_contact_less_contacts), 44);
                    doneWithSync();
                }
            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void uploadContactsApi(String requestParam, final ContactSyncEntry currentSyncEntry){
        RequestParams params = new RequestParams();
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            //DialogPopup.showLoadingDialog(this, "Loading...");
            params.put("access_token", accessToken);
            params.put("session_id", sessionId);
            params.put("engagement_id", engagementId);
            Log.i("access_token and session_id", accessToken+", "+sessionId+", "+engagementId);

            try {
                if (requestParam != null) {
                    params.put("contacts", requestParam);
                }
            } catch(Exception e){
                e.printStackTrace();
            }

            Log.i("params request_dup_registration", "=" + params);


            SyncHttpClient client1 = Data.getSyncClient();
//            AsyncHttpClient client = Data.getClient();
            client1.post(Config.getServerUrl() + "/refer_all_contacts", params,
                    new CustomAsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(Throwable arg3) {
                            Log.e("request fail", arg3.toString());
							Prefs.with(ContactsUploadService.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                            doneWithSync();
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i("Server response request_dup_registration", "response = " + response);

                            try {
								JSONObject jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								String message = JSONParser.getServerMessage(jObj);
								if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
									Data.userData.contactSaved = 1;
								}
								else{
									Prefs.with(ContactsUploadService.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
								}
                            }  catch (Exception exception) {
                                exception.printStackTrace();
                                //DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                            //DialogPopup.dismissLoadingDialog();
                            currentSyncEntry.setSynced(true);
                            checkIfAllSynced();
                            doneWithSync();
                        }
                    });
        }
        else {
            //Database2.getInstance(ContactsUploadService.this).insertPendingAPICall(ContactsUploadService.this, Config.getServerUrl()+"/refer_all_contacts", params);
            //DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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

    /**
     * Class that holds a single contact upload operation
     */
    private static class ContactSyncEntry {

        public final List<String> numbersToSync;
        public final List<String> nameToSync;
        public final List<String> emailToSync;


        public final int capacity;

        private boolean mSynced;

        public ContactSyncEntry(int capacity) {
            numbersToSync = new ArrayList<String>(capacity);
            nameToSync = new ArrayList<String>(capacity);
            emailToSync = new ArrayList<String>(capacity);
            this.capacity = capacity;
        }

        public boolean isSynced() {
            return mSynced;
        }

        public void setSynced(boolean mSynced) {
            this.mSynced = mSynced;
        }

        public boolean isFull() {
            return numbersToSync.size() == capacity;
        }

        public void addNumber(String number) {

            if (!TextUtils.isEmpty(number)) {
                numbersToSync.add(number);
            }
        }

        public void addName(String name) {

            if (!TextUtils.isEmpty(name)) {
                nameToSync.add(name);
            }
        }

        public void addEmail(String email) {

            if (!TextUtils.isEmpty(email)) {
                emailToSync.add(email);
            }
        }

        public void removeNumber(String number) {
            numbersToSync.remove(number);
        }

        public void removeName(String name) {
            nameToSync.remove(name);
        }


        public String getJoinedNumbersCsv() {

            final List<String> quotedNumbers = new ArrayList<String>(numbersToSync.size());

            for (String eachNumber : numbersToSync) {
                quotedNumbers.add(String.format(Locale.US, "'%s'", eachNumber));
            }


            return TextUtils.join(",", quotedNumbers);
        }

        public String getJoinedNamesCsv() {
            final List<String> quotedNames = new ArrayList<>(nameToSync.size());

            for (String eachName : nameToSync) {
                quotedNames.add(String.format(Locale.US, "'%s'", eachName));
            }
            return TextUtils.join(",", quotedNames);

        }

    }
}
