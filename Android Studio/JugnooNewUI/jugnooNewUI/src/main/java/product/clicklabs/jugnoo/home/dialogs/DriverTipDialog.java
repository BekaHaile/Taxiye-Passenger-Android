package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Font;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PrefixedEditText;

/**
 * Created by socomo on 1/4/16.
 */
public class DriverTipDialog {

    private Activity activity;
    private String currency;
    private Callback callback;
    private Dialog driverTipDialog;
    private PrefixedEditText edtAmount;
    private Button actionButton;

    private static final Integer TAG_ACTION_EDIT = 0;
    private static final Integer TAG_ACTION_DONE = 1;

    public DriverTipDialog(Activity activity,  Callback callback,String currency) {
        this.activity = activity;
        this.callback = callback;
        this.currency = currency;

    }



    public Dialog showPriorityTipDialog(Double tipValue){
        try {

            if(driverTipDialog==null){
                driverTipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                driverTipDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_TopInBottomOut;
                driverTipDialog.setContentView(R.layout.dialog_tip);
                edtAmount = driverTipDialog.findViewById(R.id.etCode);
                ((TextView)driverTipDialog.findViewById(R.id.tvTitle)).setTypeface(Fonts.mavenMedium(activity),Typeface.BOLD);
                edtAmount.setTypeface(Fonts.mavenMedium(activity));
                actionButton= driverTipDialog.findViewById(R.id.btn_done);
                ((TextView)driverTipDialog.findViewById(R.id.btnCancel)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView)driverTipDialog.findViewById(R.id.btn_done)).setTypeface(Fonts.mavenMedium(activity));

                driverTipDialog.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getTag()!=null && v.getTag()==TAG_ACTION_EDIT){
                            edtAmount.setEnabled(true);
                            actionButton.setTag(TAG_ACTION_DONE);
                            actionButton.setText(activity.getString(R.string.done));
                      }else{

                            callback.onConfirmed(Double.parseDouble(edtAmount.getText().toString()));

                        }
                    }
                });
                driverTipDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        driverTipDialog.dismiss();
                        callback.onCancelled();

                    }
                });

                edtAmount.addTextChangedListener(new UpdateCurrencyDrawableWatcher(edtAmount,currency));
                WindowManager.LayoutParams layoutParams = driverTipDialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.6f;
                driverTipDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                driverTipDialog.setCancelable(false);
                driverTipDialog.setCanceledOnTouchOutside(false);
            }


            if(tipValue!=null){
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


            return driverTipDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public interface Callback{
        void onConfirmed(Double amount);
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
    }

}
