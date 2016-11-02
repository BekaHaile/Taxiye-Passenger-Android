package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
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

public class OrderStatusActivity extends BaseActivity {

    private RelativeLayout relative;
    private TextView textViewTitle, tvOrderStatus, tvOrderStatusVal, tvOrderTime, tvOrderTimeVal, tvDeliveryTime, tvDeliveryTimeVal, tvDeliveryTo,
            tvDelveryPlace, tvDeliveryToVal, tvSubAmount, tvSubAmountVal;
    private ImageView imageViewBack;
    private int orderId;
    private NonScrollListView listViewOrder;
    private OrderItemsAdapter orderItemsAdapter;
    private LinearLayout llFinalAmount;
    private ArrayList<HistoryResponse.OrderItem> subItemsOrders = new ArrayList<HistoryResponse.OrderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        if(getIntent().hasExtra(Constants.KEY_ORDER_ID)){
            orderId = getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0);
        }

        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewTitle.setText("Order #"+orderId);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        tvOrderStatus = (TextView) findViewById(R.id.tvOrderStatus); tvOrderStatus.setTypeface(Fonts.mavenMedium(this));
        tvOrderStatusVal = (TextView) findViewById(R.id.tvOrderStatusVal); tvOrderStatusVal.setTypeface(Fonts.mavenMedium(this));
        tvOrderTime = (TextView) findViewById(R.id.tvOrderTime); tvOrderTime.setTypeface(Fonts.mavenMedium(this));
        tvOrderTimeVal = (TextView) findViewById(R.id.tvOrderTimeVal);tvOrderTimeVal.setTypeface(Fonts.mavenMedium(this));
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime); tvDeliveryTime.setTypeface(Fonts.mavenMedium(this));
        tvDeliveryTimeVal = (TextView) findViewById(R.id.tvDeliveryTimeVal); tvDeliveryTimeVal.setTypeface(Fonts.mavenMedium(this));
        tvDeliveryTo = (TextView) findViewById(R.id.tvDeliveryTo); tvDeliveryTo.setTypeface(Fonts.mavenMedium(this));
        tvDelveryPlace = (TextView) findViewById(R.id.tvDelveryPlace); tvDelveryPlace.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvDeliveryToVal = (TextView) findViewById(R.id.tvDeliveryToVal); tvDeliveryToVal.setTypeface(Fonts.mavenMedium(this));
        tvSubAmount = (TextView) findViewById(R.id.tvSubAmount); tvSubAmount.setTypeface(Fonts.mavenMedium(this));
        tvSubAmountVal = (TextView) findViewById(R.id.tvSubAmountVal); tvSubAmountVal.setTypeface(Fonts.mavenMedium(this));
        llFinalAmount = (LinearLayout) findViewById(R.id.llFinalAmount);


        listViewOrder = (NonScrollListView) findViewById(R.id.listViewCart);
        orderItemsAdapter = new OrderItemsAdapter(OrderStatusActivity.this, subItemsOrders);
        listViewOrder.setAdapter(orderItemsAdapter);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        getOrderData(OrderStatusActivity.this);
    }

    public void performBackPressed(){
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
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
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
        RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
        TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges);
        TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal);
        tvDelCharges.setText(fieldText);
        tvDelChargesVal.setText(fieldTextVal);
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
            tvDeliveryTimeVal.setText(historyResponse.getData().get(0).getExpectedDeliveryDate());
            tvDeliveryToVal.setText(historyResponse.getData().get(0).getDeliveryAddress());
            try {
                tvSubAmountVal.setText(String.format(getResources().getString(R.string.rupees_value_format), Integer.toString(getSubTotalAmount(historyResponse))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvOrderStatusVal.setTextColor(Color.parseColor(historyResponse.getData().get(0).getOrderStatusColor()));

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
                        String.valueOf(getSubTotalAmount(historyResponse) - ((historyResponse.getData().get(0).getDiscount()).intValue() + (historyResponse.getData().get(0).getJugnooDeducted()).intValue())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
