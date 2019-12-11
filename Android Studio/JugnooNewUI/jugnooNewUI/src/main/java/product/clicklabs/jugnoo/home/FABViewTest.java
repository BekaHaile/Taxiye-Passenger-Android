package product.clicklabs.jugnoo.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private RelativeLayout relativeLayoutFABTest;
    private Boolean fabtoggleModeOn;
    private ToggleModeData currentToggleData;

    public boolean isFabtoggleModeOn() {
        return fabtoggleModeOn!=null && fabtoggleModeOn;
    }

    public FloatingActionMenu getMenuLabelsRightTest(FloatingActionMenu menuLabelsRightTest) {
       return menuLabelsRightTest;
    }

    public FloatingActionMenu menuLabelsRightTest;
    private FloatingActionButton fabMealsTest;
    private FloatingActionButton fabFreshTest;
    private FloatingActionButton fabAutosTest;
    private FloatingActionButton fabMenusTest;
    private FloatingActionButton fabPayTest;
    private FloatingActionButton fabFeedTest;
    private FloatingActionButton fabProsTest;
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

            relativeLayoutFABTest = (RelativeLayout) view;
            menuLabelsRightTest = (FloatingActionMenu) view.findViewById(R.id.menu_labels_right_Test);
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
            tvGenieHelp.setText(activity.getString(R.string.explore_more_from_jugnoo, activity.getString(R.string.app_name)));
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
                    if(isFabtoggleModeOn()){

                            if(currentToggleData==null){
                                return;
                            }

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
                    MyApplication.getInstance().getAppSwitcher().switchApp(activity, currentToggleData.getClientId(), finalLatLng, false);

                        GAUtils.event(JUGNOO, GENIE+OPEN, getOffering(currentToggleData.getClientId())+SELECTED);

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
                        GAUtils.event(JUGNOO, getOffering(null)+HOME, GENIE+OPENED);
                        GAUtils.trackScreenView(GENIE_OPEN_SCREEN);
                    } else {
                        isOpened = false;
                        if(activity instanceof HomeActivity){
                            ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(true);
//                            ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);
                        }
                        ivJeanieHelp.setVisibility(View.GONE);
                        tvGenieExpandMessage.setVisibility(View.GONE);
                        GAUtils.event(JUGNOO, getOffering(null)+HOME, GENIE+CLOSED);
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


        setFABButtons(false);


    }

    private void setUIInital(boolean toggleModeOn,String clientIdToShowOnToggle) {
        if(toggleModeOn && clientIdToShowOnToggle!=null){
            fabtoggleModeOn = true;
            menuLabelsRightTest.setFABToggleModeOn(true);
           boolean wasMenuOpen =  menuLabelsRightTest.close(true,true);
            if(activity instanceof HomeActivity && wasMenuOpen){
                ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);

            }


            currentToggleData = getListToggleModeData().get(clientIdToShowOnToggle);
            menuLabelsRightTest.setMenuIcon(ContextCompat.getDrawable(activity, currentToggleData.getIcon()));
            menuLabelsRightTest.setMenuButtonColorNormal(ContextCompat.getColor(activity, currentToggleData.getColor()));
            menuLabelsRightTest.setMenuButtonColorPressed(ContextCompat.getColor(activity, currentToggleData.getColorPressed()));
            menuLabelsRightTest.setMenuButtonColorRipple(ContextCompat.getColor(activity, currentToggleData.getColorPressed()));



         /*   if(activity instanceof HomeActivity){
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

            hideToggleJeanieIfOfferingNotAvailable();*/
        }else{
            currentToggleData = null;
            fabtoggleModeOn = false;
            menuLabelsRightTest.setFABToggleModeOn(false);
            menuLabelsRightTest.setMenuIcon(ContextCompat.getDrawable(activity,menuLabelsRightTest.isOpened()?R.drawable.ic_fab_cross:R.drawable.ic_fab_jeanie));
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

    public int setFABButtons(boolean getVisibilityOfFab){
        try {
            List<String> offeringsEnabled = getClientIdOfEnabledOffering();
            int noOfOfferingsEnabled = offeringsEnabled.size();
            if(noOfOfferingsEnabled==0 ||  (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 0) || Data.userData.getIntegratedJugnooEnabled()==0){
                if(getVisibilityOfFab) return  View.GONE;
                setRelativeLayoutFABTestVisibility(View.GONE);

            }else if(noOfOfferingsEnabled<=2){

                if(noOfOfferingsEnabled==1){
                    int fabVisibility = View.GONE;
                    if (activity instanceof HomeActivity){
                        fabVisibility = Data.userData.getAutosEnabled()==1?View.GONE:View.VISIBLE;
                    }else if( activity instanceof MainActivity){
                        fabVisibility = Data.userData.getPayEnabled()==1?View.GONE:View.VISIBLE;
                    } else if(activity instanceof FreshActivity){
                        fabVisibility = Data.userData.getAutosEnabled()==1||Data.userData.getPayEnabled()==1 || !((FreshActivity)activity).currentOpenClientIdForFab().equals(offeringsEnabled.get(0))
                                    ?View.VISIBLE:View.GONE;
                    }
                    //fab may or may not be visible depending upon current offering opening, however if visible it would be always in toggle mode
                    if(getVisibilityOfFab)return  fabVisibility;



                    if(fabVisibility == View.VISIBLE){
                        setUIInital(true,offeringsEnabled.get(0));
                        setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }else{
                        setRelativeLayoutFABTestVisibility(View.GONE);
                    }
                }else {
                    if(getVisibilityOfFab)return  View.VISIBLE; //fab would always be visible depending in either toggle or jeanie mode


                    boolean showToggle = false;
                    String clientIdOfferingToShowOnToggle = null;
                    if(activity instanceof HomeActivity){
                        showToggle = Data.userData.getAutosEnabled()==1;
                        if(showToggle)clientIdOfferingToShowOnToggle = offeringsEnabled.get(0).equals(Config.getAutosClientId())?offeringsEnabled.get(1):offeringsEnabled.get(0);
                    } else if( activity instanceof MainActivity){
                        showToggle = Data.userData.getPayEnabled()==1;
                        if(showToggle)clientIdOfferingToShowOnToggle = offeringsEnabled.get(0).equals(Config.getPayClientId())?offeringsEnabled.get(1):offeringsEnabled.get(0);
                    }else if(activity instanceof FreshActivity){
                        String currentOpenClientId = ((FreshActivity)activity).currentOpenClientIdForFab();
                        showToggle = currentOpenClientId.equals(offeringsEnabled.get(0)) || currentOpenClientId.equals(offeringsEnabled.get(1));
                        if(showToggle)clientIdOfferingToShowOnToggle = offeringsEnabled.get(0).equals(currentOpenClientId)?offeringsEnabled.get(1):offeringsEnabled.get(0);

                    }

                    if(!showToggle){
                        setJeanieModeFabVisibility();
                    }

                    setUIInital(showToggle,clientIdOfferingToShowOnToggle);
                    setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }



            } else {

                if(getVisibilityOfFab)return  View.VISIBLE;//fab would always be visible in jeanie mode;
                setJeanieModeFabVisibility();


                setRlGenieHelpVisibility();
                if(fabtoggleModeOn==null || fabtoggleModeOn){
                    setUIInital(false,Config.getAutosClientId());

                }
                setRelativeLayoutFABTestVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return View.VISIBLE;

    }

    private void setJeanieModeFabVisibility() {
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
            if(visibility==View.VISIBLE && setFABButtons(true)==View.VISIBLE){
                relativeLayoutFABTest.setVisibility(View.VISIBLE);

            }else if(visibility==View.GONE){
                relativeLayoutFABTest.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.printStackTrace();
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
        params.setMarginStart(params.getMarginStart());
        params.setMarginEnd(params.getMarginEnd());
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
            menuLabelsRightTest.setPaddingRelative((int) (40f * ASSL.Xscale()), 0, (int) (40f * ASSL.Xscale()),paddingBottom);
            setRlGenieHelpBottomMargin(paddingBottom + (int)(ASSL.Yscale() * 100f));
        }else{
            menuLabelsRightTest.setPaddingRelative((int) (40f * ASSL.Xscale()), 0, (int) (40f * ASSL.Xscale()), paddingBottom);
            setRlGenieHelpBottomMargin(paddingBottom + (int)(ASSL.Yscale() * 100f));
        }

    }

    public void showTutorial(){
        if(Data.userData != null && Data.userData.getShowTutorial() == 1 && getNoOfOfferingsEnabled()>2) {
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
    private String getOffering(String specificClientId) {
        if (clientId == null) {
            clientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
        }
        String clientIdToCheck  = specificClientId==null?clientId:specificClientId;
        if (clientIdToCheck.equalsIgnoreCase(Config.getFreshClientId())) {
            return FRESH;
        } else if (clientIdToCheck.equalsIgnoreCase(Config.getMealsClientId())) {
            return MEALS;
        } else if (clientIdToCheck.equalsIgnoreCase(Config.getMenusClientId())) {
            return GACategory.MENUS;
        }else if (clientIdToCheck.equalsIgnoreCase(Config.getDeliveryCustomerClientId())) {
            return GACategory.DELIVERY_CUSTOMER;
        } else if (clientIdToCheck.equalsIgnoreCase(Config.getPayClientId())) {
            return PAY;
        } else if (clientIdToCheck.equalsIgnoreCase(Config.getFeedClientId())) {
            return FEED;
        }  else if (clientIdToCheck.equalsIgnoreCase(Config.getProsClientId())) {
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
        try {
            if(!isSameState(offeringsVisibilityData)){
                ApiFindADriver.parseResponseForOfferingsEnabled(offeringsVisibilityData);
                setFABButtons(false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     *
     * It is used in case of toggle mode on
     *
     */

    private List<String> getClientIdOfEnabledOffering(){
        List<String> configs = new ArrayList<>();

        if(Data.userData.getDeliveryCustomerEnabled()==1)
            configs.add(Config.DELIVERY_CUSTOMER_CLIENT_ID);
        if(Data.userData.getMenusEnabled()==1)
            configs.add(Config.MENUS_CLIENT_ID);
        if(Data.userData.getMealsEnabled()==1)
            configs.add(Config.MEALS_CLIENT_ID);
        if(Data.userData.getFreshEnabled()==1)
            configs.add(Config.FRESH_CLIENT_ID);
        if(Data.userData.getFeedEnabled()==1)
            configs.add(Config.FEED_CLIENT_ID);
        if(Data.userData.getProsEnabled()==1)
            configs.add(Config.PROS_CLIENT_ID);

        if(Data.userData.getGroceryEnabled()==1)
            configs.add(Config.GROCERY_CLIENT_ID);
        if(Data.userData.getDeliveryEnabled()==1)
            configs.add(Config.DELIVERY_CLIENT_ID);

        if(Data.userData.getAutosEnabled()==1)
            configs.add(Config.AUTOS_CLIENT_ID);
        if(Data.userData.getPayEnabled()==1)
            configs.add(Config.PAY_CLIENT_ID);


        return configs;




    }



    private static HashMap<String,ToggleModeData> listToggleModeData ;

    public class ToggleModeData{
        private String clientId;
        private int icon;
        private int color;
        private int colorPressed;

        ToggleModeData(String clientId, int icon, int color, int colorPressed) {
            this.clientId = clientId;
            this.icon = icon;
            this.color = color;
            this.colorPressed = colorPressed;
        }

        public String getClientId() {
            return clientId;
        }

        public int getIcon() {
            return icon;
        }

        public int getColor() {
            return color;
        }

        public int getColorPressed() {
            return colorPressed;
        }
    }

    private void initOfferingsList(){
         listToggleModeData = new HashMap<>();
        listToggleModeData.put(Config.getAutosClientId(),new ToggleModeData(Config.getAutosClientId(), R.drawable.ic_rides,R.color.theme_color,R.color.orange_rides_fab_pressed));
        listToggleModeData.put(Config.getDeliveryCustomerClientId(),new ToggleModeData(Config.getDeliveryCustomerClientId(),R.drawable.ic_delivery_customer,R.color.green_delivery_customer_fab,R.color.green_delivery_customer_fab));
        listToggleModeData.put(Config.getMealsClientId(),new ToggleModeData(Config.getMealsClientId(),R.drawable.ic_meals,R.color.pink_meals_fab,R.color.pink_meals_fab_pressed));
        listToggleModeData.put(Config.getFreshClientId(),new ToggleModeData(Config.getFreshClientId(),R.drawable.ic_groceries_new_vector,R.color.green_fresh_fab,R.color.green_fresh_fab_pressed));
        listToggleModeData.put(Config.getMenusClientId(),new ToggleModeData(Config.getMenusClientId(),R.drawable.ic_menus,R.color.purple_menus_fab,R.color.purple_menus_fab_pressed));
        listToggleModeData.put(Config.getPayClientId(),new ToggleModeData(Config.getPayClientId(),R.drawable.ic_pay,R.color.yellow_pay_fab,R.color.yellow_pay_fab_pressed));
        listToggleModeData.put(Config.getFeedClientId(),new ToggleModeData(Config.getFeedClientId(),R.drawable.ic_anywhere_fab,R.color.grey_feed_fab,R.color.grey_feed_fab_pressed));
        listToggleModeData.put(Config.getProsClientId(),new ToggleModeData(Config.getProsClientId(),R.drawable.ic_pros,R.color.orange_pros_fab,R.color.orange_pros_fab_pressed));
        //not used now in project,fallback cases written just in case
        listToggleModeData.put(Config.getGroceryClientId(),new ToggleModeData(Config.getGroceryClientId(),R.drawable.ic_groceries_new_vector,R.color.green_fresh_fab,R.color.green_fresh_fab_pressed));
        listToggleModeData.put(Config.getDeliveryClientId(),new ToggleModeData(Config.getDeliveryClientId(),R.drawable.ic_rides,R.color.theme_color,R.color.orange_rides_fab_pressed));

    }

    public  HashMap<String, ToggleModeData> getListToggleModeData() {
        if(listToggleModeData==null){
            initOfferingsList();
        }

        return listToggleModeData;
    }

}
