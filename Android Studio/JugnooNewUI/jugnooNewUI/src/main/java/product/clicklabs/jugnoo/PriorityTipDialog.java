package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by socomo on 1/4/16.
 */
public class PriorityTipDialog {

    Activity activity;

    public PriorityTipDialog(Activity activity) {
        this.activity = activity;
        showPriorityTipDialog(activity);

    }

    private void showPriorityTipDialog(Activity activity){
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_priority_tip);

            FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.latoRegular(activity));
            TextView textHighPriority = (TextView)dialog.findViewById(R.id.textViewHighPriority);
            textHighPriority.setTypeface(Fonts.latoLight(activity));
            ImageView close = (ImageView)dialog.findViewById(R.id.close);
            EditText editTextValue1 = (EditText)dialog.findViewById(R.id.editTextValue1);
            EditText editTextValue2 = (EditText)dialog.findViewById(R.id.editTextValue2);

            textMessage.setMovementMethod(new ScrollingMovementMethod());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);

            Spannable word = new SpannableString(activity.getResources().getString(R.string.type));
            word.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.grey_black_light)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textHighPriority.setText(word);
            Spannable wordTwo = new SpannableString("(1.8)\n");

            wordTwo.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.yellow)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textHighPriority.append(wordTwo);
            Spannable wordThree = new SpannableString(activity.getResources().getString(R.string.type_bottom));

            wordThree.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.grey_black_light)), 0, wordThree.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textHighPriority.append(wordThree);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    // listenerPositive.onClick(view);
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
