package product.clicklabs.jugnoo;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.OfferingListAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.utils.ASSL;


public class HomeSwitcherActivity extends BaseAppCompatActivity {

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_switcher);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);
		ImageView ivMenu = (ImageView) findViewById(R.id.ivMenu);
		TextView tvHeading = (TextView) findViewById(R.id.tvHeading);
		RecyclerView rvOfferings = (RecyclerView) findViewById(R.id.rvOfferings);
		rvOfferings.setLayoutManager(new LinearLayoutManager(this));

		tvHeading.setText(getString(R.string.hello_user_what_you_like_to_do_format, Data.userData.userName));

		MenuBar menuBar = new MenuBar(this, drawerLayout);
        //menuBar.setUserData();

		ArrayList<OfferingListAdapter.Offering> offerings = new ArrayList<>();
        try {
			offerings.add(new OfferingListAdapter.Offering(Config.getAutosClientId(), "Rides", "Affordable auto rickshaws online", R.drawable.ic_auto_grey));
            if ((Data.userData.getFreshEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getFreshClientId(), "Fresh", "Order fruits, vegetables & groceries online", R.drawable.ic_fresh_grey));
            }
            if ((Data.userData.getMealsEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getMealsClientId(), "Meals", "Home styled breakfast, lunch, snacks & dinner", R.drawable.ic_meals_grey));
            }
            if ((Data.userData.getMenusEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getMenusClientId(), "Menus", "Online food delivering from restaurants", R.drawable.ic_menus_grey));
            }
            if ((Data.userData.getFeedEnabled() == 1)) {
				offerings.add(new OfferingListAdapter.Offering(Config.getFeedClientId(), "Fatafat", "Get anything delivered from anywhere", R.drawable.ic_fatafat_menu));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OfferingListAdapter offeringListAdapter = new OfferingListAdapter(this, offerings, new OfferingListAdapter.Callback() {
            @Override
            public Location getLocation() {
                return location;
            }
        }, rvOfferings);
        rvOfferings.setAdapter(offeringListAdapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
		MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 10000);
	}

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().getLocationFetcher().destroy();
    }


    private Location location;

    private LocationUpdate locationUpdate = new LocationUpdate() {
        @Override
        public void onLocationChanged(Location location) {
            HomeSwitcherActivity.this.location = location;
        }
    };


}
