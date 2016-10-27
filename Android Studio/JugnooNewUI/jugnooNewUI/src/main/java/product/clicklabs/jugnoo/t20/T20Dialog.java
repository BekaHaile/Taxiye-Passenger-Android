package product.clicklabs.jugnoo.t20;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Selection;
import product.clicklabs.jugnoo.t20.models.T20DataType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 3/4/16.
 */
public class T20Dialog {

	private final String TAG = T20Dialog.class.getSimpleName();
	private Activity activity;
	private T20DialogCallback callback;
	private String engagementId;
	private PassengerScreenMode passengerScreenMode;
	private Schedule schedule;
	private Dialog dialog;
	private ImageView imageViewTeam1Check, imageViewTeam2Check;

	public T20Dialog(Activity activity, String engagementId, PassengerScreenMode passengerScreenMode, Schedule schedule,
					 T20DialogCallback callback) {
		this.activity = activity;
		this.engagementId = engagementId;
		this.passengerScreenMode = passengerScreenMode;
		this.schedule = schedule;
		this.callback = callback;
	}

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_t20);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			ImageView imageViewDialogTop = (ImageView) dialog.findViewById(R.id.imageViewDialogTop);

			LinearLayout linearLayoutBeforeRide = (LinearLayout) dialog.findViewById(R.id.linearLayoutBeforeRide);
			TextView textViewPlayPredictWin = (TextView) dialog.findViewById(R.id.textViewPlayPredictWin);
			textViewPlayPredictWin.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewTeams = (TextView) dialog.findViewById(R.id.textViewTeams);
			textViewTeams.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textViewConditions = (TextView) dialog.findViewById(R.id.textViewConditions);
			textViewConditions.setTypeface(Fonts.mavenLight(activity));

			if(Data.userData != null && !"".equalsIgnoreCase(Data.userData.getT20WCInfoText())){
				textViewConditions.setText(Data.userData.getT20WCInfoText());
			}

			LinearLayout linearLayoutAfterRide = (LinearLayout) dialog.findViewById(R.id.linearLayoutAfterRide);
			TextView textViewWhoDoYou = (TextView) dialog.findViewById(R.id.textViewWhoDoYou);
			textViewWhoDoYou.setTypeface(Fonts.mavenRegular(activity));
			LinearLayout linearLayoutTeam1 = (LinearLayout) dialog.findViewById(R.id.linearLayoutTeam1);
			imageViewTeam1Check = (ImageView) dialog.findViewById(R.id.imageViewTeam1Check);
			TextView textViewTeam1 = (TextView) dialog.findViewById(R.id.textViewTeam1);
			textViewTeam1.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			LinearLayout linearLayoutTeam2 = (LinearLayout) dialog.findViewById(R.id.linearLayoutTeam2);
			imageViewTeam2Check = (ImageView) dialog.findViewById(R.id.imageViewTeam2Check);
			TextView textViewTeam2 = (TextView) dialog.findViewById(R.id.textViewTeam2);
			textViewTeam2.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			Button buttonSubmit = (Button) dialog.findViewById(R.id.buttonSubmit);
			buttonSubmit.setTypeface(Fonts.mavenRegular(activity));

			ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);

			View.OnClickListener onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch(v.getId()){

						case R.id.imageViewClose:
							dialog.dismiss();
							if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode){
								Prefs.with(activity).save(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED, 1);
							}
							else if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
								Prefs.with(activity).save(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED, 1);
							}
							callback.onDismiss();
							break;

						case R.id.linearLayoutTeam1:
							setSelectedTeamId(schedule.getTeam1Id());
							break;

						case R.id.linearLayoutTeam2:
							setSelectedTeamId(schedule.getTeam2Id());
							break;

						case R.id.buttonSubmit:
							if(schedule.getSelectedTeamId().equals(-1)){
								Utils.showToast(activity, activity.getResources().getString(R.string.team_not_selected_error));
							} else{
								submitSelectedTeam();
							}
							break;

					}
				}
			};


			imageViewClose.setOnClickListener(onClickListener);
			linearLayoutTeam1.setOnClickListener(onClickListener);
			linearLayoutTeam2.setOnClickListener(onClickListener);
			buttonSubmit.setOnClickListener(onClickListener);


			if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode){
				imageViewDialogTop.setImageResource(R.drawable.popup_t20_before_ride);
				linearLayoutBeforeRide.setVisibility(View.VISIBLE);
				linearLayoutAfterRide.setVisibility(View.GONE);

				textViewTeams.setText(String.format(activity.getResources().getString(R.string.team_vs_team_format),
						schedule.getTeam1().getShortName(), schedule.getTeam2().getShortName()));

				dialog.show();
			}
			else if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
				imageViewDialogTop.setImageResource(R.drawable.popup_t20_after_ride);
				linearLayoutBeforeRide.setVisibility(View.GONE);
				linearLayoutAfterRide.setVisibility(View.VISIBLE);

				textViewTeam1.setText(schedule.getTeam1().getName());
				textViewTeam2.setText(schedule.getTeam2().getName());
				schedule.setSelectedTeamId(-1);

				imageViewTeam1Check.setImageResource(R.drawable.check_box_unchecked);
				imageViewTeam2Check.setImageResource(R.drawable.check_box_unchecked);

				dialog.show();
			} else{
				callback.notShown();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

	private void setSelectedTeamId(Integer selectedTeamId){
		schedule.setSelectedTeamId(selectedTeamId);
		if(schedule.getSelectedTeamId().equals(schedule.getTeam1Id())){
			imageViewTeam1Check.setImageResource(R.drawable.check_box_checked);
			imageViewTeam2Check.setImageResource(R.drawable.check_box_unchecked);
		} else if(schedule.getSelectedTeamId().equals(schedule.getTeam2Id())){
			imageViewTeam1Check.setImageResource(R.drawable.check_box_unchecked);
			imageViewTeam2Check.setImageResource(R.drawable.check_box_checked);
		}
	}


	private void submitSelectedTeam() {
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
			params.put(Constants.KEY_SCHEDULE_ID, String.valueOf(schedule.getScheduleId()));
			params.put(Constants.KEY_TEAM_ID, String.valueOf(schedule.getSelectedTeamId()));

			RestClient.getApiServices().insertUserT20Prediction(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "submitT20TeamSelection jsonString=>" + jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.getInt(Constants.KEY_FLAG);
						String message = JSONParser.getServerMessage(jObj);
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

							List<Selection> selections = Database2.getInstance(activity).getT20DataItems(T20DataType.SELECTION.getOrdinal());
							selections.add(new Selection(schedule.getScheduleId(), schedule.getSelectedTeamId()));
							Database2.getInstance(activity).insertUpdateT20Data(T20DataType.SELECTION.getOrdinal(), selections);

							dialog.dismiss();
							if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode){
								Prefs.with(activity).save(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED, 1);
							}
							else if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
								Prefs.with(activity).save(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED, 1);
							}
							DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									callback.onDismiss();
								}
							});
							FlurryEventLogger.event(FlurryEventNames.SUPPORT_ISSUE_FEEDBACK_SUBMITTED);
						} else {
							DialogPopup.alertPopup(activity, "", message);
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						retryDialog(DialogErrorType.NO_NET);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "submitT20TeamSelection error=>" + error);
					DialogPopup.dismissLoadingDialog();
					retryDialog(DialogErrorType.NO_NET);
				}
			});
		} else {
			retryDialog(DialogErrorType.NO_NET);
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {

					}
				});
	}


	public interface T20DialogCallback{
		void onDismiss();
		void notShown();
	}

}
