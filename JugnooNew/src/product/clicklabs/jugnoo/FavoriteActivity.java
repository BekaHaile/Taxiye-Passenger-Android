package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FavoriteActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView favTitleText, noFavoriteLocationsText;
	ListView favoriteList;
	ProgressBar progressBarFavorites;
	
	static boolean zoomToMap = false;
	static LatLng zoomLatLng;
	
	FavoriteListAdapter favoriteListAdapter;
	ArrayList<FavoriteLocation> favoriteLocations = new ArrayList<FavoriteLocation>();
	
	AsyncHttpClient fetchFavoritesClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_activity);
		
		zoomToMap = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(FavoriteActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		favTitleText = (TextView) findViewById(R.id.favTitleText); favTitleText.setTypeface(Data.regularFont(getApplicationContext()));
		noFavoriteLocationsText = (TextView) findViewById(R.id.noFavoriteLocationsText); 
		noFavoriteLocationsText.setTypeface(Data.regularFont(getApplicationContext()));
		progressBarFavorites = (ProgressBar) findViewById(R.id.progressBarFavorites);
		
		progressBarFavorites.setVisibility(View.GONE);
		noFavoriteLocationsText.setVisibility(View.GONE);
		
		
		favoriteList = (ListView) findViewById(R.id.favoriteList);
		favoriteListAdapter = new FavoriteListAdapter();
		
		favoriteList.setAdapter(favoriteListAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		noFavoriteLocationsText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAllFavoriteAsync(FavoriteActivity.this);
			}
		});
		
		getAllFavoriteAsync(FavoriteActivity.this);
		
	}
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			noFavoriteLocationsText.setText(message);
			noFavoriteLocationsText.setVisibility(View.VISIBLE);
			
			favoriteLocations.clear();
			favoriteListAdapter.notifyDataSetChanged();
		}
		else{
			if(favoriteLocations.size() == 0){
				noFavoriteLocationsText.setText(message);
				noFavoriteLocationsText.setVisibility(View.VISIBLE);
			}
			else{
				noFavoriteLocationsText.setVisibility(View.GONE);
			}
			favoriteListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderFavorite {
		TextView name;
		ImageView favDeleteBtn;
		LinearLayout relative;
		int id;
	}

	class FavoriteListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderFavorite holder;

		public FavoriteListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return favoriteLocations.size();
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
				
				holder = new ViewHolderFavorite();
				convertView = mInflater.inflate(R.layout.favorite_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); holder.name.setTypeface(Data.regularFont(getApplicationContext()));
				holder.favDeleteBtn = (ImageView) convertView.findViewById(R.id.favDeleteBtn);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				holder.favDeleteBtn.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 127));
				ASSL.DoMagic(holder.relative);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderFavorite) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+favoriteLocations.get(position).name);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = favoriteLocations.get(holder.id);
					
					Log.e("searchResult.latLng ==",">"+favoriteLocation.latLng);
					
					zoomToMap = true;
					zoomLatLng = favoriteLocation.latLng;
					
					finish();
					overridePendingTransition(R.anim.left_in, R.anim.left_out);
					
				}
			});
			
			
			
			holder.favDeleteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = favoriteLocations.get(holder.id);
					
					deleteFavoriteAsync(FavoriteActivity.this, favoriteLocation.sNo, holder.id);
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	/**
	 * ASync for get all favorite locations from server
	 */
	public void getAllFavoriteAsync(final Activity activity) {
		if(fetchFavoritesClient == null){
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				Log.i("access_token", "=" + Data.userData.accessToken);
				fetchFavoritesClient = Data.getClient();
				fetchFavoritesClient.post(Data.SERVER_URL + "/get_fav_locations", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								updateListData("Some error occurred. Tap to retry", true);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										int flag = jObj.getInt("flag");	
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										}
										else{
										}
										updateListData("Some error occurred. Tap to retry", true);
									}
									else{
										JSONArray favouriteData = jObj.getJSONArray("favourite_data");
										favoriteLocations.clear();
										if(favouriteData.length() > 0){
											for(int i=0; i<favouriteData.length(); i++){
												JSONObject favData = favouriteData.getJSONObject(i);
												favoriteLocations.add(new FavoriteLocation(favData.getInt("s_no"), favData.getString("fav_name"), 
														new LatLng(favData.getDouble("fav_latitude"), favData.getDouble("fav_longitude"))));
											}
										}
										updateListData("No favorites", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
								}
		
							}
							
							@Override
								public void onFinish() {
									super.onFinish();
									fetchFavoritesClient = null;
								}
							
						});
			}
			else {
			}
		}

	}
	
	
	/**
	 * ASync for deleteFavoriteAsync from server
	 */
	public void deleteFavoriteAsync(final Activity activity, int sNo, final int index) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("s_no", ""+sNo);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("s_no", "="+sNo);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/delete_fav_locations", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

					@Override
					public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Removed from favourite successfully"}	//result
									
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
									
									favoriteLocations.remove(index);
									updateListData("No favorites", false);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
						
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
		if(fetchFavoritesClient != null){
			fetchFavoritesClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
