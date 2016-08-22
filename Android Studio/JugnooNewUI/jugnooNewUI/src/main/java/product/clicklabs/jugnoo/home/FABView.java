package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionButton;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;

/**
 * Created by ankit on 8/22/16.
 */
public class FABView {
    Activity activity;
    private RelativeLayout relativeLayoutFAB;
    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton fabDelivery;
    private FloatingActionButton fabMeals;
    private FloatingActionButton fabFresh;
    private FloatingActionButton fabAutos;
    private View fabExtra;

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
        setFABButtons();

        menuLabelsRight.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                if (opened) {
                    text = "Menu opened";
                    fabExtra.setVisibility(View.VISIBLE);
                } else {
                    text = "Menu closed";
                    fabExtra.setVisibility(View.GONE);
                }
                //Toast.makeText(FreshActivity.this, text, Toast.LENGTH_SHORT).show();
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
                        }
                    }, 300);
                } else {
                    //menuLabelsRight.open(true);
                    //fabExtra.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setFABButtons(){
        if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0) && (Data.userData.getDeliveryEnabled() == 0)){
            relativeLayoutFAB.setVisibility(View.GONE);
        } else {
            relativeLayoutFAB.setVisibility(View.VISIBLE);
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
            switch (v.getId()) {
                case R.id.fabDelivery:
                    //Toast.makeText(HomeActivity.this, "Delivery", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.fabMeals:
                    //Toast.makeText(HomeActivity.this, "Meals", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId());
                        }
                    }, 250);
                    break;
                case R.id.fabFresh:
                    //Toast.makeText(HomeActivity.this, "Fresh", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId());
                        }
                    }, 250);
                    break;
                case R.id.fabAutos:
                    //Toast.makeText(HomeActivity.this, "Autos", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId());
                        }
                    }, 250);
                    break;
            }
            fabExtra.performClick();
        }
    };
}
