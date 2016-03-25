package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
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
    private TextView textViewTypeAmount;
    private Button btnOk;
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
            textViewTypeAmount = (TextView)dialog.findViewById(R.id.textViewTypeAmount);
            textViewTypeAmount.setTypeface(Fonts.mavenRegular(activity));
            ((TextView)dialog.findViewById(R.id.textViewRupee)).setTypeface(Fonts.mavenRegular(activity));
            editTextAmount = (EditText) dialog.findViewById(R.id.editTextAmount);
            editTextAmount.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenRegular(activity));
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.mavenRegular(activity));


            Spannable spannableName = new SpannableString(transferSenderName);
            spannableName.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Spannable spannablePhone = new SpannableString(transferPhone);
            spannablePhone.setSpan(new StyleSpan(Typeface.BOLD), 0, spannablePhone.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textViewRechargeInfo.setText("");
            textViewRechargeInfo.append(activity.getResources().getString(R.string.driver));
            textViewRechargeInfo.append(" ");
            textViewRechargeInfo.append(spannableName);
            textViewRechargeInfo.append(" ");
            textViewRechargeInfo.append(activity.getResources().getString(R.string.paytm_recharge_via_driver_info));
            textViewRechargeInfo.append(" ");
            textViewRechargeInfo.append(spannablePhone);

            editTextAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    btnOk.performClick();
                    return true;
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editTextAmount.getText().toString().trim().equals(transferAmount)) {
                        authenticatePaytmRechargeApi(editTextAmount.getText().toString().trim());
                    } else{
                        editTextAmount.requestFocus();
                        editTextAmount.setError(activity.getResources()
                                .getString(R.string.type_amount_to_confirm_the_transaction));
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
            textViewTypeAmount.requestFocus();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void authenticatePaytmRechargeApi(final String amountEntered){
        new ApiAuthenticatePaytmRecharge(activity, dialog, new ApiAuthenticatePaytmRecharge.Callback() {
            @Override
            public void onSuccess() {
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
