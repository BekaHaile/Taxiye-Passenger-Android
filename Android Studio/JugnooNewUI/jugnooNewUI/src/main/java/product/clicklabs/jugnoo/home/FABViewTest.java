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
import product.clicklabs.jugnoo.config.Config;
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
            fabPayTest = (FloatingActionButton) view.findViewById(R.id.fabPayTest);
            fabFeedTest = (FloatingActionButton) view.findViewById(R.id.fabFeedTest);
            menuLabelsRightTest.setIconAnimated(true);
            menuLabelsRightTest.setClosedOnTouchOutside(true);
            fabMealsTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabFreshTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabMenusTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabAutosTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabPayTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabFeedTest.setLabelTextColor(ContextCompat.getColor(activity, R.color.black));
            fabMenusTest.setOnClickListener(clickListener);
            fabPayTest.setOnClickListener(clickListener);
            fabMealsTest.setOnClickListener(clickListener);
            fabFreshTest.setOnClickListener(clickListener);
            fabAutosTest.setOnClickListener(clickListener);
            fabFeedTest.setOnClickListener(clickListener);
            relativeLayoutFABTest.setVisibility(View.GONE);
            menuLabelsRightTest.setMenuButtonColorNormal(activity.getResources().getColor(R.color.white));
            menuLabelsRightTest.setMenuButtonColorPressed(activity.getResources().getColor(R.color.grey_light));
            menuLabelsRightTest.setMenuButtonColorRipple(activity.getResources().getColor(R.color.grey_light_alpha));


            rlGenieHelp = (RelativeLayout) view.findViewById(R.id.rlGenieHelp);
            tvGenieHelp = (TextView) view.findViewById(R.id.tvGenieHelp);
            ivJeanieHelp = (ImageView) view.findViewById(R.id.ivJeanieHelp);
            tvGenieExpandMessage = (TextView) view.findViewById(R.id.tvGenieExpandMessage);

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
                if(Data.userData == null || Data.userData.getExpandJeanie() == 0) {
                    menuLabelsRightTest.getMenuIconView().setImageResource(menuLabelsRightTest.isOpened()
                            ? R.drawable.ic_fab_jeanie : R.drawable.ic_fab_cross);
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
            if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0)
                    && (Data.userData.getMenusEnabled() == 0) && (Data.userData.getPayEnabled() == 0)
                    && (Data.userData.getFeedEnabled() == 0)
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

                if (Data.userData.getFeedEnabled() != 1) {
                    fabFeedTest.setVisibility(View.GONE);
                } else {
                    if(isOpened) {
                        fabFeedTest.setVisibility(View.VISIBLE);
                    }
                }

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

            if (Data.userData.getMenusEnabled() == 1) {
                fabMenusTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getPayEnabled() == 1) {
                fabPayTest.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getFeedEnabled() == 1) {
                fabFeedTest.setVisibility(View.VISIBLE);
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
            menuLabelsRightTest.close(true);
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

            }
            GAUtils.event(JUGNOO, GENIE+OPEN, selectedOffering+SELECTED);

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
        if(visibility == View.VISIBLE
                && Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1
                && Data.userData.getIntegratedJugnooEnabled() == 1){
            relativeLayoutFABTest.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutFABTest.setVisibility(View.GONE);
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
        } else if (clientId.equalsIgnoreCase(Config.getPayClientId())) {
            return PAY;
        } else if (clientId.equalsIgnoreCase(Config.getFeedClientId())) {
            return FEED;
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

}
