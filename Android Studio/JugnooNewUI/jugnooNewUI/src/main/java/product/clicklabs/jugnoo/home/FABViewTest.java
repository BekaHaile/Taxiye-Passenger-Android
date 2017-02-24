package product.clicklabs.jugnoo.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.activities.MainActivity;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionButton;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;

/**
 * Created by ankit on 8/22/16.
 */
public class FABViewTest {
    Activity activity;
    public RelativeLayout relativeLayoutFABTest;
    public FloatingActionMenu menuLabelsRightTest;
//    public FloatingActionButton fabDeliveryTest;
    public FloatingActionButton fabMealsTest;
    public FloatingActionButton fabFreshTest;
    public FloatingActionButton fabAutosTest;
    public FloatingActionButton fabGroceryTest;
    public FloatingActionButton fabMenusTest;
    public FloatingActionButton fabPayTest;
    public View view;
    private boolean isOpened;
    private final String GENIE_OPEN = "Genie Open";
    //public View fabExtra;

    private RelativeLayout rlGenieHelp;
    private TextView tvGenieHelp;
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
//            relativeLayoutFABTest = (RelativeLayout) view.findViewById(R.id.relativeLayoutFABTest);
            relativeLayoutFABTest = (RelativeLayout) view;
            menuLabelsRightTest = (FloatingActionMenu) view.findViewById(R.id.menu_labels_right_Test);
//            fabDeliveryTest = (FloatingActionButton) view.findViewById(R.id.fabDeliveryTest);
            fabMealsTest = (FloatingActionButton) view.findViewById(R.id.fabMealsTest);
            fabFreshTest = (FloatingActionButton) view.findViewById(R.id.fabFreshTest);
            fabAutosTest = (FloatingActionButton) view.findViewById(R.id.fabAutosTest);
            fabGroceryTest = (FloatingActionButton) view.findViewById(R.id.fabGroceryTest);
            fabMenusTest = (FloatingActionButton) view.findViewById(R.id.fabMenusTest);
            fabPayTest = (FloatingActionButton) view.findViewById(R.id.fabPayTest);
            //fabExtra = (View) activity.findViewById(R.id.fabExtra);
            //fabExtra.setVisibility(View.GONE);
            menuLabelsRightTest.setIconAnimated(true);
            menuLabelsRightTest.setClosedOnTouchOutside(true);
//            fabDeliveryTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabMealsTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabFreshTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabGroceryTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabMenusTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabAutosTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabPayTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
//            fabDeliveryTest.setOnClickListener(clickListener);
            fabGroceryTest.setOnClickListener(clickListener);
            fabMenusTest.setOnClickListener(clickListener);
            fabPayTest.setOnClickListener(clickListener);
            fabMealsTest.setOnClickListener(clickListener);
            fabFreshTest.setOnClickListener(clickListener);
            fabAutosTest.setOnClickListener(clickListener);
            relativeLayoutFABTest.setVisibility(View.GONE);
            menuLabelsRightTest.setMenuButtonColorNormal(activity.getResources().getColor(R.color.white));
            menuLabelsRightTest.setMenuButtonColorPressed(activity.getResources().getColor(R.color.grey_light));
            menuLabelsRightTest.setMenuButtonColorRipple(activity.getResources().getColor(R.color.grey_light_alpha));
            //menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_jeanie);


            rlGenieHelp = (RelativeLayout) view.findViewById(R.id.rlGenieHelp);
            tvGenieHelp = (TextView) view.findViewById(R.id.tvGenieHelp);
            ivJeanieHelp = (ImageView) view.findViewById(R.id.ivJeanieHelp);

            setRlGenieHelpBottomMargin(170f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setFABButtons();
        isOpened = false;
        menuLabelsRightTest.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                try {
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
                        FlurryEventLogger.event(Constants.INFORMATIVE, Events.GENIE, "Opened");
                    } else {
                        isOpened = false;
                        if(activity instanceof HomeActivity){
                            ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(true);
//                            ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);
                        }
                        ivJeanieHelp.setVisibility(View.GONE);
                        FlurryEventLogger.event(Constants.INFORMATIVE, Events.GENIE, "Closed");
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

        createCustomAnimation();


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
                menuLabelsRightTest.getMenuIconView().setImageResource(menuLabelsRightTest.isOpened()
                        ? R.drawable.ic_fab_jeanie : R.drawable.ic_fab_cross);
                if(activity instanceof HomeActivity) {
                    if (menuLabelsRightTest.isOpened()) {
                        ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.GONE);
                    } else {
                        ((HomeActivity) activity).getViewSlidingExtra().setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuLabelsRightTest.setIconToggleAnimatorSet(set);
    }


    public void setFABButtons(boolean toShowJeanieHelp){
        try {
            if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0)
                    && (Data.userData.getMenusEnabled() == 0) && (Data.userData.getPayEnabled() == 0)
                    && (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1)){
                relativeLayoutFABTest.setVisibility(View.GONE);
            } else {
                relativeLayoutFABTest.setVisibility(View.VISIBLE);
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

                if(Data.userData.getGroceryEnabled() != 1){
                    fabGroceryTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabGroceryTest.setVisibility(View.VISIBLE);
                    }
                }

                if(Data.userData.getMenusEnabled() != 1){
                    fabMenusTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabMenusTest.setVisibility(View.VISIBLE);
                    }
                }

                if(Data.userData.getPayEnabled() != 1){
                    fabPayTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabPayTest.setVisibility(View.VISIBLE);
                    }
                }

                /*if(activity instanceof HomeActivity) {
                    if (Prefs.with(activity).getInt(Constants.SHOW_GEANIE_HELP, 0) == 0) {
                        ((HomeActivity) activity).getRlGenieHelp().setVisibility(View.VISIBLE);
                    } else {
                        ((HomeActivity) activity).getRlGenieHelp().setVisibility(View.GONE);
                    }
                } else if(activity instanceof HomeActivity){
                    if (Prefs.with(activity).getInt(Constants.SHOW_GEANIE_HELP, 0) == 0) {
                        ((HomeActivity) activity).getRlGenieHelp().setVisibility(View.VISIBLE);
                    } else {
                        ((HomeActivity) activity).getRlGenieHelp().setVisibility(View.GONE);
                    }
                }*/

                setRlGenieHelpVisibility();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

    private void setButtonsVisibilityOnOpen(){
        try {
            if (Data.userData.getFreshEnabled() == 1) {
                fabFreshTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getMealsEnabled() == 1) {
                fabMealsTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getGroceryEnabled() == 1) {
                fabGroceryTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getMenusEnabled() == 1) {
                fabMenusTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getPayEnabled() == 1) {
                fabPayTest.setVisibility(View.VISIBLE);
            }

//            if (Data.userData.getDeliveryEnabled() == 1) {
//                fabDeliveryTest.setVisibility(View.VISIBLE);
//            }
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
            menuLabelsRightTest.close(true);
            switch (v.getId()) {
//                case R.id.fabDeliveryTest:
                    //Toast.makeText(HomeActivity.this, "Delivery", Toast.LENGTH_SHORT).show();
//                    break;
                case R.id.fabMealsTest:
                    //Toast.makeText(HomeActivity.this, "Meals", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.MEALS, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.MEALS);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;
                case R.id.fabFreshTest:
                    //Toast.makeText(HomeActivity.this, "Fresh", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.FRESH, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.FRESH);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;
                case R.id.fabAutosTest:
                    //Toast.makeText(activity, "Autos", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.AUTO, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.AUTO);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;
                case R.id.fabGroceryTest:
                    //Toast.makeText(HomeActivity.this, "Autos", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.GROCERY, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.GROCERY);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getGroceryClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;
                case R.id.fabMenusTest:
                    //Toast.makeText(HomeActivity.this, "Autos", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.MENUS, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.MENUS);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMenusClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;
                case R.id.fabPayTest:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.PAY, null);
                            FlurryEventLogger.event(Constants.INFORMATIVE, GENIE_OPEN, FirebaseEvents.PAY);
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getPayClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;

            }
            //fabExtra.performClick();
        }
    };

    public void closeMenu(){
        try {
            menuLabelsRightTest.close(false);
            menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_jeanie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRlGenieHelpVisibility(){
        if(Data.userData.getShowJeanieHelpText() == 1
                && Prefs.with(activity).getInt(Constants.SP_SHOW_GEANIE_HELP, 0) == 0
                && !Data.isJeanieShownInSession()){
            handler.postDelayed(runnableJeanieHelpShow, 2000);
        } else {
            rlGenieHelp.setVisibility(View.GONE);
            handler.removeCallbacks(runnableJeanieHelpShow);
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
        relativeLayoutFABTest.setVisibility(visibility);
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

    public void setMenuLabelsRightTestPadding(float paddingBottom){
        float scale = activity.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (paddingBottom * scale + 0.5f);
        setMenuLabelsRightTestPadding(dpAsPixels);
    }

    public void setMenuLabelsRightTestPadding(int paddingBottom){
        menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, paddingBottom);
        setRlGenieHelpBottomMargin(paddingBottom + (int)(ASSL.Yscale() * 100f));
    }

    public void showTutorial(){
        if(Data.userData != null && Data.userData.getShowTutorial() == 1) {
            menuLabelsRightTest.open(true);
            ivJeanieHelp.setVisibility(View.VISIBLE);
            Data.userData.setShowTutorial(0);
        }
    }

}
