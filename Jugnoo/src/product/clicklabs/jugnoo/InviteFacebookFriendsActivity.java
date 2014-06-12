package product.clicklabs.jugnoo;

import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.Builder;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class InviteFacebookFriendsActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	EditText searchFriendsEt;
	GridView facebookFriendsGrid;
	TextView noFriendsText;
	Button sendInviteBtn;
	
	FacebookFriendsGridAdapter facebookFriendsGridAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_friends_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(InviteFacebookFriendsActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		searchFriendsEt = (EditText) findViewById(R.id.searchFriendsEt); searchFriendsEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		facebookFriendsGrid = (GridView) findViewById(R.id.facebookFriendsGrid);
		noFriendsText = (TextView) findViewById(R.id.noFriendsText); noFriendsText.setTypeface(Data.regularFont(getApplicationContext()));
		
		sendInviteBtn = (Button) findViewById(R.id.sendInviteBtn); sendInviteBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		Data.friendInfosDuplicate.clear();
		Data.friendInfosDuplicate.addAll(Data.friendInfos);
		
		
		facebookFriendsGridAdapter = new FacebookFriendsGridAdapter();
		facebookFriendsGrid.setAdapter(facebookFriendsGridAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		
		searchFriendsEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int count = facebookFriendsGridAdapter.search(s.toString());
				if(count == 0){
					searchFriendsEt.requestFocus();
					searchFriendsEt.setError("No results found");
				}
				else{
					searchFriendsEt.setError(null);
				}
			}
		});
		
		
		sendInviteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					
					String userIds = "";
					
					for(int i = 0; i<Data.friendInfosDuplicate.size(); i++){
						if(Data.friendInfosDuplicate.get(i).tick){
							userIds = userIds + Data.friendInfosDuplicate.get(i).fbId + ",";
						}
					}
					
					
					//TODO
					if(!"".equalsIgnoreCase(userIds)){
						userIds = userIds.substring(0, userIds.length()-1);
						inviteFbFriend(userIds);
					}
					else{
						inviteFbFriend("");
//						new DialogPopup().alertPopup(InviteFacebookFriendsActivity.this, "", "Select some friends first.");
					}
					
//					getAllFBFriends();
					
				} catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		
		
		if(Data.friendInfos.size() == 0){
			noFriendsText.setVisibility(View.VISIBLE);
		}
		else{
			noFriendsText.setVisibility(View.GONE);
		}
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
	}
	
	
	
	public class FacebookFriendsGridAdapter extends ArrayAdapter<String> {

		FriendViewHolder holder;
		
		public FacebookFriendsGridAdapter() {
			super(InviteFacebookFriendsActivity.this, R.layout.invite_friends_grid_item, new ArrayList<String>());
		}

		@Override
		public int getCount() {
			return Data.friendInfos.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if (row == null) {
				LayoutInflater inflater = ((Activity) InviteFacebookFriendsActivity.this).getLayoutInflater();
				row = inflater.inflate(R.layout.invite_friends_grid_item, parent, false);
				
				holder = new FriendViewHolder();
				
				holder.friendImage = (ImageView) row.findViewById(R.id.friendImage);
				holder.tick = (ImageView) row.findViewById(R.id.tick);
				
				holder.progress = (ProgressBar) row.findViewById(R.id.progress);
				
				holder.friendName = (TextView) row.findViewById(R.id.friendName); holder.friendName.setTypeface(Data.regularFont(getApplicationContext()));
				
				holder.relative = (LinearLayout) row.findViewById(R.id.relative);
				holder.relative.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				ASSL.DoMagic(holder.relative);
				
				holder.relative.setTag(holder);
				
				row.setTag(holder);

			} else {
				holder = (FriendViewHolder) row.getTag();
			}

			holder.idPos = position;
			
			FriendInfo friendInfo = Data.friendInfos.get(position);
			
			holder.friendName.setText(friendInfo.fbName);
			
			AQuery aq = new AQuery(holder.friendImage);  //http://graph.facebook.com/100005838482296/picture?width=160&height=160
			aq.id(holder.friendImage).progress(holder.progress).image(friendInfo.fbImage, Data.imageOptionsFullRound());
			
			if(friendInfo.flag == 0){
				holder.tick.setVisibility(View.VISIBLE);
			}
			else{
				holder.tick.setVisibility(View.GONE);
			}
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (FriendViewHolder) v.getTag();
					
					if(Data.friendInfos.get(holder.idPos).flag == 0){
						if(Data.friendInfos.get(holder.idPos).tick){
							Data.friendInfos.get(holder.idPos).tick = false;
						}
						else{
							Data.friendInfos.get(holder.idPos).tick = true;
						}
						
						for(int i=0; i<Data.friendInfosDuplicate.size(); i++){
							if(Data.friendInfosDuplicate.get(i).fbId.equalsIgnoreCase(Data.friendInfos.get(holder.idPos).fbId)){
								Data.friendInfosDuplicate.get(i).tick = Data.friendInfos.get(holder.idPos).tick;
								break;
							}
						}
						notifyDataSetChanged();
					}
					
					
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							
//							
//							
//						}
//					}).start();
					
					
					
				}
			});
			
			
			
			if(friendInfo.tick){
				holder.tick.setBackgroundResource(R.drawable.check_on);
			}
			else{
				holder.tick.setBackgroundResource(R.drawable.check_off);
			}
			
			
			

			return row;
		}

		
		public int search(String text){
			text = text.toLowerCase();
			
			Data.friendInfos.clear();
			
			if(text.length() == 0){
				Data.friendInfos.addAll(Data.friendInfosDuplicate);
			}
			else{
				for(FriendInfo friendInfo : Data.friendInfosDuplicate){
					if(friendInfo.fbName.toLowerCase().contains(text)){
						Data.friendInfos.add(friendInfo);
					}
				}
			}
			
			notifyDataSetChanged();
			
			return Data.friendInfos.size();
		}
		
	}

	class FriendViewHolder {
		int idPos;
		ImageView friendImage, tick;
		ProgressBar progress;
		TextView friendName;
		LinearLayout relative;
	}
	

	
//	public void getAllFBFriends(){
////		Request request = new Request(Session.getActiveSession(), "me/taggable_friends", null, HttpMethod.POST);
////		request.setCallback(new Request.Callback() {
////		    @Override
////		    public void onCompleted(Response response) {
////		        if (response.getError() == null) {
////		        	
////		        }
////		        Log.e("Tests", "got response: " + response);
////		    }
////		});
////		request.executeAsync();
//	}
	
	
	public void inviteFbFriend(String userId){
		
		Bundle parameters = new Bundle();
		parameters.putString("to", ""+userId);
		parameters.putString("message", "Download app now to get started. Available on Google Play Store and App Store");
		parameters.putString("data", "Get from one place to another with ease.");
		parameters.putString("link", "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo");
		

		WebDialog.Builder builder = new Builder(InviteFacebookFriendsActivity.this, Session.getActiveSession(), "apprequests", parameters);

		builder.setOnCompleteListener(new OnCompleteListener() {

		    @Override
		    public void onComplete(Bundle values, FacebookException error) {
		    	Log.e("values","="+values);
		    	Log.e("error","="+error);
		        if (error != null){
		            if (error instanceof FacebookOperationCanceledException){
		                Toast.makeText(InviteFacebookFriendsActivity.this,"Request cancelled",Toast.LENGTH_SHORT).show();
		            }
		            else{
		                Toast.makeText(InviteFacebookFriendsActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
		            }
		        }
		        else{

		            final String requestId = values.getString("request");
		            if (requestId != null) {
		            	new DialogPopup().alertPopup(InviteFacebookFriendsActivity.this, "", "Friends invited successfully.");
		            } 
		            else {
		                Toast.makeText(InviteFacebookFriendsActivity.this,"Request cancelled",Toast.LENGTH_SHORT).show();
		            }
		        }                       
		    }
		});

		WebDialog webDialog = builder.build();
		webDialog.show();
	        
	        
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
