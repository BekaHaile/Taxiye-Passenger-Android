package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.activities.MainActivity;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.BlockedDriversActivity;
import product.clicklabs.jugnoo.ChangeLanguageActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareDetailsActivity;
import product.clicklabs.jugnoo.FavouriteDriversActivity;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ReferDriverActivity;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.adapters.BlockedDriversAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.credits.SendCreditsToCustomer;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.schedulerides.UpcomingRidesActivity;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.SupportMailActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.tutorials.NewUserFlow;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SelectorBitmapLoader;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

/**
 * Created by Ankit on 4/29/16.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GAAction, GACategory{

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

    public void setList(ArrayList<MenuInfo> menuInfos){
        this.menuList = menuInfos;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_account, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(585, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHeaderHolder(v, activity);
        }

        else{
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
                if(position==menuList.size()-1){
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolder) viewholder).relative.getLayoutParams();
                    params.bottomMargin = (int)(activity.getResources().getDimensionPixelSize(R.dimen.dp_15));
                    ((ViewHolder) viewholder).relative.setLayoutParams(params);
                }else{
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolder) viewholder).relative.getLayoutParams();
                    params.bottomMargin = 0;
                    ((ViewHolder) viewholder).relative.setLayoutParams(params);
                }

                MenuInfo menuInfo = menuList.get(position);
                ViewHolder holder = (ViewHolder) viewholder;
                holder.relative.setTag(position);
                holder.textViewMenu.setText(menuInfo.getName());
                holder.imageViewMenuIcon.setPadding(0, 0, 0, 0);

                if(menuInfo.getIsNew() == 1){
                    holder.textViewNew.setVisibility(View.VISIBLE);
                } else{
                    holder.textViewNew.setVisibility(View.GONE);
                }

                holder.textViewValue.setVisibility(View.GONE);
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
                }
                else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(menuInfo.getTag())
						||MenuInfoTags.FREE_RIDES_NEW.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_share_selector);
                }
                else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_wallet_selector);
                    try {
                        holder.textViewValue.setText(!activity.getResources().getBoolean(R.bool.wallet_amount_sidemenu)? Utils.getMoneyDecimalFormatWithoutFloat().format(Data.userData.getTotalWalletBalance()):String.format(activity.getResources()
                                .getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormatWithoutFloat().format(Data.userData.getTotalWalletBalance())));
                        holder.textViewValue.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.INBOX.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_inbox_selector);
                    try {
                        int unreadNotificationsCount = Prefs.with(activity).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
                        if(unreadNotificationsCount > 0){
                            holder.textViewValue.setVisibility(View.VISIBLE);
                            holder.textViewValue.setText(String.valueOf(unreadNotificationsCount));
                   /*         holder.textViewValue.setBackgroundResource(R.drawable.background_theme_rounded);
                            setLayoutParamsForValue(holder.textViewValue);*/
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
                           /* holder.textViewValue.setBackgroundResource(R.drawable.background_theme_rounded);
                            setLayoutParamsForValue(holder.textViewValue);*/
						}

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_history_selector);
                }  else if(MenuInfoTags.SCHEDULED_RIDES.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_history_selector);
                } else if(MenuInfoTags.SIGNUP_TUTORIAL.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    if(!TextUtils.isEmpty(Data.userData.userName)
                            //&& (!TextUtils.isEmpty(Data.userData.userEmail))
                            && Prefs.with(activity).getInt(SPLabels.USERNAME_UPDATED, 0) == 1){
                        hideLayout(holder.relative);
                    } else {
                        holder.imageViewMenuIcon.setImageResource(R.drawable.ic_free_discount_selector);
                    }
                } else if(MenuInfoTags.JUGNOO_STAR.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_jugnoo_star_selector);
                    if(!Data.userData.isSubscriptionActive() && Data.userData.getSubscriptionData().getSubscribedUser() == 0){
                        holder.textViewNew.setVisibility(View.VISIBLE);
                    } else{
                        holder.textViewNew.setVisibility(View.GONE);
                    }
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
                } else if(MenuInfoTags.CHANGE_LOCALE.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_support_selector);
                } else if(MenuInfoTags.CALL_SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_call_grey_theme_selector);
                } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_about_selector);
                }else if(MenuInfoTags.FUGU_SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_jugnoo_chat_selector);
                }
                else if(MenuInfoTags.TICKET_SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_jugnoo_chat_selector);
                }
                else if(MenuInfoTags.EMAIL_SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_jugnoo_chat_selector);

                }else if(MenuInfoTags.FARE_DETAILS.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_fare_details_grey_theme_selector);
                    int padding = activity.getResources().getDimensionPixelSize(R.dimen.dp_4);
                    holder.imageViewMenuIcon.setPadding(padding, padding, padding, padding);

                }else if(MenuInfoTags.FAVOURITE_DRIVERS.getTag().equalsIgnoreCase(menuInfo.getTag())){

                }else if(MenuInfoTags.BLOCKED_DRIVERS.getTag().equalsIgnoreCase(menuInfo.getTag())){

                }
                else if(MenuInfoTags.CUSTOMER_TO_CUSTOMER.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_fare_details_grey_theme_selector);
                    int padding = activity.getResources().getDimensionPixelSize(R.dimen.dp_4);
                    holder.imageViewMenuIcon.setPadding(padding, padding, padding, padding);
                }
                else{
                    hideLayout(holder.relative);
                }


                holder.relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int)v.getTag();
                            String tag = menuList.get(pos).getTag();
                            onClickAction(tag,activity,getLatLng());
                            if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(tag)){
                                GAUtils.event(SIDE_MENU, FREE_GIFT+CLICKED, "");
                            } else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(tag)){
                                GAUtils.event(SIDE_MENU, WALLET+CLICKED, "");
                            } else if(MenuInfoTags.INBOX.getTag().equalsIgnoreCase(tag)){
                                GAUtils.event(SIDE_MENU, INBOX+CLICKED, "");
                            } else if(MenuInfoTags.OFFERS.getTag().equalsIgnoreCase(tag)) {
                                GAUtils.event(SIDE_MENU, PROMOTIONS+CLICKED, "");
                            } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(tag)) {
                                GAUtils.event(SIDE_MENU, HISTORY+CLICKED, "");
                            } else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(tag)) {
                                GAUtils.event(SIDE_MENU, SUPPORT+CLICKED, "");
                            } else if(MenuInfoTags.JUGNOO_STAR.getTag().equalsIgnoreCase(tag)){
                                GAUtils.event(SIDE_MENU, JUGNOO+STAR+CLICKED, "");
                            }
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

            try {

                holder.linearLayoutCategories.setVisibility(View.GONE);
                holder.linearLayoutSubCategories.setVisibility(View.GONE);
                holder.imageViewArrow.setVisibility(View.VISIBLE);
                holder.imageViewArrow.setRotation(270);
                setSubCategories(holder);

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.linearLayoutCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.linearLayoutSubCategories.getVisibility() == View.VISIBLE){
                        holder.linearLayoutSubCategories.setVisibility(View.GONE);
                        holder.imageViewArrow.setRotation(270);
                    } else {
                        holder.linearLayoutSubCategories.setVisibility(View.VISIBLE);
                        holder.imageViewArrow.setRotation(90);
                        GAUtils.event(SIDE_MENU, CATEGORY+EXPANDED, "");
                    }
                }
            });



            showLayoutOfferings(holder.linearLayoutSubAutos);
            showLayoutOfferings(holder.linearLayoutSubFeed);
            showLayoutOfferings(holder.linearLayoutSubFresh);
            showLayoutOfferings(holder.linearLayoutSubGrocery);
            showLayoutOfferings(holder.linearLayoutSubMenus);
            showLayoutOfferings(holder.linearLayoutSubPay);
            showLayoutOfferings(holder.linearLayoutSubMeals);
            showLayoutOfferings(holder.linearLayoutSubPros);
            showLayoutOfferings(holder.linearlayoutDeliveryCustomer);
            holder.linearLayoutSubAutos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.GET_A_RIDE.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                  //  holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, RIDES);
                }
            });

            holder.linearLayoutSubFresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.FRESH.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                 //   holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, FRESH);
                }
            });

            holder.linearLayoutSubMeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.MEALS.getTag(),activity,getLatLng());
                //    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    holder.imageViewArrow.setRotation(270);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, MEALS);
                }
            });


            holder.linearLayoutSubGrocery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.GROCERY.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                //    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, FRESH);
                }
            });

            holder.linearLayoutSubMenus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.MENUS.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                 //   holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, GAAction.MENUS);
                }
            });

            holder.linearlayoutDeliveryCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.DELIVERY_CUSTOMER.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                 //   holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, GACategory.DELIVERY_CUSTOMER);
                }
            });

            holder.linearLayoutSubPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.PAY.getTag(),activity,getLatLng());
                    holder.imageViewArrow.setRotation(270);
                  //  holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    GAUtils.event(JUGNOO, PAY+HOME, LEFT_MENU_ICON+CLICKED);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, PAY);
                }
            });

            holder.linearLayoutSubFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.FEED.getTag(),activity,getLatLng());
                 //   holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    holder.imageViewArrow.setRotation(270);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, FEED);
                }
            });

            holder.linearLayoutSubPros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAction(MenuInfoTags.PROS.getTag(),activity,getLatLng());
                    //    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                    holder.imageViewArrow.setRotation(270);
                    GAUtils.event(SIDE_MENU, CATEGORY+CLICKED, PROS);
                }
            });
        }

    }

    private LatLng getLatLng(){
        LatLng latLng = new LatLng(Data.latitude, Data.longitude);
        if(activity instanceof HomeActivity){
            latLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
        } else if(activity instanceof FreshActivity){
            latLng = ((FreshActivity)activity).getSelectedLatLng();
        } else if(activity instanceof MainActivity){
            latLng = ((MainActivity)activity).getCurrentPlaceLatLng();
        } else if(activity instanceof HomeSwitcherActivity){
            latLng = ((HomeSwitcherActivity)activity).getCurrentPlaceLatLng();
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

    private void showLayoutOfferings(View view){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
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

    public static void onClickAction(final String tag,Activity activity,LatLng latLng){
        onClickAction(tag, 0, 0,activity,latLng);
    }

    public static void onClickAction(final String tag, final int orderId, final int productType, final Activity activity, final LatLng  latLng){
        try {
            if(MenuInfoTags.GAME.getTag().equalsIgnoreCase(tag)){
                if (Data.userData.getGamePredictEnable() == 1) {
                    Intent intent = new Intent(activity, T20Activity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

                }
            } else if((MenuInfoTags.GET_A_RIDE.getTag().equalsIgnoreCase(tag))) {
                closeDrawerIfOpen(activity);
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), latLng, false);
            } else if(MenuInfoTags.JUGNOO_FRESH.getTag().equalsIgnoreCase(tag)){

            }
            else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, ShareActivity.class);
                intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(MenuInfoTags.FREE_RIDES_NEW.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, ShareActivity.class);
				intent.putExtra(Constants.KEY_IS_NEW_REFERRAL, true);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(MenuInfoTags.REFER_A_DRIVER.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, ReferDriverActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                //MyApplication.getInstance().logEvent(FirebaseEvents.Refer_a_driver);
            }else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            } else if(MenuInfoTags.INBOX.getTag().equalsIgnoreCase(tag)){
                LatLng currLatLng = null;
                if(activity instanceof HomeActivity){
                    currLatLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
                } else if(activity instanceof FreshActivity){
                    currLatLng = ((FreshActivity)activity).getSelectedLatLng();
                }

                if(currLatLng != null){
                    Data.latitude = currLatLng.latitude;
                    Data.longitude = currLatLng.longitude;
                }
                activity.startActivity(new Intent(activity, NotificationCenterActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            }else if(MenuInfoTags.OFFERS.getTag().equalsIgnoreCase(tag)) {
                LatLng currLatLng = latLng;

                if (currLatLng != null) {
                    Data.latitude = currLatLng.latitude;
                    Data.longitude = currLatLng.longitude;
                }
                if (MyApplication.getInstance().isOnline()) {
                    activity.startActivity(new Intent(activity, PromotionActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    onClickAction(tag,orderId,productType,activity,latLng);
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
                intent.putExtra(Constants.KEY_ORDER_ID, orderId);
                intent.putExtra(Constants.KEY_PRODUCT_TYPE, productType);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            }else if(MenuInfoTags.SCHEDULED_RIDES.getTag().equalsIgnoreCase(tag)) {
                Intent intent = new Intent(activity, UpcomingRidesActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            } else if(MenuInfoTags.SIGNUP_TUTORIAL.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, NewUserFlow.class);
                intent.putExtra(Constants.KEY_MENU_SIGNUP_TUTORIAL, true);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(tag)) {
                activity.startActivity(new Intent(activity, SupportActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            }
            else if(MenuInfoTags.CHANGE_LOCALE.getTag().equalsIgnoreCase(tag)) {
                activity.startActivity(new Intent(activity, ChangeLanguageActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

            } else if (MenuInfoTags.CALL_SUPPORT.getTag().equalsIgnoreCase(tag)) {
                Utils.openCallIntent(activity, Prefs.with(activity).getString(Constants.KEY_CUSTOMER_SUPPORT_NUMBER, ""));
            } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            } else if(MenuInfoTags.JUGNOO_STAR.getTag().equalsIgnoreCase(tag)){
                if((Data.userData.getSubscriptionData().getSubscribedUser() != null && Data.userData.getSubscriptionData().getSubscribedUser() == 1)
                        || Data.userData.isSubscriptionActive()){
                    activity.startActivity(new Intent(activity, JugnooStarSubscribedActivity.class));
                } else {
                    activity.startActivity(new Intent(activity, JugnooStarActivity.class));
                }
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(MenuInfoTags.FUGU_SUPPORT.getTag().equalsIgnoreCase(tag)){
                if(activity instanceof BaseAppCompatActivity){
                    ((BaseAppCompatActivity)activity).openFugu();
                }
            }
            else if(MenuInfoTags.TICKET_SUPPORT.getTag().equalsIgnoreCase(tag)){
            	HomeUtil.openHippoTicketSupport(activity);
            }
            else if(MenuInfoTags.EMAIL_SUPPORT.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, SupportMailActivity.class));
            }
            else if(MenuInfoTags.FARE_DETAILS.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, FareDetailsActivity.class)
                        .putExtra(Constants.KEY_LATITUDE, latLng.latitude)
                        .putExtra(Constants.KEY_LONGITUDE, latLng.longitude));
            }
            else if(MenuInfoTags.FRESH.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getFreshClientId(), activity,latLng);
            }
            else if(MenuInfoTags.MEALS.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getMealsClientId(), activity,latLng);
            }
            else if(MenuInfoTags.GROCERY.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getGroceryClientId(), activity,latLng);
            }
            else if(MenuInfoTags.MENUS.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getMenusClientId(), activity,latLng);
            }
            else if(MenuInfoTags.DELIVERY_CUSTOMER.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getDeliveryCustomerClientId(), activity,latLng);
            }
            else if(MenuInfoTags.PAY.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getPayClientId(), activity,latLng);
            }
            else if(MenuInfoTags.FEED.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getFeedClientId(), activity,latLng);
            }
            else if(MenuInfoTags.PROS.getTag().equalsIgnoreCase(tag)){
                openOffering(Config.getProsClientId(), activity,latLng);
            }
            else if(MenuInfoTags.FAVOURITE_DRIVERS.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, FavouriteDriversActivity.class));
            }
            else if(MenuInfoTags.BLOCKED_DRIVERS.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, BlockedDriversActivity.class));
            }
            else if(MenuInfoTags.CUSTOMER_TO_CUSTOMER.getTag().equalsIgnoreCase(tag)){
                openCustomerToCustomerWallet(Config.getProsClientId(), activity,latLng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openCustomerToCustomerWallet(final String clientId, final Activity activity, final LatLng latLng) {
        Intent intent = new Intent(activity, SendCreditsToCustomer.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private Handler handler;
    private Handler getHandler(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }
    private static void openOffering(final String clientId, final Activity activity, final LatLng latLng){
        closeDrawerIfOpen(activity);
        if(clientId.equals(Config.DELIVERY_CUSTOMER_CLIENT_ID) && activity instanceof FreshActivity &&
                ((FreshActivity)activity).currentOpenClientIdForFab().equals(Config.DELIVERY_CUSTOMER_CLIENT_ID)){
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, clientId, latLng, false);
            }
        }, 200);
    }

    public static void closeDrawerIfOpen(Activity activity) {
        if(activity instanceof FreshActivity){
            ((FreshActivity) activity).getMenuBar().getDrawerLayout().closeDrawer(GravityCompat.START);
        }else if(activity instanceof HomeActivity){
            ((HomeActivity) activity).getMenuBar().getDrawerLayout().closeDrawer(GravityCompat.START);
        }
    }

    public static void accountClick(Activity activity){
        activity.startActivity(new Intent(activity, AccountActivity.class));
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private void setSubCategories(ViewHeaderHolder holder){
        try {

                if (Data.userData.getAutosEnabled() == 0 && Data.userData.getFreshEnabled() == 0
                        && Data.userData.getMealsEnabled() == 0
                        && Data.userData.getDeliveryEnabled() == 0
                        && Data.userData.getGroceryEnabled() == 0
                        && Data.userData.getMenusEnabled() == 0
                        && Data.userData.getDeliveryCustomerEnabled() == 0
                        && Data.userData.getPayEnabled() == 0
                        && Data.userData.getFeedEnabled() == 0
                        && Data.userData.getProsEnabled() == 0 || (Data.userData.getIntegratedJugnooEnabled() == 0)) {
                    holder.linearLayoutCategories.setVisibility(View.GONE);
                    holder.linearLayoutSubCategories.setVisibility(View.GONE);
                } else {
                    holder.linearLayoutCategories.setVisibility(View.GONE);
                   holder.linearLayoutSubCategories.setVisibility(View.VISIBLE);
                   int enabledOfferings = 0;
                    if (Data.userData.getAutosEnabled() == 1) {
                        holder.linearLayoutSubAutos.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubAutos.setVisibility(View.GONE);
                    }

                    if (Data.userData.getFreshEnabled() == 1) {
                        holder.linearLayoutSubFresh.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubFresh.setVisibility(View.GONE);
                    }

                    if (Data.userData.getMealsEnabled() == 1) {
                        holder.linearLayoutSubMeals.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubMeals.setVisibility(View.GONE);
                    }

                  /*  if(Data.userData.getGroceryEnabled() == 1){
                        holder.linearLayoutSubGrocery.setVisibility(View.VISIBLE);
                    } else {
                        holder.linearLayoutSubGrocery.setVisibility(View.GONE);
                    }*/

                    if(Data.userData.getMenusEnabled() == 1){
                        holder.linearLayoutSubMenus.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubMenus.setVisibility(View.GONE);
                    }
                   if(Data.userData.getDeliveryCustomerEnabled() == 1){
                        holder.linearlayoutDeliveryCustomer.setVisibility(View.VISIBLE);
                       enabledOfferings++;
                    } else {
                        holder.linearlayoutDeliveryCustomer.setVisibility(View.GONE);
                    }

                    if(Data.userData.getPayEnabled() == 1){
                        holder.linearLayoutSubPay.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubPay.setVisibility(View.GONE);
                    }

                    if (Data.userData.getFeedEnabled() == 1) {
                        holder.linearLayoutSubFeed.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubFeed.setVisibility(View.GONE);
                    }

                    if (Data.userData.getProsEnabled() == 1) {
                        holder.linearLayoutSubPros.setVisibility(View.VISIBLE);
                        enabledOfferings++;
                    } else {
                        holder.linearLayoutSubPros.setVisibility(View.GONE);
                    }
                    if(enabledOfferings <= 1){
                        holder.linearLayoutSubCategories.setVisibility(View.GONE);
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

        public ImageView imageViewArrow;
        public TextView  textViewCategories, textViewAutos, textViewFresh, textViewMeals,
                textViewGrocery, textViewMenus, textViewPay, textViewFeed, textViewPros;
        public LinearLayout linearLayoutCategories, linearLayoutSubCategories, linearLayoutSubMeals, linearLayoutSubFresh, linearLayoutSubAutos,
            linearLayoutSubGrocery, linearLayoutSubMenus, linearLayoutSubPay, linearLayoutSubFeed, linearLayoutSubPros, linearlayoutDeliveryCustomer;

        public ViewHeaderHolder(View convertView, Activity context) {
            super(convertView);



            imageViewArrow = (ImageView) convertView.findViewById(R.id.imageViewArrow);
            textViewCategories = (TextView) convertView.findViewById(R.id.textViewCategories); textViewCategories.setTypeface(Fonts.mavenRegular(context));
            textViewAutos = (TextView) convertView.findViewById(R.id.textViewAutos); textViewAutos.setTypeface(Fonts.mavenRegular(context));
            textViewFresh = (TextView) convertView.findViewById(R.id.textViewFresh); textViewFresh.setTypeface(Fonts.mavenRegular(context));
            textViewMeals = (TextView) convertView.findViewById(R.id.textViewMeals); textViewMeals.setTypeface(Fonts.mavenRegular(context));
            textViewGrocery = (TextView) convertView.findViewById(R.id.textViewGrocery); textViewGrocery.setTypeface(Fonts.mavenRegular(context));
            textViewMenus = (TextView) convertView.findViewById(R.id.textViewMenus); textViewMenus.setTypeface(Fonts.mavenRegular(context));
            textViewPay = (TextView) convertView.findViewById(R.id.textViewPay); textViewPay.setTypeface(Fonts.mavenRegular(context));
            textViewFeed = (TextView) convertView.findViewById(R.id.textViewFeed); textViewFeed.setTypeface(Fonts.mavenRegular(context));
            textViewPros = (TextView) convertView.findViewById(R.id.textViewPros); textViewPros.setTypeface(Fonts.mavenRegular(context));
            linearLayoutCategories = (LinearLayout) convertView.findViewById(R.id.linearLayoutCategories);
            linearLayoutSubCategories = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubCategories);
            linearLayoutSubAutos = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubAutos);
            linearLayoutSubFresh = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubFresh);
            linearLayoutSubMeals = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubMeals);
            linearLayoutSubGrocery = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubGrocery);
            linearLayoutSubMenus = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubMenus);
            linearLayoutSubPay = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubPay);
            linearLayoutSubFeed = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubFeed);
            linearLayoutSubPros = (LinearLayout) convertView.findViewById(R.id.linearLayoutSubPros);
            linearlayoutDeliveryCustomer = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeliveryCustomer);



            textViewFeed.setText(R.string.delivery_new_name);
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
