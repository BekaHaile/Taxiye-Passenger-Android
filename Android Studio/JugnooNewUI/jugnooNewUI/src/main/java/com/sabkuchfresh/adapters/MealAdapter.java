package com.sabkuchfresh.adapters;

import android.content.Context;
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
import android.widget.Toast;

import com.sabkuchfresh.R;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.DateOperations;
import com.sabkuchfresh.utils.Fonts;

import java.util.ArrayList;

/**
 * Created by gurmail on 15/07/16.
 */
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<SubItem> subItems;
    private Callback callback;

    private int listType = 0;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;

    public MealAdapter(FreshActivity activity, ArrayList<SubItem> subItems, Callback callback) {
        this.activity = activity;
        this.subItems = subItems;
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
        if (viewType == MAIN_ITEM) {
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
            if (holder instanceof ViewHolderSlot) {

                ViewHolderSlot mHolder = ((ViewHolderSlot) holder);
                final SubItem subItem = subItems.get(position);

                mHolder.textViewTitle.setText(subItem.getSubItemName());
                mHolder.textPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                        subItem.getPrice()));
                mHolder.textViewdetails.setText(subItem.getItemLargeDesc());
//                mHolder.deliveryTime.setText(DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()) + "-"
//                        + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd()));

                if (subItem.isExpanded()) {
                    mHolder.textViewdetails.setVisibility(View.VISIBLE);
                } else {
                    mHolder.textViewdetails.setVisibility(View.GONE);
                }

                mHolder.linear.setTag(position);
                mHolder.linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            if (subItem.isExpanded()) {
                                subItem.setExpanded(false);
                            } else {
                                subItem.setExpanded(true);
                            }
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (subItem.getIsVeg() == 1) {
                    mHolder.foodType.setBackgroundResource(R.drawable.veg);
                } else {
                    mHolder.foodType.setBackgroundResource(R.drawable.nonveg);
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
                    mHolder.cartLayout.setVisibility(View.GONE);
                    mHolder.deliveryTime.setText("Order Starts at\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()));
                } else {
                    if(subItem.getStock() == 0) {
                        mHolder.imageClosed.setVisibility(View.VISIBLE);
                        mHolder.cartLayout.setVisibility(View.GONE);
                    } else {
                        mHolder.imageClosed.setVisibility(View.GONE);
                        mHolder.cartLayout.setVisibility(View.VISIBLE);
                    }

                    mHolder.deliveryTime.setText("Open Now\n"+ DateOperations.convertDayTimeAPViaFormat(subItem.getOrderStart()) + "-"
                            + DateOperations.convertDayTimeAPViaFormat(subItem.getOrderEnd()));
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
                            } else {
                                Toast.makeText(activity, "Can't order more then " + subItems.get(pos).getStock() + " units", Toast.LENGTH_SHORT).show();
                            }
                            callback.onPlusClicked(pos, subItems.get(pos));
                            notifyDataSetChanged();
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
                                Toast.makeText(activity, "Can't order more then " + subItems.get(pos).getStock() + " units", Toast.LENGTH_SHORT).show();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == subItems.size()) {
            return BLANK_ITEM;
        }
        return MAIN_ITEM;
//        return slots.get(position).getSlotViewType().getOrdinal();
    }

    @Override
    public int getItemCount() {
        if(listType == AppConstant.ListType.HOME)
            return subItems == null ? 0 : subItems.size()+1;
        else
            return subItems == null ? 0 : subItems.size();
    }

//    @Override
//    public int getItemCount() {
//        return subItems == null ? 0 : subItems.size();
//    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        RelativeLayout belowLayout;
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
            textViewdetails.setTypeface(Fonts.mavenRegular(context));
            deliveryTime = (TextView) itemView.findViewById(R.id.delivery_time);
            deliveryTime.setTypeface(Fonts.mavenRegular(context));
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


    public interface Callback {
        void onSlotSelected(int position, SubItem slot);

        void onPlusClicked(int position, SubItem subItem);

        void onMinusClicked(int position, SubItem subItem);
    }

}
