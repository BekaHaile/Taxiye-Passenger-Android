package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


public class RideTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity activity;
    private int rowLayout;
    private ArrayList<HistoryResponse.Datum> historyData;
    private int totalRides;
    private Callback callback;

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");
    private DecimalFormat decimalFormatNoDec = new DecimalFormat("#");

    public RideTransactionsAdapter(ArrayList<HistoryResponse.Datum> historyData, Activity activity, int rowLayout, Callback callback,
                                   int totalRides) {
        this.historyData = historyData;
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.callback = callback;
        this.totalRides = totalRides;
    }

    public void notifyList(int totalRides) {
        this.totalRides = totalRides;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_show_more, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewFooterHolder(v, activity);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        if (viewholder instanceof ViewHolder) {
            HistoryResponse.Datum orderHistory = getItem(position);
            Log.d("TAG", "orderHistory id = "+orderHistory.getOrderId());
            ViewHolder holder = (ViewHolder) viewholder;
            holder.relative.setTag(position);
            if (orderHistory.getProductType() == ProductType.AUTO.getOrdinal()) {

                holder.textViewIdValue.setText("" + orderHistory.getEngagementId());
                holder.textViewFromValue.setText(orderHistory.getPickupAddress());
                holder.textViewToValue.setText(orderHistory.getDropAddress());
                holder.textViewDetails.setText("Details: ");

                holder.textViewFrom.setText("From: ");
                holder.textViewTo.setText("To: ");
                holder.textViewTo.setVisibility(View.VISIBLE);

                if (0 == orderHistory.getIsCancelledRide()) {
                    holder.textViewStatusValue.setText("Ride Completed");
                    holder.textViewStatusValue.setTextColor(activity.getResources().getColor(R.color.theme_color));

                    if (orderHistory.getRideTime() == 1) {
                        holder.textViewDetailsValue.setText(decimalFormat.format(orderHistory.getDistance()) + " km, "
                                + decimalFormatNoDec.format(orderHistory.getRideTime()) + " minute, " + orderHistory.getDate());
                    } else {
                        holder.textViewDetailsValue.setText(decimalFormat.format(orderHistory.getDistance()) + " km, "
                                + decimalFormatNoDec.format(orderHistory.getRideTime()) + " minutes, " + orderHistory.getDate());
                    }
                    holder.textViewAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(orderHistory.getAmount())));
                    holder.textViewAmount.setTextColor(activity.getResources().getColor(R.color.theme_color));

                    holder.textViewRideCancelled.setVisibility(View.GONE);
                    holder.relativeLayoutTo.setVisibility(View.VISIBLE);
                } else {
                    holder.textViewStatusValue.setText("Ride Cancelled");
                    holder.textViewStatusValue.setTextColor(activity.getResources().getColor(R.color.red));
                    holder.textViewDetailsValue.setText(orderHistory.getDate() + ",");
                    holder.textViewAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                            Utils.getMoneyDecimalFormat().format(orderHistory.getAmount())));
                    holder.textViewAmount.setTextColor(activity.getResources().getColor(R.color.red));
                    holder.textViewRideCancelled.setVisibility(View.VISIBLE);
                    holder.relativeLayoutTo.setVisibility(View.GONE);
                }

            } else if (orderHistory.getProductType() == ProductType.FRESH.getOrdinal() ||
                    orderHistory.getProductType() == ProductType.MEALS.getOrdinal()) {
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getOrderId()));
                holder.textViewStatusValue.setText(orderHistory.getOrderStatus());
                try{
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch(Exception e){
                    e.printStackTrace();
                }

                holder.textViewFrom.setText("To : ");


                holder.textViewFromValue.setText(orderHistory.getDeliveryAddress());
                holder.textViewDetails.setText("Details: ");
                holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormat(DateOperations
                        .utcToLocalTZ(orderHistory.getOrderTime())));

                holder.relativeLayoutTo.setVisibility(View.GONE);
                holder.textViewRideCancelled.setVisibility(View.GONE);
                try {
                    holder.textViewAmount.setText(String.format(activity.getResources()
                                    .getString(R.string.rupees_value_format_without_space),
                            com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));

//                    holder.textViewAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
//                            orderHistory.getOrderAmount()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        callback.onClick(position, getItem(position));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (viewholder instanceof ViewFooterHolder) {
            ViewFooterHolder holder = (ViewFooterHolder) viewholder;
            holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onShowMoreClick();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (historyData == null || historyData.size() == 0) {
            return 0;
        } else {
            if (totalRides > historyData.size()) {
                return historyData.size() + 1;
            } else {
                return historyData.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == historyData.size();
    }

    private HistoryResponse.Datum getItem(int position) {
        if (isPositionFooter(position)) {
            return null;
        }
        return historyData.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewIdValue, textViewFrom, textViewFromValue, textViewTo,
                textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount,
                textViewRateRide, textViewRideCancelled;
        public TextView textViewStatus, textViewStatusValue;
        public RelativeLayout relativeLayoutTo;
        public RelativeLayout relative;

        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            ((TextView) convertView.findViewById(R.id.textViewId)).setTypeface(Fonts.mavenMedium(context));
            textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue);
            textViewIdValue.setTypeface(Fonts.mavenMedium(context));
            textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom);
            textViewFrom.setTypeface(Fonts.mavenMedium(context));
            textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue);
            textViewFromValue.setTypeface(Fonts.mavenRegular(context));
            textViewTo = (TextView) convertView.findViewById(R.id.textViewTo);
            textViewTo.setTypeface(Fonts.mavenMedium(context));
            textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue);
            textViewToValue.setTypeface(Fonts.mavenRegular(context));
            textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails);
            textViewDetails.setTypeface(Fonts.mavenMedium(context));
            textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue);
            textViewDetailsValue.setTypeface(Fonts.mavenRegular(context));
            textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount);
            textViewAmount.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
            textViewRateRide = (TextView) convertView.findViewById(R.id.textViewRateRide);
            textViewRateRide.setTypeface(Fonts.mavenLight(context));
            textViewRideCancelled = (TextView) convertView.findViewById(R.id.textViewRideCancelled);
            textViewRideCancelled.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);

            textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus);
            textViewStatus.setTypeface(Fonts.mavenRegular(context));
            textViewStatusValue = (TextView) convertView.findViewById(R.id.textViewStatusValue);
            textViewStatusValue.setTypeface(Fonts.mavenRegular(context));

            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
        }
    }


    public class ViewFooterHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutShowMore;
        public TextView textViewShowMore;

        public ViewFooterHolder(View convertView, Activity context) {
            super(convertView);
            relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
            textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore);
            textViewShowMore.setTypeface(Fonts.mavenLight(context));
            textViewShowMore.setText(context.getResources().getString(R.string.show_more));
        }
    }

    public interface Callback {
        void onClick(int position, HistoryResponse.Datum rideInfo);

        void onShowMoreClick();

        void onRateRideClick(int position, HistoryResponse.Datum rideInfo);
    }

}
