package com.sabkuchfresh.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 12/5/17.
 * Tutorial dialog for fatafat how it works
 */

public class FatafatHowItWorksDialog {

    private Context mContext;
    private DialogCallback mDialogCallback;
    private Dialog mTutorialDialog;
    private RecyclerView mRecyclerViewHowItWorks;

    /**
     * Constructor
     *
     * @param context        calling context
     * @param dialogCallback mTutorialDialog dismiss callback
     */
    public FatafatHowItWorksDialog(Context context, DialogCallback dialogCallback) {
        mContext = context;
        mDialogCallback = dialogCallback;
        init();
        populateData();
    }

    /**
     * Populates tutorial data in the dialog
     */
    private void populateData() {

        // todo set data
    }


    /**
     * Init the dialog
     */
    private void init() {
        mTutorialDialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        mTutorialDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
        mTutorialDialog.setContentView(R.layout.layout_fatafat_tutorial);
        WindowManager.LayoutParams layoutParams = mTutorialDialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.6f;
        mTutorialDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mTutorialDialog.setCancelable(true);
        mTutorialDialog.setCanceledOnTouchOutside(true);

        ImageView imgVwCross = (ImageView) mTutorialDialog.findViewById(R.id.imgVwCross);
        mRecyclerViewHowItWorks = (RecyclerView) mTutorialDialog.findViewById(R.id.rvHowItWorks);


        imgVwCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mTutorialDialog.dismiss();
            }
        });

        mTutorialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                mDialogCallback.onDialogDismiss();
            }
        });
    }

    /**
     * Shows the tutorial dialog
     */
    public void showDialog() {
        if (mTutorialDialog != null && !mTutorialDialog.isShowing()) {
            mTutorialDialog.show();
        }
    }

    /**
     * Dialog dismiss callback
     */
    interface DialogCallback {
        /**
         * On mTutorialDialog dismissed
         */
        void onDialogDismiss();
    }


}
