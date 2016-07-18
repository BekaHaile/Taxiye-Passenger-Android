package product.clicklabs.jugnoo.home.dialogs;

/**
 * Created by ankit on 6/1/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Ankit on 3/4/16.
 */
public class PoolIntroDialog {

    private final String TAG = PoolIntroDialog.class.getSimpleName();
    private Activity activity;
    private Callback callback;
    private Dialog dialog = null;

    public PoolIntroDialog(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public Dialog show() {
        try {
            if(Prefs.with(activity).getInt(Constants.SP_POOL_INTRO_SHOWN, 0) == 0) {
                dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
                dialog.setContentView(R.layout.dialog_pool_intro);

                RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
                new ASSL(activity, relative, 1134, 720, false);

                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.6f;
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
                ImageView imageViewPool = (ImageView) dialog.findViewById(R.id.imageViewPool);
                TextView textViewCoolToPool = (TextView) dialog.findViewById(R.id.textViewCoolToPool); textViewCoolToPool.setTypeface(Fonts.mavenMedium(activity));
                TextView textViewShareTheJourney = (TextView) dialog.findViewById(R.id.textViewShareTheJourney); textViewShareTheJourney.setTypeface(Fonts.mavenMedium(activity));
                TextView textViewRidingAlone = (TextView) dialog.findViewById(R.id.textViewRidingAlone); textViewRidingAlone.setTypeface(Fonts.mavenMedium(activity));
                Button buttonContinue = (Button) dialog.findViewById(R.id.buttonContinue); buttonContinue.setTypeface(Fonts.avenirNext(activity));

//                final Spannable coolSB = new SpannableString("COOL");
//                final Spannable poolSB = new SpannableString("POOL");
//                coolSB.setSpan(new StyleSpan(Typeface.BOLD), 0, coolSB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                poolSB.setSpan(new StyleSpan(Typeface.BOLD), 0, poolSB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                textViewCoolToPool.setText("");
//                textViewCoolToPool.setText("ITS ");
//                textViewCoolToPool.append(coolSB);
//                textViewCoolToPool.append(" TO ");
//                textViewCoolToPool.append(poolSB);

                buttonContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callback.onContinueClicked();
                    }
                });

                linearLayoutInner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog.dismiss();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        callback.onDialogDismiss();
                    }
                });

                dialog.show();
                Prefs.with(activity).save(Constants.SP_POOL_INTRO_SHOWN, 1);
            } else{
                callback.notShown();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.notShown();
        }
        return dialog;
    }


    public interface Callback{
        void onContinueClicked();
        void onMayBeLaterClicked();
        void onDialogDismiss();
        void notShown();
    }

}
