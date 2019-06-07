package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;


public class DialogPopup {

	private static boolean ifOtherDialog(Activity activity, String message,
										 final View.OnClickListener positiveListener,
										 final View.OnClickListener negativeListener, boolean cancellable){
		if(message.contains(activity.getString(R.string.connection_lost_desc))){
			dialogNoInternet(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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
					}, cancellable, true);
			return false;
		}
		else if(message.contains(activity.getString(R.string.connection_lost_please_try_again))){
			dialogNoInternet(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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
					}, cancellable, true);
			return false;
		}
		else if(message.contains(activity.getString(R.string.connection_lost_please_try_again))){
			dialogNoInternet(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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
					}, cancellable, true);
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

				RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);

				RelativeLayout relativeLayoutInner = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutInner);
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(LinkMovementMethod.getInstance());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));

				textMessage.setText(message);

				textHead.setVisibility(View.GONE);

				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));

				View.OnClickListener dismissOnClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}

				};
				btnOk.setOnClickListener(dismissOnClickListener);
				frameLayout.setOnClickListener(dismissOnClickListener);
				relativeLayoutInner.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
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
		dialogNoInternet(activity, title, message, alertCallBackWithButtonsInterface, false, true);
	}

	public static void dialogNoInternet(Activity activity, String title, String message,
										final Utils.AlertCallBackWithButtonsInterface alertCallBackWithButtonsInterface,
										final boolean cancellable, final boolean closeOnNegative) {
		try {
			dismissAlertPopup();

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_no_internet);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancellable);
			dialog.setCanceledOnTouchOutside(cancellable);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(LinkMovementMethod.getInstance());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			ImageView btnClose = (ImageView)dialog.findViewById(R.id.close);
		    btnClose.setVisibility(closeOnNegative?View.VISIBLE:View.GONE);
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
					if(closeOnNegative) {
						dialog.dismiss();
					}
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

			dialog.findViewById(R.id.linearLayoutInner).setOnClickListener(new View.OnClickListener() {
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


	public static void dialogNoInternet(Activity activity, DialogErrorType dialogErrorType,
										final Utils.AlertCallBackWithButtonsInterface alertCallBackWithButtonsInterface,boolean closeOnNegative) {
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
		dialogNoInternet(activity, title, text, alertCallBackWithButtonsInterface, false,closeOnNegative);
	}


	public static void alertPopupLeftOriented(Activity activity, String title, String message,
											  boolean leftOriented, boolean applyMinDimens, boolean inHtml) {
		alertPopupLeftOriented(activity, title, message, leftOriented, applyMinDimens, inHtml, false, -1, -1, -1, null);
	}

	public static void alertPopupLeftOriented(Activity activity, String title, String message,
											  boolean leftOriented, boolean applyMinDimens, boolean inHtml,
											  boolean showTitle, int titleTextColorRes, int titleTextSizeSP, int messageTextSizeSP,
											  Typeface typeface){
		try {
			dismissAlertPopup();
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			RelativeLayout relativeLayoutInner = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutInner);

			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenLight(activity));

			textMessage.setMovementMethod(LinkMovementMethod.getInstance());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));


			if(applyMinDimens){
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayoutInner.getLayoutParams();
				int margin = (int) (30.0f * Math.min(ASSL.Xscale(), ASSL.Yscale()));
				params.setMargins(margin, margin, margin, margin);
				params.setMarginStart(margin);
				params.setMarginEnd(margin);
				relativeLayoutInner.setLayoutParams(params);
			}
			if(leftOriented) {
				textMessage.setGravity(Gravity.START);
				LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) textMessage.getLayoutParams();
				layoutParams1.gravity=Gravity.START;
				textMessage.setLayoutParams(layoutParams1);
			}
			textHead.setText(title);
			if(inHtml){
				try {
					textMessage.setText(Utils.trimHTML(Utils.fromHtml(message)));
				} catch (Exception e) {
					e.printStackTrace();
					textMessage.setText(message);
				}
			} else {
				textMessage.setText(message);
			}
			
			
			textHead.setVisibility(showTitle ? View.VISIBLE : View.GONE);
			if(showTitle && titleTextColorRes > 0){
				textHead.setTextColor(ContextCompat.getColor(activity, titleTextColorRes));
			}
			if(showTitle && titleTextSizeSP > 0){
				textHead.setTextSize(titleTextSizeSP);
			}
			if(typeface != null){
				textHead.setTypeface(typeface);
				textMessage.setTypeface(typeface);
			}
			if(messageTextSizeSP > 0){
				textMessage.setTextSize(messageTextSizeSP);
			}
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			frameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}

			});
			relativeLayoutInner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});


			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static CharSequence trim(CharSequence s) {
		if(s.length()==0)
			return "";

		int start = 0;
		int end = s.length();
		while (start < end && Character.isWhitespace(s.charAt(start))) {
			start++;
		}

		while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
			end--;
		}

		return s.subSequence(start, end);
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
											  final View.OnClickListener onClickListener, boolean newInstance){
		alertPopupWithListener(activity, title, message, buttonText, onClickListener, newInstance, false, false);
	}

	public static void alertPopupWithListener(Activity activity, String title, String message, String buttonText,
											  final View.OnClickListener onClickListener, boolean newInstance,
											  boolean showTitle, boolean cancellable) {
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

				RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(cancellable);
				dialog.setCanceledOnTouchOutside(cancellable);


				TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
				textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
				textMessage.setTypeface(Fonts.mavenLight(activity));

				textMessage.setMovementMethod(LinkMovementMethod.getInstance());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

				textHead.setText(title);
				textMessage.setText(message);

				textHead.setVisibility(showTitle ? View.VISIBLE : View.GONE);

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

				if(cancellable){
					frameLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.findViewById(R.id.relativeLayoutInner).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
				}


				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertPopupWithListener(Activity activity, String title, String message, final View.OnClickListener onClickListener) {
		alertPopupWithListener(activity, title, message, "", onClickListener, false);
	}

	public static void alertPopupTwoButtonsWithListeners(Activity activity, String message,
														 final View.OnClickListener listenerPositive) {
		alertPopupTwoButtonsWithListeners(activity, "", message, "", "", listenerPositive, null, true, false, null);
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

				RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
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

				textMessage.setMovementMethod(LinkMovementMethod.getInstance());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

				textHead.setText(title);
				if(message.contains("<br/>") || message.contains("<br>")){
					textMessage.setText(Utils.trimHTML(Utils.fromHtml(message)));
				} else {
					textMessage.setText(message);
				}

				if (showTitle && !TextUtils.isEmpty(title)) {
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
						if(listenerPositive != null) {
							listenerPositive.onClick(view);
						}
					}
				});

				btnCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if(listenerNegative != null) {
							listenerNegative.onClick(v);
						}
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
								final boolean cancelable, final View.OnClickListener listenerPositive, final View.OnClickListener listenerNegative,
															   final DialogInterface.OnDismissListener onDismissListener) {
		try {
			dismissAlertPopup();
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_upload_contacts);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
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

			textMessage.setMovementMethod(LinkMovementMethod.getInstance());
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

			dialog.setOnDismissListener(onDismissListener);

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

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
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
					//DialogPopup.dismissAlertPopup();
					try {
						dialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
            final View loader = progressDialog.findViewById(R.id.ivAnimation);
            if(loader instanceof ImageView){
				loader.setBackgroundResource(R.drawable.loader_new_frame_anim);
				loader.post(new Runnable() {
					@Override
					public void run() {
						AnimationDrawable frameAnimation =
								(AnimationDrawable) loader.getBackground();
						frameAnimation.start();
					}
				});
			}
			/*GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(animImageView);
			Glide.with(context)
					.load(R.drawable.jugnoo_logo)
					.placeholder(R.drawable.jugnoo_logo)
					.into(imageViewTarget);*/
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
			final View loader = progressDialog.findViewById(R.id.ivAnimation);
			if(loader instanceof ImageView){
				loader.setBackgroundResource(R.drawable.loader_new_frame_anim);
				loader.post(new Runnable() {
					@Override
					public void run() {
						AnimationDrawable frameAnimation =
								(AnimationDrawable) loader.getBackground();
						frameAnimation.start();
					}
				});
			}

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
		        alertDialogPrepare.setTitle(R.string.google_play_services_error);
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage(R.string.google_play_services_outdated_please_update);
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
		        alertDialogPrepare.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
		        alertDialogPrepare.setTitle(R.string.location_settings);
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage(R.string.location_not_enabled_enable_from_settings);
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
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
                alertDialogPrepare.setTitle(R.string.location_settings);
                alertDialogPrepare.setCancelable(false);

                // Setting Dialog Message
                alertDialogPrepare.setMessage(R.string.gps_not_enabled_enable_from_settings);

                // On pressing Settings button
                alertDialogPrepare.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
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
