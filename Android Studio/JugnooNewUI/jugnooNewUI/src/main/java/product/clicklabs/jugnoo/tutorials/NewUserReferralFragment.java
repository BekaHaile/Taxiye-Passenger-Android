package product.clicklabs.jugnoo.tutorials;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserReferralFragment extends Fragment {

    private View root;
    private Button buttonApplyPromo;
    private NewUserFlow activity;
    private RelativeLayout rlRoot;
    private EditText etPromoCode;
    private static final String TAG = NewUserReferralFragment.class.getName();

    public static NewUserReferralFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserReferralFragment fragment = new NewUserReferralFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_referral, container, false);
        rlRoot = (RelativeLayout) root.findViewById(R.id.rlRoot);
        activity = (NewUserFlow)getActivity();
        try {
            if (rlRoot != null) {
                new ASSL(activity, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        etPromoCode = (EditText) root.findViewById(R.id.etPromoCode);
        buttonApplyPromo = (Button) root.findViewById(R.id.buttonApplyPromo);

        activity.getTvTitle().setText(activity.getResources().getString(R.string.add_referral_code));
        activity.getIvBack().setVisibility(View.GONE);
        activity.setTickLineView();
        Data.userData.getSignupTutorial().setDs1(0);
        Data.userData.setShowTutorial(1);

        buttonApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.getTransactionUtils().openNewUserCompleteProfileFragment(activity, activity.getRlContainer());
                String promoCode = etPromoCode.getText().toString().trim();
                if (promoCode.length() > 0) {
                    apiApplyReferralCode(activity, promoCode);
                } else {
                    etPromoCode.requestFocus();
                    etPromoCode.setError(getString(R.string.code_cant_be_empty));
                }
            }
        });

        return root;
    }

    private void openCompleteProfile(){
        if(Data.userData.getSignupTutorial().getDs2() != null
                && Data.userData.getSignupTutorial().getDs2() == 1){
            activity.getTransactionUtils().openNewUserCompleteProfileFragment(activity, activity.getRlContainer());
        } else{
            activity.performBackPressed();
        }

    }

    /**
     * API call for applying promo code to server
     */
    public void apiApplyReferralCode(final Activity activity, final String promoCode) {
        try {
            if(!HomeActivity.checkIfUserDataNull(activity)) {
                if (MyApplication.getInstance().isOnline()) {
                    DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

                    HashMap<String, String> params = new HashMap<>();
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_CODE, promoCode);

                    new HomeUtil().putDefaultParams(params);
                    RestClient.getApiService().enterCode(params, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            Log.i(TAG, "enterCode response = " + responseStr);
                            try {
                                JSONObject jObj = new JSONObject(responseStr);
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                                    HomeActivity.logoutUser(activity);
                                } else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                    String errorMessage = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                                    String message = jObj.getString("message");
                                    Utils.showToast(activity, message);
                                    openCompleteProfile();
                                } else {
                                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));

                            }
                            DialogPopup.dismissLoadingDialog();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "enterCode error="+error.toString());
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        }
                    });
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    apiApplyReferralCode(activity, promoCode);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
