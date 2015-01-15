package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.CouponStatus;
import product.clicklabs.jugnoo.datastructure.NotificationInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateComparator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class NotificationsActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewInfo;
	ProgressBar progressBar;
	
	ListView listView;
	NotificationsListAdapter notificationsListAdapter;
	
	AsyncHttpClient fetchNotificationsAsyncHttpClient;
	
	ArrayList<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
	
	
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
		setContentView(R.layout.activity_account);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(NotificationsActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		textViewInfo = (TextView) findViewById(R.id.textViewAccountInfo); textViewInfo.setTypeface(Data.regularFont(getApplicationContext()));
		progressBar = (ProgressBar) findViewById(R.id.progressBarAccount);
		
		notificationInfos.clear();
		
		listView = (ListView) findViewById(R.id.listViewCoupons);
		notificationsListAdapter = new NotificationsListAdapter(NotificationsActivity.this);
		listView.setAdapter(notificationsListAdapter);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchNotificationsAsyncHttpClient != null){
			fetchNotificationsAsyncHttpClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			notificationInfos.clear();
			notificationsListAdapter.notifyDataSetChanged();
		}
		else{
			if(notificationInfos.size() == 0){
				textViewInfo.setText(message);
				textViewInfo.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
			}
			notificationsListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderNotification {
		TextView textViewTitle;
		RelativeLayout relative;
		int id;
	}

	class NotificationsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderNotification holder;
		Context context;
		public NotificationsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return notificationInfos.size();
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
				holder = new ViewHolderNotification();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewYouHave); holder.textViewTitle.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderNotification) convertView.getTag();
			}
			
			holder.id = position;
			
			NotificationInfo notificationInfo = notificationInfos.get(position);
			
			holder.textViewTitle.setText(notificationInfo.title);
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderNotification) v.getTag();
				}
			});
			
			return convertView;
		}

	}
}
