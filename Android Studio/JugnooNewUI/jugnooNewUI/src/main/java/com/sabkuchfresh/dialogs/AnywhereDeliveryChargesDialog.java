package com.sabkuchfresh.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 5/2/16.
 */
public class AnywhereDeliveryChargesDialog {

	private final String TAG = AnywhereDeliveryChargesDialog.class.getSimpleName();
	private FreshActivity activity;
	private Callback callback;
	ArrayList<HashMap<String,Double>> popupData;
	private Dialog dialog ;
	private LinearLayout linearLayoutInner;
	private double estimatedCharges;
	private TextView textViewFare;
	private String tandCText ;
	private TextView textViewTandC;

	public AnywhereDeliveryChargesDialog(FreshActivity activity, Callback callback, ArrayList<HashMap<String, Double>> popupData, double estimatedCharges, String tandCText) {
		this.activity = activity;
		this.callback = callback;
		this.popupData = popupData;
		this.estimatedCharges = estimatedCharges;
		this.tandCText = tandCText;
		init();
		setPopupData();


	}

	public AnywhereDeliveryChargesDialog show() {
		try {


			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private void setPopupData() {
		textViewFare.setText(String.format("%s%s", activity.getString(R.string.rupee), Utils.getMoneyDecimalFormat().format(estimatedCharges)));
		LayoutInflater inflater = LayoutInflater.from(activity);


		for(HashMap<String,Double> mapValue: popupData){
            Map.Entry<String,Double> entry = mapValue.entrySet().iterator().next();
            String label = entry.getKey();
            double value = entry.getValue();
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_details_anywhere_delivery_dialog, null, false);
            ((TextView)linearLayout.findViewById(R.id.tv_label)).setText(label);
			((TextView)linearLayout.findViewById(R.id.tv_value)).setText(String.format("%s%s", activity.getString(R.string.rupee), Utils.getMoneyDecimalFormat().format(value)));
            ((View)linearLayout.findViewById(R.id.view_dotted)).setLayerType(View.LAYER_TYPE_SOFTWARE, null) ;
			linearLayoutInner.addView(linearLayout,linearLayoutInner.getChildCount()-1);




        }

		if(!TextUtils.isEmpty(tandCText)){
			textViewTandC.setText(tandCText);
			textViewTandC.setVisibility(View.VISIBLE);
		}else{
			textViewTandC.setVisibility(View.GONE);
		}
	}

	@NonNull
	private void init() {
		dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_anywhere_delivery_charges);

		RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
//			new ASSL(activity, relative, 1134, 720, false);

		WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
		layoutParams.dimAmount = 0.6f;
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		 linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
		textViewFare = ((TextView) dialog.findViewById(R.id.textViewFareDetails));
		textViewTandC = ((TextView) dialog.findViewById(R.id.textViewTandC));
		textViewFare.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
		textViewTandC.setTypeface(Fonts.mavenLight(activity));
		ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
		imageViewClose.setOnClickListener(new View.OnClickListener() {
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
                callback.onDialogDismiss();
            }
        });

	}


	public interface Callback{
		void onDialogDismiss();
	}

}