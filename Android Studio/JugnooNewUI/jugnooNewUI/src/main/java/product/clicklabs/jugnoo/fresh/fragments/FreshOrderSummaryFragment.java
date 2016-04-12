package product.clicklabs.jugnoo.fresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.adapters.FreshOrderItemAdapter;
import product.clicklabs.jugnoo.fresh.models.OrderHistory;
import product.clicklabs.jugnoo.fresh.models.OrderItem;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class FreshOrderSummaryFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = FreshOrderSummaryFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;
	private RecyclerView recyclerViewOrderItems;
	private FreshOrderItemAdapter freshOrderItemAdapter;

	private TextView textViewOrderIdValue, textViewOrderDeliveryDateValue, textViewOrderDeliverySlotValue,
			textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue,
			textViewPaymentMode, textViewPaymentModeValue;
	private Button buttonOk;


	private View rootView;
    private FreshActivity activity;

	public FreshOrderSummaryFragment(){
	}


    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshOrderSummaryFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_order_summary, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewOrderId)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderDeliveryDate)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderDeliverySlot)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderReceipt)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewPaymentBy)).setTypeface(Fonts.mavenRegular(activity));

		textViewOrderIdValue = (TextView) rootView.findViewById(R.id.textViewOrderIdValue); textViewOrderIdValue.setTypeface(Fonts.mavenRegular(activity));
		textViewOrderDeliveryDateValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliveryDateValue); textViewOrderDeliveryDateValue.setTypeface(Fonts.mavenRegular(activity));
		textViewOrderDeliverySlotValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliverySlotValue); textViewOrderDeliverySlotValue.setTypeface(Fonts.mavenRegular(activity));
		textViewTotalAmountValue = (TextView) rootView.findViewById(R.id.textViewTotalAmountValue); textViewTotalAmountValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDeliveryChargesValue = (TextView) rootView.findViewById(R.id.textViewDeliveryChargesValue); textViewDeliveryChargesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewAmountPayableValue = (TextView) rootView.findViewById(R.id.textViewAmountPayableValue); textViewAmountPayableValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewPaymentMode = (TextView) rootView.findViewById(R.id.textViewPaymentMode); textViewPaymentMode.setTypeface(Fonts.mavenRegular(activity));
		textViewPaymentModeValue = (TextView) rootView.findViewById(R.id.textViewPaymentModeValue); textViewPaymentModeValue.setTypeface(Fonts.mavenRegular(activity));
		buttonOk = (Button) rootView.findViewById(R.id.buttonOk); buttonOk.setTypeface(Fonts.mavenRegular(activity));

		recyclerViewOrderItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewOrderItems);
		recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewOrderItems.setHasFixedSize(false);

		try {
			if(activity.getOrderHistoryOpened() != null) {
				OrderHistory orderHistory = activity.getOrderHistoryOpened();
				freshOrderItemAdapter = new FreshOrderItemAdapter(activity,
						(ArrayList<OrderItem>) orderHistory.getOrderItems());
				recyclerViewOrderItems.setAdapter(freshOrderItemAdapter);

				textViewOrderIdValue.setText(String.valueOf(orderHistory.getOrderId()));
				textViewOrderDeliveryDateValue.setText(DateOperations.convertDateOnlyViaFormat(DateOperations
						.utcToLocalTZ(orderHistory.getOrderTime())));
				textViewOrderDeliverySlotValue.setText("");
				textViewTotalAmountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount()
								- orderHistory.getDeliveryCharges())));
				textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getDeliveryCharges())));
				textViewAmountPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));
				if(orderHistory.getPaymentMode().equals(PaymentOption.PAYTM.getOrdinal())){
					textViewPaymentMode.setText(activity.getResources().getString(R.string.paytm));
				} else{
					textViewPaymentMode.setText(activity.getResources().getString(R.string.cash));
				}
				textViewPaymentModeValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.performBackPressed();
			}
		});

		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
		System.gc();
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}

}
