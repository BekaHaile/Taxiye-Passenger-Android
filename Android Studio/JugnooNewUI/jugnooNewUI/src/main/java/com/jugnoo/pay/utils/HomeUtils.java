package com.jugnoo.pay.utils;

import android.util.Pair;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.wallet.models.TransactionInfo;

/**
 * Created by shankar on 12/8/16.
 */

public class HomeUtils {

	public Pair<Integer, Integer> getTransactionTypeStringColor(TransactionInfo transactionInfo){
		int text = -1;
		int color = -1;
		int statusInt = transactionInfo.getStatus();
		if (statusInt == 1) {
			if(transactionInfo.transactionType == 4){
				text = R.string.received;
			}else {
				text = R.string.completed;
			}
			color = R.color.green_rupee;
		} else if (statusInt == 3) {
			text = R.string.cancelled;
			color = R.color.red_status;
		} else if (statusInt == 4) {
			text = R.string.declined;
			color = R.color.red_status;
		} else if (statusInt == 2) {
			text = R.string.failed;
			color = R.color.red_status;
		}
		return new Pair<Integer, Integer>(text, color);
	}

	public int getPayStatusString(TransactionInfo transactionInfo){
		int text = -1;
		if (transactionInfo.transactionType == 1) {
			text = R.string.payment_to;
		} else if (transactionInfo.transactionType == 2) {
			text = R.string.requested_from;
		} else if (transactionInfo.transactionType == 3) {
			text = R.string.requested_by;
		} else if (transactionInfo.transactionType == 4) {
			text = R.string.payment_from;
		}
		return text;
	}

}
