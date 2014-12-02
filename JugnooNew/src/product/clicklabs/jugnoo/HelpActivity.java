package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.HelpItem;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class HelpActivity extends FragmentActivity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	ListView listViewHelp;
	RelativeLayout helpExpandedRl;
	
	ProgressBar progressBarHelp;
	TextView textViewInfoDisplay;
	WebView helpWebview;
	
	HelpListAdapter helpListAdapter;
	
	ArrayList<HelpItem> helpItems = new ArrayList<HelpItem>();
	HelpItem selectedHelpItem;
	
	AsyncHttpClient fetchHelpDataClient;
	
	// *****************************Used for flurry work***************//
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
		setContentView(R.layout.help_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(HelpActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		listViewHelp = (ListView) findViewById(R.id.listViewHelp);
		helpListAdapter = new HelpListAdapter();
		listViewHelp.setAdapter(helpListAdapter);
		
		helpExpandedRl = (RelativeLayout) findViewById(R.id.helpExpandedRl);
		helpExpandedRl.setVisibility(View.GONE);
		
		
		progressBarHelp = (ProgressBar) findViewById(R.id.progressBarHelp);
		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Data.regularFont(getApplicationContext()));
		helpWebview = (WebView) findViewById(R.id.helpWebview);
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(selectedHelpItem != null){
					getHelpAsync(HelpActivity.this, selectedHelpItem);
				}
			}
		});
		
		
		helpItems.clear();
		helpItems.add(new HelpItem(HelpSection.MAIL_US, "Send Us an Email"));
		helpItems.add(new HelpItem(HelpSection.CALL_US, "Call Us"));
		helpItems.add(new HelpItem(HelpSection.FAQ, "FAQs"));
		helpItems.add(new HelpItem(HelpSection.ABOUT, "About Jugnoo"));
		helpItems.add(new HelpItem(HelpSection.TERMS, "Terms of Use"));
		helpItems.add(new HelpItem(HelpSection.PRIVACY, "Privacy Policy"));
		
		
		helpListAdapter.notifyDataSetChanged();
		
		
	}
	
	
	public void openHelpData(HelpItem helpItem, String data, boolean errorOccured) {
		if (errorOccured) {
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			textViewInfoDisplay.setText(data);
			helpWebview.setVisibility(View.GONE);
		} else {
			textViewInfoDisplay.setVisibility(View.GONE);
			helpWebview.setVisibility(View.VISIBLE);
			loadHTMLContent(data);
		}
		selectedHelpItem = helpItem;
		title.setText("" + helpItem.name);

	}
	
	public void loadHTMLContent(String data){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        helpWebview.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}
	
	
	class ViewHolderHelp {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class HelpListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderHelp holder;

		public HelpListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return helpItems.size();
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
				holder = new ViewHolderHelp();
				convertView = mInflater.inflate(R.layout.help_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); holder.name.setTypeface(Data.regularFont(getApplicationContext()));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderHelp) convertView.getTag();
			}
			
			holder.id = position;
			
			holder.name.setText(helpItems.get(position).name);
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderHelp) v.getTag();
					
					switch(helpItems.get(holder.id).id){
						case MAIL_US:
							openMailIntentToSupport();
							FlurryEventLogger.mailToSupportPressed(Data.userData.accessToken);
							break;
							
						case CALL_US:
							openCallIntent("+919023121121");
							FlurryEventLogger.callToSupportPressed(Data.userData.accessToken);
							break;
							
						default:
							getHelpAsync(HelpActivity.this, helpItems.get(holder.id));
							FlurryEventLogger.particularHelpOpened(helpItems.get(holder.id).name, Data.userData.accessToken);
							
					}
				}
			});
			
			
			return convertView;
		}

	}
	
	
	
	public void openMailIntentToSupport(){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@jugnoo.in" });
		email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Support");
		email.putExtra(Intent.EXTRA_TEXT, "");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client:"));
	}
	
	public void openCallIntent(String phoneNumber){
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
	}
	
	
	/**
	 * ASync for get rides from server
	 */
	public void getHelpAsync(final Activity activity, final HelpItem helpItem) {
		if(fetchHelpDataClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				
				helpExpandedRl.setVisibility(View.VISIBLE);
				progressBarHelp.setVisibility(View.VISIBLE);
				textViewInfoDisplay.setVisibility(View.GONE);
				helpWebview.setVisibility(View.GONE);
				loadHTMLContent("");
				
				Log.e("helpItem", "="+helpItem);
				
				RequestParams params = new RequestParams();
				params.put("section", ""+helpItem.id.getOrdinal());
				
				fetchHelpDataClient = Data.getClient();
				fetchHelpDataClient.post(Data.SERVER_URL + "/get_information", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarHelp.setVisibility(View.GONE);
								openHelpData(helpItem, "Some error occured. Tap to retry.", true);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.i("Server response faq ", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											openHelpData(helpItem, "Some error occured. Tap to retry.", true);
										}
									}
									else{
										String data = jObj.getString("data");
										openHelpData(helpItem, data, false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									openHelpData(helpItem, "Some error occured. Tap to retry.", true);
								}
								progressBarHelp.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
								fetchHelpDataClient = null;
							}
						});
			}
			else {
				openHelpData(helpItem, "No internet connection. Tap to retry.", true);
			}
		}
	}
	
	
	public void performBackPressed(){
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(helpExpandedRl.getVisibility() == View.VISIBLE){
			helpExpandedRl.setVisibility(View.GONE);
			title.setText("Help");
		}
		else{
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        ASSL.closeActivity(relative);
        System.gc();
	}

}

