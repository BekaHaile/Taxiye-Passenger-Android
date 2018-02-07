package com.sabkuchfresh.fatafatchatpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.sabkuchfresh.adapters.UserContactAdapter;
import com.sabkuchfresh.datastructure.UserContactObject;
import com.sabkuchfresh.feed.models.ContactResponseModel;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.ContactsUploadService;
import product.clicklabs.jugnoo.retrofit.CreateChatResponse;
import product.clicklabs.jugnoo.utils.ContactBean;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.RetrofitError;

/**
 * Created by cl-macmini-01 on 2/6/18.
 */

public class NewConversationActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText etSearchConnections;
    private ImageButton imgBtnSync;
    private RecyclerView rvConnections;
    private BroadcastReceiver contactSyncReceiver;
    private ArrayList<ContactBean> allContactsList = new ArrayList<>();
    private ArrayList<UserContactObject> allJugnooContacts = new ArrayList<>();
    private UserContactAdapter mUserContactAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        initViews();
        registerSyncUpdateReceiver();
        // proceed to sync contacts
        syncContacts();

    }

    /**
     * Init views
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Fonts.avenirNext(this));
        tvTitle.setText(getResources().getString(R.string.txt_new_message));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_selector);
        etSearchConnections = (EditText) findViewById(R.id.etSearchConnections);
        imgBtnSync = (ImageButton) findViewById(R.id.imgBtnSync);
        rvConnections = (RecyclerView) findViewById(R.id.rvConnections);
        rvConnections.setLayoutManager(new LinearLayoutManager(this));

        etSearchConnections.addTextChangedListener(this);
        imgBtnSync.setOnClickListener(this);
    }


    /**
     * Registers broadcast when contact sync is done
     */
    private void registerSyncUpdateReceiver() {
        contactSyncReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(Constants.ACTION_LOADING_COMPLETE)) {
                        allContactsList = intent.getParcelableArrayListExtra(Constants.KEY_CONTACTS_LIST);
                        if (!NewConversationActivity.this.isFinishing()) {
                            DialogPopup.dismissLoadingDialog();
                            //fetch contacts from api
                            fetchContacts();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_LOADING_COMPLETE);
        registerReceiver(contactSyncReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactSyncReceiver != null) {
            unregisterReceiver(contactSyncReceiver);
        }
    }

    /**
     * Fetched jugnoo contacts from server
     */
    private void fetchContacts() {

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

        new ApiCommon<ContactResponseModel>(this).showLoader(true).execute(params, ApiName.FETCH_CONTACTS,
                new APICommonCallback<ContactResponseModel>() {
                    @Override
                    public boolean onNotConnected() {
                        return false;
                    }

                    @Override
                    public boolean onException(final Exception e) {
                        return false;
                    }

                    @Override
                    public void onSuccess(final ContactResponseModel contactResponseModel, final String message, final int flag) {
                        if (!NewConversationActivity.this.isFinishing()) {
                            if (contactResponseModel.getContacts() != null && contactResponseModel.getContacts().size() > 0) {
                                renderContacts(contactResponseModel.getContacts());
                            }
                        }
                    }

                    @Override
                    public boolean onError(final ContactResponseModel contactResponseModel, final String message, final int flag) {
                        return false;
                    }

                    @Override
                    public boolean onFailure(final RetrofitError error) {
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

    }

    /**
     * Sync contacts
     */
    private void syncContacts() {
        DialogPopup.showLoadingDialog(this, "");
        // start the contact upload sync in background
        Intent syncContactsIntent = new Intent(this, ContactsUploadService.class);
        syncContactsIntent.putExtra(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        syncContactsIntent.putExtra(Constants.KEY_COMING_FROM_NEW_CONVERSATION, true);
        startService(syncContactsIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.imgBtnSync) {
            syncContacts();
        }
    }

    /**
     * Render contacts
     *
     * @param jugnooContacts jugnoo contacts
     */
    private void renderContacts(ArrayList<UserContactObject> jugnooContacts) {
        // get name of the contacts from the all contacts list
        for (ContactBean contactBean : allContactsList) {
            for (UserContactObject jugnooContact : jugnooContacts) {
                String jugnooPhone = jugnooContact.getPhoneNumber();
                // full comparison
                if (jugnooPhone.equals(contactBean.getPhone())) {
                    jugnooContact.setUserName(contactBean.getName());
                }
                // last 10 digit comparison( server appends +91 )
                else if (jugnooPhone.length() >= 10 && jugnooPhone.substring(jugnooContact.getPhoneNumber().length() - 10)
                        .equals(contactBean.getPhone())) {
                    jugnooContact.setUserName(contactBean.getName());
                }

            }
        }
        allJugnooContacts = jugnooContacts;

        if (mUserContactAdapter != null) {
            mUserContactAdapter.updateContacts(jugnooContacts);
        } else {
            mUserContactAdapter = new UserContactAdapter(this, jugnooContacts);
            rvConnections.setAdapter(mUserContactAdapter);
        }
    }


    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        // filter list based on phone number and name
        if (!s.toString().trim().isEmpty()) {
            ArrayList<UserContactObject> filteredList = new ArrayList<>();
            for (UserContactObject contactObject : allJugnooContacts) {
                if (contactObject.getUserName().toLowerCase().startsWith(s.toString().trim()) ||
                        contactObject.getPhoneNumber().contains(s.toString().trim())) {
                    filteredList.add(contactObject);
                }
            }
            if (mUserContactAdapter != null) {
                mUserContactAdapter.updateContacts(filteredList);
            }
        } else {
            // set all
            if (allJugnooContacts.size() > 0) {
                mUserContactAdapter.updateContacts(allJugnooContacts);
            }
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {

    }

    /**
     * Contact clicked from contacts list
     *
     * @param adapterPosition the position of the clicked contact
     */
    public void onContactSelected(final int adapterPosition) {
        final UserContactObject userContactObject = mUserContactAdapter.getJugnooContacts().get(adapterPosition);

        //create new chat
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.PAYER_USER_IDENTIFIER, Data.userData.userIdentifier);
        params.put(Constants.PAYEE_PHONE_NUMBER, userContactObject.getPhoneNumber());

        new ApiCommon<CreateChatResponse>(this).showLoader(true).execute(params, ApiName.CREATE_CHAT
                , new APICommonCallback<CreateChatResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        return false;
                    }

                    @Override
                    public void onSuccess(CreateChatResponse createChatResponse, String message, int flag) {

                        // open chat
                        if (!NewConversationActivity.this.isFinishing()) {
                            if (createChatResponse != null && !TextUtils.isEmpty(createChatResponse.getChannelId())
                                    && createChatResponse.getFuguData() != null) {

                                FuguConfig.getInstance().openDirectChat(NewConversationActivity.this,
                                        Long.parseLong(createChatResponse.getChannelId()));
                            }
                            // indicate to fugu that a new peer chat is created ( so we can refresh the chat
                            // activity when we resume it )
                            FuguConfig.getInstance().setNewPeerChatCreated(true);
                            finish();

                        }
                    }

                    @Override
                    public boolean onError(CreateChatResponse createChatResponse, String message, int flag) {
                        return false;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                });
    }
}

