package com.sabkuchfresh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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

import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.RestaurantSearchResponse;
import com.sabkuchfresh.utils.CustomTypeFaceSpan;
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
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private String TAG = "Menus Screen";
    private ArrayList<RecentOrder> recentOrders;
    private ArrayList<String> possibleStatus;

    private FreshActivity activity;
    private ArrayList<MenusResponse.Vendor> vendorsComplete, vendorsFiltered, vendorsToShow;
    private Callback callback;
    private String searchText;

    private static final int MAIN_ITEM = 0;
    private static final int FORM_ITEM = 1;
    private static final int STATUS_ITEM = 3;
    private static final int NO_VENDORS_ITEM = 4;


    public MenusRestaurantAdapter(FreshActivity activity, ArrayList<MenusResponse.Vendor> vendors,
                                  ArrayList<RecentOrder> recentOrders, ArrayList<String> possibleStatus, Callback callback) {
        this.activity = activity;
        this.vendorsComplete = vendors;
        this.vendorsFiltered = new ArrayList<>();
        this.vendorsFiltered.addAll(vendors);
        this.vendorsToShow = new ArrayList<>();
        this.vendorsToShow.addAll(vendors);
        this.callback = callback;
        searchText = "";

        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        timerHandler.postDelayed(timerRunnable, 1000);
        restaurantName = ""; locality = ""; telephone = "";
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                applyFilter();
                if (timerHandler != null) {
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
            for(MenusResponse.Vendor vendor : vendorsFiltered) {
                if(searchedRestaurantIds == null) {
                    if (vendor.getName().toLowerCase().contains(text) || vendor.getCuisines().toString().toLowerCase().contains(text)) {
                        vendorsToShow.add(vendor);
                    }
                } else {
                    if(searchedRestaurantIds.contains(vendor.getRestaurantId())){
                        vendorsToShow.add(vendor);
                    }
                }
            }
        }
        notifyDataSetChanged();
        callback.onNotify(vendorsToShow.size());
    }

    public void setList(ArrayList<MenusResponse.Vendor> vendors) {
        this.vendorsComplete = vendors;
        this.vendorsFiltered.clear();
        this.vendorsFiltered.addAll(vendors);
        this.vendorsToShow.clear();
        this.vendorsToShow.addAll(vendors);
        applyFilter();
    }

    public void applyFilter(){
        vendorsFiltered.clear();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        for (MenusResponse.Vendor vendor : vendorsComplete) {
            boolean cuisineMatched = false, moMatched = false, dtMatched = false;
            for (String cuisine : activity.getCuisinesSelected()) {
                if (vendor.getCuisines().contains(cuisine)) {
                    cuisineMatched = true;
                    break;
                }
            }
            cuisineMatched = activity.getCuisinesSelected().size() > 0 ? cuisineMatched : true;

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


            if (DateOperations.getTimeDifferenceInHHmmss(vendor.getCloseIn(), vendor.getOpensAt()) >= 0) {
                String currentSystemTime = dateFormat.format(new Date()).toString();
                long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()), currentSystemTime);
                long minutes = ((timeDiff1 / (1000l * 60l)));
                if (minutes <= 0) {
                    vendor.setIsClosed(1);
                }
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
                }

                return point;
            }
        });

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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meals_order_status, parent, false);

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
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
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
                    statusHolder.tvOrderIdValue.setText(recentOrder.getOrderId().toString());
                    statusHolder.tvDeliveryTime.setText(recentOrder.getEndTime());
                    statusHolder.container.setTag(position);
                    statusHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int pos = (int) v.getTag();
                                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                                intent.putExtra(Constants.KEY_ORDER_ID, recentOrders.get(pos).getOrderId());
                                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MENUS.getOrdinal());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.ORDER_STATUS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (holder instanceof ViewHolder) {
                position = position - recentOrders.size();
                ViewHolder mHolder = ((ViewHolder) holder);
                MenusResponse.Vendor vendor = vendorsToShow.get(position);
                mHolder.textViewRestaurantName.setText(vendor.getName());

                mHolder.vSep.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String currentSystemTime = dateFormat.format(new Date()).toString();
                long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()), currentSystemTime);
                long minutes = ((timeDiff1 / (1000l * 60l)));

                if(vendor.getIsClosed() == 1 || vendor.getIsAvailable() == 0){
                    mHolder.textViewRestaurantCloseTime.setVisibility(View.VISIBLE);
                    mHolder.textViewRestaurantCloseTime.setText(R.string.closed);
                } else {
                    if (minutes > vendor.getBufferTime()) {
                        mHolder.textViewRestaurantCloseTime.setVisibility(View.GONE);
                    } else if (minutes <= vendor.getBufferTime() && minutes > 0) {
                        mHolder.textViewRestaurantCloseTime.setVisibility(View.VISIBLE);
                        mHolder.textViewRestaurantCloseTime.setText("Closes in " + minutes + " min");
                    } else {
                        mHolder.textViewRestaurantCloseTime.setVisibility(View.GONE);
                    }
                }

                mHolder.textViewMinimumOrder.setVisibility(((vendor.getMinimumOrderAmount() != null)) ? View.VISIBLE : View.GONE);
                if (vendor.getMinimumOrderAmount() != null) {
                    if (vendor.getMinimumOrderAmount() > 0) {
                        mHolder.textViewMinimumOrder.setText(activity.getString(R.string.minimum_order_rupee_format, Utils.getMoneyDecimalFormat().format(vendor.getMinimumOrderAmount())));
                    } else {
                        mHolder.textViewMinimumOrder.setText(activity.getString(R.string.no_minimum_order));
                    }
                }

                mHolder.textViewAddressLine.setVisibility(((vendor.getRestaurantAddress() != null)) ? View.VISIBLE : View.GONE);
                mHolder.textViewAddressLine.setText(vendor.getRestaurantAddress());

                if (vendor.getCuisines() != null && vendor.getCuisines().size() > 0) {
                    StringBuilder cuisines = new StringBuilder();
                    int maxSize = vendor.getCuisines().size() > 3 ? 3 : vendor.getCuisines().size();
                    for (int i = 0; i < maxSize; i++) {
                        String cuisine = vendor.getCuisines().get(i);
                        cuisines.append(cuisine);
                        if (i < maxSize - 1) {
                            cuisines.append(" ").append(activity.getString(R.string.bullet)).append(" ");
                        }
                    }
                    mHolder.textViewRestaurantCusines.setText(cuisines.toString());
                } else {
                    mHolder.textViewRestaurantCusines.setText("");
                }

                mHolder.rlRoot.setTag(position);
                mHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            callback.onRestaurantSelected(pos, vendorsToShow.get(pos));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                activity.setVendorDeliveryTimeToTextView(vendor, mHolder.textViewDelivery);
                setTextViewDrawableColor(mHolder.textViewDelivery, ContextCompat.getColor(activity, R.color.text_color));



                try {
                    if (!TextUtils.isEmpty(vendor.getImage())) {
                        float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                        Picasso.with(activity).load(vendor.getImage())
                                .placeholder(R.drawable.ic_fresh_item_placeholder)
                                .resize((int)(ratio * 150f), (int)(ratio * 150f))
                                .centerCrop()
                                .transform(new RoundBorderTransform((int)(ratio*6f), 0))
                                .error(R.drawable.ic_meal_place_holder)
                                .into(mHolder.imageViewRestaurantImage);
                    } else {
                        mHolder.imageViewRestaurantImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHolder.imageViewRestaurantImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                }

                mHolder.tvRating.setVisibility(View.GONE);
                if (vendor.getRating() != null) {
                    Typeface star = Typeface.createFromAsset(activity.getAssets(), "fonts/icomoon.ttf");
                    Typeface rating = Fonts.mavenMedium(activity);
                    String paddingStarToText = " ";
                    Spannable restaurantRating = new SpannableString(activity.getString(R.string.star_icon) + paddingStarToText + vendor.getRating());

                    //append strings and set different fonts to each subString
                    restaurantRating.setSpan(new CustomTypeFaceSpan("", star), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    restaurantRating.setSpan(new CustomTypeFaceSpan("", rating), 1, restaurantRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    int ratingColor;
                    if (vendor.getColorCode() != null && vendor.getColorCode().startsWith("#") && vendor.getColorCode().length() == 7)
                        ratingColor = Color.parseColor(vendor.getColorCode());
                    else
                        ratingColor = Color.parseColor("#8dd061"); //default Green Color

                    setTextViewBackgroundDrawableColor(mHolder.tvRating, ratingColor);
                    mHolder.tvRating.setText(restaurantRating);
                    mHolder.tvRating.setVisibility(View.VISIBLE);
                }

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTextViewBackgroundDrawableColor(TextView textView, int color) {
        if(textView.getBackground() != null){
            textView.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
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
                    statusHolder.lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 3) {
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
                if (status == 2) {
                    statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 2) {
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
                if (status == 1) {
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 1) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    statusHolder.ivStatus1.setLayoutParams(layoutParams);
                    statusHolder.ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
                    statusHolder.lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
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


    public boolean filterApplied() {
        return (activity.getCuisinesSelected().size() > 0
                || activity.getMoSelected() != MenusFilterFragment.MinOrder.NONE
                || activity.getDtSelected() != MenusFilterFragment.DeliveryTime.NONE
                || activity.getSortBySelected() != MenusFilterFragment.SortType.NONE
                || activity.getQuickFilterSelected().size() > 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (vendorsCompleteCount() > 0) {
            if (position >= 0 && position < recentOrders.size()) {
                return STATUS_ITEM;
            } else if (position >= recentOrders.size() && vendorsToShow.size() == 0) {
                return NO_VENDORS_ITEM;
            } else if (position >= recentOrders.size() && position - recentOrders.size() < vendorsToShow.size()) {
                return MAIN_ITEM;
            } else {
                if (vendorsToShowCount() > 0) {
                    return FORM_ITEM;
                } else {
                    return NO_VENDORS_ITEM;
                }
            }
        } else if (position < recentOrdersSize()) {
            return STATUS_ITEM;
        } else {
            if (vendorsToShowCount() > 0) {
                return FORM_ITEM;
            } else {
                return NO_VENDORS_ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {
        int noVenderToShowCount = ((recentOrdersSize() > 0 || vendorsCompleteCount() > 0) && (vendorsToShowCount() == 0)) ? 1 : 0;
        int formItem = (vendorsToShowCount() > 0) ? 1 : 0;
        return  recentOrdersSize() + vendorsToShowCount() + noVenderToShowCount + formItem;
    }

    private int recentOrdersSize() {
        return recentOrders == null ? 0 : recentOrders.size();
    }

    private int vendorsToShowCount() {
        return vendorsToShow == null ? 0 : vendorsToShow.size();
    }

    private int vendorsCompleteCount() {
        return vendorsComplete == null ? 0 : vendorsComplete.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlRoot;
        public View vSep;
        public ImageView imageViewRestaurantImage;
        public TextView textViewRestaurantName, textViewMinimumOrder, textViewRestaurantCusines;
        public TextView textViewRestaurantCloseTime, textViewAddressLine, textViewDelivery, tvRating;


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
        }
    }


    class ViewHolderRestaurantForm extends RecyclerView.ViewHolder {
        public TextView tvCouldNotFind, tvRecommend;
        public EditText etRestaurantName, etLocality, etTelephone;
        public Button bSubmit;

        public ViewHolderRestaurantForm(View itemView, Context context) {
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

    class ViewNoVenderItem extends RecyclerView.ViewHolder {
        public RelativeLayout rlLayoutNoVender;
        public TextView textViewNoMenus;

        public ViewNoVenderItem(View itemView, Context context) {
            super(itemView);
            rlLayoutNoVender = (RelativeLayout) itemView.findViewById(R.id.rlLayoutNoVender);
            textViewNoMenus = (TextView) itemView.findViewById(R.id.textViewNoMenus);
            textViewNoMenus.setTypeface(Fonts.mavenMedium(context));
        }
    }




    class ViewTitleStatus extends RecyclerView.ViewHolder {

        public LinearLayout linear;
        public RelativeLayout container, relativeStatusBar;
        public TextView tvOrderId, tvOrderIdValue, tvDeliveryBefore, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        public ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        public View lineStatus1, lineStatus2, lineStatus3;

        public ViewTitleStatus(View itemView, Context context) {
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
            lineStatus1 = (View) itemView.findViewById(R.id.lineStatus1);
            lineStatus2 = (View) itemView.findViewById(R.id.lineStatus2);
            lineStatus3 = (View) itemView.findViewById(R.id.lineStatus3);
        }
    }


    public interface Callback {
        void onRestaurantSelected(int position, MenusResponse.Vendor vendor);

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
    public void searchRestaurantsAutoComplete(final String searchText) {
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
                                activity.getTopBar().setPBSearchVisibility(View.GONE);
                                try {
                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                            searchVendors(searchText, productsResponse.getRestaurantIds());
                                            if(productsResponse.getRestaurantIds().size() > 0) {
                                                queryMap.put(searchText, productsResponse.getRestaurantIds());
                                            }
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


    public void apiRecommendRestaurant() {
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

                                                }
                                            }, false, true);
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


}
