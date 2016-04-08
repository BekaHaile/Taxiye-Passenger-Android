package product.clicklabs.jugnoo.fresh;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshCheckoutItemsFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshFragment;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.TopBar;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		new ASSL(this, drawerLayout, 1134, 720, false);


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

	private FreshCheckoutItemsFragment getFreshCheckoutItemsFragment(){
		return (FreshCheckoutItemsFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutItemsFragment.class.getName());
	}

	public void updateCartValues(){
		try {
			double totalPrice = 0;
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
	}

	public void fragmentUISetup(Fragment fragment){
		if(fragment instanceof FreshFragment){
			topBar.imageViewMenu.setVisibility(View.VISIBLE);
			topBar.relativeLayoutNotification.setVisibility(View.VISIBLE);
			topBar.imageViewBack.setVisibility(View.GONE);
			topBar.imageViewDelete.setVisibility(View.GONE);
			textViewCheckout.setVisibility(View.GONE);
			topBar.title.setText(getResources().getString(R.string.jugnoo_fresh));

		} else if(fragment instanceof FreshCheckoutItemsFragment){
			topBar.imageViewMenu.setVisibility(View.GONE);
			topBar.relativeLayoutNotification.setVisibility(View.GONE);
			topBar.imageViewBack.setVisibility(View.VISIBLE);
			topBar.imageViewDelete.setVisibility(View.VISIBLE);
			textViewCheckout.setVisibility(View.VISIBLE);
			topBar.title.setText(getResources().getString(R.string.my_cart));

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
						getFreshCheckoutItemsFragment().deleteCart();
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, true, false);
	}

	public void performBackPressed(){
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

}
