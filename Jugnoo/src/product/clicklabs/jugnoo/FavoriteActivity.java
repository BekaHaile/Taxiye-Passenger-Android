package product.clicklabs.jugnoo;

import org.json.JSONObject;

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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FavoriteActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView favTitleText, noFavoriteLocationsText;
	ListView favoriteList;
	
	static boolean zoomToMap = false;
	static LatLng zoomLatLng;
	
	FavoriteListAdapter favoriteListAdapter;
	
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
		
		updateFavoriteList();
		
	}
	
	void updateFavoriteList(){
		
		favoriteListAdapter.notifyDataSetChanged();
		
		if(Data.favoriteLocations.size() == 0){
			noFavoriteLocationsText.setVisibility(View.VISIBLE);
		}
		else{
			noFavoriteLocationsText.setVisibility(View.GONE);
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
			return Data.favoriteLocations.size();
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
			
			holder.name.setText(""+Data.favoriteLocations.get(position).name);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = Data.favoriteLocations.get(holder.id);
					
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
					
					FavoriteLocation favoriteLocation = Data.favoriteLocations.get(holder.id);
					
					deleteFavoriteAsync(FavoriteActivity.this, favoriteLocation.sNo, holder.id);
					
				}
			});
			
			
			return convertView;
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
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/delete_fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Removed from favourite successfully"}	//result
									
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
									
									Data.favoriteLocations.remove(index);
									updateFavoriteList();
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
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
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
