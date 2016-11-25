package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
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

import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<MenusResponse.Vendor> vendorsComplete, vendors, vendorsToShow;
    private Callback callback;
    private String searchText;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int SEARCH_FILTER_ITEM = 2;

    public MenusRestaurantAdapter(FreshActivity activity, ArrayList<MenusResponse.Vendor> vendors, Callback callback) {
        this.activity = activity;
        this.vendorsComplete = vendors;
        this.vendors = new ArrayList<>();
        this.vendors.addAll(vendors);
        this.vendorsToShow = new ArrayList<>();
        this.vendorsToShow.addAll(vendors);
        this.callback = callback;
        searchText = "";
    }

    private void searchVendors(String text){
        vendorsToShow.clear();
        text = text.toLowerCase();
        if(TextUtils.isEmpty(text)){
            vendorsToShow.addAll(vendors);
        } else {
            for(MenusResponse.Vendor vendor : vendors){
                if(vendor.getName().toLowerCase().contains(text)
                        || vendor.getCuisines().toString().toLowerCase().contains(text)){
                    vendorsToShow.add(vendor);
                }
            }
        }
        notifyDataSetChanged();
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
        for(MenusResponse.Vendor vendor : vendorsComplete){
            boolean cuisineMatched = true, moMatched = false, dtMatched = false;
            for(String cuisine : activity.getCuisinesSelected()){
                if(!vendor.getCuisines().contains(cuisine)){
                    cuisineMatched = false;
                    break;
                }
            }
            moMatched = activity.getMoSelected() == MenusFilterFragment.MinOrder.NONE
                    || vendor.getMinimumOrderAmount() <= activity.getMoSelected().getOrdinal();

            dtMatched = activity.getDtSelected() == MenusFilterFragment.DeliveryTime.NONE
                    || vendor.getMinDeliveryTime() <= activity.getDtSelected().getOrdinal();

            if(cuisineMatched && moMatched && dtMatched){
                vendors.add(vendor);
            }
        }

        Collections.sort(vendors, new Comparator<MenusResponse.Vendor>() {
            @Override
            public int compare(MenusResponse.Vendor lhs, MenusResponse.Vendor rhs) {
                if(activity.getSortBySelected() != MenusFilterFragment.SortType.NONE){
                    if(activity.getSortBySelected() == MenusFilterFragment.SortType.POPULARITY){
                        return lhs.getPopularity() - rhs.getPopularity();
                    } else if(activity.getSortBySelected() == MenusFilterFragment.SortType.DISTANCE){
                        return -(lhs.getDistance() - rhs.getDistance());
                    } else if(activity.getSortBySelected() == MenusFilterFragment.SortType.PRICE){
                        return lhs.getPriceRange() - rhs.getPriceRange();
                    }
                }
                return 0;
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
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ViewHolder) {
                position = vendors.size() > 0 ? position-1 : position;
                ViewHolder mHolder = ((ViewHolder) holder);
                MenusResponse.Vendor vendor = vendorsToShow.get(position);

                mHolder.textViewRestaurantName.setText(vendor.getName());
                mHolder.textViewClosed.setVisibility(vendor.getIsClosed() == 1 ? View.VISIBLE : View.GONE);
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


                mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                switch(vendor.getPriceRange()){
                    case 2:
                        mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                    case 1:
                        mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                    case 0:
                        mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.green_rupee));
                }

                if(vendor.getIsClosed() == 0){
                    String deliveryTime = String.valueOf(vendor.getDeliveryTime());
                    if(vendor.getMinDeliveryTime() != null){
                        deliveryTime = String.valueOf(vendor.getMinDeliveryTime()) + "-" + deliveryTime;
                    }
                    mHolder.textViewAvailability.setText(activity.getString(R.string.mins_format, deliveryTime));
                } else {
                    mHolder.textViewAvailability.setText(activity.getString(R.string.opens_at_format,
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
                titleholder.relative.setBackgroundColor(activity.getResources().getColor(R.color.menu_item_selector_color));
            } else if (holder instanceof ViewHolderFilter) {
                ViewHolderFilter holderFilter = ((ViewHolderFilter) holder);
                holderFilter.cardViewFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.getTransactionUtils().openMenusFilterFragment(activity, activity.getRelativeLayoutContainer());
                    }
                });
                holderFilter.editTextSearch.removeTextChangedListener(textWatcher);
                holderFilter.editTextSearch.addTextChangedListener(textWatcher);
                holderFilter.editTextSearch.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        if(position == 0 && vendors.size() > 0){
            return SEARCH_FILTER_ITEM;
        } else if((vendors.size() > 0 ? position-1 : position) < vendorsToShow.size()) {
            return MAIN_ITEM;
        } else {
            return BLANK_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return (vendorsToShow == null) ? 0 : vendorsToShow.size() + (vendors.size() > 0 ? 2 : 0);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearRoot;
        public ImageView imageViewRestaurantImage;
        public TextView textViewClosed, textViewRestaurantName, textViewAvailability, textViewRestaurantCusines,
                textViewR1, textViewR2, textViewR3;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            linearRoot = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            imageViewRestaurantImage = (ImageView) itemView.findViewById(R.id.imageViewRestaurantImage);

            textViewClosed = (TextView) itemView.findViewById(R.id.textViewClosed); textViewClosed.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewRestaurantName = (TextView) itemView.findViewById(R.id.textViewRestaurantName); textViewRestaurantName.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewAvailability = (TextView) itemView.findViewById(R.id.textViewAvailability); textViewAvailability.setTypeface(Fonts.mavenMedium(context));
            textViewRestaurantCusines = (TextView) itemView.findViewById(R.id.textViewRestaurantCusines); textViewRestaurantCusines.setTypeface(Fonts.mavenMedium(context));
            textViewR1 = (TextView) itemView.findViewById(R.id.textViewR1); textViewR1.setTypeface(Fonts.mavenRegular(context));
            textViewR2 = (TextView) itemView.findViewById(R.id.textViewR2); textViewR2.setTypeface(Fonts.mavenRegular(context));
            textViewR3 = (TextView) itemView.findViewById(R.id.textViewR3); textViewR3.setTypeface(Fonts.mavenRegular(context));

        }
    }

    static class ViewTitleHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ViewTitleHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    static class ViewHolderFilter extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayoutSearchFilter;
        private EditText editTextSearch;
        private CardView cardViewFilter;
        public ViewHolderFilter(View itemView, Context context) {
            super(itemView);
            relativeLayoutSearchFilter = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutSearchFilter);
            editTextSearch = (EditText) itemView.findViewById(R.id.editTextSearch); editTextSearch.setTypeface(Fonts.mavenMedium(context));
            cardViewFilter = (CardView) itemView.findViewById(R.id.cardViewFilter);
        }
    }


    public interface Callback {
        void onRestaurantSelected(int position, MenusResponse.Vendor vendor);
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

}
