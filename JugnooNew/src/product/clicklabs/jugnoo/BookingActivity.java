package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class BookingActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title, textViewNoBookings;
	ProgressBar progressBarBookings;
	ListView bookingsList;
	
	BookingListAdapter bookingListAdapter;
	
	AsyncHttpClient fetchBookingsClient;
	
	ArrayList<RideInfo> rides = new ArrayList<RideInfo>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_activity);
		
		rides.clear();
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(BookingActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		textViewNoBookings = (TextView) findViewById(R.id.textViewNoBookings); textViewNoBookings.setTypeface(Data.regularFont(getApplicationContext()));
		
		progressBarBookings = (ProgressBar) findViewById(R.id.progressBarBookings);
		
		bookingsList = (ListView) findViewById(R.id.bookingsList);
		bookingListAdapter = new BookingListAdapter();
		
		bookingsList.setAdapter(bookingListAdapter);
		
		progressBarBookings.setVisibility(View.GONE);
		textViewNoBookings.setVisibility(View.GONE);
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		getRidesAsync(BookingActivity.this);
		
	}
	
	void updateBookingList(){
		if(rides.size() == 0){
			textViewNoBookings.setVisibility(View.VISIBLE);
		}
		else{
			textViewNoBookings.setVisibility(View.GONE);
		}
		bookingListAdapter.notifyDataSetChanged();
	}
	
	
	class ViewHolderBooking {
		TextView fromText, fromValue, toText, toValue, distanceValue, timeValue, fareValue, balanceValue;
		ImageView couponImg;
		LinearLayout relative;
		int id;
	}

	class BookingListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderBooking holder;

		public BookingListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return rides.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderBooking();
				convertView = mInflater.inflate(R.layout.booking_list_item, null);
				
				holder.fromText = (TextView) convertView.findViewById(R.id.fromText); holder.fromText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
				holder.fromValue = (TextView) convertView.findViewById(R.id.fromValue); holder.fromValue.setTypeface(Data.regularFont(getApplicationContext()));
				holder.toText = (TextView) convertView.findViewById(R.id.toText); holder.toText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
				holder.toValue = (TextView) convertView.findViewById(R.id.toValue); holder.toValue.setTypeface(Data.regularFont(getApplicationContext()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.regularFont(getApplicationContext()));
				holder.timeValue = (TextView) convertView.findViewById(R.id.timeValue); holder.timeValue.setTypeface(Data.regularFont(getApplicationContext()));
				holder.fareValue = (TextView) convertView.findViewById(R.id.fareValue); holder.fareValue.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
				holder.balanceValue = (TextView) convertView.findViewById(R.id.balanceValue); holder.balanceValue.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
				holder.couponImg = (ImageView) convertView.findViewById(R.id.couponImg);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderBooking) convertView.getTag();
			}
			
			
			RideInfo booking = rides.get(position);
			
			DateOperations dateOperations = new DateOperations();
			
			holder.id = position;
			
			holder.fromValue.setText(booking.fromLocation);
			holder.toValue.setText(booking.toLocation);
			holder.distanceValue.setText(booking.distance + " km");
			holder.timeValue.setText(dateOperations.convertDate(dateOperations.utcToLocal(booking.time)));
			holder.fareValue.setText("Rs. "+booking.fare);
			holder.balanceValue.setVisibility(View.GONE);
			
			if(1 == booking.couponUsed){
				holder.couponImg.setVisibility(View.VISIBLE);
			}
			else{
				holder.couponImg.setVisibility(View.GONE);
			}
			
			dateOperations = null;
			
			
			return convertView;
		}

	}
	
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		if(fetchBookingsClient != null){
			fetchBookingsClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	/**
	 * ASync for get rides from server
	 */
	public void getRidesAsync(final Activity activity) {
		if(fetchBookingsClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarBookings.setVisibility(View.VISIBLE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("current_mode", "0");
			
				//booking_history
				
				fetchBookingsClient = Data.getClient();
				fetchBookingsClient.post(Data.SERVER_URL + "/booking_history", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarBookings.setVisibility(View.GONE);
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										int flag = jObj.getInt("flag");	
										String error = jObj.getString("error");
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
											new DialogPopup().alertPopup(activity, "", error);
										}
										else{
											new DialogPopup().alertPopup(activity, "", error);
										}
									}
									else{
										JSONArray bookingData = jObj.getJSONArray("booking_data");
										rides.clear();
										if(bookingData.length() > 0){
											for(int i=0; i<bookingData.length(); i++){
												JSONObject booData = bookingData.getJSONObject(i);
												rides.add(new RideInfo(booData.getString("id"), booData.getString("from"),
														booData.getString("to"), booData.getString("fare"), booData.getString("distance"),
														booData.getString("time"), "", booData.getInt("coupon_used")));
											}
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
								updateBookingList();
								progressBarBookings.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
								fetchBookingsClient = null;
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
	}
	
}
