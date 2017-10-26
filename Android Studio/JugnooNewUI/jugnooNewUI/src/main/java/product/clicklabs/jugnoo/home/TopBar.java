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

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements  GACategory, GAAction {


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
        textViewTitle.setTextColor(activity.getResources().getColor(R.color.text_color));
//        textViewTitle.getPaint().setShader(FeedUtils.textColorGradient(activity, textViewTitle));
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
            textViewTitle.setText(activity.getResources().getString(R.string.rides));
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
                    GAUtils.event(JUGNOO, RIDES+HOME, LEFT_MENU_ICON+CLICKED);

                    break;

                case R.id.buttonCheckServer:
                    break;

                case R.id.imageViewHelp:
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).sosDialog(activity);
                        try {
                            if(PassengerScreenMode.P_REQUEST_FINAL == HomeActivity.passengerScreenMode){
                                GAUtils.event(RIDES, DRIVER_ENROUTE, HELP+GAAction.BUTTON+CLICKED);
                            } else if (PassengerScreenMode.P_DRIVER_ARRIVED == ((HomeActivity) activity).passengerScreenMode) {
                            } else if (PassengerScreenMode.P_IN_RIDE == ((HomeActivity) activity).passengerScreenMode) {
                                GAUtils.event(RIDES, RIDE+IN_PROGRESS, HELP+GAAction.BUTTON+CLICKED);
                            } else if (PassengerScreenMode.P_RIDE_END == HomeActivity.passengerScreenMode){
                                GAUtils.event(RIDES, FEEDBACK, HELP+GAAction.BUTTON+CLICKED);
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

                case R.id.imageViewSearchIcon:

                    break;
                case R.id.relativeLayoutNotification:
                    ((HomeActivity) activity).openNotification();
                    break;
            }
        }
    };




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
