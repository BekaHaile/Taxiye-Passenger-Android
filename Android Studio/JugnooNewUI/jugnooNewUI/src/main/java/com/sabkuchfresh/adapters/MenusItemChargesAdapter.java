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
    public Object getItem(int position)
    {
        return null;
    }
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenusItemChargesAdapter.ViewHolder holder = new MenusItemChargesAdapter.ViewHolder();
        if (convertView == null) {
            convertView = menusItemChargesLayout.inflate(R.layout.list_item_menu_charge, null);
            holder.relativeLayoutMenusItemCharges = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutMenusItemCharges);
            holder.imageViewSep1 = (ImageView) convertView.findViewById(R.id.imageViewSep1);
            holder.textViewMenusItemChargeName = (TextView) convertView.findViewById(R.id.textViewMenusItemChargeName);
            holder.textViewMenusItemChargeValue = (TextView) convertView.findViewById(R.id.textViewMenusItemChargeValue);
        } else {
            holder = (MenusItemChargesAdapter.ViewHolder) convertView.getTag();
        }
        try
        {
            holder.textViewMenusItemChargeName.setText(taxes.get(position).getKey());
            holder.textViewMenusItemChargeValue.setText(context.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(taxes.get(position).getValue())));
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

