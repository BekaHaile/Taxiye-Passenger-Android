package product.clicklabs.jugnoo.fresh;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshFragment;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by shankar on 4/6/16.
 */
public class FreshActivity extends FragmentActivity {

	private RelativeLayout relativeLayoutContainer;

	private ProductsResponse productsResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh);

		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
		new ASSL(this, relativeLayoutContainer, 1134, 720, false);

		getSupportFragmentManager().beginTransaction()
				.add(relativeLayoutContainer.getId(), new FreshFragment(),
						FreshFragment.class.getName())
				.addToBackStack(FreshFragment.class.getName())
				.commitAllowingStateLoss();

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
}
