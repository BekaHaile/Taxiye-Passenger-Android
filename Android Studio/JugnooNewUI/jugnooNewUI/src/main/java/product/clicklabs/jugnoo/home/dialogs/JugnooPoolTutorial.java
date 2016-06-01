package product.clicklabs.jugnoo.home.dialogs;

/**
 * Created by ankit on 6/1/16.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Ankit on 3/4/16.
 */
public class JugnooPoolTutorial {

    private final String TAG = JugnooPoolTutorial.class.getSimpleName();
    private Activity activity;
    private Callback callback;
    private Dialog dialog = null;

    public JugnooPoolTutorial(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public Dialog show() {
        try {
            if(Prefs.with(activity).getInt(Constants.SP_FRESH_INTRO_SHOWN, 0) == 1) {
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
                ((TextView) dialog.findViewById(R.id.textViewCoolToPool)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView) dialog.findViewById(R.id.textViewCoolToPool1)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
                ((TextView) dialog.findViewById(R.id.textViewCoolToPool2)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView) dialog.findViewById(R.id.textViewCoolToPool3)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
                ((TextView) dialog.findViewById(R.id.textViewShareTheJourney)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView) dialog.findViewById(R.id.textViewShareTheJourney1)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView) dialog.findViewById(R.id.textViewRidingAlone)).setTypeface(Fonts.mavenMedium(activity));
                ((TextView) dialog.findViewById(R.id.textViewRidingAlone1)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
                ((TextView) dialog.findViewById(R.id.textViewRidingAlone2)).setTypeface(Fonts.mavenMedium(activity));
                ImageView imageViewGif = (ImageView)dialog.findViewById(R.id.imageViewGif);
                Glide.with(activity).load(R.drawable.pool_intro_gif).placeholder(R.drawable.pool_1).into(imageViewGif);


                Button buttonContinue = (Button) dialog.findViewById(R.id.buttonContinue);
                TextView textViewLater = (TextView) dialog.findViewById(R.id.textViewLater);
                textViewLater.setTypeface(Fonts.mavenRegular(activity));
                buttonContinue.setTypeface(Fonts.mavenRegular(activity));

                buttonContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callback.onContinueClicked();
                    }
                });

                textViewLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callback.onMayBeLaterClicked();
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
                Prefs.with(activity).save(Constants.SP_FRESH_INTRO_SHOWN, 1);
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
