package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SendMoneyResponse;

/**
 * Created by shankar on 12/27/16.
 */

public class PaySDKUtils {

	public static final int REQUEST_CODE_SEND_MONEY = 1011;

	public void openSendMoneyPage(Activity activity, SendMoneyResponse.TxnDetails txnDetails) {
		Bundle bundle = new Bundle();
		bundle.putString("mid", txnDetails.getMid()); // YBL0000000000123
		bundle.putString("merchantKey", txnDetails.getMkey());// b0222ce704ebc0c1f4dc24360751f9f6
		bundle.putString("merchantTxnID", Integer.toString(txnDetails.getOrderId())); // 11
		bundle.putString("transactionDesc", txnDetails.getTransactionDescription()); // SEND MONEY REQUEST
		bundle.putString("currency", txnDetails.getCurrency()); //INR
		bundle.putString("appName", "com.jugnoo.pay"); // JUGNOO_PAY
		bundle.putString("paymentType", txnDetails.getPaymentType()); // P2P
		bundle.putString("transactionType", txnDetails.getTransactionType()); // PAY
		bundle.putString("merchantCatCode", txnDetails.getMcc());  // 4814
		bundle.putString("amount", Integer.toString(txnDetails.getAmount())); // 20.00
		bundle.putString("payeeMobileNo", txnDetails.getPayee_phone_no());

		bundle.putString("payeePayAddress", txnDetails.getPayee_vpa());

		bundle.putString("payerMobileNo", txnDetails.getPayer_phone_no());
		bundle.putString("payerPaymentAddress", txnDetails.getPayer_vpa());

		// new keys for new yes bank SDK - added on 21-11-2016
		bundle.putString("payeeAccntNo", txnDetails.getPayeeAccntNo());
		bundle.putString("payeeIFSC", txnDetails.getPayeeIFSC());
		bundle.putString("payeeAadhaarNo", txnDetails.getPayeeAadhaarNo());
		bundle.putString("expiryTime", txnDetails.getExpiryTime());
		bundle.putString("payerAccntNo", txnDetails.getPayerAccntNo());
		bundle.putString("payerIFSC", txnDetails.getPayerIFSC());
		bundle.putString("payerAadhaarNo", txnDetails.getPayerAadhaarNo());

		bundle.putString("subMerchantID", txnDetails.getSubMerchantID());
		bundle.putString("whitelistedAccnts", txnDetails.getWhitelistedAccnts());
		bundle.putString("payerMMID", txnDetails.getPayerMMID());
		bundle.putString("payeeMMID", txnDetails.getPayeeMMID());
		bundle.putString("refurl", txnDetails.getRefurl());
		//-----------------------


//		Intent intent = new Intent(activity, PayActivity.class);
//		intent.putExtras(bundle);
//		activity.startActivityForResult(intent, REQUEST_CODE_SEND_MONEY);
	}


	public MessageRequest parseSendMoneyData(Intent data){
		Bundle bundle = data.getExtras();
		String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
		String orderNo = bundle.getString("orderNo");
		String txnAmount = bundle.getString("txnAmount");
		String tranAuthdate = bundle.getString("tranAuthdate");
		String statusCode = bundle.getString("status");
		String statusDesc = bundle.getString("statusDesc");
		String responsecode = bundle.getString("responsecode");
		String approvalCode = bundle.getString("approvalCode");
		String payerVA = bundle.getString("payerVA");
		String npciTxnId = bundle.getString("npciTxnId");
		String refId = bundle.getString("refId");

		// new parameters for new yes bank SDK
		String payerAccountNo = bundle.getString("payerAccountNo");
		String payerIfsc = bundle.getString("payerIfsc");
		String payerAccName = bundle.getString("payerAccName");
		//------------------------

		String add1 = bundle.getString("add1");
		String add2 = bundle.getString("add2");
		String add3 = bundle.getString("add3");
		String add4 = bundle.getString("add4");
		String add5 = bundle.getString("add5");
		String add6 = bundle.getString("add6");
		String add7 = bundle.getString("add7");
		String add8 = bundle.getString("add8");
		String add9 = bundle.getString("add9");
		String add10 = bundle.getString("add10");


		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setTxnAmount(txnAmount);
		messageRequest.setApprovalCode(approvalCode);
		messageRequest.setNpciTxnId(npciTxnId);
		messageRequest.setOrderNo(orderNo);
		messageRequest.setPayerVA(payerVA);
		messageRequest.setRefId(refId);
		messageRequest.setResponsecode(responsecode);
		messageRequest.setTranAuthdate(tranAuthdate);
		messageRequest.setPgMeTrnRefNo(pgMeTrnRefNo);
		messageRequest.setStatus(statusCode);
		messageRequest.setStatusDesc(statusDesc);

		// new code - added at 21-11-2016
		messageRequest.setPayerAccountNo(payerAccountNo);
		messageRequest.setPayerIfsc(payerIfsc);
		messageRequest.setPayerAccName(payerAccName);
		//--------------
		return messageRequest;
	}

}
