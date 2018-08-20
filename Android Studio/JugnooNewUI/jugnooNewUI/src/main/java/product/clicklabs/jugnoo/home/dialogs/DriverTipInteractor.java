package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.widgets.PrefixedEditText;

/**
 * Created by socomo on 1/4/16.
 */
public class DriverTipInteractor {

    private Activity activity;
    private String currency;
    private Callback callback;
    private Dialog driverTipDialog;
    private PrefixedEditText edtAmount;
    public Button actionButton;
    private String engagementId;

    private static final Integer TAG_ACTION_EDIT = 0;
    private static final Integer TAG_ACTION_DONE = 1;
    private UpdateCurrencyDrawableWatcher edtAmountWatcher;

    public DriverTipInteractor(Activity activity, Callback callback,String engagementId) {
        this.activity = activity;
        this.callback = callback;
        this.engagementId = engagementId;

    }



    public void showPriorityTipDialog(Double tipValue, String currency){
        try {

            if(driverTipDialog==null){
                driverTipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                driverTipDialog.setContentView(R.layout.dialog_tip);
                edtAmount = driverTipDialog.findViewById(R.id.etTipAmount);
                ((TextView)driverTipDialog.findViewById(R.id.tvTitle)).setTypeface(Fonts.mavenMedium(activity),Typeface.BOLD);
                edtAmount.setTypeface(Fonts.mavenMedium(activity));
                actionButton= driverTipDialog.findViewById(R.id.btn_done);
                actionButton.setTypeface(Fonts.mavenMedium(activity));

                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getTag()!=null && v.getTag()==TAG_ACTION_EDIT){
                            edtAmount.setEnabled(true);
                            edtAmount.setSelection(edtAmount.getText().toString().length());
                            actionButton.setTag(TAG_ACTION_DONE);
                            actionButton.setText(activity.getString(R.string.done));
                      }else{

                            PaymentModeConfigData stripePaymentData =   MyApplication.getInstance().getWalletCore().getStripeConfigData();
                            if(!activity.getResources().getBoolean(R.bool.is_card_mandatory_for_driver_tip) ||
                               (stripePaymentData!=null &&  stripePaymentData.getCardsData()!=null && stripePaymentData.getCardsData().size()>0)){
                                try {
                                    editTip(Double.parseDouble(edtAmount.getText().toString().trim()));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }


                           }else{

                                DialogPopup.alertPopupWithListener(activity, "", activity.getString(R.string.please_add_card_to_proceed), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(activity, PaymentActivity.class);
                                        intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
                                        intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.STRIPE_CARDS.getOrdinal());
                                        intent.putExtra(Constants.KEY_ADD_CARD_DRIVER_TIP, true);
                                        activity.startActivityForResult(intent, HomeActivity.REQ_CODE_ADD_CARD_DRIVER_TIP);
                                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    }
                                });
                            }


                        }
                    }
                });
                driverTipDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        driverTipDialog.dismiss();
                        callback.onCancelled();

                    }
                });

                edtAmountWatcher = new UpdateCurrencyDrawableWatcher(edtAmount, currency);
                edtAmount.addTextChangedListener(edtAmountWatcher);
                WindowManager.LayoutParams layoutParams = driverTipDialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.6f;
                driverTipDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                driverTipDialog.setCancelable(false);
                driverTipDialog.setCanceledOnTouchOutside(false);
            }



            edtAmountWatcher.updateCurrencyUnit(currency);
            if(tipValue!=null && tipValue>0){
                edtAmount.setText(String.valueOf(tipValue));
                edtAmount.setEnabled(false);
                actionButton.setTag(TAG_ACTION_EDIT);
                actionButton.setText(activity.getString(R.string.edit));
             }else{
                edtAmount.setText(null);
                edtAmount.setEnabled(true);
                actionButton.setTag(TAG_ACTION_DONE);
                actionButton.setText(activity.getString(R.string.done));
            }



            driverTipDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface Callback{
        void onConfirmed(Double amount,String engagementId);
        void onCancelled();
    }

    class UpdateCurrencyDrawableWatcher implements  TextWatcher{

        private Integer countBeforeChange = 0;

        private PrefixedEditText editText;
        private String currencyUnit;
        UpdateCurrencyDrawableWatcher(PrefixedEditText editText, String currencyUnit){
                this.editText = editText;
                this.currencyUnit = currencyUnit;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            countBeforeChange= s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>0 && countBeforeChange==0){
                editText.setHint(null);
                if(editText.getTextDrawable()==null){
                    editText.setPrefix(Utils.getCurrencySymbol(currencyUnit));

                }else{
                    editText.setCompoundDrawables(editText.getTextDrawable(),null,null,null);
                }
            }else if(s.length()==0){
                editText.setHint(R.string.enter_amount);
                editText.setCompoundDrawables(null,null,null,null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        void updateCurrencyUnit(String currencyUnit){
            if(!currencyUnit.equals(this.currencyUnit)){
                editText.updateTextDrawable(currencyUnit);
            }

        }
    }


    private void editTip(final Double amount){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
        params.put(Constants.KEY_TIP_AMOUNT, String.valueOf(amount));

        new ApiCommon<>(activity).showLoader(true).execute(params, ApiName.EDIT_TIP,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {
                        if(Data.autoData!=null && Data.autoData.getAssignedDriverInfo()!=null){
                            Data.autoData.getAssignedDriverInfo().setTipAmount(amount);

                        }
                        if(Data.autoData!=null && Data.autoData.getEndRideData()!=null){
                            Data.autoData.getEndRideData().setDriverTipAmount(amount);

                        }

                        if(driverTipDialog!=null && driverTipDialog.isShowing())driverTipDialog.dismiss();
                        callback.onConfirmed(amount,engagementId);

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });



    }

    public void deleteTip(){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
        params.put("is_delete","1");

        new ApiCommon<>(activity).showLoader(true).execute(params, ApiName.EDIT_TIP,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {
                        if(Data.autoData!=null && Data.autoData.getAssignedDriverInfo()!=null){
                            Data.autoData.getAssignedDriverInfo().setTipAmount(null);

                        }
                        if(Data.autoData!=null && Data.autoData.getEndRideData()!=null){
                            Data.autoData.getEndRideData().setDriverTipAmount(0.0);

                        }
                       if(driverTipDialog!=null && driverTipDialog.isShowing())driverTipDialog.dismiss();
                        callback.onConfirmed(null,engagementId);

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });



    }



}
