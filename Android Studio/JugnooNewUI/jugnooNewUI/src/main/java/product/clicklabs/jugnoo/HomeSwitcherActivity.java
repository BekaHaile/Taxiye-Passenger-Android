package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.OfferingListAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.utils.ASSL;


public class HomeSwitcherActivity extends BaseAppCompatActivity implements GACategory, GAAction{

    DrawerLayout drawerLayout;
	LatLng latLng;
	MenuBar menuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_switcher);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);
		ImageView ivMenu = (ImageView) findViewById(R.id.ivMenu);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle); tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
		TextView tvHeading = (TextView) findViewById(R.id.tvHeading);
		RecyclerView rvOfferings = (RecyclerView) findViewById(R.id.rvOfferings);
		rvOfferings.setNestedScrollingEnabled(false);
		rvOfferings.setLayoutManager(new LinearLayoutManager(this));

		tvHeading.setText("Hello, "+Data.userData.userName+"!\n");
		SpannableStringBuilder ssb = new SpannableStringBuilder("What would you like to\nget done today?");
		ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new RelativeSizeSpan(1.2f), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvHeading.append(ssb);

		menuBar = new MenuBar(this, drawerLayout);
		latLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.loginLatitude), getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.loginLongitude));
		ArrayList<OfferingListAdapter.Offering> offerings = new ArrayList<>();
        try {
			offerings.add(new OfferingListAdapter.Offering(Config.getAutosClientId(), getString(R.string.rides), "Affordable auto rickshaws online", R.drawable.ic_rides_switcher,R.drawable.ic_graph_autos,R.drawable.bg_home_switcher_autos));
			if ((Data.userData.getMealsEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getMealsClientId(), getString(R.string.meals), "Home styled breakfast, lunch, snacks & dinner", R.drawable.ic_meals_switcher,R.drawable.ic_graph_meals,R.drawable.bg_home_switcher_meals));
			}
			if ((Data.userData.getFreshEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getFreshClientId(), getString(R.string.fatafat), "Order fruits, vegetables & groceries online", R.drawable.ic_fresh_switcher,R.drawable.ic_grocery_graph,R.drawable.bg_home_switcher_grocery));
            }
            if ((Data.userData.getMenusEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getMenusClientId(), getString(R.string.menus), "Online food delivering from restaurants", R.drawable.ic_menus_switcher,R.drawable.ic_graph_menus,R.drawable.bg_home_switcher_menus));
            }
            if ((Data.userData.getFeedEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getFeedClientId(), Data.getFeedName(this), "Get anything delivered from anywhere", R.drawable.ic_anywhere_switcher,R.drawable.ic_graph_anywhere,R.drawable.bg_home_switcher_feed));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OfferingListAdapter offeringListAdapter = new OfferingListAdapter(this, offerings, new OfferingListAdapter.Callback() {
            @Override
            public LatLng getLatLng() {
				return getCurrentPlaceLatLng();
            }
        }, rvOfferings);
        rvOfferings.setAdapter(offeringListAdapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

		drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				GAUtils.event(JUGNOO, HOME + PAGE , SIDE_MENU+CLICKED);
				GAUtils.trackScreenView(HOME + PAGE + SIDE_MENU);
			}

			@Override
			public void onDrawerClosed(View drawerView) {

			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});

		GAUtils.trackScreenView(JUGNOO + HOME + PAGE);
		fetchWalletBalance();
    }

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
		MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 10000);
		menuBar.setUserData();
	}

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().getLocationFetcher().destroy();
    }


    private LocationUpdate locationUpdate = new LocationUpdate() {
        @Override
        public void onLocationChanged(Location location) {
			latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    };


	private ApiFetchWalletBalance apiFetchWalletBalance = null;
	private void fetchWalletBalance() {
		try {
			if(apiFetchWalletBalance == null){
				apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
					@Override
					public void onSuccess() {
						MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
						menuBar.setUserData();
					}

					@Override
					public void onFailure() {
						try {
							menuBar.setUserData();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFinish() {
					}

					@Override
					public void onRetry(View view) {
					}

					@Override
					public void onNoRetry(View view) {
					}
				});
			}
			apiFetchWalletBalance.getBalance(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LatLng getCurrentPlaceLatLng(){
		return latLng == null ? Data.getIndiaCentre() : latLng;
	}

}
