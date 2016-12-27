package com.sabkuchfresh.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.retrofit.model.ProductsResponse;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.GridViewAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by socomo on 12/27/16.
 */

public class MenusItemChargesAdapter extends BaseAdapter
{
    private Context context;
    LayoutInflater menusItemChargesLayout;
    ArrayList<String> itemCharges;
    public MenusItemChargesAdapter(Context context)
    {
        this.context = context;
    }

    public MenusItemChargesAdapter(Context context, ArrayList<String> itemCharges)
    {
        this.context = context;
        this.itemCharges = itemCharges;
        this.menusItemChargesLayout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return itemCharges.size();
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
        if (convertView == null)
        {
            convertView = menusItemChargesLayout.inflate(R.layout.list_item_menu_charge, null);
            holder.relativeLayoutMenusItemCharges = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutMenusItemCharges);
            holder.imageViewSep1 = (ImageView) convertView.findViewById(R.id.imageViewSep1);
            holder.textViewMenusItemCharge = (TextView) convertView.findViewById(R.id.textViewMenusItemCharge);
            holder.textViewMenusItemChargeValue = (TextView) convertView.findViewById(R.id.textViewMenusItemChargeValue);

        }
        else
            holder = (MenusItemChargesAdapter.ViewHolder) convertView.getTag();
        try
        {

            Log.v("gridListSize ","gridListSize "+itemCharges.size());


                holder.textViewMenusItemCharge.setText(itemCharges.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        public RelativeLayout relativeLayoutMenusItemCharges;
        public ImageView imageViewSep1;
        public TextView textViewMenusItemCharge, textViewMenusItemChargeValue;

    }
}

