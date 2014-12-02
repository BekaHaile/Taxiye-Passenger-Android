package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.ScheduleStatus;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateOperations;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FutureSchedulesFragment extends Fragment {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;
	
	FutureSchedulesListAdapter futureSchedulesListAdapter;
	
	RelativeLayout main;
	
	AsyncHttpClient fetchFutureSchedulesClient;

	ArrayList<FutureSchedule> futureSchedules = new ArrayList<FutureSchedule>();
	
	public FutureSchedulesFragment() {
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		futureSchedules.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Data.regularFont(getActivity()));
		listView = (ListView) rootView.findViewById(R.id.listView);
		
		futureSchedulesListAdapter = new FutureSchedulesListAdapter(getActivity());
		listView.setAdapter(futureSchedulesListAdapter);
		
		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);
		
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFutureSchedulesAsync(getActivity());
			}
		});
		
		
		futureSchedules.add(new FutureSchedule("1", "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160102", "2014-12-02 06:11:11", "Request in Progress", new LatLng(0, 0), 1, 0));
		futureSchedules.add(new FutureSchedule("1", "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160102", "2014-12-02 06:15:11", "Request confirmed", new LatLng(0, 0), 0, 1));
		futureSchedules.add(new FutureSchedule("1", "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160102", "2014-12-02 06:19:11", "Request canceled", new LatLng(0, 0), 0, 2));
		
		futureSchedulesListAdapter.notifyDataSetChanged();
		
//		getFutureSchedulesAsync(getActivity());
		
		return rootView;
	}

	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			
			futureSchedules.clear();
			futureSchedulesListAdapter.notifyDataSetChanged();
		}
		else{
			if(futureSchedules.size() == 0){
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			futureSchedulesListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroy() {
		if(fetchFutureSchedulesClient != null){
			fetchFutureSchedulesClient.cancelAllRequests(true);
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

	
	class ViewHolderFutureSchedule {
		TextView pickupLocationText, pickupLocationValue, pickupDateTimeText, pickupDateTimeValue, scheduleStatusValue;
		ImageView forwardImage;
		LinearLayout relative;
		int id;
	}

	class FutureSchedulesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderFutureSchedule holder;
		Activity activity;
		public FutureSchedulesListAdapter(Activity context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.activity = context;
		}

		@Override
		public int getCount() {
			return futureSchedules.size();
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
				holder = new ViewHolderFutureSchedule();
				convertView = mInflater.inflate(R.layout.list_item_future_schedule, null);
				
				holder.pickupLocationText = (TextView) convertView.findViewById(R.id.pickupLocationText); holder.pickupLocationText.setTypeface(Data.regularFont(activity), Typeface.BOLD);
				holder.pickupLocationValue = (TextView) convertView.findViewById(R.id.pickupLocationValue); holder.pickupLocationValue.setTypeface(Data.regularFont(activity));
				holder.pickupDateTimeText = (TextView) convertView.findViewById(R.id.pickupDateTimeText); holder.pickupDateTimeText.setTypeface(Data.regularFont(activity), Typeface.BOLD);
				holder.pickupDateTimeValue = (TextView) convertView.findViewById(R.id.pickupDateTimeValue); holder.pickupDateTimeValue.setTypeface(Data.regularFont(activity));
				holder.scheduleStatusValue = (TextView) convertView.findViewById(R.id.scheduleStatusValue); holder.scheduleStatusValue.setTypeface(Data.regularFont(activity));
				
				holder.forwardImage = (ImageView) convertView.findViewById(R.id.forwardImage);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderFutureSchedule) convertView.getTag();
			}
			
			FutureSchedule futureSchedule = futureSchedules.get(position);
			
			holder.id = position;
			
			holder.pickupLocationValue.setText(futureSchedule.pickupAddress);
			holder.pickupDateTimeValue.setText(DateOperations.convertDate(futureSchedule.pickupTime));
			holder.scheduleStatusValue.setText(futureSchedule.scheduleStatusStr);
			
			if(ScheduleStatus.IN_PROCESS.getOrdinal() == futureSchedule.scheduleStatusFlag){
				holder.scheduleStatusValue.setTextColor(activity.getResources().getColor(R.color.yellow_status));
			}
			else if(ScheduleStatus.CONFIRMED.getOrdinal() == futureSchedule.scheduleStatusFlag){
				holder.scheduleStatusValue.setTextColor(activity.getResources().getColor(R.color.green_status));
			}
			else{
				holder.scheduleStatusValue.setTextColor(activity.getResources().getColor(R.color.red_status));
			}
			
			if(futureSchedule.isChangeable == 1){
				holder.relative.setBackgroundResource(R.drawable.list_white_selector);
				holder.forwardImage.setVisibility(View.VISIBLE);
			}
			else{
				holder.relative.setBackgroundResource(R.drawable.list_white_normal);
				holder.forwardImage.setVisibility(View.GONE);
			}
			
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderFutureSchedule) v.getTag();
					FutureSchedule futureSchedule = futureSchedules.get(holder.id);
					if(futureSchedule.isChangeable == 1){
						showFutureScheduleOperations(activity, futureSchedule);
					}
				}
			});
			
			
			return convertView;
		}

	}
	

	
	void showFutureScheduleOperations(final Activity activity, final FutureSchedule futureSchedule) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.future_schedule_operations_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
			TextView textExtra = (TextView) dialog.findViewById(R.id.textExtra); textExtra.setTypeface(Data.regularFont(activity));
			
			textHead.setText("Future Ride");
			textMessage.setText("Do you want to change your scheduled ride?");
			textExtra.setText("Terms and Conditions");
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity)); btnOk.setText("Reschedule");
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity)); btnCancel.setText("Remove");
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn);
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					ScheduleRideActivity.selectedScheduleCalendar = DateOperations.getCalendarFromTimeStamp(futureSchedule.pickupTime);
					ScheduleRideActivity.selectedScheduleLatLng = futureSchedule.pickupLatLng;
					switchToScheduleScreen(activity);
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
				}
			});
			
			textExtra.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			
			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
			
			
			dialog.findViewById(R.id.innerRl).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});

			frameLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void switchToScheduleScreen(Activity activity){
		startActivity(new Intent(activity, ScheduleRideActivity.class));
		activity.finish();
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	

	/**
	 * ASync for get rides from server
	 */
	public void getFutureSchedulesAsync(final Activity activity) {
		if(fetchFutureSchedulesClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBar.setVisibility(View.VISIBLE);
				textViewInfoDisplay.setVisibility(View.GONE);
				
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				
				fetchFutureSchedulesClient = Data.getClient();
				fetchFutureSchedulesClient.post(Data.SERVER_URL + "/future_schedules", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBar.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.d("Server response", "response = " + response);
								
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										JSONArray futureSchedulesArray = jObj.getJSONArray("future_schedules");
										futureSchedules.clear();
										if(futureSchedulesArray.length() > 0){
											for(int i=0; i<futureSchedulesArray.length(); i++){
												JSONObject futureScheduleData = futureSchedulesArray.getJSONObject(i);
												futureSchedules.add(new FutureSchedule(futureScheduleData.getString("schedule_id"), 
														futureScheduleData.getString("pickup_address"), 
														futureScheduleData.getString("pickup_time"), 
														futureScheduleData.getString("status_string"), 
														new LatLng(futureScheduleData.getDouble("pickup_latitude"), futureScheduleData.getDouble("pickup_longitude")), 
														futureScheduleData.getInt("is_changeable"),
														futureScheduleData.getInt("status_flag")));
											}
										}
										updateListData("No future schedules", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchFutureSchedulesClient = null;
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
