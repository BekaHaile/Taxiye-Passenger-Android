package com.sabkuchfresh.home;

import android.app.Dialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshSortingAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshSortingDialog implements FlurryEventNames {

	private final String TAG = FreshSortingDialog.class.getSimpleName();
	private FreshActivity activity;
	private Dialog dialog;

    private ArrayList<SortResponseModel> sortList;
    private FreshDeliverySortDialogCallback sortDialogCallback;
    private FreshSortingAdapter sortingAdapter;

    public FreshSortingDialog(FreshActivity activity, ArrayList<SortResponseModel> sortList,
                              FreshDeliverySortDialogCallback sortDialogCallback) {
        this.activity = activity;
        this.sortList = sortList;
        this.sortDialogCallback = sortDialogCallback;
    }

    int pos = 102;
    private int actualPos = 1;
    public Dialog showSorting() {
        try {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogDown;
            dialog.setContentView(R.layout.dialog_fresh_list_sorting);

            RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            TextView textViewSelectDeliverySlot = (TextView) dialog.findViewById(R.id.textViewSelectDeliverySlot);
            textViewSelectDeliverySlot.setTypeface(Fonts.mavenRegular(activity));

            RecyclerView recyclerViewDeliverySlots = (RecyclerView) dialog.findViewById(R.id.recyclerViewDeliverySlots);
            recyclerViewDeliverySlots.setLayoutManager(new LinearLayoutManager(activity));
            recyclerViewDeliverySlots.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDeliverySlots.setHasFixedSize(false);

//            Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Fonts.mavenRegular(activity));

            for(int i=0;i<sortList.size();i++) {
                if(sortList.get(i).isCheck()) {
                    actualPos = i;
                }
            }

            relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    dialog.dismiss();
                }
            });

//            btnOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activity.setSlotSelected(activity.getSlotToSelect());
//                    sortDialogCallback.onOkClicked(pos);
//                    dialog.dismiss();
//                }
//            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //reset array again
                    for(int i=0;i<sortList.size();i++) {
                        if(i==actualPos) {
                            sortList.get(i).setCheck(true);
                        } else {
                            sortList.get(i).setCheck(false);
                        }
                    }
                    FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.CANCEL);
                    dialog.dismiss();
                }
            });

            sortingAdapter = new FreshSortingAdapter(activity, sortList, new FreshSortingAdapter.Callback() {
                @Override
                public void onSlotSelected(int position, SortResponseModel slot) {
//                    activity.setSlotToSelect(slot);
                    pos = position;
                    activity.setSlotSelected(activity.getSlotSelected());
                    sortDialogCallback.onOkClicked(pos);
                    dialog.dismiss();

                    int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                    if(type == AppConstant.ApplicationType.MEALS){
                        MyApplication.getInstance().logEvent(FirebaseEvents.M_SORT+"_"+slot.name, null);
                    } else if(type == AppConstant.ApplicationType.GROCERY){
						MyApplication.getInstance().logEvent(FirebaseEvents.G_SORT+"_"+slot.name, null);
					} else if(type == AppConstant.ApplicationType.MENUS){
						MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_SORT+"_"+slot.name, null);
					} else{
                        MyApplication.getInstance().logEvent(FirebaseEvents.F_SORT+"_"+slot.name, null);
                    }
                }
            });

            recyclerViewDeliverySlots.setAdapter(sortingAdapter);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    public interface FreshDeliverySortDialogCallback {
        void onOkClicked(int value);
    }

}
