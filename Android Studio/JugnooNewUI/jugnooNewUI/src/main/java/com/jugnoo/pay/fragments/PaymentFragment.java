package com.jugnoo.pay.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jugnoo.pay.activities.AddPaymentAddressActivity;
import com.jugnoo.pay.activities.SelectContactActivity;
import com.jugnoo.pay.adapters.ContactsListAdapter;
import com.jugnoo.pay.adapters.PaymentAddressAdapter;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.RecyclerViewClickListener;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by ankit on 05/12/16.
 */

public class PaymentFragment extends Fragment implements RecyclerViewClickListener {

    private View rootView;
    private RecyclerView rvPaymentAddress;
    private PaymentAddressAdapter paymentAddressAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_address, container, false);

        rvPaymentAddress = (RecyclerView) rootView.findViewById(R.id.rvPaymentAddress);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvPaymentAddress.setLayoutManager(mLayoutManager);
        rvPaymentAddress.setItemAnimator(new DefaultItemAnimator());

        paymentAddressAdapter = new PaymentAddressAdapter(getActivity(), new ArrayList<SelectUser>(), this);
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

    @Override
    public void recyclerViewListClicked(View v, int position) {

    }

    @Override
    public void recyclerViewListClicked(View v, int position, int viewType) {

    }
}
