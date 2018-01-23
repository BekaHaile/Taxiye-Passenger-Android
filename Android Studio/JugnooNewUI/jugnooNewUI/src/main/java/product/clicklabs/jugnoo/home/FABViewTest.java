package product.clicklabs.jugnoo.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.activities.MainActivity;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.OfferingsVisibilityResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionButton;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;

/**
 * Created by ankit on 8/22/16.
 */
public class FABViewTest implements GACategory, GAAction {
    Activity activity;
    public RelativeLayout relativeLayoutFABTest;
    private boolean fabtoggleModeOn;

    public boolean isFabtoggleModeOn() {
        return fabtoggleModeOn;
    }

    public FloatingActionMenu getMenuLabelsRightTest(FloatingActionMenu menuLabelsRightTest) {
       return menuLabelsRightTest;
    }

    public FloatingActionMenu menuLabelsRightTest;
    public FloatingActionButton fabMealsTest;
    public FloatingActionButton fabFreshTest;
    public FloatingActionButton fabAutosTest;
    public FloatingActionButton fabMenusTest;
    public FloatingActionButton fabPayTest;
    public FloatingActionButton fabFeedTest;
    public FloatingActionButton fabProsTest;
    private FloatingActionButton fabDeliveryCustomer;

    public View view;
    private boolean isOpened;
    private final String GENIE_OPEN = "Genie Open";

    private RelativeLayout rlGenieHelp;
    private TextView tvGenieHelp, tvGenieExpandMessage;
    private ImageView ivJeanieHelp;

    public FABViewTest(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        initComponent();
    }

    public FloatingActionMenu getMenuLabelsRightTest() {
        return menuLabelsRightTest;
    }

    private void initComponent(){
        try {

            try {
                if(Data.userData.isRidesAndFatafatEnabled() && (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1)){
                    fabtoggleModeOn = true;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            relativeLayoutFABTest = (RelativeLayout) view;
            menuLabelsRightTest = (FloatingActionMenu) view.findViewById(R.id.menu_labels_right_Test);
            menuLabelsRightTest.setFABToggleModeOn(fabtoggleModeOn);
            fabMealsTest = (FloatingActionButton) view.findViewById(R.id.fabMealsTest);
            fabFreshTest = (FloatingActionButton) view.findViewById(R.id.fabFreshTest);
            fabAutosTest = (FloatingActionButton) view.findViewById(R.id.fabAutosTest);
            fabMenusTest = (FloatingActionButton) view.findViewById(R.id.fabMenusTest);
            fabDeliveryCustomer = (FloatingActionButton) view.findViewById(R.id.fabDeliveryCustomer);
            fabPayTest = (FloatingActionButton) view.findViewById(R.id.fabPayTest);
            fabFeedTest = (FloatingActionButton) view.findViewById(R.id.fabFeedTest);
            fabProsTest = (FloatingActionButton) view.findViewById(R.id.fabProsTest);
            menuLabelsRightTest.setIconAnimated(true);
            menuLabelsRightTest.setClosedOnTouchOutside(true);
            fabMealsTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabFreshTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabMenusTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabDeliveryCustomer.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabAutosTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabPayTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabFeedTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabProsTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabMenusTest.setOnClickListener(clickListener);
            fabDeliveryCustomer.setOnClickListener(clickListener);
            fabPayTest.setOnClickListener(clickListener);
            fabMealsTest.setOnClickListener(clickListener);
            fabFreshTest.setOnClickListener(clickListener);
            fabAutosTest.setOnClickListener(clickListener);
            fabFeedTest.setOnClickListener(clickListener);
            fabProsTest.setOnClickListener(clickListener);
            relativeLayoutFABTest.setVisibility(View.GONE);
            rlGenieHelp = (RelativeLayout) view.findViewById(R.id.rlGenieHelp);
            tvGenieHelp = (TextView) view.findViewById(R.id.tvGenieHelp);
            ivJeanieHelp = (ImageView) view.findViewById(R.id.ivJeanieHelp);
            tvGenieExpandMessage = (TextView) view.findViewById(R.id.tvGenieExpandMessage);
            tvGenieExpandMessage.setVisibility(View.GONE);

            fabFeedTest.setLabelText(Data.getFeedName(activity), 14);

            setRlGenieHelpBottomMargin(170f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isOpened = false;
        menuLabelsRightTest.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                try {
                    if(fabtoggleModeOn){

                        try {
                            if(Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0){
                                Data.latitude = Data.autoData.getLastRefreshLatLng().latitude;
                                Data.longitude = Data.autoData.getLastRefreshLatLng().longitude;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    LatLng latLng = new LatLng(Data.latitude, Data.longitude);
                    if(activity instanceof HomeActivity){
                        latLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
                    } else if(activity instanceof FreshActivity){
                        latLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
                    } else if(activity instanceof MainActivity){
                        latLng = ((MainActivity)activity).getCurrentPlaceLatLng();
                    }
                    final LatLng finalLatLng = latLng;
                        String selectedOffering;
                            if(activity instanceof HomeActivity){
                                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getDeliveryCustomerClientId(), finalLatLng, false);

                                selectedOffering = GACategory.DELIVERY_CUSTOMER;
                            }else{
                                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), finalLatLng, false);

                                selectedOffering = RIDES;
                            }

                        GAUtils.event(JUGNOO, GENIE+OPEN, selectedOffering+SELECTED);

                        return;
                    }


                    if (opened) {
                        setButtonsVisibilityOnOpen();
                        isOpened = true;
                        if(activity instanceof HomeActivity){
                            ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(false);
//                            ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.VISIBLE);
                        }
                        Prefs.with(activity).save(Constants.SP_SHOW_GEANIE_HELP, 1);
                        setRlGenieHelpVisibility();
                        Utils.hideSoftKeyboard(activity, relativeLayoutFABTest);
                        GAUtils.event(JUGNOO, getOffering()+HOME, GENIE+OPENED);
                        GAUtils.trackScreenView(GENIE_OPEN_SCREEN);
                    } else {
                        isOpened = false;
                        if(activity instanceof HomeActivity){
                            ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(true);
//                            ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);
                        }
                        ivJeanieHelp.setVisibility(View.GONE);
                        tvGenieExpandMessage.setVisibility(View.GONE);
                        GAUtils.event(JUGNOO, getOffering()+HOME, GENIE+CLOSED);
                    }
                    try {
                        if(activity instanceof HomeActivity) {
                            ((HomeActivity) activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSurgeImageVisibility();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        rlGenieHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabelsRightTest.open(true);
            }
        });


        setUIInital();


    }

    private void setUIInital() {
        if(fabtoggleModeOn){
            menuLabelsRightTest.setFABToggleModeOn(true);
           boolean wasMenuOpen =  menuLabelsRightTest.close(true,true);
            if(activity instanceof HomeActivity && wasMenuOpen){
                ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);

            }
            if(activity instanceof HomeActivity){
                menuLabelsRightTest.setMenuIcon(ContextCompat.getDrawable(activity, R.drawable.ic_delivery_customer));
                menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity,R.color.green_delivery_customer_fab));
                menuLabelsRightTest.setMenuButtonColorPressed(ContextCompat.getColor(activity,R.color.green_delivery_customer_fab_pressed));
                menuLabelsRightTest.setMenuButtonColorRipple(ContextCompat.getColor(activity,R.color.green_delivery_customer_fab_pressed));
            }else{

                menuLabelsRightTest.setMenuIcon(ContextCompat.getDrawable(activity,R.drawable.ic_rides));
                menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity,R.color.theme_color));
                menuLabelsRightTest.setMenuButtonColorPressed(ContextCompat.getColor(activity,R.color.orange_rides_fab_pressed));
                menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity,R.color.orange_rides_fab_pressed));
            }

            hideToggleJeanieIfOfferingNotAvailable();
        }else{
            menuLabelsRightTest.setFABToggleModeOn(false);
            menuLabelsRightTest.setMenuIcon(ContextCompat.getDrawable(activity,R.drawable.ic_fab_jeanie));
            menuLabelsRightTest.setMenuButtonColorNormal(activity.getResources().getColor(R.color.white));
            menuLabelsRightTest.setMenuButtonColorPressed(activity.getResources().getColor(R.color.grey_light));
            menuLabelsRightTest.setMenuButtonColorRipple(activity.getResources().getColor(R.color.grey_light_alpha));
            createCustomAnimation();

        }
    }


    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menuLabelsRightTest.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menuLabelsRightTest.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menuLabelsRightTest.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menuLabelsRightTest.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(100);
        scaleOutY.setDuration(100);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(Data.userData == null || Data.userData.getExpandJeanie() == 0) {
                    menuLabelsRightTest.getMenuIconView().setImageResource(menuLabelsRightTest.isOpened() ? R.drawable.ic_fab_jeanie : R.drawable.ic_fab_cross);
                    menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity, R.color.white));
                } else if(Data.userData != null) {
                    Data.userData.setExpandJeanie(0);
                    menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_jeanie);
                    menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity, R.color.grey_light));
                }
                try {
                    if(activity instanceof HomeActivity) {
						if (menuLabelsRightTest.isOpened()) {
							((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);
						} else {
							((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.VISIBLE);
                            ((HomeActivity) activity).getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
						}
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuLabelsRightTest.setIconToggleAnimatorSet(set);
    }

    public void setFABButtons(){
        try {
            if(getNoOfOfferingsEnabled()<=1 ||  (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 0) || Data.userData.getIntegratedJugnooEnabled()==0){
                relativeLayoutFABTest.setVisibility(View.GONE);
            }else if(Data.userData.isRidesAndFatafatEnabled() && (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1)){
                if(!isFabtoggleModeOn()){
                    fabtoggleModeOn = true;
                    setUIInital();
                }

                relativeLayoutFABTest.setVisibility(View.VISIBLE);


            } else {
                if (Data.userData.getAutosEnabled() != 1) {
                    fabAutosTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabAutosTest.setVisibility(View.VISIBLE);
                    }
                }


                if (Data.userData.getFreshEnabled() != 1) {
                    fabFreshTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabFreshTest.setVisibility(View.VISIBLE);
                    }
                }

                if (Data.userData.getMealsEnabled() != 1) {
                    fabMealsTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabMealsTest.setVisibility(View.VISIBLE);
                    }
                }

                if(Data.userData.getMenusEnabled() != 1){
                    fabMenusTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabMenusTest.setVisibility(View.VISIBLE);
                    }
                }
                if(Data.userData.getDeliveryCustomerEnabled() != 1){
                    fabDeliveryCustomer.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabDeliveryCustomer.setVisibility(View.VISIBLE);
                    }
                }

                if(Data.userData.getPayEnabled() != 1){
                    fabPayTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabPayTest.setVisibility(View.VISIBLE);
                    }
                }

                if (Data.userData.getFeedEnabled() != 1) {
                    fabFeedTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabFeedTest.setVisibility(View.VISIBLE);
                    }
                }

                if (Data.userData.getProsEnabled() != 1) {
                    fabProsTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabProsTest.setVisibility(View.VISIBLE);
                    }
                }

                relativeLayoutFABTest.setVisibility(View.VISIBLE);

                setRlGenieHelpVisibility();

                if(isFabtoggleModeOn()){
                    fabtoggleModeOn = false;
                    setUIInital();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

    private void setButtonsVisibilityOnOpen(){
        try {
            if (Data.userData.getAutosEnabled() == 1) {
                fabAutosTest.setVisibility(View.VISIBLE);
            }
            if (Data.userData.getFreshEnabled() == 1) {
                fabFreshTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getMealsEnabled() == 1) {
                fabMealsTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getMenusEnabled() == 1) {
                fabMenusTest.setVisibility(View.VISIBLE);
            }
            if (Data.userData.getDeliveryCustomerEnabled() == 1) {
                fabDeliveryCustomer.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getPayEnabled() == 1) {
                fabPayTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getFeedEnabled() == 1) {
                fabFeedTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getProsEnabled() == 1) {
                fabProsTest.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if(Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0){
					Data.latitude = Data.autoData.getLastRefreshLatLng().latitude;
					Data.longitude = Data.autoData.getLastRefreshLatLng().longitude;
				}
            } catch (Exception e) {
                e.printStackTrace();
            }

            LatLng latLng = new LatLng(Data.latitude, Data.longitude);
            if(activity instanceof HomeActivity){
                latLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
            } else if(activity instanceof FreshActivity){
                latLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
            } else if(activity instanceof MainActivity){
                latLng = ((MainActivity)activity).getCurrentPlaceLatLng();
            }
            final LatLng finalLatLng = latLng;
            menuLabelsRightTest.close(true, false);
            String selectedOffering = RIDES;
            switch (v.getId()) {
                case R.id.fabMealsTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = MEALS;
                    break;
                case R.id.fabFreshTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = FRESH;
                    break;
                case R.id.fabAutosTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = RIDES;
                    break;
                case R.id.fabMenusTest:

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMenusClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = GAAction.MENUS;
                    break;

                case R.id.fabDeliveryCustomer:

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getDeliveryCustomerClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = GACategory.DELIVERY_CUSTOMER;
                    break;
                case R.id.fabPayTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getPayClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = PAY;
                    break;
                case R.id.fabFeedTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFeedClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = FEED;
                    break;
                case R.id.fabProsTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getProsClientId(), finalLatLng, false);
                        }
                    }, 300);
                    selectedOffering = PROS;
                    break;
            }
            GAUtils.event(JUGNOO, GENIE+OPEN, selectedOffering+SELECTED);

        }
    };

    public void closeMenu(){
        try {
            menuLabelsRightTest.close(false, false);
            menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_jeanie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRlGenieHelpVisibility(){
        try {
            if(Data.userData.getShowJeanieHelpText() == 1
                    && Prefs.with(activity).getInt(Constants.SP_SHOW_GEANIE_HELP, 0) == 0
                    && !Data.isJeanieShownInSession()){
                handler.postDelayed(runnableJeanieHelpShow, 2000);
            } else {
                rlGenieHelp.setVisibility(View.GONE);
                handler.removeCallbacks(runnableJeanieHelpShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable runnableJeanieHelpShow = new Runnable() {
        @Override
        public void run() {
            rlGenieHelp.setVisibility(View.VISIBLE);
            Data.setJeanieShownInSession(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlGenieHelp.setVisibility(View.GONE);
                }
            }, 4000);
        }
    };

    public void setRelativeLayoutFABTestVisibility(int visibility){
        try {
            if(visibility == View.VISIBLE
                    && Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1
                    && Data.userData.getIntegratedJugnooEnabled() == 1){
                relativeLayoutFABTest.setVisibility(View.VISIBLE);
            } else {
                relativeLayoutFABTest.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(visibility != View.VISIBLE){
//            hideJeanieHelpInSession();
        }
    }

    public void hideJeanieHelpInSession(){
        Data.setJeanieShownInSession(true);
        setRlGenieHelpVisibility();
    }

    public void setRlGenieHelpBottomMargin(float bottomMargin){
        setRlGenieHelpBottomMargin((int) (ASSL.Yscale() * bottomMargin));
    }

    public void setRlGenieHelpBottomMargin(int bottomMargin){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlGenieHelp.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
        rlGenieHelp.setLayoutParams(params);
    }

    /**
     * @param paddingBottom in pixels without scale
     */
    public void setMenuLabelsRightTestPadding(float paddingBottom){
        int dpAsPixels = getPaddingAsPerDisplayMetrics(paddingBottom);
        setMenuLabelsRightTestPadding(dpAsPixels);
    }

    public int getPaddingAsPerDisplayMetrics(float paddingBottom) {
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (paddingBottom * scale + 0.5f);
    }

    /**
     * @param paddingBottom in pixels with scale or absolute
     */
    public void setMenuLabelsRightTestPadding(int paddingBottom){
        if(activity instanceof FreshActivity){
            menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0,paddingBottom);
            setRlGenieHelpBottomMargin(paddingBottom + (int)(ASSL.Yscale() * 100f));
        }else{
            menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, paddingBottom);
            setRlGenieHelpBottomMargin(paddingBottom + (int)(ASSL.Yscale() * 100f));
        }

    }

    public void showTutorial(){
        if(Data.userData != null && Data.userData.getShowTutorial() == 1) {
            menuLabelsRightTest.open(true);
            Animation animation = new AlphaAnimation(0f, 1f);
            animation.setDuration(1000);
            animation.setFillAfter(false);
            ivJeanieHelp.setVisibility(View.VISIBLE);
            ivJeanieHelp.startAnimation(animation);
            Data.userData.setShowTutorial(0);
        }
    }

    private String clientId;
    private String getOffering() {
        if (clientId == null) {
            clientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
        }
        if (clientId.equalsIgnoreCase(Config.getFreshClientId())) {
            return FRESH;
        } else if (clientId.equalsIgnoreCase(Config.getMealsClientId())) {
            return MEALS;
        } else if (clientId.equalsIgnoreCase(Config.getMenusClientId())) {
            return GACategory.MENUS;
        }else if (clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())) {
            return GACategory.DELIVERY_CUSTOMER;
        } else if (clientId.equalsIgnoreCase(Config.getPayClientId())) {
            return PAY;
        } else if (clientId.equalsIgnoreCase(Config.getFeedClientId())) {
            return FEED;
        }  else if (clientId.equalsIgnoreCase(Config.getProsClientId())) {
            return PROS;
        } else {
            return RIDES;
        }
    }

    public boolean getIsOpened(){
        return isOpened;
    }

    public void expandJeanieFirstTime(){
        try {
            if(Data.userData != null && Data.userData.getExpandJeanie() == 1) {
				getMenuLabelsRightTest().open(true);
				if(!TextUtils.isEmpty(Data.userData.getExpandedGenieText())) {
					tvGenieExpandMessage.setText(Data.userData.getExpandedGenieText());
					Animation animation = new AlphaAnimation(0f, 1f);
					animation.setDuration(500);
					animation.setFillAfter(false);
					tvGenieExpandMessage.setVisibility(View.VISIBLE);
					tvGenieExpandMessage.startAnimation(animation);
                    GAUtils.trackScreenView(JEANIE+EXPANDED+AUTOMATIC);
				}
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Data.userData.setExpandJeanie(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
			} else if(Data.userData != null){
                Data.userData.setExpandJeanie(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideToggleJeanieIfOfferingNotAvailable(){
        if(fabtoggleModeOn){
            if(activity instanceof FreshActivity){
                menuLabelsRightTest.setVisibility(Data.userData.getAutosEnabled()==1?View.VISIBLE:View.GONE);

            }else{
                menuLabelsRightTest.setVisibility(Data.userData.getDeliveryCustomerEnabled()==1?View.VISIBLE:View.GONE);

            }
        }
    }
    private int getNoOfOfferingsEnabled(){


        return noOfOfferingsEnabled(Data.userData.getDeliveryCustomerEnabled(),
                Data.userData.getAutosEnabled(),
                Data.userData.getFreshEnabled(),
                Data.userData.getMealsEnabled(),
                Data.userData.getDeliveryEnabled(),
                Data.userData.getGroceryEnabled(),
                Data.userData.getMenusEnabled(),
                Data.userData.getPayEnabled(),
                Data.userData.getFeedEnabled(),
                Data.userData.getProsEnabled());
    }


    private int noOfOfferingsEnabled(Integer... offeringFlag){
        int noOfOfferings = 0;
        for(int offeringState:offeringFlag){
            if(offeringState==1){
                noOfOfferings++;
            }
        }
        return noOfOfferings;
    }



    private boolean isSameState(OfferingsVisibilityResponse.OfferingsVisibilityData offeringsVisibilityData){
        return Data.userData.getDeliveryCustomerEnabled() == offeringsVisibilityData.getDeliveryCustomerEnabled() &&
                Data.userData.getAutosEnabled() == offeringsVisibilityData.getAutosEnabled() &&
                Data.userData.getFreshEnabled() == offeringsVisibilityData.getFreshEnabled() &&
                Data.userData.getMealsEnabled() == offeringsVisibilityData.getMealsEnabled() &&
                Data.userData.getDeliveryEnabled() == offeringsVisibilityData.getDeliveryEnabled() &&
                Data.userData.getGroceryEnabled() == offeringsVisibilityData.getGroceryEnabled() &&
                Data.userData.getMenusEnabled() == offeringsVisibilityData.getMenusEnabled() &&
                Data.userData.getPayEnabled() == offeringsVisibilityData.getPayEnabled() &&
                Data.userData.getFeedEnabled() == offeringsVisibilityData.getFeedEnabled() &&
                Data.userData.getProsEnabled() == offeringsVisibilityData.getProsEnabled() &&
                Data.userData.getIntegratedJugnooEnabled() == offeringsVisibilityData.getIntegratedJugnooEnabled();

    }

    public boolean triggerStateChangeFunction(OfferingsVisibilityResponse.OfferingsVisibilityData offeringsVisibilityData){
        if(!isSameState(offeringsVisibilityData)){
            ApiFindADriver.parseResponseForOfferingsEnabled(offeringsVisibilityData);
            setFABButtons();
            return true;
        }
        return false;
    }
}
