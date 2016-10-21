package product.clicklabs.jugnoo.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
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
    public FloatingActionButton fabDeliveryTest;
    public FloatingActionButton fabMealsTest;
    public FloatingActionButton fabFreshTest;
    public FloatingActionButton fabAutosTest;
    public FloatingActionButton fabGroceryTest;
    public View view;
    private boolean isOpened;
    //public View fabExtra;

    public FABViewTest(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        initComponent();
    }

    private void initComponent(){
        try {
//            relativeLayoutFABTest = (RelativeLayout) view.findViewById(R.id.relativeLayoutFABTest);
            relativeLayoutFABTest = (RelativeLayout) view;
            menuLabelsRightTest = (FloatingActionMenu) view.findViewById(R.id.menu_labels_right_Test);
            fabDeliveryTest = (FloatingActionButton) view.findViewById(R.id.fabDeliveryTest);
            fabMealsTest = (FloatingActionButton) view.findViewById(R.id.fabMealsTest);
            fabFreshTest = (FloatingActionButton) view.findViewById(R.id.fabFreshTest);
            fabAutosTest = (FloatingActionButton) view.findViewById(R.id.fabAutosTest);
            fabGroceryTest = (FloatingActionButton) view.findViewById(R.id.fabGroceryTest);
            //fabExtra = (View) activity.findViewById(R.id.fabExtra);
            //fabExtra.setVisibility(View.GONE);
            menuLabelsRightTest.setIconAnimated(true);
            menuLabelsRightTest.setClosedOnTouchOutside(true);
            fabDeliveryTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabMealsTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabFreshTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabGroceryTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabAutosTest.setLabelTextColor(activity.getResources().getColor(R.color.black));
            fabDeliveryTest.setOnClickListener(clickListener);
            fabGroceryTest.setOnClickListener(clickListener);
            fabMealsTest.setOnClickListener(clickListener);
            fabFreshTest.setOnClickListener(clickListener);
            fabAutosTest.setOnClickListener(clickListener);
            relativeLayoutFABTest.setVisibility(View.GONE);
            menuLabelsRightTest.setMenuButtonColorNormal(activity.getResources().getColor(R.color.white));
            menuLabelsRightTest.setMenuButtonColorPressed(activity.getResources().getColor(R.color.grey_light));
            menuLabelsRightTest.setMenuButtonColorRipple(activity.getResources().getColor(R.color.grey_light_alpha));
            //menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_jeanie);


        } catch (Exception e) {
            e.printStackTrace();
        }
        //setFABButtons();

        menuLabelsRightTest.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    isOpened = true;
                    if(activity instanceof HomeActivity){
                        ((HomeActivity)activity).getViewSlidingExtra().setVisibility(View.VISIBLE);
                        ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(false);
                    }
                } else {
                    isOpened = false;
                    if(activity instanceof HomeActivity){
                        ((HomeActivity)activity).getViewSlidingExtra().setVisibility(View.GONE);
                        ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(true);
                    }
                }
            }
        });

        createCustomAnimation();

        /*menuLabelsRightTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuLabelsRightTest.isOpened()){
                    menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_autos);
                    if(activity instanceof HomeActivity){
                        ((HomeActivity)activity).getViewSlidingExtra().setVisibility(View.GONE);
                        ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(true);
                    } else{
                        menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_meals);
                        if(activity instanceof HomeActivity){
                            ((HomeActivity)activity).getViewSlidingExtra().setVisibility(View.VISIBLE);
                            ((HomeActivity)activity).getSlidingBottomPanel().getSlidingUpPanelLayout().setEnabled(false);
                        }
                    }
                }
            }
        });*/




        /*fabExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuLabelsRight.isOpened()) {
                    menuLabelsRight.close(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabExtra.setVisibility(View.GONE);
                            //setRelativeLayoutFABVisibility(null);
                            if(activity instanceof HomeActivity){
                                setRelativeLayoutFABVisibility(HomeActivity.passengerScreenMode);
                            } else if(activity instanceof FreshActivity){
                                setRelativeLayoutFABVisibility(null);
                            }
                            setFABMenuDrawable();
                        }
                    }, 300);
                } else {
                    //menuLabelsRight.open(true);
                    //fabExtra.setVisibility(View.VISIBLE);
                }

            }
        });*/
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
                        ? R.drawable.ic_fab_jeanie : R.drawable.ic_cross_et);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuLabelsRightTest.setIconToggleAnimatorSet(set);
    }

    public void setRelativeLayoutFABVisibility(PassengerScreenMode passengerScreenMode){
        //relativeLayoutFAB.setVisibility(View.INVISIBLE);
        try {
            if(Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                    (Data.userData.getFreshEnabled() == 1 || Data.userData.getMealsEnabled() == 1 || Data.userData.getDeliveryEnabled() == 1 || Data.userData.getGroceryEnabled() == 1)
                    && Data.userData.getIntegratedJugnooEnabled() == 1) {
                if (passengerScreenMode != null) {
                    if ((passengerScreenMode == PassengerScreenMode.P_INITIAL
                            && !((HomeActivity) activity).confirmedScreenOpened)
                            || passengerScreenMode == PassengerScreenMode.P_ASSIGNING
                            || passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED
                            || passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL
                            || passengerScreenMode == PassengerScreenMode.P_IN_RIDE
                            || passengerScreenMode == PassengerScreenMode.P_RIDE_END
                            || ((HomeActivity)activity).dropLocationSearched) {
                        relativeLayoutFABTest.setVisibility(View.INVISIBLE);
                        //((HomeActivity) activity).getImageViewFabFake().setVisibility(View.VISIBLE);
                        //((HomeActivity) activity).getRelativeLayoutFABTest().setVisibility(View.VISIBLE);
                    } else {
                        relativeLayoutFABTest.setVisibility(View.VISIBLE);
                        menuLabelsRightTest.close(true);
                        fabFreshTest.setVisibility(View.INVISIBLE);
                        fabMealsTest.setVisibility(View.INVISIBLE);
                        fabGroceryTest.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (activity instanceof FreshActivity) {
                        relativeLayoutFABTest.setVisibility(View.INVISIBLE);
                        //((FreshActivity) activity).getImageViewFabFake().setVisibility(View.VISIBLE);
                    } else {
                        relativeLayoutFABTest.setVisibility(View.INVISIBLE);
                    }
                }
            } else{
                relativeLayoutFABTest.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FloatingActionMenu getMenuLabelsRight() {
        return menuLabelsRightTest;
    }

    public void setFABMenuDrawable(){
        String currentOpenedOffering = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
        if (menuLabelsRightTest.isOpened()) {
            if(Config.getAutosClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_auto_test);
            } else if(Config.getFreshClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_fresh_test);
            } else if(Config.getMealsClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_meals_test);
            } else if(Config.getGroceryClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_grocery_test);
            }
        } else {
            menuLabelsRightTest.getMenuIconView().setImageResource(R.drawable.ic_fab_menu_selector);
        }

        fabDeliveryTest.setVisibility(View.GONE);
        //fabGrocery.setVisibility(View.GONE);
        if(Config.getAutosClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabAutosTest.setVisibility(View.GONE);
        } else if(Config.getFreshClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabFreshTest.setVisibility(View.GONE);
            //fabAutos.setVisibility(View.VISIBLE);
        } else if(Config.getMealsClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabMealsTest.setVisibility(View.GONE);
            //fabAutos.setVisibility(View.VISIBLE);
        } else if(Config.getGroceryClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabGroceryTest.setVisibility(View.GONE);
        }

        //setFABButtons();
    }

    public void setFABButtons(){
        try {
            if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0)
                    && (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1)){
                relativeLayoutFABTest.setVisibility(View.GONE);
            } else {
                relativeLayoutFABTest.setVisibility(View.VISIBLE);
                if (Data.userData.getFreshEnabled() != 1) {
                    fabFreshTest.setVisibility(View.GONE);
                }

                if (Data.userData.getMealsEnabled() != 1) {
                    fabMealsTest.setVisibility(View.GONE);
                }

                if(Data.userData.getGroceryEnabled() != 1){
                    fabGroceryTest.setVisibility(View.GONE);
                }

                if (Data.userData.getDeliveryEnabled() != 1) {
                    fabDeliveryTest.setVisibility(View.GONE);
                }
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
            }
            final LatLng finalLatLng = latLng;
            menuLabelsRightTest.close(true);
            switch (v.getId()) {
                case R.id.fabDeliveryTest:
                    //Toast.makeText(HomeActivity.this, "Delivery", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.fabMealsTest:
                    //Toast.makeText(HomeActivity.this, "Meals", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().logEvent(FirebaseEvents.BUTTON+"_"+FirebaseEvents.MEALS, null);
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
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getGroceryClientId(), finalLatLng, false);
                        }
                    }, 300);
                    break;

            }
            //fabExtra.performClick();
        }
    };

}
