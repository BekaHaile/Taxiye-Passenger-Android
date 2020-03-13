package product.clicklabs.jugnoo.promotion.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/2/16.
 */
public class ReferDriverDialog {

	private Activity activity;
	private Dialog dialog;

	private EditText editTextName, editTextPhone;
	private ScrollView scrollView;
	private LinearLayout linearLayoutMain;
	private TextView textViewScroll;
	private Button buttonRefer;

	public ReferDriverDialog(Activity activity) {
		this.activity = activity;
	}

	public Dialog show(){
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_BottomInBottomOut;
			dialog.setContentView(R.layout.dialog_refer_driver);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
			textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));

			((TextView) dialog.findViewById(R.id.textViewCameAcross)).setTypeface(Fonts.mavenRegular(activity));
			((TextView) dialog.findViewById(R.id.textViewCameAcross)).setText(activity.getString(R.string.came_across_auto_driver, activity.getString(R.string.app_name)));

			editTextName = (EditText) dialog.findViewById(R.id.editTextName); editTextName.setTypeface(Fonts.mavenRegular(activity));
			editTextPhone = (EditText) dialog.findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Fonts.mavenRegular(activity));

			scrollView = (ScrollView) dialog.findViewById(R.id.scrollView);
			linearLayoutMain = (LinearLayout) dialog.findViewById(R.id.linearLayoutMain);
			textViewScroll = (TextView) dialog.findViewById(R.id.textViewScroll);

			buttonRefer = (Button) dialog.findViewById(R.id.buttonRefer); buttonRefer.setTypeface(Fonts.mavenRegular(activity));
			buttonRefer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.hideSoftKeyboard(activity, editTextName);
					if ((!editTextName.getText().toString().isEmpty()) && (!editTextPhone.getText().toString().isEmpty())) {
						if (Utils.validPhoneNumber(editTextPhone.getText().toString())) {
							referDriver();
						} else {
							editTextPhone.requestFocus();
							editTextPhone.setError(activity.getString(R.string.invalid_number));
							editTextName.setError(null);
						}
					} else {
						if (editTextName.getText().toString().isEmpty()) {
							editTextName.requestFocus();
							editTextName.setError(activity.getString(R.string.name_is_required));
							editTextPhone.setError(null);
						} else if (editTextPhone.getText().toString().isEmpty()) {
							editTextPhone.requestFocus();
							editTextPhone.setError(activity.getString(R.string.enter_contact_number));
							editTextName.setError(null);
						} else {
							editTextName.setError(activity.getString(R.string.name_is_required));
							editTextPhone.setError(null);
						}

					}
				}
			});
			editTextPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					buttonRefer.performClick();
					return true;
				}
			});

			ImageView imageViewBack = (ImageView) dialog.findViewById(R.id.imageViewBack);
			imageViewBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissDialog();
				}
			});

			KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
					new KeyboardLayoutListener.KeyBoardStateHandler() {
						@Override
						public void keyboardOpened() {
							scrollView.scrollTo(0, buttonRefer.getBottom());
						}

						@Override
						public void keyBoardClosed() {

						}
					});
			keyboardLayoutListener.setResizeTextView(false);
			linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

			ImageView ivC2DReferralImage = dialog.findViewById(R.id.ivC2DReferralImage);
			TextView tvC2DReferralMessage = dialog.findViewById(R.id.tvC2DReferralMessage);
			tvC2DReferralMessage.setTypeface(Fonts.mavenMedium(activity));


			String image = Prefs.with(activity).getString(Constants.KEY_C_2_D_REFERRAL_IMAGE, "");
			String info = Prefs.with(activity).getString(Constants.KEY_C_2_D_REFERRAL_INFO, "");

			if(!TextUtils.isEmpty(image)){
				Picasso.with(activity)
						.load(image)
						.placeholder(R.drawable.ic_promotions_driver_refer)
						.error(R.drawable.ic_promotions_driver_refer)
						.into(ivC2DReferralImage);
			}
			if(!TextUtils.isEmpty(info)){
				tvC2DReferralMessage.setText(info);

				String details = Prefs.with(activity).getString(Constants.KEY_C_2_D_REFERRAL_DETAILS, "");
				if(!TextUtils.isEmpty(details)){
					SpannableStringBuilder ssb = new SpannableStringBuilder("\n"+activity.getString(R.string.details));

					ClickableSpan clickableSpan = new ClickableSpan() {
						@Override
						public void onClick(View textView) {
							try {
								boolean html = Utils.DetectHtml.isHtml(details);
								DialogPopup.alertPopupLeftOriented(activity, "",
										details, true, true, html);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					ssb.setSpan(clickableSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					tvC2DReferralMessage.append(ssb);
					tvC2DReferralMessage.setMovementMethod(LinkMovementMethod.getInstance());
				}


			}


			dialog.show();
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			textViewTitle.requestFocus();
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void dismissDialog(){
		try {
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void referDriver(){
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_REFER_DRIVER_NAME, editTextName.getText().toString());
				params.put(Constants.KEY_REFER_DRIVER_PHONE_NO, editTextPhone.getText().toString());
				Log.i("Refer Driver params=", "" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().referDriver(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						DialogPopup.dismissLoadingDialog();
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Refer Driver Response", "" + responseStr);
						if(settleUserDebt.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
							DialogPopup.alertPopupWithListener(activity, "", settleUserDebt.getMessage(), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									dismissDialog();
								}
							});
						}else {
							DialogPopup.alertPopup(activity, "", settleUserDebt.getMessage());
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("Refer Driver error", ""+ error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						referDriver();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}

}