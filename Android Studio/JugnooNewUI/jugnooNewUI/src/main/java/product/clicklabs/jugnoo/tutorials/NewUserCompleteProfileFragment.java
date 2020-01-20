package product.clicklabs.jugnoo.tutorials;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.GoogleSigninActivity;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static android.app.Activity.RESULT_OK;
import static product.clicklabs.jugnoo.Constants.KEY_GOOGLE_PARCEL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserCompleteProfileFragment extends Fragment{

    private View root;
    private Button bClaimCoupon;
    private LinearLayout rlRoot;
    private NewUserFlow activity;
    private RelativeLayout rlFacebook, rlGoogle;
    private EditText etName, etEmail;
    FacebookLoginHelper facebookLoginHelper;
    private static final int GOOGLE_SIGNIN_REQ_CODE_LOGIN = 1124;
    private static final String TAG = NewUserCompleteProfileFragment.class.getName();


    public static NewUserCompleteProfileFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserCompleteProfileFragment fragment = new NewUserCompleteProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_complete_profile, container, false);
        rlRoot = (LinearLayout) root.findViewById(R.id.rlRoot);
        activity = (NewUserFlow) getActivity();
        try {
            if (rlRoot != null) {
                new ASSL(activity, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rlFacebook = (RelativeLayout) root.findViewById(R.id.rlFacebook);
        rlGoogle = (RelativeLayout) root.findViewById(R.id.rlGoogle);
        bClaimCoupon = (Button) root.findViewById(R.id.bClaimCoupon);
        etName = (EditText) root.findViewById(R.id.etName);
        etEmail = (EditText) root.findViewById(R.id.etEmail);

        if(!TextUtils.isEmpty(Data.userData.userName)){
            etName.setText(Data.userData.userName);
        }
        if(!TextUtils.isEmpty(Data.userData.userEmail)){
            etEmail.setText(Data.userData.userEmail);
        }

        rlFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().isOnline()) {
                    Utils.hideSoftKeyboard(activity, etName);
                    facebookLoginHelper.openFacebookSession();
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    rlFacebook.performClick();
                                }

                                @Override
                                public void neutralClick(View v) {
                                }

                                @Override
                                public void negativeClick(View v) {
                                }
                            });
                }
            }
        });

        rlGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.getInstance().isOnline()) {
                    Utils.hideSoftKeyboard(activity, etName);
                    startActivityForResult(new Intent(activity, GoogleSigninActivity.class),
                            GOOGLE_SIGNIN_REQ_CODE_LOGIN);

                } else{
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    rlGoogle.performClick();
                                }

                                @Override
                                public void neutralClick(View v) {
                                }

                                @Override
                                public void negativeClick(View v) {
                                }
                            });
                }
            }
        });

        bClaimCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etName.getText().toString())){
                    etName.requestFocus();
                    etName.setError(activity.getResources().getString(R.string.username_empty_error));
                } else if(TextUtils.isEmpty(etEmail.getText().toString())){
                    etEmail.requestFocus();
                    etEmail.setError(activity.getResources().getString(R.string.email_empty_error));
                } else if (!Utils.isEmailValid(etEmail.getText().toString())) {
                    etEmail.requestFocus();
                    etEmail.setError(getString(R.string.please_enter_valid_email_id));
                } else {
                    apiUpdateUserProfile(activity, etName.getText().toString(), etEmail.getText().toString());
                }
            }
        });

        activity.getTvTitle().setText(activity.getResources().getString(R.string.complete_profile));
        activity.setTickLineView();

        facebookLoginHelper = new FacebookLoginHelper(activity, activity.getCallbackManager(), new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
                //Data.facebookUserData = facebookUserData;
                Log.v("facebook data", "---> "+facebookUserData.firstName+"---> "+facebookUserData.userEmail);
                setUserNameEmail(facebookUserData.firstName, facebookUserData.userEmail);
            }

            @Override
            public void facebookLoginError(String message) {
                Utils.showToast(activity, message);
            }
        });
        return root;
    }

    private void setUserNameEmail(String name, String email){
        etName.setText(name);
        etEmail.setText(email);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGNIN_REQ_CODE_LOGIN) {
            if (RESULT_OK == resultCode) {
                Data.googleSignInAccount = data.getParcelableExtra(KEY_GOOGLE_PARCEL);
                Log.v("google data", "---> "+Data.googleSignInAccount.getDisplayName()+"---> "+Data.googleSignInAccount.getEmail());
                setUserNameEmail(Data.googleSignInAccount.getDisplayName(), Data.googleSignInAccount.getEmail());
            }
        }
    }

    public void apiUpdateUserProfile(final Activity activity, final String updatedName,
                                     final String updatedEmail) {
        if(MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.updating));

            HashMap<String, String> params = new HashMap<>();

            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_UPDATED_USER_NAME, updatedName);
            params.put(Constants.KEY_UPDATED_USER_EMAIL, updatedEmail);
            params.put("signup_tutorial", "1");

            if(Data.googleSignInAccount != null
                    &&!TextUtils.isEmpty(Data.googleSignInAccount.getIdToken())){
                params.put("google_access_token", Data.googleSignInAccount.getIdToken());
            }
            if(Data.facebookUserData != null
                    && !TextUtils.isEmpty(Data.facebookUserData.accessToken)){
                params.put("fb_access_token", Data.facebookUserData.accessToken);
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
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                Data.userData.userName = updatedName;
                                Data.userData.userEmail = updatedEmail;
                                Prefs.with(activity).save(SPLabels.USERNAME_UPDATED, 1);
                                Utils.showToast(activity, jObj.optString("message"));
                                openWalletFragment();
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

    private void openWalletFragment(){
        if(!activity.isFromMenu()
                &&Data.userData.getSignupTutorial().getDs3() != null
                && Data.userData.getSignupTutorial().getDs3() == 1){
            activity.getTransactionUtils().openNewUserWalletFragment(activity, activity.getRlContainer());
        } else{
            activity.performBackPressed();
        }

    }
}
