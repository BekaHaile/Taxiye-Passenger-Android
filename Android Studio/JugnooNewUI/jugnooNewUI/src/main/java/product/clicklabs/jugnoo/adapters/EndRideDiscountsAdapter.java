package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 7/27/15.
 */
public class EndRideDiscountsAdapter extends BaseAdapter {

    LayoutInflater mInflater;
	ViewHolderDiscount holder;
    Context context;

    ArrayList<DiscountType> discountTypes;

    public EndRideDiscountsAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.discountTypes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return discountTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	public synchronized void setList(ArrayList<DiscountType> discountTypes){
		this.discountTypes = discountTypes;
		notifyDataSetChanged();
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderDiscount();
            convertView = mInflater.inflate(R.layout.list_item_end_ride_discount, null);

            holder.textViewDiscount = (TextView) convertView.findViewById(R.id.textViewDiscount); holder.textViewDiscount.setTypeface(Fonts.latoRegular(context));
			holder.textViewDiscountRupee = (TextView) convertView.findViewById(R.id.textViewDiscountRupee); holder.textViewDiscountRupee.setTypeface(Fonts.latoRegular(context));
			holder.textViewDiscountValue = (TextView) convertView.findViewById(R.id.textViewDiscountValue); holder.textViewDiscountValue.setTypeface(Fonts.latoRegular(context));

            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

            holder.relative.setLayoutParams(new ListView.LayoutParams(560, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderDiscount) convertView.getTag();
        }

        holder.id = position;

        DiscountType discountType = discountTypes.get(position);

        holder.textViewDiscount.setText("- "+discountType.name);
		holder.textViewDiscountValue.setText("- "+Utils.getMoneyDecimalFormat().format(discountType.value));

        return convertView;
    }


    private class ViewHolderDiscount {
        TextView textViewDiscount, textViewDiscountRupee, textViewDiscountValue;
        RelativeLayout relative;
        int id;
    }
}

