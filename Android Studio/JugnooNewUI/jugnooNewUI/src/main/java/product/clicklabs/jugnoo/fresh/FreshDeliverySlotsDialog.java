package product.clicklabs.jugnoo.fresh;

import android.app.Dialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.adapters.FreshDeliverySlotsAdapter;
import product.clicklabs.jugnoo.fresh.models.Slot;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshDeliverySlotsDialog {

	private final String TAG = FreshDeliverySlotsDialog.class.getSimpleName();
	private FreshActivity activity;
	private FreshDeliverySlotsDialogCallback callback;
	private FreshDeliverySlotsAdapter freshDeliverySlotsAdapter;
	private ArrayList<Slot> slots;
	private Dialog dialog;

	public FreshDeliverySlotsDialog(FreshActivity activity, ArrayList<Slot> slots,
									FreshDeliverySlotsDialogCallback callback) {
		this.activity = activity;
		this.slots = slots;
		this.callback = callback;
	}

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
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

	public void notifySlots(){
		if(freshDeliverySlotsAdapter != null) {
			freshDeliverySlotsAdapter.notifyDataSetChanged();
		}
	}

	public interface FreshDeliverySlotsDialogCallback{
		void onOkClicked();
	}

}
