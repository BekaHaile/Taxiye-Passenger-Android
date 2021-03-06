package product.clicklabs.jugnoo;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.gson.Gson;
import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.utils.ImageCompression;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.adapters.AccountMenuItemsAdapter;
import product.clicklabs.jugnoo.adapters.GenderDropdownAdapter;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.fragments.AddressBookFragment;
import product.clicklabs.jugnoo.fragments.DocumentUploadFragment;
import product.clicklabs.jugnoo.fragments.NotificationSettingFragment;
import product.clicklabs.jugnoo.fragments.ProfileVerificationFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.dialogs.JeanieIntroDialog;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogActivity;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.DocumentData;
import product.clicklabs.jugnoo.retrofit.model.Gender;
import product.clicklabs.jugnoo.retrofit.model.GenderValues;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
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
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static product.clicklabs.jugnoo.Constants.SP_CURRENT_STATE;


public class AccountActivity extends BaseFragmentActivity implements GAAction, GACategory, OnCountryPickerListener {

    private final String TAG = "View Account";
    private static final int REQ_CODE_IMAGE_PERMISSION = 1001;
    private static final int REQUEST_CODE_SELECT_IMAGES=99;

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
    TextView  tvPreferences;
    RelativeLayout relativeLayoutAddressBook, relativeLayoutContainer;
    NonScrollListView listViewSavedLocations;
    RelativeLayout relativeLayoutAddNewAddress, rlPrivacyPreferences;
    View viewStarIcon;
    SavedPlacesAdapter savedPlacesAdapter;
    private RecyclerView rvMenuItems;
    private static final int FRAMEWORK_REQUEST_CODE = 1;

    private FBAccountKit fbAccountKit;
    private boolean setJeanieState;
    Bundle bundle = new Bundle();
    public static boolean updateMenuBar ;

    private RelativeLayout rlMain;
    private TextView tvAbout;
    private TextView textViewAddressBook;
    private CountryPicker countryPicker;
    private PermissionCommon permissionCommon;
    private Picker picker;
    private ImageCompression imageCompressionTask;

    private AppCompatSpinner spinnerGender;
    private EditText etDOB;
    private boolean isEditModeOn = false;

    private RelativeLayout relativeLayoutProfileVerification;
    private TextView textViewProfileVerification;
    private ImageView ivProfileVerifyStatus;
    private int status;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);

        permissionCommon = new PermissionCommon(this).setCallback(new PermissionCommon.PermissionListener() {
            @Override
            public void permissionGranted(int requestCode) {
                pickImages();
            }

            @Override
            public boolean permissionDenied(int requestCode, boolean neverAsk) {
                return true;
            }

            @Override
            public void onRationalRequestIntercepted(int requestCode) {

            }
        });

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
        status = Prefs.with(this).getInt(SP_CURRENT_STATE, 0);
        if(status != 0) imageViewEditProfile.setVisibility(View.GONE);
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

        relativeLayoutProfileVerification = findViewById(R.id.relativeLayoutProfileVerification);
        textViewProfileVerification = findViewById(R.id.textViewProfileVerification);
        textViewProfileVerification.setTypeface(Fonts.mavenMedium(this));
        ivProfileVerifyStatus = findViewById(R.id.ivProfileVerifyStatus);
        if (Data.autoData.getCustomerVerificationStatus() == Constants.DocStatuses.REJECTED.ordinal()) {
            ivProfileVerifyStatus.setVisibility(View.VISIBLE);
            ivProfileVerifyStatus.setImageResource(R.drawable.ic_cancel_red);
        } else if (Data.autoData.getCustomerVerificationStatus() == Constants.DocStatuses.VERIFIED.ordinal()) {
            ivProfileVerifyStatus.setVisibility(View.VISIBLE);
            ivProfileVerifyStatus.setImageResource(R.drawable.ic_checked);
        } else {
            //ivProfileVerifyStatus.setVisibility(View.GONE);
            ivProfileVerifyStatus.setVisibility(View.VISIBLE);
            ivProfileVerifyStatus.setImageResource(R.drawable.ic_info_yellow);
        }


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
            }, false, false, false, false);
            listViewSavedLocations.setAdapter(savedPlacesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutAddNewAddress = (RelativeLayout) findViewById(R.id.relativeLayoutAddNewAddress);
        ((TextView) findViewById(R.id.textViewAddNewAddress)).setTypeface(Fonts.mavenMedium(this));

        relativeLayoutAddressBook = (RelativeLayout) findViewById(R.id.relativeLayoutAddressBook);
        rlPrivacyPreferences = findViewById(R.id.rlPrivacyPreferences);
        textViewAddressBook =  ((TextView)findViewById(R.id.textViewAddressBook));
        textViewAddressBook.setTypeface(Fonts.mavenMedium(this));

        tvPreferences = findViewById(R.id.tvPreferences);
        tvPreferences.setTypeface(Fonts.mavenMedium(this));

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
        if(Data.userData != null && Data.userData.getNotificationSettingEnabled() == 1) {
            rlPrivacyPreferences.setVisibility(View.VISIBLE);
        } else {
            rlPrivacyPreferences.setVisibility(View.GONE);
        }

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
                if(Data.userData.getRegAs() == 1
                        && Prefs.with(AccountActivity.this).getInt(Constants.KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT, 0) == 1){
                    DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this,
                            Prefs.with(AccountActivity.this).getString(Constants.KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT_MESSAGE,
                                    getString(R.string.registered_as_driver_phone_number_will_be_edited)),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editPhoneFBAccountKIt();
                                }
                            });
                } else {
                    editPhoneFBAccountKIt();
                }
            }

            void editPhoneFBAccountKIt() {
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
                if(isEditModeOn) {
                    performBackPressed();
                } else {
                    imageViewEditProfileSave.performClick();
                }
            }
        });

        imageViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUserName.isEnabled()) {
                    permissionCommon.getPermission(REQ_CODE_IMAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                }
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
                        if ("".equalsIgnoreCase(emailChanged)) {
                            int random = ThreadLocalRandom.current().nextInt(1000, Integer.MAX_VALUE);
                            emailChanged = nameChanged.replace(" ", "") + String.valueOf(random) + "@emptyEmail.com";
                        }
                        String phoneNoChanged = editTextPhone.getText().toString().trim();
                        String countryCode = tvCountryCode.getText().toString();
                        if(TextUtils.isEmpty(countryCode)){
                            Utils.showToast(AccountActivity.this, getString(R.string.please_select_country_code));
                            return;
                        }
                        if(TextUtils.isEmpty(nameChanged)){
                            Utils.showToast(AccountActivity.this, getString(R.string.please_enter_name));
                            return;
                        }
                        phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged, countryCode);
                        if ("".equalsIgnoreCase(nameChanged)) {
                            editTextUserName.requestFocus();
                            editTextUserName.setError(getResources().getString(R.string.username_empty_error));
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
							if (spinnerGender.isEnabled() && Prefs.with(AccountActivity.this).getInt(Constants.KEY_CUSTOMER_GENDER_FILTER, 0) == 1
									&& selectedGenderPosition == 0) {
								Utils.showToast(AccountActivity.this, getString(R.string.please_select_gender));
								return;
							}
							if (etDOB.isEnabled() && Prefs.with(AccountActivity.this).getInt(Constants.KEY_CUSTOMER_DOB_INPUT, 0) == 1
									&& etDOB.getText().toString().isEmpty()) {
								Utils.showToast(AccountActivity.this, getString(R.string.please_enter_date_of_birth));
								return;
							}
                        	String finalPhoneNoChanged = phoneNoChanged;
                            boolean phoneEdited = !Data.userData.phoneNo.equalsIgnoreCase(countryCode + finalPhoneNoChanged);
                            if(phoneEdited && Data.userData.getRegAs() == 1
                                    && Prefs.with(AccountActivity.this).getInt(Constants.KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT, 0) == 1){
                                String finalEmailChanged = emailChanged;
                                DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this,
                                        Prefs.with(AccountActivity.this).getString(Constants.KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT_MESSAGE,
                                                getString(R.string.registered_as_driver_phone_number_will_be_edited)),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                updateUserProfileAPI(AccountActivity.this, Utils.capEachWord(nameChanged), finalEmailChanged, countryCode + finalPhoneNoChanged,
                                                        phoneEdited, countryCode);
                                            }
                                        });
                            } else {
                                updateUserProfileAPI(AccountActivity.this, Utils.capEachWord(nameChanged), emailChanged, countryCode + phoneNoChanged,
                                        phoneEdited, countryCode);
                            }
                        }
                    } else {
                        toggleProfileOptions(false);
                        editTextUserName.requestFocus();
                        editTextUserName.setSelection(editTextUserName.getText().length());
                        editTextUserName.setEnabled(true);
                        editTextUserName.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        editTextEmail.setEnabled(true);
                        editTextEmail.setBackgroundResource(R.drawable.bg_white_orange_bb);
//                        if(Data.userData.getGender() == 0) {
                            spinnerGender.setEnabled(true);
                            spinnerGender.setBackgroundResource(R.drawable.bg_white_orange_bb);
//                        }
                        if(TextUtils.isEmpty(Data.userData.getDateOfBirth())) {
                            etDOB.setEnabled(true);
                            etDOB.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        }
                        editTextPhone.setEnabled(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1);
                        tvCountryCode.setEnabled(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1);
                        if(Prefs.with(AccountActivity.this).getInt(Constants.KEY_LOGIN_CHANNEL, 0) == 1) {
                            linearLayoutPhone.setBackgroundResource(R.drawable.bg_white_orange_bb);
                            tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0);
                        }
                        //buttonEditProfile.setText(getResources().getString(R.string.save_changes));
                        imageViewEditProfile.setVisibility(View.GONE);
                        isEditModeOn = true;
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

        rlPrivacyPreferences.setOnClickListener(v -> {
            if (!isEditModeOn) {
                openPrivacyPreferencesFragment(AccountActivity.this, relativeLayoutContainer, true);
                GAUtils.event(GACategory.SIDE_MENU, USER + PROFILE, PRIVACY_PREFERENCES);
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

        spinnerGender = findViewById(R.id.spinnerGender); spinnerGender.setVisibility(View.GONE);
        etDOB = findViewById(R.id.etDOB); etDOB.setVisibility(View.GONE);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGenderPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(new Gender(0, getString(R.string.select_gender)));
        genders.add(new Gender(GenderValues.MALE.getType(), getString(R.string.male)));
        genders.add(new Gender(GenderValues.FEMALE.getType(), getString(R.string.female)));

        ArrayAdapter<Gender> adapter = new GenderDropdownAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders, selectedGenderPosition);
        spinnerGender.setAdapter(adapter);
        etDOB.setOnClickListener(view -> openDatePicker());
        etDOB.setTypeface(Fonts.mavenMedium(this));

        if(Prefs.with(this).getInt(Constants.SHOW_CUSTOMER_VERIFICATION,0)==1)
            relativeLayoutProfileVerification.setVisibility(View.VISIBLE);
        else
            relativeLayoutProfileVerification.setVisibility(View.GONE);

        relativeLayoutProfileVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileVerificationFragment(AccountActivity.this,relativeLayoutContainer,true);
            }
        });

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        GAUtils.trackScreenView(PROFILE_SCREEN);

        setMenuItemsAdapter();

        getAllowedAuthChannels();
        /* check and Show the profile fragment */
        if (getIntent().hasExtra(ProfileVerificationFragment.class.getSimpleName())) {
            if (getIntent().getBooleanExtra(ProfileVerificationFragment.class.getSimpleName(), false)) {
                openProfileVerificationFragment(AccountActivity.this, relativeLayoutContainer, true);
            }
        }
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
                            } else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.CALL_SUPPORT.getTag())) {
                                Utils.openCallIntent(AccountActivity.this,
                                        Prefs.with(AccountActivity.this).getString(Constants.KEY_CUSTOMER_SUPPORT_NUMBER, ""));
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

    private void openPrivacyPreferencesFragment(FragmentActivity activity, View container, boolean addFrag) {
        if (addFrag) {
            if (transactionUtils.checkIfFragmentAdded(activity, NotificationSettingFragment.class.getName())) {
                activity.getSupportFragmentManager().popBackStack();
            }
            if (!transactionUtils.checkIfFragmentAdded(activity, NotificationSettingFragment.class.getName())) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(container.getId(), NotificationSettingFragment.newInstance(true),
                                NotificationSettingFragment.class.getName())
                        .addToBackStack(NotificationSettingFragment.class.getName());
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
                }
                transaction.commitAllowingStateLoss();

                textViewTitle.setText(R.string.verification_status);
                rlMain.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }

//        NotificationSettingFragment fragmentPreferences = getPrivacyPreferencesFragment();
//        if(fragmentPreferences == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                    .add(relativeLayoutContainer.getId(),
//                            NotificationSettingFragment.newInstance(true),
//                            NotificationSettingFragment.class.getName())
//                    .commitAllowingStateLoss();
//        }
//        textViewTitle.setText(R.string.privacy_preferences);
//        rlMain.setVisibility(View.GONE);
//        tvAbout.setVisibility(View.GONE);
    }

    public NotificationSettingFragment getPrivacyPreferencesFragment(){
        return (NotificationSettingFragment) getSupportFragmentManager().findFragmentByTag(NotificationSettingFragment.class.getName());
    }

    private void removePrivacyPreferencesFragment() {
        NotificationSettingFragment fragmentPrivacyPref = getPrivacyPreferencesFragment();
        if (fragmentPrivacyPref != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragmentPrivacyPref)
                    .commitAllowingStateLoss();

            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }
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
            spinnerGender.setEnabled(false); spinnerGender.setBackgroundResource(R.drawable.background_white);
            etDOB.setEnabled(false); etDOB.setBackgroundResource(R.drawable.background_white);

            selectedGenderPosition = Data.userData.getGender();
            spinnerGender.setSelection(Data.userData.getGender());

            if(!TextUtils.isEmpty(Data.userData.getDateOfBirth())) {
                etDOB.setText(DateOperations.utcToLocalWithTZFallback(Data.userData.getDateOfBirth()).split(" ")[0]);
            } else {
                etDOB.setText("");
            }

            spinnerGender.setVisibility(Prefs.with(this).getInt(Constants.KEY_CUSTOMER_GENDER_FILTER, 0) == 0 ? View.GONE : View.VISIBLE);
            etDOB.setVisibility(Prefs.with(this).getInt(Constants.KEY_CUSTOMER_DOB_INPUT, 0) == 0 ? View.GONE : View.VISIBLE);


            setUserNameToFields();
            if(!Data.userData.userEmail.contains("@facebook.com") && !Data.userData.userEmail.toLowerCase().startsWith("guest")
                    && (!Data.userData.userEmail.contains("@app.jugnoo.in"))
                    && (!Data.userData.userEmail.endsWith("@email.com")) && (!Data.userData.userEmail.endsWith("@emptyEmail.com"))) {
                editTextEmail.setText(Data.userData.userEmail);
            } else {
                editTextEmail.setText("");
            }
			editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.phoneNo, Data.userData.getCountryCode()));
            tvCountryCode.setText(Data.userData.getCountryCode());

            setUserImage();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void setUserImage() {
        try {
            if (!"".equalsIgnoreCase(Data.userData.userImage)) {
                float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform())
                        .resize((int) (160f * minRatio), (int) (160f * minRatio)).centerCrop().into(imageViewProfileImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserNameToFields() {
        if(!Data.userData.userName.equalsIgnoreCase("User")) {
            editTextUserName.setText(Data.userData.userName);
		} else {
			editTextUserName.setText("");
		}
    }


    public void performBackPressed(){
//		AddressBookFragment fragmentAB = getAddressBookFragment();
//		NotificationSettingFragment fragmentPrivacyPref = getPrivacyPreferencesFragment();
//		if (fragmentAB != null) {
//            removeAddressBookFragment();
//        }
//		else if (fragmentPrivacyPref != null) {
//		    removePrivacyPreferencesFragment();
//        }
//		else
		    if(getSupportFragmentManager().getBackStackEntryCount() > 0){
			if(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().equals(AddressBookFragment.class.getName())) {
				openAddressBookFragment(AccountActivity.this, relativeLayoutContainer, false);
			} else if(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().equals(ProfileVerificationFragment.class.getName())){
				openProfileVerificationFragment(AccountActivity.this,relativeLayoutContainer,false);
			}else if(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().equals(NotificationSettingFragment.class.getName())){
                openPrivacyPreferencesFragment(AccountActivity.this,relativeLayoutContainer,false);
			} else if(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().equals(DocumentUploadFragment.class.getName())){
				textViewTitle.setText(R.string.verification_status);
				getSupportFragmentManager().popBackStack();
			} else {
				getSupportFragmentManager().popBackStack();
			}
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
                if(!isEditModeOn) {
                    reloadProfileAPI(this);
                }
                textViewEmergencyContact.setText(getResources()
                        .getString(Data.userData.getEmergencyContactsList() != null && Data.userData.getEmergencyContactsList().size() > 0 ?
                                R.string.emergency_contacts : R.string.add_emergency_contacts));
            } catch (Exception e) {
                e.printStackTrace();
            }

            scrollView.scrollTo(0, 0);
            if (!getIntent().hasExtra(ProfileVerificationFragment.class.getSimpleName())) {
                textViewTitle.setText(getAddressBookFragment() == null ? getPrivacyPreferencesFragment() == null
                    ? R.string.title_my_profile : R.string.privacy_preferences : R.string.profile_saved_location_text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && getSupportFragmentManager().findFragmentByTag(ProfileVerificationFragment.class.getName()) instanceof ProfileVerificationFragment) {
            ProfileVerificationFragment fragment = (ProfileVerificationFragment) getSupportFragmentManager().findFragmentByTag(ProfileVerificationFragment.class.getName());
            if (!isFinishing() && fragment != null && fragment.isAdded()) {
                textViewTitle.setText(R.string.verification_status);
            } else {
                textViewTitle.setText(getAddressBookFragment() == null ? R.string.title_my_profile : R.string.profile_saved_location_text);
            }
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && getSupportFragmentManager().findFragmentByTag(DocumentUploadFragment.class.getName()) instanceof DocumentUploadFragment) {
            DocumentUploadFragment fragment = (DocumentUploadFragment) getSupportFragmentManager().findFragmentByTag(DocumentUploadFragment.class.getName());
            if (!isFinishing() && fragment != null && fragment.isAdded()) {
                if (documentData != null) {
                    textViewTitle.setText(documentData.getDocumentName());
                }
            }
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

            if(selectedGenderPosition != Data.userData.getGender()){
                params.put(Constants.KEY_GENDER, String.valueOf(selectedGenderPosition));
            }
            if(TextUtils.isEmpty(Data.userData.getDateOfBirth()) && !etDOB.getText().toString().equalsIgnoreCase(Data.userData.getDateOfBirth())){
                params.put(Constants.KEY_DATE_OF_BIRTH, etDOB.getText().toString());
            }

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
                                isEditModeOn = false;
                                toggleProfileOptions(true);
                                String message = jObj.getString("message");
                                updateSubscriptionMessage(updatedName);
                                Data.userData.userName = updatedName;
                                Data.userData.userEmail = updatedEmail;
                                Data.userData.setGender(selectedGenderPosition);
                                if(Data.userData.getGender() == 0){
                                    Data.userData.setGender(selectedGenderPosition);
                                }
                                if(TextUtils.isEmpty(Data.userData.getDateOfBirth())){
                                    Data.userData.setDateOfBirth(etDOB.getText().toString());
                                }

                                setUserNameToFields();
                                if(!Data.userData.userEmail.contains("@facebook.com") && !Data.userData.userEmail.toLowerCase().startsWith("guest")
                                        && (!Data.userData.userEmail.contains("@app.jugnoo.in")) && (!Data.userData.userEmail.endsWith("@emptyEmail.com"))) {
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

	private void updateUserProfileImage(final MultipartTypedOutput params){
        params.addPart(Constants.KEY_CLIENT_ID, new TypedString(Config.getAutosClientId()));
        new ApiCommon<SettleUserDebt>(this).showLoader(true).execute(params, ApiName.UPDATE_USER_PROFILE_MULTIPART, new APICommonCallback<SettleUserDebt>() {
            @Override
            public void onSuccess(SettleUserDebt settleUserDebt, String message, int flag) {
                Utils.showToast(AccountActivity.this, message);
                reloadProfileAPI(AccountActivity.this);
                updateMenuBar = true;
            }

            @Override
            public boolean onError(SettleUserDebt settleUserDebt, String message, int flag) {
                return false;
            }
        });
    }



	public void reloadProfileAPI(final Activity activity) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

                DialogPopup.showLoadingDialog(this, "Loading...");
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
                                    String userImage = jObj.optString("user_image", Data.userData.userImage);
                                    String countryCode = jObj.optString(Constants.KEY_COUNTRY_CODE, Data.userData.getCountryCode());
                                    if(!countryCode.contains("+")){
                                        countryCode = "+"+countryCode;
                                    }
                                    int emailVerificationStatus = jObj.getInt("email_verification_status");

                                    updateSubscriptionMessage(userName);
                                    Data.userData.userName = userName;
                                    Data.userData.phoneNo = phoneNo;
                                    Data.userData.userEmail = email;
                                    Data.userData.userImage = userImage;
                                    Data.userData.setCountryCode(countryCode);
                                    Data.userData.emailVerificationStatus = emailVerificationStatus;
                                    if(Data.userData != null) {
                                        Data.userData.setGender(jObj.optInt(Constants.KEY_GENDER, Data.userData.getGender()));
                                        Data.userData.setDateOfBirth(jObj.optString(Constants.KEY_DATE_OF_BIRTH, Data.userData.getDateOfBirth()));
                                    }

                                    setUserData();

                                    if(status == 0)
                                    imageViewEditProfile.setVisibility(View.VISIBLE);
                                    imageViewEditProfileSave.setVisibility(View.GONE);
                                    isEditModeOn = false;
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "reloadMyProfile error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                    }
                });
            }
        }
	}





	public void logoutAsync(final Activity activity) {
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
        if(requestCode== REQUEST_CODE_SELECT_IMAGES && resultCode==RESULT_OK){
            compressImageAndUpload(data);
        }
    }

    private void compressImageAndUpload(Intent data) {
        if(data!=null && data.getSerializableExtra("imagesList")!=null) {
			ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
			if (images != null && images.size() != 0) {
				final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

				//Compress Images if any new added
				ArrayList<String> imageEntries =null;
				for(Object image:images){
					if(image instanceof ImageEntry){
						if(imageEntries==null)
							imageEntries= new ArrayList<>();

						imageEntries.add(((ImageEntry) image).path);
					}
				}

				if(imageEntries!=null){
					//upload feedback with new Images
						imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
							@Override
							public void processFinish(ImageCompression.CompressedImageModel[] output) {

								if (output != null) {
									for (ImageCompression.CompressedImageModel file : output) {
										if (file != null) {
											multipartTypedOutput.addPart(Constants.KEY_UPDATED_USER_IMAGE, new TypedFile("image/*", file.getFile()));
										}
									}

								}
								//place order with images
								updateUserProfileImage(multipartTypedOutput);
							}

							@Override
							public void onError() {
								DialogPopup.dismissLoadingDialog();

							}
						}, this);
					imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
				}
			}
		}
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
        if (addFrag) {
            if (transactionUtils.checkIfFragmentAdded(activity, AddressBookFragment.class.getName())) {
                activity.getSupportFragmentManager().popBackStack();
            }
            if (!transactionUtils.checkIfFragmentAdded(activity, AddressBookFragment.class.getName())) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(container.getId(), new AddressBookFragment(),
                                AddressBookFragment.class.getName())
                        .addToBackStack(AddressBookFragment.class.getName());
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
                }
                transaction.commitAllowingStateLoss();

                textViewTitle.setText(R.string.profile_saved_location_text);
                rlMain.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void removeAddressBookFragment() {
        AddressBookFragment fragmentAB = getAddressBookFragment();
        if (fragmentAB != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragmentAB)
                    .commitAllowingStateLoss();

            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }
    }


    public AddressBookFragment getAddressBookFragment() {
        return (AddressBookFragment) getSupportFragmentManager().findFragmentByTag(AddressBookFragment.class.getName());
    }


    public TextView getTextViewTitle(){
        return textViewTitle;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void pickImages() {
        if(picker == null){
            picker = new Picker.Builder(this, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.SINGLE_IMAGE).build();
        }
        picker.setLimit(1);
        picker.startActivity(this,REQUEST_CODE_SELECT_IMAGES);

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

                        JSONParser.allowedAuthChannelTimeConfigVariables(AccountActivity.this, jObj);

                        setEditPhoneUI();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    SplashNewActivity.allowedAuthChannelsHitOnce = false;
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
        tvPreferences.setEnabled(isEnable);
        textViewEmergencyContact.setEnabled(isEnable);
        if(accountMenuItemsAdapter!=null){
            accountMenuItemsAdapter.toggleMenuItems(isEnable);
        }

    }

    @Override
    public void onSelectCountry(Country country) {
        tvCountryCode.setText(country.getDialCode());
    }

    private int selectedGenderPosition = 0;
    private Calendar calendar = Calendar.getInstance();
    private void openDatePicker(){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
            if(calendar != null){
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etDOB.setText(new SimpleDateFormat(Constants.DOB_DATE_FORMAT, Locale.ENGLISH).format(calendar.getTime()));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, onDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate((long) (System.currentTimeMillis() - (3.154e+12)));
        datePickerDialog.show();

    }

    public void openProfileVerificationFragment(FragmentActivity activity, View container, boolean addFrag) {
        if (addFrag) {
            if (transactionUtils.checkIfFragmentAdded(activity, ProfileVerificationFragment.class.getName())) {
                activity.getSupportFragmentManager().popBackStack();
            }
            if (!transactionUtils.checkIfFragmentAdded(activity, ProfileVerificationFragment.class.getName())) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(container.getId(), ProfileVerificationFragment.Companion.newInstance(),
                                ProfileVerificationFragment.class.getName())
                        .addToBackStack(ProfileVerificationFragment.class.getName());
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
                }
                transaction.commitAllowingStateLoss();

                textViewTitle.setText(R.string.verification_status);
                rlMain.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
            textViewTitle.setText(R.string.title_my_profile);
            rlMain.setVisibility(View.VISIBLE);
            tvAbout.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);
        }
    }

    private DocumentData documentData = null;

    public void openDocumentUploadFragment(DocumentData documentData) {
        this.documentData = documentData;
        if (transactionUtils.checkIfFragmentAdded(this, ProfileVerificationFragment.class.getName())) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .add(relativeLayoutContainer.getId(), DocumentUploadFragment.Companion.newInstance(documentData),
                            DocumentUploadFragment.class.getName())
                    .addToBackStack(DocumentUploadFragment.class.getName());
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                transaction.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
            }
            transaction.commitAllowingStateLoss();
            //textViewTitle.setText(R.string.profile_verification);
            textViewTitle.setText(documentData.getDocumentName());
        }
    }
}
