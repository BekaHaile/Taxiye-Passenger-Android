package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by socomo on 1/22/16.
 */
public class NewLookTutorialDialog {
    private Activity activity;

    public NewLookTutorialDialog(Activity activity) {
        this.activity = activity;
        showNewLookDialog();
    }

    private void showNewLookDialog(){
        if((Prefs.with(activity).getInt(SPLabels.NEW_LOOK_TUTORIAL_SHOWN, 0) == 0)){
            Prefs.with(activity).save(SPLabels.NEW_LOOK_TUTORIAL_SHOWN, 1);
            // for tutorial screens
            try {
                final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                //dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_TopInBottomOut;
                dialog.setContentView(R.layout.dialog_new_look);

                RelativeLayout root = (RelativeLayout) dialog.findViewById(R.id.root);
                new ASSL(activity, root, 1134, 720, true);

                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.8f;
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

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
