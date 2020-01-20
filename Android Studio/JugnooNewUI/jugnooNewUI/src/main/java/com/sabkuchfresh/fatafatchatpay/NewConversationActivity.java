package com.sabkuchfresh.fatafatchatpay;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hippo.HippoConfig;
import com.sabkuchfresh.adapters.UserContactAdapter;
import com.sabkuchfresh.datastructure.UserContactObject;
import com.sabkuchfresh.feed.models.ContactResponseModel;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.paperdb.Paper;
import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.PaperDBKeys;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.ContactsUploadService;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.retrofit.CreateChatResponse;
import product.clicklabs.jugnoo.utils.ContactBean;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;
import retrofit.RetrofitError;

/**
 * Created by cl-macmini-01 on 2/6/18.
 */

public class NewConversationActivity extends BaseAppCompatActivity implements View.OnClickListener, TextWatcher{

    public static final int REQUEST_CODE_CONTACTS = 1000;
    private EditText etSearchConnections;
    private ImageButton imgBtnSync;
    private RecyclerView rvConnections;
    private BroadcastReceiver contactSyncReceiver;
    private ArrayList<ContactBean> allContactsList = new ArrayList<>();
    private ArrayList<UserContactObject> allJugnooContacts = new ArrayList<>();
    private UserContactAdapter mUserContactAdapter;
    private SharedPreferences sharedPreferences;
    private LinearLayout llContactSyncing;
    private TextView tvNoJugnooConnections;
    private Animation rotateAnim;
    private ImageView ivContactSync;
    private RelativeLayout rlSync;
    private PermissionCommon mPermissionCommon;
    private TextView tvJugnooConnection;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        mPermissionCommon = new PermissionCommon(this).setCallback(new PermissionCommon.PermissionListener() {
            @Override
            public void permissionGranted(int requestCode) {
                performSyncOperation();
            }

            @Override
            public boolean permissionDenied(int requestCode, boolean neverAsk) {
                return true;
            }

            @Override
            public void onRationalRequestIntercepted(int requestCode) {

            }
        }) ;
        initViews();
        registerSyncUpdateReceiver();

        sharedPreferences = getSharedPreferences(Data.SHARED_PREF_NAME, MODE_PRIVATE);
        boolean areContactsSyncedOneTime = sharedPreferences.getBoolean(Constants.SP_CONTACTS_SYNCED, false);

        // load initial contacts if we have any
        if (Paper.book().read(PaperDBKeys.DB_JUGNOO_CONTACTS_LIST) != null) {
            allJugnooContacts = Paper.book().read(PaperDBKeys.DB_JUGNOO_CONTACTS_LIST);
            if (mUserContactAdapter != null) {
                mUserContactAdapter.updateContacts(allJugnooContacts);
            } else {
                mUserContactAdapter = new UserContactAdapter(this, allJugnooContacts);
                rvConnections.setAdapter(mUserContactAdapter);
            }

            // show keyboard
            Utils.showSoftKeyboard(this, etSearchConnections);

        } else {
            // hide
            Utils.hideSoftKeyboard(this, etSearchConnections);
        }
        if (!areContactsSyncedOneTime) {
            // sync
            syncContacts();
        } else {
            allContactsList = Paper.book().read(PaperDBKeys.DB_ALL_CONTACTS_LIST);
            if (allContactsList != null) {
                // user has synced one time , only fetch contacts
                fetchContacts(false);
            } else {
                syncContacts();
            }
        }
        new HomeUtil().forceRTL(this);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        View view = getCurrentFocus();
        try {
            boolean ret = super.dispatchTouchEvent(event);
            if (view != null && view instanceof EditText) {
                View w = getCurrentFocus();
                int[] scrcoords = new int[2];
                assert w != null;
                w.getLocationOnScreen(scrcoords);
                float x = event.getRawX() + w.getLeft() - scrcoords[0];
                float y = event.getRawY() + w.getTop() - scrcoords[1];
                if (event.getAction() == MotionEvent.ACTION_UP
                        && (x < w.getLeft() || x >= w.getRight()
                        || y < w.getTop() || y > w.getBottom())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
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
        llContactSyncing = (LinearLayout) findViewById(R.id.llContactSyncing);
        llContactSyncing.setVisibility(View.GONE);
        tvNoJugnooConnections = (TextView) findViewById(R.id.tvNoJugnooConnections);
        tvNoJugnooConnections.setVisibility(View.GONE);
        tvNoJugnooConnections.setText(getString(R.string.txt_no_jugnoo_connections, getString(R.string.app_name)));
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        ivContactSync = (ImageView) llContactSyncing.findViewById(R.id.ivContactSync);
        rlSync = (RelativeLayout) findViewById(R.id.rlSync);
        tvJugnooConnection = (TextView) findViewById(R.id.tvJugnooConnection);
        tvJugnooConnection.setText(getString(R.string.txt_jugnoo_connections, getString(R.string.app_name)));

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

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Paper.book().write(PaperDBKeys.DB_ALL_CONTACTS_LIST, allContactsList);
                                }
                            }).start();

                            //DialogPopup.dismissLoadingDialog();
                            sharedPreferences.edit().putBoolean(Constants.SP_CONTACTS_SYNCED, true).commit();
                            //fetch contacts from api
                            fetchContacts(false);
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
     *
     * @param showLoader show loader or not
     */
    private void fetchContacts(final boolean showLoader) {

        if(Data.userData == null){
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

        new ApiCommon<ContactResponseModel>(this).showLoader(showLoader).execute(params, ApiName.FETCH_CONTACTS,
                new APICommonCallback<ContactResponseModel>() {
                    @Override
                    public boolean onNotConnected() {
                        hideSyncLayout();
                        return false;
                    }

                    @Override
                    public boolean onException(final Exception e) {
                        hideSyncLayout();
                        return false;
                    }

                    @Override
                    public void onSuccess(final ContactResponseModel contactResponseModel, final String message, final int flag) {
                        if (!NewConversationActivity.this.isFinishing()) {
                            hideSyncLayout();
                            if (contactResponseModel.getContacts() != null && contactResponseModel.getContacts().size() > 0) {
                                renderContacts(contactResponseModel.getContacts());
                                tvNoJugnooConnections.setVisibility(View.GONE);
                            } else {
                                tvNoJugnooConnections.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public boolean onError(final ContactResponseModel contactResponseModel, final String message, final int flag) {
                        hideSyncLayout();
                        return false;
                    }

                    @Override
                    public boolean onFailure(final RetrofitError error) {
                        hideSyncLayout();
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {
                        hideSyncLayout();
                    }
                });

    }

    private void showSyncLayout() {
        if (!this.isFinishing()) {
            llContactSyncing.setVisibility(View.VISIBLE);
            rvConnections.setVisibility(View.GONE);
            tvNoJugnooConnections.setVisibility(View.GONE);
            ivContactSync.startAnimation(rotateAnim);

            // disable the sync button
            imgBtnSync.setEnabled(false);
            etSearchConnections.setEnabled(false);
            rlSync.setVisibility(View.GONE);
        }
    }

    private void hideSyncLayout() {
        if (!this.isFinishing()) {
            llContactSyncing.setVisibility(View.GONE);
            rvConnections.setVisibility(View.VISIBLE);
            ivContactSync.clearAnimation();

            // re-enable sync button
            imgBtnSync.setEnabled(true);
            etSearchConnections.setEnabled(true);
            rlSync.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sync contacts
     */
    private void syncContacts() {
        //DialogPopup.showLoadingDialog(this, "");

        mPermissionCommon.getPermission(REQUEST_CODE_CONTACTS, Manifest.permission.READ_CONTACTS);

    }
    private void performSyncOperation(){
        showSyncLayout();
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
                if (contactBean.getName() != null) {
                    // full comparison
                    if (jugnooPhone.equals(contactBean.getPhone())) {
                        jugnooContact.setUserName(contactBean.getName());
                    }
                    // last 10 digit comparison( server appends +91 )
                    else if (jugnooPhone.length() >= 10 && contactBean.getPhone().length() >= 10 &&
                            jugnooPhone.substring(jugnooContact.getPhoneNumber().length() - 10)
                                    .equals(contactBean.getPhone().substring(contactBean.getPhone().length() - 10))) {
                        jugnooContact.setUserName(contactBean.getName());
                    }
                }

            }
        }

        Collections.sort(jugnooContacts, new Comparator<UserContactObject>() {
            @Override
            public int compare(final UserContactObject o1, final UserContactObject o2) {
                return o1.getUserName().compareToIgnoreCase(o2.getUserName());
            }
        });

        Paper.book().write(PaperDBKeys.DB_JUGNOO_CONTACTS_LIST, jugnooContacts);
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
                if (contactObject.getUserName().toLowerCase().startsWith(s.toString().trim().toLowerCase()) ||
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

                                HippoConfig.getInstance().openChat(NewConversationActivity.this,
                                        Long.parseLong(createChatResponse.getChannelId()));
                            }
                            // indicate to fugu that a new peer chat is created ( so we can refresh the chat
                            // activity when we resume it )
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        mPermissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


}

