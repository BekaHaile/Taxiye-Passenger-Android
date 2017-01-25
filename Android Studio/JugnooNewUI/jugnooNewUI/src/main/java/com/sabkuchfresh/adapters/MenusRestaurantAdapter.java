package com.sabkuchfresh.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private String TAG = "Menus Screen";
    private ArrayList<RecentOrder> recentOrders;
    private ArrayList<String> possibleStatus;
    private int listType = 0;

    private FreshActivity activity;
    private ArrayList<MenusResponse.Vendor> vendorsComplete, vendors, vendorsToShow;
    private Callback callback;
    private String searchText;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int SEARCH_FILTER_ITEM = 2;
    private static final int STATUS_ITEM = 3;
    private static final int NO_VENDORS_ITEM = 4;


    public MenusRestaurantAdapter(FreshActivity activity, ArrayList<MenusResponse.Vendor> vendors,ArrayList<RecentOrder> recentOrders,ArrayList<String> possibleStatus, Callback callback) {
        this.activity = activity;
        this.vendorsComplete = vendors;
        this.vendors = new ArrayList<>();
        this.vendors.addAll(vendors);
        this.vendorsToShow = new ArrayList<>();
        this.vendorsToShow.addAll(vendors);
        this.callback = callback;
        searchText = "";

        this.recentOrders = recentOrders;
        this.possibleStatus = possibleStatus;
        timerHandler.postDelayed(timerRunnable, 1000);
    }
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
                @Override
                public void run() {
                    applyFilter();
                    timerHandler.postDelayed(this, 60000); //run every minute
                    }
            };

    private void searchVendors(String text){
        vendorsToShow.clear();
        text = text.toLowerCase();
        if(TextUtils.isEmpty(text)){
            vendorsToShow.addAll(vendors);
        } else {
            for(MenusResponse.Vendor vendor : vendors)
            {
                if(vendor.getName().toLowerCase().contains(text) || vendor.getCuisines().toString().toLowerCase().contains(text))
                {
                    vendorsToShow.add(vendor);
                }
            }
        }
        notifyDataSetChanged();
		callback.onNotify(vendorsToShow.size());
    }

    public void setList(ArrayList<MenusResponse.Vendor> vendors) {
        this.vendorsComplete = vendors;
        this.vendors.clear();
        this.vendors.addAll(vendors);
        this.vendorsToShow.clear();
        this.vendorsToShow.addAll(vendors);
        applyFilter();
    }

    public void applyFilter(){
        vendors.clear();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        for(MenusResponse.Vendor vendor : vendorsComplete){
            boolean cuisineMatched = false, moMatched = false, dtMatched = false;
            for(String cuisine : activity.getCuisinesSelected()){
                if(vendor.getCuisines().contains(cuisine)){
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
            for(String filter : activity.getQuickFilterSelected())
            {
                if((filter.equalsIgnoreCase(Constants.ACCEPTONLINE) && vendor.getApplicablePaymentMode().equals(ApplicablePaymentMode.CASH.getOrdinal()))
                        || (filter.equalsIgnoreCase(Constants.OFFERSDISCOUNT) && vendor.getOffersDiscounts().equals(0))
                        || (filter.equalsIgnoreCase(Constants.PUREVEGETARIAN) && vendor.getPureVegetarian().equals(0))
                        || (filter.equalsIgnoreCase(Constants.FREEDELIVERY) && vendor.getFreeDelivery().equals(0))){
                    qfMatched = false;
                    break;
                }
            }


            if(DateOperations.getTimeDifferenceInHHmmss(vendor.getCloseIn(), vendor.getOpensAt()) >= 0){
                String currentSystemTime = dateFormat.format(new Date()).toString();
                long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()) , currentSystemTime);
                long minutes =  ((timeDiff1 / (1000l*60l)));
                if(minutes <= 0){
                    vendor.setIsClosed(1);
                }
            }




            if(cuisineMatched && moMatched && dtMatched && qfMatched){
                vendors.add(vendor);
            }
        }

        Collections.sort(vendors, new Comparator<MenusResponse.Vendor>() {
            @Override
            public int compare(MenusResponse.Vendor lhs, MenusResponse.Vendor rhs) {
                int point = 0;


                if(lhs.getIsClosed()==0 && rhs.getIsClosed()!=0)
                {
                    point = -(rhs.getIsClosed() - lhs.getIsClosed());
                }
               else if(lhs.getIsClosed()!=0 && rhs.getIsClosed()==0)
                {
                    point = -(rhs.getIsClosed() - lhs.getIsClosed());
                }
               else if(lhs.getIsAvailable()==0 && rhs.getIsAvailable()!=0)
                {
                    point = rhs.getIsAvailable() - lhs.getIsAvailable();
                }
                else if(lhs.getIsAvailable()!=0 && rhs.getIsAvailable()==0)
                {
                    point = rhs.getIsAvailable() - lhs.getIsAvailable();
                }

//                else if (activity.getSortBySelected() == MenusFilterFragment.SortType.ONLINEPAYMENTACCEPTED)
//                {
//                    point = rhs.getApplicablePaymentMode() - lhs.getApplicablePaymentMode();
//                }
                else if (activity.getSortBySelected() == MenusFilterFragment.SortType.POPULARITY)
                {
                    point = rhs.getPopularity() - lhs.getPopularity();
                }
                else if (activity.getSortBySelected() == MenusFilterFragment.SortType.DISTANCE)
                {
                    point = -(int)(rhs.getDistance() - lhs.getDistance());
                }
                else if (activity.getSortBySelected() == MenusFilterFragment.SortType.PRICE)
                {
                    point = lhs.getPriceRange() - rhs.getPriceRange();
                }

                return point;
            }
        });

        searchVendors(searchText);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewTitleHolder(v);
        } else if (viewType == SEARCH_FILTER_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_search_filter, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolderFilter(v, activity);
        }
        else if (viewType == STATUS_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meals_order_status, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewTitleStatus(v, activity);
        }
        else if (viewType == NO_VENDORS_ITEM) {
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
                    position = vendorsComplete.size() > 0 ? position - 1 : position;
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

            }
            else if (holder instanceof ViewHolder) {
                position = position - recentOrders.size();
                position = vendorsComplete.size() > 0 ? position : position;
                ViewHolder mHolder = ((ViewHolder) holder);
                MenusResponse.Vendor vendor = vendorsToShow.get(position);
                mHolder.textViewRestaurantName.setText(vendor.getName());

                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String currentSystemTime = dateFormat.format(new Date()).toString();
                long timeDiff1 = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()) , currentSystemTime);
                long minutes =  ((timeDiff1 / (1000l*60l)));

    //            long timeDiff = DateOperations.getTimeDifferenceInHHMM(DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn()) , formattedDate) / (60 * 1000);


                Log.e(TAG, " position= "+position+", get close time  "+DateOperations.convertDayTimeAPViaFormat(vendor.getCloseIn())+
                        " minute= "+minutes+" restaurant name= "+vendor.getName());


                if(minutes > vendor.getBufferTime()){
                mHolder.relativeLayoutRestaurantCloseTime.setVisibility(View.GONE);
                } else if(minutes <= vendor.getBufferTime() && minutes > 0) {
                mHolder.relativeLayoutRestaurantCloseTime.setVisibility(View.VISIBLE);
                mHolder.textViewRestaurantCloseTime.setText("Closes in "+minutes+" min");
                } else {
                mHolder.relativeLayoutRestaurantCloseTime.setVisibility(View.GONE);
                }
                mHolder.textViewClosed.setVisibility(((vendor.getIsClosed() == 1)||(vendor.getIsAvailable()==0)) ? View.VISIBLE : View.GONE);

                mHolder.textViewDelivery.setVisibility(((vendor.getMinimumOrderAmount() != null)) ? View.VISIBLE : View.GONE);
                if(vendor.getMinimumOrderAmount() != null){
                    if(vendor.getMinimumOrderAmount() > 0){
                        mHolder.textViewDelivery.setText(activity.getString(R.string.minimum_order_rupee_format, Utils.getMoneyDecimalFormat().format(vendor.getMinimumOrderAmount())));
                    } else {
                        mHolder.textViewDelivery.setText(activity.getString(R.string.no_minimum_order));
                    }
                }

                mHolder.imageViewAddressLine.setVisibility(((vendor.getRestaurantAddress() != null)) ? View.VISIBLE : View.GONE);
                mHolder.textViewAddressLine.setVisibility(((vendor.getRestaurantAddress() != null)) ? View.VISIBLE : View.GONE);
                mHolder.textViewAddressLine.setText(vendor.getRestaurantAddress());




                /*mHolder.textViewClosed.setVisibility(((vendor.getIsClosed() == 1)||(vendor.getIsAvailable()==0)) ? View.VISIBLE : View.GONE);
                mHolder.relativeLayoutRestaurantCloseTime.setVisibility(((vendor.getInClose() != null)) ? View.VISIBLE : View.GONE);
                mHolder.textViewRestaurantCloseTime.setText(vendor.getInClose());

                mHolder.textViewDelivery.setVisibility(((vendor.getMinimumOrderAmount() != null)) ? View.VISIBLE : View.GONE);
                mHolder.textViewDelivery.setText(activity.getString(R.string.minimum_order_rupee_format, Utils.getMoneyDecimalFormat().format(vendor.getMinimumOrderAmount())));
*/
                mHolder.textViewAddressLine.setText(vendor.getRestaurantAddress());

                if(vendor.getCuisines() != null && vendor.getCuisines().size() > 0){
                    StringBuilder cuisines = new StringBuilder();
                    int maxSize = vendor.getCuisines().size() > 3 ? 3 : vendor.getCuisines().size();
                    for(int i=0; i<maxSize; i++){
                        String cuisine = vendor.getCuisines().get(i);
                        cuisines.append(cuisine);
                        if(i < maxSize-1){
                            cuisines.append(" ").append(activity.getString(R.string.bullet)).append(" ");
                        }
                    }
                    mHolder.textViewRestaurantCusines.setText(cuisines.toString());
                } else {
                    mHolder.textViewRestaurantCusines.setText("");
                }

                mHolder.linearRoot.setTag(position);
                mHolder.linearRoot.setOnClickListener(new View.OnClickListener() {
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
               /* mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                switch(vendor.getPriceRange()){
                    case 2:
                        mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                    case 1:
                        mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                    case 0:
                        mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                }*/

                if(vendor.getIsClosed() == 0){
                    String deliveryTime = String.valueOf(vendor.getDeliveryTime());
                    if(vendor.getMinDeliveryTime() != null){
                        deliveryTime = String.valueOf(vendor.getMinDeliveryTime()) + "-" + deliveryTime;
                    }
                    mHolder.textViewMinimumOrder.setText(activity.getString(R.string.delivers_in_format, deliveryTime));
                } else {
                    mHolder.textViewMinimumOrder.setText(activity.getString(R.string.opens_at_format,
                            String.valueOf(DateOperations.convertDayTimeAPViaFormat(vendor.getOpensAt()))));
                }

                try {
                    if (!TextUtils.isEmpty(vendor.getImage())) {
                        Picasso.with(activity).load(vendor.getImage())
                                .placeholder(R.drawable.ic_meal_place_holder)
                                .fit()
                                .centerCrop()
                                .error(R.drawable.ic_meal_place_holder)
                                .into(mHolder.imageViewRestaurantImage);
                    } else {
                        mHolder.imageViewRestaurantImage.setImageResource(R.drawable.ic_meal_place_holder);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (holder instanceof ViewTitleHolder) {
                ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
                titleholder.relative.setVisibility(View.VISIBLE);
                titleholder.relative.setBackgroundColor(activity.getResources().getColor(R.color.white));
            } else if (holder instanceof ViewHolderFilter) {
                ViewHolderFilter holderFilter = ((ViewHolderFilter) holder);
                holderFilter.cardViewFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holderFilter.editTextSearch.removeTextChangedListener(textWatcher);
                holderFilter.editTextSearch.addTextChangedListener(textWatcher);
                holderFilter.editTextSearch.requestFocus();
                holderFilter.imageViewFilterApplied.setVisibility(filterApplied() ? View.VISIBLE : View.GONE);
            } else if (holder instanceof ViewNoVenderItem){
                ViewNoVenderItem holderNoVenderItem = (ViewNoVenderItem) holder;
                if(vendorsComplete.size() == 0) {
                    holderNoVenderItem.textViewNoMenus.setText(R.string.no_menus_available_your_location);
                } else if(vendors.size() == 0){
                    holderNoVenderItem.textViewNoMenus.setText(R.string.no_menus_available_with_these_filters);
                } else if(vendorsToShow.size()==0) {
                    holderNoVenderItem.textViewNoMenus.setText(R.string.no_menus_available_with_this_name);
                }
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


    private boolean filterApplied(){
        return (activity.getCuisinesSelected().size() > 0
                || activity.getMoSelected() != MenusFilterFragment.MinOrder.NONE
                || activity.getDtSelected() != MenusFilterFragment.DeliveryTime.NONE
                || activity.getSortBySelected() != MenusFilterFragment.SortType.NONE
                || activity.getQuickFilterSelected().size()>0);
    }

    @Override
    public int getItemViewType(int position) {
        int filterTabPos = 0;
        if(vendorsCompleteCount()>0)
        {
//            if(position==0) {
//                Log.e(TAG, position+">"+SEARCH_FILTER_ITEM);
//                return SEARCH_FILTER_ITEM;
//            }
//            else
            if(position>=filterTabPos && position-filterTabPos < recentOrdersSize())
            {
                Log.e(TAG, position+">"+STATUS_ITEM);
                return STATUS_ITEM;
            }
            else if(position >= filterTabPos+recentOrders.size() && vendorsToShow.size() == 0)
            {
                Log.v(TAG,"no vender item  "+ position+">"+MAIN_ITEM);
                return NO_VENDORS_ITEM;
            }
            else if(position >= filterTabPos+recentOrders.size() && position-(filterTabPos+recentOrders.size()) < vendorsToShow.size())
            {
                Log.e(TAG, position+">"+MAIN_ITEM);
                return MAIN_ITEM;
            }
            else
            {
                if(vendorsToShowCount() > 0){
                    return BLANK_ITEM;
                } else {
                    return NO_VENDORS_ITEM;
                }
            }
        } else if(position<recentOrdersSize()) {
            Log.e(TAG, position+">"+STATUS_ITEM);
            return STATUS_ITEM;
        }
        else
        {
            Log.e(TAG, position+">"+BLANK_ITEM);
            if(vendorsToShowCount() > 0){
                return BLANK_ITEM;
            } else {
                return NO_VENDORS_ITEM;
            }
        }

    }

    @Override
    public int getItemCount() {
        int noVenderToShowCount = ((recentOrdersSize() > 0 || vendorsCompleteCount() > 0) && (vendorsToShowCount() == 0)) ? 1 : 0;
//        int filterCount = (vendorsCompleteCount() > 0) ? 1 : 0;
        int filterCount = 0;
        int blankItem = (vendorsToShowCount() > 0) ? 1 : 0;

        return  recentOrdersSize() + vendorsToShowCount() + noVenderToShowCount + filterCount + blankItem;
    }

    private int recentOrdersSize(){
        return recentOrders == null ? 0 : recentOrders.size();
    }
    private int vendorsToShowCount(){
        return vendorsToShow == null ? 0 : vendorsToShow.size();
    }
    private int vendorsCompleteCount(){
        return vendorsComplete == null ? 0 : vendorsComplete.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearRoot;
        public ImageView imageViewRestaurantImage, imageViewAddressLine;
        public TextView textViewClosed, textViewRestaurantName, textViewMinimumOrder, textViewRestaurantCusines,
                textViewR1, textViewR2, textViewR3;

        public RelativeLayout relativeLayoutRestaurantCloseTime;
        public TextView textViewRestaurantCloseTime, textViewAddressLine, textViewDelivery;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            linearRoot = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            imageViewRestaurantImage = (ImageView) itemView.findViewById(R.id.imageViewRestaurantImage);
            imageViewAddressLine = (ImageView) itemView.findViewById(R.id.imageViewAddressLine);
            textViewClosed = (TextView) itemView.findViewById(R.id.textViewClosed); textViewClosed.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewRestaurantName = (TextView) itemView.findViewById(R.id.textViewRestaurantName); textViewRestaurantName.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewMinimumOrder = (TextView) itemView.findViewById(R.id.textViewMinimumOrder); textViewMinimumOrder.setTypeface(Fonts.mavenRegular(context));
            textViewRestaurantCusines = (TextView) itemView.findViewById(R.id.textViewRestaurantCusines); textViewRestaurantCusines.setTypeface(Fonts.mavenMedium(context));

            relativeLayoutRestaurantCloseTime = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutRestaurantCloseTime);
            textViewRestaurantCloseTime = (TextView) itemView.findViewById(R.id.textViewRestaurantCloseTime);textViewRestaurantCloseTime.setTypeface(Fonts.mavenMedium(context));
            textViewAddressLine = (TextView) itemView.findViewById(R.id.textViewAddressLine);textViewAddressLine.setTypeface(Fonts.mavenMedium(context));
            textViewDelivery = (TextView) itemView.findViewById(R.id.textViewDelivery);textViewDelivery.setTypeface(Fonts.mavenRegular(context));


            /* textViewR1 = (TextView) itemView.findViewById(R.id.textViewR1); textViewR1.setTypeface(Fonts.mavenRegular(context));
            textViewR2 = (TextView) itemView.findViewById(R.id.textViewR2); textViewR2.setTypeface(Fonts.mavenRegular(context));
            textViewR3 = (TextView) itemView.findViewById(R.id.textViewR3); textViewR3.setTypeface(Fonts.mavenRegular(context));*/

        }
    }

    static class ViewTitleHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ViewTitleHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    static class ViewNoVenderItem extends RecyclerView.ViewHolder {
        public RelativeLayout rlLayoutNoVender;
        public TextView textViewNoMenus;
        public ViewNoVenderItem(View itemView,Context context)
        {
            super(itemView);
            rlLayoutNoVender = (RelativeLayout) itemView.findViewById(R.id.rlLayoutNoVender);
            textViewNoMenus  = (TextView) itemView.findViewById(R.id.textViewNoMenus);
            textViewNoMenus.setTypeface(Fonts.mavenMedium(context));
        }
    }



    static class ViewHolderFilter extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayoutSearchFilter;
        private EditText editTextSearch;
        private CardView cardViewFilter;
        private ImageView imageViewFilterApplied;
        public ViewHolderFilter(View itemView, Context context) {
            super(itemView);
            relativeLayoutSearchFilter = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutSearchFilter);
            editTextSearch = (EditText) itemView.findViewById(R.id.editTextSearch); editTextSearch.setTypeface(Fonts.mavenMedium(context));
            cardViewFilter = (CardView) itemView.findViewById(R.id.cardViewFilter);
            imageViewFilterApplied = (ImageView) itemView.findViewById(R.id.imageViewFilterApplied);
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
        void onRestaurantSelected(int position, MenusResponse.Vendor vendor);
		void onNotify(int count);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            searchText = s.toString();
            searchVendors(s.toString());
        }
    };

    public void searchVendorsFromTopBar(String s){
        searchText = s;
        searchVendors(s);
    }

}
