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
	
	
	
	
	
	
	
	
	
}
