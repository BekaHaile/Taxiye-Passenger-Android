package com.sabkuchfresh.adapters;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by shankar on 1/20/17.
 */

public class DeliveryHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

    private static final String TAG = DeliveryHomeAdapter.class.getName();
    private FreshActivity activity;
    private List<Object> dataToDisplay;
    private List<RecentOrder> remainingRecentOrders;
    private ArrayList<String> possibleStatus;
    private Callback callback;
    private boolean showOrderOptions = false;
    private DeliverySeeAll deliverySeeAllOrders;


    private static final int VIEW_TITLE_CATEGORY = 1;
    private static final int VIEW_ORDER_ITEM = 2;
    private static final int VIEW_SEE_ALL = 3;
    private static final int VIEW_VENDOR = 4;
    private static final int VIEW_DIVIDER = 5;
    private static final int OFFERS_PAGER_ITEM = 6;
    private static final int OFFER_STRIP_ITEM = 7;
    private static final int ITEM_PROGRESS_BAR = 8;
    private static final int NO_VENDORS_ITEM = 9;


    private static final int RECENT_ORDERS_TO_SHOW = 2;

    private RecyclerView recyclerView;

    private final static ColorMatrix BW_MATRIX = new ColorMatrix();
    private final static ColorMatrixColorFilter BW_FILTER;
    static {
        BW_MATRIX.setSaturation(0);
        BW_FILTER = new ColorMatrixColorFilter(BW_MATRIX);

    }


    public DeliveryHomeAdapter(FreshActivity activity, Callback callback, RecyclerView recyclerView, ArrayList<String> possibleStatus) {
        this.activity = activity;
        this.callback = callback;
        this.recyclerView = recyclerView;
        this.possibleStatus = possibleStatus;
        timerHandler = activity.getHandler();
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private Handler timerHandler;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                notifyDataSetChanged();
                if (timerHandler != null) {
                    Log.v(TAG, "notifying automaically");
                    timerHandler.postDelayed(timerRunnable, 60000); //run every minute
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void removeHandler(){
        if(timerHandler != null){
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler = null;
        }
    }

    public void setList(MenusResponse menusResponse, boolean isPagination) {

        if(!isPagination || dataToDisplay==null)
          this.dataToDisplay = new ArrayList<>();



        final int sizeListBeforeAdding = dataToDisplay.size();
        if(isPagination){
            showPaginationProgressBar(false,false);

        }else{
            dataToDisplay.add(new DeliveryDivider());

        }

        if(!isPagination && menusResponse.getRecentOrders() != null && menusResponse.getRecentOrders().size()>0){
            int sizeRecentOrders = menusResponse.getRecentOrders().size();

            dataToDisplay.add(new MenusResponse.Category(true));
            int count = Math.min(RECENT_ORDERS_TO_SHOW, sizeRecentOrders);
            for(int i=0; i<count; i++){
                dataToDisplay.add(menusResponse.getRecentOrders().get(i));
            }
            if(sizeRecentOrders > RECENT_ORDERS_TO_SHOW){
                cacheRemainingRecentOrders(menusResponse.getRecentOrders());
                if(deliverySeeAllOrders == null){
                    deliverySeeAllOrders = new DeliverySeeAll(-1);
                }
                dataToDisplay.add(deliverySeeAllOrders);
            }
            dataToDisplay.add(new DeliveryDivider());
        }

        int vendorsCount = 0;
        if(menusResponse.getCategories() != null) {
            for (MenusResponse.Category category : menusResponse.getCategories()) {
                if (category.getVendors() != null) {
                    if(activity.getCategoryIdOpened() < 0) {
                        dataToDisplay.add(new MenusResponse.Category(category.getImage(), category.getCategoryName()));
                    }
                    dataToDisplay.addAll(category.getVendors());
                    if(activity.getCategoryIdOpened() < 0) {
                        if (category.getCount() > category.getVendors().size()) {
                            dataToDisplay.add(new DeliverySeeAll(category.getId()));
                        }
                    }
                    dataToDisplay.add(new DeliveryDivider());
                    vendorsCount = vendorsCount + category.getVendors().size();
                }
            }
            if(menusResponse.getCategories().size()>0)
                dataToDisplay.remove(dataToDisplay.size()-1);
        }

        //if no vendors to show
        if(!isPagination && (vendorsCount == 0)){
            int messageResId = R.string.no_menus_available_your_location;
            if(activity.getMenusFragment() != null
                    && !TextUtils.isEmpty(activity.getMenusFragment().getSearchText())){
                messageResId = R.string.oops_no_results_found;
            } else if(activity.isFilterApplied()){
                messageResId = R.string.no_menus_available_with_these_filters;
            }
            dataToDisplay.add(new NoVendorModel(activity.getString(messageResId)));
        }

       if(isPagination){
            final int sizeListAfterAddding = dataToDisplay.size();
            final int diff = sizeListAfterAddding-sizeListBeforeAdding;
           if(sizeListAfterAddding<sizeListBeforeAdding){
               if(diff==-1){
                 notifyItemRemoved(dataToDisplay.size());
               }else{
                   notifyItemRangeRemoved(dataToDisplay.size(),(-diff));
               }
           }else if(sizeListAfterAddding>sizeListBeforeAdding){
               if(diff==1){
                   notifyItemChanged(dataToDisplay.size());
               }else{
                   notifyItemRangeChanged(dataToDisplay.size(),diff);
               }
           }



        }else{
           notifyDataSetChanged();

       }

    }



    public void setList(List<Object> dataToDisplay) {
        this.dataToDisplay = dataToDisplay;
        notifyDataSetChanged();
    }
    public List<Object> getDataToDisplay() {
        return dataToDisplay;
    }

    private void cacheRemainingRecentOrders(List<RecentOrder> recentOrders){
        if(remainingRecentOrders == null){
            remainingRecentOrders = new ArrayList<>();
        }
        remainingRecentOrders.clear();
        remainingRecentOrders.addAll(recentOrders);
        if(remainingRecentOrders.size() > RECENT_ORDERS_TO_SHOW){
            for(int i=0; i<RECENT_ORDERS_TO_SHOW; i++) {
                remainingRecentOrders.remove(0);
            }
        }
    }

    private void fillRemainingRecentOrders(int position){
        if(remainingRecentOrders != null && remainingRecentOrders.size() > 0){
            dataToDisplay.addAll(position, remainingRecentOrders);
            dataToDisplay.remove(deliverySeeAllOrders);
            if(remainingRecentOrders.size() == 1){
                notifyItemChanged(position);
            } else {
                notifyItemRemoved(position);
                notifyItemRangeInserted(position+1, remainingRecentOrders.size());
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TITLE_CATEGORY:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_title, parent, false);
                return new ViewTitleCategory(v);
            case VIEW_ORDER_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_order_status, parent, false);
                return new ViewOrderStatus(v, this);
            case VIEW_SEE_ALL:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_see_all, parent, false);
                return new ViewSeeAll(v, this);
            case VIEW_VENDOR:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant, parent, false);
                return new ViewHolderVendor(v, this);
            case VIEW_DIVIDER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_divider_delivery, parent, false);
                return new ViewDivider(v);
            case OFFERS_PAGER_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_offers_pager, parent, false);
                return new ViewHolderOffers(v);
            case OFFER_STRIP_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_min_order, parent, false);
                return new ViewHolderOfferStrip(v, this);
            case ITEM_PROGRESS_BAR:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_bar_feed, parent, false);
                return new ProgressBarViewHolder(v);
            case NO_VENDORS_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_vendor, parent, false);
                return new ViewNoVenderItem(v);
            default:
                throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if(mholder instanceof ViewHolderVendor){
            DeliveryHomeAdapter.ViewHolderVendor mHolder = ((DeliveryHomeAdapter.ViewHolderVendor) mholder);
            MenusResponse.Vendor vendor = (MenusResponse.Vendor) dataToDisplay.get(position);
            mHolder.textViewRestaurantName.setText(vendor.getName());


            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String currentSystemTime = dateFormat.format(new Date());
            long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()), currentSystemTime);
            long minutes = ((timeDiff1 / (1000L* 60L)));
            if (minutes <= 0) {
                vendor.setIsClosed(1);
            }

            int visMinOrder = View.VISIBLE;
            if(!TextUtils.isEmpty(vendor.getMinOrderText())){
                mHolder.textViewMinimumOrder.setText(Utils.trimHTML(Utils.fromHtml(vendor.getMinOrderText())));
            } else if(vendor.getMinOrderText() == null) {
                if (vendor.getMinimumOrderAmount() != null && vendor.getMinimumOrderAmount() > 0) {
                    mHolder.textViewMinimumOrder.setText(activity.getString(R.string.minimum_order_rupee_format, Utils.getMoneyDecimalFormat().format(vendor.getMinimumOrderAmount())));
                } else {
                    mHolder.textViewMinimumOrder.setText(R.string.no_minimum_order);
                }
            } else {
                visMinOrder = View.GONE;
            }

            int visDeliveryTime = activity.setVendorDeliveryTimeAndDrawableColorToTextView(vendor, mHolder.textViewDelivery, R.color.text_color);
            int visibilityCloseTime = View.VISIBLE;
            RelativeLayout.LayoutParams paramsCloseTime = (RelativeLayout.LayoutParams) mHolder.textViewRestaurantCloseTime.getLayoutParams();
            RelativeLayout.LayoutParams paramsDelivery = (RelativeLayout.LayoutParams) mHolder.textViewDelivery.getLayoutParams();

            // restaurant is closed or not available
            if(vendor.getIsClosed() == 1 || vendor.getIsAvailable() == 0){
                mHolder.textViewRestaurantCloseTime.setText(R.string.closed);
                paramsCloseTime.addRule(RelativeLayout.BELOW, mHolder.textViewMinimumOrder.getId());
                paramsCloseTime.setMargins(paramsCloseTime.leftMargin, (int)(ASSL.Yscale() * 14f), (int)(ASSL.Xscale() * 14f),
                        paramsCloseTime.bottomMargin);

                paramsDelivery.setMargins(paramsDelivery.leftMargin, (int)(ASSL.Yscale() * 23f), paramsDelivery.rightMargin,
                        (int)(ASSL.Yscale() * 26f));
                paramsDelivery.addRule(RelativeLayout.RIGHT_OF, mHolder.textViewRestaurantCloseTime.getId());
                mHolder.imageViewRestaurantImage.setColorFilter(BW_FILTER);
                mHolder.tvOffer.getBackground().setColorFilter(BW_FILTER);
                mHolder.textViewMinimumOrder.setTextColor(ContextCompat.getColor(activity,R.color.text_color));

            } else {
                mHolder.imageViewRestaurantImage.setColorFilter(null);
                mHolder.tvOffer.getBackground().setColorFilter(null);
                mHolder.textViewMinimumOrder.setTextColor(ContextCompat.getColor(activity,R.color.order_history_status_color));

                // restaurant about to close
                if (minutes <= vendor.getBufferTime() && minutes > 0) {
                    mHolder.textViewRestaurantCloseTime.setText("Closing in " + minutes + (minutes>1?" mins":" min"));
                    paramsDelivery.setMargins(paramsDelivery.leftMargin, (int)(ASSL.Yscale() * 14f), paramsDelivery.rightMargin,
                            (int)(ASSL.Yscale() * 14f));
                }
                // restaurant is open
                else {
                    visibilityCloseTime = View.GONE;
                    paramsDelivery.setMargins(paramsDelivery.leftMargin, (int)(ASSL.Yscale() * 14f), paramsDelivery.rightMargin,
                            (int)(ASSL.Yscale() * 26f));
                }
                paramsDelivery.addRule(RelativeLayout.RIGHT_OF, mHolder.imageViewRestaurantImage.getId());
                paramsCloseTime.addRule(RelativeLayout.BELOW, mHolder.textViewDelivery.getId());
                paramsCloseTime.setMargins(paramsCloseTime.leftMargin, visDeliveryTime != View.VISIBLE ? (int)(ASSL.Yscale() * 14f) : 0, 0,
                        paramsCloseTime.bottomMargin);
            }
            mHolder.textViewRestaurantCloseTime.setVisibility(visibilityCloseTime);
            mHolder.textViewRestaurantCloseTime.setLayoutParams(paramsCloseTime);
            mHolder.textViewDelivery.setLayoutParams(paramsDelivery);
            mHolder.textViewDelivery.setVisibility(visDeliveryTime == View.VISIBLE ? View.VISIBLE : View.GONE);
            mHolder.textViewMinimumOrder.setVisibility(visMinOrder);


            mHolder.textViewAddressLine.setVisibility(!TextUtils.isEmpty(vendor.getRestaurantAddress()) ? View.VISIBLE : View.GONE);
            mHolder.textViewAddressLine.setText(vendor.getRestaurantAddress());


            int visibilityCuisines = View.VISIBLE;
            StringBuilder cuisines = new StringBuilder();
            if (vendor.getCuisines() != null && vendor.getCuisines().size() > 0) {
                int maxSize = vendor.getCuisines().size() > 3 ? 3 : vendor.getCuisines().size();
                for (int i = 0; i < maxSize; i++) {
                    String cuisine = vendor.getCuisines().get(i);
                    cuisines.append(cuisine);
                    if (i < maxSize - 1) {
                        cuisines.append(" ").append(activity.getString(R.string.bullet)).append(" ");
                    }
                }
            } else {
                visibilityCuisines = View.GONE;
            }
            mHolder.textViewRestaurantCusines.setText(cuisines.toString());
            mHolder.textViewRestaurantCusines.setVisibility(visibilityCuisines);


            try {
                if (!TextUtils.isEmpty(vendor.getImage())) {
                    float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                    Picasso.with(activity).load(vendor.getImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .resize((int)(ratio * 150f), (int)(ratio * 150f))
                            .centerCrop()
                            .transform(new RoundBorderTransform((int)(ratio*6f), 0))
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(mHolder.imageViewRestaurantImage);
                } else {
                    mHolder.imageViewRestaurantImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHolder.imageViewRestaurantImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
            }

            int visibilityRating = View.GONE;
            if (vendor.getRating() != null && vendor.getRating() >= 1d) {
                visibilityRating = View.VISIBLE;
                if(vendor.getIsClosed() == 1 || vendor.getIsAvailable() == 0){
                    setRatingViews(mHolder.llRatingStars,mHolder.tvReviewCount,vendor.getRating(), vendor.getReviewCount());
                } else{
                    setRatingViews(mHolder.llRatingStars,mHolder.tvReviewCount,vendor.getRating(), vendor.getReviewCount());
                }
            }
            mHolder.llRatingStars.setVisibility(visibilityRating);

            mHolder.tvOffer.setVisibility(TextUtils.isEmpty(vendor.getOfferText())?View.GONE:View.VISIBLE);
            mHolder.tvOffer.setText(vendor.getOfferText());
        } else if(mholder instanceof ViewOrderStatus){

            ViewOrderStatus statusHolder = ((ViewOrderStatus) mholder);
            try {
                RecentOrder recentOrder = (RecentOrder) dataToDisplay.get(position);
                statusHolder.relativeStatusBar.setVisibility(View.VISIBLE);
                for (int i = 0; i < statusHolder.relativeStatusBar.getChildCount(); i++) {
                    if (statusHolder.relativeStatusBar.getChildAt(i) instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) (statusHolder.relativeStatusBar.getChildAt(i));
                        for (int j = 0; j < viewGroup.getChildCount(); j++) {
                            viewGroup.getChildAt(j).setVisibility(View.GONE);
                        }
                    } else {
                        statusHolder.relativeStatusBar.getChildAt(i).setVisibility(View.GONE);
                    }
                }
                showPossibleStatus(possibleStatus, recentOrder.getStatus(), statusHolder);
                statusHolder.tvOrderIdValue.setText(Utils.fromHtml("Order: #<b>"+recentOrder.getOrderId().toString()+"</b>"));
                statusHolder.tvOrderIdValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                statusHolder.tvDeliveryTime.setText(recentOrder.getEndTime());
                statusHolder.tvDeliveryTime.setTextColor(ContextCompat.getColor(activity, R.color.text_color));

                // if orders are for view only on Delivery Home Page
                if(!showOrderOptions){
                    statusHolder.rlRestaurantInfo.setVisibility(View.GONE);
                    statusHolder.tvDeliveryTime.setText(statusHolder.tvOrderIdValue.getText());
                    statusHolder.tvOrderIdValue.setText(recentOrder.getRestaurantName());
                    statusHolder.tvOrderIdValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                    statusHolder.rlOrderNotDelivered.setVisibility(View.GONE);
                    statusHolder.rlTrackViewOrder.setVisibility(View.GONE);
                    return;
                }

                statusHolder.rlRestaurantInfo.setVisibility(!TextUtils.isEmpty(recentOrder.getRestaurantName()) ? View.VISIBLE : View.GONE);
                statusHolder.tvRestaurantName.setText(recentOrder.getRestaurantName());
                if(recentOrder.getOrderAmount() != null) {
                    statusHolder.tvOrderAmount.setText(activity.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormatWithoutFloat().format(recentOrder.getOrderAmount())));
                }

                // track or view order buttons
                statusHolder.rlTrackViewOrder.setVisibility(View.VISIBLE);
                if(recentOrder.getShowLiveTracking() == 1 && recentOrder.getDeliveryId() > 0){
                    statusHolder.tvTrackOrder.setTextColor(ContextCompat.getColorStateList(activity, R.color.purple_text_color_selector));
                } else {
                    statusHolder.tvTrackOrder.setTextColor(ContextCompat.getColor(activity, R.color.purple_text_color_aplha));
                }

                // not delivered views case
                if(recentOrder.isDeliveryNotDone() ||
                        recentOrder.getDeliveryConfirmation() == 1 || recentOrder.getDeliveryConfirmation() == 0){
                    statusHolder.rlOrderNotDelivered.setVisibility(View.VISIBLE);
                    statusHolder.rlTrackViewOrder.setVisibility(View.GONE);
                    statusHolder.relativeStatusBar.setVisibility(View.GONE);
                    Utils.setTextUnderline(statusHolder.tvDeliveryTime, activity.getString(R.string.view_order));
                    statusHolder.tvDeliveryTime.setTextColor(ContextCompat.getColorStateList(activity, R.color.text_color_selector));

                    RelativeLayout.LayoutParams paramsTV = (RelativeLayout.LayoutParams) statusHolder.tvOrderNotDelivered.getLayoutParams();
                    boolean deliveryMarkedYes = recentOrder.getDeliveryConfirmation() == 1;
                    int marginTop = activity.getResources().getDimensionPixelSize(deliveryMarkedYes ? R.dimen.dp_8 : R.dimen.dp_14);
                    statusHolder.tvOrderDeliveredDigIn.setVisibility(deliveryMarkedYes ? View.VISIBLE : View.GONE);
                    statusHolder.llOrderDeliveredYes.setVisibility(deliveryMarkedYes ? View.GONE : View.VISIBLE);
                    statusHolder.llOrderDeliveredNo.setVisibility(deliveryMarkedYes ? View.GONE : View.VISIBLE);
                    statusHolder.vOrderDeliveredMidSep.setVisibility(deliveryMarkedYes ? View.GONE : View.VISIBLE);
                    statusHolder.vOrderDeliveredTopSep.setVisibility(deliveryMarkedYes ? View.GONE : View.VISIBLE);

                    if(recentOrder.isDeliveryNotDone() && recentOrder.getDeliveryConfirmation() < 0){
                        statusHolder.tvOrderNotDelivered.setText(recentOrder.getDeliveryNotDoneMsg());
                        statusHolder.ivOrderDeliveredYes.setVisibility(View.GONE);
                        statusHolder.ivOrderDeliveredNo.setVisibility(View.GONE);
                        statusHolder.tvOrderDeliveredYes.setText(activity.getString(R.string.yes).toUpperCase()); statusHolder.tvOrderDeliveredYes.setTextSize(14);
                        statusHolder.tvOrderDeliveredNo.setText(activity.getString(R.string.no).toUpperCase()); statusHolder.tvOrderDeliveredNo.setTextSize(14);
                    }
                    else if(recentOrder.getDeliveryConfirmation() == 0){ // no case
                        statusHolder.tvOrderNotDelivered.setText(recentOrder.getDeliveryConfirmationMsg());
                        statusHolder.ivOrderDeliveredYes.setVisibility(View.VISIBLE);
                        statusHolder.ivOrderDeliveredYes.setImageResource(Data.isFuguChatEnabled()?R.drawable.ic_restaurant_chat:R.drawable.ic_restaurant_report);
                        statusHolder.ivOrderDeliveredNo.setVisibility(View.VISIBLE);
                        statusHolder.tvOrderDeliveredYes.setText(Data.isFuguChatEnabled()?R.string.chat_with_us :R.string.report_issue); statusHolder.tvOrderDeliveredYes.setTextSize(12);
                        statusHolder.tvOrderDeliveredNo.setText(R.string.call_restaurant); statusHolder.tvOrderDeliveredNo.setTextSize(12);
                    }
                    else if(deliveryMarkedYes){ // yes local case
                        statusHolder.tvOrderNotDelivered.setText(R.string.dig_in_enjoy_food_experience_feedback);
                    }
                    paramsTV.setMargins(paramsTV.leftMargin, marginTop, paramsTV.rightMargin, paramsTV.bottomMargin);
                    statusHolder.tvOrderNotDelivered.setLayoutParams(paramsTV);
                } else {
                    statusHolder.rlOrderNotDelivered.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (mholder instanceof ViewTitleCategory){
            ViewTitleCategory holder = (ViewTitleCategory) mholder;
            MenusResponse.Category category = (MenusResponse.Category) dataToDisplay.get(position);
            if(category.isTypeOrder()){
                holder.tvCateogoryTitle.setText(R.string.ongoing_orders);
                holder.iconTitle.setImageResource(R.drawable.ic_ongoing_orders);
            } else {
                holder.tvCateogoryTitle.setText(category.getCategoryName());
                if(!TextUtils.isEmpty(category.getImage())){
                    Picasso.with(activity).load(category.getImage())
                            .placeholder(R.drawable.ic_nav_select_category)
                            .error(R.drawable.ic_nav_select_category)
                            .into(holder.iconTitle);
                } else {
                    holder.iconTitle.setImageResource(R.drawable.ic_nav_select_category);
                }
            }
        }  else if (mholder instanceof ViewHolderOffers){
            List<MenusResponse.BannerInfo> bannerInfos = ((BannerInfosModel) dataToDisplay.get(position)).getBannerInfos();
            ViewHolderOffers holderOffers = (ViewHolderOffers) mholder;
            if(holderOffers.menusVendorOffersAdapter == null) {
                holderOffers.menusVendorOffersAdapter = new MenusVendorOffersAdapter(activity, bannerInfos, new MenusVendorOffersAdapter.Callback() {
                    @Override
                    public void onBannerInfoClick(MenusResponse.BannerInfo bannerInfo) {
                        if (bannerInfo.getRestaurantId() == -1 && bannerInfo.getDeepIndex() != -1) {
                            callback.onBannerInfoDeepIndexClick(bannerInfo.getDeepIndex());
                        } else if (bannerInfo.getRestaurantId() != -1 && bannerInfo.getDeepIndex() == -1) {
                            callback.onRestaurantSelected(bannerInfo.getRestaurantId());
                        }
                    }
                });
            } else {
                holderOffers.menusVendorOffersAdapter.setList(bannerInfos);
            }
            holderOffers.pagerMenusVendorOffers.setAdapter(holderOffers.menusVendorOffersAdapter);

            holderOffers.tabDots.setupWithViewPager(holderOffers.pagerMenusVendorOffers, true);
            for (int i = 0; i < holderOffers.tabDots.getTabCount(); i++) {
                View tab = ((ViewGroup) holderOffers.tabDots.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(activity.getResources().getDimensionPixelSize(R.dimen.dp_4), 0, 0, 0);
                tab.requestLayout();
            }
            if(bannerInfos.size() == 1){
                holderOffers.tabDots.setVisibility(View.GONE);
            } else{
                holderOffers.tabDots.setVisibility(View.VISIBLE);
            }
        } else if (mholder instanceof ViewHolderOfferStrip){
            MenusResponse.StripInfo stripInfo = (MenusResponse.StripInfo) dataToDisplay.get(position);
            ViewHolderOfferStrip holderStrip = (ViewHolderOfferStrip) mholder;
            holderStrip.textViewMinOrder.setText((stripInfo != null && !TextUtils.isEmpty(stripInfo.getText())) ? stripInfo.getText() : "");
        } else if (mholder instanceof ViewNoVenderItem){
            ViewNoVenderItem holder = (ViewNoVenderItem) mholder;
            holder.textViewNoMenus.setText(((NoVendorModel)dataToDisplay.get(position)).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return dataToDisplay == null ? 0 : dataToDisplay.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object  = dataToDisplay.get(position);

        if(object instanceof MenusResponse.Category)
            return VIEW_TITLE_CATEGORY;

        if(object instanceof DeliverySeeAll)
            return VIEW_SEE_ALL;

        if(object instanceof DeliveryDivider)
            return VIEW_DIVIDER;

        if(object instanceof RecentOrder)
            return VIEW_ORDER_ITEM;

        if(object instanceof MenusResponse.Vendor)
            return VIEW_VENDOR;

        if(object instanceof MenusResponse.StripInfo)
            return OFFER_STRIP_ITEM;

        if(object instanceof BannerInfosModel)
            return OFFERS_PAGER_ITEM;

        if(object instanceof ProgressBarModel)
            return ITEM_PROGRESS_BAR;

        if(object instanceof NoVendorModel)
            return NO_VENDORS_ITEM;

        Log.e(TAG, ">"+object);

        throw new IllegalArgumentException();
    }


    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if (pos != RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {
                case R.id.rlRoot:
                    callback.onRestaurantSelected(((MenusResponse.Vendor)dataToDisplay.get(pos)).getRestaurantId());
                    break;
                case R.id.tvViewOrder:
                case R.id.tvDeliveryTime:
                case R.id.container:
                    viewOrderClick(pos);
                    break;
                case R.id.tvTrackOrder:
                    liveTrackingClick(pos);
                    break;
                case R.id.llOrderDeliveredYes:
                    orderDeliveredClick(pos, true);
                    break;
                case R.id.llOrderDeliveredNo:
                    orderDeliveredClick(pos, false);
                    break;
                case R.id.ll_see_all:
                    if(dataToDisplay.get(pos) instanceof DeliverySeeAll){
                        if(((DeliverySeeAll)dataToDisplay.get(pos)).getCategoryId() > 0){
							callback.openCategory(((DeliverySeeAll)dataToDisplay.get(pos)).getCategoryId());
                        } else {
							// TODO: 06/09/17 recent orders see all
                            fillRemainingRecentOrders(pos);
						}
                    }
                    break;
                case R.id.llRoot:
                    break;

                case R.id.tvNeedHelp:
                    break;

                case R.id.tvViewDetails:
                    break;

                case R.id.llMain:
                    break;
                case R.id.textViewMinOrder:
                    MenusResponse.StripInfo stripInfo = (MenusResponse.StripInfo) dataToDisplay.get(pos);
                    if(stripInfo != null) {
                        if (stripInfo.getRestaurantId() == -1 && stripInfo.getDeepIndex() != -1) {
                            callback.onBannerInfoDeepIndexClick(stripInfo.getDeepIndex());
                        } else if (stripInfo.getRestaurantId() != -1 && stripInfo.getDeepIndex() == -1) {
                            callback.onRestaurantSelected(stripInfo.getRestaurantId());
                        }
                    }
                    break;
            }
        }
    }

    public void showPaginationProgressBar(boolean show, boolean notifyAdapter) {
        if(dataToDisplay==null || dataToDisplay.size()==0)
            return;

        boolean isProgressBarExist = dataToDisplay.get(dataToDisplay.size() - 1) instanceof ProgressBarModel;
        if(show) {
            if (!isProgressBarExist) {
                dataToDisplay.add(ProgressBarModel.getInstance());
                if(notifyAdapter){
                    recyclerView.post(getProgressDisplayRunnable(true,dataToDisplay.size()));

                }
            }
        }else{
           if(dataToDisplay.remove(ProgressBarModel.getInstance())){
               if(notifyAdapter){
                   recyclerView.post(getProgressDisplayRunnable(false,dataToDisplay.size()));

               }
           }

        }

    }

    private  class ProgressBarDisplayRunnable implements Runnable {
        private int position;
        private boolean isInsert;

        public void setPosition(int position) {
            this.position = position;
        }

        public void setInsert(boolean insert) {
            isInsert = insert;
        }

        @Override
        public void run() {
            if(isInsert){
                notifyItemInserted(position);

            }else{
                notifyItemRemoved(position);

            }

        }
    }
    private ProgressBarDisplayRunnable progressBarDisplayRunnable;
    private ProgressBarDisplayRunnable getProgressDisplayRunnable(boolean isInsert,int position){
        if(progressBarDisplayRunnable==null){
            progressBarDisplayRunnable = new ProgressBarDisplayRunnable();
        }
        progressBarDisplayRunnable.setInsert(isInsert);
        progressBarDisplayRunnable.setPosition(position);

        return progressBarDisplayRunnable;
    }

    private class ViewHolderVendor extends RecyclerView.ViewHolder {
        public RelativeLayout rlRoot;
        View vSep;
        ImageView imageViewRestaurantImage;
        TextView textViewRestaurantName, textViewMinimumOrder, textViewRestaurantCusines;
        TextView textViewRestaurantCloseTime, textViewAddressLine, textViewDelivery,tvOffer;
        LinearLayout llRatingStars;TextView tvReviewCount;




        public ViewHolderVendor(final View itemView, final ItemListener itemListener) {
            super(itemView);
            rlRoot = (RelativeLayout) itemView.findViewById(R.id.rlRoot);
            rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(rlRoot, itemView);
                }
            });
            vSep = itemView.findViewById(R.id.vSep);
            imageViewRestaurantImage = (ImageView) itemView.findViewById(R.id.imageViewRestaurantImage);
            textViewRestaurantName = (TextView) itemView.findViewById(R.id.textViewRestaurantName); textViewRestaurantName.setTypeface(textViewRestaurantName.getTypeface(), Typeface.BOLD);
            textViewMinimumOrder = (TextView) itemView.findViewById(R.id.textViewMinimumOrder);
            textViewRestaurantCusines = (TextView) itemView.findViewById(R.id.textViewRestaurantCusines);
            textViewRestaurantCloseTime = (TextView) itemView.findViewById(R.id.textViewRestaurantCloseTime);
            textViewAddressLine = (TextView) itemView.findViewById(R.id.textViewAddressLine);
            textViewDelivery = (TextView) itemView.findViewById(R.id.textViewDelivery);
            llRatingStars = (LinearLayout) itemView.findViewById(R.id.llRatingStars);
            tvReviewCount = (TextView) itemView.findViewById(R.id.tvReviewCount);
            tvOffer = (TextView)itemView.findViewById(R.id.tv_offer);
        }
    }
    private class ViewOrderStatus extends RecyclerView.ViewHolder {

        public LinearLayout linear;
        RelativeLayout container, relativeStatusBar;
        TextView tvOrderIdValue, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        View lineStatus1, lineStatus2, lineStatus3;
        RelativeLayout rlRestaurantInfo, rlTrackViewOrder;
        TextView tvRestaurantName, tvOrderAmount, tvTrackOrder, tvViewOrder;

        RelativeLayout rlOrderNotDelivered;
        TextView tvOrderDeliveredDigIn, tvOrderNotDelivered, tvOrderDeliveredYes, tvOrderDeliveredNo;
        LinearLayout llOrderDeliveredYes, llOrderDeliveredNo;
        ImageView ivOrderDeliveredYes, ivOrderDeliveredNo;
        View vOrderDeliveredMidSep, vOrderDeliveredTopSep;

        ViewOrderStatus(final View itemView, final ItemListener itemListener) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            relativeStatusBar = (RelativeLayout) itemView.findViewById(R.id.relativeStatusBar);
            tvOrderIdValue = (TextView) itemView.findViewById(R.id.tvOrderIdValue);
            tvOrderIdValue.setTypeface(Fonts.mavenMedium(activity));
            tvDeliveryTime = (TextView) itemView.findViewById(R.id.tvDeliveryTime);
            tvDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
            tvStatus0 = (TextView) itemView.findViewById(R.id.tvStatus0);
            tvStatus0.setTypeface(Fonts.mavenRegular(activity));
            tvStatus1 = (TextView) itemView.findViewById(R.id.tvStatus1);
            tvStatus1.setTypeface(Fonts.mavenRegular(activity));
            tvStatus2 = (TextView) itemView.findViewById(R.id.tvStatus2);
            tvStatus2.setTypeface(Fonts.mavenRegular(activity));
            tvStatus3 = (TextView) itemView.findViewById(R.id.tvStatus3);
            tvStatus3.setTypeface(Fonts.mavenRegular(activity));
            ivStatus0 = (ImageView) itemView.findViewById(R.id.ivStatus0);
            ivStatus1 = (ImageView) itemView.findViewById(R.id.ivStatus1);
            ivStatus2 = (ImageView) itemView.findViewById(R.id.ivStatus2);
            ivStatus3 = (ImageView) itemView.findViewById(R.id.ivStatus3);
            lineStatus1 = itemView.findViewById(R.id.lineStatus1);
            lineStatus2 = itemView.findViewById(R.id.lineStatus2);
            lineStatus3 = itemView.findViewById(R.id.lineStatus3);

            rlRestaurantInfo = (RelativeLayout) itemView.findViewById(R.id.rlRestaurantInfo);
            rlTrackViewOrder = (RelativeLayout) itemView.findViewById(R.id.rlTrackViewOrder);
            tvRestaurantName = (TextView) itemView.findViewById(R.id.tvRestaurantName);
            tvOrderAmount = (TextView) itemView.findViewById(R.id.tvOrderAmount);
            tvTrackOrder = (TextView) itemView.findViewById(R.id.tvTrackOrder);
            tvTrackOrder.setTypeface(tvTrackOrder.getTypeface(), Typeface.BOLD);
            tvViewOrder = (TextView) itemView.findViewById(R.id.tvViewOrder);
            tvViewOrder.setTypeface(tvViewOrder.getTypeface(), Typeface.BOLD);

            rlOrderNotDelivered = (RelativeLayout) itemView.findViewById(R.id.rlOrderNotDelivered);
            tvOrderNotDelivered = (TextView) itemView.findViewById(R.id.tvOrderNotDelivered);
            tvOrderDeliveredDigIn = (TextView) itemView.findViewById(R.id.tvOrderDeliveredDigIn);
            tvOrderDeliveredDigIn.setTypeface(tvOrderDeliveredDigIn.getTypeface(), Typeface.BOLD);
            tvOrderDeliveredYes = (TextView) itemView.findViewById(R.id.tvOrderDeliveredYes);
            tvOrderDeliveredYes.setTypeface(tvOrderDeliveredYes.getTypeface(), Typeface.BOLD);
            tvOrderDeliveredNo = (TextView) itemView.findViewById(R.id.tvOrderDeliveredNo);
            tvOrderDeliveredNo.setTypeface(tvOrderDeliveredNo.getTypeface(), Typeface.BOLD);
            llOrderDeliveredYes = (LinearLayout) itemView.findViewById(R.id.llOrderDeliveredYes);
            llOrderDeliveredNo = (LinearLayout) itemView.findViewById(R.id.llOrderDeliveredNo);
            ivOrderDeliveredYes = (ImageView) itemView.findViewById(R.id.ivOrderDeliveredYes);
            ivOrderDeliveredNo = (ImageView) itemView.findViewById(R.id.ivOrderDeliveredNo);
            vOrderDeliveredMidSep = itemView.findViewById(R.id.vOrderDeliveredMidSep);
            vOrderDeliveredTopSep = itemView.findViewById(R.id.vOrderDeliveredTopSep);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(v, itemView);
                }
            };

            tvViewOrder.setOnClickListener(onClickListener);
            tvDeliveryTime.setOnClickListener(onClickListener);
            container.setOnClickListener(onClickListener);
            tvTrackOrder.setOnClickListener(onClickListener);
            llOrderDeliveredYes.setOnClickListener(onClickListener);
            llOrderDeliveredNo.setOnClickListener(onClickListener);
        }
    }
    class ViewTitleCategory extends RecyclerView.ViewHolder {
        @Bind(R.id.icon_title)
        ImageView iconTitle;
        @Bind(R.id.tv_cateogory_title)
        TextView tvCateogoryTitle;

        ViewTitleCategory(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    private static class ViewDivider extends RecyclerView.ViewHolder {

        ViewDivider(View view) {
            super(view);
        }
    }
    class ViewSeeAll extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_see_all)
        LinearLayout llSeeAll;

        ViewSeeAll(final View itemView, final ItemListener itemListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(llSeeAll, itemView);
                }
            });
        }
    }
    public class ViewHolderOffers extends RecyclerView.ViewHolder {
        public MenusVendorOffersAdapter menusVendorOffersAdapter;
        public ViewPager pagerMenusVendorOffers;
        public TabLayout tabDots;

        public ViewHolderOffers(View view) {
            super(view);
            pagerMenusVendorOffers = (ViewPager) view.findViewById(R.id.pagerMenusVendorOffers);
            tabDots = (TabLayout) view.findViewById(R.id.tabDots);
        }
    }

    public class ViewHolderOfferStrip extends RecyclerView.ViewHolder {
        public TextView textViewMinOrder;
        public ViewHolderOfferStrip(final View view, final ItemListener itemListener){
            super(view);
            textViewMinOrder = (TextView) view.findViewById(R.id.textViewMinOrder);
            ViewGroup.LayoutParams params = textViewMinOrder.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            textViewMinOrder.setLayoutParams(params);

            textViewMinOrder.setVisibility(View.VISIBLE);
            textViewMinOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(v, view);
                }
            });
        }
    }
    private class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        public ProgressBarViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ViewNoVenderItem extends RecyclerView.ViewHolder {
        RelativeLayout rlLayoutNoVender;
        TextView textViewNoMenus;

        ViewNoVenderItem(View itemView) {
            super(itemView);
            rlLayoutNoVender = (RelativeLayout) itemView.findViewById(R.id.rlLayoutNoVender);
            textViewNoMenus = (TextView) itemView.findViewById(R.id.textViewNoMenus);
        }
    }

    public static class DeliverySeeAll{
        private int categoryId;

        private DeliverySeeAll(){}

        public DeliverySeeAll(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }
    }
    public static class DeliveryDivider{
        private static DeliveryDivider deliveryDivider;
        private DeliveryDivider() {
        }
        public DeliveryDivider getInstance(){
            if(deliveryDivider==null)
                deliveryDivider= new DeliveryDivider();
            return deliveryDivider;
        }
    }
    public static class ProgressBarModel{
        private static ProgressBarModel progressBarModel;
        private ProgressBarModel() {
        }
        public static ProgressBarModel getInstance(){
            if(progressBarModel ==null)
                progressBarModel = new ProgressBarModel();
            return progressBarModel;
        }
    }
    public static class BannerInfosModel{
        private List<MenusResponse.BannerInfo> bannerInfos;

        public BannerInfosModel(List<MenusResponse.BannerInfo> bannerInfos) {
            this.bannerInfos = bannerInfos;
        }

        public List<MenusResponse.BannerInfo> getBannerInfos() {
            return bannerInfos;
        }
    }
    public static class NoVendorModel{
        private String message;
        public NoVendorModel(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }


    private void setRatingViews(LinearLayout llRatingStars,TextView tvReviewCount,Double rating, long reviewCount) {
        llRatingStars.removeAllViews();
        activity.addStarsToLayout(llRatingStars, rating,
                R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey);
        llRatingStars.addView(tvReviewCount);
        tvReviewCount.setText(reviewCount+" Ratings");
    }

    public interface Callback {
        void onRestaurantSelected(int vendorId);
        void onBannerInfoDeepIndexClick(int deepIndex);
        void openCategory(int categoryId);
    }

    private void showPossibleStatus(ArrayList<String> possibleStatus, int status, ViewOrderStatus statusHolder) {
        setDefaultState(statusHolder);
        int selectedSize = (int) (35 * ASSL.Xscale());
        switch (possibleStatus.size()) {
            case 4:
                statusHolder.tvStatus3.setVisibility(View.VISIBLE);
                statusHolder.ivStatus3.setVisibility(View.VISIBLE);
                statusHolder.lineStatus3.setVisibility(View.VISIBLE);
                statusHolder.tvStatus3.setText(possibleStatus.get(3));
                if (status == 3) {
                    statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                } else if (status > 3) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus3.setLayoutParams(layoutParams);
                    statusHolder.ivStatus3.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                }
            case 3:
                statusHolder.tvStatus2.setVisibility(View.VISIBLE);
                statusHolder.ivStatus2.setVisibility(View.VISIBLE);
                statusHolder.lineStatus2.setVisibility(View.VISIBLE);
                statusHolder.tvStatus2.setText(possibleStatus.get(2));
                if (status == 2) {
                    statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                } else if (status > 2) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus2.setLayoutParams(layoutParams);
                    statusHolder.ivStatus2.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                }
            case 2:
                statusHolder.tvStatus1.setVisibility(View.VISIBLE);
                statusHolder.ivStatus1.setVisibility(View.VISIBLE);
                statusHolder.lineStatus1.setVisibility(View.VISIBLE);
                statusHolder.tvStatus1.setText(possibleStatus.get(1));
                if (status == 1) {
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                } else if (status > 1) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus1.setLayoutParams(layoutParams);
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(activity, R.color.order_status_green));
                }
            case 1:
                statusHolder.tvStatus0.setVisibility(View.VISIBLE);
                statusHolder.ivStatus0.setVisibility(View.VISIBLE);
                statusHolder.tvStatus0.setText(possibleStatus.get(0));
                if (status == 0) {
                    statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status_green);
                } else if (status > 0) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus0.setLayoutParams(layoutParams);
                    statusHolder.ivStatus0.setBackgroundResource(R.drawable.ic_order_status_green);

                }
                break;
        }
    }

    private void setDefaultState(ViewOrderStatus statusHolder) {
        int selectedSize = (int) (25 * ASSL.Xscale());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus3.setLayoutParams(layoutParams);
        statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(activity, R.color.rank_5));
        statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus2.setLayoutParams(layoutParams);
        statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(activity, R.color.rank_5));
        statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus1.setLayoutParams(layoutParams);
        statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(activity, R.color.rank_5));
        statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status);
        statusHolder.ivStatus0.setLayoutParams(layoutParams);
    }

    private void viewOrderClick(int pos){
        try {
            Intent intent = new Intent(activity, RideTransactionsActivity.class);
            intent.putExtra(Constants.KEY_ORDER_ID, ((RecentOrder)dataToDisplay.get(pos)).getOrderId());
            intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MENUS.getOrdinal());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void liveTrackingClick(int pos){
        try {
            RecentOrder order = ((RecentOrder)dataToDisplay.get(pos));
            if(order.getShowLiveTracking() == 1 && order.getDeliveryId() > 0) {
                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                intent.putExtra(Constants.KEY_ORDER_ID, order.getOrderId());
                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MENUS.getOrdinal());
                intent.putExtra(Constants.KEY_OPEN_LIVE_TRACKING, 1);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);


            } else {
                Utils.showToast(activity, !TextUtils.isEmpty(order.getTrackDeliveryMessage()) ?
                        order.getTrackDeliveryMessage() : activity.getString(R.string.tracking_not_available_message));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void orderDeliveredClick(int pos, boolean delivered){
        try {
            final RecentOrder order = (RecentOrder) dataToDisplay.get(pos);
            if(delivered){
                if(order.isDeliveryNotDone() && order.getDeliveryConfirmation() < 0) {
                    apiConfirmDelivery(order.getOrderId(), 1, pos);
                } else {
                    activity.getHomeUtil().openFuguOrSupport(activity, activity.getRelativeLayoutContainer(),
                            order.getOrderId(), order.getSupportCategory(), order.getExpectedDeliveryDate(), ProductType.MENUS.getOrdinal());
                }
            } else {
                if (order.isDeliveryNotDone() && order.getDeliveryConfirmation() < 0) {
                    apiConfirmDelivery(order.getOrderId(), 0, pos);
                } else {
                    Utils.openCallIntent(activity, order.getRestaurantNumber());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiConfirmDelivery(int orderId, final int isDelivered, final int position) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));
                params.put(Constants.KEY_IS_DELIVERED, String.valueOf(isDelivered));

                DialogPopup.showLoadingDialog(activity, "");

                new HomeUtil().putDefaultParams(params);
                RestClient.getMenusApiService().confirmDeliveryByUser(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt productsResponse, Response response) {
                        try {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                    RecentOrder order = (RecentOrder) dataToDisplay.get(position);
                                    order.setDeliveryConfirmation(isDelivered);
                                    order.setDeliveryConfirmationMsg(productsResponse.getMessage());
                                    notifyDataSetChanged();
                                } else {
                                    DialogPopup.alertPopup(activity, "", productsResponse.getMessage());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}