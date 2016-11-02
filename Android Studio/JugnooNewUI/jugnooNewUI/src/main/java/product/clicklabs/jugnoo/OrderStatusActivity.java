package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCartItemsAdapter;
import com.sabkuchfresh.adapters.FreshOrderItemAdapter;
import com.sabkuchfresh.adapters.OrderItemsAdapter;
import com.sabkuchfresh.retrofit.model.SubItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/10/16.
 */

public class OrderStatusActivity extends Fragment {

    private RelativeLayout relative;
    private TextView tvOrderStatus, tvOrderStatusVal, tvOrderTime, tvOrderTimeVal, tvDeliveryTime, tvDeliveryTimeVal, tvDeliveryTo,
            tvDelveryPlace, tvDeliveryToVal, tvSubAmount, tvSubAmountVal, tvPaymentMethod;
    private ImageView ivPaymentMethodVal, ivDeliveryPlace;
    private Button bNeedHelp;
    private int orderId;
    private NonScrollListView listViewOrder;
    private OrderItemsAdapter orderItemsAdapter;
    private LinearLayout llFinalAmount, llDeliveryPlace;
    private ArrayList<HistoryResponse.OrderItem> subItemsOrders = new ArrayList<HistoryResponse.OrderItem>();
    private HistoryResponse.Datum orderHistory;
    private FragmentActivity activity;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_order_status, container, false);
        relative = (RelativeLayout) rootView.findViewById(R.id.relative);

        activity = getActivity();

        new ASSL(activity, relative, 1134, 720, false);


        try {
            orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
            setFragTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvOrderStatus = (TextView) rootView.findViewById(R.id.tvOrderStatus); tvOrderStatus.setTypeface(Fonts.mavenMedium(activity));
        tvOrderStatusVal = (TextView) rootView.findViewById(R.id.tvOrderStatusVal); tvOrderStatusVal.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTime = (TextView) rootView.findViewById(R.id.tvOrderTime); tvOrderTime.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTimeVal = (TextView) rootView.findViewById(R.id.tvOrderTimeVal);tvOrderTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTime = (TextView) rootView.findViewById(R.id.tvDeliveryTime); tvDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTimeVal = (TextView) rootView.findViewById(R.id.tvDeliveryTimeVal); tvDeliveryTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTo = (TextView) rootView.findViewById(R.id.tvDeliveryTo); tvDeliveryTo.setTypeface(Fonts.mavenMedium(activity));
        tvDelveryPlace = (TextView) rootView.findViewById(R.id.tvDelveryPlace); tvDelveryPlace.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvDeliveryToVal = (TextView) rootView.findViewById(R.id.tvDeliveryToVal); tvDeliveryToVal.setTypeface(Fonts.mavenMedium(activity));
        tvSubAmount = (TextView) rootView.findViewById(R.id.tvSubAmount); tvSubAmount.setTypeface(Fonts.mavenMedium(activity));
        tvSubAmountVal = (TextView) rootView.findViewById(R.id.tvSubAmountVal); tvSubAmountVal.setTypeface(Fonts.mavenMedium(activity));
        llFinalAmount = (LinearLayout) rootView.findViewById(R.id.llFinalAmount);
        tvPaymentMethod = (TextView) rootView.findViewById(R.id.tvPaymentMethod); tvPaymentMethod.setTypeface(Fonts.mavenMedium(activity));
        ivPaymentMethodVal = (ImageView) rootView.findViewById(R.id.ivPaymentMethodVal);
        bNeedHelp = (Button) rootView.findViewById(R.id.bNeedHelp);
        llDeliveryPlace = (LinearLayout) rootView.findViewById(R.id.llDeliveryPlace);
        ivDeliveryPlace = (ImageView) rootView.findViewById(R.id.ivDeliveryPlace);


        listViewOrder = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        orderItemsAdapter = new OrderItemsAdapter(activity, subItemsOrders);
        listViewOrder.setAdapter(orderItemsAdapter);

        getOrderData(activity);


        bNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof RideTransactionsActivity) {
                    new TransactionUtils().openRideIssuesFragment(activity,
                            ((RideTransactionsActivity) activity).getContainer(),
                            -1, -1, null, null, 0, false, 0, orderHistory);
                } else{
                    activity.onBackPressed();
                }
            }
        });

        return relative;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setFragTitle();
        }
    }

    private void setFragTitle(){
        if(activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle("Order #" + orderId);
        } else if(activity instanceof SupportActivity) {
            ((SupportActivity) activity).setTitle("Order #" + orderId);
        }
    }

    /**
     * Method used to get order information
     */
    private void getOrderData(final Activity activity) {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("order_id", "" + orderId);
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.INTERATED, "1");
                RestClient.getFreshApiService().orderHistory(params, new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {

                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    orderHistory = historyResponse.getData().get(0);
                                    subItemsOrders.clear();
                                    subItemsOrders.addAll(historyResponse.getData().get(0).getOrderItems());
                                    orderItemsAdapter.notifyDataSetChanged();

                                    setStatusResponse(historyResponse);
                                } else {
                                    //updateListData(message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            //updateListData(Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        //updateListData(Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            }
            else {
                //updateListData(Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFinalAmountView(LinearLayout llFinalAmount, String fieldText, String fieldTextVal){
        LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
        RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
        TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges); tvDelCharges.setTypeface(Fonts.mavenMedium(activity));
        TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal); tvDelChargesVal.setTypeface(Fonts.mavenMedium(activity));
        tvDelCharges.setText(fieldText);
//        tvDelChargesVal.setText(fieldTextVal);
        tvDelChargesVal.setText(String.format(getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormatWithoutFloat().format(Float.parseFloat(fieldTextVal))));
        llFinalAmount.addView(view);
        ASSL.DoMagic(relative);
    }

    private int getSubTotalAmount(HistoryResponse historyResponse){
        int subTotal = Integer.parseInt(Utils.getMoneyDecimalFormat().format(historyResponse.getData().get(0).getOrderAmount() - historyResponse.getData().get(0).getDeliveryCharges()
                + historyResponse.getData().get(0).getJugnooDeducted() + historyResponse.getData().get(0).getDiscount()));
        return subTotal;
    }

    private void setStatusResponse(HistoryResponse historyResponse) {
        try {
            tvOrderStatusVal.setText(historyResponse.getData().get(0).getOrderStatus());
            tvOrderTimeVal.setText(historyResponse.getData().get(0).getOrderTime());
            tvOrderTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(historyResponse.getData().get(0).getOrderTime())));
            tvDeliveryTimeVal.setText(historyResponse.getData().get(0).getExpectedDeliveryDate());
            tvDeliveryToVal.setText(historyResponse.getData().get(0).getDeliveryAddress());
            try {
                tvSubAmountVal.setText(String.format(getResources().getString(R.string.rupees_value_format), Integer.toString(getSubTotalAmount(historyResponse))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvOrderStatusVal.setTextColor(Color.parseColor(historyResponse.getData().get(0).getOrderStatusColor()));

            if(!historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase("")){
                llDeliveryPlace.setVisibility(View.VISIBLE);
                tvDelveryPlace.setText(historyResponse.getData().get(0).getDeliveryAddressType());
                if(historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.home))){
                    ivDeliveryPlace.setImageResource(R.drawable.home);
                } else if(historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.work))){
                    ivDeliveryPlace.setImageResource(R.drawable.work);
                } else{
                    ivDeliveryPlace.setImageResource(R.drawable.ic_loc_other);
                }
            } else{
                llDeliveryPlace.setVisibility(View.GONE);
            }

            if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.CASH.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_cash_small);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.PAYTM.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_paytm_small);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.MOBIKWIK.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_mobikwik_small);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.FREECHARGE.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_freecharge_small);
            } else{
                ivPaymentMethodVal.setImageResource(R.drawable.ic_cash_small);
            }

            if((historyResponse.getData().get(0).getDeliveryCharges() != null) && (historyResponse.getData().get(0).getDeliveryCharges() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.delivery_charges).toString(), historyResponse.getData().get(0).getDeliveryCharges().toString());
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.total_amount).toString(),
                        String.valueOf(getSubTotalAmount(historyResponse) + (historyResponse.getData().get(0).getDeliveryCharges()).intValue()));
            }

            if((historyResponse.getData().get(0).getDiscount() != null) && (historyResponse.getData().get(0).getDiscount() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.discount).toString(), historyResponse.getData().get(0).getDiscount().toString());
            }

            if((historyResponse.getData().get(0).getJugnooDeducted() != null) && (historyResponse.getData().get(0).getJugnooDeducted() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.jugnoo_cash).toString(), historyResponse.getData().get(0).getJugnooDeducted().toString());
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.amount_payable).toString(),
                        String.valueOf(getSubTotalAmount(historyResponse) - ((historyResponse.getData().get(0).getDiscount()).intValue()
                                + (historyResponse.getData().get(0).getJugnooDeducted()).intValue())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
