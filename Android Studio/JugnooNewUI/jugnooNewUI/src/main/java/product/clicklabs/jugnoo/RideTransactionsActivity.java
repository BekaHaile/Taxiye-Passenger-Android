package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;

import io.paperdb.Paper;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


public class  RideTransactionsActivity extends BaseAppCompatActivity implements GAAction, FeedbackFragment.ParentActivityMethods {

    private final String TAG = RideTransactionsActivity.class.getSimpleName();

	DrawerLayout drawerLayout;
	RelativeLayout relative;

	public TextView textViewTitle;
	ImageView imageViewBack;

    RelativeLayout relativeLayoutContainer;

	ImageView ivBack;
	TextView tvReset;
	RelativeLayout rlRides;
	RelativeLayout rlMeals;
	RelativeLayout rlFresh;
	RelativeLayout rlMenus;
	RelativeLayout rlDeliveryCustomer;
	RelativeLayout rlPros, rlFeed;
	public RelativeLayout rlFilter;
	TextView tvFeed;
	ImageView ivRidesRadio, ivMealsRadio, ivFreshRadio, ivMenusRadio, ivDeliveryCustomerRadio, ivProsRadio, ivFeedRadio, ivFilterApplied;

	ArrayList<Integer> productTypedFiltered = new ArrayList<>();
	boolean filtersChanged;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
    }

	@Override
	protected void onPause() {
		super.onPause();
		Paper.book().write(PaperDBKeys.HISTORY_PRODUCT_TYPES, productTypedFiltered);
	}

	private String gaCategory;
	public String getGaCategory(){
		if(gaCategory == null){
			int type = getAppType();
			if(type == AppConstant.ApplicationType.MEALS){
				gaCategory = GACategory.MEALS;
			} else if(type == AppConstant.ApplicationType.MENUS){
				gaCategory = GACategory.MENUS;
			} else if(type == AppConstant.ApplicationType.DELIVERY_CUSTOMER){
				gaCategory = GACategory.DELIVERY_CUSTOMER;
			} else if(type == AppConstant.ApplicationType.PROS){
				gaCategory = GACategory.PROS;
			} else if(type == AppConstant.ApplicationType.FEED){
				gaCategory = Data.getFeedName(this)+" ";
			} else {
				gaCategory = GACategory.FRESH;
			}
		}
		return gaCategory;
	}
	public int getAppType() {
		return Prefs.with(RideTransactionsActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);

		GAUtils.trackScreenView(HISTORY);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);

		relative = (RelativeLayout) findViewById(R.id.relative);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_HISTORY);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		imageViewBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvReset = (TextView) findViewById(R.id.tvReset);
		rlRides = (RelativeLayout) findViewById(R.id.rlRides);
		rlMeals = (RelativeLayout) findViewById(R.id.rlMeals);
		rlFresh = (RelativeLayout) findViewById(R.id.rlFresh);
		rlMenus = (RelativeLayout) findViewById(R.id.rlMenus);
		rlDeliveryCustomer = (RelativeLayout) findViewById(R.id.rlDeliveryCustomer);
		rlPros = (RelativeLayout) findViewById(R.id.rlPros);
		rlFeed = (RelativeLayout) findViewById(R.id.rlFeed);
		ivRidesRadio = (ImageView) findViewById(R.id.ivRidesRadio);
		ivMealsRadio = (ImageView) findViewById(R.id.ivMealsRadio);
		ivFreshRadio = (ImageView) findViewById(R.id.ivFreshRadio);
		ivMenusRadio = (ImageView) findViewById(R.id.ivMenusRadio);
		ivDeliveryCustomerRadio = (ImageView) findViewById(R.id.ivDeliveryCustomerRadio);
		ivProsRadio = (ImageView) findViewById(R.id.ivProsRadio);
		ivFeedRadio = (ImageView) findViewById(R.id.ivFeedRadio);
		tvFeed = (TextView) findViewById(R.id.tvFeed);
		tvFeed.setText(Data.getFeedName(this));

		rlFilter = (RelativeLayout) findViewById(R.id.rlFilter);
		ivFilterApplied = (ImageView) findViewById(R.id.ivFilterApplied);
		rlFilter.setVisibility(View.GONE);

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		tvReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				productTypedFiltered.clear();
				setSelectedFilters();
			}
		});

		rlFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(GravityCompat.END);
			}
		});

		rlRides.setOnClickListener(offeringFilterClickListener);
		rlMeals.setOnClickListener(offeringFilterClickListener);
		rlFresh.setOnClickListener(offeringFilterClickListener);
		rlMenus.setOnClickListener(offeringFilterClickListener);
		rlDeliveryCustomer.setOnClickListener(offeringFilterClickListener);
		rlPros.setOnClickListener(offeringFilterClickListener);
		rlFeed.setOnClickListener(offeringFilterClickListener);
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

		if(Data.userData != null){

			rlMeals.setVisibility(Data.userData.getMealsEnabled() == 1? View.VISIBLE : View.GONE);
			rlFresh.setVisibility(Data.userData.getFreshEnabled() == 1? View.VISIBLE : View.GONE);
			rlMenus.setVisibility(Data.userData.getMenusEnabled() == 1? View.VISIBLE : View.GONE);
			rlDeliveryCustomer.setVisibility(Data.userData.getDeliveryCustomerEnabled() == 1 ? View.VISIBLE : View.GONE);
			rlPros.setVisibility(Data.userData.getProsEnabled() == 1 ? View.VISIBLE : View.GONE);
			rlFeed.setVisibility(Data.userData.getFeedEnabled() == 1? View.VISIBLE : View.GONE);
		}

		drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				filtersChanged = false;
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				if(filtersChanged && getTopFragment() instanceof RideTransactionsFragment){
					getRideTransactionsFragment().getRecentRidesAPI(RideTransactionsActivity.this, true);
				}
			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});

		productTypedFiltered = Paper.book().read(PaperDBKeys.HISTORY_PRODUCT_TYPES, new ArrayList<Integer>());
		setSelectedFilters();
		filtersChanged = false;

		getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		rlFilter.setVisibility(View.GONE);


		if(getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0) != 0){
			new TransactionUtils(). openOrderStatusFragment(this, relativeLayoutContainer, getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0),
					getIntent().getIntExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal()),
					getIntent().getIntExtra(Constants.KEY_OPEN_LIVE_TRACKING, 0), false, true);
		} else {
			getSupportFragmentManager().beginTransaction()
					.add(relativeLayoutContainer.getId(), new RideTransactionsFragment(), RideTransactionsFragment.class.getName())
					.addToBackStack(RideTransactionsFragment.class.getName())
					.commitAllowingStateLoss();
		}

		if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey(Constants.KEY_FUGU_CHANNEL_ID)){
			// we come from anywhere payment and have to start fugu
//			if(BuildConfig.DEBUG) {
//				Bundle extras = getIntent().getExtras();
//				FuguConfig.getInstance().openChatByTransactionId(extras.getString(Constants.KEY_FUGU_CHANNEL_ID),
//						String.valueOf(Data.getFuguUserData().getUserId()),
//						extras.getString(Constants.KEY_FUGU_CHANNEL_NAME),
//						extras.getStringArrayList(Constants.KEY_FUGU_TAGS),
//						new String[]{extras.getString(Constants.KEY_MESSAGE)});
//			}
		}
	}

	public void setTitle(String title){
		textViewTitle.setText(title);
	}

	public RelativeLayout getContainer(){
		return relativeLayoutContainer;
	}


	public void performBackPressed(){
		Utils.hideSoftKeyboard(this, relativeLayoutContainer);
		OrderStatusFragment orderFrag = getOrderStatusFragment();
		if(orderFrag == null || !orderFrag.performBack()) {
			if(drawerLayout.isDrawerOpen(GravityCompat.END)){
				drawerLayout.closeDrawer(GravityCompat.END);
				return;
			}
			if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			} else {
				super.onBackPressed();
			}
		}
	}



	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {

		try {
			super.onDestroy();
			Data.isOrderCancelled = false;
			ASSL.closeActivity(drawerLayout);
			System.gc();
		} catch (Exception e) {
		}

	}


	public OrderStatusFragment getOrderStatusFragment(){
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(OrderStatusFragment.class.getName());
		if(fragment != null){
			return (OrderStatusFragment) fragment;
		}
		return null;
	}

	public RideTransactionsFragment getRideTransactionsFragment(){
		return (RideTransactionsFragment) getSupportFragmentManager().findFragmentByTag(RideTransactionsFragment.class.getName());
	}

	public DrawerLayout getDrawerLayout(){
		return drawerLayout;
	}


	private OnClickListener offeringFilterClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int productType = ProductType.AUTO.getOrdinal();
			switch (v.getId()){
				case R.id.rlRides:
					productType = ProductType.AUTO.getOrdinal();
					break;
				case R.id.rlMeals:
					productType = ProductType.MEALS.getOrdinal();
					break;
				case R.id.rlFresh:
					productType = ProductType.FRESH.getOrdinal();
					break;
				case R.id.rlMenus:
					productType = ProductType.MENUS.getOrdinal();
					break;
				case R.id.rlDeliveryCustomer:
					productType = ProductType.DELIVERY_CUSTOMER.getOrdinal();
					break;
				case R.id.rlPros:
					productType = ProductType.PROS.getOrdinal();
					break;
				case R.id.rlFeed:
					productType = ProductType.FEED.getOrdinal();
					break;
			}

			if(productTypedFiltered.contains(productType)){
				productTypedFiltered.remove(Integer.valueOf(productType));
			} else {
				productTypedFiltered.add(Integer.valueOf(productType));
			}

			setSelectedFilters();
		}
	};

	private void setSelectedFilters(){
		ivRidesRadio.setImageResource(productTypedFiltered.contains(ProductType.AUTO.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivMealsRadio.setImageResource(productTypedFiltered.contains(ProductType.MEALS.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivFreshRadio.setImageResource(productTypedFiltered.contains(ProductType.FRESH.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivMenusRadio.setImageResource(productTypedFiltered.contains(ProductType.MENUS.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivDeliveryCustomerRadio.setImageResource(productTypedFiltered.contains(ProductType.DELIVERY_CUSTOMER.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivProsRadio.setImageResource(productTypedFiltered.contains(ProductType.PROS.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		ivFeedRadio.setImageResource(productTypedFiltered.contains(ProductType.FEED.getOrdinal()) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		filtersChanged = true;
		ivFilterApplied.setVisibility(productTypedFiltered.size() > 0 ? View.VISIBLE : View.GONE);
	}

	public Fragment getTopFragment() {
		try {
			FragmentManager fragmentManager = getSupportFragmentManager();
			String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
			return fragmentManager.findFragmentByTag(fragmentTag);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Integer> getProductTypedFiltered(){
		return productTypedFiltered;
	}


	private Handler handler;

	@Override
	public Handler getHandler(){
		if(handler == null){
			handler = new Handler();
		}
		return handler;
	}

	@Override
	public View getFragmentContainer() {
		return getContainer();
	}

	@Override
	public void registerForKeyBoardEvent(KeyboardLayoutListener.KeyBoardStateHandler keyboardListener) {

	}

	@Override
	public void unRegisterKeyBoardListener() {

	}
}
