package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
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

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusRestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<MenusResponse.Vendor> vendors, vendorsToShow;
    private Callback callback;
    private EditText editTextSearch;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;

    public MenusRestaurantAdapter(FreshActivity activity, ArrayList<MenusResponse.Vendor> vendors, Callback callback, EditText editTextSearch) {
        this.activity = activity;
        this.vendors = vendors;
        this.vendorsToShow = new ArrayList<>();
        this.vendorsToShow.addAll(vendors);
        this.callback = callback;
        this.editTextSearch = editTextSearch;

        this.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchVendors(s.toString());
            }
        });
    }

    private void searchVendors(String text){
        vendorsToShow.clear();
        text = text.toLowerCase();
        if(TextUtils.isEmpty(text)){
            vendorsToShow.addAll(vendors);
        } else {
            for(MenusResponse.Vendor vendor : vendors){
                if(vendor.getVendorName().toLowerCase().contains(text)
                        || vendor.getCuisines().toString().toLowerCase().contains(text)){
                    vendorsToShow.add(vendor);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setList(ArrayList<MenusResponse.Vendor> vendors) {
        this.vendors = vendors;
        this.vendorsToShow.clear();
        this.vendorsToShow.addAll(vendors);
        notifyDataSetChanged();
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
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ViewHolder) {
                ViewHolder mHolder = ((ViewHolder) holder);
                MenusResponse.Vendor vendor = vendorsToShow.get(position);

                mHolder.textViewRestaurantName.setText(vendor.getVendorName());
                mHolder.textViewClosed.setVisibility(vendor.getIsClosed() == 1 ? View.VISIBLE : View.GONE);
                mHolder.textViewRestaurantCusines.setText(vendor.getCuisines().toString().replace("[", "").replace("]", "").replace(", ", " . "));

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


                mHolder.textViewR1.setBackgroundResource(R.drawable.circle_grey_rupee);
                mHolder.textViewR2.setBackgroundResource(R.drawable.circle_grey_rupee);
                mHolder.textViewR3.setBackgroundResource(R.drawable.circle_grey_rupee);
                mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.text_color_light_less));
                switch(vendor.getPriceRange()){
                    case 2:
                        mHolder.textViewR3.setBackgroundResource(R.drawable.circle_green_rupee);
                        mHolder.textViewR3.setTextColor(activity.getResources().getColor(R.color.white));
                    case 1:
                        mHolder.textViewR2.setBackgroundResource(R.drawable.circle_green_rupee);
                        mHolder.textViewR2.setTextColor(activity.getResources().getColor(R.color.white));
                    case 0:
                        mHolder.textViewR1.setBackgroundResource(R.drawable.circle_green_rupee);
                        mHolder.textViewR1.setTextColor(activity.getResources().getColor(R.color.white));
                }

                if(vendor.getIsClosed() == 0){
                    mHolder.textViewAvailability.setText(activity.getString(R.string.mins_format, String.valueOf(vendor.getDeliveryTime())));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        if(position < vendorsToShow.size()) {
            return MAIN_ITEM;
        } else{
            return BLANK_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return vendorsToShow == null || vendorsToShow.size() == 0 ? 0 : vendorsToShow.size() + 1;
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


    public interface Callback {
        void onRestaurantSelected(int position, MenusResponse.Vendor vendor);

    }

}
