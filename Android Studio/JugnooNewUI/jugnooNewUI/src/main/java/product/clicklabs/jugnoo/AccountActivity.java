package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.json.JSONObject;

import java.util.HashMap;

import io.branch.referral.Branch;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.dialogs.JeanieIntroDialog;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class AccountActivity extends BaseActivity implements FlurryEventNames, FirebaseEvents {

    private final String TAG = "View Account";

	LinearLayout relative;

	private TextView textViewSave, textViewPasswordSave;
	ImageView imageViewBack;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

    ImageView imageViewProfileImage;
	EditText editTextUserName, editTextEmail, editTextPhone;
    LinearLayout linearLayoutPhone;
    ImageView imageViewEditProfile;

    RelativeLayout relativeLayoutChangePassword, relativeLayoutEmergencyContact;
    TextView textViewEmergencyContact;
    LinearLayout linearLayoutPasswordChange;
    RelativeLayout relativeLayoutOldPassword, relativeLayoutNewPassword, relativeLayoutRetypePassword;
    EditText editTextOldPassword, editTextNewPassword, editTextRetypePassword;
    ImageView imageViewChangePassword, imaveViewOldPasswordVisibility, imaveViewNewPasswordVisibility, imaveViewRetypePasswordVisibility;

    LinearLayout linearLayoutLogout, linearLayoutAbout;

	ImageView imageViewEditHome, imageViewEditWork, imageViewJugnooJeanie, imageViewPokemon, imageViewFAB, imageViewFABQuestion;
	RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork, relativeLayoutJugnooJeanie;
    LinearLayout relativeLayoutPokemon, relativeLayoutFAB;
	TextView textViewAddHome, textViewAddHomeValue, textViewAddWork, textViewAddWorkValue, textViewJugnooJeanie, textViewPokemon, textViewFAB;
    private LinearLayout linearLayoutSave, linearLayoutPasswordSave;

    private boolean setJeanieState;
    public static final int ADD_HOME = 2, ADD_WORK = 3;
    Bundle bundle = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

        textViewSave = (TextView) findViewById(R.id.textViewSave); textViewSave.setTypeface(Fonts.mavenMedium(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

        imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);

		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Fonts.mavenMedium(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Fonts.mavenMedium(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Fonts.mavenMedium(this));
        linearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayoutPhone);
        imageViewEditProfile = (ImageView) findViewById(R.id.imageViewEditProfile);
        ((TextView)findViewById(R.id.textViewPhone91)).setTypeface(Fonts.mavenMedium(this));
        linearLayoutSave = (LinearLayout)findViewById(R.id.linearLayoutSave);
        textViewPasswordSave = (TextView) findViewById(R.id.textViewPasswordSave); textViewPasswordSave.setTypeface(Fonts.mavenMedium(this));
        linearLayoutPasswordSave = (LinearLayout) findViewById(R.id.linearLayoutPasswordSave);

        relativeLayoutChangePassword = (RelativeLayout) findViewById(R.id.relativeLayoutChangePassword);
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
        imageViewJugnooJeanie = (ImageView)findViewById(R.id.imageViewJugnooJeanie);
        relativeLayoutJugnooJeanie.setVisibility(View.GONE);
        if(Prefs.with(AccountActivity.this).getInt(SPLabels.SHOW_JUGNOO_JEANIE, 0) == 1){
            relativeLayoutJugnooJeanie.setVisibility(View.VISIBLE);
        }


        //textViewPokemon, imageViewPokemon
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
        imageViewFABQuestion = (ImageView)findViewById(R.id.imageViewFABQuestion);
        imageViewFAB = (ImageView)findViewById(R.id.imageViewFAB);
        relativeLayoutFAB.setVisibility(View.GONE);
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

        imageViewPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Prefs.with(AccountActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                    imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_off);
                    Prefs.with(AccountActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 0);
                } else {
                    imageViewPokemon.setImageResource(R.drawable.jugnoo_sticky_on);
                    Prefs.with(AccountActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 1);
                }
            }
        });

        imageViewFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Prefs.with(AccountActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_off);
                    Prefs.with(AccountActivity.this).save(Constants.FAB_ENABLED_BY_USER, 0);
                } else {
                    imageViewFAB.setImageResource(R.drawable.jugnoo_sticky_on);
                    Prefs.with(AccountActivity.this).save(Constants.FAB_ENABLED_BY_USER, 1);
                }
            }
        });

        imageViewFABQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JeanieIntroDialog(AccountActivity.this).show();
            }
        });


        linearLayoutLogout = (LinearLayout) findViewById(R.id.linearLayoutLogout);
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

        linearLayoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
            }
        });




        imageViewJugnooJeanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccessibilitySettingsOn(getApplicationContext())) {
                    if (Prefs.with(AccountActivity.this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == false) {
                        Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, true);
                        imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_on);
                    } else {
                        Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, false);
                        imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_off);
                    }
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    setJeanieState = true;
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

        imageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutSave.performClick();
            }
        });

        linearLayoutSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    editTextUserName.setError(null);
                    editTextEmail.setError(null);
                    editTextPhone.setError(null);
                    if (editTextUserName.isEnabled()) {
                        String nameChanged = editTextUserName.getText().toString().trim().toUpperCase();
                        String emailChanged = editTextEmail.getText().toString().trim();
                        String phoneNoChanged = editTextPhone.getText().toString().trim();
                        phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged);
                        if ("".equalsIgnoreCase(nameChanged)) {
                            editTextUserName.requestFocus();
                            editTextUserName.setError(getResources().getString(R.string.username_empty_error));
                        } else if ("".equalsIgnoreCase(emailChanged)) {
                            editTextEmail.requestFocus();
                            editTextEmail.setError(getResources().getString(R.string.email_empty_error));
                        } else if ("".equalsIgnoreCase(phoneNoChanged)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError(getResources().getString(R.string.phone_empty_error));
                        } else if (!Utils.validPhoneNumber(phoneNoChanged)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError(getResources().getString(R.string.invalid_phone_error));
                        } else if (Data.userData.userName.equalsIgnoreCase(nameChanged)
                                && Data.userData.userEmail.equalsIgnoreCase(emailChanged)
                                && Data.userData.phoneNo.equalsIgnoreCase("+91" + phoneNoChanged)) {
                            editTextUserName.requestFocus();
                            editTextUserName.setError(getResources().getString(R.string.nothing_changed));
                        } else {
                            updateUserProfileAPI(AccountActivity.this, nameChanged, emailChanged, "+91" + phoneNoChanged,
                                    !Data.userData.phoneNo.equalsIgnoreCase("+91" + phoneNoChanged));
                            MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+EDIT_PHONE_NUMBER, bundle);
                            FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "edit phone number");
                        }
                    } else {
                        editTextUserName.requestFocus();
                        editTextUserName.setSelection(editTextUserName.getText().length());
                        editTextUserName.setEnabled(true);
                        editTextUserName.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        editTextEmail.setEnabled(true);
                        editTextEmail.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        editTextPhone.setEnabled(true);
                        linearLayoutPhone.setBackgroundResource(R.drawable.bg_white_orange_bb);
                        //buttonEditProfile.setText(getResources().getString(R.string.save_changes));
                        imageViewEditProfile.setVisibility(View.GONE);
                        linearLayoutSave.setVisibility(View.VISIBLE);
                        Utils.showSoftKeyboard(AccountActivity.this, editTextUserName);
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
                            MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+CHANGE_PASSWORD, bundle);
                            FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Change Password");
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
                            getResources().getString(R.string.new_password_length_error_format), 6));
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
                Intent intent = new Intent(AccountActivity.this, EmergencyActivity.class);
                intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                        EmergencyActivity.EmergencyActivityMode.EMERGENCY_CONTACTS.getOrdinal());
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(AccountActivity.this, CLICKS_ON_EMERGENCY_CONTACTS);
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+EMERGENCY_CONTACTS, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Emergency contacts");
            }
        });

        relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, AddPlaceActivity.class);
                intent.putExtra("requestCode", "HOME");
                intent.putExtra("address", Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, ""));
                startActivityForResult(intent, ADD_HOME);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(AccountActivity.this, HOW_MANY_USERS_ADDED_ADD_HOME);
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+FirebaseEvents.ADD_HOME, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Add Home");
            }
        });

        relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountActivity.this, AddPlaceActivity.class);
                intent.putExtra("requestCode", "WORK");
                intent.putExtra("address", Prefs.with(AccountActivity.this).getString(SPLabels.ADD_WORK, ""));
                startActivityForResult(intent, ADD_WORK);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(AccountActivity.this, HOW_MANY_USERS_ADDED_ADD_WORK);
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+FirebaseEvents.ADD_WORK, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Add Work");
            }
        });

        imageViewEditHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutAddHome.performClick();
            }
        });

        imageViewEditWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutAddWork.performClick();
            }
        });


		linearLayoutLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this, "",
                        getResources().getString(R.string.are_you_sure_you_want_to_logout),
                        getResources().getString(R.string.logout), getResources().getString(R.string.cancel),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                FlurryEventLogger.event(AccountActivity.this, CLICKS_ON_LOGOUT);
                                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+LOGOUT, bundle);
                                logoutAsync(AccountActivity.this);
                            }
                        },
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                            }
                        },
                        true, false);

            }
        });


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


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

			editTextUserName.setText(Data.userData.userName.toUpperCase());
			editTextEmail.setText(Data.userData.userEmail);
			editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.phoneNo));

			try{
				if(!"".equalsIgnoreCase(Data.userData.userImage)){
					Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfileImage);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void performBackPressed(){
        FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Back");
        MyApplication.getInstance().logEvent(INFORMATIVE+"_"+VIEW_ACCOUNT+"_"+BACK, bundle);
        if (editTextUserName.isEnabled() || linearLayoutPasswordChange.getVisibility() == View.VISIBLE) {
            if(linearLayoutPasswordChange.getVisibility() == View.VISIBLE){
                relativeLayoutChangePassword.performClick();
            }
            setUserData();
            imageViewEditProfile.setVisibility(View.VISIBLE);
            linearLayoutSave.setVisibility(View.GONE);
            editTextUserName.setError(null);
            editTextEmail.setError(null);
            editTextPhone.setError(null);
        } else{
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
	}



	@Override
	protected void onResume() {
		super.onResume();
        if(setJeanieState){
            setJeanieState = false;
            if(isAccessibilitySettingsOn(getApplicationContext())){
                Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, true);
                imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_on);
            }else{ //((!isAccessibilitySettingsOn(getApplicationContext()) && (Prefs.with(AccountActivity.this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == false))) {
                Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, false);
                imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_off);
            }
        }else{
            if((isAccessibilitySettingsOn(getApplicationContext())
                    && (Prefs.with(AccountActivity.this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == true))){
                Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, true);
                imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_on);
            }else{ //((!isAccessibilitySettingsOn(getApplicationContext()) && (Prefs.with(AccountActivity.this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == false))) {
                Prefs.with(AccountActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, false);
                imageViewJugnooJeanie.setImageResource(R.drawable.jugnoo_sticky_off);
            }
        }

		HomeActivity.checkForAccessTokenChange(this);

		reloadProfileAPI(this);
        textViewEmergencyContact.setText(getResources()
                .getString(Data.userData.getEmergencyContactsList() != null && Data.userData.getEmergencyContactsList().size() > 0 ?
                        R.string.emergency_contacts : R.string.add_emergency_contacts));

		scrollView.scrollTo(0, 0);
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
                                     final boolean phoneUpdated) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Updating...");

			HashMap<String, String> params = new HashMap<>();

			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_UPDATED_USER_NAME, updatedName);
            params.put(Constants.KEY_UPDATED_USER_EMAIL, updatedEmail);
            params.put(Constants.KEY_UPDATED_PHONE_NO, updatedPhone);

            RestClient.getApiServices().updateUserProfile(params, new Callback<SettleUserDebt>() {
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
                                linearLayoutSave.setVisibility(View.GONE);
                                imageViewEditProfile.setVisibility(View.VISIBLE);
                                String message = jObj.getString("message");
                                Data.userData.userName = updatedName;
                                Data.userData.userEmail = updatedEmail;
                                editTextUserName.setText(Data.userData.userName.toUpperCase());
                                editTextEmail.setText(Data.userData.userEmail);
                                if(phoneUpdated) {
                                    Intent intent = new Intent(activity, PhoneNoOTPConfirmScreen.class);
                                    intent.putExtra("phone_no_verify", updatedPhone);
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                } else{
                                    DialogPopup.alertPopup(activity, "", message);
                                    reloadProfileAPI(activity);
                                }
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "updateUserProfile error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}



	public void reloadProfileAPI(final Activity activity) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (AppStatus.getInstance(activity).isOnline(activity)) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

                RestClient.getApiServices().reloadMyProfile(params, new Callback<SettleUserDebt>() {
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
                                    int emailVerificationStatus = jObj.getInt("email_verification_status");

                                    Data.userData.userName = userName;
                                    Data.userData.phoneNo = phoneNo;
                                    Data.userData.userEmail = email;
                                    Data.userData.emailVerificationStatus = emailVerificationStatus;

                                    setUserData();
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




	public void sendEmailVerifyLink(final Activity activity) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Updating...");

			HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

            RestClient.getApiServices().sendVerifyEmailLink(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "sendVerifyEmailLink response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.dialogBanner(activity, error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                String message = jObj.getString("message");
                                DialogPopup.dialogBanner(activity, message);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "sendVerifyEmailLink error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}


	void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, "Please Wait ...");

			HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

            Log.i("access_token", "=" + Data.userData.accessToken);

            RestClient.getApiServices().logoutUser(params, new Callback<SettleUserDebt>() {
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
                                logoutFunc(activity, null);
                                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Logout");
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "logoutUser error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}

    private void logoutFunc(Activity activity, String message){
        try {
            PicassoTools.clearCache(Picasso.with(activity));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FacebookLoginHelper.logoutFacebook();

        GCMIntentService.clearNotifications(activity);

        Data.clearDataOnLogout(activity);

        HomeActivity.userMode = UserMode.PASSENGER;
        HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;

        ActivityCompat.finishAffinity(activity);
        Intent intent = new Intent(activity, SplashNewActivity.class);
        if(message != null){
            intent.putExtra(Constants.KEY_LOGGED_OUT, 1);
            intent.putExtra(Constants.KEY_MESSAGE, message);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        Branch.getInstance(activity).logout();
    }


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "product.clicklabs.jugnoo/product.clicklabs.jugnoo.sticky.WindowChangeDetectingService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("Jugnoo Jeanie", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Jugnoo Jeanie", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("Jugnoo Jeanie", "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v("Jugnoo Jeanie", "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v("Jugnoo Jeanie", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("Jugnoo Jeanie", "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    private void setSavePlaces() {
        if (!Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
            String homeString = Prefs.with(AccountActivity.this).getString(SPLabels.ADD_HOME, "");
            SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(homeString);
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
            SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(workString);
            textViewAddWork.setText(getResources().getString(R.string.work));
            textViewAddWorkValue.setVisibility(View.VISIBLE);
            textViewAddWorkValue.setText(searchResult.getAddress());
            imageViewEditWork.setVisibility(View.VISIBLE);
        } else{
            textViewAddWork.setText(getResources().getString(R.string.add_work));
            textViewAddWorkValue.setVisibility(View.GONE);
            imageViewEditWork.setVisibility(View.GONE);
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setSavePlaces();
        } else if (resultCode == RESULT_CANCELED) {
            setSavePlaces();
        }
    }


    public void updateUserProfileChangePasswordAPI(final Activity activity, final String oldPassword, final String newPassword) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Updating...");

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_OLD_PASSWORD, oldPassword);
            params.put(Constants.KEY_NEW_PASSWORD, newPassword);

            RestClient.getApiServices().updateUserProfile(params, new Callback<SettleUserDebt>() {
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
                                logoutFunc(activity, message);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "updateUserProfile error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }

}
