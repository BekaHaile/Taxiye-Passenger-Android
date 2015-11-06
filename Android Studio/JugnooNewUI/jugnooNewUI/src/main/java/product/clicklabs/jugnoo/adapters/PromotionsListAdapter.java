package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 7/6/15.
 */
public class PromotionsListAdapter extends BaseAdapter implements FlurryEventNames {

    class ViewHolderPromotion {
        TextView textViewCouponTitle, textViewTNC;
        LinearLayout relative;
        int id;
    }



    LayoutInflater mInflater;
    ViewHolderPromotion holder;
    Activity context;

    private ArrayList<PromoCoupon> promoCouponList;
    private PromoCoupon selectedCoupon;

    private PromotionListEventHandler promotionListEventHandler;

    public PromotionsListAdapter(Activity context, PromotionListEventHandler promotionListEventHandler) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.promoCouponList = new ArrayList<>();
        this.selectedCoupon = null;
        this.promotionListEventHandler = promotionListEventHandler;
    }

    public PromoCoupon getSelectedCoupon(){
        return selectedCoupon;
    }

    @Override
    public int getCount() {
        return promoCouponList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderPromotion();
            convertView = mInflater.inflate(R.layout.list_item_promo_coupon, null);

            holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Fonts.latoRegular(context));
            holder.textViewTNC = (TextView) convertView.findViewById(R.id.textViewTNC); holder.textViewTNC.setTypeface(Fonts.latoLight(context), Typeface.BOLD);

            holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

            holder.relative.setTag(holder);
            holder.textViewTNC.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderPromotion) convertView.getTag();
        }

        holder.id = position;

        PromoCoupon promoCoupon = promoCouponList.get(position);

        if(promoCoupon instanceof CouponInfo){
            holder.textViewCouponTitle.setText(((CouponInfo)promoCoupon).title);
        }
        else if(promoCoupon instanceof PromotionInfo){
            holder.textViewCouponTitle.setText(((PromotionInfo)promoCoupon).title);
        }

        if(selectedCoupon.id == promoCoupon.id){
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.white));
        }
        else{
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.black_text));
            holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.black_text));
        }

        if(promoCoupon.id > -1){
            holder.textViewTNC.setVisibility(View.VISIBLE);
        }
        else{
            holder.textViewTNC.setVisibility(View.GONE);
        }



        holder.relative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder = (ViewHolderPromotion) v.getTag();
                selectedCoupon = promoCouponList.get(holder.id);
                notifyDataSetChanged();
            }
        });

        holder.textViewTNC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder = (ViewHolderPromotion) v.getTag();
                PromoCoupon promoCoupon = promoCouponList.get(holder.id);
                if(promoCoupon instanceof CouponInfo){
                    DialogPopup.alertPopupLeftOriented(context, "", ((CouponInfo) promoCoupon).description);
                    if(((CouponInfo)promoCoupon).id == -1){
                        FlurryEventLogger.event(COUPON_SELECTION_NOT_MADE);
                    }
                    else{
                        FlurryEventLogger.event(COUPON_SELECTION_MADE);
                    }
                }
                else if(promoCoupon instanceof PromotionInfo){
                    if(((PromotionInfo)promoCoupon).id > 0){
                        DialogPopup.alertPopupHtml(context, "", ((PromotionInfo)promoCoupon).terms);
						FlurryEventLogger.event(COUPON_SELECTION_MADE);
                    }
                }
            }
        });

        return convertView;
    }



    Handler dismissHandler;
    Runnable dismissRunnable;
    public void startDismissHandler(){
        stopDismissHandler();
        try {
            dismissHandler = new Handler();
            dismissRunnable = new Runnable() {

                @Override
                public void run() {
                    try {promotionListEventHandler.onDismiss();} catch (Exception e) {e.printStackTrace();}
                }
            };
            dismissHandler.postDelayed(dismissRunnable, 2 * 60000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopDismissHandler(){
        try{
            if(dismissHandler != null && dismissRunnable != null){
                dismissHandler.removeCallbacks(dismissRunnable);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public void fetchPromotionsAPI(final Activity activity, LatLng promoLatLng) {
        if (AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("latitude", ""+promoLatLng.latitude);
            params.put("longitude", ""+promoLatLng.longitude);

            Log.i("params", "=" + params);

            AsyncHttpClient fetchPromotionClient = Data.getClient();
            fetchPromotionClient.post(Config.getServerUrl() + "/show_available_promotions", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e("Server response show_available_promotions", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
                                int flag = jObj.getInt("flag");
                                if(ApiResponseFlags.AVAILABLE_PROMOTIONS.getOrdinal() == flag){
                                    promoCouponList.clear();
                                    promoCouponList.addAll(JSONParser.parsePromoCoupons(jObj));
                                    JSONParser.parseCurrentFareStructure(jObj);

//                                    dynamicFactor = jObj.getDouble("dynamic_factor");

                                    if(promoCouponList.size() > 0) {
                                        promoCouponList.add(new CouponInfo(-1, "Don't apply coupon on this ride"));
                                        selectedCoupon = promoCouponList.get(0);
                                        startDismissHandler();
                                    }
                                    else{
                                        selectedCoupon = new CouponInfo(0, "");
                                    }

                                    PromotionsListAdapter.this.notifyDataSetChanged();
									promotionListEventHandler.onPromoListFetched(promoCouponList.size());
                                }
                                else{
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
                            }
                        }  catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }

                        DialogPopup.dismissLoadingDialog();
                    }

                });
        }
    }


	public interface PromotionListEventHandler {
		void onDismiss();
		void onPromoListFetched(int totalPromoCoupons);
	}


}
