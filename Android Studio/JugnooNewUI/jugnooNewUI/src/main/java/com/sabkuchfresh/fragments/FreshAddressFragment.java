package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.FreshAddressAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.AddPlacesModel;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.NonScrollListView;


public class FreshAddressFragment extends Fragment implements View.OnClickListener, FlurryEventNames, FreshAddressAdapter.Callback {

    private final String TAG = FreshAddressFragment.class.getSimpleName();

    private NonScrollListView listViewDeliveryAddress;

    private LinearLayout linearLayoutMain;

    private View rootView;
    private FreshActivity activity;

    private String homeAddress = "", workAddress = "", lastAddress = "";
    protected Bus mBus;

    Button addressAdd;


    private View addressView;
    private FreshAddressAdapter addressFragment;

    public FreshAddressFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_address, container, false);


        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();

        activity.fragmentUISetup(this);
        addressView = (View) rootView.findViewById(R.id.addAddress);
        if(activity.getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses().size() == 0) {
            addressView.setVisibility(View.VISIBLE);
        } else {
            addressView.setVisibility(View.GONE);
        }

        addressAdd = (Button) addressView.findViewById(R.id.add_button);
        addressAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openMapAddress(null);
//                onAddAddress();
            }
        });



        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);

        new ASSL(activity, linearLayoutMain, 1134, 720, false);

        listViewDeliveryAddress = (NonScrollListView) rootView.findViewById(R.id.listViewDeliveryAddress);
        listViewDeliveryAddress.setVisibility(View.VISIBLE);

        addressFragment = new FreshAddressAdapter(activity, activity.getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses(), this);
        listViewDeliveryAddress.setAdapter(addressFragment);

        return rootView;
    }


    @Subscribe
    public void onUpdateListEvent(AddressAdded event) {
        if (event.flag) {
            // New Address added
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutMain);
        System.gc();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageViewEditOther: {

                break;
            }

        }
    }

    @Subscribe
    public void onSortEvent(AddPlacesModel event) {
        if (event.flag) {

        }
    }

    @Override
    public void onSlotSelected(int position, DeliveryAddress slot) {
        activity.setSelectedAddress(slot.getLastAddress());
        activity.setSelectedLatLng(new LatLng(Double.parseDouble(slot.getDeliveryLatitude()), Double.parseDouble(slot.getDeliveryLongitude())));
        activity.setSelectedAddressId(0);
        FlurryEventLogger.event(Address_Screen, CHANGE_ADDRESS, ""+position);

        mBus.post(new AddressAdded(true));
//        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_address_selected), 3);

        activity.performBackPressed();
    }
}
