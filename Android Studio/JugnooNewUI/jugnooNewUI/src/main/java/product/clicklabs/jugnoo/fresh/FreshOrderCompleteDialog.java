package product.clicklabs.jugnoo.fresh;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshOrderCompleteDialog {

	private final String TAG = FreshOrderCompleteDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;

	public FreshOrderCompleteDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(String orderId, String deliverySlot, String deliveryDay) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_fresh_order_complete);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView) dialog.findViewById(R.id.textViewThankYou)).setTypeface(Fonts.mavenRegular(activity));

			TextView textViewOrderId = (TextView) dialog.findViewById(R.id.textViewOrderId);
			textViewOrderId.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewOrderDeliverySlot = (TextView) dialog.findViewById(R.id.textViewOrderDeliverySlot);
			textViewOrderDeliverySlot.setTypeface(Fonts.mavenRegular(activity));
			textViewOrderDeliverySlot.setText(deliverySlot);
			textViewOrderDeliverySlot.append("\n");
			textViewOrderDeliverySlot.append(deliveryDay);

			textViewOrderId.setText(activity.getResources().getString(R.string.your_order_id));

			final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
			final SpannableStringBuilder sb = new SpannableStringBuilder(orderId);
			sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textViewOrderId.append(sb);
			textViewOrderId.append("\n");
			textViewOrderId.append(activity.getResources().getString(R.string.will_be_delivered_by));

			Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
			buttonOk.setTypeface(Fonts.mavenRegular(activity));

			buttonOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
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
					dialog.dismiss();
				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.onDismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback{
		void onDismiss();
	}

}
