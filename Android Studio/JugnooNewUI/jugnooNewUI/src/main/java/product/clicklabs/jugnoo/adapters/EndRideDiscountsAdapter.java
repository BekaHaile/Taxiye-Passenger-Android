package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by socomo20 on 7/27/15.
 */
public class EndRideDiscountsAdapter extends BaseAdapter {

    LayoutInflater mInflater;
	ViewHolderDiscount holder;
    Context context;
    boolean showNegativeValues, isFromOrderHistory;

    ArrayList<DiscountType> discountTypes;
    String currency;

    public EndRideDiscountsAdapter(Context context, boolean showNegativeValues) {
        this.context = context;
        this.showNegativeValues = showNegativeValues;
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

	public synchronized void setList(ArrayList<DiscountType> discountTypes, String currency){
		this.discountTypes = discountTypes;
		this.currency = currency;
		notifyDataSetChanged();
	}

    public synchronized void setList(ArrayList<DiscountType> discountTypes, String currency, boolean isFromOrderHistory){
        this.discountTypes = discountTypes;
        this.currency = currency;
        this.isFromOrderHistory = isFromOrderHistory;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderDiscount();
            convertView = mInflater.inflate(R.layout.list_item_end_ride_discount, null);

            holder.textViewDiscount = (TextView) convertView.findViewById(R.id.textViewDiscount); holder.textViewDiscount.setTypeface(Fonts.mavenLight(context));
			holder.textViewDiscountValue = (TextView) convertView.findViewById(R.id.textViewDiscountValue); holder.textViewDiscountValue.setTypeface(Fonts.mavenRegular(context));

            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

            holder.relative.setLayoutParams(new ListView.LayoutParams(560, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderDiscount) convertView.getTag();
        }

        holder.id = position;

        DiscountType discountType = discountTypes.get(position);

//        if(discountType.getReferenceId() == 0) {
//            holder.textViewDiscount.setText("- " + discountType.name);
//            holder.textViewDiscountValue.setText("-"+String.format(context.getResources()
//                            .getString(R.string.rupees_value_format),
//                    Utils.getMoneyDecimalFormat().format(discountType.value)));
//        } else{
        if(isFromOrderHistory) {
            holder.textViewDiscountValue.setTextSize(20);
            holder.textViewDiscount.setTextSize(20);
            convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            convertView.setPadding(0, 18, 0, 18);
        }
            holder.textViewDiscount.setText(discountType.getName());
            holder.textViewDiscountValue.setText(Utils.formatCurrencyValue(currency, showNegativeValues ? -discountType.value : discountType.value));
//        }

        return convertView;
    }

    private class ViewHolderDiscount {
        TextView textViewDiscount, textViewDiscountValue;
        RelativeLayout relative;
        int id;
    }
}

