package com.jugnoo.pay.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jugnoo.pay.adapters.ContactsListAdapter;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.Prefs;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.Validator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SelectContactActivity extends BaseActivity implements RecyclerViewClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }
    @Bind(R.id.phone_et)
    EditText searchET;
    @OnClick(R.id.imageViewRefresh)
    void imageViewRefreshClicked(){
        fetchUserContacts();
    }
    private String accessToken;
    private RecyclerView contactsRecycler;
//    / ArrayList
   private ArrayList<SelectUser> selectUsers;
   private List<SelectUser> temp;
    // Cursor to load contacts list
    private Cursor phones, email;

    // Pop up
   private ContentResolver resolver;
    private ContactsListAdapter adapter;
    private boolean requestStatus=false;
    public static SelectContactActivity selectContactActivityObj;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        ButterKnife.bind(this);
        toolbarTitleTxt.setText(R.string.select_contact_screen);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        selectContactActivityObj = this;
        accessToken = Data.userData.accessToken;          //Prefs.with(SelectContactActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
        contactsRecycler = (RecyclerView) findViewById(R.id.contacts_recycler);
       requestStatus = getIntent().getBooleanExtra(AppConstants.REQUEST_STATUS,false);

        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectContactActivity.this);
        contactsRecycler.setLayoutManager(mLayoutManager);
        contactsRecycler.setItemAnimator(new DefaultItemAnimator());

        if((Prefs.with(SelectContactActivity.this).getString(SharedPreferencesName.USER_CONTACTS, "") != null)
                && (!Prefs.with(SelectContactActivity.this).getString(SharedPreferencesName.USER_CONTACTS, "").equalsIgnoreCase(""))){
            Gson gson = new Gson();
            String json = Prefs.with(SelectContactActivity.this).getString(SharedPreferencesName.USER_CONTACTS, "");
            Type type = new TypeToken<ArrayList<SelectUser>>() {}.getType();
            selectUsers = gson.fromJson(json, type);
            adapter = new ContactsListAdapter(SelectContactActivity.this, selectUsers, SelectContactActivity.this);
            contactsRecycler.setAdapter(adapter);
        } else {
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.filter(searchET.getText().toString());
            }
        });


    }

    private void fetchUserContacts(){
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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
            Intent intent = new Intent(SelectContactActivity.this, SendMoneyActivity.class);
            intent.putExtra(AppConstants.REQUEST_STATUS, requestStatus);
            Bundle bun =new Bundle();
            bun.putParcelable(AppConstants.CONTACT_DATA, data);
            intent.putExtras( bun);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            finish();
        }
        else if(new Validator().validateEmail(searchET.getText().toString()))
        {
            SelectUser newData = new SelectUser();
            newData.setName("VPA address");
            newData.setPhone(searchET.getText().toString());
            newData.setAmount("");
            data.setOrderId("0");
            Intent intent = new Intent(SelectContactActivity.this, SendMoneyActivity.class);
            intent.putExtra(AppConstants.REQUEST_STATUS, requestStatus);
            Bundle bun =new Bundle();
            bun.putParcelable(AppConstants.CONTACT_DATA, newData);
            intent.putExtras( bun);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            finish();
        }
        else
        {
            searchET.requestFocus();
            searchET.setHovered(true);
            searchET.setError("Please fill alteast 10 Digits mobile number.");
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
            CallProgressWheel.showLoadingDialog(SelectContactActivity.this, "Please wait..");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

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
                            if(o1.getName().toString().equalsIgnoreCase(o2.getName().toString())){
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
                    Prefs.with(SelectContactActivity.this).save(SharedPreferencesName.USER_CONTACTS, json);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new ContactsListAdapter(SelectContactActivity.this, selectUsers, SelectContactActivity.this);
            contactsRecycler.setAdapter(adapter);
            CallProgressWheel.dismissLoadingDialog();



//            // Select item on listclick
//            adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    Log.e("search", "here---------------- listener");
//
//                    SelectUser data = selectUsers.get(i);
//                }
//            });

//            contactsRecycler.setFastScrollEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(phones != null && !phones.isClosed()){
            phones.close();
        }

    }

}
