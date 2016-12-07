package com.jugnoo.pay.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jugnoo.pay.activities.AddPaymentAddressActivity;
import com.jugnoo.pay.activities.SelectContactActivity;
import com.jugnoo.pay.activities.SendMoneyActivity;
import com.jugnoo.pay.adapters.ContactsListAdapter;
import com.jugnoo.pay.adapters.PaymentAddressAdapter;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.FetchPaymentAddressResponse;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 05/12/16.
 */

public class PaymentFragment extends Fragment {

    private View rootView;
    private RecyclerView rvPaymentAddress;
    private LinearLayout llPlaceHolder;
    private PaymentAddressAdapter paymentAddressAdapter;
    private ArrayList<FetchPaymentAddressResponse.VpaList> fetchList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_address, container, false);

        rvPaymentAddress = (RecyclerView) rootView.findViewById(R.id.rvPaymentAddress);
        llPlaceHolder = (LinearLayout) rootView.findViewById(R.id.llPlaceHolder);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvPaymentAddress.setLayoutManager(mLayoutManager);
        rvPaymentAddress.setItemAnimator(new DefaultItemAnimator());

        paymentAddressAdapter = new PaymentAddressAdapter(getActivity(), fetchList, new PaymentAddressAdapter.Callback() {
            @Override
            public void recyclerViewListClicked(int position) {
                SelectUser data = new SelectUser();
                data.setName(fetchList.get(position).getName());
                data.setPhone(fetchList.get(position).getVpa());
                data.setThumb("");
                data.setAmount("");
                data.setOrderId("0");
                if(new Validator().validateEmail(fetchList.get(position).getVpa()))
                {
                    SelectUser newData = new SelectUser();
                    newData.setName(fetchList.get(position).getName());
                    newData.setPhone(fetchList.get(position).getVpa());
                    newData.setThumb("");
                    newData.setAmount("");
                    newData.setOrderId("0");
                    Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                    intent.putExtra(AppConstant.REQUEST_STATUS, ((SelectContactActivity)getActivity()).isRequestStatus());
                    Bundle bun =new Bundle();
                    bun.putParcelable(AppConstant.CONTACT_DATA, newData);
                    intent.putExtras( bun);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    getActivity().finish();
                }
                else
                {
                    ((SelectContactActivity)getActivity()).getSearchET().requestFocus();
                    ((SelectContactActivity)getActivity()).getSearchET().setHovered(true);
                    ((SelectContactActivity)getActivity()).getSearchET().setError("Please fill alteast 10 Digits mobile number.");
                }
            }

            @Override
            public void onDelete(String vpa) {
                apiDeletePaymentAddress(vpa);
            }
        });
        rvPaymentAddress.setAdapter(paymentAddressAdapter);

        (((SelectContactActivity)getActivity()).getIvToolbarAddVPA()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPaymentAddressActivity.class));
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });




        return rootView;
    }

    public PaymentAddressAdapter getPaymentAddressAdapter() {
        return paymentAddressAdapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        (((SelectContactActivity)getActivity()).getSearchET()).setText("");
        apiFetchPaymentAddress();
    }

    public void apiFetchPaymentAddress() {
        try {
            if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
                CallProgressWheel.showLoadingDialog(getActivity(), "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                RestClient.getPayApiService().fetchPaymentAddress(params, new Callback<FetchPaymentAddressResponse>() {
                    @Override
                    public void success(FetchPaymentAddressResponse fetchPaymentAddressResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == fetchPaymentAddressResponse.getFlag()){
                                fetchList.clear();
                                fetchList.addAll(fetchPaymentAddressResponse.getVpaList());
                                if(fetchList.size() > 0){
                                    llPlaceHolder.setVisibility(View.GONE);
                                } else{
                                    llPlaceHolder.setVisibility(View.VISIBLE);
                                }
                                paymentAddressAdapter.setList(fetchList);
                            } else {
                                DialogPopup.alertPopup(getActivity(), "", fetchPaymentAddressResponse.getMessage());
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            retryDialogFetchPayData(DialogErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogFetchPayData(DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialogFetchPayData(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private void retryDialogFetchPayData(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(getActivity(),
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiFetchPaymentAddress();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    public void apiDeletePaymentAddress(final String vpa) {
        try {
            if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
                CallProgressWheel.showLoadingDialog(getActivity(), "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_VPA, vpa);

                RestClient.getPayApiService().deletePaymentAddress(params, new Callback<AccountManagementResponse>() {
                    @Override
                    public void success(AccountManagementResponse accountManagementResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.optInt("", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                            String message = JSONParser.getServerMessage(jObj);
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                apiFetchPaymentAddress();
                            } else {
                                DialogPopup.alertPopup(getActivity(), "", message);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            retryDialogDeletePayData(DialogErrorType.SERVER_ERROR, vpa);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogDeletePayData(DialogErrorType.CONNECTION_LOST, vpa);
                    }
                });
            } else {
                retryDialogDeletePayData(DialogErrorType.NO_NET, vpa);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private void retryDialogDeletePayData(DialogErrorType dialogErrorType, final String vpa){
        DialogPopup.dialogNoInternet(getActivity(),
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiDeletePaymentAddress(vpa);
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
