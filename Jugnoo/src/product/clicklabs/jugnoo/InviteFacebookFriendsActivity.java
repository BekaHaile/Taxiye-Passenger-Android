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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;

public class InviteFacebookFriendsActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	EditText searchFriendsEt;
	GridView facebookFriendsGrid;
	Button sendInviteBtn;
	
	FacebookFriendsGridAdapter facebookFriendsGridAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_friends_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(InviteFacebookFriendsActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		
		searchFriendsEt = (EditText) findViewById(R.id.searchFriendsEt);
		
		facebookFriendsGrid = (GridView) findViewById(R.id.facebookFriendsGrid);
		
		sendInviteBtn = (Button) findViewById(R.id.sendInviteBtn);
		
		
		Data.friendInfos.add(new FriendInfo("Ram1", "http://graph.facebook.com/100005838482296/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra2", "http://graph.facebook.com/100005838482297/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra3", "http://graph.facebook.com/100005838482298/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra4", "http://graph.facebook.com/100005838482299/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra5", "http://graph.facebook.com/100005838482206/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra6", "http://graph.facebook.com/100005838482207/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra7", "http://graph.facebook.com/100005838482208/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra8", "http://graph.facebook.com/100005838482216/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra9", "http://graph.facebook.com/100005838482227/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("Ra0", "http://graph.facebook.com/100005838482238/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("sam", "http://graph.facebook.com/100005838482249/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("dam", "http://graph.facebook.com/100005838482256/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("fam", "http://graph.facebook.com/100005838482267/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("gam", "http://graph.facebook.com/100005838482278/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("ham", "http://graph.facebook.com/100005838482116/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("jam", "http://graph.facebook.com/100005838482227/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("kam", "http://graph.facebook.com/100005838482338/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("lam", "http://graph.facebook.com/100005838482449/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("qam", "http://graph.facebook.com/100005838482556/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("wam", "http://graph.facebook.com/100005838482667/picture?width=160&height=160"));
		Data.friendInfos.add(new FriendInfo("eam", "http://graph.facebook.com/100005838482778/picture?width=160&height=160"));
		
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
				facebookFriendsGridAdapter.search(s.toString());
			}
		});
		
		
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
				
				holder.friendName = (TextView) row.findViewById(R.id.friendName);
				
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
			
			holder.friendName.setText(friendInfo.name);
			
			AQuery aq = new AQuery(holder.friendImage);  //http://graph.facebook.com/100005838482296/picture?width=160&height=160
			aq.id(holder.friendImage).progress(holder.progress).image(friendInfo.image, Data.imageOptionsFullRound());
			
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (FriendViewHolder) v.getTag();
					
					if(Data.friendInfos.get(holder.idPos).tick){
						Data.friendInfos.get(holder.idPos).tick = false;
					}
					else{
						Data.friendInfos.get(holder.idPos).tick = true;
					}
					
					notifyDataSetChanged();
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

		
		public void search(String text){
			text = text.toLowerCase();
			
			Data.friendInfos.clear();
			
			if(text.length() == 0){
				Data.friendInfos.addAll(Data.friendInfosDuplicate);
			}
			else{
				for(FriendInfo friendInfo : Data.friendInfosDuplicate){
					if(friendInfo.name.toLowerCase().contains(text)){
						Data.friendInfos.add(friendInfo);
					}
				}
			}
			
			notifyDataSetChanged();
			
		}
		
	}

	class FriendViewHolder {
		int idPos;
		ImageView friendImage, tick;
		ProgressBar progress;
		TextView friendName;
		LinearLayout relative;
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
