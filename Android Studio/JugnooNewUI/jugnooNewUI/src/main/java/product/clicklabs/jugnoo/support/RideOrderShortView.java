package product.clicklabs.jugnoo.support;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 8/24/16.
 */
public class RideOrderShortView {

	private Context context;
	private ImageView imageViewProductType, imageViewDriver;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotal, textViewTripTotalValue,
			textViewDate, textViewStart, textViewStartValue, textViewEnd, textViewEndValue, textViewStatus,
			textViewOrderAddress, textViewIssueWithRide;
	private RelativeLayout relativeLayoutDriverImage, relativeLayoutStartEnd, relativeLayoutIssueWithRide;
	private LinearLayout linearLayoutOrderData;

	public RideOrderShortView(Context context, View rootView, boolean supportMain){
		this.context = context;
		initViews(rootView, supportMain);
	}


	private void initViews(View rootView, boolean supportMain){

		imageViewProductType = (ImageView) rootView.findViewById(R.id.imageViewProductType);
		imageViewDriver = (ImageView) rootView.findViewById(R.id.imageViewDriver);

		textViewDriverName = (TextView) rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenRegular(context));
		textViewDriverCarNumber = (TextView) rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenMedium(context));
		textViewTripTotal = (TextView) rootView.findViewById(R.id.textViewTripTotal); textViewTripTotal.setTypeface(Fonts.mavenRegular(context));
		textViewTripTotalValue = (TextView) rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.avenirNext(context));
		textViewDate = (TextView) rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenMedium(context));
		textViewStart = (TextView) rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.mavenMedium(context));
		textViewStartValue = (TextView) rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenRegular(context));
		textViewEnd = (TextView) rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.mavenMedium(context));
		textViewEndValue = (TextView) rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenRegular(context));
		textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus); textViewStatus.setTypeface(Fonts.mavenMedium(context));
		textViewOrderAddress = (TextView) rootView.findViewById(R.id.textViewOrderAddress); textViewOrderAddress.setTypeface(Fonts.mavenRegular(context));
		textViewIssueWithRide = (TextView) rootView.findViewById(R.id.textViewIssueWithRide); textViewIssueWithRide.setTypeface(Fonts.mavenMedium(context));

		relativeLayoutDriverImage = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverImage);
		relativeLayoutStartEnd = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutStartEnd);
		relativeLayoutIssueWithRide = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutIssueWithRide);
		linearLayoutOrderData = (LinearLayout) rootView.findViewById(R.id.linearLayoutOrderData);

		if(supportMain){
			relativeLayoutIssueWithRide.setVisibility(View.VISIBLE);
		} else{
			relativeLayoutIssueWithRide.setVisibility(View.GONE);
		}
	}

	public void updateData(EndRideData endRideData, HistoryResponse.Datum datum){
		try{
			DecimalFormat df = new DecimalFormat("#.#");
			DecimalFormat df0 = new DecimalFormat("#");
			if(endRideData != null){
				imageViewProductType.setImageResource(R.drawable.ic_auto_grey);
				textViewIssueWithRide.setText(context.getString(R.string.issue_with_the_recent_ride));
				textViewDriverName.setText(endRideData.driverName);
				textViewDriverCarNumber.setText(endRideData.driverCarNumber);
				textViewTripTotal.setText(R.string.trip_total);
				if("".equalsIgnoreCase(endRideData.getTripTotal())){
					textViewTripTotalValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.fare));
				} else{
					textViewTripTotalValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getTripTotal()));
				}
				textViewDate.setText(context.getString(R.string.date_colon_format, endRideData.getEngagementDate()));
				textViewStart.append(" " + endRideData.pickupTime);
				textViewEnd.append(" " + endRideData.dropTime);
				textViewStartValue.setText(endRideData.pickupAddress);
				textViewEndValue.setText(endRideData.dropAddress);

				if(!"".equalsIgnoreCase(endRideData.driverImage)){
					float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
					Picasso.with(context).load(endRideData.driverImage).transform(new CircleTransform())
							.resize((int)(94f * minRatio), (int)(94f * minRatio)).centerCrop()
							.into(imageViewDriver);
				}

				float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayoutDriverImage.getLayoutParams();
				params.width = (int) (ratio * 96f);
				relativeLayoutDriverImage.setLayoutParams(params);
				relativeLayoutStartEnd.setVisibility(View.VISIBLE);
				linearLayoutOrderData.setVisibility(View.GONE);
			}
			else if(datum != null){
				if(datum.getProductType() == ProductType.FRESH.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_grocery_grey_vector);
				} else if(datum.getProductType() == ProductType.MEALS.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_meals_grey);
				} else if(datum.getProductType() == ProductType.GROCERY.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_fresh_grey);
				} else if(datum.getProductType() == ProductType.MENUS.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_menus_grey);
				} else if(datum.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_menus_grey);
				} else if(datum.getProductType() == ProductType.PAY.getOrdinal()){
					imageViewProductType.setImageResource(R.drawable.ic_pay_grey);
				}
				textViewIssueWithRide.setText(context.getString(R.string.issue_with_the_recent_order));
				if(datum.getProductType() == ProductType.MENUS.getOrdinal()
						|| datum.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
					textViewDriverName.setText(context.getString(R.string.order_date_colon).replace(":", ""));
					textViewDriverCarNumber.setText(DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getOrderTime())));
				} else {
					textViewDriverName.setText(context.getString(R.string.delivery_date));
					textViewDriverCarNumber.setText(datum.getExpectedDeliveryDate());
				}
				textViewTripTotal.setText(R.string.order_total);
				textViewTripTotalValue.setText(context.getString(R.string.rupees_value_format,
						Utils.getMoneyDecimalFormat().format(datum.getDiscountedAmount())));
				textViewStatus.setText(datum.getOrderStatus());
				try{textViewStatus.setTextColor(Color.parseColor(datum.getOrderStatusColor()));} catch(Exception e){}
				textViewOrderAddress.setText(datum.getDeliveryAddress());


				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayoutDriverImage.getLayoutParams();
				params.width = 0;
				relativeLayoutDriverImage.setLayoutParams(params);
				relativeLayoutStartEnd.setVisibility(View.GONE);
				linearLayoutOrderData.setVisibility(View.VISIBLE);

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
