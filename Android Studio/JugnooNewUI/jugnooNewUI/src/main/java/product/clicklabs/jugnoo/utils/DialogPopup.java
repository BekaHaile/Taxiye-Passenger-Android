package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;


public class DialogPopup {

	private static boolean ifOtherDialog(Activity activity, String message,
										 final View.OnClickListener positiveListener,
										 final View.OnClickListener negativeListener, boolean cancellable){
		if(message.contains(Data.CHECK_INTERNET_MSG)){
			dialogNoInternet(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View v) {
							if(positiveListener != null){
								positiveListener.onClick(v);
							}
						}

						@Override
						public void neutralClick(View v) {

						}

						@Override
						public void negativeClick(View v) {
							if(negativeListener != null){
								negativeListener.onClick(v);
							}
						}
					}, cancellable);
			return false;
		}
		else if(message.contains(Data.SERVER_NOT_RESOPNDING_MSG)){
			dialogNoInternet(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View v) {
							if(positiveListener != null){
								positiveListener.onClick(v);
							}
						}

						@Override
						public void neutralClick(View v) {

						}

						@Override
						public void negativeClick(View v) {
							if(negativeListener != null){
								negativeListener.onClick(v);
							}
						}
					}, cancellable);
			return false;
		}
		else if(message.contains(Data.SERVER_ERROR_MSG)){
			dialogNoInternet(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View v) {
							if(positiveListener != null){
								positiveListener.onClick(v);
							}
						}

						@Override
						public void neutralClick(View v) {

						}

						@Override
						public void negativeClick(View v) {
							if(negativeListener != null){
								negativeListener.onClick(v);
							}
						}
					}, cancellable);
			return false;
		} else{
			return true;
		}
	}

	public static Dialog dialog;
	public static void alertPopup(Activity activity, String title, String message) {
		try {
			if(ifOtherDialog(activity, message, null, null, false)){
				dismissAlertPopup();
				if("".equalsIgnoreCase(title)){
					title = activity.getResources().getString(R.string.alert);
				}

				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_one_button);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				textHead.setVisibility(View.GONE);

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}

				});

				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopup(Activity activity, String title, String message, boolean showTitle) {
		try {
			if(ifOtherDialog(activity, message, null, null, false)){
				dismissAlertPopup();
				if("".equalsIgnoreCase(title)){
					title = activity.getResources().getString(R.string.alert);
				}

				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_one_button);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				if(showTitle){
					textHead.setVisibility(View.VISIBLE);
				} else{
					textHead.setVisibility(View.GONE);
				}

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}

				});

				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dialogNoInternet(Activity activity, String title, String message,
										final Utils.AlertCallBackWithButtonsInterface alertCallBackWithButtonsInterface){
		dialogNoInternet(activity, title, message, alertCallBackWithButtonsInterface, false);
	}

	public static void dialogNoInternet(Activity activity, String title, String message,
										final Utils.AlertCallBackWithButtonsInterface alertCallBackWithButtonsInterface,
										final boolean cancellable) {
		try {
			dismissAlertPopup();

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_no_internet);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancellable);
			dialog.setCanceledOnTouchOutside(cancellable);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			ImageView btnClose = (ImageView)dialog.findViewById(R.id.close);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if(alertCallBackWithButtonsInterface != null) {
						alertCallBackWithButtonsInterface.positiveClick(view);
					}
				}
			});

			btnClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if(alertCallBackWithButtonsInterface != null) {
						alertCallBackWithButtonsInterface.negativeClick(v);
					}
				}
			});

			frameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(cancellable) {
						dialog.dismiss();
					}
				}
			});

			dialog.findViewById(R.id.innerRl).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dialogNoInternet(Activity activity, DialogErrorType dialogErrorType,
										final Utils.AlertCallBackWithButtonsInterface alertCallBackWithButtonsInterface) {
		String title = activity.getResources().getString(R.string.no_net_title);
		String text = activity.getResources().getString(R.string.no_net_text);
		if(dialogErrorType.getOrdinal() == DialogErrorType.CONNECTION_LOST.getOrdinal()){
			title = activity.getResources().getString(R.string.conn_lost_title);
			text = activity.getResources().getString(R.string.conn_lost_text);
		}
		else if(dialogErrorType.getOrdinal() == DialogErrorType.SERVER_ERROR.getOrdinal()){
			title = activity.getResources().getString(R.string.server_error_title);
			text = activity.getResources().getString(R.string.server_error_text);
		}
		dialogNoInternet(activity, title, text, alertCallBackWithButtonsInterface);
	}

	
	public static void alertPopupHtml(Activity activity, String title, String message) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(Html.fromHtml(message));
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			
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

	
	public static void alertPopupLeftOriented(Activity activity, String title, String message) {
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			textMessage.setGravity(Gravity.LEFT);
			
			textHead.setText(title);
			textMessage.setText(message);
			
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			
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
	
	
	public static void dismissAlertPopup(){
		try{
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		}catch(Exception e){
		}
	}
	
	public static void alertPopupWithListener(Activity activity, String title, String message, String buttonText,
											  final View.OnClickListener onClickListener, boolean newInstance) {
		try {
			if(ifOtherDialog(activity, message, onClickListener, null, false)) {
				dismissAlertPopup();
				if ("".equalsIgnoreCase(title)) {
					title = activity.getResources().getString(R.string.alert);
				}

				Dialog dialogI = null;
				if (newInstance) {
					dialogI = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				} else {
					DialogPopup.dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialogI = DialogPopup.dialog;
				}
				final Dialog dialog = dialogI;
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_one_button);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
				textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
				textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				textHead.setVisibility(View.GONE);

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
				btnOk.setTypeface(Fonts.mavenRegular(activity));

				if (buttonText.length() > 0) {
					btnOk.setText(buttonText);
				}

				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						if (onClickListener != null) {
							onClickListener.onClick(view);
						}
					}

				});

				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopupWithListener(Activity activity, String title, String message, final View.OnClickListener onClickListener) {
		alertPopupWithListener(activity, title, message, "", onClickListener, false);
	}


	
	public static void alertPopupTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext, 
			final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative, final boolean cancelable, final boolean showTitle) {
        alertPopupTwoButtonsWithListeners(activity, title, message, okText, canceltext, listenerPositive, listenerNegative, cancelable, showTitle, null);
	}

    public static void alertPopupTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext,
                                                         final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative,
                                                         final boolean cancelable, final boolean showTitle, DialogInterface.OnCancelListener dialogCancelListener) {
        try {
			if(ifOtherDialog(activity, message, listenerPositive, listenerNegative, cancelable)) {
				dismissAlertPopup();
				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_two_buttons);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(cancelable);
				dialog.setCanceledOnTouchOutside(cancelable);

				if (dialogCancelListener != null) {
					dialog.setOnCancelListener(dialogCancelListener);
				}

				TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
				textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog
						.findViewById(R.id.textMessage);
				textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				if (showTitle) {
					textHead.setVisibility(View.VISIBLE);
				} else {
					textHead.setVisibility(View.GONE);
				}

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
				btnOk.setTypeface(Fonts.mavenRegular(activity));
				if (!"".equalsIgnoreCase(okText)) {
					btnOk.setText(okText);
				}

				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
				btnCancel.setTypeface(Fonts.mavenRegular(activity));
				if (!"".equalsIgnoreCase(canceltext)) {
					btnCancel.setText(canceltext);
				}

				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						listenerPositive.onClick(view);
					}
				});

				btnCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						listenerNegative.onClick(v);
					}
				});


				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});


				dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (cancelable) {
							dismissAlertPopup();
						}
					}
				});

				dialog.show();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static Dialog uploadContactsTwoButtonsWithListeners(Activity activity, String title, String message, String okText, String canceltext,
								final boolean cancelable, final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative) {
		try {
			dismissAlertPopup();
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_upload_contacts);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog
					.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);



			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					listenerPositive.onClick(view);
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listenerNegative.onClick(v);
                }
            });


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (cancelable) {
                        dismissAlertPopup();
                    }
                }
            });

			dialog.show();
            return dialog;
		} catch (Exception e) {
			e.printStackTrace();
            return null;
		}
	}
	
	
	
	public static void dialogBanner(Activity activity, String message) {
		dialogBannerWithCancelListener(activity, message, null, 5000);
	}

	public static void dialogBannerWithCancelListener(Activity activity, String message, final View.OnClickListener onClickListener, long timeToDismiss){
		try {
			dismissAlertPopup();

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_banner);

			LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, linearLayout, 1134, 720, false);

			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			TextView textViewBanner = (TextView) dialog.findViewById(R.id.textViewBanner); textViewBanner.setTypeface(Fonts.mavenLight(activity));
			textViewBanner.setText(message);

			linearLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if(onClickListener != null) {
						onClickListener.onClick(v);
					}
				}
			});

			dialog.show();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogPopup.dismissAlertPopup();
				}
			}, timeToDismiss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






    public static ProgressDialog progressDialog;

    /**
     * @param context
     * @param message
     */
    public static void showLoadingDialog(Context context, String message) {
        try {
            if (isDialogShowing()) {
                dismissLoadingDialog();
            }


            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.isFinishing()) {
                    return;
                }
            }

            progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            progressDialog.show();
            WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.dialog_loading_box);
            RelativeLayout frameLayout = (RelativeLayout) progressDialog.findViewById(R.id.dlgProgress);
            new ASSL((Activity) context, frameLayout, 1134, 720, false);
			final ImageView animImageView = (ImageView) progressDialog.findViewById(R.id.ivAnimation);
			animImageView.setBackgroundResource(R.drawable.anim);
			animImageView.post(new Runnable() {
				@Override
				public void run() {
					AnimationDrawable frameAnimation =
							(AnimationDrawable) animImageView.getBackground();
					frameAnimation.start();
				}
			});
        } catch(Exception e){
            e.printStackTrace();
        }
    }


	/**
	 * @param context
	 * @param message
	 */
	public static void showLoadingDialogDownwards(Context context, String message) {
		try {
			if (isDialogShowing()) {
				dismissLoadingDialog();
			}


			if (context instanceof Activity) {
				Activity activity = (Activity) context;
				if (activity.isFinishing()) {
					return;
				}
			}

			progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			progressDialog.show();
			WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;

			progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			progressDialog.setCancelable(false);
			progressDialog.setContentView(R.layout.dialog_loading_box_downwards);
			RelativeLayout frameLayout = (RelativeLayout) progressDialog.findViewById(R.id.dlgProgress);
			new ASSL((Activity) context, frameLayout, 1134, 720, false);
			final ImageView animImageView = (ImageView) progressDialog.findViewById(R.id.ivAnimation);
			animImageView.setBackgroundResource(R.drawable.anim);
			animImageView.post(new Runnable() {
				@Override
				public void run() {
					AnimationDrawable frameAnimation =
							(AnimationDrawable) animImageView.getBackground();
					frameAnimation.start();
				}
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
    public static boolean isDialogShowing() {
        try {
            if (progressDialog == null) {
                return false;
            } else {
                return progressDialog.isShowing();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Dismisses above loading dialog
     */
    public static void dismissLoadingDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            Log.e("e", "=" + e);
        }
    }


	public static ProgressDialog showLoadingDialogNewInstance(Context context, String message) {
		try {
			if (context instanceof Activity) {
				Activity activity = (Activity) context;
				if (activity.isFinishing()) {
					return null;
				}
			}

			ProgressDialog progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			progressDialog.show();
			WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			progressDialog.setCancelable(false);
			progressDialog.setContentView(R.layout.dialog_loading_box);
			RelativeLayout frameLayout = (RelativeLayout) progressDialog.findViewById(R.id.dlgProgress);
			new ASSL((Activity) context, frameLayout, 1134, 720, false);
			final ImageView animImageView = (ImageView) progressDialog.findViewById(R.id.ivAnimation);
			animImageView.setBackgroundResource(R.drawable.anim);
			animImageView.post(new Runnable() {
				@Override
				public void run() {
					AnimationDrawable frameAnimation =
							(AnimationDrawable) animImageView.getBackground();
					frameAnimation.start();
				}
			});

			return progressDialog;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static AlertDialog googlePlayAlertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public static void showGooglePlayErrorAlert(final Activity mContext){
		try{
			if(googlePlayAlertDialog != null && googlePlayAlertDialog.isShowing()){
				googlePlayAlertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Google Play Services Error");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Google Play services not found or outdated. Please install Google Play Services?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	dialog.dismiss();
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
                            ComponentName componentName = intent.resolveActivity(mContext.getPackageManager());
                            if(componentName != null){
                                mContext.startActivity(intent);
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
		            }
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	mContext.finish();
		            }
		        });
		 
		        googlePlayAlertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        googlePlayAlertDialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static AlertDialog locationAlertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public static void showLocationSettingsAlert(final Context mContext){
		try{

			if(!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					&&
					!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(locationAlertDialog != null && locationAlertDialog.isShowing()){
				locationAlertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Location Settings");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Location is not enabled. Do you want to enable it from settings menu?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		            	mContext.startActivity(intent);
		            	dialog.dismiss();
		            }
		        });
		 
		        locationAlertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        locationAlertDialog.show();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


    public static void showLocationSettingsAlertGPS(final Context mContext){
        try{

            if(!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
                if(locationAlertDialog != null && locationAlertDialog.isShowing()){
                    locationAlertDialog.dismiss();
                }
                AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);

                // Setting Dialog Title
                alertDialogPrepare.setTitle("Location Settings");
                alertDialogPrepare.setCancelable(false);

                // Setting Dialog Message
                alertDialogPrepare.setMessage("GPS Location is not enabled. Do you want to enable it from settings menu?");

                // On pressing Settings button
                alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });

                locationAlertDialog = alertDialogPrepare.create();

                // Showing Alert Message
                locationAlertDialog.show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
