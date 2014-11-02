package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DriverMissedRidesFragment extends Fragment {

	ProgressBar progressBarMissedRides;
	TextView textViewInfoDisplay;
	ListView listViewMissedRides;
	
	DriverMissedRidesListAdapter driverMissedRidesListAdapter;
	
	RelativeLayout main;

	ArrayList<MissedRideInfo> missedRideInfos = new ArrayList<MissedRideInfo>();
	
	AsyncHttpClient fetchMissedRidesClient;
	
	public DriverMissedRidesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		missedRideInfos.clear();
		View rootView = inflater.inflate(R.layout.fragment_missed_rides, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBarMissedRides = (ProgressBar) rootView.findViewById(R.id.progressBarMissedRides);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Data.regularFont(getActivity()));
		listViewMissedRides = (ListView) rootView.findViewById(R.id.listViewMissedRides);
		
		driverMissedRidesListAdapter = new DriverMissedRidesListAdapter();
		listViewMissedRides.setAdapter(driverMissedRidesListAdapter);
		
		progressBarMissedRides.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);
		
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getMissedRidesAsync(getActivity());
//				if(!"No rides currently".equalsIgnoreCase(textViewInfoDisplay.getText().toString())){
//					getMissedRidesAsync(getActivity());
//				}
			}
		});
		
		getMissedRidesAsync(getActivity());

		return rootView;
	}

	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			
			missedRideInfos.clear();
			driverMissedRidesListAdapter.notifyDataSetChanged();
		}
		else{
			if(missedRideInfos.size() == 0){
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverMissedRidesListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroy() {
		if(fetchMissedRidesClient != null){
			fetchMissedRidesClient.cancelAllRequests(true);
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

	class ViewHolderDriverMissedRides {
		TextView textViewMissedRideFrom, textViewMissedRideFromValue, textViewMissedRideTime, textViewMissedRideCustomerName, textViewMissedRideCustomerNameValue,
				textViewMissedRideCustomerDistance, textViewMissedRideCustomerDistanceValue;
		LinearLayout relative;
		int id;
	}

	class DriverMissedRidesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverMissedRides holder;
		DateOperations dateOperations;
		public DriverMissedRidesListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateOperations = new DateOperations();
		}

		@Override
		public int getCount() {
			return missedRideInfos.size();
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
				holder = new ViewHolderDriverMissedRides();
				convertView = mInflater.inflate(R.layout.list_item_missed_rides, null);
				
				holder.textViewMissedRideFrom = (TextView) convertView.findViewById(R.id.textViewMissedRideFrom); holder.textViewMissedRideFrom.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideFromValue = (TextView) convertView.findViewById(R.id.textViewMissedRideFromValue); holder.textViewMissedRideFromValue.setTypeface(Data.regularFont(getActivity()));
				
				holder.textViewMissedRideTime = (TextView) convertView.findViewById(R.id.textViewMissedRideTime); holder.textViewMissedRideTime.setTypeface(Data.regularFont(getActivity()));
				
				holder.textViewMissedRideCustomerName = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerName); holder.textViewMissedRideCustomerName.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideCustomerNameValue = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerNameValue); holder.textViewMissedRideCustomerNameValue.setTypeface(Data.regularFont(getActivity()));
				
				holder.textViewMissedRideCustomerDistance = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerDistance); holder.textViewMissedRideCustomerDistance.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideCustomerDistanceValue = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerDistanceValue); holder.textViewMissedRideCustomerDistanceValue.setTypeface(Data.regularFont(getActivity()));
				
				
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverMissedRides) convertView.getTag();
			}
			
			if(dateOperations == null){
				dateOperations = new DateOperations();
			}
			
			MissedRideInfo missedRideInfo = missedRideInfos.get(position);
			
			holder.id = position;
			
			holder.textViewMissedRideFromValue.setText(missedRideInfo.pickupLocationAddress);
			holder.textViewMissedRideTime.setText(dateOperations.convertDate(dateOperations.utcToLocal(missedRideInfo.timestamp)));
			
			holder.textViewMissedRideCustomerNameValue.setText(missedRideInfo.customerName);
			holder.textViewMissedRideCustomerDistanceValue.setText(missedRideInfo.customerDistance+" km");
			
			return convertView;
		}
	}
	


	/**
	 * ASync for get rides from server
	 */
	public void getMissedRidesAsync(final Activity activity) {
		if(fetchMissedRidesClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarMissedRides.setVisibility(View.VISIBLE);
				textViewInfoDisplay.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				fetchMissedRidesClient = Data.getClient();
				fetchMissedRidesClient.post(Data.SERVER_URL + "/get_missed_rides", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarMissedRides.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
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
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										
	//									{
	//									    "missed_rides": [
	//									         {
//							            			"engagement_id": 250,
//							            			"pickup_location_address": "1604, Sector 18D, Chandigarh 160018, India",
//							            			"timestamp": "2014-07-30 05:51:42",
//							            			"user_name": "Shankar Bhagwati",
//							            			"distance": 0.42
//							        			}
	//									    ]
	//									}
										
										JSONArray missedRidesData = jObj.getJSONArray("missed_rides");
										missedRideInfos.clear();
										DecimalFormat decimalFormat = new DecimalFormat("#.#");
										if(missedRidesData.length() > 0){
											for(int i=missedRidesData.length()-1; i>=0; i--){
												JSONObject rideData = missedRidesData.getJSONObject(i);
												missedRideInfos.add(new MissedRideInfo(rideData.getString("engagement_id"),
														rideData.getString("pickup_location_address"),
														rideData.getString("timestamp"),
														rideData.getString("user_name"),
														decimalFormat.format(rideData.getDouble("distance"))));
											}
										}
										updateListData("No missed rides currently", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBarMissedRides.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchMissedRidesClient = null;
								super.onFinish();
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}

	}


}
