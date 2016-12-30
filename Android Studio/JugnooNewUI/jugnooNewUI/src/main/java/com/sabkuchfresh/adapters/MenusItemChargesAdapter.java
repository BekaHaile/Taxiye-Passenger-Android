package com.sabkuchfresh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by socomo on 12/27/16.
 */

public class MenusItemChargesAdapter extends BaseAdapter
{
    private Context context;
    LayoutInflater menusItemChargesLayout;
    ArrayList<SubItem.Tax> taxes;

    public MenusItemChargesAdapter(Context context, ArrayList<SubItem.Tax> taxes) {
        this.context = context;
        this.taxes = taxes;
        this.menusItemChargesLayout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return taxes.size();
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
        MenusItemChargesAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = menusItemChargesLayout.inflate(R.layout.list_item_menu_charge, null);
            holder = new MenusItemChargesAdapter.ViewHolder();
            holder.relativeLayoutMenusItemCharges = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutMenusItemCharges);
            holder.imageViewSep1 = (ImageView) convertView.findViewById(R.id.imageViewSep1);
            holder.textViewMenusItemChargeName = (TextView) convertView.findViewById(R.id.textViewMenusItemChargeName); holder.textViewMenusItemChargeName.setTypeface(Fonts.mavenMedium(context));
            holder.textViewMenusItemChargeValue = (TextView) convertView.findViewById(R.id.textViewMenusItemChargeValue); holder.textViewMenusItemChargeValue.setTypeface(Fonts.mavenRegular(context));

            convertView.setTag(holder);
        } else {
            holder = (MenusItemChargesAdapter.ViewHolder) convertView.getTag();
        }
        try {
            holder.textViewMenusItemChargeName.setText(taxes.get(position).getKey());
            holder.textViewMenusItemChargeValue.setText(context.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(taxes.get(position).getValue())));

            if(taxes.get(position).getValue() > 0){
                holder.textViewMenusItemChargeValue.setTextColor(context.getResources().getColor(R.color.text_color));
            } else {
                holder.textViewMenusItemChargeValue.setText(context.getString(R.string.free));
                holder.textViewMenusItemChargeValue.setTextColor(context.getResources().getColor(R.color.green));
            }

            holder.imageViewSep1.setVisibility((position) == getCount()-1 ? View.GONE : View.VISIBLE );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        public RelativeLayout relativeLayoutMenusItemCharges;
        public ImageView imageViewSep1;
        public TextView textViewMenusItemChargeName, textViewMenusItemChargeValue;

    }
}

