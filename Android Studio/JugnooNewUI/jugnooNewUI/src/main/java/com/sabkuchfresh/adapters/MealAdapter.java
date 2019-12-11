package com.sabkuchfresh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hippo.HippoConfig;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.views.animateheartview.LikeButton;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DiscountInfo;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.TextViewStrikeThrough;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by gurmail on 15/07/16.
 */
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GAAction{

    private String TAG = "Meals Screen";
    private FreshActivity activity;
    private ArrayList<SubItem> subItems;
    private Callback callback;
    private ArrayList<RecentOrder> recentOrders;
    private ArrayList<String> possibleStatus;
    private int showBulkOrderOption;
    private ProductsResponse.MealsBulkBanner mealsBulkBanner;
    private DiscountInfo discountInfo;
    private boolean showDiscountedPrices;
    private RecyclerView recyclerView;
    private ArrayList<SubItem> allSubItems = new ArrayList<>();



    private int listType = 0;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int STATUS_ITEM = 2;
    private static final int BULK_ORDER_ITEM = 3;

    public interface CallbackCheckForAdd{
        void addConfirmed();
    }

    public MealAdapter(FreshActivity activity, ArrayList<SubItem> subItems) {
        this.activity = activity;
        this.subItems = subItems;
        this.recentOrders = new ArrayList<>();
        this.possibleStatus = new ArrayList<>();
        cloneAllItemsList();
    }

    public MealAdapter(FreshActivity activity, ArrayList<SubItem> subItems, ArrayList<RecentOrder> recentOrders, ArrayList<String> possibleStatus, Callback callback, DiscountInfo discountInfo, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.subItems = subItems;
        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        this.callback = callback;
        this.discountInfo = discountInfo;
        cloneAllItemsList();
        scheduleHandlerForUpdatingDiscountTime(true, true);

    }

    public void setList(ArrayList<SubItem> subItems, ArrayList<RecentOrder> recentOrders, ArrayList<String> possibleStatus, ProductsResponse.MealsBulkBanner mealsBulkBanner, DiscountInfo discountInfo) {
        this.subItems = subItems;
        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        this.showBulkOrderOption = (mealsBulkBanner != null) ? mealsBulkBanner.getMealsBannerEnabled() : 0;
        this.mealsBulkBanner = mealsBulkBanner;
        this.discountInfo = discountInfo;
        cloneAllItemsList();
        scheduleHandlerForUpdatingDiscountTime(true, true);
        notifyDataSetChanged();
    }

    /**
     * Checks if we have some items in cart ( qty would be non zero )
     * If yes hide those vendors
     */
    public void checkForCartItemsPresent(){
        for(SubItem item:subItems){
            if(item.getSubItemQuantitySelected()!=null && item.getSubItemQuantitySelected()>0){
                hideOtherVendors(item.getVendorId());
                break;
            }
        }
    }

    private void cloneAllItemsList(){
        allSubItems.clear();
        allSubItems.addAll(subItems);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == STATUS_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_status, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewTitleStatus(v, activity);
        }
        else if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meal, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            return new ViewTitleHolder(v);
        } else if (viewType == BULK_ORDER_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bulk_order, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            int margin = activity.getResources().getDimensionPixelSize(R.dimen.dp_12);
            layoutParams.setMargins(margin, margin, margin, margin);
            layoutParams.setMarginStart(margin);
            layoutParams.setMarginEnd(margin);
            v.setLayoutParams(layoutParams);
            return new ViewHolderBulkOrder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

//    public void setData()

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        try {
            if (holder instanceof ViewTitleStatus) {
                ViewTitleStatus statusHolder = ((ViewTitleStatus) holder);
                try {
                    RecentOrder recentOrder = recentOrders.get(position);
                    for(int i=0; i<statusHolder.relativeStatusBar.getChildCount(); i++)
                    {
                        if(statusHolder.relativeStatusBar.getChildAt(i) instanceof ViewGroup)
                        {
                            ViewGroup viewGroup = (ViewGroup)(statusHolder.relativeStatusBar.getChildAt(i));
                            for(int j=0; j<viewGroup.getChildCount(); j++)
                            {
                                viewGroup.getChildAt(j).setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            statusHolder.relativeStatusBar.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    showPossibleStatus(possibleStatus, recentOrder.getStatus(), statusHolder);
                    statusHolder.tvOrderIdValue.setText(recentOrder.getOrderId().toString());
                    statusHolder.tvDeliveryTime.setText(recentOrder.getEndTime());
                    if((recentOrder.getOrderStatusText() != null) && (!recentOrder.getOrderStatusText().equalsIgnoreCase(""))){
                       statusHolder.tvDeliveryTime.setText(recentOrder.getOrderStatusText());
                    } else{
                        statusHolder.tvDeliveryTime.setText(activity.getResources().getString(R.string.delivery_before_colon)+" "+recentOrder.getEndTime());
                    }

                    statusHolder.container.setTag(position);
                    statusHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int pos = (int) v.getTag();
                                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                                intent.putExtra(Constants.KEY_ORDER_ID, recentOrders.get(pos).getOrderId());
                                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (holder instanceof ViewHolderSlot) {
                position = position - recentOrders.size();
                final ViewHolderSlot mHolder = ((ViewHolderSlot) holder);
                final SubItem subItem = subItems.get(position);

                mHolder.textViewTitle.setText(subItem.getSubItemName());

                // control vendor name visibility
                boolean showVendorName = false;
                if(position == 0){
                   showVendorName = true;
                }
                else if(!subItems.get(position-1).getVendorId().equals(subItem.getVendorId())){
                    // different vendor
                    showVendorName = true;
                }
                if(showVendorName){
                    mHolder.llVendorName.setVisibility(View.VISIBLE);
                    mHolder.tvVendorName.setVisibility(View.VISIBLE);
                    if(subItem.getVendorName()!=null){
                        mHolder.tvVendorName.setText(subItem.getVendorName());
                    }
                    else {
                        mHolder.tvVendorName.setText("");
                    }
                    // hide vendor divider for first vendor
                    if(position == 0){
                        mHolder.vwVendorDivider.setVisibility(View.GONE);
                    }
                    else {
                        mHolder.vwVendorDivider.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    mHolder.llVendorName.setVisibility(View.GONE);
                    mHolder.tvVendorName.setVisibility(View.GONE);
                }

                mHolder.textViewdetails.setText(subItem.getItemLargeDesc());
//                mHolder.deliveryTime.setText(DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()) + "-"
//                        + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd()));

                /*if (subItem.isExpanded()) {
                    mHolder.textViewdetails.setVisibility(View.VISIBLE);
                } else {
                    mHolder.textViewdetails.setVisibility(View.GONE);
                }*/

                if(Data.getMealsData().isMealsFavEnabled()){
                    mHolder.rlLikeLayout.setVisibility(View.VISIBLE);
                    String noOfLikesLabel;
                    if(subItem.getLikeCount()>0)
                         noOfLikesLabel =  activity.getResources().getQuantityString(R.plurals.like_suffix, subItem.getLikeCount(), subItem.getLikeCount());
                    else
                        noOfLikesLabel = " "+activity.getString(R.string.like);

                        mHolder.tvLikeCount.setText(noOfLikesLabel);
                        mHolder.rlLikeLayout.setVisibility(View.VISIBLE);
                         mHolder.likeButton.setLiked(subItem.getIsLiked());

                   /* if(subItem.getIsLiked()){
                            mHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_active_new,0,0,0);
                        }else {
                            mHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_new,0,0,0);

                        }*/

                    mHolder.rlLikeLayout.setTag(position);
                    mHolder.rlLikeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int pos  = (int) v.getTag();
                            SubItem subItem1  = subItems.get(pos);
                            if(!subItem1.isLikeAPIInProgress()){
                                subItem1.setLikeAPIInProgress(true);
                                callback.onLikeClicked(subItem1,pos);
                            }



                        }
                    });


                }else{
                    mHolder.rlLikeLayout.setVisibility(View.GONE);
                }

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
                mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_dark_selector);
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                if (subItem.getSubItemQuantitySelected() == 0) {
                    if(subItem.getStock() > 0){
                        mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_theme_selector);
                        mHolder.imageViewMinus.setVisibility(View.GONE);
                        mHolder.textViewQuantity.setVisibility(View.GONE);
                    } else{
                        mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                    }
                } else {
                    mHolder.imageViewMinus.setVisibility(View.VISIBLE);
                    mHolder.textViewQuantity.setVisibility(View.VISIBLE);
                }

                if(subItem.getcanOrder() == 0) {
                    mHolder.imageClosed.setVisibility(View.GONE);
                    mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                    mHolder.cartLayout.setVisibility(View.VISIBLE);
                    mHolder.deliveryTime.setVisibility(View.VISIBLE);
                    mHolder.deliveryTime.setText(subItem.getDeliveryTimeText()+"\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart(), false));
                } else {
                    if(subItem.getStock() == 0) {
                        mHolder.imageClosed.setVisibility(View.VISIBLE);
                        mHolder.cartLayout.setVisibility(View.GONE);
                    } else {
                        mHolder.imageClosed.setVisibility(View.GONE);
                        mHolder.cartLayout.setVisibility(View.VISIBLE);
                    }

                    mHolder.deliveryTime.setText(subItem.getDeliveryTimeText()+"\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart(), false) + " - "
                            + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd(), false));
                    mHolder.deliveryTime.setVisibility(View.GONE);
                }

                if(subItem.getEarliestDeliveryMessage() != null && !subItem.getEarliestDeliveryMessage().equalsIgnoreCase("")){
                    mHolder.rlEarliestDelivery.setVisibility(View.VISIBLE);
                    mHolder.tvEarliestDelivery.setText(subItem.getEarliestDeliveryMessage());
                } else{
                    mHolder.rlEarliestDelivery.setVisibility(View.GONE);
                }


                mHolder.imageViewMinus.setTag(position);
                mHolder.imageViewPlus.setTag(position);

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
                            int itemId = subItems.get(pos).getSubItemId();
                            if(callback.checkForMinus(pos, subItems.get(pos))) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                        subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);

                                callback.onMinusClicked(pos, subItems.get(pos));

                                notifyDataSetChanged();
                                GAUtils.event(activity.getGaCategory(), HOME, ITEM+DECREASED);
                            } else{
                                callback.minusNotDone(pos, subItems.get(pos));
                            }

                            // if the cart is empty, we need to reshow all vendors
                            if(callback.isMealsCartEmpty()){
                               showAllVendors();
                               adjustScroll(itemId);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final int pos = (int) v.getTag();

                            CallbackCheckForAdd callbackCheckForAdd = new CallbackCheckForAdd() {
                                @Override
                                public void addConfirmed() {
                                    addItemToCart(pos);
                                }
                            };

                            if(callback.canAddItem(subItems.get(pos),callbackCheckForAdd)){
                               addItemToCart(pos);
                               // hide other vendors so user cannot add item from them
                                int itemId = subItems.get(pos).getSubItemId();
                                hideOtherVendors(subItems.get(pos).getVendorId());
                                adjustScroll(itemId);

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                try {
                    if (subItem.getSubItemImage() != null && !"".equalsIgnoreCase(subItem.getSubItemImage())) {
                        Picasso.with(activity).load(subItem.getSubItemImage())
                                .placeholder(R.drawable.ic_fresh_new_placeholder)
                                .fit()
                                .centerCrop()
                                .error(R.drawable.ic_fresh_new_placeholder)
                                .into(mHolder.imageViewMmeals);
                    } else {
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Picasso.with(activity).load(R.drawable.ic_fresh_new_placeholder)
                            .placeholder(R.drawable.ic_fresh_new_placeholder)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_fresh_new_placeholder)
                            .into(mHolder.imageViewMmeals);
                }


                String discountOfferDisplay = getDiscountOfferDisplay();
                if(discountInfo!=null  && showDiscountedPrices && discountOfferDisplay!=null
                        && (!subItem.getActualPrice().equals(subItem.getPrice()))){
                    mHolder.tvDiscountedPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormatWithoutFloat().format(subItem.getActualPrice())));
                    mHolder.textPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormatWithoutFloat().format(subItem.getPrice())));
                    mHolder.tvDiscountedOffer.setText(discountOfferDisplay);
                    mHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                    mHolder.tvDiscountedOffer.setVisibility(View.VISIBLE);
                } else{
                    if(subItem.getActualPrice()!=null) {
                        subItem.setPrice(subItem.getActualPrice());
                    }
                    mHolder.textPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormatWithoutFloat().format(subItem.getPrice())));
                    mHolder.tvDiscountedPrice.setVisibility(View.GONE);
                    mHolder.tvDiscountedOffer.setVisibility(View.GONE);
                }
            } else if (holder instanceof ViewTitleHolder) {
                ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
                titleholder.relative.setVisibility(View.VISIBLE);
                titleholder.relative.setBackgroundColor(activity.getResources().getColor(R.color.white));
            } else if(holder instanceof ViewHolderBulkOrder){
                ViewHolderBulkOrder holderBulkOrder = (ViewHolderBulkOrder) holder;
                if(mealsBulkBanner != null && !TextUtils.isEmpty(mealsBulkBanner.getImageUrl2X())) {
                    Picasso.with(activity).load(mealsBulkBanner.getImageUrl2X())
                            .placeholder(R.drawable.ic_fresh_new_placeholder)
                            .error(R.drawable.ic_fresh_new_placeholder)
                            .into(holderBulkOrder.ivBulkOrder);
                } else {
                    Picasso.with(activity).load(R.drawable.ic_fresh_new_placeholder).into(holderBulkOrder.ivBulkOrder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Hides other vendors
     * @param vendorId the vendor to show
     */
    public void hideOtherVendors(final Integer vendorId) {
        Iterator<SubItem> itemIterator = subItems.iterator();
        while(itemIterator.hasNext()){
            if(!itemIterator.next().getVendorId().equals(vendorId)){
                // remove
                itemIterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Adjusts scroll to the selected item
     * @param itemId the id of the selected item
     */
    private void adjustScroll(final int itemId){

        // calculate position of the item
        int position = 0;

        // add offset from recent orders if present
        if(recentOrders!=null && recentOrders.size()>0){
            position+=recentOrders.size();
        }

        for(SubItem item:subItems){
            if(item.getSubItemId() == itemId){
                break;
            }
            position++;
        }

        // scroll to position
        final int finalPosition = position;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(finalPosition,0);
    }


    /**
     * Shows all vendors
     */
    public void showAllVendors(){
        subItems.clear();
        subItems.addAll(allSubItems);
        notifyDataSetChanged();
    }

    private void addItemToCart(int pos){
        if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
        } else {
            Utils.showToast(activity, activity.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
        }

        callback.onPlusClicked(pos, subItems.get(pos));
        notifyDataSetChanged();
        if(subItems.get(pos).getSubItemQuantitySelected() == 1){
            GAUtils.event(activity.getGaCategory(), HOME, ITEM+ADDED);
        } else {
            GAUtils.event(activity.getGaCategory(), HOME, ITEM+INCREASED);
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
        int recentOrdersSize = recentOrders == null ? 0 : recentOrders.size();
        int subItemsSize = subItems == null ? 0 : subItems.size();
        if(position < recentOrdersSize){
            return STATUS_ITEM;
        } else if(position >= recentOrdersSize && position < recentOrdersSize + subItemsSize) {
            return MAIN_ITEM;
        } else if(showBulkOrderOption == 1 && position == getItemCount()-((recentOrdersSize + subItemsSize) > 0 ? 2 : 1)){
            return BULK_ORDER_ITEM;
        } else {
            return BLANK_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        int recentOrdersSize = recentOrders == null ? 0 : recentOrders.size();
        int subItemsSize = subItems == null ? 0 : subItems.size();
        return (((recentOrdersSize + subItemsSize) > 0) ? (recentOrdersSize + subItemsSize + 1) : 0) + showBulkOrderOption;
    }

    public void notifyOnLike(int position, boolean isLiked) {
        try {
            if (subItems != null && position < subItems.size()) {
                SubItem subItem = (SubItem) subItems.get(position);
                if (subItem.isLikeAPIInProgress()) {
                    subItem.setLikeAPIInProgress(false);
                    if (isLiked) {
                        if(recyclerView!=null){
                           MealAdapter.ViewHolderSlot viewHolderReviewImage = ((MealAdapter.ViewHolderSlot)recyclerView.findViewHolderForAdapterPosition(position));
                        if(viewHolderReviewImage!=null) {
                            LikeButton likeButton = viewHolderReviewImage.likeButton;
                            likeButton.onClick(likeButton);
                        }
                        }

                        subItem.setLikeCount(subItem.getLikeCount() + 1);
                    } else if (subItem.getLikeCount() > 0)
                        subItem.setLikeCount(subItem.getLikeCount() - 1);

                    subItem.setIsLiked(isLiked);

                    if(recentOrders!=null)
                        position +=recentOrders.size();
                    notifyItemChanged(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public int getItemCount() {
//        return subItems == null ? 0 : subItems.size();
//    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        public RelativeLayout belowLayout, rlEarliestDelivery;
        public LinearLayout linearLayoutQuantitySelector, cartLayout;
        private ImageView imageViewMmeals, foodType;
        private ImageView imageViewMinus, imageViewPlus, imageClosed;
        public TextView textViewTitle, textPrice, textViewdetails, deliveryTime, textViewQuantity, tvEarliestDelivery;
        public TextViewStrikeThrough tvDiscountedPrice;
        public TextView tvDiscountedOffer,tvLikeCount;
        private RelativeLayout rlLikeLayout;
        private LikeButton likeButton;
        private TextView tvVendorName;
        private LinearLayout llVendorName;
        private View vwVendorDivider;

        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            cartLayout = (LinearLayout) itemView.findViewById(R.id.cart_layout);
            belowLayout = (RelativeLayout) itemView.findViewById(R.id.below_layout);
            rlEarliestDelivery = (RelativeLayout) itemView.findViewById(R.id.rlEarliestDelivery);

            imageViewMmeals = (ImageView) itemView.findViewById(R.id.imageViewMmeals);
            foodType = (ImageView) itemView.findViewById(R.id.food_type);
            imageClosed = (ImageView) itemView.findViewById(R.id.image_view_closed);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            likeButton = (LikeButton) itemView.findViewById(R.id.like_button_animate);

            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewTitle.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textPrice = (TextView) itemView.findViewById(R.id.text_price);
            textPrice.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewdetails = (TextView) itemView.findViewById(R.id.textViewdetails);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tv_like_count);
            textViewdetails.setTypeface(Fonts.mavenMedium(context));
            deliveryTime = (TextView) itemView.findViewById(R.id.delivery_time);
            deliveryTime.setTypeface(Fonts.mavenMedium(context));
            textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
            textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            tvEarliestDelivery = (TextView) itemView.findViewById(R.id.tvEarliestDelivery);
            tvEarliestDelivery.setTypeface(Fonts.mavenMedium(context));
            tvDiscountedPrice = (TextViewStrikeThrough) itemView.findViewById(R.id.text_price_striked);
            tvDiscountedOffer = (TextView) itemView.findViewById(R.id.tv_discounted_offer);
            rlLikeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_like_layout);
            tvVendorName = (TextView) itemView.findViewById(R.id.tvVendorName);
            tvVendorName.setTypeface(Fonts.mavenMedium(context), Typeface.NORMAL);
            llVendorName = (LinearLayout) itemView.findViewById(R.id.llVendorName);
            vwVendorDivider = itemView.findViewById(R.id.vwVendorDivider);

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
        public TextView tvOrderIdValue, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        public ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        public View lineStatus1, lineStatus2, lineStatus3;

        public ViewTitleStatus(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            relativeStatusBar = (RelativeLayout) itemView.findViewById(R.id.relativeStatusBar);
            tvOrderIdValue = (TextView) itemView.findViewById(R.id.tvOrderIdValue); tvOrderIdValue.setTypeface(Fonts.mavenMedium(context));
            tvDeliveryTime = (TextView) itemView.findViewById(R.id.tvDeliveryTime); tvDeliveryTime.setTypeface(Fonts.mavenMedium(context));
            tvStatus0 = (TextView) itemView.findViewById(R.id.tvStatus0); tvStatus0.setTypeface(Fonts.mavenRegular(context));
            tvStatus1 = (TextView) itemView.findViewById(R.id.tvStatus1); tvStatus1.setTypeface(Fonts.mavenRegular(context));
            tvStatus2 = (TextView) itemView.findViewById(R.id.tvStatus2); tvStatus2.setTypeface(Fonts.mavenRegular(context));
            tvStatus3 = (TextView) itemView.findViewById(R.id.tvStatus3); tvStatus3.setTypeface(Fonts.mavenRegular(context));
            ivStatus0 = (ImageView) itemView.findViewById(R.id.ivStatus0);
            ivStatus1 = (ImageView) itemView.findViewById(R.id.ivStatus1);
            ivStatus2 = (ImageView) itemView.findViewById(R.id.ivStatus2);
            ivStatus3 = (ImageView) itemView.findViewById(R.id.ivStatus3);
            lineStatus1 = itemView.findViewById(R.id.lineStatus1);
            lineStatus2 = itemView.findViewById(R.id.lineStatus2);
            lineStatus3 = itemView.findViewById(R.id.lineStatus3);
        }
    }


    public interface Callback {
        void onSlotSelected(int position, SubItem slot);

        void onPlusClicked(int position, SubItem subItem);

        void onMinusClicked(int position, SubItem subItem);

        boolean checkForMinus(int position, SubItem subItem);
        void minusNotDone(int position, SubItem subItem);

        boolean onLikeClicked(SubItem subItem, int pos);

        boolean canAddItem(SubItem subItem, MealAdapter.CallbackCheckForAdd callbackCheckForAdd);

        boolean isMealsCartEmpty();
    }


    class ViewHolderBulkOrder extends RecyclerView.ViewHolder {

        public ImageView ivBulkOrder;

        public ViewHolderBulkOrder(View itemView) {
            super(itemView);
            ivBulkOrder = (ImageView) itemView.findViewById(R.id.ivBulkOrder);
            ivBulkOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(mealsBulkBanner.getOpenNextPage() == 1){
                            activity.getTransactionUtils().openMealsBulkOrderFragment(activity, activity.getRelativeLayoutContainer(), mealsBulkBanner.getNextPageImage());
                            GAUtils.event(activity.getGaCategory(), HOME, BULK_ORDER_NEXT_PAGE);
                        } else {
                            HippoConfig.getInstance().openChat(activity,Data.CHANNEL_ID_FUGU_BULK_MEALS());
                            GAUtils.event(activity.getGaCategory(), HOME, BULK_ORDER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.showToast(activity, activity.getString(R.string.something_went_wrong));
                    }
                }
            });
        }


    }

    private Handler handler = new Handler();
    private long currentTime;
    private void scheduleHandlerForUpdatingDiscountTime(boolean firstTime, boolean scheduleClearRunnableOnEndTime){


        showDiscountedPrices = discountInfo != null && discountInfo.getIsActive()
                && discountInfo.getDiscountEndTime() != null && discountInfo.getCurrentDate() != null;

        long scheduleRunnableTime = 60 * 1000;
        if (showDiscountedPrices) {
            if (firstTime) {
                Date currentDate = DateOperations.getDateFromString(discountInfo.getCurrentDate());
                if (currentDate != null) {
                    currentTime = currentDate.getTime();
                } else {
                    showDiscountedPrices = false;
                }



            } else {
                currentTime += 60 * 1000;
            }

            Date endDate = DateOperations.getDateFromString(discountInfo.getDiscountEndTime());
            showDiscountedPrices = endDate != null && currentTime < endDate.getTime();

            if(showDiscountedPrices && firstTime && endDate!=null && scheduleClearRunnableOnEndTime){
                stopTimerHandler.removeCallbacksAndMessages(null);
                stopTimerHandler.postDelayed(stopTimerRunnable,endDate.getTime()-currentTime);
            }
            if(showDiscountedPrices && firstTime && endDate!=null){
                long TimeDiffSecs = (endDate.getTime()-currentTime)/1000;
                int secs = (int) (TimeDiffSecs % 60);
                if(secs>0){
                    scheduleRunnableTime = secs* 1000;
                }
            }

        }


        handler.removeCallbacksAndMessages(null);
        if (showDiscountedPrices) {


            handler.postDelayed(updateDiscountedLabelRunnable, scheduleRunnableTime);
        }

        activity.setShowingEarlyBirdDiscount(showDiscountedPrices);

    }

    private Runnable updateDiscountedLabelRunnable = new Runnable() {
        @Override
        public void run() {
            scheduleHandlerForUpdatingDiscountTime(false, false);
            notifyDataSetChanged();

        }
    };

    public void removeScheduledHandler(){
        handler.removeCallbacksAndMessages(null);
        stopTimerHandler.removeCallbacksAndMessages(null);
    }
    private Handler stopTimerHandler = new Handler();
     private Runnable stopTimerRunnable = new Runnable() {
        @Override
        public void run() {
            scheduleHandlerForUpdatingDiscountTime(false, false);
            notifyDataSetChanged();
        }
    };

    public String getDiscountOfferDisplay(){
        if(discountInfo==null){
            return null;
        }
        if(discountInfo.getTextToDisplay()!=null && discountInfo.getTextToDisplay().trim().length()>0){
            return discountInfo.getTextToDisplay();
        }

        if(discountInfo.getDiscountEndTime()==null){
            return null;
        }
        Date endDate = DateOperations.getDateFromString(discountInfo.getDiscountEndTime());
        if(endDate==null  || currentTime > endDate.getTime())
            return null;

        if(endDate.getTime()-currentTime>discountInfo.getThreshHoldTime()*60*1000){
            return activity.getString(R.string.discount_valid_till_format, DateOperations.getAmPmFromServerDateFormat(discountInfo.getDiscountEndTime()));

        }


        long TimeDiffSecs = (endDate.getTime()-currentTime)/1000;
        int hours = (int) (TimeDiffSecs / 3600);
        int min = (int) (TimeDiffSecs / 60 - hours * 60);
        int secs = (int) (TimeDiffSecs % 60);

        if(min>1 || (min==1 && secs>0))
        {
            if(secs>0)
                min+=1;
            String suffix= " "+activity.getString(min>1 ? R.string.mins : R.string.min);
            return activity.getString(R.string.discount_valid_for_next_format, min + suffix);
        } else if(secs>0){
            return activity.getString(R.string.discount_valid_for_less_than_1_min);
        }
        else
            return null;
   /*     if(min>1)
           return "Discount valid for next "+ min + suffix;
        else if(secs>0)
            return "Discount valid for less than 1 min";
        else
            return null;*/

    }

    public static boolean isDiscountValid(DiscountInfo discountInfo){
        if(discountInfo!=null && discountInfo.getIsActive() && discountInfo.getDiscountEndTime()!=null && discountInfo.getCurrentDate()!=null){
            Date endDate = DateOperations.getDateFromString(discountInfo.getDiscountEndTime());
            Date currentDate = DateOperations.getDateFromString(discountInfo.getCurrentDate());
            return endDate!=null && currentDate!=null && endDate.getTime()>currentDate.getTime();
        }
        return false;
    }
}
