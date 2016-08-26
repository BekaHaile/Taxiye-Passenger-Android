package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionButton;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;

/**
 * Created by ankit on 8/22/16.
 */
public class FABView {
    Activity activity;
    public RelativeLayout relativeLayoutFAB;
    public FloatingActionMenu menuLabelsRight;
    public FloatingActionButton fabDelivery;
    public FloatingActionButton fabMeals;
    public FloatingActionButton fabFresh;
    public FloatingActionButton fabAutos;
    public View fabExtra;

    public FABView(Activity activity) {
        this.activity = activity;
        initComponent();
    }

    private void initComponent(){
        relativeLayoutFAB = (RelativeLayout) activity.findViewById(R.id.relativeLayoutFAB);
        menuLabelsRight = (FloatingActionMenu) activity.findViewById(R.id.menu_labels_right);
        menuLabelsRight.setIconAnimated(false);
        fabDelivery = (FloatingActionButton) activity.findViewById(R.id.fabDelivery);
        fabMeals = (FloatingActionButton) activity.findViewById(R.id.fabMeals);
        fabFresh = (FloatingActionButton) activity.findViewById(R.id.fabFresh);
        fabAutos = (FloatingActionButton) activity.findViewById(R.id.fabAutos);
        fabExtra = (View) activity.findViewById(R.id.fabExtra);
        //fabExtra.setVisibility(View.GONE);
        menuLabelsRight.setIconAnimated(false);
        fabDelivery.setLabelTextColor(activity.getResources().getColor(R.color.black));
        fabMeals.setLabelTextColor(activity.getResources().getColor(R.color.black));
        fabFresh.setLabelTextColor(activity.getResources().getColor(R.color.black));
        fabAutos.setLabelTextColor(activity.getResources().getColor(R.color.black));
        fabDelivery.setOnClickListener(clickListener);
        fabMeals.setOnClickListener(clickListener);
        fabFresh.setOnClickListener(clickListener);
        fabAutos.setOnClickListener(clickListener);
        relativeLayoutFAB.setVisibility(View.INVISIBLE);
        setFABButtons();

        menuLabelsRight.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                setFABMenuDrawable();
                //setFABButtons();
                if (opened) {
                    text = "Menu opened";
                    fabExtra.setVisibility(View.VISIBLE);
                    relativeLayoutFAB.setVisibility(View.VISIBLE);
                } else {
                    text = "Menu closed";
                    fabExtra.setVisibility(View.GONE);
                    if(activity instanceof HomeActivity){
                        setRelativeLayoutFABVisibility(HomeActivity.passengerScreenMode);
                    } else if(activity instanceof FreshActivity){
                        setRelativeLayoutFABVisibility(null);
                    }
                }
            }
        });

        fabExtra.setOnClickListener(new View.OnClickListener() {
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
        });
    }

    public void setRelativeLayoutFABVisibility(PassengerScreenMode passengerScreenMode){
        //relativeLayoutFAB.setVisibility(View.INVISIBLE);
        if(passengerScreenMode != null) {
            if ((passengerScreenMode == PassengerScreenMode.P_INITIAL
                    && !((HomeActivity)activity).confirmedScreenOpened)
                    || passengerScreenMode == PassengerScreenMode.P_RIDE_END) {
                relativeLayoutFAB.setVisibility(View.INVISIBLE);
                ((HomeActivity)activity).getImageViewFabFake().setVisibility(View.VISIBLE);
            }else{
                relativeLayoutFAB.setVisibility(View.VISIBLE);
            }
        } else{
            if(activity instanceof FreshActivity){
                relativeLayoutFAB.setVisibility(View.VISIBLE);
            } else {
                relativeLayoutFAB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public FloatingActionMenu getMenuLabelsRight() {
        return menuLabelsRight;
    }

    public void setFABMenuDrawable(){
        String currentOpenedOffering = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
        if (menuLabelsRight.isOpened()) {
            if(Config.getAutosClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRight.getMenuIconView().setImageResource(R.drawable.ic_fab_autos_selected);
            } else if(Config.getFreshClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRight.getMenuIconView().setImageResource(R.drawable.ic_fab_fresh_selected);
            } else if(Config.getMealsClientId().equalsIgnoreCase(currentOpenedOffering)){
                menuLabelsRight.getMenuIconView().setImageResource(R.drawable.ic_fab_meals_selected);
            }
        } else {
            menuLabelsRight.getMenuIconView().setImageResource(R.drawable.ic_fab_menu);
        }

        fabDelivery.setVisibility(View.GONE);
        if(Config.getAutosClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabAutos.setVisibility(View.GONE);
        } else if(Config.getFreshClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabFresh.setVisibility(View.GONE);
        } else if(Config.getMealsClientId().equalsIgnoreCase(currentOpenedOffering)){
            fabMeals.setVisibility(View.GONE);
        }
        //setFABButtons();
    }

    public void setFABButtons(){
        if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0) && (Data.userData.getDeliveryEnabled() == 0)){
            relativeLayoutFAB.setVisibility(View.GONE);
        } else {
            relativeLayoutFAB.setVisibility(View.INVISIBLE);
            if (Data.userData.getFreshEnabled() == 1) {
                fabFresh.setVisibility(View.VISIBLE);
            } else {
                fabFresh.setVisibility(View.GONE);
            }

            if (Data.userData.getMealsEnabled() == 1) {
                fabMeals.setVisibility(View.VISIBLE);
            } else {
                fabMeals.setVisibility(View.GONE);
            }

            if (Data.userData.getDeliveryEnabled() == 1) {
                fabDelivery.setVisibility(View.VISIBLE);
            } else {
                fabDelivery.setVisibility(View.GONE);
            }
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LatLng latLng = new LatLng(Data.latitude, Data.longitude);
            if(activity instanceof HomeActivity){
                latLng = ((HomeActivity)activity).getCurrentPlaceLatLng();
            } else if(activity instanceof FreshActivity){
                latLng = ((FreshActivity)activity).getCurrentPlaceLatLng();
            }
            final LatLng finalLatLng = latLng;
            switch (v.getId()) {
                case R.id.fabDelivery:
                    //Toast.makeText(HomeActivity.this, "Delivery", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.fabMeals:
                    //Toast.makeText(HomeActivity.this, "Meals", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId(), finalLatLng);
                        }
                    }, 250);
                    break;
                case R.id.fabFresh:
                    //Toast.makeText(HomeActivity.this, "Fresh", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId(), finalLatLng);
                        }
                    }, 250);
                    break;
                case R.id.fabAutos:
                    //Toast.makeText(HomeActivity.this, "Autos", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), finalLatLng);
                        }
                    }, 250);
                    break;
            }
            fabExtra.performClick();
        }
    };

}
