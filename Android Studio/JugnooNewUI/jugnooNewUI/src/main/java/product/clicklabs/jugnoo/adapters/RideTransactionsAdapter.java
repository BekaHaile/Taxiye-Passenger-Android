package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.pros.ui.adapters.ProsSuperCategoriesAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
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
        HistoryResponse.Datum orderHistory = getItem(position);
        if (viewholder instanceof ViewHolder && orderHistory != null) {
            ViewHolder holder = (ViewHolder) viewholder;
            holder.relative.setTag(position);
            if (orderHistory.getProductType() == ProductType.AUTO.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getEngagementId()));
                holder.textViewFrom.setText(R.string.from_colon);
                holder.textViewFromValue.setText(orderHistory.getPickupAddress());
                holder.textViewTo.setText(R.string.to_colon);
                holder.textViewToValue.setText(orderHistory.getDropAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                holder.textViewAmount.setText(activity.getString(R.string.rupees_value_format_without_space,
                        Utils.getMoneyDecimalFormat().format(orderHistory.getAmount())));
                holder.imageViewProductType.setImageResource(R.drawable.ic_auto_grey);

                try {
                    int vehicleType = orderHistory.getVehicleType();
                    int rideType = orderHistory.getRideType();
                    holder.imageViewProductType.setImageResource(getVehicleTypeDrawable(vehicleType, rideType));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (0 == orderHistory.getIsCancelledRide()) {
                    if(orderHistory.getAutosStatusText() == null) {
                        holder.textViewStatusValue.setText(R.string.ride_compeleted);
                    } else {
                        holder.textViewStatusValue.setText(orderHistory.getAutosStatusText());
                    }
                    try{
                        holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getAutosStatusColor()));
                    } catch (Exception e){
                        holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, R.color.text_color_blue));
                    }
                    holder.textViewDetailsValue.setText(orderHistory.getDate() + ", " + decimalFormat.format(orderHistory.getDistance()) + " km, "+decimalFormatNoDec.format(orderHistory.getRideTime()) + " min");
                    holder.relativeLayoutTo.setVisibility(View.VISIBLE);
                } else {
                    if(orderHistory.getAutosStatusText() == null) {
                        holder.textViewStatusValue.setText(R.string.ride_cancelled);
                    } else {
                        holder.textViewStatusValue.setText(orderHistory.getAutosStatusText());
                    }
                    try{
                        holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getAutosStatusColor()));
                    } catch (Exception e){
                        holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, R.color.text_color_red));
                    }
                    holder.textViewDetailsValue.setText(orderHistory.getDate());
                    holder.relativeLayoutTo.setVisibility(View.GONE);
                }
            } else if (orderHistory.getProductType() == ProductType.FRESH.getOrdinal()
                    || orderHistory.getProductType() == ProductType.MEALS.getOrdinal()
                    || orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()
                    || orderHistory.getProductType() == ProductType.MENUS.getOrdinal()
                    || orderHistory.getProductType() == ProductType.PAY.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewStatusValue.setText(orderHistory.getOrderStatus());
                try{
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch (Exception e){
                    holder.textViewStatusValue.setTextColor(activity.getResources().getColor(R.color.text_color_blue));
                }
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getOrderId()));
                holder.textViewFrom.setText(R.string.address_colon);
                holder.textViewFromValue.setText(orderHistory.getDeliveryAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                if(orderHistory.getProductType() == ProductType.MENUS.getOrdinal()){
                    holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(orderHistory.getOrderTime())));
                } else {
                    holder.textViewDetailsValue.setText(orderHistory.getExpectedDeliveryDate() + ", " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime()) + " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime()));
                }

                holder.textViewAmount.setText(activity.getString(R.string.rupees_value_format_without_space, Utils.getMoneyDecimalFormat().format(orderHistory.getDiscountedAmount())));


                if(orderHistory.getProductType() == ProductType.FRESH.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_fresh_grey);
                } else if(orderHistory.getProductType() == ProductType.MEALS.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_meals_grey);
                } else if(orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_fresh_grey);
                } else if(orderHistory.getProductType() == ProductType.MENUS.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_menus_grey);
                } else if(orderHistory.getProductType() == ProductType.PAY.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_pay_grey);
                }
                holder.relativeLayoutTo.setVisibility(View.GONE);
            }
            else if(orderHistory.getProductType() == ProductType.PROS.getOrdinal()){
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewStatusValue.setText(ProsSuperCategoriesAdapter.getProsOrderState(orderHistory.getJobStatus()).second);
                try{
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch (Exception e){
                    holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, orderHistory.getJobStatusColorRes()));
                }
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getJobId()));
                holder.textViewFrom.setText(R.string.address_colon);
                holder.textViewFromValue.setText(orderHistory.getJobAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormatTZ(orderHistory.getJobTime())
                +", "+orderHistory.getJobNameSplitted());

                holder.textViewAmount.setText("-");
                holder.imageViewProductType.setImageResource(R.drawable.ic_pros_grey);

                holder.relativeLayoutTo.setVisibility(View.GONE);
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
        public TextView textViewId, textViewIdValue, textViewFrom, textViewFromValue, textViewTo,
                textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewStatus, textViewStatusValue;
        public ImageView imageViewProductType;
        public RelativeLayout relativeLayoutTo;
        public RelativeLayout relative;

        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus); textViewStatus.setTypeface(Fonts.mavenMedium(context));
            textViewStatusValue = (TextView) convertView.findViewById(R.id.textViewStatusValue); textViewStatusValue.setTypeface(Fonts.mavenMedium(context));
            textViewId = (TextView) convertView.findViewById(R.id.textViewId); textViewId.setTypeface(Fonts.mavenMedium(context));
            textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue); textViewIdValue.setTypeface(Fonts.mavenRegular(context));
            textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom); textViewFrom.setTypeface(Fonts.mavenMedium(context));
            textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue); textViewFromValue.setTypeface(Fonts.mavenRegular(context));
            textViewTo = (TextView) convertView.findViewById(R.id.textViewTo); textViewTo.setTypeface(Fonts.mavenMedium(context));
            textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue); textViewToValue.setTypeface(Fonts.mavenRegular(context));
            textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails); textViewDetails.setTypeface(Fonts.mavenMedium(context));
            textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue); textViewDetailsValue.setTypeface(Fonts.mavenRegular(context));

            textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount); textViewAmount.setTypeface(Fonts.avenirNext(context));
            imageViewProductType = (ImageView) convertView.findViewById(R.id.imageViewProductType);

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

    }

    private int getVehicleTypeDrawable(int vehicleType, int rideType){
        if (vehicleType == VehicleTypeValue.AUTOS.getOrdinal()) {
            if(rideType == RideTypeValue.POOL.getOrdinal()) {
                return R.drawable.ic_history_pool;
            } else {
                return R.drawable.ic_auto_grey;
            }
        }
        else if (vehicleType == VehicleTypeValue.BIKES.getOrdinal()) {
            if(rideType == RideTypeValue.POOL.getOrdinal()) {
                return R.drawable.ic_history_pool;
            } else {
                return R.drawable.ic_history_bike;
            }
        }
        else if (vehicleType == VehicleTypeValue.TAXI.getOrdinal()) {
            if(rideType == RideTypeValue.POOL.getOrdinal()) {
                return R.drawable.ic_history_carpool;
            } else {
                return R.drawable.ic_history_car;
            }
        }
        else if (vehicleType == VehicleTypeValue.HELICOPTER.getOrdinal()) {
            return R.drawable.ic_helicopter_invoice;
        }
        else {
            return R.drawable.ic_auto_grey;
        }
    }

}
