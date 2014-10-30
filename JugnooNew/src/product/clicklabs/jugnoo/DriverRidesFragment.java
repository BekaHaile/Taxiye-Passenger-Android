package product.clicklabs.jugnoo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DriverRidesFragment extends Fragment {

	ProgressBar progressBarRides;
	TextView textViewNoRides;
	ListView listViewRides;
	
	DriverRidesListAdapter driverRidesListAdapter;
	
	RelativeLayout main;
	
	AsyncHttpClient fetchRidesClient;

	public DriverRidesFragment() {
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Data.rides.clear();
		View rootView = inflater.inflate(R.layout.fragment_rides, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBarRides = (ProgressBar) rootView.findViewById(R.id.progressBarRides);
		textViewNoRides = (TextView) rootView.findViewById(R.id.textViewNoRides); textViewNoRides.setTypeface(Data.regularFont(getActivity()));
		listViewRides = (ListView) rootView.findViewById(R.id.listViewRides);
		
		driverRidesListAdapter = new DriverRidesListAdapter();
		listViewRides.setAdapter(driverRidesListAdapter);
		
		progressBarRides.setVisibility(View.GONE);
		textViewNoRides.setVisibility(View.GONE);
		
		getRidesAsync(getActivity());
		
		return rootView;
	}

	
	public void updateListData(){
		if(Data.rides.size() == 0){
			textViewNoRides.setVisibility(View.VISIBLE);
		}
		else{
			textViewNoRides.setVisibility(View.GONE);
		}
		driverRidesListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		if(fetchRidesClient != null){
			fetchRidesClient.cancelAllRequests(true);
		}
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	
	class ViewHolderDriverRides {
		TextView fromText, fromValue, toText, toValue, distanceValue, timeValue, fareValue;
		LinearLayout relative;
		int id;
	}

	class DriverRidesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRides holder;
		DateOperations dateOperations;
		public DriverRidesListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateOperations = new DateOperations();
		}

		@Override
		public int getCount() {
			return Data.rides.size();
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
				holder = new ViewHolderDriverRides();
				convertView = mInflater.inflate(R.layout.booking_list_item, null);
				
				holder.fromText = (TextView) convertView.findViewById(R.id.fromText); holder.fromText.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.fromValue = (TextView) convertView.findViewById(R.id.fromValue); holder.fromValue.setTypeface(Data.regularFont(getActivity()));
				holder.toText = (TextView) convertView.findViewById(R.id.toText); holder.toText.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.toValue = (TextView) convertView.findViewById(R.id.toValue); holder.toValue.setTypeface(Data.regularFont(getActivity()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.regularFont(getActivity()));
				holder.timeValue = (TextView) convertView.findViewById(R.id.timeValue); holder.timeValue.setTypeface(Data.regularFont(getActivity()));
				holder.fareValue = (TextView) convertView.findViewById(R.id.fareValue); holder.fareValue.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}
			
			
			RideInfo booking = Data.rides.get(position);
			
			if(dateOperations == null){
				dateOperations = new DateOperations();
			}
			
			holder.id = position;
			
			holder.fromValue.setText(booking.fromLocation);
			holder.toValue.setText(booking.toLocation);
			holder.distanceValue.setText(booking.distance + " km");
			holder.timeValue.setText(dateOperations.convertDate(dateOperations.utcToLocal(booking.time)));
			holder.fareValue.setText("Rs. "+booking.fare);
			
			
			return convertView;
		}

	}
	


	/**
	 * ASync for get rides from server
	 */
	public void getRidesAsync(final Activity activity) {
		if(fetchRidesClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarRides.setVisibility(View.VISIBLE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("current_mode", "1");
			
				//booking_history
				
				fetchRidesClient = Data.getClient();
				fetchRidesClient.post(Data.SERVER_URL + "/booking_history", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarRides.setVisibility(View.GONE);
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
	
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.d("Server response", "response = " + response);
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
										Data.rides.clear();
										if(bookingData.length() > 0){
											for(int i=0; i<bookingData.length(); i++){
												JSONObject booData = bookingData.getJSONObject(i);
												Data.rides.add(new RideInfo(booData.getString("id"), booData.getString("from"),
														booData.getString("to"), booData.getString("fare"), booData.getString("distance"),
														booData.getString("time")));
											}
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
								updateListData();
								progressBarRides.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchRidesClient = null;
								super.onFinish();
							}
							
							
							
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}

	}
	
	

}
