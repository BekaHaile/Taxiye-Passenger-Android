package com.sabkuchfresh.fragments;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.adapters.RestaurantReviewImagesAdapter;
import com.sabkuchfresh.adapters.RestaurantReviewsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiRestaurantFetchFeedback;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.CreateChatResponse;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedString;


public class MerchantInfoFragment extends Fragment implements GAAction {

    @BindView(R.id.tvMerchantName)
    TextView tvMerchantName;
    @BindView(R.id.tvReviewCount)
    TextView tvReviewCount;
    @BindView(R.id.tvOpensAt)
    TextView tvOpensAt;
    @BindView(R.id.bOrderOnline)
    Button bOrderOnline;
    @BindView(R.id.tvMerchantAddress)
    TextView tvMerchantAddress;
    @BindView(R.id.rvTopReviews)
    RecyclerView rvTopReviews;
    @BindView(R.id.llSeeAll)
    LinearLayout llSeeAll;
    @BindView(R.id.tvSeeAllReviews)
    TextView tvSeeAllReviews;
    @BindView(R.id.progressWheel)
    ProgressWheel progressWheel;
    @BindView(R.id.tvReviewsHeader)
    TextView tvReviewsHeader;
    @BindView(R.id.tvNoReviews)
    TextView tvNoReviews;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.tvCuisines)
    TextView tvCuisines;
    @BindView(R.id.tv_delivers_in)
    TextView tvDeliversIn;
    @BindView(R.id.tv_min_order_amt)
    TextView tvMinOrderAmt;
    @BindView(R.id.tvOffer)
    TextView tvOffer;
    @BindView(R.id.llOffer)
    LinearLayout llOffer;
    @BindView(R.id.tvOpenStatus)
    TextView tvOpenStatus;
    @BindView(R.id.layout_order_details)
    RelativeLayout layoutOrderDetails;
    @BindView(R.id.tvlabelBullet)
    TextView tvlabelBullet;
    @BindView(R.id.ratingBarReview)
    RatingBarMenuFeedback ratingBarReview;
    @BindView(R.id.etReview)
    EditText etReview;
    @BindView(R.id.tvSubmitReview)
    TextView tvSubmitReview;
    @BindView(R.id.tvReviewTextCount)
    TextView tvReviewTextCount;
    @BindView(R.id.tvRateRestaurant)
    TextView tvRateRestaurant;
    @BindView(R.id.vAddReviewSep)
    View vAddReviewSep;
    @BindView(R.id.photos_count)
    TextView photosCount;
    @BindView(R.id.recycler_view_photos)
    RecyclerView recyclerViewPhotos;
    @BindView(R.id.layout_photos)
    LinearLayout layoutPhotos;
    @BindView(R.id.tvMerchantPhone)
    TextView tvMerchantPhone;
    @BindView(R.id.tvOutOfRadiusFatafatBanner)
    TextView tvOutOfRadiusFatafatBanner;
    @BindView(R.id.llMyReview)
    LinearLayout llMyReview;
    @BindView(R.id.llRatingStars)
    LinearLayout llRatingStars;
    @BindView(R.id.tvRating)
    TextView tvRating;
    @BindView(R.id.divider_below_details)
    View dividerBelowDetails;
    @BindView(R.id.llLocate)
    LinearLayout llLocate;
    @BindView(R.id.llPay)
    LinearLayout llPay;
    @BindView(R.id.llChatNow)
    LinearLayout llChatNow;
    @BindView(R.id.ivPay)
    ImageView ivPay;
    @BindView(R.id.ivChatNow)
    ImageView ivChatNow;
    @BindView(R.id.label_delivers_in)
    TextView labelDeliversIn;
    @BindView(R.id.view_center_order)
    View viewCenterOrder;
    @BindView(R.id.label_min_order_amt)
    TextView labelMinOrderAmt;
    @BindView(R.id.tvMerchantMinOrder)
    TextView tvMerchantMinOrder;
    @BindView(R.id.llMerchantMinOrderStrip)
    LinearLayout llMerchantMinOrderStrip;
    @BindView(R.id.ivStarRestaurantRating)
    ImageView ivStarRestaurantRating;
    @BindView(R.id.tvRestaurantRating)
    TextView tvRestaurantRating;
    @BindView(R.id.tvRatingReview)
    TextView tvRatingReview;
    @BindView(R.id.llRatingParent)
    LinearLayout llRatingParent;

    private View rootView;
    private FreshActivity activity;
    private RestaurantReviewsAdapter reviewsAdapter;
    private int restaurantId;

    // holds current user review
    private FetchFeedbackResponse.Review mUserReview;

    public MerchantInfoFragment() {
    }

    Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_merchant_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        photosCount.setTypeface(photosCount.getTypeface(), Typeface.BOLD);
        activity = (FreshActivity) getActivity();
        makeBold(tvMinOrderAmt, tvDeliversIn, tvMerchantName, tvReviewCount, tvReviewsHeader);
        activity.fragmentUISetup(this);
        activity.appBarLayout.setExpanded(true);
        activity.fragmentUISetup(this);

        // hide the delivery icon text
        activity.tvCollapRestaurantDeliveryTime.setVisibility(View.GONE);

        rvTopReviews.setLayoutManager(new LinearLayoutManager(activity));
        reviewsAdapter = new RestaurantReviewsAdapter(activity, new RestaurantReviewsAdapter.Callback() {
            @Override
            public void onEdit(FetchFeedbackResponse.Review review) {
                activity.setCurrentReview(review);
                activity.openRestaurantAddReviewFragment(true, 0.0f);
            }

            @Override
            public void onShare(FetchFeedbackResponse.Review review) {

            }

            @Override
            public void onLike(FetchFeedbackResponse.Review review) {
                GAUtils.event(activity.getGaCategory(), GAAction.FEED, GAAction.FEED + GAAction.LIKED);
            }

            @Override
            public void onScrollStateChanged(int newState) {
            }

            @Override
            public int getRestaurantId() {
                return activity.getVendorOpened().getRestaurantId();
            }

            @Override
            public MenusResponse.Vendor getVendor() {
                return activity.getVendorOpened();
            }

            @Override
            public String getShareTextSelf() {
                return "";
            }

            @Override
            public String getShareTextOther() {
                return "";
            }

            @Override
            public int getShareIsEnabled() {
                return 0;
            }

            @Override
            public int getLikeIsEnabled() {
                return 0;
            }

            @Override
            public RecyclerView getRecyclerView() {
                return rvTopReviews;
            }
        }, restaurantReviews, true);
        rvTopReviews.setAdapter(reviewsAdapter);

        activity.tvCollapRestaurantName.setVisibility(View.GONE);
        activity.llCollapseRating.setVisibility(View.GONE);
        activity.tvCollapRestaurantDeliveryTime.setText("");
        activity.clearRestaurantRatingStars(activity.llCollapRatingStars, activity.tvCollapRestaurantRating, null);
        progressWheel.stopSpinning();
        progressWheel.setVisibility(View.GONE);
        tvRateRestaurant.setVisibility(View.GONE);
        ratingBarReview.setVisibility(View.GONE);
        vAddReviewSep.setVisibility(View.GONE);
        tvNoReviews.setVisibility(View.GONE);
        restaurantId = activity.getVendorOpened() != null ? activity.getVendorOpened().getRestaurantId() : 0;
        setMerchantInfoToUI();
        GAUtils.trackScreenView(activity.getGaCategory() + MERCHANT_INFO + V2);
        etReview.setVisibility(View.GONE);
        tvSubmitReview.setVisibility(View.GONE);
        tvReviewTextCount.setVisibility(View.GONE);
        ratingBarReview.setOnScoreChanged(new RatingBarMenuFeedback.IRatingBarCallbacks() {
            @Override
            public void scoreChanged(float score) {

                if (userHasReviewed == 0) {
                    ratingBarReview.setScore(0, false);
                    ratingBarReview.setEnabled(false);
                    activity.openRestaurantAddReviewFragment(false, score);
                    activity.getHandler().postDelayed(enableRatingBarRunnable, 300);
                    if (score <= 0) {
                        Utils.hideSoftKeyboard(activity, etReview);
                    }
                } else {
                    Utils.showToast(activity, activity.getString(R.string.you_have_already_reviewed_format, activity.getVendorOpened().getName()));
                }

            }
        });

        etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etReview.setMinHeight(s.length() > 0 ? activity.getResources().getDimensionPixelSize(R.dimen.dp_60) : 0);
                etReview.setBackgroundResource(s.length() > 0 ? R.drawable.bg_white_r_b_new : R.drawable.bg_menu_item_selector_color_r_extra);
                tvReviewTextCount.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                tvReviewTextCount.setText(s.length() + "/500");
                tvSubmitReview.setText(s.length() > 0 ? R.string.submit_your_review : R.string.submit_your_rating);
            }
        });
        if (activity.getMenuProductsResponse() != null)
            setPhotos(activity.getMenuProductsResponse().getReviewImages());

        // my rating
        llMyReview.setVisibility(View.GONE);

        return rootView;
    }

    private Runnable enableRatingBarRunnable = new Runnable() {
        @Override
        public void run() {
            ratingBarReview.setEnabled(true);
        }
    };


    public void makeBold(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                activity.fragmentUISetup(this);
                if (userHasReviewed == 0 && activity.getVendorOpened().isHasRated()) {
                    userHasReviewed = 1;
                    setRatingUI();
                    fetchFeedback();
                } else if (activity.getVendorOpened().isUserJustEditedReview() && userHasReviewed == 1) {
                    activity.getVendorOpened().setUserJustEditedReview(false);
                    // refresh rating
                    fetchFeedback();
                }

                activity.tvCollapRestaurantDeliveryTime.setVisibility(View.GONE);
                activity.tvCollapRestaurantName.setVisibility(View.GONE);
                activity.llCollapseRating.setVisibility(View.GONE);


                if (getView() != null) {
                    scrollView.postDelayed(scrollToTopRunnable, 30);
                }

            }
        } catch (Exception e) {
        }
    }


    private Runnable scrollToTopRunnable = new Runnable() {
        @Override
        public void run() {
            if (getView() != null) {
                scrollView.scrollTo(0, 0);
            }
        }
    };

    void setMerchantInfoToUI() {
        try {
            if (activity.getMenuProductsResponse() != null) {
                tvMerchantPhone.setText(activity.getVendorOpened().getContactList());
                if (activity.updateCart) {
                    activity.updateCart = false;
                    activity.openCart();
                }

                setUpCollapseToolbarData();

                activity.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (Prefs.with(activity).getInt(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, -1) > 0) {
                            activity.openRestaurantReviewsListFragment();
                        }
                    }
                });


                tvMerchantName.setText(activity.getVendorOpened().getName());
                DeliveryHomeAdapter.setCuisines(activity.getVendorOpened(), activity, tvCuisines);
                if (activity.getVendorOpened().getRating() != null) {
                    String ratingText = product.clicklabs.jugnoo.utils.Utils.getDecimalFormat1Decimal().format(activity.getVendorOpened().getRating()) + " ";
                    tvReviewCount.setText(ratingText);
                    tvReviewCount.setVisibility(View.VISIBLE);
                    tvRestaurantRating.setText(ratingText);
                    tvRestaurantRating.setVisibility(View.VISIBLE);
                    ivStarRestaurantRating.setColorFilter(new PorterDuffColorFilter(Color.parseColor(activity.getVendorOpened().getColorCode())
                            , PorterDuff.Mode.SRC_IN));
                    ivStarRestaurantRating.setVisibility(View.VISIBLE);

                } else {
                    tvReviewCount.setVisibility(View.GONE);
                    tvRestaurantRating.setVisibility(View.GONE);
                    ivStarRestaurantRating.setVisibility(View.GONE);
                }

                tvlabelBullet.setText(activity.getString(R.string.bullet) + " ");
                tvOpensAt.setText(activity.getVendorOpened().getRestaurantTimingsStr());
                if (tvOpensAt.getText().length() == 0 && !TextUtils.isEmpty(activity.getVendorOpened().getNextOpenText())) {
                    tvOpensAt.setText(activity.getVendorOpened().getNextOpenText());
                } else {
                    tvlabelBullet.setText("");
                }

                setOpenCloseStateText(true);
                activity.getHandler().postDelayed(timerRunnable, 6000);

                if (activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_UNAVAILABLE || activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_CHAT
                        || activity.getVendorOpened().getOutOfRadius() == 1 || (TextUtils.isEmpty(activity.getVendorOpened().getDeliveryTimeText()) &&
                        TextUtils.isEmpty(activity.getVendorOpened().getMinOrderText()))) {
                    layoutOrderDetails.setVisibility(View.GONE);
                } else {


                    tvDeliversIn.setText(activity.getVendorOpened().getDeliveryTimeText());
                    tvMinOrderAmt.setText(activity.getVendorOpened().getMinOrderText());
                    viewCenterOrder.setVisibility(View.VISIBLE);
                    llMerchantMinOrderStrip.setVisibility(View.GONE);
                    layoutOrderDetails.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(activity.getVendorOpened().getDeliveryTimeText())) {
                           /* labelDeliversIn.setVisibility(View.GONE);
                            tvDeliversIn.setVisibility(View.GONE);
                            removeRule(RelativeLayout.ALIGN_PARENT_RIGHT, labelMinOrderAmt);
                            removeRule(RelativeLayout.ALIGN_PARENT_RIGHT, tvMinOrderAmt);*/

                        //show min Order Amount in a strip below phone details instead of position it in center
                        layoutOrderDetails.setVisibility(View.GONE);
                        tvMerchantMinOrder.setText(activity.getString(R.string.min_order_strip_merchant_info, activity.getVendorOpened().getMinOrderText()));
                        llMerchantMinOrderStrip.setVisibility(View.VISIBLE);


                    } else if (TextUtils.isEmpty(activity.getVendorOpened().getMinOrderText())) {
                        tvDeliversIn.setText(activity.getVendorOpened().getDeliveryTimeText());
                        labelMinOrderAmt.setVisibility(View.GONE);
                        tvMinOrderAmt.setVisibility(View.GONE);
                        viewCenterOrder.setVisibility(View.GONE);
                        removeRule(RelativeLayout.ALIGN_PARENT_START, labelDeliversIn);
                        removeRule(RelativeLayout.ALIGN_PARENT_START, tvDeliversIn);
                        layoutOrderDetails.setVisibility(View.VISIBLE);


                        //center align delivery text if min order amount is empty

                    }
                }

                if (activity.getMenuProductsResponse().getMenusPromotionInfo() != null && activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText() != null) {
                    tvOffer.setText(activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText());
                    llOffer.setVisibility(View.VISIBLE);
                } else {
                    llOffer.setVisibility(View.GONE);
                }


                String addressToSet;
                if (DeliveryHomeAdapter.getDistanceRestaurant(activity, activity.getVendorOpened()) != null) {
                    addressToSet = DeliveryHomeAdapter.getDistanceRestaurant(activity, activity.getVendorOpened()) + activity.getString(R.string.bullet) + " " + activity.getVendorOpened().getAddress();
                } else {
                    addressToSet = activity.getVendorOpened().getAddress();
                }
                tvMerchantAddress.setText(addressToSet);
                tvMerchantAddress.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getAddress()) ? View.VISIBLE : View.GONE);
                rvTopReviews.setVisibility(View.GONE);
                llSeeAll.setVisibility(View.GONE);
                fetchFeedback();


                ivChatNow.getDrawable().setColorFilter(activity.getVendorOpened().isChatModeEnabled() ? null : BW_FILTER);
                ivPay.getDrawable().setColorFilter(activity.getVendorOpened().getPayModeEnabled() ? null : BW_FILTER);
                if (activity.getVendorOpened().getPayModeEnabled()) {
                    llLocate.setVisibility(View.GONE);
                    llPay.setVisibility(View.VISIBLE);
                } else {
                    llLocate.setVisibility(View.VISIBLE);
                    llPay.setVisibility(View.GONE);
                }
               /* if (!activity.getVendorOpened().isChatModeEnabled()) {
                    ivChatNow.getDrawable().setColorFilter(BW_FILTER);
                } else {
                    ivChatNow.getDrawable().setColorFilter(null);
                }*/

                bOrderOnline.setBackgroundResource((activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0) ?
                        R.drawable.capsule_grey_dark_bg : R.drawable.capsule_theme_color_selector);
                bOrderOnline.setVisibility(activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_UNAVAILABLE ? View.GONE : View.VISIBLE);
//                dividerBelowDetails.setVisibility(activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_UNAVAILABLE ? View.GONE : View.VISIBLE);
                bOrderOnline.setText(activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_CHAT || activity.getVendorOpened().getOutOfRadius() == 1 ? R.string.action_order_via_fatafat : R.string.order_online);

                // decide whether to show out of radius strip
                if (activity.getVendorOpened().isOutOfRadiusStrip()) {

                    tvOutOfRadiusFatafatBanner.setVisibility(View.VISIBLE);
                    String subHeading = activity.getString(R.string.fatafat_banner_sub_heading);
                    tvOutOfRadiusFatafatBanner.setText(subHeading);
                } else {
                    tvOutOfRadiusFatafatBanner.setVisibility(View.GONE);
                }

                // hide the order online button if we have vendor menu in back ( direct vendor case)
                if(activity.getVendorMenuFragment()!=null){
                    bOrderOnline.setVisibility(View.GONE);
                }


            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void removeRule(int rule, View view) {


        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) layoutParams).addRule(rule, 0);
            view.setLayoutParams(layoutParams);
        }


    }


    private void setOpenCloseStateText(boolean firstTime) {
        try {
            if (DeliveryHomeAdapter.setRestaurantOpenStatus(activity, tvOpenStatus, activity.getVendorOpened(), false) == View.VISIBLE) {
                tvOpenStatus.setTextColor(ContextCompat.getColor(activity, R.color.red_dark_more));
            } else {
                tvOpenStatus.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
            }
        } catch (Exception e) {
            if (activity.getVendorOpened().getIsClosed() != null && activity.getVendorOpened().getIsAvailable() != null) {
                if (activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0) {
                    tvOpenStatus.setText(getString(R.string.closed)+" ");
                    tvOpenStatus.setTextColor(ContextCompat.getColor(activity, R.color.red_dark_more));
                } else {
                    tvOpenStatus.setText(getString(R.string.open_now)+" ");
                    tvOpenStatus.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                }
            } else {
                tvOpenStatus.setText("");
                tvlabelBullet.setText("");
            }
            e.printStackTrace();
        }
        if (firstTime || tvOpensAt.getText().length() > 0) {
            tvlabelBullet.setText(activity.getString(R.string.bullet) + " ");
        }
        // hide bullet text if opensAt is empty
        if(tvOpensAt.getText().toString().trim().isEmpty()){
            tvlabelBullet.setText("");
        }
    }

    private void setUpCollapseToolbarData() {
        if (activity.getVendorOpened() != null) {
            if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                Picasso.with(activity).load(activity.getVendorOpened().getImage())
                        .fit().centerCrop().placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(activity.ivCollapseRestImage);
            } else {
                activity.ivCollapseRestImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_fresh_item_placeholder));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeRunnable();
        unbinder.unbind();
    }

    @OnClick({R.id.llChatNow, R.id.tvMerchantPhone, R.id.bOrderOnline, R.id.llSeeAll, R.id.tvMerchantAddress,
            R.id.tvSubmitReview, R.id.llOffer, R.id.tvReviewCount, R.id.llEditReview, R.id.llLocate, R.id.llCall, R.id.llPay})
    public void onViewClicked(View view) {
        try {
            switch (view.getId()) {
                case R.id.llChatNow:
                    if (activity.getVendorOpened().isChatModeEnabled()) {

                        initiateMerchantChat();
//                        sendUserClickEvent(Constants.KEY_CHAT_MODE);
                    } else {
                        Utils.showToast(activity, activity.getString(R.string.chat_is_not_enabled_format, activity.getVendorOpened().getName()));
                    }
                    break;
                case R.id.llCall:
                    activity.callVendor();

                    break;
                case R.id.llPay:
                    if (activity.getVendorOpened().getPayModeEnabled()) {

//                        sendUserClickEvent(Constants.KEY_CHAT_MODE);
                    } else {
                        Utils.showToast(activity, activity.getString(R.string.pay_is_not_enabled_format, activity.getVendorOpened().getName()));
                    }
                    break;
                case R.id.tvMerchantPhone:
                    activity.callVendor();
                    sendUserClickEvent(Constants.KEY_CALL_MODE);
                    break;
                case R.id.bOrderOnline:
                    if (activity.getVendorOpened().getOrderMode() == Constants.ORDER_MODE_CHAT) {
                        if (!activity.isDeliveryOpenInBackground()) {
                            return;
                        }
                        if ((activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0)) {
                            return;
                        }

                        GAUtils.event(GACategory.FATAFAT3, GAAction.RESTAURANT_DETAIL, GAAction.LABEL_ORDER_VIA_FATAFAT);
                        activity.setOrderViaChatData(new FreshActivity.OrderViaChatData(activity.getVendorOpened().getLatLng(), activity.getVendorOpened().getAddress(), activity.getVendorOpened().getName(), activity.getVendorOpened().getRestaurantId()));
                        activity.switchOffering(Config.getFeedClientId());
                        return;
                    }

                    if (activity.getVendorOpened().getOutOfRadius() == 1 && (activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0)) {
                        return;
                    }


                    if (activity.getMenuProductsResponse().getCategories() != null
                            && activity.getVendorOpened().getRestaurantId().equals(activity.getMenuProductsResponse().getVendor().getRestaurantId())) {
                        activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                    } else {
                        activity.fetchRestaurantMenuAPI(activity.getVendorOpened().getRestaurantId(), false, null, null, -1, null);
                    }
                    sendUserClickEvent(Constants.KEY_ORDER_MODE);
                    break;
                case R.id.llSeeAll:
                    activity.openRestaurantReviewsListFragment();
                    break;
                case R.id.llLocate:
                case R.id.tvMerchantAddress:
                    sendUserClickEvent(Constants.KEY_LOCATE_MODE);
                    Utils.openMapsDirections(activity, activity.getVendorOpened().getLatLng());
                    break;
                case R.id.tvSubmitReview:
                    if (userHasReviewed == 0) {
                        String reviewText = etReview.getText().toString().trim();
                        if (reviewText.length() > 500) {
                            Utils.showToast(activity, activity.getString(R.string.feedback_must_be_in_500));
                            return;
                        }
                        if (ratingBarReview.getScore() <= 0) {
                            Utils.showToast(activity, getString(R.string.error_no_rating));
                            return;
                        }
                        uploadFeedback(reviewText);
                    } else {
                        Utils.showToast(activity, activity.getString(R.string.you_have_already_reviewed_format, activity.getVendorOpened().getName()));
                    }
                    break;

                case R.id.llOffer:
                    if (activity.getMenuProductsResponse() != null && activity.getMenuProductsResponse().getMenusPromotionInfo() != null) {
                        DialogPopup.alertPopupLeftOriented(activity, activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText(),
                                activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoTC(), true, true, true, true,
                                R.color.theme_color, 16, 13, Fonts.mavenMedium(activity));
                    }
                    break;

                case R.id.tvReviewCount:
                    activity.openRestaurantReviewsListFragment();
                    break;

                case R.id.llEditReview:
                    if (mUserReview != null) {
                        activity.setCurrentReview(mUserReview);
                        activity.openRestaurantAddReviewFragment(true, 0.0f);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int userHasReviewed = 0;
    private ArrayList<FetchFeedbackResponse.Review> restaurantReviews = new ArrayList<>();
    private ApiRestaurantFetchFeedback apiRestaurantFetchFeedback;

    public void fetchFeedback() {
        if (apiRestaurantFetchFeedback == null) {
            apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
                @Override
                public void onSuccess(FetchFeedbackResponse fetchFeedbackResponse, boolean scrollToTop) {
                    if (getView() != null) {
                        userHasReviewed = fetchFeedbackResponse.getHasAlreadyRated();

                        setRatingUI();
                        tvRateRestaurant.setText(activity.getString(R.string.rate_format, activity.getVendorOpened().getName()));

                        restaurantReviews.clear();
                        restaurantReviews.addAll(fetchFeedbackResponse.getReviews());
                        reviewsAdapter.notifyDataSetChanged();
                        if (restaurantReviews.size() == 0) {
                            tvNoReviews.setVisibility(View.VISIBLE);
                            rvTopReviews.setVisibility(View.GONE);
                            llSeeAll.setVisibility(View.GONE);
                        } else {
                            tvNoReviews.setVisibility(View.GONE);
                            rvTopReviews.setVisibility(View.VISIBLE);
                            llSeeAll.setVisibility(fetchFeedbackResponse.getReviewCount() > 2 ? View.VISIBLE : View.GONE);
                            if(fetchFeedbackResponse.getReviewCount()>2){
                            StringBuilder reviewBuilder = new StringBuilder();
                            reviewBuilder.append(activity.getResources().getString(R.string.see_all_reviews));
                            reviewBuilder.append(" (").append(String.valueOf(fetchFeedbackResponse.getReviewCount()))
                                    .append(")");
                            tvSeeAllReviews.setText(reviewBuilder.toString());
                            }
                        }

                        // set rating and reviews text
                        if(activity!=null && !activity.isFinishing() && userHasReviewed==0) {

                            // show text if ratings > 0
                            if(activity.getVendorOpened().getReviewCount()>0){
                                tvRatingReview.setVisibility(View.VISIBLE);
                                StringBuilder ratingReviewBuilder = new StringBuilder();
                                ratingReviewBuilder.append(activity.getVendorOpened().getReviewCount())
                                        .append(" ")
                                        .append(activity.getString(R.string.txt_ratings)).append(" ")
                                        .append(activity.getString(R.string.txt_and)).append(" ")
                                        .append(fetchFeedbackResponse.getReviewCount()).append(" ")
                                        .append(activity.getString(R.string.reviews));
                                tvRatingReview.setText(ratingReviewBuilder.toString().toLowerCase());

                            } else {
                                tvRatingReview.setVisibility(View.GONE);
                            }

                        }
                        if (fetchFeedbackResponse.getReviewImageLimit() != 0) {
                            activity.setReviewImageCount(fetchFeedbackResponse.getReviewImageLimit());
                        }
                        if (fetchFeedbackResponse.getRestaurantInfo() != null) {
                            if (activity.getVendorOpened() != null) {
                                activity.getVendorOpened().setRating(fetchFeedbackResponse.getRestaurantInfo().getRating());
                                activity.getVendorOpened().setReviewCount(fetchFeedbackResponse.getRestaurantInfo().getReviewCount());
                            }
                        }


                        // user review
                        if (fetchFeedbackResponse.getUserReview() != null && userHasReviewed == 1 && fetchFeedbackResponse.getUserReview().getRating() != null &&
                                fetchFeedbackResponse.getUserReview().getRating() != 0.0) {
                            mUserReview = fetchFeedbackResponse.getUserReview();
                            double rating = mUserReview.getRating();
                            activity.setRestaurantRatingStarsToLL(llRatingStars, tvRating,
                                    rating, R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey
                                    , null, 0, Color.parseColor(mUserReview.getColor()));
                        }


                    }
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onRetry(View view) {
                    fetchFeedback();
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        apiRestaurantFetchFeedback.hit(activity.getVendorOpened().getRestaurantId(), false, progressWheel, 1);
    }

    private void setRatingUI() {
        tvRateRestaurant.setVisibility(userHasReviewed == 1 ? View.GONE : View.VISIBLE);
        ratingBarReview.setVisibility(userHasReviewed == 1 ? View.GONE : View.VISIBLE);
        llRatingParent.setVisibility(userHasReviewed == 1 ? View.GONE : View.VISIBLE);
        vAddReviewSep.setVisibility(View.VISIBLE);
        llMyReview.setVisibility(userHasReviewed == 1 ? View.VISIBLE : View.GONE);
    }


    public void sendUserClickEvent(String eventName) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
        params.put(Constants.KEY_EVENT_TYPE, Constants.KEY_USER_CLICK);
        params.put(Constants.KEY_EVENT_STATUS, eventName);

        new ApiCommon<>(activity).showLoader(false).execute(params, ApiName.USER_CLICK_EVENTS,
                new APICommonCallback<FeedCommonResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return true;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        return true;
                    }

                    @Override
                    public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return true;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        return true;
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    private final static ColorMatrix BW_MATRIX = new ColorMatrix();
    private final static ColorMatrixColorFilter BW_FILTER;

    static {
        BW_MATRIX.setSaturation(0);
        BW_FILTER = new ColorMatrixColorFilter(BW_MATRIX);
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    private void uploadFeedback(final String reviewDesc) {

        final MultipartTypedOutput params = new MultipartTypedOutput();
        try {
            if (!MyApplication.getInstance().isOnline())
                return;

            if (Config.getLastOpenedClientId(activity).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getMenusClientId())) {
                Data.getMenusData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getDeliveryCustomerClientId())) {
                Data.getDeliveryCustomerData().setPendingFeedback(0);
            }


            params.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
            params.addPart(Constants.RATING_TYPE, new TypedString(Constants.RATING_TYPE_STAR));
            params.addPart(Constants.INTERATED, new TypedString("1"));


            int score = Math.round(ratingBarReview.getScore());
            if (score >= 1)
                params.addPart(Constants.RATING, new TypedString(String.valueOf(score)));


            if (!TextUtils.isEmpty(reviewDesc)) {
                params.addPart(Constants.KEY_REVIEW_DESC, new TypedString(reviewDesc));
            }


            if (activity.getVendorOpened().getRestaurantId() > 0) {
                params.addPart(Constants.KEY_RESTAURANT_ID, new TypedString(String.valueOf(activity.getVendorOpened().getRestaurantId())));
            }

            params.addPart(Constants.KEY_CLIENT_ID, new TypedString("" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())));

            Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                @Override
                public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                    DialogPopup.dismissLoadingDialog();
                    try {
                        if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {


                            if (activity.getCurrentReview() == null) {
                                if (!TextUtils.isEmpty(reviewDesc)) {
                                    GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED, GAAction.TEXT + GAAction.ADDED);
                                }


                                int score = Math.round(ratingBarReview.getScore());
                                if (score >= 1) {
                                    GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED + GAAction.RATING_ADDED, String.valueOf(score));

                                }


                                GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED, GAAction.FEED + GAAction.ADDED);

                            } else {
                                GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED, GAAction.FEED + GAAction.EDITED);

                            }


                            ratingBarReview.setScore(0f, true);
                            etReview.setText("");
                            fetchFeedback();

                            Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));
                            RestaurantReviewsListFragment frag = activity.getRestaurantReviewsListFragment();
                            if (frag != null) {
                                frag.fetchFeedback(true);
                            }
                        } else {
                            DialogPopup.alertPopup(activity, "", notificationInboxResponse.getMessage());
                        }
                    } catch (Exception e) {
                        DialogPopup.dismissLoadingDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                            new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View view) {
                                    uploadFeedback(reviewDesc);
                                }

                                @Override
                                public void neutralClick(View view) {

                                }

                                @Override
                                public void negativeClick(View view) {

                                }
                            });
                }
            };


            new HomeUtil().putDefaultParamsMultipart(params);
            if (activity.getCurrentReview() == null) {
                RestClient.getMenusApiService().orderFeedback(params, callback);
            } else {
                //Editing old review
                params.addPart(Constants.KEY_FEEDBACK_ID, new TypedString(activity.getCurrentReview().getFeedbackId() + ""));
                RestClient.getMenusApiService().editFeedback(params, callback);
            }

        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }




    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                setOpenCloseStateText(false);
                activity.getHandler().postDelayed(timerRunnable, 60000); //run every minute
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void removeRunnable() {
        activity.getHandler().removeCallbacks(timerRunnable);
    }

    private RestaurantReviewImagesAdapter imagesAdapter;

    public void setPhotos(final ArrayList<FetchFeedbackResponse.ReviewImage> vendorImages) {

        if (vendorImages != null && vendorImages.size() > 0) {

            layoutPhotos.setVisibility(View.VISIBLE);
            photosCount.setText(getString(R.string.photos_bracket_format, String.valueOf(vendorImages.size())));
            if (imagesAdapter == null) {
                imagesAdapter = new RestaurantReviewImagesAdapter(activity, null, vendorImages, true,
                        new RestaurantReviewImagesAdapter.Callback() {
                            @Override
                            public void onImageClick(int positionImageClicked, FetchFeedbackResponse.Review review) {
                                try {
                                    ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(positionImageClicked, vendorImages);
                                    dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewPhotos.setAdapter(imagesAdapter);
            } else {
                imagesAdapter.setList(null, vendorImages);
                recyclerViewPhotos.setAdapter(imagesAdapter);
            }
        } else {
            layoutPhotos.setVisibility(View.GONE);
        }

    }

    private CreateChatResponse fuguMerchantData;
    private boolean isAPIInProgress;

    public void initiateMerchantChat() {
        if (fuguMerchantData == null) {


            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.PAYER_USER_IDENTIFIER, Data.userData.userIdentifier);
            params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
            new ApiCommon<CreateChatResponse>(activity).execute(params, ApiName.CREATE_CHAT, new APICommonCallback<CreateChatResponse>() {
                @Override
                public boolean onNotConnected() {
                    return false;
                }

                @Override
                public boolean onException(Exception e) {
                    return false;
                }

                @Override
                public void onSuccess(CreateChatResponse createChatResponse, String message, int flag) {

                    fuguMerchantData = createChatResponse;
                    openMerchantChat();
                }

                @Override
                public boolean onError(CreateChatResponse createChatResponse, String message, int flag) {
                    return false;
                }

                @Override
                public boolean onFailure(RetrofitError error) {
                    return false;
                }

                @Override
                public void onNegativeClick() {

                }

                @Override
                public void onFinish() {
                    super.onFinish();

                }
            });

        } else {
            openMerchantChat();
        }


    }

    private void openMerchantChat() {
        if (fuguMerchantData != null && !TextUtils.isEmpty(fuguMerchantData.getChannelId()) && fuguMerchantData.getFuguData() != null) {
            /*FuguConfig.getInstance().openChatByTransactionId(fuguMerchantData.getChannelId(), String.valueOf(Data.getFuguUserData().getUserId()),
                    fuguMerchantData.getFuguData().getChannelName(), fuguMerchantData.getFuguData().getFuguTags());*/
            FuguConfig.getInstance().openChat(activity,
                    Long.parseLong(fuguMerchantData.getChannelId()));
        }
    }
}
