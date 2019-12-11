package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;


public class PromoCouponsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

    private int layoutRID;
    private Activity activity;
    private ArrayList<PromoCoupon> offerList = new ArrayList<>();
    private PromoCouponsAdapter.Callback callback;
    private LayoutInflater mInflater;
    private static final int REFFERAL_ITEM = 1;
    private static final int OFFER_ITEM = 0;
    private RecyclerView recyclerView;


    public PromoCouponsRecyclerAdapter(Activity activity, int layoutRID, ArrayList<PromoCoupon> offerList, PromoCouponsAdapter.Callback callback, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.layoutRID = layoutRID;
        this.offerList = offerList;
        this.callback = callback;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<PromoCoupon> offerList) {
        this.offerList = offerList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case REFFERAL_ITEM:
                View referalView = mInflater.inflate(R.layout.order_place_referal, parent, false);
                viewHolder = new ReferalViewHolder(referalView, this);
                viewHolder.itemView.setVisibility(View.GONE);
                break;
            case OFFER_ITEM:
                View promoView = mInflater.inflate(layoutRID, parent, false);
                viewHolder = new PromoViewHolder(promoView, this);
                ((PromoViewHolder) viewHolder).relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                ASSL.DoMagic(((PromoViewHolder) viewHolder).relative);
                break;
            default:
                throw new IllegalArgumentException();


		}


		return viewHolder;


	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof PromoViewHolder){

			PromoViewHolder promoViewHolder = (PromoViewHolder) holder;
			PromoCoupon promoCoupon = offerList.get(position);
			promoViewHolder.textViewOfferName.setText(promoCoupon.getTitle());

			promoViewHolder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
			if(layoutRID == R.layout.list_item_promo_coupon) {
				if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
					promoViewHolder.rlContainer.setBackgroundResource(R.drawable.background_white_theme_color_rounded_bordered);
					promoViewHolder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				} else {
					promoViewHolder.rlContainer.setBackgroundColor(activity.getResources().getColor(R.color.offer_popup_item_color));
					promoViewHolder.textViewOfferName.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
				}
			} else {
				if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)) {
					promoViewHolder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
				} else {
					promoViewHolder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
				}
				promoViewHolder.textViewOfferName.setTextColor(ContextCompat.getColor(activity, promoCoupon.getIsValid() == 1 ? R.color.text_color : R.color.text_color_hint));
			}

		}else{

			final PromoCoupon promoCoupon = offerList.get(position);
			setReferalView(promoCoupon,((ReferalViewHolder)holder));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if(offerList.get(position)!=null && offerList.get(position).showPromoBox()){
			return REFFERAL_ITEM;

		}

		return OFFER_ITEM;
	}

	@Override
	public int getItemCount() {
		return offerList==null?0:offerList.size();
	}



	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int positionInList = recyclerView.getChildAdapterPosition(parentView);
		if(positionInList!=RecyclerView.NO_POSITION){

			switch (viewClicked.getId()){
				case R.id.textViewTNC:
					try {
						PromoCoupon promoCoupon = offerList.get(positionInList);
						if (promoCoupon instanceof CouponInfo) {
							DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo) promoCoupon).description, true, true, false);
						} else if (promoCoupon instanceof PromotionInfo) {
							DialogPopup.alertPopupLeftOriented(activity, "", ((PromotionInfo) promoCoupon).terms, true, true, true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case R.id.relative:
					try {
						PromoCoupon promoCoupon = offerList.get(positionInList);
						if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)){
							callback.setSelectedCoupon(-1);
						} else {

							if(callback.getSelectedCoupon()!=null && callback.getSelectedCoupon().showPromoBox()){
								showRemoveCouponPopup(positionInList);
							}else{
								applyCouponfromPosition(positionInList);
							}

						}
						notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case R.id.tv_apply:
					RecyclerView.ViewHolder viewHolder  = recyclerView.getChildViewHolder(parentView);
					if(viewHolder instanceof ReferalViewHolder){
						ReferalViewHolder referalViewHolder = (ReferalViewHolder) viewHolder;
						PromoCoupon coupon = offerList.get(positionInList);
						if(coupon.isPromoApplied()){
							showRemoveCouponPopup(null);
						}else{
							String input = referalViewHolder.editText.getText().toString().trim();
							if(input.length()>0 ){
								if((referalViewHolder).tvError.getVisibility()!=View.VISIBLE){
									callback.applyPromoCoupon(input);

								}

							}else{
								Utils.showToast(activity, activity.getString(R.string.please_enter_code));
							}

						}
					}


					break;
			}



		}

	}

	private void applyCouponfromPosition(int positionInList) {
		callback.setSelectedCoupon(positionInList);
		callback.onCouponSelected();
	}

	private void removePromoCoupon() {
		callback.setSelectedCoupon(-1);
		notifyDataSetChanged();
	}


	class PromoViewHolder extends RecyclerView.ViewHolder{
		public int id;
		RelativeLayout relative, rlContainer;
		TextView textViewOfferName, textViewTNC;
		ImageView imageViewRadio;
		PromoViewHolder(final View itemView, final ItemListener itemListener) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
			textViewOfferName = (TextView) itemView.findViewById(R.id.textViewOfferName);
			textViewOfferName.setTypeface(Fonts.mavenRegular(activity));
			textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
			textViewTNC.setTypeface(Fonts.mavenLight(activity));
			imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
			textViewTNC.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(v,itemView);
				}
			});
			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(v,itemView);
				}
			});
		}
	}
	class ReferalViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public EditText editText;
		Button btnApply;
		TextView tvError;


		ReferalViewHolder(final View itemView, final ItemListener itemListener) {
			super(itemView);
			editText = (EditText) itemView.findViewById(R.id.edtPromo);
			btnApply = (Button) itemView.findViewById(R.id.tv_apply);
			tvError = (TextView) itemView.findViewById(R.id.tv_promo_error);
			Utils.addCapitaliseFilterToEditText(editText);
			btnApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(v,itemView);
				}
			});
			editText.addTextChangedListener(new PromoTextWatcher(tvError,btnApply));

		}
	}




	private void setReferalView(PromoCoupon referralCode,ReferalViewHolder viewHolderReferal) {


		if(referralCode!=null){
			if(referralCode.isPromoApplied() && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(referralCode) ){
				viewHolderReferal.editText.setEnabled(false);
				viewHolderReferal.editText.setText(referralCode.getPromoName());
				viewHolderReferal.tvError.setVisibility(View.VISIBLE);
				viewHolderReferal.tvError.setText(referralCode.messageToDisplay());
				viewHolderReferal.tvError.setTextColor(ContextCompat.getColor(activity,R.color.text_green_color));
				viewHolderReferal.btnApply.setText(R.string.label_remove);

			}else {
				if(referralCode.isPromoApplied()){
					referralCode.setIsPromoApplied(false);
					referralCode.setMessageToDisplay(null);
					viewHolderReferal.editText.setText(null);
				}
				viewHolderReferal.editText.setEnabled(true);
				viewHolderReferal.tvError.setVisibility(View.VISIBLE);
				viewHolderReferal.tvError.setText(referralCode.messageToDisplay());
				viewHolderReferal.tvError.setTextColor(ContextCompat.getColor(activity,R.color.red_dark));
				if(referralCode.messageToDisplay()!=null){
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
		private Button buttonApply;
		PromoTextWatcher(TextView textView,Button buttonApply) {
			this.textView = textView;
			this.buttonApply = buttonApply;

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
				textView.setVisibility(View.GONE);

			}

			if(s.length()==0 && buttonApply.isEnabled()){
				buttonApply.setEnabled(false);
			}else{
				if(!buttonApply.isEnabled()){
					buttonApply.setEnabled(true);
				}
			}

		}
	}

	public void showRemoveCouponPopup(final Integer couponPositionToApplyAfterRemoval){
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", activity.getString(R.string.remove_popup_message),
				activity.getString(R.string.yes),
				activity.getString(R.string.no),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						removePromoCoupon();
						if(couponPositionToApplyAfterRemoval!=null){
							applyCouponfromPosition(couponPositionToApplyAfterRemoval);
						}

					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {


					}
				}, true, false);
	}


}
