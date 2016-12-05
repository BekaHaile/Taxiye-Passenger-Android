package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-38 on 31/05/16.
 */
public class CustomDrawerAdapter extends ArrayAdapter<String> {

    Context context;
    String[] drawerItemList;
    int selectedPosition=-1;
    double tokensCount=0;
     public CustomDrawerAdapter(Context context, int layoutResourceID,
                                String[] listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.item_name);
            drawerHolder.leftIcon = (ImageView) view.findViewById(R.id.item_left_icon);
            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }


        drawerHolder.ItemName.setText(drawerItemList[position]);
        if(position==0)
            drawerHolder.leftIcon.setBackgroundResource(R.drawable.icon_bank_account_b);
        if(position==1)
            drawerHolder.leftIcon.setBackgroundResource(R.drawable.icon_pending_transactions_b);
       else if(position==2)
            drawerHolder.leftIcon.setBackgroundResource(R.drawable.icon_transactions_history_b);
       else if(position==3)
            drawerHolder.leftIcon.setBackgroundResource(R.drawable.icon_f_a_qs_b);
       else if(position==4)
            drawerHolder.leftIcon.setBackgroundResource(R.drawable.icon_support_b);


        /*if (position == selectedPosition) {
                drawerHolder.ItemName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                view.setBackgroundColor(context.getResources().getColor(R.color.progress_secondary));
        }
        else {
            view.setBackgroundColor(context.getResources().getColor(R.color.appBackgroundColor));
            drawerHolder.ItemName.setTextColor(context.getResources().getColor(R.color.lightBlackTxtColor));
//            drawerHolder.ItemName.setTextColor(context.getResources().getColor(R.color.drawer_item_txt_color));
//            drawerHolder.rightArrowIcon.setImageDrawable(context.getResources().getDrawable(
//                    R.drawable.icon_arrow_menu_normal));
        }*/


        return view;
    }


    public void setTokensCount(double tokensCount)
    {
        this.tokensCount = tokensCount;
        notifyDataSetChanged();
    }

    private static class DrawerItemHolder {
        TextView ItemName,tokenTxt;
         ImageView leftIcon;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

}