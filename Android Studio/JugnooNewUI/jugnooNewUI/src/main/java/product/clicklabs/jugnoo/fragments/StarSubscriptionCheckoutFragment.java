package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 30/12/16.
 */

public class StarSubscriptionCheckoutFragment extends Fragment implements PromoCouponsAdapter.Callback {

    private View rootView;
    private JugnooStarActivity activity;
    private TextView tvPaymentPlan, tvPlanAmount;
    private Button bPlaceOrder;
    private LinearLayout linearLayoutOffers, linearLayoutRoot;
    private NonScrollListView listViewOffers;
    private PromoCouponsAdapter promoCouponsAdapter;
    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    public static StarSubscriptionCheckoutFragment newInstance(String subscription){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString("plan", subscription);
        fragment.setArguments(bundle);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_star_subscription_checkout, container, false);

        activity = (JugnooStarActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Bundle bundle = getArguments();
            String plan = bundle.getString("plan", "");
            SubscriptionData.Subscription subscription = new Gson().fromJson(plan, SubscriptionData.Subscription.class);


            tvPaymentPlan = (TextView) rootView.findViewById(R.id.tvPaymentPlan); tvPaymentPlan.setTypeface(Fonts.mavenMedium(activity));
            tvPlanAmount = (TextView) rootView.findViewById(R.id.tvPlanAmount); tvPlanAmount.setTypeface(Fonts.mavenMedium(activity));
            bPlaceOrder = (Button) rootView.findViewById(R.id.bPlaceOrder); bPlaceOrder.setTypeface(Fonts.mavenMedium(activity));

            tvPaymentPlan.setText(subscription.getDescription());
            tvPlanAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                    Utils.getMoneyDecimalFormat().format(subscription.getAmount())));

            linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
            listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
            promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
            listViewOffers.setAdapter(promoCouponsAdapter);

            updateCouponsDataView();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void updateCouponsDataView(){
        try {
            promoCoupons = Data.userData.getPromoCoupons();
            if(promoCoupons != null) {
                if(promoCoupons.size() > 0){
                    linearLayoutOffers.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutOffers.setVisibility(View.GONE);
                }
                promoCouponsAdapter.setList(promoCoupons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCouponSelected() {

    }

    @Override
    public PromoCoupon getSelectedCoupon() {
        return null;
    }

    @Override
    public void setSelectedCoupon(int position) {

    }
}
