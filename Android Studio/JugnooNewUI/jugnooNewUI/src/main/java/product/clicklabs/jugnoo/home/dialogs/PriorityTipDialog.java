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

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by socomo on 1/4/16.
 */
public class PriorityTipDialog {

    private Activity activity;
    private double fareFactor;
    private int priorityTipCategory;
    private Callback callback;

    public PriorityTipDialog(Activity activity, double fareFactor, int priorityTipCategory, Callback callback) {
        this.activity = activity;
        this.fareFactor = fareFactor;
        this.priorityTipCategory = priorityTipCategory;
        this.callback = callback;
    }

    public Dialog showDialog(){
        if((priorityTipCategory == PriorityTipCategory.LOW_PRIORITY_DIALOG.getOrdinal() ||
                (priorityTipCategory == PriorityTipCategory.HIGH_PRIORITY_DIALOG.getOrdinal()))){
            return showPriorityTipDialog(activity, fareFactor, priorityTipCategory);
        }
        else{
            callback.onConfirmed(false);
            return null;
        }
    }

    private Dialog showPriorityTipDialog(final Activity activity, double fareFactor, int priorityTipCategory){
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_TopInBottomOut;
            dialog.setContentView(R.layout.dialog_priority_tip);

            RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            String string = String.valueOf(fareFactor);
            String[] parts = string.split("\\.");
            final String part1 = parts[0];
            final String part2 = parts[1];

            LinearLayout linearLayoutLowPriority = (LinearLayout)dialog.findViewById(R.id.linearLayoutLowPriority);
            LinearLayout linearLayoutHighPriority = (LinearLayout)dialog.findViewById(R.id.linearLayoutHighPriority);
            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenRegular(activity));
            TextView textHighPriority = (TextView)dialog.findViewById(R.id.textViewHighPriority);
            textHighPriority.setTypeface(Fonts.mavenRegular(activity));
            ImageView close = (ImageView)dialog.findViewById(R.id.close);
            TextView textViewTipValue = (TextView)dialog.findViewById(R.id.textViewTipValue);
            textViewTipValue.setTypeface(Fonts.mavenRegular(activity));
            final EditText editTextValue1 = (EditText)dialog.findViewById(R.id.editTextValue1);
            final EditText editTextValue2 = (EditText)dialog.findViewById(R.id.editTextValue2);


            if(priorityTipCategory == PriorityTipCategory.HIGH_PRIORITY_DIALOG.getOrdinal()){
                linearLayoutHighPriority.setVisibility(View.VISIBLE);
                linearLayoutLowPriority.setVisibility(View.GONE);
                editTextValue1.setHint(part1);
                editTextValue2.setHint(part2);
            }else{
                linearLayoutLowPriority.setVisibility(View.VISIBLE);
                linearLayoutHighPriority.setVisibility(View.GONE);

//                linearLayoutHighPriority.setVisibility(View.VISIBLE);
//                linearLayoutLowPriority.setVisibility(View.GONE);
//                editTextValue1.setHint(part1);
//                editTextValue2.setHint(part2);
            }

            textViewTipValue.setText(String.valueOf(fareFactor) + "X");

            textMessage.setMovementMethod(LinkMovementMethod.getInstance());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenRegular(activity));

            Spannable word = new SpannableString(activity.getResources().getString(R.string.type));
            word.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_color)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textHighPriority.setText(word);
            Spannable wordTwo = new SpannableString(" ("+fareFactor+")\n");

            wordTwo.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.theme_color)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textHighPriority.append(wordTwo);
            Spannable wordThree = new SpannableString(activity.getResources().getString(R.string.type_bottom));

            wordThree.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_color)), 0, wordThree.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textHighPriority.append(wordThree);

            editTextValue1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().equals(part1)) {
                        editTextValue1.setTextColor(activity.getResources().getColor(R.color.theme_color));
                        editTextValue2.setEnabled(true);
                        editTextValue2.requestFocus();
                    } else {
                        editTextValue1.setTextColor(activity.getResources().getColor(R.color.red));
                        editTextValue2.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editTextValue2.setEnabled(false);
            editTextValue2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.v("character is", "--> " + s.toString());
                    if (editTextValue1.getText().toString().equals(part1) && s.toString().equals(part2)) {
                        Log.v("code matched", "code matched");
                        editTextValue1.setTextColor(activity.getResources().getColor(R.color.theme_color));
                        callback.onConfirmed(true);
                        dialog.dismiss();
                        Utils.hideSoftKeyboard(activity, editTextValue2);
                    } else {
                        if (part2.startsWith(s.toString())) {
                            editTextValue2.setTextColor(activity.getResources().getColor(R.color.theme_color));
                        } else {
                            editTextValue2.setTextColor(activity.getResources().getColor(R.color.red));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.getInstance().isOnline()) {
                        dialog.dismiss();
                        callback.onConfirmed(true);
                    } else{
                        DialogPopup.dialogNoInternet(activity, activity.getString(R.string.connection_lost_title),
                                activity.getString(R.string.connection_lost_desc), new Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View v) {
                                        btnOk.performClick();
                                    }

                                    @Override
                                    public void neutralClick(View v) {

                                    }

                                    @Override
                                    public void negativeClick(View v) {

                                    }
                                });
                    }
                    // listenerPositive.onClick(view);
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onCancelled();
                    dialog.dismiss();
                }
            });


            dialog.show();
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            close.requestFocus();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public interface Callback{
        void onConfirmed(boolean confirmClicked);
        void onCancelled();
    }

}
