package product.clicklabs.jugnoo.fresh;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fresh.fragments.FreshAddressFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCartItemsFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCheckoutFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshOrderHistoryFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshOrderSummaryFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshPaymentFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshSupportFragment;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.OrderHistory;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.fresh.models.Slot;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.fresh.models.UserCheckoutResponse;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.TopBar;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/6/16.
 */
public class FreshActivity extends FragmentActivity {

	private final String TAG = FreshActivity.class.getSimpleName();
	private DrawerLayout drawerLayout;

	private RelativeLayout relativeLayoutContainer;

	private ImageView imageViewSearch;
	private RelativeLayout relativeLayoutCheckoutBar, relativeLayoutCart;
	private LinearLayout linearLayoutCheckout;
	private TextView textViewCartItemsCount, textViewTotalPrice, textViewCheckout, textViewMinOrder;

	private MenuBar menuBar;
	private TopBar topBar;
	private TransactionUtils transactionUtils;

	private ProductsResponse productsResponse;
	private UserCheckoutResponse userCheckoutResponse;

	private String selectedAddress = "";
	private Slot slotSelected, slotToSelect;
	private PaymentOption paymentOption;

	private OrderHistory orderHistoryOpened;
	private int orderHistoryOpenedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);

		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
		relativeLayoutCheckoutBar = (RelativeLayout) findViewById(R.id.relativeLayoutCheckoutBar);
		relativeLayoutCart = (RelativeLayout) findViewById(R.id.relativeLayoutCart);
		linearLayoutCheckout = (LinearLayout) findViewById(R.id.linearLayoutCheckout);

		textViewCartItemsCount = (TextView) findViewById(R.id.textViewCartItemsCount);
		textViewCartItemsCount.setTypeface(Fonts.mavenRegular(this));
		textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
		textViewTotalPrice.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCartItemsCount.setMinWidth((int) (45f * ASSL.Xscale()));
		textViewCheckout = (TextView) findViewById(R.id.textViewCheckout);
		textViewCheckout.setTypeface(Fonts.mavenRegular(this));
		textViewMinOrder = (TextView)findViewById(R.id.textViewMinOrder);
		textViewMinOrder.setTypeface(Fonts.mavenRegular(this));

		menuBar = new MenuBar(this, drawerLayout);
		topBar = new TopBar(this, drawerLayout);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(updateCartValuesGetTotalPrice().second > 0) {
					if (getTransactionUtils().checkIfFragmentAdded(FreshActivity.this, FreshCartItemsFragment.class.getName())) {
						getTransactionUtils().openCheckoutFragment(FreshActivity.this, relativeLayoutContainer);
					} else {
						getTransactionUtils().openCartFragment(FreshActivity.this, relativeLayoutContainer);
					}
				} else {
					Toast.makeText(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty), Toast.LENGTH_SHORT).show();
				}
			}
		};

		relativeLayoutCheckoutBar.setOnClickListener(onClickListener);
		linearLayoutCheckout.setOnClickListener(onClickListener);

		relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(updateCartValuesGetTotalPrice().second > 0) {
					getTransactionUtils().openCartFragment(FreshActivity.this, relativeLayoutContainer);
				} else {
					Toast.makeText(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty), Toast.LENGTH_SHORT).show();
				}
			}
		});

		imageViewSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer);
			}
		});


		addFreshFragment();

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


	public FreshOrderHistoryFragment getFreshOrderHistoryFragment(){
		return (FreshOrderHistoryFragment) getSupportFragmentManager().findFragmentByTag(FreshOrderHistoryFragment.class.getName());
	}

	private FreshOrderSummaryFragment getFreshOrderSummaryFragment(){
		return (FreshOrderSummaryFragment) getSupportFragmentManager().findFragmentByTag(FreshOrderSummaryFragment.class.getName());
	}


	public Pair<Double, Integer> updateCartValuesGetTotalPrice(){
		Pair<Double, Integer> pair;
		double totalPrice = 0; // Done by Ankit
		int totalQuantity = 0;
		try {
			if(getProductsResponse() != null
					&& getProductsResponse().getCategories() != null) {
				for (Category category : getProductsResponse().getCategories()) {
					for (SubItem subItem : category.getSubItems()) {
						if (subItem.getSubItemQuantitySelected() > 0) {
							totalQuantity++;
							totalPrice = totalPrice + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
						}
					}
				}
				textViewTotalPrice.setText(String.format(getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(totalPrice)));
				if (totalQuantity > 0) {
					textViewCartItemsCount.setVisibility(View.VISIBLE);
					textViewCartItemsCount.setText(String.valueOf(totalQuantity));
				} else {
					textViewCartItemsCount.setVisibility(View.GONE);
				}
				if (getFreshCartItemsFragment() != null){
					if (this.getFreshCartItemsFragment().isVisible() && totalPrice < getProductsResponse().getDeliveryInfo().getMinAmount()) {
						textViewMinOrder.setVisibility(View.VISIBLE);
					} else {
						textViewMinOrder.setVisibility(View.GONE);
					}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pair = new Pair<>(totalPrice, totalQuantity);
		return pair;
	}

	public void fragmentUISetup(Fragment fragment){
		textViewMinOrder.setVisibility(View.GONE);
		imageViewSearch.setVisibility(View.GONE);
		if(fragment instanceof FreshFragment){
			topBar.imageViewMenu.setVisibility(View.VISIBLE);
			topBar.relativeLayoutNotification.setVisibility(View.VISIBLE);
			topBar.imageViewBack.setVisibility(View.GONE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			textViewCheckout.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
			//imageViewSearch.setVisibility(View.VISIBLE);

			topBar.title.setVisibility(View.GONE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.VISIBLE);
			topBar.title.setText(getResources().getString(R.string.jugnoo_fresh));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);

		} else if(fragment instanceof FreshCartItemsFragment){
			textViewMinOrder.setText(String.format(getResources().getString(R.string.fresh_min_order_value), getProductsResponse().getDeliveryInfo().getMinAmount().intValue()));
			try {
				String[] splited = textViewTotalPrice.getText().toString().split("\\s+");
				String split_one=splited[1];
				if(Double.parseDouble(split_one) < getProductsResponse().getDeliveryInfo().getMinAmount()){
					textViewMinOrder.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.VISIBLE);
			topBar.textViewAdd.setVisibility(View.GONE);
			textViewCheckout.setVisibility(View.VISIBLE);
			relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.my_cart));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshCheckoutFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.checkout));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshAddressFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.VISIBLE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.address));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshPaymentFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.payment));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshOrderHistoryFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.order_history));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshOrderSummaryFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.order_summary));
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

		} else if(fragment instanceof FreshSupportFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			topBar.textViewAdd.setVisibility(View.GONE);
			relativeLayoutCheckoutBar.setVisibility(View.GONE);

			topBar.title.setVisibility(View.VISIBLE);
			topBar.linearLayoutFreshSwapper.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.support));
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
						clearCart();
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


	public void orderComplete(){
		clearCart();
		for(Category category : productsResponse.getCategories()){
			for(SubItem subItem : category.getSubItems()){
				subItem.setSubItemQuantitySelected(0);
			}
		}
		selectedAddress = "";
		slotSelected = null;
		slotToSelect = null;
		paymentOption = null;

		FragmentManager fm = getSupportFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount()-1; i++) {
			fm.popBackStack();
		}

		updateCartValuesGetTotalPrice();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FreshFragment frag = getFreshFragment();
				if(frag != null){
					frag.getAllProducts();
				}
			}
		}, 1000);
	}

	private void addFreshFragment(){
		getSupportFragmentManager().beginTransaction()
				.add(relativeLayoutContainer.getId(), new FreshFragment(),
						FreshFragment.class.getName())
				.addToBackStack(FreshFragment.class.getName())
				.commitAllowingStateLoss();
	}

	public void openOrderHistory(){
		getTransactionUtils().openOrderHistoryFragment(FreshActivity.this, relativeLayoutContainer);
	}

	public void openSupport(){
		getTransactionUtils().openSupportFragment(FreshActivity.this, relativeLayoutContainer);
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

	public void setTopBarAddVisibility(int visibility){
		topBar.textViewAdd.setVisibility(visibility);
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

	public Slot getSlotSelected() {
		return slotSelected;
	}

	public void setSlotSelected(Slot slotSelected) {
		this.slotSelected = slotSelected;
	}

	public Slot getSlotToSelect() {
		return slotToSelect;
	}

	public void setSlotToSelect(Slot slotToSelect) {
		this.slotToSelect = slotToSelect;
	}

	public PaymentOption getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(PaymentOption paymentOption) {
		this.paymentOption = paymentOption;
	}

	public OrderHistory getOrderHistoryOpened() {
		return orderHistoryOpened;
	}

	public void setOrderHistoryOpened(int position, OrderHistory orderHistoryOpened) {
		this.orderHistoryOpenedPosition = position;
		this.orderHistoryOpened = orderHistoryOpened;
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveCartToSP();
		Log.e(TAG, "cart saved="+Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));

	}

	private void saveCartToSP(){
		try{
			JSONObject jCart = new JSONObject();
			if(getProductsResponse() != null
					&& getProductsResponse().getCategories() != null) {
				for (Category category : getProductsResponse().getCategories()) {
					for (SubItem subItem : category.getSubItems()) {
						if (subItem.getSubItemQuantitySelected() > 0) {
							try {
								jCart.put(String.valueOf(subItem.getSubItemId()), (int)subItem.getSubItemQuantitySelected());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			Prefs.with(this).save(Constants.SP_FRESH_CART, jCart.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void updateCartFromSP(){
		try{
			JSONObject jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
			if(getProductsResponse() != null
					&& getProductsResponse().getCategories() != null) {
				for (Category category : getProductsResponse().getCategories()) {
					for (SubItem subItem : category.getSubItems()) {
						try {
							subItem.setSubItemQuantitySelected(jCart.optInt(String.valueOf(subItem.getSubItemId()),
									(int)subItem.getSubItemQuantitySelected()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void clearCart(){
		Prefs.with(this).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);
	}

	public int getOrderHistoryOpenedPosition() {
		return orderHistoryOpenedPosition;
	}

	public void setOrderHistoryOpenedPosition(int orderHistoryOpenedPosition) {
		this.orderHistoryOpenedPosition = orderHistoryOpenedPosition;
	}
}
