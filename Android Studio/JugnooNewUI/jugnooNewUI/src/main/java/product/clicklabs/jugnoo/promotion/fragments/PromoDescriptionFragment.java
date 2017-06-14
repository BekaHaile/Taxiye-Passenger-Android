package product.clicklabs.jugnoo.promotion.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 01/05/17.
 */

public class PromoDescriptionFragment extends Fragment {

	@Bind(R.id.tvOfferingName)
	TextView tvOfferingName;
	@Bind(R.id.tvOfferTitle)
	TextView tvOfferTitle;
	@Bind(R.id.tvOfferExpireDate)
	TextView tvOfferExpireDate;
	@Bind(R.id.tvOfferTerms)
	TextView tvOfferTerms;
	private View rootView;
	private Context context;

	private String offeringName, clientId;
	private PromoCoupon promoCoupon;


	public static PromoDescriptionFragment newInstance(String offeringName, String clientId, PromoCoupon promoCoupon){
		PromoDescriptionFragment fragment = new PromoDescriptionFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.KEY_OFFERING_NAME, offeringName);
		bundle.putString(Constants.KEY_CLIENT_ID, clientId);
		bundle.putSerializable(Constants.KEY_PROMO_COUPON, promoCoupon);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments(){
		Bundle bundle = getArguments();
		offeringName = bundle.getString(Constants.KEY_OFFERING_NAME);
		clientId = bundle.getString(Constants.KEY_CLIENT_ID);
		promoCoupon = (PromoCoupon) bundle.getSerializable(Constants.KEY_PROMO_COUPON);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context != null) {
			this.context = context;
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_promo_description, container, false);
		ButterKnife.bind(this, rootView);

		parseArguments();

		tvOfferingName.setText(offeringName);

		SpannableStringBuilder offerTitle = new SpannableStringBuilder(promoCoupon.getTitle());
		offerTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, offerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvOfferTitle.setText(offerTitle);

		String expireDate = DateOperations.convertDateOnlyViaFormatSlash(DateOperations.utcToLocalWithTZFallback(promoCoupon.getExpiryDate()));
		SpannableStringBuilder validUntilDate = new SpannableStringBuilder(context.getString(R.string.valid_until_format, expireDate));
		validUntilDate.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_87)),
				0, validUntilDate.length()-expireDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		validUntilDate.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				validUntilDate.length()-expireDate.length(), validUntilDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvOfferExpireDate.setText(validUntilDate);

		if (promoCoupon instanceof CouponInfo) {
			tvOfferTerms.setText(((CouponInfo) promoCoupon).description);
		} else if (promoCoupon instanceof PromotionInfo) {
			tvOfferTerms.setText(Utils.trimHTML(Utils.fromHtml(((PromotionInfo) promoCoupon).terms)));
		}

		Utils.hideKeyboard(getActivity());

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick(R.id.bUseCoupon)
	public void useCoupon() {
		if(context instanceof PromotionActivity && promoCoupon != null) {
			Prefs.with(context).save(Constants.SP_USE_COUPON_ + clientId, promoCoupon.getId());
			Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, (promoCoupon instanceof CouponInfo));
			if(!clientId.equals(Config.getAutosClientId())) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Utils.showToast(context, context.getString(R.string.offer_auto_applied_message_format, "checkout"), Toast.LENGTH_LONG);
					}
				}, 500);
			}
			MyApplication.getInstance().getAppSwitcher().switchApp((PromotionActivity)context, clientId,
					new LatLng(Data.latitude, Data.longitude), true);
		}
	}
}