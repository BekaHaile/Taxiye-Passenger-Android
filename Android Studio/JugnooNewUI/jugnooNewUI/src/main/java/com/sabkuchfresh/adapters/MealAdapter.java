package com.sabkuchfresh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by gurmail on 15/07/16.
 */
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "Meals Screen";
    private FreshActivity activity;
    private ArrayList<SubItem> subItems;
    private Callback callback;
    private ArrayList<RecentOrder> recentOrders;
    private ArrayList<String> possibleStatus;

    private int listType = 0;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int STATUS_ITEM = 2;


    public MealAdapter(FreshActivity activity, ArrayList<SubItem> subItems) {
        this.activity = activity;
        this.subItems = subItems;
        this.recentOrders = new ArrayList<>();
        this.possibleStatus = new ArrayList<>();
    }

    public MealAdapter(FreshActivity activity, ArrayList<SubItem> subItems, ArrayList<RecentOrder> recentOrders, ArrayList<String> possibleStatus, Callback callback) {
        this.activity = activity;
        this.subItems = subItems;
        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        this.callback = callback;
    }

    public void setList(ArrayList<SubItem> subItems) {
        this.subItems = subItems;
        notifyDataSetChanged();
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_meals, parent, false);
//        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
//        v.setLayoutParams(layoutParams);
//        ASSL.DoMagic(v);
//        return new ViewHolderSlot(v, activity);
//
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == STATUS_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meals_order_status, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewTitleStatus(v, activity);
        }
        else if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_meals, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewTitleHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

//    public void setData()

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ViewTitleStatus) {
                final ViewTitleStatus statusHolder = ((ViewTitleStatus) holder);
                try {
                    final RecentOrder recentOrder = recentOrders.get(position);
                    for(int i=0; i<statusHolder.relativeStatusBar.getChildCount(); i++){
                        if(statusHolder.relativeStatusBar.getChildAt(i) instanceof ViewGroup){
                            ViewGroup viewGroup = (ViewGroup)(statusHolder.relativeStatusBar.getChildAt(i));
                            for(int j=0; j<viewGroup.getChildCount(); j++){
                                viewGroup.getChildAt(j).setVisibility(View.GONE);
                            }
                        } else{
                            statusHolder.relativeStatusBar.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    showPossibleStatus(possibleStatus, recentOrder.getStatus(), statusHolder);
                    statusHolder.tvOrderIdValue.setText(recentOrder.getOrderId().toString());
                    statusHolder.tvDeliveryTime.setText(recentOrder.getEndTime());

                    statusHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, RideTransactionsActivity.class);
                            intent.putExtra(Constants.KEY_ORDER_ID, recentOrder.getOrderId());
                            intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.ORDER_STATUS);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (holder instanceof ViewHolderSlot) {
                position = position - recentOrders.size();
                ViewHolderSlot mHolder = ((ViewHolderSlot) holder);
                final SubItem subItem = subItems.get(position);

                mHolder.textViewTitle.setText(subItem.getSubItemName());
                mHolder.textPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormatWithoutFloat().format(subItem.getPrice())));
                mHolder.textViewdetails.setText(subItem.getItemLargeDesc());
//                mHolder.deliveryTime.setText(DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()) + "-"
//                        + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd()));

                /*if (subItem.isExpanded()) {
                    mHolder.textViewdetails.setVisibility(View.VISIBLE);
                } else {
                    mHolder.textViewdetails.setVisibility(View.GONE);
                }*/

                mHolder.linear.setTag(position);
                mHolder.linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                           /* if (subItem.isExpanded()) {
                                subItem.setExpanded(false);
                            } else {
                                subItem.setExpanded(true);
                            }*/
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (subItem.getIsVeg() == 1) {
                    mHolder.foodType.setImageResource(R.drawable.veg);
                } else {
                    mHolder.foodType.setImageResource(R.drawable.nonveg);
                }

                mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
                if (subItem.getSubItemQuantitySelected() == 0) {
                    mHolder.mAddButton.setVisibility(View.VISIBLE);
                    mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                } else {
                    mHolder.mAddButton.setVisibility(View.GONE);
                    mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                }

                if(subItem.getcanOrder() == 0) {
                    mHolder.imageClosed.setVisibility(View.GONE);
                    mHolder.mAddButton.setVisibility(View.GONE);
                    mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                    mHolder.cartLayout.setVisibility(View.VISIBLE);
                    mHolder.deliveryTime.setVisibility(View.VISIBLE);
                    mHolder.deliveryTime.setText(subItem.getDeliveryTimeText()+"\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()));
                } else {
                    if(subItem.getStock() == 0) {
                        mHolder.imageClosed.setVisibility(View.VISIBLE);
                        mHolder.cartLayout.setVisibility(View.GONE);
                    } else {
                        mHolder.imageClosed.setVisibility(View.GONE);
                        mHolder.cartLayout.setVisibility(View.VISIBLE);
                    }

                    mHolder.deliveryTime.setText(subItem.getDeliveryTimeText()+"\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()) + " to "
                            + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd()));
                    mHolder.deliveryTime.setVisibility(View.GONE);
                }

//                if(subItem.getcanOrder() == 1) {
//                    mHolder.imageClosed.setVisibility(View.GONE);
//                    mHolder.cartLayout.setVisibility(View.VISIBLE);
//
//                } else {
//                    mHolder.imageClosed.setVisibility(View.VISIBLE);
//                    mHolder.cartLayout.setVisibility(View.GONE);
//                    mHolder.deliveryTime.setText("Order Starts at "+DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()));
//                }


                mHolder.imageViewMinus.setTag(position);
                mHolder.imageViewPlus.setTag(position);
                mHolder.mAddButton.setTag(position);

                mHolder.belowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            if(callback.checkForMinus(pos, subItems.get(pos))) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                        subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                                callback.onMinusClicked(pos, subItems.get(pos));

                                notifyDataSetChanged();
                            } else{
                                callback.minusNotDone(pos, subItems.get(pos));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                            } else {
                                Utils.showToast(activity, activity.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                            }
                            callback.onPlusClicked(pos, subItems.get(pos));
                            notifyDataSetChanged();
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_ADD, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                            } else {
                                Utils.showToast(activity, activity.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                            }
                            callback.onPlusClicked(pos, subItems.get(pos));
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                try {
                    if (subItem.getSubItemImage() != null && !"".equalsIgnoreCase(subItem.getSubItemImage())) {
                        Picasso.with(activity).load(subItem.getSubItemImage())
                                .placeholder(R.drawable.ic_meal_place_holder)
                                .fit()
                                .centerCrop()
                                .error(R.drawable.ic_meal_place_holder)
                                .into(mHolder.imageViewMmeals);
                    } else {
                        mHolder.imageViewMmeals.setImageResource(R.drawable.ic_meal_place_holder);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (holder instanceof ViewTitleHolder) {
                ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
                titleholder.relative.setVisibility(View.VISIBLE);
                titleholder.relative.setBackgroundColor(activity.getResources().getColor(R.color.menu_item_selector_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showPossibleStatus(ArrayList<String> possibleStatus, int status, ViewTitleStatus statusHolder){
        setDefaultState(statusHolder);
        int selectedSize = (int)(35*ASSL.Xscale());
        switch (possibleStatus.size()){
            case 4:
                statusHolder.tvStatus3.setVisibility(View.VISIBLE);
                statusHolder.ivStatus3.setVisibility(View.VISIBLE);
                statusHolder.lineStatus3.setVisibility(View.VISIBLE);
                statusHolder.tvStatus3.setText(possibleStatus.get(3));
                if(status == 3){
                    statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if(status > 3){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus3.setLayoutParams(layoutParams);
                    statusHolder.ivStatus3.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 3:
                statusHolder.tvStatus2.setVisibility(View.VISIBLE);
                statusHolder.ivStatus2.setVisibility(View.VISIBLE);
                statusHolder.lineStatus2.setVisibility(View.VISIBLE);
                statusHolder.tvStatus2.setText(possibleStatus.get(2));
                if(status == 2){
                    statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if(status > 2){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus2.setLayoutParams(layoutParams);
                    statusHolder.ivStatus2.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 2:
                statusHolder.tvStatus1.setVisibility(View.VISIBLE);
                statusHolder.ivStatus1.setVisibility(View.VISIBLE);
                statusHolder.lineStatus1.setVisibility(View.VISIBLE);
                statusHolder.tvStatus1.setText(possibleStatus.get(1));
                if(status == 1){
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if(status > 1){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus1.setLayoutParams(layoutParams);
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 1:
                statusHolder.tvStatus0.setVisibility(View.VISIBLE);
                statusHolder.ivStatus0.setVisibility(View.VISIBLE);
                statusHolder.tvStatus0.setText(possibleStatus.get(0));
                if(status == 0){
                    statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status_green);
                } else if(status > 0){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus0.setLayoutParams(layoutParams);
                    statusHolder.ivStatus0.setBackgroundResource(R.drawable.ic_order_status_green);
                }
            break;
        }
    }

    private void setDefaultState(ViewTitleStatus statusHolder) {
        int selectedSize = (int)(25*ASSL.Xscale());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus3.setLayoutParams(layoutParams);
        statusHolder.lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus2.setLayoutParams(layoutParams);
        statusHolder.lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus1.setLayoutParams(layoutParams);
        statusHolder.lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus0.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemViewType(int position) {
        if(position < recentOrders.size()){
            return STATUS_ITEM;
        } else if(position >= recentOrders.size() && position < recentOrders.size() + subItems.size()) {
            return MAIN_ITEM;
        } else{
            return BLANK_ITEM;
        }
//        return slots.get(position).getSlotViewType().getOrdinal();
    }

    @Override
    public int getItemCount() {
        if(listType == AppConstant.ListType.HOME){
            if(recentOrders != null && recentOrders.size() > 0){
                return subItems == null ? 0 : subItems.size() + recentOrders.size() + 1;
            } else {
                return subItems == null ? 0 : subItems.size() + 1;
            }
        }

        else
            return subItems == null ? 0 : subItems.size();
    }

//    @Override
//    public int getItemCount() {
//        return subItems == null ? 0 : subItems.size();
//    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        public RelativeLayout belowLayout;
        public LinearLayout linearLayoutQuantitySelector, cartLayout;
        private ImageView imageViewMmeals, foodType;
        private ImageView imageViewMinus, imageViewPlus, imageClosed;
        public TextView textViewTitle, textPrice, textViewdetails, deliveryTime, textViewQuantity;
        public Button mAddButton;

        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            cartLayout = (LinearLayout) itemView.findViewById(R.id.cart_layout);
            belowLayout = (RelativeLayout) itemView.findViewById(R.id.below_layout);

            imageViewMmeals = (ImageView) itemView.findViewById(R.id.imageViewMmeals);
            foodType = (ImageView) itemView.findViewById(R.id.food_type);
            imageClosed = (ImageView) itemView.findViewById(R.id.image_view_closed);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

            mAddButton = (Button) itemView.findViewById(R.id.add_button);

            mAddButton.setTypeface(Fonts.mavenRegular(context));

            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewTitle.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textPrice = (TextView) itemView.findViewById(R.id.text_price);
            textPrice.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewdetails = (TextView) itemView.findViewById(R.id.textViewdetails);
            textViewdetails.setTypeface(Fonts.mavenMedium(context));
            deliveryTime = (TextView) itemView.findViewById(R.id.delivery_time);
            deliveryTime.setTypeface(Fonts.mavenMedium(context));
            textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
            textViewQuantity.setTypeface(Fonts.mavenRegular(context));

        }
    }

    static class ViewTitleHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative;

        public ViewTitleHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    static class ViewTitleStatus extends RecyclerView.ViewHolder {

        public LinearLayout linear;
        public RelativeLayout container, relativeStatusBar;
        public TextView tvOrderId, tvOrderIdValue,tvDeliveryBefore, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        public ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        public View lineStatus1, lineStatus2, lineStatus3;

        public ViewTitleStatus(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            relativeStatusBar = (RelativeLayout) itemView.findViewById(R.id.relativeStatusBar);
            tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId); tvOrderId.setTypeface(Fonts.mavenRegular(context));
            tvOrderIdValue = (TextView) itemView.findViewById(R.id.tvOrderIdValue); tvOrderIdValue.setTypeface(Fonts.mavenMedium(context));
            tvDeliveryBefore = (TextView) itemView.findViewById(R.id.tvDeliveryBefore); tvDeliveryBefore.setTypeface(Fonts.mavenRegular(context));
            tvDeliveryTime = (TextView) itemView.findViewById(R.id.tvDeliveryTime); tvDeliveryTime.setTypeface(Fonts.mavenMedium(context));
            tvStatus0 = (TextView) itemView.findViewById(R.id.tvStatus0); tvStatus0.setTypeface(Fonts.mavenRegular(context));
            tvStatus1 = (TextView) itemView.findViewById(R.id.tvStatus1); tvStatus1.setTypeface(Fonts.mavenRegular(context));
            tvStatus2 = (TextView) itemView.findViewById(R.id.tvStatus2); tvStatus2.setTypeface(Fonts.mavenRegular(context));
            tvStatus3 = (TextView) itemView.findViewById(R.id.tvStatus3); tvStatus3.setTypeface(Fonts.mavenRegular(context));
            ivStatus0 = (ImageView) itemView.findViewById(R.id.ivStatus0);
            ivStatus1 = (ImageView) itemView.findViewById(R.id.ivStatus1);
            ivStatus2 = (ImageView) itemView.findViewById(R.id.ivStatus2);
            ivStatus3 = (ImageView) itemView.findViewById(R.id.ivStatus3);
            lineStatus1 = (View) itemView.findViewById(R.id.lineStatus1);
            lineStatus2 = (View) itemView.findViewById(R.id.lineStatus2);
            lineStatus3 = (View) itemView.findViewById(R.id.lineStatus3);
        }
    }


    public interface Callback {
        void onSlotSelected(int position, SubItem slot);

        void onPlusClicked(int position, SubItem subItem);

        void onMinusClicked(int position, SubItem subItem);

        boolean checkForMinus(int position, SubItem subItem);
        void minusNotDone(int position, SubItem subItem);

    }

}
