package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FeedbackActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideSummaryActivity;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.RideInfo;
import product.clicklabs.jugnoo.retrofit.model.SupportFAQ;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Ankit on 7/17/15.
 */
public class RideTransactionsAdapter extends RecyclerView.Adapter<RideTransactionsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    private ArrayList<RideInfo> rideInfosList = new ArrayList<>();
    private Callback callback;

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");
    private DecimalFormat decimalFormatNoDec = new DecimalFormat("#");

    public RideTransactionsAdapter(ArrayList<RideInfo> rideInfosList, Activity activity, int rowLayout) {
        this.rideInfosList = rideInfosList;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(RideTransactionsAdapter.ViewHolder holder, int position) {
        RideInfo rideInfo = rideInfosList.get(position);

        holder.id = position;
        holder.relative.setTag(holder);
        holder.linearLayoutCancel.setTag(holder);
        holder.relativeLayoutRateRide.setTag(holder);
        holder.linearLayoutRideReceipt.setTag(holder);

        holder.textViewPickupAt.setVisibility(View.GONE);
        holder.textViewAmount.setVisibility(View.VISIBLE);
        holder.linearLayoutCancel.setVisibility(View.GONE);

        holder.textViewIdValue.setText("" + rideInfo.engagementId);
        holder.textViewFromValue.setText(rideInfo.pickupAddress);
        holder.textViewToValue.setText(rideInfo.dropAddress);
        holder.textViewDetails.setText("Details: ");

        if(0 == rideInfo.isCancelledRide) {
            if (rideInfo.rideTime == 1) {
                holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
                        + decimalFormatNoDec.format(rideInfo.rideTime) + " minute, " + rideInfo.date);
            } else {
                holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
                        + decimalFormatNoDec.format(rideInfo.rideTime) + " minutes, " + rideInfo.date);
            }
            holder.textViewAmount.setText(activity.getResources().getString(R.string.rupee) + Utils.getMoneyDecimalFormat().format(rideInfo.amount));

            if (1 != rideInfo.isRatedBefore) {
                holder.relativeLayoutRateRide.setVisibility(View.VISIBLE);
                holder.imageViewDiv.setVisibility(View.GONE);
            } else {
                holder.relativeLayoutRateRide.setVisibility(View.GONE);
                holder.imageViewDiv.setVisibility(View.VISIBLE);
            }
            holder.linearLayoutRideReceipt.setVisibility(View.VISIBLE);
            holder.textViewRideCancelled.setVisibility(View.GONE);
            holder.relativeLayoutTo.setVisibility(View.VISIBLE);
        }
        else{
            holder.textViewDetailsValue.setText(rideInfo.date+",");
            holder.textViewAmount.setText(activity.getResources().getString(R.string.rupee) + Utils.getMoneyDecimalFormat().format(rideInfo.amount));
            holder.relativeLayoutRateRide.setVisibility(View.GONE);
            holder.imageViewDiv.setVisibility(View.VISIBLE);
            holder.linearLayoutRideReceipt.setVisibility(View.GONE);
            holder.textViewRideCancelled.setVisibility(View.VISIBLE);
            holder.relativeLayoutTo.setVisibility(View.GONE);
        }

        holder.linearLayoutCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.relativeLayoutRateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RideTransactionsAdapter.ViewHolder holder = (RideTransactionsAdapter.ViewHolder) v.getTag();
                    Intent intent = new Intent(activity, FeedbackActivity.class);
                    intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.PAST_RIDE.getOrdinal());
                    intent.putExtra("position", holder.id);
                    intent.putExtra("driver_id", rideInfosList.get(holder.id).driverId);
                    intent.putExtra("engagement_id", rideInfosList.get(holder.id).engagementId);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(FlurryEventNames.RIDE_RATED_ON_RIDE_HISTORY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.linearLayoutRideReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(AppStatus.getInstance(activity).isOnline(activity)) {
                        RideTransactionsAdapter.ViewHolder holder = (RideTransactionsAdapter.ViewHolder) v.getTag();
                        Intent intent = new Intent(activity, RideSummaryActivity.class);
                        intent.putExtra("engagement_id", rideInfosList.get(holder.id).engagementId);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                    else{
                        DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
                    }
                    FlurryEventLogger.event(FlurryEventNames.RIDE_SUMMARY_CHECKED_LATER);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

	}

    @Override
    public int getItemCount() {
        return rideInfosList == null ? 0 : rideInfosList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPickupAt, textViewIdValue, textViewFrom, textViewFromValue, textViewTo,
                textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewCancel,
                textViewRateRide, textViewRideCancelled;
        public ImageView imageViewDiv;
        public RelativeLayout relativeLayoutTo, relativeLayoutRateRide;
        public LinearLayout linearLayoutCancel;
        public LinearLayout linearLayoutRideReceipt;
        public RelativeLayout relative;
        public int id;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewPickupAt = (TextView) convertView.findViewById(R.id.textViewPickupAt); textViewPickupAt.setTypeface(Fonts.mavenLight(context));
            ((TextView)convertView.findViewById(R.id.textViewId)).setTypeface(Fonts.mavenLight(context));
            textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue); textViewIdValue.setTypeface(Fonts.mavenLight(context));
            textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom); textViewFrom.setTypeface(Fonts.mavenLight(context));
            textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue); textViewFromValue.setTypeface(Fonts.mavenLight(context));
            textViewTo = (TextView) convertView.findViewById(R.id.textViewTo); textViewTo.setTypeface(Fonts.mavenLight(context));
            textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue); textViewToValue.setTypeface(Fonts.mavenLight(context));
            textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails); textViewDetails.setTypeface(Fonts.mavenLight(context));
            textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue); textViewDetailsValue.setTypeface(Fonts.mavenLight(context));
            textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount);
            textViewAmount.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            textViewCancel = (TextView) convertView.findViewById(R.id.textViewCancel); textViewCancel.setTypeface(Fonts.mavenLight(context));
            textViewRateRide = (TextView) convertView.findViewById(R.id.textViewRateRide); textViewRateRide.setTypeface(Fonts.mavenLight(context));
            textViewRideCancelled = (TextView) convertView.findViewById(R.id.textViewRideCancelled);
            textViewRideCancelled.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);

            imageViewDiv = (ImageView) convertView.findViewById(R.id.imageViewDiv);

            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            linearLayoutCancel = (LinearLayout) convertView.findViewById(R.id.linearLayoutCancel);
            relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
            relativeLayoutRateRide = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutRateRide);
            linearLayoutRideReceipt = (LinearLayout) convertView.findViewById(R.id.linearLayoutRideReceipt);
        }
    }

    public interface Callback{
        void onClick(int position, SupportFAQ supportFAQ);
    }

}
