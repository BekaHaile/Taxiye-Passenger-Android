package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.retrofit.model.ServiceType;
import product.clicklabs.jugnoo.retrofit.model.ServiceTypeValue;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements GACategory, GAAction {


    Activity activity;
    DrawerLayout drawerLayout;

    //Top RL
    public RelativeLayout topRl;
    public ImageView imageViewMenu;
    public TextView textViewTitle;
    public Button buttonCheckServer;
    public ImageView imageViewHelp, imageViewScheduleRide;
    public ImageView imageViewBack;
    public TextView tvScheduleRidePopup, tvCancel;
    public ImageView imageViewShadow;

    public TopBar(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        setupTopBar();
    }

    private void setupTopBar() {
        topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
        imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
        textViewTitle = (TextView) drawerLayout.findViewById(R.id.textViewTitle);

        textViewTitle.setTypeface(Fonts.avenirNext(activity));
        textViewTitle.setTextColor(activity.getResources().getColor(R.color.text_color));
//        textViewTitle.getPaint().setShader(FeedUtils.textColorGradient(activity, textViewTitle));
        buttonCheckServer = (Button) drawerLayout.findViewById(R.id.buttonCheckServer);
        imageViewHelp = (ImageView) drawerLayout.findViewById(R.id.imageViewHelp);
        imageViewScheduleRide = (ImageView) drawerLayout.findViewById(R.id.imageViewScheduleRide);

        imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
        imageViewBack.setVisibility(View.GONE);
        tvScheduleRidePopup = (TextView) drawerLayout.findViewById(R.id.tvScheduleRidePopup);
        tvScheduleRidePopup.setTypeface(Fonts.mavenRegular(activity));
        imageViewShadow = drawerLayout.findViewById(R.id.imageViewShadow);

		tvCancel = drawerLayout.findViewById(R.id.tvCancel);


        //Top bar events
        topRl.setOnClickListener(topBarOnClickListener);
        imageViewMenu.setOnClickListener(topBarOnClickListener);
        buttonCheckServer.setOnClickListener(topBarOnClickListener);
        imageViewScheduleRide.setOnClickListener(topBarOnClickListener);
		tvCancel.setOnClickListener(topBarOnClickListener);

        buttonCheckServer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showToast(activity, Config.getServerUrlName());
                return false;
            }
        });

        imageViewHelp.setOnClickListener(topBarOnClickListener);
        imageViewBack.setOnClickListener(topBarOnClickListener);

        textViewTitle.setText(activity.getResources().getString(R.string.rides));


    }

    private View.OnClickListener topBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.topRl:
                    break;

                case R.id.imageViewMenu:
                    //activity.startActivity(new Intent(activity, FreshActivity.class));
                    drawerLayout.openDrawer(GravityCompat.START);
                    GAUtils.event(JUGNOO, RIDES + HOME, LEFT_MENU_ICON + CLICKED);

                    break;

                case R.id.buttonCheckServer:
                    break;
                case R.id.imageViewScheduleRide:
                    openScheduleFragment(Data.autoData.getServiceTypeSelected(), true);
                    break;

                case R.id.imageViewHelp:
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).sosDialog(activity);
                        try {
                            if (PassengerScreenMode.P_REQUEST_FINAL == HomeActivity.passengerScreenMode) {
                                GAUtils.event(RIDES, DRIVER_ENROUTE, HELP + GAAction.BUTTON + CLICKED);
                            } else if (PassengerScreenMode.P_DRIVER_ARRIVED == ((HomeActivity) activity).passengerScreenMode) {
                            } else if (PassengerScreenMode.P_IN_RIDE == ((HomeActivity) activity).passengerScreenMode) {
                                GAUtils.event(RIDES, RIDE + IN_PROGRESS, HELP + GAAction.BUTTON + CLICKED);
                            } else if (PassengerScreenMode.P_RIDE_END == HomeActivity.passengerScreenMode) {
                                GAUtils.event(RIDES, FEEDBACK, HELP + GAAction.BUTTON + CLICKED);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    break;

                case R.id.imageViewBack:
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).performBackpressed();
                    }
                    break;
                case R.id.tvCancel:
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).cancelClick();
                    }
                    break;

            }
        }
    };

    public void openScheduleFragment(ServiceType serviceType, boolean openSchedule) {
        ((HomeActivity) activity).scheduleRideContainer.setVisibility(View.VISIBLE);
        ((HomeActivity) activity).getTransactionUtils().openScheduleRideFragment(((HomeActivity) activity), ((HomeActivity) activity).scheduleRideContainer, serviceType, openSchedule);
        imageViewBack.setVisibility(View.VISIBLE);
        imageViewMenu.setVisibility(View.GONE);
        tvScheduleRidePopup.setVisibility(View.GONE);
        if (serviceType != null && serviceType.getSupportedRideTypes() != null && serviceType.getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())) {
            textViewTitle.setText(activity.getString(R.string.rentals));
        } else if (serviceType != null && serviceType.getSupportedRideTypes() != null && serviceType.getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType())) {
            textViewTitle.setText(activity.getString(R.string.out_station));
        } else {
            textViewTitle.setText(activity.getString(R.string.schedule_a_ride));
        }
    }


    public void setTopBarState(boolean defaultState, String title) {
        imageViewMenu.setVisibility(View.VISIBLE);
        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_INITIAL
                || HomeActivity.passengerScreenMode == PassengerScreenMode.P_SEARCH
                || HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
            imageViewHelp.setVisibility(View.GONE);

        } else {
            imageViewHelp.setVisibility(View.VISIBLE);

        }
        imageViewBack.setVisibility(View.GONE);
        textViewTitle.setText(activity.getResources().getString(R.string.rides));
        if (!defaultState) {
            imageViewMenu.setVisibility(View.GONE);
            imageViewHelp.setVisibility(View.GONE);
            imageViewBack.setVisibility(View.VISIBLE);
            textViewTitle.setText(title);
        }
    }

}
