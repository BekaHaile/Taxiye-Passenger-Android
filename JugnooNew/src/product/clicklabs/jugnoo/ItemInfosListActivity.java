package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ItemInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class ItemInfosListActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn, infoBtn;
	TextView title;
	
	TextView textViewInfo;
	ProgressBar progressBar;
	
	ListView listView;
	ItemInfoListAdapter itemInfoListAdapter;
	
	
	
	
	AsyncHttpClient fetchItemInfosClient;
	
	ArrayList<ItemInfo> itemInfosList = new ArrayList<ItemInfo>();
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_list);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ItemInfosListActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		infoBtn = (Button) findViewById(R.id.infoBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.regularFont(getApplicationContext()));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		itemInfosList.clear();
		
		listView = (ListView) findViewById(R.id.listView);
		itemInfoListAdapter = new ItemInfoListAdapter(ItemInfosListActivity.this);
		listView.setAdapter(itemInfoListAdapter);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		infoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getItemInfosAsync(ItemInfosListActivity.this);
			}
		});
		
		
		
//		getItemInfosAsync(ItemInfosListActivity.this);
		
		
		itemInfosList.add(new ItemInfo(1, "Cherry Cake", "100", "Cherry Cake", "http://www.divianaalchemy.com/storage/Lucuma-Cake-IMG_4573.jpg"));
		itemInfosList.add(new ItemInfo(2, "Gelato Cake", "150", "Gelato Cake", "http://www.frostgelato.com/images/cake_img.png"));
		itemInfosList.add(new ItemInfo(3, "Rum Cake", "100", "Rum Cake", "http://www.matthews1812house.com/cgi-local/db_images/products/cache/57-image-335-290-crop.jpg"));
		itemInfosList.add(new ItemInfo(4, "Pineapple Cake", "100", "Pineapple Cake", "http://hostedmedia.reimanpub.com/TOH/Images/Photos/37/300x300/exps12159_QC10107C49.jpg"));
		
		itemInfoListAdapter.notifyDataSetChanged();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchItemInfosClient != null){
			fetchItemInfosClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			itemInfosList.clear();
			itemInfoListAdapter.notifyDataSetChanged();
		}
		else{
			if(itemInfosList.size() == 0){
				textViewInfo.setText(message);
				textViewInfo.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
			}
			itemInfoListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderItemInfo {
		TextView textViewItemName, textViewItemPrice, textViewItemCount;
		ImageView imageViewItem, imageViewMinus, imageViewPlus;
		LinearLayout relative;
		int id;
	}

	class ItemInfoListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderItemInfo holder;
		Context context;
		public ItemInfoListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return itemInfosList.size();
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
				holder = new ViewHolderItemInfo();
				convertView = mInflater.inflate(R.layout.list_item_item_info, null);
				
				holder.textViewItemName = (TextView) convertView.findViewById(R.id.textViewItemName); holder.textViewItemName.setTypeface(Data.regularFont(context), Typeface.BOLD);
				holder.textViewItemPrice = (TextView) convertView.findViewById(R.id.textViewItemPrice); holder.textViewItemPrice.setTypeface(Data.regularFont(context));
				holder.textViewItemCount = (TextView) convertView.findViewById(R.id.textViewItemCount); holder.textViewItemCount.setTypeface(Data.regularFont(context));
				
				holder.imageViewItem = (ImageView) convertView.findViewById(R.id.imageViewItem);
				holder.imageViewMinus = (ImageView) convertView.findViewById(R.id.imageViewMinus);
				holder.imageViewPlus = (ImageView) convertView.findViewById(R.id.imageViewPlus);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.imageViewMinus.setTag(holder);
				holder.imageViewPlus.setTag(holder);
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderItemInfo) convertView.getTag();
			}
			
			holder.id = position;
			
			ItemInfo itemInfo = itemInfosList.get(position);
			
			holder.textViewItemName.setText(itemInfo.name);
			holder.textViewItemPrice.setText("Rs. "+itemInfo.price);
			
			
			try{Picasso.with(context).load(itemInfo.image).into(holder.imageViewItem);}catch(Exception e){}
			
			if(itemInfo.countSelected > 0){
				holder.textViewItemCount.setText(""+itemInfo.countSelected);
			}
			else{
				holder.textViewItemCount.setText("-");
			}
			
			
			holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					if(itemInfosList.get(holder.id).countSelected > 0){
						itemInfosList.get(holder.id).countSelected--;
						notifyDataSetChanged();
					}
				}
			});
			
			holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					if(itemInfosList.get(holder.id).countSelected < 99){
						itemInfosList.get(holder.id).countSelected++;
						notifyDataSetChanged();
					}
				}
			});
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderItemInfo) v.getTag();
					ItemInfo itemInfo = itemInfosList.get(holder.id);
					
				}
			});
			
			return convertView;
		}

	}
	
	
	/**
	 * ASync for get Account info from server
	 */
	public void getItemInfosAsync(final Activity activity) {
		if(fetchItemInfosClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBar.setVisibility(View.VISIBLE);
				itemInfosList.clear();
				itemInfoListAdapter.notifyDataSetChanged();
				textViewInfo.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				fetchItemInfosClient = Data.getClient();
				fetchItemInfosClient.post(Data.SERVER_URL + "/get_items", params,
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
								Log.e("Server response", "response = " + response);
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
										
//										{
//										    "coupons": [
//										        {
//										            "title": "Free ride",
//										            "description": "upte 100/-",
//										            "discount": 100,
//										            "maximum": 100,
//										            "image": null,
//										            "type": 0,
//										            "redeemed_on": null,
//										            "status": 1,
//										            "expiry_date": "2014-11-07T18:29:59.000Z"
//										        }
//										    ]
//										}
										
										itemInfosList.clear();
										
										if(jObj.has("items")){
											JSONArray itemsData = jObj.getJSONArray("items");
											if(itemsData.length() > 0){
												for(int i=0; i<itemsData.length(); i++){
													JSONObject itemData = itemsData.getJSONObject(i);
													
													itemInfosList.add(new ItemInfo(itemData.getInt("id"), itemData.getString("name"), itemData.getString("price"), 
															itemData.getString("description"), itemData.getString("image")));
												}
											}
										}
										updateListData("No items there", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchItemInfosClient = null;
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
