package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/10/16.
 */
public class PromoCouponsAdapter extends BaseAdapter {

    private int layoutRID;
    private Activity activity;
    private ArrayList<PromoCoupon> offerList = new ArrayList<>();
    private Callback callback;
    private LayoutInflater mInflater;

    public PromoCouponsAdapter(Activity activity, int layoutRID, ArrayList<PromoCoupon> offerList, Callback callback) {
        this.activity = activity;
        this.layoutRID = layoutRID;
        this.offerList = offerList;
        this.callback = callback;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<PromoCoupon> offerList){
        this.offerList = offerList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return offerList.size();
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutRID, null);
            holder = new ViewHolder(convertView, activity);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.id = position;
        onBindViewHolder(holder, position);

        return convertView;
    }


    public void onBindViewHolder(PromoCouponsAdapter.ViewHolder holder, int position) {
        PromoCoupon promoCoupon = offerList.get(position);

        if (promoCoupon == null) {
            holder.textViewNoCurrentOffersYet.setVisibility(View.VISIBLE);
            holder.textViewOfferName.setVisibility(View.GONE);
            holder.textViewTNC.setVisibility(View.GONE);
            holder.imageViewRadio.setVisibility(View.GONE);


        } else {

            holder.textViewOfferName.setText(promoCoupon.getTitle());

            holder.relative.setTag(holder);
            holder.textViewTNC.setTag(position);

            holder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
            if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
                holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
                holder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            } else {
                holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
                holder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
            }
            holder.textViewOfferName.setTextColor(ContextCompat.getColor(activity, promoCoupon.getIsValid() == 1 ? R.color.text_color : R.color.text_color_hint));

            holder.textViewTNC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        PromoCoupon promoCoupon = offerList.get(position);
                        if (promoCoupon instanceof CouponInfo) {
                            DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo) promoCoupon).description, true, true, false);
                        } else if (promoCoupon instanceof PromotionInfo) {
                            DialogPopup.alertPopupLeftOriented(activity, "", ((PromotionInfo) promoCoupon).terms, true, true, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = ((ViewHolder) v.getTag()).id;
                        PromoCoupon promoCoupon = offerList.get(position);
                        if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
                            callback.setSelectedCoupon(-1);

                        } else {

                            callback.setSelectedCoupon(position);
                            callback.onCouponSelected();
                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    class ViewHolder {
        public int id;
        public RelativeLayout relative, rlContainer;
        public TextView textViewOfferName, textViewTNC,textViewNoCurrentOffersYet;
        public ImageView imageViewRadio;
        public ViewHolder(View itemView, Activity activity) {
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
            textViewOfferName = (TextView) itemView.findViewById(R.id.textViewOfferName);
            textViewOfferName.setTypeface(Fonts.mavenRegular(activity));
            textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
            textViewTNC.setTypeface(Fonts.mavenLight(activity));
            imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
            textViewNoCurrentOffersYet=(TextView) itemView.findViewById(R.id.textViewNoCurrentOffersYet);
        }
    }

    public interface Callback{

        void onCouponSelected();
        PromoCoupon getSelectedCoupon();
        boolean setSelectedCoupon(int position);
        void applyPromoCoupon(String text);

    }

}
