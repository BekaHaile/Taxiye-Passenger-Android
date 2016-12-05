package com.jugnoo.pay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import product.clicklabs.jugnoo.OTPConfirmScreen;
import product.clicklabs.jugnoo.PhoneNoOTPConfirmScreen;


/**
 * Created by socomo20 on 8/12/15.
 */
public class IncomingSmsReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();

					String senderNum = phoneNumber;
					String message = currentMessage.getDisplayMessageBody();

					Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

					if(Prefs.with(context).getString(Constants.SP_OTP_SCREEN_OPEN, "")
							.equalsIgnoreCase(OTPConfirmScreen.class.getName())) {
						Intent otpConfirmScreen = new Intent(context, OTPConfirmScreen.class);
						otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						otpConfirmScreen.putExtra("sender_num", senderNum);
						otpConfirmScreen.putExtra("message", message);
						context.startActivity(otpConfirmScreen);
						break;
					}
					else if(Prefs.with(context).getString(Constants.SP_OTP_SCREEN_OPEN, "")
							.equalsIgnoreCase(PhoneNoOTPConfirmScreen.class.getName())) {
						Intent otpConfirmScreen = new Intent(context, PhoneNoOTPConfirmScreen.class);
						otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						otpConfirmScreen.putExtra("sender_num", senderNum);
						otpConfirmScreen.putExtra("message", message);
						context.startActivity(otpConfirmScreen);
						break;
					}

				} // end for loop
			} // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" +e);

		}
	}
}