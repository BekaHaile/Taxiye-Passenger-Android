package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.fragments.SupportMainFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportActivity extends BaseFragmentActivity implements FlurryEventNames {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	LinearLayout linearLayoutContainer;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		

		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		getSupportFragmentManager().beginTransaction()
				.add(linearLayoutContainer.getId(), new SupportMainFragment(), SupportMainFragment.class.getName())
				.addToBackStack(SupportMainFragment.class.getName())
				.commitAllowingStateLoss();

	}

	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
