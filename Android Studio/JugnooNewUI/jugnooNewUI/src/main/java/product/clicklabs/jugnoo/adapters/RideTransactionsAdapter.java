package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.pros.ui.adapters.ProsCatalogueAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
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
            int padding = (int) (ASSL.minRatio() * 12f);
            holder.imageViewProductType.setPaddingRelative(padding, padding, padding, padding);
            holder.textViewAmount.setVisibility(View.VISIBLE);
            if (orderHistory.getProductType() == ProductType.AUTO.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getEngagementId()));
                holder.textViewFrom.setText(R.string.from_colon);
                holder.textViewFromValue.setText(orderHistory.getPickupAddress());
                holder.textViewTo.setText(R.string.to_colon);
                holder.textViewToValue.setText(orderHistory.getDropAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                holder.textViewAmount.setText(Utils.formatCurrencyValue(orderHistory.getCurrency(), orderHistory.getAmount()));
                holder.textViewAmount.setVisibility(Prefs.with(activity).getInt(Constants.KEY_SHOW_FARE_IN_RIDE_HISTORY, 1) == 1 ? View.VISIBLE : View.GONE);

                holder.imageViewProductType.setImageResource(R.drawable.autos_ride_txn_icon);
                holder.imageViewProductType.setBackgroundResource(R.drawable.circle_theme);

                try {
                    int vehicleType = orderHistory.getVehicleType();
                    int rideType = orderHistory.getRideType();
                    int resourceId = getVehicleTypeDrawable(vehicleType, rideType, orderHistory.getIconSet());
                    HomeUtil.setVehicleIcon(activity, holder.imageViewProductType, orderHistory.getHistoryIcon(),
                            resourceId, false, holder.callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (0 == orderHistory.getIsCancelledRide()) {
                    if (orderHistory.getAutosStatusText() == null) {
                        holder.textViewStatusValue.setText(R.string.ride_compeleted);
                    } else {
                        holder.textViewStatusValue.setText(orderHistory.getAutosStatusText());
                    }
                    try {
                        holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getAutosStatusColor()));
                    } catch (Exception e) {
                        holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, R.color.text_color_blue));
                    }
                    holder.textViewDetailsValue.setText(orderHistory.getDate() + ", " + decimalFormat.format(orderHistory.getDistance())
                            + " "+Utils.getDistanceUnit(orderHistory.getDistanceUnit())+", " + decimalFormatNoDec.format(orderHistory.getRideTime())
                            + " "+activity.getString(R.string.min));
                    holder.relativeLayoutTo.setVisibility(View.VISIBLE);
//                    holder.ivOrderStatusIcon.setImageResource(R.drawable.ic_tick_copy);
                } else {
                    if (orderHistory.getAutosStatusText() == null) {
                        holder.textViewStatusValue.setText(R.string.ride_cancelled);
                    } else {
                        holder.textViewStatusValue.setText(orderHistory.getAutosStatusText());
                    }
                    try {
                        holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getAutosStatusColor()));
                    } catch (Exception e) {
                        holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, R.color.text_color_red));
                    }
                    holder.textViewDetailsValue.setText(orderHistory.getDate());
                    holder.relativeLayoutTo.setVisibility(View.GONE);
//                    holder.ivOrderStatusIcon.setVisibility(View.VISIBLE);
//                    holder.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_history_cancelled);

                }
            } else if (orderHistory.getProductType() == ProductType.FRESH.getOrdinal()
                    || orderHistory.getProductType() == ProductType.MEALS.getOrdinal()
                    || orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()
                    || orderHistory.getProductType() == ProductType.MENUS.getOrdinal()
                    || orderHistory.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()
                    || orderHistory.getProductType() == ProductType.PAY.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewStatusValue.setText(orderHistory.getOrderStatus());
                try {
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch (Exception e) {
                    holder.textViewStatusValue.setTextColor(activity.getResources().getColor(R.color.text_color_blue));
                }
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getOrderId()));
                holder.textViewFrom.setText(R.string.address_colon);
                holder.textViewFromValue.setText(orderHistory.getDeliveryAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                if (orderHistory.getProductType() == ProductType.MENUS.getOrdinal()
                        || orderHistory.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
                    holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(orderHistory.getOrderTime())));
                } else {
                    holder.textViewDetailsValue.setText(orderHistory.getExpectedDeliveryDate() + ", " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime(), false) + " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime(), false));
                }

                holder.textViewAmount.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(orderHistory.getDiscountedAmount(), orderHistory.getCurrencyCode(), orderHistory.getCurrency()));

                if (orderHistory.getProductType() == ProductType.FRESH.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_groceries_new_vector);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_grocery_new);
                } else if (orderHistory.getProductType() == ProductType.MEALS.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_meals);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_pink_meals_fab);
                } else if (orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_groceries_new_vector);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_green);
                } else if (orderHistory.getProductType() == ProductType.MENUS.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_menus);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_purple_menu_fab);
                } else if (orderHistory.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.delivery_order_txn_icon);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_green_delivery_customer_fab);
                } else if (orderHistory.getProductType() == ProductType.PAY.getOrdinal()) {
                    holder.imageViewProductType.setImageResource(R.drawable.ic_pay_grey);
                    holder.imageViewProductType.setBackgroundResource(R.drawable.circle_yellow);
                }
                holder.relativeLayoutTo.setVisibility(View.GONE);
               /* IciciPaymentOrderStatus orderStatus = IciciPaymentRequestStatus.parseStatus(orderHistory.getProductType()==ProductType.MENUS.getOrdinal(),orderHistory.getOrderStatusInt());
                if(orderStatus==IciciPaymentOrderStatus.CANCELLED||orderStatus==IciciPaymentOrderStatus.FAILURE){
                    holder.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_history_cancelled);
                } else if(orderStatus==IciciPaymentOrderStatus.COMPLETED){
                    holder.ivOrderStatusIcon.setImageResource(R.drawable.ic_tick_copy);
                }else{
                    holder.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_history_pending);

                }*/
            } else if (orderHistory.getProductType() == ProductType.PROS.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewStatusValue.setText(ProsCatalogueAdapter.getProsOrderState(activity, orderHistory.getJobStatus()).second);
                try {
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch (Exception e) {
                    holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, orderHistory.getJobStatusColorRes()));
                }
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getJobId()));
                holder.textViewFrom.setText(R.string.address_colon);
                holder.textViewFromValue.setText(orderHistory.getJobAddress());
                holder.textViewDetails.setText(R.string.details_colon);
                android.util.Pair<String, String> pair = orderHistory.getProductNameAndJobAmount();
                holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormatTZ(orderHistory.getJobTime())
                        + ", " + pair.first);

                holder.textViewAmount.setText(!TextUtils.isEmpty(pair.second) ? Utils.formatCurrencyValue(orderHistory.getCurrency(), pair.second) : "-");
                holder.imageViewProductType.setImageResource(R.drawable.ic_pros_grey);
                holder.imageViewProductType.setImageResource(R.drawable.ic_pros);
                holder.imageViewProductType.setBackgroundResource(R.drawable.circle_pink_pros_fab);

                holder.relativeLayoutTo.setVisibility(View.GONE);
            } else if (orderHistory.getProductType() == ProductType.FEED.getOrdinal()) {
                holder.textViewStatus.setText(R.string.status_colon);
                holder.textViewStatusValue.setText(orderHistory.getOrderStatus());
                try {
                    holder.textViewStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch (Exception e) {
                    holder.textViewStatusValue.setTextColor(ContextCompat.getColor(activity, R.color.green_status));
                }
                holder.textViewId.setText(R.string.id_colon);
                holder.textViewIdValue.setText(String.valueOf(orderHistory.getOrderId()));
                holder.textViewFrom.setText(R.string.from_colon);
                holder.textViewFromValue.setText(orderHistory.getFromAddress());
                holder.textViewTo.setText(R.string.to_colon);
                holder.textViewToValue.setText(orderHistory.getToAddress());
                holder.textViewDetails.setText(R.string.created_at_colon);
                holder.textViewDetailsValue.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(orderHistory.getCreatedAt())));

                if (orderHistory.getAmount() != 0) {
                    holder.textViewAmount.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(orderHistory.getAmount(), orderHistory.getCurrencyCode(), orderHistory.getCurrency() ));
                } else {
                    holder.textViewAmount.setText("");
                }
                holder.imageViewProductType.setImageResource(Data.userData.isRidesAndFatafatEnabled() ? R.drawable.delivery_order_txn_icon : R.drawable.ic_anywhere_fab);
                holder.imageViewProductType.setBackgroundResource(Data.userData.isRidesAndFatafatEnabled() ? R.drawable.circle_green_delivery_customer_fab : R.drawable.circle_feed_grey_fab);

                holder.relativeLayoutTo.setVisibility(View.VISIBLE);
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
        public ImageView ivOrderStatusIcon;
        public CustomCallback callback;

        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus);
            textViewStatus.setTypeface(Fonts.mavenMedium(context));
            textViewStatusValue = (TextView) convertView.findViewById(R.id.textViewStatusValue);
            textViewStatusValue.setTypeface(Fonts.mavenMedium(context));
            textViewId = (TextView) convertView.findViewById(R.id.textViewId);
            textViewId.setTypeface(Fonts.mavenMedium(context));
            textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue);
            textViewIdValue.setTypeface(Fonts.mavenRegular(context));
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
            textViewAmount.setTypeface(Fonts.avenirNext(context));
            imageViewProductType = (ImageView) convertView.findViewById(R.id.imageViewProductType);

            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
            ivOrderStatusIcon = (ImageView) convertView.findViewById(R.id.iv_order_status_icon);
            callback = new CustomCallback(imageViewProductType);
        }
    }

    private class CustomCallback implements com.squareup.picasso.Callback {

        private ImageView imageView;

        private CustomCallback(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        public void onSuccess() {
            if(imageView != null) {
                imageView.setPaddingRelative(0, 0, 0, 0);
                imageView.setBackground(null);
            }
        }

        @Override
        public void onError() {

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

    private int getVehicleTypeDrawable(int vehicleType, int rideType, String iconSet) {
        if(!TextUtils.isEmpty(iconSet)) {
            VehicleIconSet vehicleIconSet = HomeUtil.getVehicleIconSet(iconSet);
            return vehicleIconSet.getIconInvoice();
        } else {
            if (vehicleType == VehicleTypeValue.AUTOS.getOrdinal()) {
                if (rideType == RideTypeValue.POOL.getOrdinal()) {
                    return R.drawable.ic_pool_white;
                } else {
                    return R.drawable.autos_ride_txn_icon;
                }
            } else if (vehicleType == VehicleTypeValue.BIKES.getOrdinal()) {
                if (rideType == RideTypeValue.POOL.getOrdinal()) {
                    return R.drawable.ic_pool_white;
                } else {
                    return R.drawable.ic_bike_white;
                }
            } else if (vehicleType == VehicleTypeValue.TAXI.getOrdinal()) {
                if (rideType == RideTypeValue.POOL.getOrdinal()) {
                    return R.drawable.ic_pool_white;
                } else {
                    return R.drawable.ic_car_white;
                }
            } else if (vehicleType == VehicleTypeValue.HELICOPTER.getOrdinal()) {
                return R.drawable.ic_copter_white;
            } else if (vehicleType == VehicleTypeValue.ERICKSHAW.getOrdinal()) {
                return R.drawable.ic_erickshaw_white;
            } else if (vehicleType == VehicleTypeValue.TRANSPORT.getOrdinal()
                    || iconSet.equalsIgnoreCase(VehicleIconSet.TRANSPORT.getName())) {
                return R.drawable.ic_transport_white;
            } else {
                return R.drawable.autos_ride_txn_icon;
            }
        }


    }

}
