package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.LoginResponse;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by shankar on 5/10/16.
 */
public class PromoCouponsAdapter extends BaseAdapter {

	private int layoutRID;
	private Activity activity;
	private ArrayList<PromoCoupon> offerList = new ArrayList<>();
	private Callback callback;
	private LayoutInflater mInflater;
	private static final  int REFERAL_ITEM = 1;
	private static final  int OFFER_ITEM = 0;
	private int offerItemPosition;

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
	public int getItemViewType(int position) {
		if(offerList.get(position)!=null && offerList.get(position).showPromoBox()){
			offerItemPosition = position;
			return REFERAL_ITEM;

		}

		return OFFER_ITEM;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object holder  =null;
		if (convertView == null) {
			if(getItemViewType(position)==REFERAL_ITEM){
				convertView = mInflater.inflate(R.layout.order_place_referal, null);
				holder = new ViewHolderReferal(convertView, activity);
			}else{
				convertView = mInflater.inflate(layoutRID, null);
				holder = new ViewHolder(convertView, activity);
				((ViewHolder)holder).relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(((ViewHolder)holder).relative);
			}


			convertView.setTag(holder);
		} else {

			holder = convertView.getTag();
			if(holder instanceof ViewHolderReferal){
				((ViewHolderReferal)holder).id = position;

			}else{
				((ViewHolder)holder).id = position;

			}
		}

		onBindViewHolder(holder, position);

		return convertView;
	}



	public void onBindViewHolder(final Object holder, int position) {

		if(holder instanceof ViewHolder){

			PromoCoupon promoCoupon = offerList.get(position);

			((ViewHolder)holder).textViewOfferName.setText(promoCoupon.getTitle());

			((ViewHolder)holder).relative.setTag(holder);
			((ViewHolder)holder).textViewTNC.setTag(position);

			((ViewHolder)holder).textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
			if(layoutRID == R.layout.list_item_promo_coupon) {
				if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
					((ViewHolder)holder).rlContainer.setBackgroundResource(R.drawable.background_white_theme_color_rounded_bordered);
					((ViewHolder)holder).textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				} else {
					((ViewHolder)holder).rlContainer.setBackgroundColor(activity.getResources().getColor(R.color.offer_popup_item_color));
					((ViewHolder)holder).textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
				}
			} else {
				if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
					((ViewHolder)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
				} else {
					((ViewHolder)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
				}
				((ViewHolder)holder).textViewOfferName.setTextColor(ContextCompat.getColor(activity, promoCoupon.getIsValid() == 1 ? R.color.text_color : R.color.text_color_hint));
			}

			((ViewHolder)holder).textViewTNC.setOnClickListener(new View.OnClickListener() {
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

			((ViewHolder)holder).relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int position = ((ViewHolder) v.getTag()).id;
						PromoCoupon promoCoupon = offerList.get(position);
						if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)){
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

		}else{

			final PromoCoupon promoCoupon = offerList.get(position);
			setReferalView(promoCoupon,((ViewHolderReferal)holder));


			((ViewHolderReferal)holder).btnApply.setTag(holder);
			((ViewHolderReferal)holder).btnApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolderReferal holderReferal = (ViewHolderReferal) v.getTag();
					int position = holderReferal.id;
					PromoCoupon coupon = offerList.get(position);
					if(coupon.isPromoApplied()){
						callback.setSelectedCoupon(-1);
						coupon.setIsPromoApplied(false);
						coupon.setMessageToDisplay(null);
						holderReferal.editText.setText(null);
						notifyDataSetChanged();
					}else{
						String input = holderReferal.editText.getText().toString().trim();
						if(input.length()>0 && (holderReferal).tvError.getVisibility()!=View.VISIBLE){

							callback.applyPromoCoupon(input);

						}

					}

					/*if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(coupon)) {


					} else {

					}*/
				}
			});

		}

	}


	class ViewHolder {
		public int id;
		public RelativeLayout relative, rlContainer;
		public TextView textViewOfferName, textViewTNC;
		public ImageView imageViewRadio;
		public ViewHolder(View itemView, Activity activity) {
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
			textViewOfferName = (TextView) itemView.findViewById(R.id.textViewOfferName);
			textViewOfferName.setTypeface(Fonts.mavenRegular(activity));
			textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
			textViewTNC.setTypeface(Fonts.mavenLight(activity));
			imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
		}
	}
	class ViewHolderReferal {
		public int id;
		public EditText editText;
		public Button btnApply;
		public TextView tvError;


		public ViewHolderReferal(View itemView, Activity activity) {
			editText = (EditText) itemView.findViewById(R.id.edtPromo);
			btnApply = (Button) itemView.findViewById(R.id.tv_apply);
			tvError = (TextView) itemView.findViewById(R.id.tv_promo_error);
			editText.addTextChangedListener(new PromoTextWatcher(tvError,editText));
		}
	}

	public interface Callback{

		void onCouponSelected();
		PromoCoupon getSelectedCoupon();
		boolean setSelectedCoupon(int position);

		void applyPromoCoupon(String text);

	}


	private void setReferalView(PromoCoupon referalCode,ViewHolderReferal viewHolderReferal) {


		if(referalCode!=null){
			if(referalCode.isPromoApplied()){
				viewHolderReferal.editText.setEnabled(false);
				viewHolderReferal.editText.setText(referalCode.getPromoName());
				viewHolderReferal.tvError.setVisibility(View.VISIBLE);
				viewHolderReferal.tvError.setText(referalCode.messageToDisplay());
				viewHolderReferal.tvError.setTextColor(ContextCompat.getColor(activity,R.color.text_green_color));
				viewHolderReferal.btnApply.setText(R.string.label_remove);

			}else {
				viewHolderReferal.editText.setEnabled(true);
				viewHolderReferal.tvError.setVisibility(View.VISIBLE);
				viewHolderReferal.tvError.setText(referalCode.messageToDisplay());
				viewHolderReferal.tvError.setTextColor(ContextCompat.getColor(activity,R.color.red_dark));
				if(referalCode.messageToDisplay()!=null){
					viewHolderReferal.tvError.setVisibility(View.VISIBLE);
				}else{
					viewHolderReferal.tvError.setVisibility(View.GONE);
				}
				viewHolderReferal.btnApply.setText(R.string.label_apply);




			}

		}else{

			viewHolderReferal.editText.setEnabled(true);
			viewHolderReferal.editText.setText(null);
			viewHolderReferal.tvError.setVisibility(View.GONE);


		}
	}
	private class PromoTextWatcher implements  TextWatcher{
		private TextView textView;
		private EditText editText;
		public PromoTextWatcher(TextView textView,EditText editText) {
			this.textView = textView;
			this.editText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if(textView.getVisibility()==View.VISIBLE){
				if(offerList!=null && offerItemPosition<offerList.size()){
					offerList.get(offerItemPosition).setMessageToDisplay(null);
					notifyDataSetChanged();

				}
			}

		}
	}

}
