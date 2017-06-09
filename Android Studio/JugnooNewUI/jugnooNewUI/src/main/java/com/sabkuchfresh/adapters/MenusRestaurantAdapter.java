package com.sabkuchfresh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.feed.ui.dialogs.DialogPopupTwoButtonCapsule;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.RestaurantSearchResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.ViewType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {


    private String TAG = "Menus Screen";
    private ArrayList<RecentOrder> recentOrders;
    private ArrayList<String> possibleStatus;

    private FreshActivity activity;
    private ArrayList<MenusResponse.Vendor> vendorsComplete, vendorsFiltered, vendorsToShow;
    private HashMap<Integer, MenusResponse.Vendor> restIdMappedVendors;
    private Callback callback;
    private String searchText;
    private boolean searchApiHitOnce = false;
    private RecyclerView recyclerView;
    private List<MenusResponse.BannerInfo> bannerInfos;
    private MenusResponse.StripInfo stripInfo;

    private static final int MAIN_ITEM = 0;
    private static final int FORM_ITEM = 1;
    private static final int STATUS_ITEM = 3;
    private static final int NO_VENDORS_ITEM = 4;
    private static final int OFFERS_PAGER_ITEM = 5;
    private static final int OFFER_STRIP_ITEM = 6;



    public MenusRestaurantAdapter(FreshActivity activity, ArrayList<MenusResponse.Vendor> vendors,
                                  ArrayList<RecentOrder> recentOrders, ArrayList<String> possibleStatus, Callback callback, RecyclerView recyclerView) {
        this.activity = activity;
        this.vendorsComplete = vendors;
        this.vendorsFiltered = new ArrayList<>();
        this.vendorsFiltered.addAll(vendors);
        this.restIdMappedVendors = new HashMap<>();
        setRestIdMappedVendors();
        this.vendorsToShow = new ArrayList<>();
        this.vendorsToShow.addAll(vendors);
        this.callback = callback;
        searchText = "";

        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        timerHandler = activity.getHandler();
        timerHandler.postDelayed(timerRunnable, 1000);
        restaurantName = ""; locality = ""; telephone = "";
        searchApiHitOnce = false;
        this.recyclerView = recyclerView;
    }

    private Handler timerHandler;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                applyFilter();
                if (timerHandler != null) {
                    Log.v(TAG, "notifying automaically");
                    timerHandler.postDelayed(timerRunnable, 60000); //run every minute
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private synchronized void searchVendors(String text, List<Integer> searchedRestaurantIds){
        vendorsToShow.clear();
        text = text.toLowerCase();
        if(TextUtils.isEmpty(text)){
            vendorsToShow.addAll(vendorsFiltered);
        } else {
            if(searchedRestaurantIds == null) {
                for (MenusResponse.Vendor vendor : vendorsFiltered) {
                    if (vendor.getName().toLowerCase().contains(text) || vendor.getCuisines().toString().toLowerCase().contains(text)) {
                        vendorsToShow.add(vendor);
                    }
                }
            } else {
                for(Integer restId : searchedRestaurantIds){
                    if(restIdMappedVendors.containsKey(restId)){
                        vendorsToShow.add(restIdMappedVendors.get(restId));
                    }
                }
            }
        }
        notifyDataSetChanged();
        callback.onNotify(vendorsToShow.size());
    }

    public void setList(ArrayList<MenusResponse.Vendor> vendors, List<MenusResponse.BannerInfo> bannerInfos,
                        MenusResponse.StripInfo stripInfo, boolean showBanner) {
        this.vendorsComplete = vendors;
        this.vendorsFiltered.clear();
        this.vendorsFiltered.addAll(vendors);
        setRestIdMappedVendors();
        this.vendorsToShow.clear();
        this.vendorsToShow.addAll(vendors);

        this.bannerInfos = showBanner ? bannerInfos : null;
        this.stripInfo = showBanner ? null : stripInfo;
        applyFilter();
    }

    private void setRestIdMappedVendors(){
        restIdMappedVendors.clear();
        for(MenusResponse.Vendor vendor : vendorsFiltered){
            restIdMappedVendors.put(vendor.getRestaurantId(), vendor);
        }
    }

    public void applyFilter(){
        vendorsFiltered.clear();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        for (MenusResponse.Vendor vendor : vendorsComplete) {
            boolean cuisineMatched = false, moMatched, dtMatched;
            for (String cuisine : activity.getCuisinesSelected()) {
                if (vendor.getCuisines().contains(cuisine)) {
                    cuisineMatched = true;
                    break;
                }
            }
            cuisineMatched = activity.getCuisinesSelected().size() <= 0 || cuisineMatched;

            moMatched = activity.getMoSelected() == MenusFilterFragment.MinOrder.NONE
                    || vendor.getMinimumOrderAmount() <= activity.getMoSelected().getOrdinal();

            dtMatched = activity.getDtSelected() == MenusFilterFragment.DeliveryTime.NONE
                    || vendor.getMinDeliveryTime() <= activity.getDtSelected().getOrdinal();

            boolean qfMatched = true;
            for(String filter : activity.getQuickFilterSelected()) {
                if((filter.equalsIgnoreCase(Constants.ACCEPTONLINE) && vendor.getApplicablePaymentMode().equals(ApplicablePaymentMode.CASH.getOrdinal()))
                        || (filter.equalsIgnoreCase(Constants.OFFERSDISCOUNT) && vendor.getOffersDiscounts().equals(0))
                        || (filter.equalsIgnoreCase(Constants.PUREVEGETARIAN) && vendor.getPureVegetarian().equals(0))
                        || (filter.equalsIgnoreCase(Constants.FREEDELIVERY) && vendor.getFreeDelivery().equals(0))) {
                    qfMatched = false;
                    break;
                }
            }


            try {
                if (DateOperations.getTimeDifferenceInHHmmss(vendor.getCloseIn(), vendor.getOpensAt()) >= 0) {
					String currentSystemTime = dateFormat.format(new Date());
					long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()), currentSystemTime);
					long minutes = ((timeDiff1 / (1000L * 60L)));
					if (minutes <= 0) {
						vendor.setIsClosed(1);
					}
				}
            } catch (Exception e) {
                e.printStackTrace();
            }


            if(cuisineMatched && moMatched && dtMatched && qfMatched){
                vendorsFiltered.add(vendor);
            }
        }

        Collections.sort(vendorsFiltered, new Comparator<MenusResponse.Vendor>() {
            @Override
            public int compare(MenusResponse.Vendor lhs, MenusResponse.Vendor rhs) {
                int point = 0;
                if (lhs.getIsClosed() == 0 && rhs.getIsClosed() != 0) {
                    point = -(rhs.getIsClosed() - lhs.getIsClosed());
                } else if (lhs.getIsClosed() != 0 && rhs.getIsClosed() == 0) {
                    point = -(rhs.getIsClosed() - lhs.getIsClosed());
                } else if (lhs.getIsAvailable() == 0 && rhs.getIsAvailable() != 0) {
                    point = rhs.getIsAvailable() - lhs.getIsAvailable();
                } else if (lhs.getIsAvailable() != 0 && rhs.getIsAvailable() == 0) {
                    point = rhs.getIsAvailable() - lhs.getIsAvailable();
                } else if (activity.getSortBySelected() == MenusFilterFragment.SortType.POPULARITY) {
                    point = rhs.getPopularity() - lhs.getPopularity();
                } else if (activity.getSortBySelected() == MenusFilterFragment.SortType.DISTANCE) {
                    point = -(int) (rhs.getDistance() - lhs.getDistance());
                } else if (activity.getSortBySelected() == MenusFilterFragment.SortType.PRICE) {
                    point = lhs.getPriceRange() - rhs.getPriceRange();
                } else if (activity.getSortBySelected() == MenusFilterFragment.SortType.DELIVERY_TIME) {
                    point = lhs.getDeliveryTime() - rhs.getDeliveryTime();
                }

                return point;
            }
        });
        setRestIdMappedVendors();

        searchRestaurant(searchText);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        } else if (viewType == FORM_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommend_restaurant, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolderRestaurantForm(v, activity);
        }
        else if (viewType == STATUS_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_status, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewTitleStatus(v, activity);
        } else if (viewType == NO_VENDORS_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_vendor, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewNoVenderItem(v, activity);
        } else if(viewType == OFFERS_PAGER_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_offers_pager, parent, false);
            return new ViewHolderOffers(v);
        } else if(viewType == OFFER_STRIP_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_min_order, parent, false);
            return new ViewHolderOfferStrip(v, this);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if(offerStripSize() > 0 && position > 0){
                position--;
            }
            if(offerVendorsSize() > 0 && position > 0){
                position--;
            }
            if (holder instanceof ViewTitleStatus) {
                ViewTitleStatus statusHolder = ((ViewTitleStatus) holder);
                try {
                    RecentOrder recentOrder = recentOrders.get(position);
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
                    statusHolder.tvOrderIdValue.setText("#"+recentOrder.getOrderId().toString());
                    statusHolder.tvDeliveryTime.setText(recentOrder.getEndTime());

                    statusHolder.container.setTag(position);
                    statusHolder.tvViewOrder.setTag(position);
                    statusHolder.tvTrackOrder.setTag(position);
                    statusHolder.container.setOnClickListener(viewOrderOnClickListener);
                    statusHolder.tvViewOrder.setOnClickListener(viewOrderOnClickListener);
                    statusHolder.tvTrackOrder.setOnClickListener(trackOrderOnClickListener);

                    statusHolder.rlTrackViewOrder.setVisibility(View.VISIBLE);
                    if(recentOrder.getShowLiveTracking() == 1 && recentOrder.getDeliveryId() > 0){
                        statusHolder.tvTrackOrder.setTextColor(ContextCompat.getColorStateList(activity, R.color.purple_text_color_selector));
                    } else {
                        statusHolder.tvTrackOrder.setTextColor(ContextCompat.getColor(activity, R.color.purple_text_color_aplha));
                    }

                    statusHolder.rlRestaurantInfo.setVisibility(!TextUtils.isEmpty(recentOrder.getRestaurantName()) ? View.VISIBLE : View.GONE);
                    statusHolder.tvRestaurantName.setText(recentOrder.getRestaurantName());
                    if(recentOrder.getOrderAmount() != null) {
                        statusHolder.tvOrderAmount.setText(activity.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormatWithoutFloat().format(recentOrder.getOrderAmount())));
                    }

                    if(recentOrder.getOrderNotDeliveredYet() == 1){
                        statusHolder.rlOrderNotDelivered.setVisibility(View.VISIBLE);
                        statusHolder.tvOrderNotDelivered.setText(recentOrder.getOrderNotDeliveredMessage());
                        statusHolder.rlOrderNotDelivered.setTag(position);
                        statusHolder.rlOrderNotDelivered.setOnClickListener(orderNotDeliveredListener);
                    } else {
                        statusHolder.rlOrderNotDelivered.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (holder instanceof ViewHolder) {
                position = position - recentOrdersSize();
                ViewHolder mHolder = ((ViewHolder) holder);
                MenusResponse.Vendor vendor = vendorsToShow.get(position);
                mHolder.textViewRestaurantName.setText(vendor.getName());

                mHolder.vSep.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String currentSystemTime = dateFormat.format(new Date());
                long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()), currentSystemTime);
                long minutes = ((timeDiff1 / (1000L* 60L)));

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
                } else {
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

                mHolder.rlRoot.setTag(position);
                mHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            callback.onRestaurantSelected(vendorsToShow.get(pos).getRestaurantId());
//                            if(searchApiHitOnce && searchText.length() > 0){
//                            for analytics event
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });




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
                    activity.setRatingAndGetColor(mHolder.tvRating, vendor.getRating(), vendor.getColorCode(), true);
                }
                mHolder.tvRating.setVisibility(visibilityRating);



/*
 Edited by Parminder Singh on 2/24/17 at 6:10 PM
 Displaying Offers strip
 **/

				mHolder.tvOffer.setVisibility(TextUtils.isEmpty(vendor.getOfferText())?View.GONE:View.VISIBLE);
				mHolder.viewOffer.setVisibility(TextUtils.isEmpty(vendor.getOfferText())?View.GONE:View.VISIBLE);
				mHolder.tvOffer.setText(vendor.getOfferText());

            } else if (holder instanceof ViewHolderRestaurantForm) {
                ViewHolderRestaurantForm titleHolder = ((ViewHolderRestaurantForm) holder);
                titleHolder.etRestaurantName.setText(restaurantName);
                titleHolder.etLocality.setText(locality);
                titleHolder.etTelephone.setText(telephone);
                titleHolder.etRestaurantName.setSelection(restaurantName.length());

                titleHolder.bSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        apiRecommendRestaurant();
                        GAUtils.event(GAAction.MENUS, GAAction.HOME , GAAction.NEW_RESTAURANT + GAAction.SUBMITTED);
//                        (Events.MENUS, Events.ADD_RESTRO, restaurantName);
                    }
                });


            } else if (holder instanceof ViewNoVenderItem){
                ViewNoVenderItem holderNoVenderItem = (ViewNoVenderItem) holder;
                if (vendorsComplete.size() == 0) {
                    holderNoVenderItem.textViewNoMenus.setText(R.string.no_menus_available_your_location);
                } else if (vendorsFiltered.size() == 0) {
                    holderNoVenderItem.textViewNoMenus.setText(R.string.no_menus_available_with_these_filters);
                } else if (vendorsToShow.size() == 0) {
                    holderNoVenderItem.textViewNoMenus.setText(R.string.oops_no_results_found);
                }

            } else if (holder instanceof ViewHolderOffers){
                ViewHolderOffers holderOffers = (ViewHolderOffers) holder;
                holderOffers.menusVendorOffersAdapter.setList(bannerInfos);
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
            } else if (holder instanceof ViewHolderOfferStrip){
                ViewHolderOfferStrip holderStrip = (ViewHolderOfferStrip) holder;
                holderStrip.textViewMinOrder.setText((stripInfo != null && !TextUtils.isEmpty(stripInfo.getText())) ? stripInfo.getText() : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void showPossibleStatus(ArrayList<String> possibleStatus, int status, ViewTitleStatus statusHolder) {
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

    private void setDefaultState(ViewTitleStatus statusHolder) {
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


    public boolean filterApplied() {
        return (activity.getCuisinesSelected().size() > 0
                || activity.getMoSelected() != MenusFilterFragment.MinOrder.NONE
                || activity.getDtSelected() != MenusFilterFragment.DeliveryTime.NONE
                || activity.getSortBySelected() != MenusFilterFragment.SortType.NONE
                || activity.getQuickFilterSelected().size() > 0);
    }

    @Override
    public int getItemViewType(int position) {
        if(offerStripSize() > 0){
            if(position == 0){
                return OFFER_STRIP_ITEM;
            } else {
                position--;
            }
        }
        if(offerVendorsSize() > 0){
            if(position == 0) {
                return OFFERS_PAGER_ITEM;
            } else {
                position--;
            }
        }
        if (vendorsCompleteCount() > 0) {
            if (position >= 0 && position < recentOrdersSize()) {
                return STATUS_ITEM;
            } else if (position >= recentOrdersSize() && vendorsToShow.size() == 0) {
                return FORM_ITEM;
            } else if (position >= recentOrdersSize() && position - recentOrdersSize() < vendorsToShow.size()) {
                return MAIN_ITEM;
            } else {
                return FORM_ITEM;
            }
        } else if (position < recentOrdersSize()) {
            return STATUS_ITEM;
        } else {
            if (vendorsCompleteCount() > 0) {
                return FORM_ITEM;
            } else {
                return NO_VENDORS_ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {
//        int noVenderToShowCount = ((recentOrdersSize() > 0 || vendorsCompleteCount() > 0) && (vendorsToShowCount() == 0)) ? 1 : 0;
        int formItem = (recentOrdersSize() > 0 || vendorsCompleteCount() > 0) ? 1 : 0;
        return offerStripSize() + (offerVendorsSize() > 0 ? 1 : 0)
                + recentOrdersSize() + vendorsToShowCount() + formItem;
    }

    private int recentOrdersSize() {
        return recentOrders == null ? 0 : recentOrders.size();
    }

    private int offerVendorsSize(){
        return bannerInfos == null || vendorsCompleteCount() == 0 ? 0 : bannerInfos.size();
    }

    private int offerStripSize(){
        return stripInfo == null || vendorsCompleteCount() == 0 || TextUtils.isEmpty(stripInfo.getText()) ? 0 : 1;
    }

    private int vendorsToShowCount() {
        return vendorsToShow == null ? 0 : vendorsToShow.size();
    }

    private int vendorsCompleteCount() {
        return vendorsComplete == null ? 0 : vendorsComplete.size();
    }

    public void setSearchApiHitOnce(boolean searchApiHitOnce){
        this.searchApiHitOnce = searchApiHitOnce;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlRoot;
        View vSep, viewOffer;
        ImageView imageViewRestaurantImage;
        TextView textViewRestaurantName, textViewMinimumOrder, textViewRestaurantCusines;
        TextView textViewRestaurantCloseTime, textViewAddressLine, textViewDelivery, tvRating,tvOffer;



        public ViewHolder(View itemView, Context context) {
            super(itemView);
            rlRoot = (RelativeLayout) itemView.findViewById(R.id.rlRoot);
            vSep = itemView.findViewById(R.id.vSep);
            imageViewRestaurantImage = (ImageView) itemView.findViewById(R.id.imageViewRestaurantImage);
            textViewRestaurantName = (TextView) itemView.findViewById(R.id.textViewRestaurantName); textViewRestaurantName.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
            textViewMinimumOrder = (TextView) itemView.findViewById(R.id.textViewMinimumOrder); textViewMinimumOrder.setTypeface(Fonts.mavenMedium(context));
            textViewRestaurantCusines = (TextView) itemView.findViewById(R.id.textViewRestaurantCusines); textViewRestaurantCusines.setTypeface(Fonts.mavenMedium(context));
            textViewRestaurantCloseTime = (TextView) itemView.findViewById(R.id.textViewRestaurantCloseTime);textViewRestaurantCloseTime.setTypeface(Fonts.mavenMedium(context));
            textViewAddressLine = (TextView) itemView.findViewById(R.id.textViewAddressLine);textViewAddressLine.setTypeface(Fonts.mavenMedium(context));
            textViewDelivery = (TextView) itemView.findViewById(R.id.textViewDelivery);textViewDelivery.setTypeface(Fonts.mavenMedium(context));
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvOffer = (TextView)itemView.findViewById(R.id.tv_offer);
            viewOffer = itemView.findViewById(R.id.tv_offer_view);
        }
    }


    private class ViewHolderRestaurantForm extends RecyclerView.ViewHolder {
        TextView tvCouldNotFind, tvRecommend;
        EditText etRestaurantName, etLocality, etTelephone;
        Button bSubmit;

        ViewHolderRestaurantForm(View itemView, Context context) {
            super(itemView);
            tvCouldNotFind = (TextView) itemView.findViewById(R.id.tvCouldNotFind);
            tvCouldNotFind.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
            tvRecommend = (TextView) itemView.findViewById(R.id.tvRecommend);
            tvRecommend.setTypeface(Fonts.mavenMedium(context));
            etRestaurantName = (EditText) itemView.findViewById(R.id.etRestaurantName);
            etRestaurantName.setTypeface(Fonts.mavenMedium(context));
            etLocality = (EditText) itemView.findViewById(R.id.etLocality);
            etLocality.setTypeface(Fonts.mavenMedium(context));
            etTelephone = (EditText) itemView.findViewById(R.id.etTelephone);
            etTelephone.setTypeface(Fonts.mavenMedium(context));
            bSubmit = (Button) itemView.findViewById(R.id.bSubmit);
            bSubmit.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

            etRestaurantName.addTextChangedListener(twRestaurantName);
            etLocality.addTextChangedListener(twLocality);
            etTelephone.addTextChangedListener(twTelephone);
            etTelephone.setOnEditorActionListener(onEditorActionListener);
        }
    }

    private class ViewNoVenderItem extends RecyclerView.ViewHolder {
        RelativeLayout rlLayoutNoVender;
        TextView textViewNoMenus;

        ViewNoVenderItem(View itemView, Context context) {
            super(itemView);
            rlLayoutNoVender = (RelativeLayout) itemView.findViewById(R.id.rlLayoutNoVender);
            textViewNoMenus = (TextView) itemView.findViewById(R.id.textViewNoMenus);
            textViewNoMenus.setTypeface(Fonts.mavenMedium(context));
        }
    }




    private class ViewTitleStatus extends RecyclerView.ViewHolder {

        public LinearLayout linear;
        RelativeLayout container, relativeStatusBar;
        TextView tvOrderId, tvOrderIdValue, tvDeliveryBefore, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        View lineStatus1, lineStatus2, lineStatus3;
        RelativeLayout rlRestaurantInfo, rlTrackViewOrder;
        TextView tvRestaurantName, tvOrderAmount, tvTrackOrder, tvViewOrder;
        RelativeLayout rlOrderNotDelivered;
        TextView tvOrderNotDelivered;

        ViewTitleStatus(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            relativeStatusBar = (RelativeLayout) itemView.findViewById(R.id.relativeStatusBar);
            tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
            tvOrderId.setTypeface(Fonts.mavenRegular(context));
            tvOrderIdValue = (TextView) itemView.findViewById(R.id.tvOrderIdValue);
            tvOrderIdValue.setTypeface(Fonts.mavenMedium(context));
            tvDeliveryBefore = (TextView) itemView.findViewById(R.id.tvDeliveryBefore);
            tvDeliveryBefore.setTypeface(Fonts.mavenRegular(context));
            tvDeliveryTime = (TextView) itemView.findViewById(R.id.tvDeliveryTime);
            tvDeliveryTime.setTypeface(Fonts.mavenMedium(context));
            tvStatus0 = (TextView) itemView.findViewById(R.id.tvStatus0);
            tvStatus0.setTypeface(Fonts.mavenRegular(context));
            tvStatus1 = (TextView) itemView.findViewById(R.id.tvStatus1);
            tvStatus1.setTypeface(Fonts.mavenRegular(context));
            tvStatus2 = (TextView) itemView.findViewById(R.id.tvStatus2);
            tvStatus2.setTypeface(Fonts.mavenRegular(context));
            tvStatus3 = (TextView) itemView.findViewById(R.id.tvStatus3);
            tvStatus3.setTypeface(Fonts.mavenRegular(context));
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
            tvTrackOrder = (TextView) itemView.findViewById(R.id.tvTrackOrder); tvTrackOrder.setTypeface(tvTrackOrder.getTypeface(), Typeface.BOLD);
            tvViewOrder = (TextView) itemView.findViewById(R.id.tvViewOrder); tvViewOrder.setTypeface(tvViewOrder.getTypeface(), Typeface.BOLD);
            rlOrderNotDelivered = (RelativeLayout) itemView.findViewById(R.id.rlOrderNotDelivered);
            tvOrderNotDelivered = (TextView) itemView.findViewById(R.id.tvOrderNotDelivered);
        }
    }


    public interface Callback {
        void onRestaurantSelected(int vendorId);
        void onBannerInfoDeepIndexClick(int deepIndex);
        void onNotify(int count);
    }


    public void removeHandler(){
        if(timerHandler != null){
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler = null;
        }
    }


    public void searchRestaurant(String s){
        if(s.length() == 0){
            queryMap.clear();
        } else if(searchText.length() > s.length()){
            queryMap.remove(searchText);
        }
        int oldLength = searchText.length();
        searchText = s;
        if(searchText.length() > 2) {
            searchRestaurantsAutoComplete(searchText);
        } else {
            if(oldLength > s.length() || oldLength == 0 || s.length() == 0){
                searchVendors("", null);
            } else if(vendorsFiltered.size() > 0 && vendorsToShow.size() == 0){
                searchVendors("", null);
            }
        }
    }

    private HashMap<String, List<Integer>> queryMap = new HashMap<>();
    private boolean refreshingAutoComplete = false;
    private void searchRestaurantsAutoComplete(final String searchText) {
        try {
            if(!refreshingAutoComplete) {
                if (MyApplication.getInstance().isOnline()) {
                    if(queryMap.containsKey(searchText)){
                        searchVendors(searchText, queryMap.get(searchText));
                    } else {
                        HashMap<String, String> params = new HashMap<>();
                        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                        params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                        params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                        params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                        params.put(Constants.INTERATED, "1");
                        params.put(Constants.KEY_SEARCH_TEXT, searchText);

                        refreshingAutoComplete = true;
                        activity.getTopBar().setPBSearchVisibility(View.VISIBLE);

                        new HomeUtil().putDefaultParams(params);
                        RestClient.getMenusApiService().fetchRestaurantViaSearch(params, new retrofit.Callback<RestaurantSearchResponse>() {
                            @Override
                            public void success(RestaurantSearchResponse productsResponse, Response response) {
//                                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                                activity.getTopBar().setPBSearchVisibility(View.GONE);
                                try {
                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                            searchVendors(searchText, productsResponse.getRestaurantIds());
                                            recyclerView.smoothScrollToPosition(0);
                                            if(productsResponse.getRestaurantIds().size() > 0) {
                                                queryMap.put(searchText, productsResponse.getRestaurantIds());
                                            }
                                            searchApiHitOnce = true;
                                        } else {
                                            searchVendors(searchText, null);
                                        }
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                refreshingAutoComplete = false;
                                recallSearch(searchText);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                activity.getTopBar().setPBSearchVisibility(View.GONE);
                                Log.e(TAG, "fetchRestaurantViaSearch error" + error.toString());
                                refreshingAutoComplete = false;
                                recallSearch(searchText);
                            }
                        });
                    }
                } else {
                    refreshingAutoComplete = true;
                    searchVendors(searchText, null);
                    refreshingAutoComplete = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            refreshingAutoComplete = false;
            recallSearch(searchText);
        }
    }

    private void recallSearch(String previousSearchText){
        if (!searchText.trim().equalsIgnoreCase(previousSearchText)) {
            searchRestaurantsAutoComplete(searchText);
        }
    }


    private String restaurantName = "", locality = "", telephone = "";

    private TextWatcher twRestaurantName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            restaurantName = s.toString().trim();
        }
    };

    private TextWatcher twLocality = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            locality = s.toString().trim();
        }
    };

    private TextWatcher twTelephone = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            telephone = s.toString().trim();
        }
    };


    private void apiRecommendRestaurant() {
        try {
            if(TextUtils.isEmpty(restaurantName)){
                Utils.showToast(activity, activity.getString(R.string.restaurant_name_is_neccessary));
                return;
            }
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, "");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_RESTAURANT_NAME, restaurantName);
                params.put(Constants.KEY_RESTAURANT_ADDRESS, locality);
                params.put(Constants.KEY_RESTAURANT_PHONE, telephone);

                new HomeUtil().putDefaultParams(params);
                RestClient.getMenusApiService().suggestRestaurant(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt productsResponse, Response response) {
                        try {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                    DialogPopup.alertPopupWithListener(activity,
                                            activity.getString(R.string.thanks_for_recommendation),
                                            productsResponse.getMessage(),
                                            activity.getString(R.string.ok),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    callback.onNotify(0);
                                                }
                                            }, false, true, true);
                                    restaurantName = ""; locality = ""; telephone = "";
                                    notifyDataSetChanged();
                                } else {
                                    DialogPopup.alertPopup(activity, "", productsResponse.getMessage());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "fetchRestaurantViaSearch error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            apiRecommendRestaurant();
            return false;
        }
    };


    private View.OnClickListener viewOrderOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int pos = (int) v.getTag();
                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                intent.putExtra(Constants.KEY_ORDER_ID, recentOrders.get(pos).getOrderId());
                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MENUS.getOrdinal());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener trackOrderOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            try {
                int pos = (int) v.getTag();
                RecentOrder order = recentOrders.get(pos);
                if(order.getShowLiveTracking() == 1 && order.getDeliveryId() > 0) {
                    Intent intent = new Intent(activity, RideTransactionsActivity.class);
                    intent.putExtra(Constants.KEY_ORDER_ID, recentOrders.get(pos).getOrderId());
                    intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MENUS.getOrdinal());
                    intent.putExtra(Constants.KEY_OPEN_LIVE_TRACKING, 1);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);


//                    Intent intent = new Intent(activity, TrackOrderActivity.class);
//                    intent.putExtra(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
//                    intent.putExtra(Constants.KEY_ORDER_ID, order.getOrderId());
//                    intent.putExtra(Constants.KEY_DELIVERY_ID, order.getDeliveryId());
//                    intent.putExtra(Constants.KEY_PICKUP_LATITUDE, order.getPickupLatitude());
//                    intent.putExtra(Constants.KEY_PICKUP_LONGITUDE, order.getPickupLongitude());
//                    intent.putExtra(Constants.KEY_DELIVERY_LATITUDE, order.getDeliveryLatitude());
//                    intent.putExtra(Constants.KEY_DELIVERY_LONGITUDE, order.getDeliveryLongitude());
//                    intent.putExtra(Constants.KEY_SHOW_DELIVERY_ROUTE, order.getShowDeliveryRoute());
//                    intent.putExtra(Constants.KEY_DRIVER_PHONE_NO, order.getDriverPhoneNo());
//                    activity.startActivity(intent);
//                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                } else {
                    Utils.showToast(activity, !TextUtils.isEmpty(order.getTrackDeliveryMessage()) ?
                            order.getTrackDeliveryMessage() : activity.getString(R.string.tracking_not_available_message));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public String getSearchText(){
        return searchText;
    }



    public class ViewHolderOffers extends RecyclerView.ViewHolder {
        public MenusVendorOffersAdapter menusVendorOffersAdapter;
        public ViewPager pagerMenusVendorOffers;
        public TabLayout tabDots;

        public ViewHolderOffers(View view) {
            super(view);
            pagerMenusVendorOffers = (ViewPager) view.findViewById(R.id.pagerMenusVendorOffers);
            tabDots = (TabLayout) view.findViewById(R.id.tabDots);
            menusVendorOffersAdapter = new MenusVendorOffersAdapter(activity, bannerInfos, new MenusVendorOffersAdapter.Callback() {
                @Override
                public void onBannerInfoClick(MenusResponse.BannerInfo bannerInfo) {
                    if(bannerInfo.getRestaurantId() == -1 && bannerInfo.getDeepIndex() != -1){
                        callback.onBannerInfoDeepIndexClick(bannerInfo.getDeepIndex());
                    } else if(bannerInfo.getRestaurantId() != -1 && bannerInfo.getDeepIndex() == -1){
                        callback.onRestaurantSelected(bannerInfo.getRestaurantId());
                    }
                }
            });
            pagerMenusVendorOffers.setAdapter(menusVendorOffersAdapter);
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

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        switch(viewClicked.getId()){
            case R.id.textViewMinOrder:
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


    private View.OnClickListener orderNotDeliveredListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int pos = (int) v.getTag();
                final RecentOrder order = recentOrders.get(pos);
                if(order.getOrderNotDeliveredYet() == 1) {
                    getDialogPopupTwoButtonCapsule().show(new DialogPopupTwoButtonCapsule.DialogCallback() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            // TODO: 09/06/17 remove this
//                            if(Data.isFuguChatEnabled()){
//                                FuguConfig.getInstance().showConversations(activity);
//                            } else {
//                                activity.startActivity(new Intent(activity, SupportActivity.class));
//                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

                                ShowPanelResponse.Item item = new ShowPanelResponse.Item();
                                item.setActionType(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal());
                                item.setSupportId(123);
                                item.setParentId(12);
                                item.setText("Need any help, type in comment box");
                                item.setViewType(ViewType.TEXT_BOX.getOrdinal());

                                new TransactionUtils().openItemInFragment(activity,
                                        activity.getRelativeLayoutContainer(),
                                        -1, "", activity.getString(R.string.how_can_we_help),
                                        item, "",
                                        order.getOrderId(), order.getExpectedDeliveryDate(),
                                        Config.getSupportNumber(activity), ProductType.MENUS.getOrdinal());

//                            }
                        }
                    }, order.getOrderNotDeliveredMessage(), activity.getString(R.string.cancel), activity.getString(R.string.get_help));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private DialogPopupTwoButtonCapsule dialogPopupTwoButtonCapsule;
    private DialogPopupTwoButtonCapsule getDialogPopupTwoButtonCapsule(){
        if(dialogPopupTwoButtonCapsule == null){
            dialogPopupTwoButtonCapsule = new DialogPopupTwoButtonCapsule(R.style.Feed_Popup_Theme, activity);
        }
        return dialogPopupTwoButtonCapsule;
    }

}
