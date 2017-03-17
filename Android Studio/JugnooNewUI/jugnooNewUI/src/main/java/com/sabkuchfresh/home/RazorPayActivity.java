package com.sabkuchfresh.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 17/03/17.
 */

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

	Snackbar snackbar;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_razorpay);

		Button buttonRetry = (Button) findViewById(R.id.buttonRetry);
		buttonRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(snackbar != null){
					snackbar.dismiss();
				}
				startPayment();
			}
		});

		startPayment();
	}


	public void startPayment() {
		/*
		  Instantiate Checkout
		 */
		Checkout checkout = new Checkout();

		/*
		  Set your logo here
		 */
		checkout.setImage(R.drawable.jugnoo_icon);

		/*
		  Reference to current activity
		 */
		final Activity activity = this;

		/*
		  Pass your payment options to the Razorpay Checkout as a JSONObject
		 */
		try {
			JSONObject options = new JSONObject();

			/*
			  Merchant Name
			  eg: Rentomojo || HasGeek etc.
			 */
			options.put("name", "Merchant Name");

			/*
			  Description can be anything
			  eg: Order #123123
			      Invoice Payment
			      etc.
			 */
			options.put("description", "Order #123456");

			options.put("currency", "INR");

			/*
			  Amount is always passed in PAISE
			  Eg: "500" = Rs 5.00
			 */
			options.put("amount", "500");
			options.put("order_id", "order_7VBZk4v2oU2YTq");
			checkout.setFullScreenDisable(true);

			checkout.open(activity, options);
		} catch(Exception e) {
			Log.e("TAG", "Error in starting Razorpay Checkout");
		}
	}


	@Override
	public void onPaymentSuccess(String s) {
		Log.i("RazorPayActivity onPaymentSuccess","s="+s);
		if(snackbar != null){
			snackbar.dismiss();
		}
		snackbar = Snackbar.make(RazorPayActivity.this.findViewById(android.R.id.content),
				"RazorPayActivity onPaymentSuccess s="+s,Snackbar.LENGTH_INDEFINITE);
		snackbar.show();

	}

	@Override
	public void onPaymentError(int i, String s) {
		Log.e("RazorPayActivity onPaymentError", "i="+i+", s="+s);
		if(snackbar != null){
			snackbar.dismiss();
		}
		snackbar = Snackbar.make(RazorPayActivity.this.findViewById(android.R.id.content),
				"RazorPayActivity onPaymentError i="+i+", s="+s,Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
	}
}
