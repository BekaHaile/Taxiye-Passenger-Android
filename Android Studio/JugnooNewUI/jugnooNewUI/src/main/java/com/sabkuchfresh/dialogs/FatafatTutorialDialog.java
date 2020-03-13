package com.sabkuchfresh.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sabkuchfresh.adapters.FatafatTutorialAdapter;
import com.sabkuchfresh.datastructure.FatafatTutorialData;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 12/5/17.
 * Tutorial dialog for fatafat how it works
 */

public class FatafatTutorialDialog {

    private Context mContext;
    private DialogCallback mDialogCallback;
    private Dialog mTutorialDialog;
    private RecyclerView mRecyclerViewHowItWorks;
    private ArrayList<FatafatTutorialData> mFatafatTutorialData = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context             calling context
     * @param fatafatTutorialData tutorial data
     */
    public FatafatTutorialDialog(Context context, ArrayList<FatafatTutorialData> fatafatTutorialData) {
        mContext = context;
        mFatafatTutorialData = fatafatTutorialData;
        init();
        populateData();
    }

    /**
     * Sets dismiss dialog callback
     *
     * @param dialogCallback the dialog callback
     */
    public void setDismissCallback(DialogCallback dialogCallback) {
        mDialogCallback = dialogCallback;
    }

    /**
     * Populates tutorial data in the dialog
     */
    private void populateData() {

        FatafatTutorialAdapter fatafatTutorialAdapter = new FatafatTutorialAdapter(mContext, mFatafatTutorialData);
        mRecyclerViewHowItWorks.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewHowItWorks.setAdapter(fatafatTutorialAdapter);
    }


    /**
     * Init the dialog
     */
    private void init() {
        mTutorialDialog = new Dialog(mContext);
        mTutorialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTutorialDialog.setContentView(R.layout.layout_fatafat_tutorial);
        WindowManager.LayoutParams layoutParams = mTutorialDialog.getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
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
                if (mDialogCallback != null) {
                    mDialogCallback.onDialogDismiss();
                }
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
