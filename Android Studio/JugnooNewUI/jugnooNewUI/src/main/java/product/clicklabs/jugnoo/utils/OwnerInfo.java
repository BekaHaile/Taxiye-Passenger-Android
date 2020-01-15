package product.clicklabs.jugnoo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by ankit on 25/10/16.
 */
public class OwnerInfo {
    // this class allows to get device information. It's done in two steps:
    // 1) get synchronization account email
    // 2) get contact data, associated with this email
    // by https://github.com/jehy
    //WARNING! You need to have permissions
    //
    //<uses-permission android:name="android.permission.READ_CONTACTS" />
    //<uses-permission android:name="android.permission.GET_ACCOUNTS" />
    //
    // in your AndroidManifest.xml for this code.

    public String id = null;
    public String email = null;
    public String phone = "";
    public String accountName = null;
    public String name = null;

    public String OwnerInfo(Activity MainActivity, String accountName) {

            ContentResolver cr = MainActivity.getContentResolver();
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.DATA + " = ?",
                    new String[] { accountName }, null);
            while (emailCur.moveToNext()) {
                id = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
                email = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                phone = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String newName = emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (name == null || newName.length() > name.length())
                    name = newName;

                Log.v("Got contacts", "ID " + id + " Email : " + email
                        + " Name : " + name+" Phone : "+phone);
            }

            emailCur.close();
            if (id != null) {
                // get the phone number
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[] { id }, null);
                while (pCur.moveToNext()) {
                    phone = pCur
                            .getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.v("Got contacts", "phone" + phone);
                }
                pCur.close();
            }
        return name;
        }


        public static String OwnerPhone(Activity MainActivity) {
            String id = null;
            String phone = null;

            try {
                TelephonyManager tMgr = (TelephonyManager)MainActivity.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    phone = tMgr.getLine1Number();
                }
            } catch (Exception e) {
                e.printStackTrace();
                phone = null;
            }

      /*      if(phone==null || phone.trim().length()==0){

                ContentResolver cr = MainActivity.getContentResolver();
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.DATA + " = ?",
                        new String[] { accountName }, null);
                while (emailCur.moveToNext()) {
                    id = emailCur
                            .getString(emailCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));

                    phone = emailCur.getString(emailCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                }

                emailCur.close();
                if (id != null) {
                    // get the phone number
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { id }, null);
                    while (pCur.moveToNext()) {
                        phone = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
                        Log.v("Got contacts", "phone" + phone);
                    }
                    pCur.close();
                }


            }
          */

            return phone;
        }

    }
