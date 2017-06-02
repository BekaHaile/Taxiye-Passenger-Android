package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.os.Bundle;

import product.clicklabs.jugnoo.R;

/**
 * Created by socomo20 on 12/11/15.
 */
public class CallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			if(getIntent().hasExtra(getResources().getString(R.string.call_number))){
				String callNumber = getIntent().getStringExtra(getResources().getString(R.string.call_number));
				if(callNumber != null) {
					Utils.openCallIntent(this, callNumber);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.finish();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
