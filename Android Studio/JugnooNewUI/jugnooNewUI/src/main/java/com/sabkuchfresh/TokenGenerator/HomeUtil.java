package com.sabkuchfresh.TokenGenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;

import com.sabkuchfresh.R;
import com.sabkuchfresh.SplashNewActivity;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.FacebookLoginHelper;
import com.sabkuchfresh.utils.Log;
import com.squareup.picasso.PicassoTools;

import io.branch.referral.Branch;

/**
 * Created by shankar on 4/25/16.
 */
public class HomeUtil {

	public static void checkForAccessTokenChange(Activity activity) {
		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
		if (!"".equalsIgnoreCase(pair.first)) {
			if (Data.userData == null) {
				logoutIntent(activity);
			} else {
				if (!pair.first.equalsIgnoreCase(Data.userData.accessToken)) {
					logoutIntent(activity);
				}
			}
		} else {
			if (Data.userData == null) {

			} else {
				logoutIntent(activity);
			}
		}
	}

	public static void logoutIntent(Activity cont) {
		try {
			FacebookLoginHelper.logoutFacebook();
			Data.userData = null;
			Intent intent = new Intent(cont, SplashNewActivity.class);
			cont.startActivity(intent);
			cont.finish();
			cont.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			Branch.getInstance(cont).logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkIfUserDataNull(Activity activity) {
		Log.e("checkIfUserDataNull", "Data.userData = " + Data.userData);
		if (Data.userData == null) {
			activity.startActivity(new Intent(activity, SplashNewActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		} else {
			return false;
		}
	}


	public static void logoutUser(final Activity cont) {
		try {

			SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
			SharedPreferences.Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			Data.clearDataOnLogout(cont);

			PicassoTools.clearCache(Picasso.with(cont));

			cont.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					FacebookLoginHelper.logoutFacebook();

					AlertDialog.Builder builder = new AlertDialog.Builder(cont);
					builder.setMessage(cont.getResources().getString(R.string.your_login_session_expired)).setTitle(cont.getResources().getString(R.string.alert));
					builder.setCancelable(false);
					builder.setPositiveButton(cont.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								dialog.dismiss();
								Intent intent = new Intent(cont, SplashNewActivity.class);
								intent.putExtra("no_anim", "yes");
								cont.startActivity(intent);
								cont.finish();
								cont.overridePendingTransition(
										R.anim.left_in,
										R.anim.left_out);
							} catch (Exception e) {
								Log.i("excption logout",
										e.toString());
							}
						}
					});

					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			});

			Branch.getInstance(cont).logout();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
