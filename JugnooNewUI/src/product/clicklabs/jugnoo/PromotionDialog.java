package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionApplyMode;
import product.clicklabs.jugnoo.datastructure.PromotionDialogEventHandler;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class PromotionDialog {

	
	private Dialog dialog;
	
	private FrameLayout frameLayout;
	private TextView textViewTitle, textViewMessage;
	private Button btnOk, btnCancel;
	private ListView listViewPromotions;
	
	private ArrayList<PromoCoupon> promoCouponList;
	private PromotionsListAdapter promotionsListAdapter;
	
	
	private PromoCoupon selectedCoupon;
	
	private Location promoLocation;
	
	private PromotionApplyMode promotionApplyMode = PromotionApplyMode.BEFORE_RIDE;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.#");
	
	public PromotionDialog(Location location, PromotionApplyMode promotionApplyMode){
		this.promoCouponList = new ArrayList<PromoCoupon>();
		this.selectedCoupon = null;
		this.promoLocation = location;
		this.promotionApplyMode = promotionApplyMode;
	}
	
	
	public void showPromoAlert(final Activity activity, final PromotionDialogEventHandler promotionDialogEventHandler) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_ride_promotion);

			frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(activity));
			textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Data.latoRegular(activity));

			listViewPromotions = (ListView) dialog.findViewById(R.id.listViewPromotions);
			promotionsListAdapter = new PromotionsListAdapter(activity);
			listViewPromotions.setAdapter(promotionsListAdapter);
			
			btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			
			btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(selectedCoupon != null){
						dialog.dismiss();
						promotionDialogEventHandler.onOkPressed(selectedCoupon);
					}
					else{
						Toast.makeText(activity, "Please select some coupon first", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					promotionDialogEventHandler.onCancelPressed();
				}
			});
			

			dialog.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	class ViewHolderPromotion {
		TextView textViewCouponTitle, textViewTNC;
		LinearLayout relative;
		int id;
	}

	class PromotionsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderPromotion holder;
		Activity context;
		Drawable drawableWhite, drawableYellow;
		public PromotionsListAdapter(Activity context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.drawableWhite = context.getResources().getDrawable(R.drawable.background_white);
			this.drawableYellow = context.getResources().getDrawable(R.drawable.background_yellow);
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
				
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Data.latoRegular(context));
				holder.textViewTNC = (TextView) convertView.findViewById(R.id.textViewTNC); holder.textViewTNC.setTypeface(Data.latoLight(context));
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				holder.textViewTNC.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(656, LayoutParams.WRAP_CONTENT));
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
				holder.relative.setBackground(drawableYellow);
				holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.white));
				holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.white));
			}
			else{
				holder.relative.setBackground(drawableWhite);
				holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.grey_dark));
				holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.grey_dark));
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
						DialogPopup.alertPopup(context, "", ((CouponInfo)promoCoupon).description);
					}
					else if(promoCoupon instanceof PromotionInfo){
						if(((PromotionInfo)promoCoupon).id > 0){
							DialogPopup.alertPopupHtml(context, "", ((PromotionInfo)promoCoupon).terms);
						}
					}
				}
			});
			
			return convertView;
		}

	}
	
	
	
	
	public void fetchPromotionsAPI(final Activity activity, final PromotionDialogEventHandler promotionDialogEventHandler) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", ""+promoLocation.getLatitude());
			params.put("longitude", ""+promoLocation.getLongitude());

			Log.i("params", "=" + params);
			
			AsyncHttpClient fetchPromotionClient = Data.getClient();
			fetchPromotionClient.post(Data.SERVER_URL + "/show_available_promotions", params,
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
										
//										{
//										    "flag": 174,
//										    "coupons": [
//										        {
//										            "title": "Free ride",
//										            "subtitle": "upto Rs. 100",
//										            "description": "Your next ride",
//										            "discount": 100,
//										            "maximum": 100,
//										            "image": "",
//										            "type": 0,
//										            "redeemed_on": "0000-00-00 00:00:00",
//										            "status": 1,
//										            "expiry_date": "December 31st 2015"
//										        }
//										    ],
//										    "promotions": [
//										        {
//										            "promo_id": 1,
//										            "title": "Flat 100% off",
//										            "terms_n_conds": "t"
//										        }
//										    ],
//										    "dynamic_factor": "1.0"
//										}
										
										
										promoCouponList.clear();
										
										JSONArray jCouponsArr = jObj.getJSONArray("coupons");
										for(int i=0; i<jCouponsArr.length(); i++){
											JSONObject coData = jCouponsArr.getJSONObject(i);
											promoCouponList.add(new CouponInfo(coData.getInt("account_id"),
													coData.getInt("type"), 
													coData.getInt("status"), 
													coData.getString("title"), 
													coData.getString("subtitle"), 
													coData.getString("description"), 
													coData.getString("image"), 
													coData.getString("redeemed_on"), 
													coData.getString("expiry_date"), 
													coData.getDouble("discount"), 
													coData.getDouble("maximum")));
										}
										
										JSONArray jPromoArr = jObj.getJSONArray("promotions");
										for(int i=0; i<jPromoArr.length(); i++){
											JSONObject coData = jPromoArr.getJSONObject(i);
											promoCouponList.add(new PromotionInfo(coData.getInt("promo_id"), 
													coData.getString("title"), 
													coData.getString("terms_n_conds")));
										}
										
										
										if(promoCouponList.size() > 0){
											
											promoCouponList.add(new PromotionInfo(-1, 
													"Don't apply coupon on this ride", 
													""));
											
											
											selectedCoupon = promoCouponList.get(0);
											
											showPromoAlert(activity, promotionDialogEventHandler);
											
											double dynamicFactor = jObj.getDouble("dynamic_factor");
											
											if(PromotionApplyMode.BEFORE_SCHEDULE.getOrdinal() == promotionApplyMode.getOrdinal()){
												textViewMessage.setVisibility(View.VISIBLE);
												textViewMessage.setText("Please note that price rates applicable at the scheduled time might be different");
												
												SpannableString sstr = new SpannableString("Choose one for this ride");
												final StyleSpan bss = new StyleSpan(Typeface.BOLD);
												sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
												
												textViewTitle.setText("Ride Scheduled. You have coupons available.\n");
												textViewTitle.append(sstr);
												
											}
											else{
												if(dynamicFactor > 1){
													textViewMessage.setVisibility(View.VISIBLE);
													textViewMessage.setText("Current rates are "+decimalFormat.format(dynamicFactor)
															+"x higher than normal to maintain availability");
												}
												else{
													textViewMessage.setVisibility(View.GONE);
												}
												textViewTitle.setText("You have coupons available. Choose one");
											}
											
											promotionsListAdapter.notifyDataSetChanged();
											
										}
										else{
											selectedCoupon = null;
											promotionDialogEventHandler.onNoCouponsAvailable();
										}
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
	
	
}
