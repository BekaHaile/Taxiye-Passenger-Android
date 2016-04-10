package product.clicklabs.jugnoo.fresh;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshAddressFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCartItemsFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCheckoutFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshFragment;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.fresh.models.UserCheckoutResponse;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.TopBar;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/6/16.
 */
public class FreshActivity extends FragmentActivity {

	private DrawerLayout drawerLayout;

	private RelativeLayout relativeLayoutContainer;

	private RelativeLayout relativeLayoutCheckoutBar, relativeLayoutCart;
	private TextView textViewCartItemsCount, textViewTotalPrice, textViewCheckout;

	private MenuBar menuBar;
	private TopBar topBar;
	private TransactionUtils transactionUtils;

	private ProductsResponse productsResponse;
	private UserCheckoutResponse userCheckoutResponse;

	private String selectedAddress = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);

		Log.e("", "");

		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		relativeLayoutCheckoutBar = (RelativeLayout) findViewById(R.id.relativeLayoutCheckoutBar);
		relativeLayoutCart = (RelativeLayout) findViewById(R.id.relativeLayoutCart);

		textViewCartItemsCount = (TextView) findViewById(R.id.textViewCartItemsCount);
		textViewCartItemsCount.setTypeface(Fonts.mavenRegular(this));
		textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
		textViewTotalPrice.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCartItemsCount.setMinWidth((int) (45f * ASSL.Xscale()));
		textViewCheckout = (TextView) findViewById(R.id.textViewCheckout);
		textViewCheckout.setTypeface(Fonts.mavenRegular(this));

		menuBar = new MenuBar(this, drawerLayout);
		topBar = new TopBar(this, drawerLayout);


		relativeLayoutCheckoutBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getTransactionUtils().openCheckoutFragment(FreshActivity.this, relativeLayoutContainer);
			}
		});

		relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getTransactionUtils().openCartFragment(FreshActivity.this, relativeLayoutContainer);
			}
		});


		getSupportFragmentManager().beginTransaction()
				.add(relativeLayoutContainer.getId(), new FreshFragment(),
						FreshFragment.class.getName())
				.addToBackStack(FreshFragment.class.getName())
				.commitAllowingStateLoss();

	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!HomeActivity.checkIfUserDataNull(this)){
			menuBar.setUserData();
			menuBar.dismissPaytmLoading();
			topBar.setUserData();
		}

	}

	private FreshFragment getFreshFragment(){
		return (FreshFragment) getSupportFragmentManager().findFragmentByTag(FreshFragment.class.getName());
	}

	private FreshCartItemsFragment getFreshCartItemsFragment(){
		return (FreshCartItemsFragment) getSupportFragmentManager().findFragmentByTag(FreshCartItemsFragment.class.getName());
	}

	private FreshCheckoutFragment getFreshCheckoutFragment(){
		return (FreshCheckoutFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutFragment.class.getName());
	}

	private FreshAddressFragment getFreshAddressFragment(){
		return (FreshAddressFragment) getSupportFragmentManager().findFragmentByTag(FreshAddressFragment.class.getName());
	}

	public double updateCartValuesGetTotalPrice(){
		double totalPrice = 0;
		try {
			int totalQuantity = 0;
			for(Category category : getProductsResponse().getCategories()){
				for(SubItem subItem : category.getSubItems()){
					if (subItem.getSubItemQuantitySelected() > 0) {
						totalQuantity++;
						totalPrice = totalPrice + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
					}
				}
			}
			textViewTotalPrice.setText(String.format(getResources().getString(R.string.rupees_value_format),
					Utils.getMoneyDecimalFormat().format(totalPrice)));
			textViewCartItemsCount.setText(String.valueOf(totalQuantity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalPrice;
	}

	public void fragmentUISetup(Fragment fragment){
		if(fragment instanceof FreshFragment){
			topBar.imageViewMenu.setVisibility(View.VISIBLE);
			topBar.relativeLayoutNotification.setVisibility(View.VISIBLE);
			topBar.imageViewBack.setVisibility(View.GONE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			textViewCheckout.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
			topBar.title.setText(getResources().getString(R.string.jugnoo_fresh));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);

		} else if(fragment instanceof FreshCartItemsFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.VISIBLE);
			topBar.textViewAdd.setVisibility(View.GONE);
			textViewCheckout.setVisibility(View.VISIBLE);
			relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
			topBar.title.setText(getResources().getString(R.string.my_cart));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshCheckoutFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.my_cart));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshAddressFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.VISIBLE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.address));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		}
	}

	public void deleteCart(){
		DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
				getResources().getString(R.string.delete_fresh_cart_message),
				getResources().getString(R.string.delete),
				getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						FreshCartItemsFragment frag = getFreshCartItemsFragment();
						if(frag != null) {
							frag.deleteCart();
						}
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, true, false);
	}

	public void addAddress(){
		FreshAddressFragment frag = getFreshAddressFragment();
		if(frag != null){
			frag.addAddressPress();
		}
	}

	public void performBackPressed(){
		Utils.hideSoftKeyboard(this, textViewCartItemsCount);
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
			overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public ProductsResponse getProductsResponse() {
		return productsResponse;
	}

	public void setProductsResponse(ProductsResponse productsResponse) {
		this.productsResponse = productsResponse;
	}

	public LatLng getCurrentPlaceLatLng(){
		return new LatLng(Data.latitude, Data.longitude);
	}

	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}

	public UserCheckoutResponse getUserCheckoutResponse() {
		return userCheckoutResponse;
	}

	public void setUserCheckoutResponse(UserCheckoutResponse userCheckoutResponse) {
		this.userCheckoutResponse = userCheckoutResponse;
	}

	public RelativeLayout getRelativeLayoutContainer() {
		return relativeLayoutContainer;
	}

	public String getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(String selectedAddress) {
		this.selectedAddress = selectedAddress;
	}
}
