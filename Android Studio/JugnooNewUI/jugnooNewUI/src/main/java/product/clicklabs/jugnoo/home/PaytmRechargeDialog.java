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
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by socomo on 1/4/16.
 */
public class PaytmRechargeDialog {

    private Activity activity;
    private String driverName, userPhoneNumber, amountToConfirm;
    private EditText editTextAmount;
    private Callback callback;

    public PaytmRechargeDialog(Activity activity, String driverName, String userPhoneNumber, String amountToConfirm,
                               Callback callback) {
        this.activity = activity;
        this.driverName = driverName;
        this.userPhoneNumber = userPhoneNumber;
        this.amountToConfirm = amountToConfirm;
        this.callback = callback;
    }

    public Dialog show(){
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
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
                    .getString(R.string.paytm_recharge_via_driver_info_format), driverName));

            Spannable word = new SpannableString(userPhoneNumber);
            word.setSpan(new StyleSpan(Typeface.BOLD), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textViewRechargeInfo.append(" ");
            textViewRechargeInfo.append(word);

            editTextAmount.setHint(amountToConfirm);

            editTextAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals(amountToConfirm)) {
                        editTextAmount.setTextColor(activity.getResources().getColor(R.color.green_status));
                    } else {
                        editTextAmount.setTextColor(activity.getResources().getColor(R.color.red_status));
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    callback.onOk();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    callback.onCancel();
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


    public interface Callback{
        void onOk();
        void onCancel();
    }

}
