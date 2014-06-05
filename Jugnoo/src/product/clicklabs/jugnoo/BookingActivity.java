package product.clicklabs.jugnoo;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BookingActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title, noBookingsText;
	ListView bookingsList;
	
	BookingListAdapter bookingListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(BookingActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		noBookingsText = (TextView) findViewById(R.id.noBookingsText); 
		noBookingsText.setTypeface(Data.regularFont(getApplicationContext()));
		
		bookingsList = (ListView) findViewById(R.id.bookingsList);
		bookingListAdapter = new BookingListAdapter();
		
		bookingsList.setAdapter(bookingListAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		
		
//		Data.bookings.add(new Booking("1", "Sector 28 dfsfgdsgfgsdfsgsfgdsfgsdgsgsgfdg", "Sector 11sdfgsdfgsfdgsdgfsg", "50", "5.4", "2014-06-05 09:11:11"));
//		Data.bookings.add(new Booking("1", "Sector 28", "Sector 11sdfgsdfgsgdfsfgds", "50", "5.4", "2014-06-05 09:11:11"));
//		Data.bookings.add(new Booking("1", "Sector 28", "Sector 11", "50", "5.4", "2014-06-05 09:11:11"));
//		Data.bookings.add(new Booking("1", "Sector 28", "Sector 11", "50", "5.4", "2014-06-05 09:11:11"));
		
		
		
		
		updateBookingList();
		
	}
	
	void updateBookingList(){
		
		bookingListAdapter.notifyDataSetChanged();
		
		if(Data.bookings.size() == 0){
			noBookingsText.setVisibility(View.VISIBLE);
		}
		else{
			noBookingsText.setVisibility(View.GONE);
		}
		
	}
	
	
	class ViewHolderBooking {
		TextView fromText, fromValue, toText, toValue, distanceValue, timeValue, fareValue;
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
			return Data.bookings.size();
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
				
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderBooking) convertView.getTag();
			}
			
			
			Booking booking = Data.bookings.get(position);
			
			DateOperations dateOperations = new DateOperations();
			
			holder.id = position;
			
			holder.fromValue.setText(booking.fromLocation);
			holder.toValue.setText(booking.toLocation);
			holder.distanceValue.setText(booking.distance + " Km");
			holder.timeValue.setText(dateOperations.convertDate(dateOperations.utcToLocal(booking.time)));
			holder.fareValue.setText("Rs. "+booking.fare);
			
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
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
