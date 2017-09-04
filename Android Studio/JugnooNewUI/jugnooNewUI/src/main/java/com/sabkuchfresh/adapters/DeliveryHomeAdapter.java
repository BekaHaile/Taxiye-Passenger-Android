package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 1/20/17.
 */

public class DeliveryHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

    private Context context;
    private List<Object> dataToDisplay;
    private Callback callback;
    private static final int VIEW_TITLE_CATEGORY = 1;
    private static final int VIEW_ORDER_ITEM = 2;
    private static final int VIEW_SEE_ALL = 3;
    private static final int VIEW_VENDOR = 4;
    private static final int VIEW_DIVIDER = 5;
    private RecyclerView recyclerView;



    public DeliveryHomeAdapter(Context context, Callback callback, RecyclerView recyclerView) {
        this.context = context;
        this.callback = callback;
        this.recyclerView = recyclerView;

    }

    public synchronized void setList(List<Object> elements) {
        this.dataToDisplay = elements;
        notifyDataSetChanged();
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
                return new ViewSeeAll(v, this);
            case VIEW_DIVIDER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_divider_delivery, parent, false);
                return new ViewDivider(v);
            default:
                throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataToDisplay == null ? 0 : dataToDisplay.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object  = dataToDisplay.get(position);

        if(object instanceof DeliveryTitleCategory)
            return VIEW_TITLE_CATEGORY;

        if(object instanceof DeliverySeeAll)
            return VIEW_SEE_ALL;

        if(object instanceof DeliveryDivider)
            return VIEW_DIVIDER;

        if(object instanceof RecentOrder)
            return VIEW_ORDER_ITEM;

        if(object instanceof MenusResponse.Vendor)
            return VIEW_VENDOR;



        throw new IllegalArgumentException();
    }


    public interface Callback {
        void onItemClick(Object clickedItem);

    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if (pos != RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {
                case R.id.llRoot:
                    break;

                case R.id.tvNeedHelp:
                    break;

                case R.id.tvViewDetails:
                    break;

                case R.id.llMain:
                    break;
            }
        }
    }


    private class ViewOrderStatus extends RecyclerView.ViewHolder {

        public LinearLayout linear;
        RelativeLayout container, relativeStatusBar;
        TextView tvOrderId, tvOrderIdValue, tvDeliveryBefore, tvDeliveryTime, tvStatus0, tvStatus1, tvStatus2, tvStatus3;
        ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
        View lineStatus1, lineStatus2, lineStatus3;
        RelativeLayout rlRestaurantInfo, rlTrackViewOrder;
        TextView tvRestaurantName, tvOrderAmount, tvTrackOrder, tvViewOrder;

        RelativeLayout rlOrderNotDelivered;
        TextView tvOrderDeliveredDigIn, tvOrderNotDelivered, tvOrderDeliveredYes, tvOrderDeliveredNo;
        LinearLayout llOrderDeliveredYes, llOrderDeliveredNo;
        ImageView ivOrderDeliveredYes, ivOrderDeliveredNo;
        View vOrderDeliveredMidSep, vOrderDeliveredTopSep;

        ViewOrderStatus(View itemView, ItemListener itemListener) {
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
        }
    }
    static class ViewTitleCategory extends RecyclerView.ViewHolder {
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
    static class ViewSeeAll extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_see_all)
        LinearLayout llSeeAll;

        ViewSeeAll(View view, final ItemListener itemListener) {
            super(view);
            ButterKnife.bind(this, view);
            llSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(llSeeAll,llSeeAll);
                }
            });
        }
    }

    public static class DeliveryTitleCategory{
        private String categoryName;
        private String categoryUrl;

        public DeliveryTitleCategory(String categoryName, String categoryUrl) {
            this.categoryName = categoryName;
            this.categoryUrl = categoryUrl;
        }
    }
    public static class DeliverySeeAll{
        private static DeliverySeeAll deliverySeeAll;
        private DeliverySeeAll() {
        }
        public DeliverySeeAll getInstance(){
            if(deliverySeeAll==null)
                deliverySeeAll= new DeliverySeeAll();
            return deliverySeeAll;
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


}