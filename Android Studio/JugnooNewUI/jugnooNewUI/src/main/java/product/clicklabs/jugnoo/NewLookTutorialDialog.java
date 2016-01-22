package product.clicklabs.jugnoo;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by socomo on 1/22/16.
 */
public class NewLookTutorialDialog {
    private Activity activity;

    public NewLookTutorialDialog(Activity activity) {
        this.activity = activity;
        showNewLookDialog();
    }

    private Dialog showNewLookDialog(){
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_TopInBottomOut;
            dialog.setContentView(R.layout.dialog_new_look);

            RelativeLayout root = (RelativeLayout) dialog.findViewById(R.id.root);
            new ASSL(activity, root, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.9f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            return dialog;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
