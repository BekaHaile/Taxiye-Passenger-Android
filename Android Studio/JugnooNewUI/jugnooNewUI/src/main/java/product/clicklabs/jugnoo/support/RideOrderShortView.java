package product.clicklabs.jugnoo.support;

import android.content.Context;
import android.graphics.Typeface;
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
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 8/24/16.
 */
public class RideOrderShortView {

	private Context context;
	private ImageView imageViewProductType, imageViewDriver;
	private TextView textViewTransactionId, textViewDetails, textViewTransactionTotal, textViewDriverName, textViewDriverCarNumber,
			textViewDate, textViewDateValue, textViewStart, textViewStartValue, textViewEnd, textViewEndValue,
			textViewOrderAddress, textViewOrderItems, textViewOrderItemsCount, textViewIssueWithRide;
	private RelativeLayout relativeLayoutDriverImage, relativeLayoutStartEnd, relativeLayoutIssueWithRide;
	private LinearLayout linearLayoutOrderData;

	public RideOrderShortView(Context context, View rootView, boolean supportMain){
		this.context = context;
		initViews(rootView, supportMain);
	}


	private void initViews(View rootView, boolean supportMain){

		imageViewProductType = (ImageView) rootView.findViewById(R.id.imageViewProductType);
		imageViewDriver = (ImageView) rootView.findViewById(R.id.imageViewDriver);

		textViewTransactionId = (TextView) rootView.findViewById(R.id.textViewTransactionId); textViewTransactionId.setTypeface(Fonts.avenirNext(context));
		textViewDetails = (TextView) rootView.findViewById(R.id.textViewDetails); textViewDetails.setTypeface(Fonts.avenirNext(context));
		textViewTransactionTotal = (TextView) rootView.findViewById(R.id.textViewTransactionTotal); textViewTransactionTotal.setTypeface(Fonts.avenirNext(context), Typeface.BOLD);
		textViewDriverName = (TextView) rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.avenirNext(context));
		textViewDriverCarNumber = (TextView) rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.avenirNext(context));
		textViewDate = (TextView) rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.avenirNext(context));
		textViewDateValue = (TextView) rootView.findViewById(R.id.textViewDateValue); textViewDateValue.setTypeface(Fonts.avenirNext(context));
		textViewStart = (TextView) rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.avenirNext(context));
		textViewStartValue = (TextView) rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.avenirNext(context));
		textViewEnd = (TextView) rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.avenirNext(context));
		textViewEndValue = (TextView) rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.avenirNext(context));
		textViewOrderAddress = (TextView) rootView.findViewById(R.id.textViewOrderAddress); textViewOrderAddress.setTypeface(Fonts.avenirNext(context));
		textViewOrderItems = (TextView) rootView.findViewById(R.id.textViewOrderItems); textViewOrderItems.setTypeface(Fonts.avenirNext(context));
		textViewOrderItemsCount = (TextView) rootView.findViewById(R.id.textViewOrderItemsCount); textViewOrderItemsCount.setTypeface(Fonts.avenirNext(context));
		textViewIssueWithRide = (TextView) rootView.findViewById(R.id.textViewIssueWithRide); textViewIssueWithRide.setTypeface(Fonts.avenirNext(context));

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

	public void updateData(EndRideData endRideData, HistoryResponse.Datum datum, boolean supportMain){
		try{
			DecimalFormat df = new DecimalFormat("#.#");
			if(endRideData != null){
				imageViewProductType.setImageResource(R.drawable.ic_support_auto);
				textViewTransactionId.setText(context.getString(R.string.transaction_id_format, endRideData.engagementId));
				textViewDetails.setText(df.format(endRideData.distance)+" km, "+endRideData.rideTime+" min");
				if("".equalsIgnoreCase(endRideData.getTripTotal())){
					textViewTransactionTotal.setText(context.getString(R.string.rupees_value_format,
							Utils.getMoneyDecimalFormat().format(endRideData.fare)));
				} else{
					textViewTransactionTotal.setText(context.getString(R.string.rupees_value_format,
							endRideData.getTripTotal()));
				}
				textViewDriverName.setText(endRideData.driverName);
				textViewDriverCarNumber.setText(endRideData.driverCarNumber);
				textViewDateValue.setText(endRideData.getRideDate());
				if(!"".equalsIgnoreCase(endRideData.driverImage)){
					Picasso.with(context).load(endRideData.driverImage).transform(new CircleTransform()).into(imageViewDriver);
				}
				textViewStart.append(" " + endRideData.pickupTime);
				textViewEnd.append(" " + endRideData.dropTime);
				textViewStartValue.setText(endRideData.pickupAddress);
				textViewEndValue.setText(endRideData.dropAddress);
				textViewIssueWithRide.setText(context.getString(R.string.issue_with_the_recent_ride));

				relativeLayoutDriverImage.setVisibility(View.VISIBLE);
				relativeLayoutStartEnd.setVisibility(View.VISIBLE);
				linearLayoutOrderData.setVisibility(View.GONE);
			}
			else if(datum != null){

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
