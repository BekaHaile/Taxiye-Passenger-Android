package sabkuchfresh.home;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.R;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Utils;
import com.sabkuchfresh.widgets.ProgressWheel;
import com.squareup.picasso.CircleTransform;

/**
 * Created by shankar on 4/8/16.
 */
public class MenuBar {

    Activity activity;
    DrawerLayout drawerLayout;

    //menu bar
    public LinearLayout menuLayout;

    public LinearLayout linearLayoutProfile;
    public ImageView imageViewProfile;
    public TextView textViewUserName, textViewViewAccount;


    public RelativeLayout relativeLayoutWallet;
    public TextView textViewWallet, textViewWalletValue;
    public ProgressWheel progressBarMenuPaytmWalletLoading;

    public RelativeLayout relativeLayoutRefer;
    public TextView textViewRefer;
    public ImageView imageViewRefer;

    public RelativeLayout relativeLayoutTransactions;
    public TextView textViewTransactions;
    public ImageView imageViewTransactions;

    public RelativeLayout relativeLayoutNotification;
    public TextView textViewNotification;

    public RelativeLayout relativeLayoutfatafat;
    public TextView textViewFatafat;

    public RelativeLayout relativeLayoutmeals;
    public TextView textViewmeals;

    public RelativeLayout relativeLayoutSupport;
    public TextView textViewSupport;

    public RelativeLayout relativeLayoutAbout;
    public TextView textViewAbout;

    public Buttonclicked buttonclicked;

    public MenuBar(Activity activity, DrawerLayout rootView, Buttonclicked buttonclicked) {
        this.activity = activity;
        this.drawerLayout = rootView;
        this.buttonclicked = buttonclicked;
        initComponents();
    }


    private void initComponents() {
        //Swipe menu
        menuLayout = (LinearLayout) drawerLayout.findViewById(R.id.menuLayout);


        linearLayoutProfile = (LinearLayout) drawerLayout.findViewById(R.id.linearLayoutProfile);
        imageViewProfile = (ImageView) drawerLayout.findViewById(R.id.imageViewProfile);
        textViewUserName = (TextView) drawerLayout.findViewById(R.id.textViewUserName);
        textViewUserName.setTypeface(Fonts.mavenRegular(activity));
        textViewViewAccount = (TextView) drawerLayout.findViewById(R.id.textViewViewAccount);
        textViewViewAccount.setTypeface(Fonts.latoRegular(activity));

        relativeLayoutfatafat = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutfatafat);
        textViewFatafat = (TextView) drawerLayout.findViewById(R.id.textViewFatafat);
        textViewFatafat.setTypeface(Fonts.mavenLight(activity));

        relativeLayoutmeals = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutmeals);
        textViewmeals = (TextView) drawerLayout.findViewById(R.id.textViewmeals);
        textViewmeals.setTypeface(Fonts.mavenLight(activity));

        relativeLayoutWallet = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutWallet);
        textViewWallet = (TextView) drawerLayout.findViewById(R.id.textViewWallet);
        textViewWallet.setTypeface(Fonts.mavenLight(activity));

        textViewWalletValue = (TextView) drawerLayout.findViewById(R.id.textViewWalletValue);
        textViewWalletValue.setTypeface(Fonts.latoRegular(activity));
        progressBarMenuPaytmWalletLoading = (ProgressWheel) drawerLayout.findViewById(R.id.progressBarMenuPaytmWalletLoading);
        progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);

        relativeLayoutRefer = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutRefer);
        textViewRefer = (TextView) drawerLayout.findViewById(R.id.textViewRefer);
        textViewRefer.setTypeface(Fonts.mavenLight(activity));
        imageViewRefer = (ImageView) drawerLayout.findViewById(R.id.imageViewRefer);

        relativeLayoutTransactions = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutTransactions);
        textViewTransactions = (TextView) drawerLayout.findViewById(R.id.textViewTransactions);
        textViewTransactions.setTypeface(Fonts.mavenLight(activity));
        imageViewTransactions = (ImageView) drawerLayout.findViewById(R.id.imageViewTransactions);

        relativeLayoutNotification = (RelativeLayout) drawerLayout.findViewById(R.id.relative_layout_notification);
        textViewNotification = (TextView) drawerLayout.findViewById(R.id.textView_notification);
        textViewNotification.setTypeface(Fonts.mavenLight(activity));

        relativeLayoutSupport = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutSupport);
        textViewSupport = (TextView) drawerLayout.findViewById(R.id.textViewSupport);
        textViewSupport.setTypeface(Fonts.mavenLight(activity));

        relativeLayoutAbout = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutAbout);
        relativeLayoutAbout.setVisibility(View.GONE);
        textViewAbout = (TextView) drawerLayout.findViewById(R.id.textViewAbout);
        textViewAbout.setTypeface(Fonts.mavenLight(activity));


        // menu events
        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonclicked.onitemClicked(AppConstant.MenuClick.USERINFO);
            }
        });


        relativeLayoutWallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonclicked.onitemClicked(AppConstant.MenuClick.WALLET);
            }
        });

        relativeLayoutfatafat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.FATAFAT);
                }
            }
        });

        relativeLayoutmeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.MEALS);
                }
            }
        });

        relativeLayoutRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.REFER);
                }
            }
        });

        relativeLayoutTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.HISTORY);
                }
            }
        });

        relativeLayoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonclicked.onitemClicked(AppConstant.MenuClick.NOTIFICATION_CENTER);
            }
        });

        relativeLayoutSupport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.SUPPORT);
                }
            }
        });

        relativeLayoutAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (activity instanceof FreshActivity) {
                    buttonclicked.onitemClicked(AppConstant.MenuClick.ABOUTUS);

                }
            }
        });


        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupShareMenu();

        if (activity instanceof FreshActivity) {
            textViewTransactions.setText(activity.getResources().getString(R.string.order_history));
        }

        try {
            if(Data.userData.stores.size()>1) {
                relativeLayoutfatafat.setVisibility(View.VISIBLE);
                relativeLayoutmeals.setVisibility(View.VISIBLE);
            } else {
                relativeLayoutfatafat.setVisibility(View.GONE);
                relativeLayoutmeals.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupShareMenu() {
        try {
            if (!TextUtils.isEmpty(Data.userData.shateName))
                textViewRefer.setText(Data.userData.shateName);

            try {
                Picasso.with(activity).load(Data.userData.shareImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewRefer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserData() {
        try {
            textViewUserName.setText(Data.userData.userName);


            textViewWalletValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(Data.userData.getTotalWalletBalance())));

            Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
            try {
                Picasso.with(activity).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissPaytmLoading() {
        try {
            progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);
            textViewWalletValue.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * used to perfome click when menu button clicked and notify in FrashActivity class
     */
    public interface Buttonclicked {
        void onitemClicked(int position);
    }

}
