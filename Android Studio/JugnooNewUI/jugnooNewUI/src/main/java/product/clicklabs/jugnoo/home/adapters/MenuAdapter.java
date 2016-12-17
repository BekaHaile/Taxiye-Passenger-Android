package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ReferDriverActivity;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SelectorBitmapLoader;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

/**
 * Created by Ankit on 4/29/16.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity activity;
    private ArrayList<MenuInfo> menuList;
    private DrawerLayout drawerLayout;

    public MenuAdapter(ArrayList<MenuInfo> menuList, Activity activity, DrawerLayout drawerLayout) {
        this.menuList = menuList;
        this.activity = activity;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_account, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(585, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHeaderHolder(v, activity);
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(585, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        if(viewholder instanceof ViewHolder) {
            try {
                -- position;
                MenuInfo menuInfo = menuList.get(position);
                ViewHolder holder = (ViewHolder) viewholder;
                holder.relative.setTag(position);
                holder.textViewMenu.setText(menuInfo.getName());

                if(menuInfo.getIsNew() == 1){
                    holder.textViewNew.setVisibility(View.VISIBLE);
                } else{
                    holder.textViewNew.setVisibility(View.GONE);
                }

                holder.textViewValue.setVisibility(View.GONE);
                holder.textViewValue.setBackgroundResource(R.drawable.background_theme_rounded);
                LinearLayout.LayoutParams paramsP = (LinearLayout.LayoutParams) holder.textViewValue.getLayoutParams();
                paramsP.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                holder.textViewValue.setLayoutParams(paramsP);
                showLayout(holder.relative);

                if(MenuInfoTags.GAME.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageDrawable(getSelector(activity, R.drawable.ic_play_pressed, R.drawable.ic_play_normal));
                    try {
                        String icon = "";
                        if(!TextUtils.isEmpty(Data.userData.getGamePredictIconUrl())){
                            icon = Data.userData.getGamePredictIconUrl();
                        } else if(!TextUtils.isEmpty(menuInfo.getIcon())){
                            icon = menuInfo.getIcon();
                        }
                        if(!"".equalsIgnoreCase(icon)){
                            Picasso.with(activity)
                                    .load(icon)
                                    .placeholder(getSelector(activity, R.drawable.ic_play_pressed, R.drawable.ic_play_normal))
                                    .error(getSelector(activity, R.drawable.ic_play_pressed, R.drawable.ic_play_normal))
                                    .into(holder.imageViewMenuIcon);
                        } else if(menuInfo.getIconNormal() != null && menuInfo.getIconHighlighted() != null) {
                            new SelectorBitmapLoader(activity).loadSelector(holder.imageViewMenuIcon, menuInfo.getIconNormal(), menuInfo.getIconHighlighted(),
                                    new SelectorBitmapLoader.Callback() {
                                        @Override
                                        public void onSuccess(Drawable drawable) {

                                        }
                                    }, true);
                        }

                        if(!"".equalsIgnoreCase(Data.userData.getGamePredictName())) {
                            holder.textViewMenu.setText(Data.userData.getGamePredictName());
                        }
                        if(!"".equalsIgnoreCase(Data.userData.getGamePredictNew())){
                            holder.textViewNew.setVisibility(View.VISIBLE);
                            holder.textViewNew.setText(Data.userData.getGamePredictNew());
                        }
                        if(Data.userData.getGamePredictEnable() != 1
                                || "".equalsIgnoreCase(Data.userData.getGamePredictUrl())){
                            hideLayout(holder.relative);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.GET_A_RIDE.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_get_a_ride_selector);
                } else if(MenuInfoTags.JUGNOO_FRESH.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_fatafat_menu_selector);
                    Data.webActivityTitle = menuInfo.getName();
                }else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_share_selector);
                } else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_wallet_selector);
                    try {
                        holder.textViewValue.setText(String.format(activity.getResources()
                                        .getString(R.string.rupees_value_format_without_space),
                                Utils.getMoneyDecimalFormatWithoutFloat().format(Data.userData.getTotalWalletBalance())));
                        holder.textViewValue.setVisibility(View.VISIBLE);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.INBOX.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_inbox_selector);
                    try {
                        int unreadNotificationsCount = Prefs.with(activity).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
                        if(unreadNotificationsCount > 0){
                            holder.textViewValue.setVisibility(View.VISIBLE);
                            holder.textViewValue.setText(String.valueOf(unreadNotificationsCount));
                            holder.textViewValue.setBackgroundResource(R.drawable.circle_theme);
                            setLayoutParamsForValue(holder.textViewValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.OFFERS.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_promotion_selector);
                    try {
                        int couponsCount = Data.userData.getTotalCouponCount();
                        if(couponsCount > 0) {
                            holder.textViewValue.setVisibility(View.VISIBLE);
                            holder.textViewValue.setText(String.valueOf(couponsCount));
                            holder.textViewValue.setBackgroundResource(R.drawable.circle_theme);
                            setLayoutParamsForValue(holder.textViewValue);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_history_selector);

                } else if(MenuInfoTags.REFER_A_DRIVER.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_refer_a_driver_selector);
                   /* try {
                        if(Data.userData.getcToDReferralEnabled() != 1){
                            hideLayout(holder.relative);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_support_selector);
                } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_about_selector);
                } else{
                    hideLayout(holder.relative);
                }

                holder.relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int)v.getTag();
                            onClickAction(menuList.get(pos).getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(viewholder instanceof ViewHeaderHolder){
            final ViewHeaderHolder holder = (ViewHeaderHolder) viewholder;
            holder.textViewCategories.setText(R.string.categories);
            holder.imageViewArrow.setVisibility(View.VISIBLE);
            try {
                holder.textViewUserName.setText(Data.userData.userName);
                holder.textViewViewPhone.setText(Data.userData.phoneNo);
                float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                if(activity instanceof HomeActivity && ((HomeActivity)activity).activityResumed){
                    if(!"".equalsIgnoreCase(Data.userData.userImage)) {
                        Picasso.with(activity).load(Data.userData.userImage).transform(new CircleTransform())
                                .resize((int)(160f * minRatio), (int)(160f * minRatio)).centerCrop()
                                .into(holder.imageViewProfile);
                    }
                }
                else{
                    if(!"".equalsIgnoreCase(Data.userData.userImage)) {
                        Picasso.with(activity).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform())
                                .resize((int)(160f * minRatio), (int)(160f * minRatio)).centerCrop()
                                .into(holder.imageViewProfile);
                    }
                }
                holder.linearLayoutCategories.setVisibility(View.GONE);
                holder.linearLayoutSubCategories.setVisibility(View.GONE);
                holder.imageViewArrow.setRotation(270);
                setSubCategories(holder);

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.linearLayoutCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(!(activity instanceof HomeSwitcherActivity)) {
//                        LatLng currLatLng = new LatLng(Data.latitude, Data.longitude);
//                        if (activity instanceof HomeActivity) {
//                            currLatLng = ((HomeActivity) activity).getCurrentPlaceLatLng();
//                        } else if (activity instanceof FreshActivity) {
//                            currLatLng = ((FreshActivity) activity).getCurrentPlaceLatLng();
//                        }
//                        final LatLng finalCurrLatLng = currLatLng;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                MyApplication.getInstance().getAppSwitcher().switchApp(activity,
//                                        Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
//                                        activity.getIntent().getData(), finalCurrLatLng, true);
//                            }
//                        }, 500);


                    if(holder.linearLayoutSubCategories.getVisibility() == View.VISIBLE){
                        holder.linearLayoutSubCategories.setVisibility(View.GONE);
                        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fab_scale_down);
                        holder.imageViewArrow.setRotation(270);
                        //holder.linearLayoutCategories.startAnimation(animation);
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES, null);
                    } else {
                        holder.linearLayoutSubCategories.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fab_scale_up);
                        holder.imageViewArrow.setRotation(90);
                        //holder.linearLayoutCategories.startAnimation(animation);
                    }

                        //notifyItemChanged(0);
//                    }
//                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountClick();
                }
            });

            holder.linearLayoutSubAutos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.GET_A_RIDE.getTag());
                    holder.imageViewArrow.setRotation(270);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_AUTOS, new Bundle());
                }
            });

            holder.linearLayoutSubFresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.FRESH.getTag());
                    holder.imageViewArrow.setRotation(270);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_FRESH, new Bundle());
                }
            });

            holder.linearLayoutSubMeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.MEALS.getTag());
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    holder.imageViewArrow.setRotation(270);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_MEALS, new Bundle());
                }
            });

            holder.linearLayoutSubDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    holder.imageViewArrow.setRotation(270);
                }
            });

            holder.linearLayoutSubGrocery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.GROCERY.getTag());
                    holder.imageViewArrow.setRotation(270);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_GROCERY, new Bundle());
                }
            });

            holder.linearLayoutSubMenus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.MENUS.getTag());
                    holder.imageViewArrow.setRotation(270);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_MENUS, new Bundle());
                }
            });

            holder.linearLayoutSubPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.PAY.getTag());
                    holder.imageViewArrow.setRotation(270);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    MyApplication.getInstance().logEvent(FirebaseEvents.MENU_CATEGORIES_PAY, new Bundle());
                }
            });
        }

    }

    private LatLng getLatLng(){
        LatLng latLng = new LatLng(Data.latitude, Data.longitude);
        if(activity instanceof HomeActivity){
            latLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
        } else if(activity instanceof FreshActivity){
            latLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
        }
        return latLng;
    }

    private void hideLayout(View view){
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.height = (int)(0f);
        view.setLayoutParams(params);
    }

    private void showLayout(View view){
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
        params.height = (int)(minRatio * 81f);
        view.setLayoutParams(params);
    }

    private void setLayoutParamsForValue(TextView textView){
        LinearLayout.LayoutParams paramsP = (LinearLayout.LayoutParams) textView.getLayoutParams();
        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
        paramsP.width = (int)(minRatio * 56f);
        paramsP.height = (int)(minRatio * 56f);
        textView.setLayoutParams(paramsP);
    }

    @Override
    public int getItemCount() {
        if(menuList == null || menuList.size() == 0){
            return 0;
        }
        else{
            return menuList.size()+1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void onClickAction(final String tag){
        try {
            if(MenuInfoTags.GAME.getTag().equalsIgnoreCase(tag)){
                if (Data.userData.getGamePredictEnable() == 1) {
                    Intent intent = new Intent(activity, T20Activity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    Bundle bundle = new Bundle();
                    String gameName = "";
                    for(MenuInfo menuInfo : menuList){
                        if(menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.GAME.getTag())){
                            gameName = menuInfo.getName();
                            break;
                        }
                    }
                    gameName = gameName.replaceAll("\\W", "_");
                    MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.GAME+"_"+gameName, bundle);
                    FlurryEventLogger.event(FlurryEventNames.WORLD_CUP_MENU);
                    FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "game");
                }
            } else if((MenuInfoTags.GET_A_RIDE.getTag().equalsIgnoreCase(tag))) {
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), getLatLng(), false);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE + "_" + FirebaseEvents.MENU + "_" + FirebaseEvents.GET_A_RIDE, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "Get a Ride");
            } else if(MenuInfoTags.JUGNOO_FRESH.getTag().equalsIgnoreCase(tag)){
                if(activity instanceof HomeActivity) {
//                    if(1 == Data.freshAvailable) {
//                        if (((HomeActivity) activity).map != null
//                                && ((HomeActivity)activity).mapStateListener != null
//                                && ((HomeActivity)activity).mapStateListener.isMapSettled()) {
//                            Data.latitude = ((HomeActivity) activity).map.getCameraPosition().target.latitude;
//                            Data.longitude = ((HomeActivity) activity).map.getCameraPosition().target.longitude;
//                        }
//                        try {
//                            if(!Data.userData.getFatafatUrlLink().trim().equalsIgnoreCase("")) {
//                                Log.v("fatafat url link", "---> " + Data.userData.getFatafatUrlLink());
//                                activity.startActivity(new Intent(activity, WebActivity.class));
//                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                            } else{
//                                CustomAppLauncher.launchApp(activity, AccessTokenGenerator.FATAFAT_FRESH_PACKAGE);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            CustomAppLauncher.launchApp(activity, AccessTokenGenerator.FATAFAT_FRESH_PACKAGE);
//                        }
//                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_JUGNOO_FRESH_CLICKED, null);
//                        Bundle bundle = new Bundle();
//                        MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.FRESH, bundle);
//                        FlurryEventLogger.eventGA(Constants.REVENUE+Constants.SLASH+Constants.ACTIVATION+Constants.SLASH+Constants.RETENTION, "Home Screen", "fresh");
//                    }
                }
            } else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, ShareActivity.class);
                intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.FREE_RIDES, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "Free rides");

            } else if(MenuInfoTags.REFER_A_DRIVER.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, ReferDriverActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                //MyApplication.getInstance().logEvent(FirebaseEvents.Refer_a_driver);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.REFER_A_DRIVER, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "Refer a driver");
            }else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.WALLET_MENU);
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_WALLET);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.WALLET, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "wallet");

            } else if(MenuInfoTags.INBOX.getTag().equalsIgnoreCase(tag)){
                LatLng currLatLng = null;
                if(activity instanceof HomeActivity){
                    currLatLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
                } else if(activity instanceof FreshActivity){
                    currLatLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
                }

                if(currLatLng != null){
                    Data.latitude = currLatLng.latitude;
                    Data.longitude = currLatLng.longitude;
                }
                activity.startActivity(new Intent(activity, NotificationCenterActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.NOTIFICATION_ICON);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.INBOX, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "inbox");

            }else if(MenuInfoTags.OFFERS.getTag().equalsIgnoreCase(tag)) {
                LatLng currLatLng = null;
                if (activity instanceof HomeActivity) {
                    currLatLng = ((HomeActivity) activity).getCurrentPlaceLatLng();
                } else if(activity instanceof FreshActivity){
                    currLatLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
                }
                if (currLatLng != null) {
                    Data.latitude = currLatLng.latitude;
                    Data.longitude = currLatLng.longitude;
                }
                if (AppStatus.getInstance(activity).isOnline(activity)) {
                    activity.startActivity(new Intent(activity, PromotionActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PROMOTIONS_SCREEN);
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE + "_" + FirebaseEvents.MENU + "_" + FirebaseEvents.PROMOTION, bundle);
                    FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "promotion");
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    onClickAction(tag);
                                }

                                @Override
                                public void neutralClick(View v) {

                                }

                                @Override
                                public void negativeClick(View v) {

                                }
                            });
                }

            } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(tag)) {
                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.RIDE_HISTORY);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE + "_" + FirebaseEvents.MENU + "_" + FirebaseEvents.RIDE_HISTORY, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "Ride History");
            } else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(tag)) {
                activity.startActivity(new Intent(activity, SupportActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE + "_" + FirebaseEvents.MENU + "_" + FirebaseEvents.SUPPORT, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "Support");
            } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.MENU+"_"+FirebaseEvents.ABOUT, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, "menu", "About");
            }
            else if(MenuInfoTags.FRESH.getTag().equalsIgnoreCase(tag)){
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId(), getLatLng(), false);
            }
            else if(MenuInfoTags.MEALS.getTag().equalsIgnoreCase(tag)){
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId(), getLatLng(), false);
            }
            else if(MenuInfoTags.GROCERY.getTag().equalsIgnoreCase(tag)){
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getGroceryClientId(), getLatLng(), false);
            }
            else if(MenuInfoTags.MENUS.getTag().equalsIgnoreCase(tag)){
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMenusClientId(), getLatLng(), false);
            }
            else if(MenuInfoTags.PAY.getTag().equalsIgnoreCase(tag)){
                drawerLayout.closeDrawer(GravityCompat.START);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getPayClientId(), getLatLng(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accountClick(){
        activity.startActivity(new Intent(activity, AccountActivity.class));
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_ACCOUNT);
    }

    private void setSubCategories(ViewHeaderHolder holder){
        try {
            if(Data.userData.getIntegratedJugnooEnabled() == 1) {
                if ((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                        && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0)
                        && (Data.userData.getMenusEnabled() == 0) && (Data.userData.getPayEnabled() == 0)) {
                    holder.linearLayoutCategories.setVisibility(View.GONE);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                } else {
                    holder.linearLayoutCategories.setVisibility(View.VISIBLE);
                    //holder.linearLayoutSubCategories.setVisibility(View.VISIBLE);
                    if (Data.userData.getFreshEnabled() == 1) {
                        holder.linearLayoutSubFresh.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubFresh.setVisibility(View.GONE);
                    }

                    if (Data.userData.getMealsEnabled() == 1) {
                        holder.linearLayoutSubMeals.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubMeals.setVisibility(View.GONE);
                    }

                    if (Data.userData.getDeliveryEnabled() == 1) {
                        holder.linearLayoutSubDelivery.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubDelivery.setVisibility(View.GONE);
                    }

                    if(Data.userData.getGroceryEnabled() == 1){
                        holder.linearLayoutSubGrocery.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubGrocery.setVisibility(View.GONE);
                    }

                    if(Data.userData.getMenusEnabled() == 1){
                        holder.linearLayoutSubMenus.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubMenus.setVisibility(View.GONE);
                    }

                    if(Data.userData.getPayEnabled() == 1){
                        holder.linearLayoutSubPay.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubPay.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMenu, textViewNew, textViewValue;
        public ImageView imageViewMenuIcon;
        public RelativeLayout relative;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewMenu = (TextView) convertView.findViewById(R.id.textViewMenu); textViewMenu.setTypeface(Fonts.mavenRegular(context));
            textViewNew = (TextView) convertView.findViewById(R.id.textViewNew); textViewNew.setTypeface(Fonts.mavenRegular(context));
            textViewValue = (TextView) convertView.findViewById(R.id.textViewValue); textViewValue.setTypeface(Fonts.mavenRegular(context));
            imageViewMenuIcon = (ImageView) convertView.findViewById(R.id.imageViewMenuIcon);
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
        }
    }


    public class ViewHeaderHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewProfile, imageViewArrow;
        public TextView textViewUserName, textViewViewPhone, textViewCategories, textViewAutos, textViewFresh, textViewMeals, textViewDelivery,
                textViewGrocery, textViewMenus, textViewPay;
        public LinearLayout linearLayoutCategories, linearLayoutSubCategories, linearLayoutSubDelivery, linearLayoutSubMeals, linearLayoutSubFresh, linearLayoutSubAutos,
            linearLayoutSubGrocery, linearLayoutSubMenus, linearLayoutSubPay;
        public ViewHeaderHolder(View convertView, Activity context) {
            super(convertView);
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            imageViewProfile = (ImageView) convertView.findViewById(R.id.imageViewProfile);//textViewUserName
            textViewUserName = (TextView) convertView.findViewById(R.id.textViewUserName); textViewUserName.setTypeface(Fonts.avenirNext(context));
            textViewViewPhone = (TextView) convertView.findViewById(R.id.textViewViewPhone); textViewViewPhone.setTypeface(Fonts.avenirNext(context));
            textViewCategories = (TextView) convertView.findViewById(R.id.textViewCategories); textViewCategories.setTypeface(Fonts.mavenRegular(context));
            textViewAutos = (TextView) convertView.findViewById(R.id.textViewAutos); textViewAutos.setTypeface(Fonts.mavenRegular(context));
            textViewFresh = (TextView) convertView.findViewById(R.id.textViewFresh); textViewFresh.setTypeface(Fonts.mavenRegular(context));
            textViewMeals = (TextView) convertView.findViewById(R.id.textViewMeals); textViewMeals.setTypeface(Fonts.mavenRegular(context));
            textViewDelivery = (TextView) convertView.findViewById(R.id.textViewDelivery); textViewDelivery.setTypeface(Fonts.mavenRegular(context));
            textViewGrocery = (TextView) convertView.findViewById(R.id.textViewGrocery); textViewGrocery.setTypeface(Fonts.mavenRegular(context));
            textViewMenus = (TextView) convertView.findViewById(R.id.textViewMenus); textViewMenus.setTypeface(Fonts.mavenRegular(context));
            textViewPay = (TextView) convertView.findViewById(R.id.textViewPay); textViewPay.setTypeface(Fonts.mavenRegular(context));
            linearLayoutCategories = (LinearLayout) convertView.findViewById(R.id.linearLayoutCategories);
            linearLayoutSubCategories = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubCategories);
            imageViewArrow = (ImageView) convertView.findViewById(R.id.imageViewArrow);
            linearLayoutSubAutos = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubAutos);
            linearLayoutSubFresh = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubFresh);
            linearLayoutSubMeals = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubMeals);
            linearLayoutSubDelivery = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubDelivery);
            linearLayoutSubGrocery = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubGrocery);
            linearLayoutSubMenus = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubMenus);
            linearLayoutSubPay = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubPay);
        }
    }

    public StateListDrawable getSelector(Context context, int pressed, int normal){
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                context.getResources().getDrawable(pressed));
        stateListDrawable.addState(new int[]{},
                context.getResources().getDrawable(normal));
        return stateListDrawable;
    }

}
