package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements FirebaseEvents {


    Activity activity;
    DrawerLayout drawerLayout;

    //Top RL
    public RelativeLayout topRl, relativeLayoutNotification;
    public ImageView imageViewMenu, imageViewSearchIcon;
    public TextView textViewTitle;
    public Button buttonCheckServer;
    public ImageView imageViewHelp;
    public ImageView imageViewBack, imageViewDelete;
    public TextView textViewAdd;
    public EditText editTextDeliveryAddress;

    public TopBar(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        setupTopBar();
    }

    private void setupTopBar() {
        topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
        relativeLayoutNotification = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutNotification);
        imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
        imageViewSearchIcon = (ImageView) drawerLayout.findViewById(R.id.imageViewSearchIcon);
        textViewTitle = (TextView) drawerLayout.findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(activity));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));
        buttonCheckServer = (Button) drawerLayout.findViewById(R.id.buttonCheckServer);
        imageViewHelp = (ImageView) drawerLayout.findViewById(R.id.imageViewHelp);

        imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
        imageViewBack.setVisibility(View.GONE);
        imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);
        textViewAdd = (TextView) drawerLayout.findViewById(R.id.textViewAdd);
        textViewAdd.setTypeface(Fonts.mavenRegular(activity));


        //Top bar events
        topRl.setOnClickListener(topBarOnClickListener);
        relativeLayoutNotification.setOnClickListener(topBarOnClickListener);
        imageViewMenu.setOnClickListener(topBarOnClickListener);
        buttonCheckServer.setOnClickListener(topBarOnClickListener);

        buttonCheckServer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showToast(activity, Config.getServerUrlName());
                FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
                return false;
            }
        });

        imageViewSearchIcon.setOnClickListener(topBarOnClickListener);
        imageViewHelp.setOnClickListener(topBarOnClickListener);
        imageViewBack.setOnClickListener(topBarOnClickListener);
        imageViewDelete.setOnClickListener(topBarOnClickListener);
        textViewAdd.setOnClickListener(topBarOnClickListener);

        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());

        if (activity instanceof HomeActivity) {
            imageViewSearchIcon.setVisibility(View.GONE);
            textViewTitle.setText(activity.getResources().getString(R.string.autos));
        }


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
                    FlurryEventLogger.event(FlurryEventNames.MENU_LOOKUP);
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_MENU_CLICKED, null);

                    try {
                        if (PassengerScreenMode.P_IN_RIDE == ((HomeActivity) activity).passengerScreenMode) {
                            FlurryEventLogger.eventGA(Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "Ride Start", "menu");
                        } else {
                            Bundle bundle = new Bundle();
                            MyApplication.getInstance().logEvent(TRANSACTION+"_"+HOME_SCREEN+"_"+MENU, bundle);

                            FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION,
                                    "Home Screen", "menu");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.buttonCheckServer:
                    break;

                case R.id.imageViewHelp:
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).sosDialog(activity);
                        try {
                            if (PassengerScreenMode.P_DRIVER_ARRIVED == ((HomeActivity) activity).passengerScreenMode) {
                                //FlurryEventLogger.eventGA(JUGNOO_CASH_ADDED_WHEN_DRIVER_ARRIVED);
                            } else if (PassengerScreenMode.P_IN_RIDE == ((HomeActivity) activity).passengerScreenMode) {
                                Bundle bundle = new Bundle();
                                MyApplication.getInstance().logEvent(TRANSACTION+"_"+HOME_SCREEN+"_"+Constants.HELP, bundle);
                                FlurryEventLogger.eventGA(Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "Ride Start", "help");
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

                case R.id.imageViewDelete:
                    break;

                case R.id.textViewAdd:
                    break;

//                case R.id.imageViewAppToggle:
//                    if (activity instanceof HomeActivity) {
//                        if (((HomeActivity) activity).map != null
//                                && ((HomeActivity) activity).mapStateListener != null
//                                && ((HomeActivity) activity).mapStateListener.isMapSettled()) {
//                            Data.latitude = ((HomeActivity) activity).map.getCameraPosition().target.latitude;
//                            Data.longitude = ((HomeActivity) activity).map.getCameraPosition().target.longitude;
//                        }
//                        try {
//                            if (!Data.userData.getFatafatUrlLink().trim().equalsIgnoreCase("")) {
//                                Log.v("fatafat url link", "---> " + Data.userData.getFatafatUrlLink());
//                                activity.startActivity(new Intent(activity, WebActivity.class));
//                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                            } else {
//                                CustomAppLauncher.launchApp(activity, AccessTokenGenerator.FATAFAT_FRESH_PACKAGE);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            CustomAppLauncher.launchApp(activity, AccessTokenGenerator.FATAFAT_FRESH_PACKAGE);
//                        }
//                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_JUGNOO_FRESH_CLICKED, null);
//
//                        Bundle bundle = new Bundle();
//                        MyApplication.getInstance().logEvent(TRANSACTION+"_"+HOME_SCREEN+"_"+FRESH, bundle);
//
//                        FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "Home Screen", "fresh");
//                    }
//                    break;

                case R.id.imageViewSearchIcon:

                    break;
                case R.id.relativeLayoutNotification:
                    ((HomeActivity) activity).openNotification();
                    break;
            }
        }
    };


    public void setUserData() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private StateListDrawable getStateListDrawable(int resourceNormal, int resourcePressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                activity.getResources().getDrawable(resourcePressed));
        stateListDrawable.addState(new int[]{},
                activity.getResources().getDrawable(resourceNormal));
        return stateListDrawable;
    }



    public void setTopBarState(Context context, boolean defaultState, String title) {
        imageViewMenu.setVisibility(View.VISIBLE);
        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_INITIAL
                || HomeActivity.passengerScreenMode == PassengerScreenMode.P_SEARCH
                || HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
            imageViewHelp.setVisibility(View.GONE);
        } else {
            imageViewHelp.setVisibility(View.VISIBLE);
        }
        imageViewBack.setVisibility(View.GONE);
        textViewTitle.setText(activity.getResources().getString(R.string.autos));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(context, textViewTitle));

        if (!defaultState) {
            imageViewMenu.setVisibility(View.GONE);
            imageViewHelp.setVisibility(View.GONE);
            imageViewBack.setVisibility(View.VISIBLE);
            textViewTitle.setText(title);
            textViewTitle.getPaint().setShader(Utils.textColorGradient(context, textViewTitle));
        }
    }

}
