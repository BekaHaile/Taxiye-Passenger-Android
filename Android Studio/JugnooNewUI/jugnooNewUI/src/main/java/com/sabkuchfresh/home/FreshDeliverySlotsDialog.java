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

import com.sabkuchfresh.R;
import com.sabkuchfresh.adapters.FreshDeliverySlotsAdapter;
import com.sabkuchfresh.adapters.FreshSortingAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.Fonts;

import java.util.ArrayList;

/**
 * Created by shankar on 3/4/16.
 * @deprecated By Gurmail
 */
public class FreshDeliverySlotsDialog implements FlurryEventNames {

	private final String TAG = FreshDeliverySlotsDialog.class.getSimpleName();
	private FreshActivity activity;
	private FreshDeliverySlotsDialogCallback callback;
	private FreshDeliverySlotsAdapter freshDeliverySlotsAdapter;
	private ArrayList<Slot> slots;
	private Dialog dialog;

    private ArrayList<SortResponseModel> sortList;
    private FreshDeliverySortDialogCallback sortDialogCallback;
    private FreshSortingAdapter sortingAdapter;

	public FreshDeliverySlotsDialog(FreshActivity activity, ArrayList<Slot> slots,
									FreshDeliverySlotsDialogCallback callback) {
		this.activity = activity;
		this.slots = slots;
		this.callback = callback;
	}

    public FreshDeliverySlotsDialog(FreshActivity activity, ArrayList<SortResponseModel> sortList,
                                    FreshDeliverySortDialogCallback sortDialogCallback) {
        this.activity = activity;
        this.sortList = sortList;
        this.sortDialogCallback = sortDialogCallback;
    }

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogDown;
			dialog.setContentView(R.layout.dialog_fresh_delivery_slots);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			TextView textViewSelectDeliverySlot = (TextView) dialog.findViewById(R.id.textViewSelectDeliverySlot);
			textViewSelectDeliverySlot.setTypeface(Fonts.mavenRegular(activity));

			RecyclerView recyclerViewDeliverySlots = (RecyclerView) dialog.findViewById(R.id.recyclerViewDeliverySlots);
			recyclerViewDeliverySlots.setLayoutManager(new LinearLayoutManager(activity));
			recyclerViewDeliverySlots.setItemAnimator(new DefaultItemAnimator());
			recyclerViewDeliverySlots.setHasFixedSize(false);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Fonts.mavenRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.setSlotSelected(activity.getSlotToSelect());
					callback.onOkClicked();
					dialog.dismiss();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			freshDeliverySlotsAdapter = new FreshDeliverySlotsAdapter(activity, slots,
					new FreshDeliverySlotsAdapter.Callback() {
						@Override
						public void onSlotSelected(int position, Slot slot) {
                            FlurryEventLogger.event(CHECKOUT_SCREEN, TIMESLOT_CHANGED, ""+(position+1));
							activity.setSlotToSelect(slot);
						}
					});

			recyclerViewDeliverySlots.setAdapter(freshDeliverySlotsAdapter);

			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
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
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

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
                    dialog.dismiss();
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
                    activity.setSlotSelected(activity.getSlotToSelect());
                    sortDialogCallback.onOkClicked(pos);
                    dialog.dismiss();
                }
            });

            recyclerViewDeliverySlots.setAdapter(sortingAdapter);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

	public void notifySlots(){
		if(freshDeliverySlotsAdapter != null) {
			freshDeliverySlotsAdapter.notifyDataSetChanged();
		}
	}


    public void notifySortList() {
        if(sortingAdapter != null) {
            sortingAdapter.notifyDataSetChanged();
        }
    }

	public interface FreshDeliverySlotsDialogCallback {
		void onOkClicked();
	}

    public interface FreshDeliverySortDialogCallback {
        void onOkClicked(int value);
    }

}
