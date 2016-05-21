package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;

/**
 * Created by Ankit on 4/29/16.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity activity;
    private ArrayList<MenuInfo> menuList;

    public MenuAdapter(ArrayList<MenuInfo> menuList, Activity activity) {
        this.menuList = menuList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_account, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(585, 280);
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
                        if(menuInfo.getIcon() != null && !"".equalsIgnoreCase(menuInfo.getIcon())){
                            icon = menuInfo.getIcon();
                        } else if(!"".equalsIgnoreCase(Data.userData.getGamePredictIconUrl())){
                            icon = Data.userData.getGamePredictIconUrl();
                        }
                        if(!"".equalsIgnoreCase(icon)){
                            Picasso.with(activity)
                                    .load(icon)
                                    .placeholder(getSelector(activity, R.drawable.ic_play_pressed, R.drawable.ic_play_normal))
                                    .error(getSelector(activity, R.drawable.ic_play_pressed, R.drawable.ic_play_normal))
                                    .into(holder.imageViewMenuIcon);
                        }
                        holder.textViewMenu.setText(Data.userData.getGamePredictName());
                        if(!"".equalsIgnoreCase(Data.userData.getGamePredictNew())){
                            holder.textViewNew.setText(Data.userData.getGamePredictNew());
                        } else{
                            holder.textViewNew.setVisibility(View.GONE);
                        }
                        if(Data.userData.getGamePredictEnable() != 1
                                || "".equalsIgnoreCase(Data.userData.getGamePredictName())){
                            hideLayout(holder.relative);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.GET_A_RIDE.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_get_a_ride_selector);
                } else if(MenuInfoTags.JUGNOO_FRESH.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_fresh_selector);
                    if(activity instanceof HomeActivity) {
                        if (1 != Data.freshAvailable) {
                            hideLayout(holder.relative);
                        }
                    }
                }else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_share_selector);
                } else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_wallet_selector);
                    try {
                        holder.textViewValue.setText(String.format(activity.getResources()
                                        .getString(R.string.rupees_value_format_without_space),
                                Utils.getMoneyDecimalFormat().format(Data.userData.getTotalWalletBalance())));
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
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.PROMOTIONS.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_promotion_selector);
                    try {
                        if(Data.userData.numCouponsAvaliable > 0) {
                            holder.textViewValue.setVisibility(View.VISIBLE);
                            holder.textViewValue.setText(String.valueOf(Data.userData.numCouponsAvaliable));
                            holder.textViewValue.setBackgroundResource(R.drawable.circle_theme);
                            setLayoutParamsForValue(holder.textViewValue);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_history_selector);
                    if(activity instanceof FreshActivity){
                        holder.textViewMenu.setText(activity.getResources().getString(R.string.order_history));
                    }
                } else if(MenuInfoTags.REFER_A_DRIVER.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_refer_a_driver_selector);
                    try {
                        if(Data.userData.getcToDReferralEnabled() != 1){
                            hideLayout(holder.relative);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_support_selector);
                } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(menuInfo.getTag())){
                    holder.imageViewMenuIcon.setImageResource(R.drawable.ic_about_selector);
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
            ViewHeaderHolder holder = (ViewHeaderHolder) viewholder;
            try {
                holder.textViewUserName.setText(Data.userData.userName);
                Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
                if(activity instanceof HomeActivity && ((HomeActivity)activity).activityResumed){
                    Picasso.with(activity).load(Data.userData.userImage).transform(new CircleTransform()).into(holder.imageViewProfile);
                }
                else{
                    Picasso.with(activity).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(holder.imageViewProfile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountClick();
                }
            });
        }

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
                    FlurryEventLogger.event(FlurryEventNames.WORLD_CUP_MENU);
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_GAME_CLICKED, null);
                }
            } else if((MenuInfoTags.GET_A_RIDE.getTag().equalsIgnoreCase(tag))){
                if(activity instanceof HomeActivity) {
                    ((HomeActivity) activity).drawerLayout.closeDrawer(GravityCompat.START);
                } else if(activity instanceof FreshActivity){
                    activity.finish();
                    activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
                }
            } else if(MenuInfoTags.JUGNOO_FRESH.getTag().equalsIgnoreCase(tag)){
                if(activity instanceof HomeActivity) {
                    if(1 == Data.freshAvailable) {
                        if (((HomeActivity) activity).map != null
                                && ((HomeActivity)activity).mapStateListener != null
                                && ((HomeActivity)activity).mapStateListener.isMapSettled()) {
                            Data.latitude = ((HomeActivity) activity).map.getCameraPosition().target.latitude;
                            Data.longitude = ((HomeActivity) activity).map.getCameraPosition().target.longitude;
                        }
                        activity.startActivity(new Intent(activity, FreshActivity.class));
                        activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_JUGNOO_FRESH_CLICKED, null);
                        FlurryEventLogger.eventGA(Constants.REVENUE+Constants.SLASH+Constants.ACTIVATION+Constants.SLASH+Constants.RETENTION, "Home Screen", "fresh");
                    }
                } else if(activity instanceof FreshActivity){
                    ((FreshActivity) activity).drawerLayout.closeDrawer(GravityCompat.START);
                }
            } else if(MenuInfoTags.FREE_RIDES.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, ShareActivity.class);
                intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FREE_RIDES_CLICKED, null);

            } else if(MenuInfoTags.WALLET.getTag().equalsIgnoreCase(tag)){
                Intent intent = new Intent(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.WALLET_MENU);
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_WALLET);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_WALLET_CLICKED, null);

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

            }else if(MenuInfoTags.PROMOTIONS.getTag().equalsIgnoreCase(tag)){
                LatLng currLatLng = null;
                if(activity instanceof HomeActivity){
                    currLatLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
                } else if(activity instanceof FreshActivity){
                    currLatLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
                }
                if (currLatLng != null) {
                    Data.latitude = currLatLng.latitude;
                    Data.longitude = currLatLng.longitude;
                    if(AppStatus.getInstance(activity).isOnline(activity)) {
                        activity.startActivity(new Intent(activity, ShareActivity.class));
                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PROMOTIONS_SCREEN);
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
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.waiting_for_location),
                            Toast.LENGTH_SHORT).show();
                }
            } else if(MenuInfoTags.HISTORY.getTag().equalsIgnoreCase(tag)){
                if(activity instanceof HomeActivity) {
                    Intent intent = new Intent(activity, RideTransactionsActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(FlurryEventNames.RIDE_HISTORY);

                } else if(activity instanceof FreshActivity){
                    ((FreshActivity)activity).openOrderHistory();
                }
            } else if(MenuInfoTags.SUPPORT.getTag().equalsIgnoreCase(tag)){
                if(activity instanceof HomeActivity) {
                    activity.startActivity(new Intent(activity, SupportActivity.class));
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                } else if(activity instanceof FreshActivity){
                    ((FreshActivity)activity).openSupport();
                }
            } else if(MenuInfoTags.ABOUT.getTag().equalsIgnoreCase(tag)){
                activity.startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
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
        public ImageView imageViewProfile;
        public TextView textViewUserName, textViewViewAccount;
        public ViewHeaderHolder(View convertView, Activity context) {
            super(convertView);
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            imageViewProfile = (ImageView) convertView.findViewById(R.id.imageViewProfile);//textViewUserName
            textViewUserName = (TextView) convertView.findViewById(R.id.textViewUserName); textViewUserName.setTypeface(Fonts.mavenMedium(context));
            textViewViewAccount = (TextView) convertView.findViewById(R.id.textViewViewAccount); textViewViewAccount.setTypeface(Fonts.mavenMedium(context));
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
