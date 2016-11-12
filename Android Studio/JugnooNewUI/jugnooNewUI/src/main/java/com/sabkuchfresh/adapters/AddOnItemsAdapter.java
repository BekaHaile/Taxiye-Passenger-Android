package com.sabkuchfresh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class AddOnItemsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<SubItem> subItems;
    private MealAdapter.Callback callback;

    public AddOnItemsAdapter(Context context, ArrayList<SubItem> subItems, MealAdapter.Callback callback) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.subItems = subItems;
        this.callback = callback;
    }

    public synchronized void setResults(ArrayList<SubItem> subItems){
        this.subItems = subItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return subItems == null ? 0 : subItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_addon_item, null);
            holder = new MainViewHolder(convertView, context);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (MainViewHolder) convertView.getTag();
        }
        holder.id = position;
        onBindViewHolder(holder, position);

        return convertView;
    }

    public void onBindViewHolder(MainViewHolder holder, int position) {
        MainViewHolder mHolder = ((MainViewHolder) holder);
        final SubItem subItem = subItems.get(position);

        mHolder.textViewItemName.setText(subItem.getSubItemName());
        mHolder.textViewItemUnit.setText(subItem.getBaseUnit());
        mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                Utils.getMoneyDecimalFormat().format(subItem.getPrice())));

        mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
        if (subItem.getSubItemQuantitySelected() == 0) {
            if (subItem.getStock() > 0) {
                mHolder.mAddButton.setVisibility(View.VISIBLE);
                mHolder.textViewOutOfStock.setVisibility(View.GONE);
            } else {
                mHolder.mAddButton.setVisibility(View.GONE);
                mHolder.textViewOutOfStock.setVisibility(View.VISIBLE);
            }
            mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
        } else {
            mHolder.mAddButton.setVisibility(View.GONE);
            mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
            mHolder.textViewOutOfStock.setVisibility(View.GONE);
        }

        if(position == getCount()-1){
            mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
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
                    if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        callback.onPlusClicked(pos, subItems.get(pos));
                    } else {
                        Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()) + "");
                    }
                    notifyDataSetChanged();
                    int appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
                    if (appType == AppConstant.ApplicationType.FRESH) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.F_ADD, null);
                    } else if (appType == AppConstant.ApplicationType.GROCERY) {
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
                    if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        callback.onPlusClicked(pos, subItems.get(pos));
                    } else {
                        Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()) + "");
                    }

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
    }


    static class MainViewHolder {
        public int id;
        public RelativeLayout relative;
        private ImageView imageViewItemImage, imageViewMinus, imageViewPlus, imageViewSep;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity, textViewOutOfStock;
        public Button mAddButton;
        public LinearLayout linearLayoutQuantitySelector;
        public MainViewHolder(View itemView, Context context) {
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            mAddButton = (Button) itemView.findViewById(R.id.add_button);

            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context));
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            textViewOutOfStock = (TextView)itemView.findViewById(R.id.textViewOutOfStock); textViewOutOfStock.setTypeface(Fonts.mavenRegular(context));
        }
    }

}
