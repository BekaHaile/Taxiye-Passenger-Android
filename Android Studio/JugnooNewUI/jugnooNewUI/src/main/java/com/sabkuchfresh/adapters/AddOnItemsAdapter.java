package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class AddOnItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FlurryEventNames {

    private Context context;
    private List<SubItem> subItems;
    private MealAdapter.Callback callback;

    private static final int MAIN_ITEM = 0;
    private static final int HEADER = 1;

    public AddOnItemsAdapter(Context context, ArrayList<SubItem> subItems, MealAdapter.Callback callback) {
        this.context = context;
        this.subItems = subItems;
        this.callback = callback;
    }

    public synchronized void setResults(ArrayList<SubItem> subItems){
        this.subItems = subItems;
        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_addon_item, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new MainViewHolder(v, context);
        } else if (viewType == HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_addon_header, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 164);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewTitleHolder(v, context);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER;
        }
        return MAIN_ITEM;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            position--;
            MainViewHolder mHolder = ((MainViewHolder) holder);
            final SubItem subItem = subItems.get(position);

            mHolder.textViewItemName.setText(subItem.getSubItemName());
            mHolder.textViewItemUnit.setText(subItem.getBaseUnit());
            mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(subItem.getPrice())));

            mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
            if (subItem.getSubItemQuantitySelected() == 0) {
                if(subItem.getStock() > 0){
                    mHolder.mAddButton.setVisibility(View.VISIBLE);
                } else{
                    mHolder.mAddButton.setVisibility(View.GONE);
                }
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
            } else {
                mHolder.mAddButton.setVisibility(View.GONE);
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
            }


            mHolder.imageViewMinus.setTag(position);
            mHolder.imageViewPlus.setTag(position);
            mHolder.mAddButton.setTag(position);


            mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                        callback.onMinusClicked(pos, subItems.get(pos));

                        notifyDataSetChanged();
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
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()) +"");
                        }
                        callback.onPlusClicked(pos, subItems.get(pos));
                        notifyDataSetChanged();
                        int appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
                        if(appType == AppConstant.ApplicationType.FRESH){
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_ADD, null);
                        } else if(appType == AppConstant.ApplicationType.GROCERY){
                            MyApplication.getInstance().logEvent(FirebaseEvents.G_ADD, null);
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
                        int pos = (int) v.getTag();
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()) +"");
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
                    Picasso.with(context).load(subItem.getSubItemImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(mHolder.imageViewItemImage);
                } else {
                    mHolder.imageViewItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(holder instanceof ViewTitleHolder) {
            ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        }
	}

    @Override
    public int getItemCount() {
        return subItems == null ? 0 : subItems.size()+1;
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        private ImageView imageViewItemImage, imageViewMinus, imageViewPlus;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity;
        public Button mAddButton;
        public LinearLayout linearLayoutQuantitySelector;
        public MainViewHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            mAddButton = (Button) itemView.findViewById(R.id.add_button);

            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context));
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewTitleHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative;
        public TextView textViewCompleteMeal;
        public ViewTitleHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            textViewCompleteMeal = (TextView) itemView.findViewById(R.id.textViewCompleteMeal);
            textViewCompleteMeal.setTypeface(Fonts.mavenMedium(context));
        }
    }

}
