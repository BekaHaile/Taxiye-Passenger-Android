package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiAuthenticatePaytmRecharge;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by socomo on 1/4/16.
 */
public class PaytmRechargeDialog {

    private Activity activity;
    private Dialog dialog;
    private String transferId, transferSenderName, transferPhone, transferAmount;
    private EditText editTextAmount;
    private Callback callback;

    public PaytmRechargeDialog(Activity activity, String transferId, String transferSenderName,
                               String transferPhone, String transferAmount,
                               Callback callback) {
        this.activity = activity;
        this.transferId = transferId;
        this.transferSenderName = transferSenderName;
        this.transferPhone = transferPhone;
        this.transferAmount = transferAmount;
        this.callback = callback;
    }

    public Dialog show(){
        try {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_paytm_recharge_via_driver);

            RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
            ((TextView)dialog.findViewById(R.id.textViewRecharge)).setTypeface(Fonts.mavenRegular(activity));
            TextView textViewRechargeInfo = (TextView) dialog.findViewById(R.id.textViewRechargeInfo);
            textViewRechargeInfo.setTypeface(Fonts.mavenLight(activity));
            ((TextView)dialog.findViewById(R.id.textViewTypeAmount)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView)dialog.findViewById(R.id.textViewRupee)).setTypeface(Fonts.mavenRegular(activity));
            editTextAmount = (EditText) dialog.findViewById(R.id.editTextAmount);
            editTextAmount.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenRegular(activity));
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.mavenRegular(activity));

            textViewRechargeInfo.setText(String.format(activity.getResources()
                    .getString(R.string.paytm_recharge_via_driver_info_format), transferSenderName));

            Spannable word = new SpannableString(transferPhone);
            word.setSpan(new StyleSpan(Typeface.BOLD), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textViewRechargeInfo.append(" ");
            textViewRechargeInfo.append(word);

//            editTextAmount.setHint(transferAmount);

            editTextAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals(transferAmount)) {
                        editTextAmount.setTextColor(activity.getResources().getColor(R.color.green_status));
                    } else {
                        editTextAmount.setTextColor(activity.getResources().getColor(R.color.red_status));
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editTextAmount.getText().toString().trim().equals(transferAmount)) {
                        authenticatePaytmRechargeApi(editTextAmount.getText().toString().trim());
                    } else{
                        editTextAmount.requestFocus();
                        editTextAmount.setError(String.format(activity.getResources()
                                .getString(R.string.type_amount_to_confirm_the_transaction_format), transferAmount));
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authenticatePaytmRechargeApi("-1");
                }
            });


            dialog.show();
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            btnOk.requestFocus();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void authenticatePaytmRechargeApi(final String amountEntered){
        new ApiAuthenticatePaytmRecharge(activity, new ApiAuthenticatePaytmRecharge.Callback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                if("-1".equalsIgnoreCase(amountEntered)){
                    callback.onCancel();
                } else{
                    callback.onOk();
                }
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onRetry(View view) {
                authenticatePaytmRechargeApi(amountEntered);
            }

            @Override
            public void onNoRetry(View view) {

            }
        }).authenticatePaytmRecharge(transferId, amountEntered);
    }



    public interface Callback{
        void onOk();
        void onCancel();
    }

}
