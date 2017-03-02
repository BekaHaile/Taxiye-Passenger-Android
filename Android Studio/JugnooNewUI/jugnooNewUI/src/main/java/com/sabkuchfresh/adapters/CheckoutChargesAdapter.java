package com.sabkuchfresh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.menus.Tax;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by socomo on 12/27/16.
 */

public class CheckoutChargesAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Tax> taxes;

	public CheckoutChargesAdapter(Context context, ArrayList<Tax> taxes) {
		this.context = context;
		this.taxes = taxes;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
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
		ViewHolderMain holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_checkout_charge, null);
			holder = new ViewHolderMain();
			holder.rlRoot = (RelativeLayout) convertView.findViewById(R.id.rlRoot);
			holder.vSep = convertView.findViewById(R.id.vSep);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);

			holder.rlRoot.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 82));
			ASSL.DoMagic(holder.rlRoot);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderMain) convertView.getTag();
		}
		try {
			holder.tvName.setText(taxes.get(position).getKey());


			String finalVal ;
			if(taxes.get(position).getValue()%1==0)
				finalVal = String.valueOf(taxes.get(position).getValue().intValue());
			else
				finalVal = Utils.getDecimalFormat2Decimal().format(taxes.get(position).getValue());
			holder.tvValue.setText(context.getString(R.string.rupees_value_format, finalVal));

			if (taxes.get(position).getValue() > 0) {
				holder.tvValue.setTextColor(context.getResources().getColor(R.color.text_color));
			} else {
				holder.tvValue.setText(context.getString(R.string.free));
				holder.tvValue.setTextColor(context.getResources().getColor(R.color.green));
			}

			holder.vSep.setVisibility((position) == getCount() - 1 ? View.GONE : View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolderMain {
		public RelativeLayout rlRoot;
		public View vSep;
		public TextView tvName, tvValue;
	}
}

