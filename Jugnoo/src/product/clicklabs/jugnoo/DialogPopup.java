package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.LanguagePrefrencesActivity.ViewHolderLanguage;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DialogPopup {

	public DialogPopup(){
		
	}
	
	
	void alertPopup(Activity activity, String title, String message) {
		try {
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			if(Data.SERVER_ERROR_MSG.equalsIgnoreCase(message)){
				message = activity.getResources().getString(R.string.server_error);
			}
			else if(Data.SERVER_NOT_RESOPNDING_MSG.equalsIgnoreCase(message)){
				message = activity.getResources().getString(R.string.server_not_responding);
			}
			else if(Data.CHECK_INTERNET_MSG.equalsIgnoreCase(message)){
				message = activity.getResources().getString(R.string.check_internet_message);
			}
			
			
			
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_message_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static ProgressDialog progressDialog;
	
	/**
	 * Displays custom loading dialog
	 * @param c application context
	 * @param msg string message to show in dialog
	 */
	public static void showLoadingDialog(Context context, String message) {
		if(isDialogShowing()){
			dismissLoadingDialog();
		}
		progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		progressDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
		progressDialog.show();
		WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
		layoutParams.dimAmount = 0.6f;
		progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		progressDialog.setCancelable(false);
		progressDialog.setContentView(R.layout.loading_box);
		
		FrameLayout frameLayout = (FrameLayout) progressDialog.findViewById(R.id.rv);
		new ASSL((Activity)context, frameLayout, 1184, 720, true);
		
		TextView messageText = (TextView) progressDialog.findViewById(R.id.textView1); messageText.setTypeface(Data.regularFont(context));
		messageText.setText(message); 
		messageText.setMinimumWidth((int)(250.0 * ASSL.Xscale()));
		
		
	}
	
	
	
	public static boolean isDialogShowing(){
		try{
			if(progressDialog == null){
				return false;
			}
			else{
				return progressDialog.isShowing();
			}
		} catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Dismisses above loading dialog
	 */
	public static void dismissLoadingDialog() {
		try{
		if(progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//TODO driver type list popup
	public void driverListPopup(final Activity activity){

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.driver_type_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity));
			
			ListView driverTypeList = (ListView) dialog.findViewById(R.id.driverTypeList);
			final DriverTypeListAdapter driverTypeListAdapter = new DriverTypeListAdapter(activity);
			driverTypeList.setAdapter(driverTypeListAdapter);
			
			Button uncheckAll = (Button) dialog.findViewById(R.id.uncheckAll); uncheckAll.setTypeface(Data.regularFont(activity));
			Button checkAll = (Button) dialog.findViewById(R.id.checkAll); checkAll.setTypeface(Data.regularFont(activity));
			
			if(Data.driverTypes == null){
				Data.driverTypes = new ArrayList<DriverType>();
			}
			Data.driverTypes.clear();
			Data.driverTypes.add(new DriverType("1", "Auto"));
			Data.driverTypes.add(new DriverType("2", "Taxi"));
			Data.driverTypes.add(new DriverType("1", "Auto"));
			Data.driverTypes.add(new DriverType("2", "Taxi"));
			Data.driverTypes.add(new DriverType("1", "Auto"));
			Data.driverTypes.add(new DriverType("2", "Taxi"));
			Data.driverTypes.add(new DriverType("1", "Auto"));
			Data.driverTypes.add(new DriverType("2", "Taxi"));
			
			
			uncheckAll.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					for(int i=0; i<Data.driverTypes.size(); i++){
						Data.driverTypes.get(i).selected = false;
					}
					driverTypeListAdapter.notifyDataSetChanged();
				}
				
			});
			
			
			checkAll.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					for(int i=0; i<Data.driverTypes.size(); i++){
						Data.driverTypes.get(i).selected = true;
					}
					driverTypeListAdapter.notifyDataSetChanged();
				}
				
			});
			
			
			Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.regularFont(activity));
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(activity));
			
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});
			
			
			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	class ViewHolderDriverType {
		TextView driverTypeName;
		ImageView tick;
		LinearLayout relative;
		int id;
	}

	class DriverTypeListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverType holder;
		Context context;

		public DriverTypeListAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			return Data.driverTypes.size();
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
				
				holder = new ViewHolderDriverType();
				convertView = mInflater.inflate(R.layout.driver_type_list_item, null);
				
				holder.driverTypeName = (TextView) convertView.findViewById(R.id.driverTypeName); holder.driverTypeName.setTypeface(Data.regularFont(context));
				holder.tick = (ImageView) convertView.findViewById(R.id.tick);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(580, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolderDriverType) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.driverTypeName.setText(Data.driverTypes.get(position).driverTypeName);
			
			if(Data.driverTypes.get(position).selected){
				holder.tick.setImageResource(R.drawable.check_on);
			}
			else{
				holder.tick.setImageResource(R.drawable.check_off);
			}
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderDriverType) v.getTag();
					
					if(Data.driverTypes.get(holder.id).selected){
						Data.driverTypes.get(holder.id).selected = false;
					}
					else{
						Data.driverTypes.get(holder.id).selected = true;
					}
					
					notifyDataSetChanged();
				}
			});
			
			
			
			return convertView;
		}

	}
	
	
	
	
}
