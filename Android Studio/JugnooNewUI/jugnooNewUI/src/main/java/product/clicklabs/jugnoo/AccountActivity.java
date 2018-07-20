package product.clicklabs.jugnoo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.country.picker.Country;
import com.country.picker.CountryPicker;
import com.country.picker.OnCountryPickerListener;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.adapters.AccountMenuItemsAdapter;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.fragments.AddressBookFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.dialogs.JeanieIntroDialog;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FBAccountKit;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class AccountActivity extends BaseFragmentActivity implements GAAction, GACategory, OnCountryPickerListener {

    private final String TAG = "View Account";

	LinearLayout relative;

	private TextView textViewTitle, textViewPasswordSave;
	ImageView imageViewBack;
    View viewTrackingLog;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

    ImageView imageViewProfileImage;
	EditText editTextUserName, editTextEmail, editTextPhone;
	TextView tvCountryCode;
    LinearLayout linearLayoutPhone;
    ImageView imageViewEditProfile, ivEditPhone, imageViewEditProfileSave;

    RelativeLayout relativeLayoutChangePassword, relativeLayoutEmergencyContact;
    TextView textViewEmergencyContact;
    LinearLayout linearLayoutPasswordChange;
    RelativeLayout relativeLayoutOldPassword, relativeLayoutNewPassword, relativeLayoutRetypePassword;
    EditText editTextOldPassword, editTextNewPassword, editTextRetypePassword;
    ImageView imageViewChangePassword, imaveViewOldPasswordVisibility, imaveViewNewPasswordVisibility, imaveViewRetypePasswordVisibility;

    LinearLayout linearLayoutLogout, linearLayoutAbout;
    ImageView ivLogout;

	ImageView imageViewEditHome, imageViewEditWork, imageViewJugnooJeanie, imageViewPokemon, imageViewFAB, imageViewFABQuestion;
	RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork, relativeLayoutJugnooJeanie;
    LinearLayout relativeLayoutPokemon, relativeLayoutFAB;
	TextView textViewAddHome, textViewAddHomeValue, textViewAddWork, textViewAddWorkValue, textViewJugnooJeanie, textViewPokemon, textViewFAB;
    private LinearLayout linearLayoutPasswordSave;

    RelativeLayout relativeLayoutAddressBook, relativeLayoutContainer;
    NonScrollListView listViewSavedLocations;
    RelativeLayout relativeLayoutAddNewAddress;
    View viewStarIcon;
    SavedPlacesAdapter savedPlacesAdapter;
    private RecyclerView rvMenuItems;
    private static final int FRAMEWORK_REQUEST_CODE = 1;

    private int nextPermissionsRequestCode = 4000;
    private final Map<Integer, AccountActivity.OnCompleteListener> permissionsListeners = new HashMap<>();
    private FBAccountKit fbAccountKit;
    private boolean setJeanieState;
    Bundle bundle = new Bundle();
    public static boolean updateMenuBar ;

    private RelativeLayout rlMain;
    private TextView tvAbout;
    private TextView textViewAddressBook;
    private CountryPicker countryPicker;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
        rvMenuItems = (RecyclerView) findViewById(R.id.rvMenuItems);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        viewTrackingLog = findViewById(R.id.viewTrackingLog);
        textViewTitle.setText(R.string.title_my_profile);
        textViewTitle.setVisibility(View.VISIBLE);
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

        imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);

		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Fonts.mavenMedium(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Fonts.mavenMedium(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Fonts.mavenMedium(this));
        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode); tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        linearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayoutPhone);
        imageViewEditProfile = (ImageView) findViewById(R.id.imageViewEditProfile);
        imageViewEditProfileSave = (ImageView) findViewById(R.id.imageViewEditProfileSave); imageViewEditProfileSave.setVisibility(View.GONE);
        textViewPasswordSave = (TextView) findViewById(R.id.textViewPasswordSave); textViewPasswordSave.setTypeface(Fonts.mavenMedium(this));
        linearLayoutPasswordSave = (LinearLayout) findViewById(R.id.linearLayoutPasswordSave);
        ivEditPhone = (ImageView) findViewById(R.id.ivEditPhone);

        relativeLayoutChangePassword = (RelativeLayout) findViewById(R.id.relativeLayoutChangePassword); relativeLayoutChangePassword.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textViewChangePassword)).setTypeface(Fonts.mavenMedium(this));
        relativeLayoutEmergencyContact = (RelativeLayout) findViewById(R.id.relativeLayoutEmergencyContact);
        textViewEmergencyContact = (TextView) findViewById(R.id.textViewEmergencyContact); textViewEmergencyContact.setTypeface(Fonts.mavenMedium(this));
        linearLayoutPasswordChange = (LinearLayout) findViewById(R.id.linearLayoutPasswordChange);
        imageViewChangePassword = (ImageView) findViewById(R.id.imageViewChangePassword);
        relativeLayoutOldPassword = (RelativeLayout) findViewById(R.id.relativeLayoutOldPassword);
        relativeLayoutNewPassword = (RelativeLayout) findViewById(R.id.relativeLayoutNewPassword);
        relativeLayoutRetypePassword = (RelativeLayout) findViewById(R.id.relativeLayoutRetypePassword);
        editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword); editTextOldPassword.setTypeface(Fonts.mavenMedium(this));
        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword); editTextNewPassword.setTypeface(Fonts.mavenMedium(this));
        editTextRetypePassword = (EditText) findViewById(R.id.editTextRetypePassword); editTextRetypePassword.setTypeface(Fonts.mavenMedium(this));
        imaveViewOldPasswordVisibility = (ImageView) findViewById(R.id.imaveViewOldPasswordVisibility);
        imaveViewNewPasswordVisibility = (ImageView) findViewById(R.id.imaveViewNewPasswordVisibility);
        imaveViewRetypePasswordVisibility = (ImageView) findViewById(R.id.imaveViewRetypePasswordVisibility);
        linearLayoutPasswordChange.setVisibility(View.GONE);
        viewStarIcon = (View) findViewById(R.id.viewStarIcon); viewStarIcon.setVisibility(View.GONE);
        //imageViewChangePassword.setRotation(270f);




		relativeLayoutAddHome = (RelativeLayout) findViewById(R.id.relativeLayoutAddHome);
        imageViewEditHome = (ImageView)findViewById(R.id.imageViewEditHome);
		textViewAddHome = (TextView) findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(this));
        textViewAddHomeValue = (TextView) findViewById(R.id.textViewAddHomeValue); textViewAddHomeValue.setTypeface(Fonts.mavenMedium(this));
        relativeLayoutAddWork = (RelativeLayout) findViewById(R.id.relativeLayoutAddWork);
        imageViewEditWork = (ImageView)findViewById(R.id.imageViewEditWork);
        textViewAddWork = (TextView) findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(this));
        textViewAddWorkValue = (TextView) findViewById(R.id.textViewAddWorkValue); textViewAddWorkValue.setTypeface(Fonts.mavenMedium(this));

        relativeLayoutJugnooJeanie = (RelativeLayout)findViewById(R.id.relativeLayoutJugnooJeanie);
        textViewJugnooJeanie = (TextView)findViewById(R.id.textViewJugnooJeanie); textViewJugnooJeanie.setTypeface(Fonts.mavenMedium(this));
        textViewJugnooJeanie.setText(getString(R.string.jugnoo_jeanie, getString(R.string.app_name)));
        imageViewJugnooJeanie = (ImageView)findViewById(R.id.imageViewJugnooJeanie);
        relativeLayoutJugnooJeanie.setVisibility(View.GONE);
        if(Prefs.with(AccountActivity.this).getInt(SPLabels.SHOW_JUGNOO_JEANIE, 0) == 1){
            relativeLayoutJugnooJeanie.setVisibility(View.VISIBLE);
        }


        relativeLayoutPokemon = (LinearLayout) findViewById(R.id.relativeLayoutPokemon);
        textViewPokemon = (TextView)findViewById(R.id.textViewPokemon); textViewPokemon.setTypeface(Fonts.mavenMedium(this));
        imageViewPokemon = (ImageView)findViewById(R.id.imageViewPokemon);
        relativeLayoutPokemon.setVisibility(View.GONE);
        if(Prefs.with(AccountActivity.this).getInt(Constants.KEY_SHOW_POKEMON_DATA, 0) == 1){
            relativeLayoutPokemon.setVisibility(View.VISIBLE);
            if(Prefs.with(AccountActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_on);
            } else {
                imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_off);
            }
        }

        relativeLayoutFAB = (LinearLayout) findViewById(R.id.relativeLayoutFAB);
        textViewFAB = (TextView)findViewById(R.id.textViewFAB); textViewFAB.setTypeface(Fonts.mavenMedium(this));
        textViewFAB.setText(getString(R.string.jugnoo_fab, getString(R.string.app_name)));
        imageViewFABQuestion = (ImageView)findViewById(R.id.imageViewFABQuestion);
        imageViewFAB = (ImageView)findViewById(R.id.imageViewFAB);
        relativeLayoutFAB.setVisibility(View.GONE);
        fbAccountKit = new FBAccountKit(AccountActivity.this);
        try {
            if((Prefs.with(AccountActivity.this).getInt(SPLabels.SHOW_FAB_SETTING, 0) == 1) &&
                    (Data.userData.getIntegratedJugnooEnabled() == 1) &&
                    (Data.userData.getFreshEnabled() == 1 || Data.userData.getMealsEnabled() == 1 ||
                    Data.userData.getDeliveryEnabled() == 1)){
                relativeLayoutFAB.setVisibility(View.VISIBLE);
                if(Prefs.with(AccountActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_on);
                } else {
                    imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_off);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listViewSavedLocations = (NonScrollListView) findViewById(R.id.listViewSavedLocations);
        try {
            savedPlacesAdapter = new SavedPlacesAdapter(this, Data.userData.getSearchResults(), new SavedPlacesAdapter.Callback() {
                @Override
                public void onItemClick(SearchResult searchResult) {
                    Intent intent=new Intent(AccountActivity.this, AddPlaceActivity.class);
                    intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                    intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
                    startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }

                @Override
                public void onDeleteClick(SearchResult searchResult) {
                }
            }, false, false, false);
            listViewSavedLocations.setAdapter(savedPlacesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutAddNewAddress = (RelativeLayout) findViewById(R.id.relativeLayoutAddNewAddress);
        ((TextView) findViewById(R.id.textViewAddNewAddress)).setTypeface(Fonts.mavenMedium(this));

        relativeLayoutAddressBook = (RelativeLayout) findViewById(R.id.relativeLayoutAddressBook);
        textViewAddressBook =  ((TextView)findViewById(R.id.textViewAddressBook));
        textViewAddressBook.setTypeface(Fonts.mavenMedium(this));

//        rlJugnooStar = (RelativeLayout) findViewById(R.id.rlJugnooStar);
      //  ((TextView)findViewById(R.id.tvJugnooStar)).setTypeface(Fonts.mavenMedium(this));

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
        tvAbout = (TextView) findViewById(R.id.tvAbout);
        tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);


        imageViewPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    if(Prefs.with(AccountActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                        imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_off);
                        Prefs.with(AccountActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 0);
                    } else {
                        imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_on);
                        Prefs.with(AccountActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 1);
                    }
                }
            }
        });

        imageViewFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    if (Prefs.with(AccountActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_off);
                        Prefs.with(AccountActivity.this).save(Constants.FAB_ENABLED_BY_USER, 0);
                    } else {
                        imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_on);
                        Prefs.with(AccountActivity.this).save(Constants.FAB_ENABLED_BY_USER, 1);
                    }
                }
            }
        });

        imageViewFABQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    new JeanieIntroDialog(AccountActivity.this).show();
                }
            }
        });


        linearLayoutLogout = (LinearLayout) findViewById(R.id.linearLayoutLogout);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);
        ((TextView)findViewById(R.id.textViewLogout)).setTypeface(Fonts.mavenMedium(this));
        linearLayoutAbout = (LinearLayout) findViewById(R.id.linearLayoutAbout);
        ((TextView)findViewById(R.id.textViewAbout)).setTypeface(Fonts.mavenMedium(this));

        setUserData();
        setSavePlaces();

		imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        setEditPhoneUI();
        ivEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("phone click", "phone click");
                PhoneNumber phoneNumber = new PhoneNumber(Data.userData.getCountryCode(),
                        Utils.retrievePhoneNumberTenChars(Data.userData.phoneNo, Data.userData.getCountryCode()),
                        Utils.getCountryIsoFromCode(AccountActivity.this, Data.userData.getCountryCode()));
                fbAccountKit.startFbAccountKit(phoneNumber);
            }
        });

        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    startActivity(new Intent(AccountActivity.this, AboutActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    GAUtils.event(SIDE_MENU, USER + PROFILE, GAAction.ABOUT);
                }
            }
        });

		editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });
		editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });
		editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });
        countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(this)
                        .build();
        tvCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.showDialog(getSupportFragmentManager());
            }
        });

        imageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewEditProfileSave.performClick();
            }
        });

        imageViewEditProfileSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    editTextUserName.setError(null);
                    editTextEmail.setError(null);
                    editTextPhone.setError(null);
                    if (editTextUserName.isEnabled()) {
                        String nameChanged = editTextUserName.getText().toString().trim();
                        String emailChanged = editTextEmail.getText().toString().trim();
                        String phoneNoChanged = editTextPhone.getText().toString().trim();
                        String countryCode = tvCountryCode.getText().toString();
                        if(TextUtils.isEmpty(countryCode)){
                            Utils.showToast(AccountActivity.this, getString(R.string.please_select_country_code));
                            return;
                        }
                        phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged, countryCode);
                        if ("".equalsIgnoreCase(nameChanged)) {
                            editTextUserName.requestFocus();
                            editTextUserName.setError(getResources().getString(R.string.username_empty_error));
                        } else if ("".equalsIgnoreCase(emailChanged)) {
                            editTextEmail.requestFocus();
                            editTextEmail.setError(getResources().getString(R.string.email_empty_error));
                        } else if ("".equalsIgnoreCase(phoneNoChanged)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError(getResources().getString(R.string.phone_empty_error));
                        } else if (!Utils.isEmailValid(emailChanged)) {
                            editTextEmail.requestFocus();
                            editTextEmail.setError(getResources().getString(R.string.invalid_email_error));
                        } else if (!Utils.validPhoneNumber(phoneNoChanged)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError(getResources().getString(R.string.invalid_phone_error));
                        } /*else if (Data.userData.userName.equals(nameChanged)
                                && Data.userData.userEmail.equalsIgnoreCase(emailChanged)
                                z&& Data.userData.phoneNo.equalsIgnoreCase("+91" + phoneNoChanged)) {
                            Utils.showToast(AccountActivity.this, getString(R.string.nothing_changed));
                        }*/ else {
                            updateUserProfileAPI(AccountActivity.this, Utils.capEachWord(nameChanged), emailChanged, countryCode + phoneNoChanged,
                                    !Data.userData.phoneNo.equalsIgnoreCase(countryCode + phoneNoChanged), countryCode);
                        }
                    } else {
                        toggleProfileOptions(false);
                        editTextUserName.requestFocus();
                        editTextUserName.setSelection(editTextUserName.getText().length());
                        editTextUserName.setEnabled(true);
                        editTextUserName.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        editTextEmail.setEnabled(true);
                        editTextEmail.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        editTextPhone.setEnabled(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1);
                        tvCountryCode.setEnabled(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1);
                        if(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1) {
                            linearLayoutPhone.setBackgroundResource(R.drawable.bg_white_orange_bb);
                            tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0);
                        }
                        //buttonEditProfile.setText(getResources().getString(R.string.save_changes));
                        imageViewEditProfile.setVisibility(View.GONE);
                        imageViewEditProfileSave.setVisibility(View.VISIBLE);
                        Utils.showSoftKeyboard(AccountActivity.this, editTextUserName);
                        GAUtils.event(SIDE_MENU, USER+PROFILE, GAAction.EDIT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editTextUserName.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextEmail.setSelection(editTextEmail.getText().length());
                editTextEmail.requestFocus();
                return true;
            }
        });

        editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextPhone.setSelection(editTextPhone.getText().length());
                editTextPhone.requestFocus();
                return true;
            }
        });
        editTextPhone.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                imageViewEditProfile.performClick();
                return true;
            }
        });



        imageViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutChangePassword.performClick();
            }
        });


        relativeLayoutChangePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long delay = 0;
                if(scrollView.getScrollY() != 0){
                    scrollView.smoothScrollTo(0, 0);
                    delay = 200;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayoutPasswordChange.getVisibility() == View.GONE) {
                            linearLayoutPasswordChange.setVisibility(View.VISIBLE);
                            linearLayoutPasswordSave.setVisibility(View.VISIBLE);
                            imageViewChangePassword.setVisibility(View.GONE);
                            GAUtils.event(SIDE_MENU, USER+PROFILE, GAAction.CHANGE_PASSWORD);
                        } else {
                            linearLayoutPasswordChange.setVisibility(View.GONE);
                            imageViewChangePassword.setVisibility(View.VISIBLE);
                            linearLayoutPasswordSave.setVisibility(View.GONE);
                        }
                    }
                }, delay);
            }
        });

        linearLayoutPasswordSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                String retypeNewPassword = editTextRetypePassword.getText().toString().trim();
                if ("".equalsIgnoreCase(oldPassword)) {
                    editTextOldPassword.requestFocus();
                    editTextOldPassword.setError(getResources().getString(R.string.old_password_empty_error));
                } else if ("".equalsIgnoreCase(newPassword)) {
                    editTextNewPassword.requestFocus();
                    editTextNewPassword.setError(getResources().getString(R.string.new_password_empty_error));
                } else if ("".equalsIgnoreCase(retypeNewPassword)) {
                    editTextRetypePassword.requestFocus();
                    editTextRetypePassword.setError(getResources().getString(R.string.retype_password_empty_error));
                } else if (!newPassword.equalsIgnoreCase(retypeNewPassword)) {
                    editTextRetypePassword.requestFocus();
                    editTextRetypePassword.setError(getResources().getString(R.string.retype_password_different_error));
                } else if (newPassword.equalsIgnoreCase(oldPassword)) {
                    editTextNewPassword.requestFocus();
                    editTextNewPassword.setError(getResources().getString(R.string.new_password_same_error));
                } else if (newPassword.length() < 6) {
                    editTextNewPassword.requestFocus();
                    editTextNewPassword.setError(String.format(
                            getResources().getString(R.string.new_password_length_error_format), String.valueOf(6)));
                } else {
                    updateUserProfileChangePasswordAPI(AccountActivity.this, oldPassword, newPassword);
                }
            }
        });

        editTextOldPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextNewPassword.requestFocus();
                return true;
            }
        });

        editTextNewPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextRetypePassword.requestFocus();
                return true;
            }
        });
        editTextRetypePassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                linearLayoutPasswordSave.performClick();
                return true;
            }
        });

        editTextOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.smoothScrollTo(0, relativeLayoutChangePassword.getTop());
                }
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });
        editTextNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.smoothScrollTo(0, relativeLayoutChangePassword.getTop());
                }
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });
        editTextRetypePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.smoothScrollTo(0, relativeLayoutChangePassword.getTop());
                }
                if (v instanceof EditText) {
                    ((EditText) v).setError(null);
                }
            }
        });


        relativeLayoutEmergencyContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    Intent intent = new Intent(AccountActivity.this, EmergencyActivity.class);
                    intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                            EmergencyActivity.EmergencyActivityMode.EMERGENCY_CONTACTS.getOrdinal());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    GAUtils.event(SIDE_MENU, USER + PROFILE, ADD + GAAction.EMERGENCY_CONTACTS);
                }
            }
        });

        relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    Intent intent = new Intent(AccountActivity.this, AddPlaceActivity.class);
                    intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
                    intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, ""));
                    startActivityForResult(intent, Constants.REQUEST_CODE_ADD_HOME);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    Intent intent = new Intent(AccountActivity.this, AddPlaceActivity.class);
                    intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_WORK);
                    intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(AccountActivity.this).getString(SPLabels.ADD_WORK, ""));
                    startActivityForResult(intent, Constants.REQUEST_CODE_ADD_WORK);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        imageViewEditHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    relativeLayoutAddHome.performClick();
                }
            }
        });

        imageViewEditWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    relativeLayoutAddWork.performClick();
                }
            }
        });

        relativeLayoutAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    Intent intent = new Intent(AccountActivity.this, AddPlaceActivity.class);
                    intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                    intent.putExtra(Constants.KEY_ADDRESS, "");
                    startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        relativeLayoutAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                        openAddressBookFragment(AccountActivity.this, relativeLayoutContainer, true);
                    GAUtils.event(SIDE_MENU, USER + PROFILE, ADDRESS_BOOK);
                }
            }
        });

       /* rlJugnooStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if((Data.userData.getSubscriptionData().getSubscribedUser() != null && Data.userData.getSubscriptionData().getSubscribedUser() == 1)
                            || Data.userData.isSubscriptionActive()){
                        startActivity(new Intent(AccountActivity.this, JugnooStarSubscribedActivity.class));
                    } else {
                        startActivity(new Intent(AccountActivity.this, JugnooStarActivity.class));
                    }
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
*/


		linearLayoutLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this, "",
                            getResources().getString(R.string.are_you_sure_you_want_to_logout),
                            getResources().getString(R.string.logout), getResources().getString(R.string.cancel),
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    logoutAsync(AccountActivity.this);
                                }
                            },
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                }
                            },
                            true, false);
                    GAUtils.event(SIDE_MENU, USER + PROFILE, GAAction.LOGOUT);
                }

            }
        });

        viewTrackingLog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!editTextUserName.isEnabled()) {
                    startActivity(new Intent(AccountActivity.this, TrackingLogActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);

                }
                return false;
            }
        });


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        GAUtils.trackScreenView(PROFILE_SCREEN);

        setMenuItemsAdapter();

        getAllowedAuthChannels();

	}

	private AccountMenuItemsAdapter accountMenuItemsAdapter;
    private void setMenuItemsAdapter() {
        if(Data.userData !=null && Data.userData.getMenuInfoList()!=null && Data.userData.getMenuInfoList().size()>0){
            ArrayList<MenuInfo> itemsToShowInAccountScreen = new ArrayList<>();
            for(MenuInfo menuInfo: Data.userData.getMenuInfoList())
            {
                if(menuInfo.getShowInAccount()){
                    itemsToShowInAccountScreen.add(menuInfo);
                }
            }

            if(itemsToShowInAccountScreen.size()>0){
                rvMenuItems.setNestedScrollingEnabled(false);
                rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
                accountMenuItemsAdapter = new AccountMenuItemsAdapter(itemsToShowInAccountScreen, rvMenuItems, new AccountMenuItemsAdapter.AccountMenuItemsCallback() {
                    @Override
                    public void onMenuItemClick(MenuInfo menuInfo) {
                        if (!editTextUserName.isEnabled()) {
                            if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.WALLET.getTag())) {

                                Intent intent = new Intent(AccountActivity.this, PaymentActivity.class);
                                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
                                startActivity(intent);
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                GAUtils.event(SIDE_MENU, USER + PROFILE, WALLET);
                            } else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.JUGNOO_STAR.getTag())) {
                                try {
                                    if ((Data.userData.getSubscriptionData().getSubscribedUser() != null && Data.userData.getSubscriptionData().getSubscribedUser() == 1)
                                            || Data.userData.isSubscriptionActive()) {
                                        startActivity(new Intent(AccountActivity.this, JugnooStarSubscribedActivity.class));
                                    } else {
                                        startActivity(new Intent(AccountActivity.this, JugnooStarActivity.class));
                                    }
                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    GAUtils.event(SIDE_MENU, USER + PROFILE, INBOX);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.INBOX.getTag())) {
                                startActivity(new Intent(AccountActivity.this, NotificationCenterActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                GAUtils.event(SIDE_MENU, USER + PROFILE, JUGNOO + STAR);
                            } else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.CHANGE_LOCALE.getTag())) {
                                startActivity(new Intent(AccountActivity.this, ChangeLanguageActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                GAUtils.event(SIDE_MENU, USER + PROFILE, GAAction.CHANGE_LANGUAGE);
                            }
                        }
                    }
                },AccountActivity.this);
                rvMenuItems.setAdapter(accountMenuItemsAdapter);
                rvMenuItems.setVisibility(View.VISIBLE);
                toggleProfileOptions(true);
            }
        }
    }

    private void setPasswordVisibility(EditText editText, ImageView imageView){
        if(editText.length() > 0) {
            if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageView.setImageResource(R.drawable.ic_password_show);
            } else {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageView.setImageResource(R.drawable.ic_password_hide);
            }
            editText.setSelection(editText.length());
        }
    }

    public void onClick(View view){
        setPasswordVisibility((EditText)((ViewGroup)view.getParent()).getChildAt(0), (ImageView)view);
    }


    public void setUserData(){
		try {
			editTextUserName.setEnabled(false); editTextUserName.setBackgroundResource(R.drawable.background_white);
            editTextEmail.setEnabled(false); editTextEmail.setBackgroundResource(R.drawable.background_white);
            editTextPhone.setEnabled(false); linearLayoutPhone.setBackgroundResource(R.drawable.background_white);
            tvCountryCode.setEnabled(false); tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            editTextUserName.setError(null);
            editTextEmail.setError(null);
            editTextPhone.setError(null);

            if(!Data.userData.userName.equalsIgnoreCase("User")) {
                editTextUserName.setText(Data.userData.userName);
            } else {
                editTextUserName.setText("");
            }
            if(!Data.userData.userEmail.contains("@facebook.com") && !Data.userData.userEmail.toLowerCase().startsWith("guest")
                    && (!Data.userData.userEmail.contains("@app.jugnoo.in"))) {
                editTextEmail.setText(Data.userData.userEmail);
            } else {
                editTextEmail.setText("");
            }
			editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.phoneNo, Data.userData.getCountryCode()));
            tvCountryCode.setText(Data.userData.getCountryCode());

			try{
				if(!"".equalsIgnoreCase(Data.userData.userImage)){
                    float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
					Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform())
                            .resize((int)(160f * minRatio), (int)(160f * minRatio)).centerCrop().into(imageViewProfileImage);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void performBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            openAddressBookFragment(AccountActivity.this, relativeLayoutContainer, false);
        }
        else if (editTextUserName.isEnabled() || linearLayoutPasswordChange.getVisibility() == View.VISIBLE) {
            if(linearLayoutPasswordChange.getVisibility() == View.VISIBLE){
                relativeLayoutChangePassword.performClick();
            }
            setUserData();
            imageViewEditProfile.setVisibility(View.VISIBLE);
            imageViewEditProfileSave.setVisibility(View.GONE);
            editTextUserName.setError(null);
            editTextEmail.setError(null);
            editTextPhone.setError(null);
        } else{
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
	}


    boolean ignoreFetchWalletCheck = true;

	@Override
	protected void onResume() {
		super.onResume();
        try {
            fetchWalletBalance(this, ignoreFetchWalletCheck);
            ignoreFetchWalletCheck = false;

            HomeActivity.checkForAccessTokenChange(this);

            if(Data.userData.getSubscriptionData().getUserSubscriptions() != null && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0){
//                rlJugnooStar.setVisibility(View.VISIBLE);
                viewStarIcon.setVisibility(View.VISIBLE);
            } else{
//                rlJugnooStar.setVisibility(View.GONE);
                viewStarIcon.setVisibility(View.GONE);
            }

         /*   if(Data.userData!=null && Data.userData.getShowJugnooStarInAcccount())
               rlJugnooStar.setVisibility(View.VISIBLE);
            else
                rlJugnooStar.setVisibility(View.GONE);*/

            try {
                reloadProfileAPI(this);
                textViewEmergencyContact.setText(getResources()
                        .getString(Data.userData.getEmergencyContactsList() != null && Data.userData.getEmergencyContactsList().size() > 0 ?
                                R.string.emergency_contacts : R.string.add_emergency_contacts));
            } catch (Exception e) {
                e.printStackTrace();
            }

            scrollView.scrollTo(0, 0);
            textViewTitle.setText(getAddressBookFragment() == null ? R.string.title_my_profile : R.string.profile_saved_location_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	public void onBackPressed() {
		performBackPressed();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
		System.gc();
	}



	public void updateUserProfileAPI(final Activity activity, final String updatedName,
                                     final String updatedEmail, final String updatedPhone,
                                     final boolean phoneUpdated, final String countryCode) {
		if(MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, getString(R.string.updating));

			HashMap<String, String> params = new HashMap<>();

			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_UPDATED_USER_NAME, updatedName);
            params.put(Constants.KEY_UPDATED_USER_EMAIL, updatedEmail);
            params.put(Constants.KEY_UPDATED_PHONE_NO, updatedPhone);
            params.put(Constants.KEY_UPDATED_COUNTRY_CODE, countryCode);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().updateUserProfile(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "updateUserProfile response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {

                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                                updateMenuBar = true;
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                updateMenuBar = true;
                                Prefs.with(activity).save(Constants.SP_KNOWLARITY_MISSED_CALL_NUMBER,
                                        jObj.optString(Constants.KEY_KNOWLARITY_MISSED_CALL_NUMBER,
                                                Prefs.with(activity).getString(Constants.SP_KNOWLARITY_MISSED_CALL_NUMBER, "")));
                                Prefs.with(activity).save(Constants.SP_OTP_VIA_CALL_ENABLED,
                                        jObj.optInt(Constants.KEY_OTP_VIA_CALL_ENABLED,
                                                Prefs.with(activity).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 0)));
                                imageViewEditProfileSave.setVisibility(View.GONE);
                                imageViewEditProfile.setVisibility(View.VISIBLE);
                                toggleProfileOptions(true);
                                String message = jObj.getString("message");
                                updateSubscriptionMessage(updatedName);
                                Data.userData.userName = updatedName;
                                Data.userData.userEmail = updatedEmail;
                                if(!Data.userData.userName.equalsIgnoreCase("User")) {
                                    editTextUserName.setText(Data.userData.userName);
                                }
                                if(!Data.userData.userEmail.contains("@facebook.com") && !Data.userData.userEmail.toLowerCase().startsWith("guest")
                                        && (!Data.userData.userEmail.contains("@app.jugnoo.in"))) {
                                    editTextEmail.setText(Data.userData.userEmail);
                                }
                                if(phoneUpdated) {
                                    Intent intent = new Intent(activity, PhoneNoOTPConfirmScreen.class);
                                    intent.putExtra("phone_no_verify", updatedPhone);
                                    intent.putExtra(Constants.KEY_COUNTRY_CODE, countryCode);
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                } else{
                                    Utils.showToast(AccountActivity.this, message);
                                    performBackPressed();
//                                    reloadProfileAPI(activity);
                                }
                            } else {
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "updateUserProfile error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
		}
		else {
			DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
		}

	}



	public void reloadProfileAPI(final Activity activity) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().reloadMyProfile(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "reloadMyProfile response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.PROFILE_INFORMATION.getOrdinal() == flag) {

                                    String userName = jObj.getString("user_name");
                                    String email = jObj.getString("user_email");
                                    String phoneNo = jObj.getString("phone_no");
                                    String countryCode = jObj.optString(Constants.KEY_COUNTRY_CODE, Data.userData.getCountryCode());
                                    if(!countryCode.contains("+")){
                                        countryCode = "+"+countryCode;
                                    }
                                    int emailVerificationStatus = jObj.getInt("email_verification_status");

                                    updateSubscriptionMessage(userName);
                                    Data.userData.userName = userName;
                                    Data.userData.phoneNo = phoneNo;
                                    Data.userData.userEmail = email;
                                    Data.userData.setCountryCode(countryCode);
                                    Data.userData.emailVerificationStatus = emailVerificationStatus;

                                    setUserData();

                                    imageViewEditProfile.setVisibility(View.VISIBLE);
                                    imageViewEditProfileSave.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "reloadMyProfile error="+error.toString());
                    }
                });
            }
        }
	}





	void logoutAsync(final Activity activity) {
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, getString(R.string.please_wait));

			HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

            Log.i("access_token", "=" + Data.userData.accessToken);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().logoutUser(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.v(TAG, "logoutUser response = " + responseStr);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);

                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.AUTH_LOGOUT_FAILURE.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.AUTH_LOGOUT_SUCCESSFUL.getOrdinal() == flag) {
                                new HomeUtil().logoutFunc(activity, null);
                            } else {
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "logoutUser error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
		}
		else {
			DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
		}
	}


    private void setSavePlaces() {
        if (!Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
            String homeString = Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, "");
            SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
            textViewAddHome.setText(getResources().getString(R.string.home));
            textViewAddHomeValue.setVisibility(View.VISIBLE);
            textViewAddHomeValue.setText(searchResult.getAddress());
            imageViewEditHome.setVisibility(View.VISIBLE);
        } else{
            textViewAddHome.setText(getResources().getString(R.string.add_home));
            textViewAddHomeValue.setVisibility(View.GONE);
            imageViewEditHome.setVisibility(View.GONE);
        }

        if (!Prefs.with(AccountActivity.this).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
            String workString = Prefs.with(AccountActivity.this).getString(SPLabels.ADD_WORK, "");
            SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
            textViewAddWork.setText(getResources().getString(R.string.work));
            textViewAddWorkValue.setVisibility(View.VISIBLE);
            textViewAddWorkValue.setText(searchResult.getAddress());
            imageViewEditWork.setVisibility(View.VISIBLE);
        } else{
            textViewAddWork.setText(getResources().getString(R.string.add_work));
            textViewAddWorkValue.setVisibility(View.GONE);
            imageViewEditWork.setVisibility(View.GONE);
        }

        if(savedPlacesAdapter != null){
            savedPlacesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.hideKeyboard(AccountActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                afterDataReceived(requestCode, resultCode, data);
            }
        },300);
    }

    private void afterDataReceived(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            setSavePlaces();
            if (requestCode == FRAMEWORK_REQUEST_CODE){
                final String toastMessage;
                final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
                if (loginResult == null || loginResult.wasCancelled()) {
                    toastMessage = getString(R.string.login_cancelled);
                } else if (loginResult.getError() != null) {
                    toastMessage = loginResult.getError().getErrorType().getMessage();
                } else {
                    String authorizationCode = loginResult.getAuthorizationCode();
                    if (authorizationCode != null) {
                        toastMessage = getString(R.string.successful) + ":" + authorizationCode;
                        apiChangeContactNumberUsingFB(AccountActivity.this, loginResult.getAuthorizationCode());
                    } else {
                        toastMessage = getString(R.string.unknown_response_type);
                    }
                }
                //Toast.makeText(this,toastMessage,Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            setSavePlaces();
        }
    }


    public void updateUserProfileChangePasswordAPI(final Activity activity, final String oldPassword, final String newPassword) {
        if(MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.updating));

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_OLD_PASSWORD, oldPassword);
            params.put(Constants.KEY_NEW_PASSWORD, newPassword);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().updateUserProfile(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "updateUserProfile response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                linearLayoutPasswordSave.setVisibility(View.GONE);
                                imageViewChangePassword.setVisibility(View.VISIBLE);
                                relativeLayoutChangePassword.performClick();
                                String message = jObj.getString(Constants.KEY_MESSAGE);
                                new HomeUtil().logoutFunc(activity, null);
                            } else {
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "updateUserProfile error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }


    private TransactionUtils transactionUtils = new TransactionUtils();

    public void openAddressBookFragment(FragmentActivity activity, View container, boolean addFrag) {
        if(addFrag) {
            if(transactionUtils.checkIfFragmentAdded(activity, AddressBookFragment.class.getName())){
                activity.getSupportFragmentManager().popBackStack();
            }
            if (!transactionUtils.checkIfFragmentAdded(activity, AddressBookFragment.class.getName())) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(container.getId(), new AddressBookFragment(),
                                AddressBookFragment.class.getName())
                        .addToBackStack(AddressBookFragment.class.getName());
                if(getSupportFragmentManager().getBackStackEntryCount() > 0){
                    transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
                }
                transaction.commitAllowingStateLoss();

                textViewTitle.setText(R.string.profile_saved_location_text);
                rlMain.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
            }
        } else{
            super.onBackPressed();
            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }
    }

    public AddressBookFragment getAddressBookFragment(){
        return (AddressBookFragment) getSupportFragmentManager().findFragmentByTag(AddressBookFragment.class.getName());
    }


    public TextView getTextViewTitle(){
        return textViewTitle;
    }

    private void startFbAccountKit(PhoneNumber phoneNumber){
            onLogin(LoginType.PHONE, phoneNumber);
    }

    private interface OnCompleteListener {
        void onComplete();
    }

    private void onLogin(final LoginType loginType, PhoneNumber phoneNumber) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.CODE);
        configurationBuilder.setTheme(R.style.AppLoginTheme_Salmon);
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.LOGIN);
        if(phoneNumber != null && !phoneNumber.toString().equalsIgnoreCase("")) {
            //configurationBuilder.setInitialPhoneNumber(phoneNumber);
        }
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        AccountActivity.OnCompleteListener completeListener = new AccountActivity.OnCompleteListener() {
            @Override
            public void onComplete() {
                startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
            }
        };
        switch (loginType) {
            case EMAIL:
                final AccountActivity.OnCompleteListener getAccountsCompleteListener = completeListener;
                completeListener = new AccountActivity.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        requestPermissions(
                                android.Manifest.permission.GET_ACCOUNTS,
                                R.string.permissions_get_accounts_title,
                                R.string.permissions_get_accounts_message,
                                getAccountsCompleteListener);
                    }
                };
                break;
            case PHONE:
                if (configuration.isReceiveSMSEnabled()) {
                    final AccountActivity.OnCompleteListener receiveSMSCompleteListener = completeListener;
                    completeListener = new AccountActivity.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            requestPermissions(
                                    android.Manifest.permission.RECEIVE_SMS,
                                    R.string.permissions_receive_sms_title,
                                    R.string.permissions_receive_sms_message,
                                    receiveSMSCompleteListener);
                        }
                    };
                }
                if (configuration.isReadPhoneStateEnabled()) {
                    final AccountActivity.OnCompleteListener readPhoneStateCompleteListener = completeListener;
                    completeListener = new AccountActivity.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            requestPermissions(
                                    android.Manifest.permission.READ_PHONE_STATE,
                                    R.string.permissions_read_phone_state_title,
                                    R.string.permissions_read_phone_state_message,
                                    readPhoneStateCompleteListener);
                        }
                    };
                }
                break;
        }
        completeListener.onComplete();
    }

    private void requestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final AccountActivity.OnCompleteListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        checkRequestPermissions(
                permission,
                rationaleTitleResourceId,
                rationaleMessageResourceId,
                listener);
    }

    @TargetApi(23)
    private void checkRequestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final AccountActivity.OnCompleteListener listener) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        final int requestCode = nextPermissionsRequestCode++;
        permissionsListeners.put(requestCode, listener);

        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(rationaleTitleResourceId)
                    .setMessage(rationaleMessageResourceId)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            requestPermissions(new String[] { permission }, requestCode);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            permissionsListeners.remove(requestCode);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            requestPermissions(new String[]{ permission }, requestCode);
        }
    }

    @TargetApi(23)
    @SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        final AccountActivity.OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
        if (permissionsListener != null
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsListener.onComplete();
        }
    }

    public void apiChangeContactNumberUsingFB(final Activity activity, final String fbAccessToken) {
        if(MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.updating));

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_FB_ACCOUNT_CODE, fbAccessToken);
            params.put(Constants.KEY_ACCOUNT_KIT_VERSION, "4.19.0");

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().changeContactNumberUsingFB(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "change phone number with fb response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        String message = JSONParser.getServerMessage(jObj);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("message");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                Data.userData.phoneNo = jObj.optString("phone_no");
                                Data.userData.setCountryCode(jObj.optString(Constants.KEY_COUNTRY_CODE, Data.userData.getCountryCode()));
                                editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.phoneNo, Data.userData.getCountryCode()));
                                tvCountryCode.setText(Data.userData.getCountryCode());
                                DialogPopup.alertPopup(activity, "", message);
                            } else {
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "change phone number with fb error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }

    private ApiFetchWalletBalance apiFetchWalletBalance = null;
    private void fetchWalletBalance(final Activity activity, boolean ignoreCheck) {
        try {
            if(apiFetchWalletBalance == null){
                apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
                        updateMenuList();
                    }

                    @Override
                    public void onFailure() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onRetry(View view) {
                    }

                    @Override
                    public void onNoRetry(View view) {
                    }
                });
            }
            long lastFetchWalletBalanceCall = Prefs.with(activity).getLong(SPLabels.CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * FETCH_WALLET_BALANCE_REFRESH_TIME)));
            long lastCallDiff = System.currentTimeMillis() - lastFetchWalletBalanceCall;
            if(ignoreCheck || lastCallDiff >= FETCH_WALLET_BALANCE_REFRESH_TIME) {
                apiFetchWalletBalance.getBalance(false);
            } else {
                updateMenuList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final long FETCH_WALLET_BALANCE_REFRESH_TIME = 5 * 60 * 1000;

    private void updateMenuList(){
        if(rvMenuItems != null && rvMenuItems.getAdapter() != null) {
            rvMenuItems.getAdapter().notifyDataSetChanged();
        }
    }


    public void getAllowedAuthChannels(){
        if (MyApplication.getInstance().isOnline()) {
            if(SplashNewActivity.allowedAuthChannelsHitOnce){
                return;
            }
            HashMap<String, String> params = new HashMap<>();
            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().getAllowedAuthChannels(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    DialogPopup.dismissLoadingDialog();
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "Auth channel response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        Prefs.with(AccountActivity.this).save(Constants.KEY_LOGIN_CHANNEL, jObj.optInt(Constants.KEY_LOGIN_CHANNEL, 0));
                        setEditPhoneUI();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    SplashNewActivity.allowedAuthChannelsHitOnce = true;
                }

                @Override
                public void failure(RetrofitError error) {
                    DialogPopup.dismissLoadingDialog();
                }
            });

        }
    }

    private void setEditPhoneUI(){
        ivEditPhone.setVisibility(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1 ? View.GONE : View.VISIBLE);
    }

    private void updateSubscriptionMessage(String updatedName){
        try {
            Data.userData.getSubscriptionData().setSubscriptionTitleNew(Data.userData.
                    getSubscriptionData().getSubscriptionTitleNew()
                    .replace(Data.userData.userName, updatedName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleProfileOptions(boolean isEnable){
        textViewAddressBook.setEnabled(isEnable);
        textViewEmergencyContact.setEnabled(isEnable);
        if(accountMenuItemsAdapter!=null){
            accountMenuItemsAdapter.toggleMenuItems(isEnable);
        }

    }

    @Override
    public void onSelectCountry(Country country) {
        tvCountryCode.setText(country.getDialCode());
    }
}
