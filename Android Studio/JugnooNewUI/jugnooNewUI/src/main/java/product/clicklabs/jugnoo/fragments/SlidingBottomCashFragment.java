package product.clicklabs.jugnoo.fragments;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by socomo on 1/8/16.
 */
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private LinearLayout linearLayoutRoot, linearLayoutCash;
    private ImageView radioBtnPaytm, radioBtnCash;
    private TextView textViewPaytmValue;
    private RelativeLayout relativeLayoutPaytm;
    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_cash, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutPaytm = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutPaytm);
        linearLayoutCash = (LinearLayout)rootView.findViewById(R.id.linearLayoutCash);
        radioBtnPaytm = (ImageView)rootView.findViewById(R.id.radio_paytm);
        radioBtnCash = (ImageView)rootView.findViewById(R.id.radio_cash);
        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenRegular(getActivity()), Typeface.BOLD);
        ((TextView)rootView.findViewById(R.id.textViewPaytm)).setTypeface(Fonts.mavenLight(getActivity()));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(getActivity()));
        textViewPaytmValue.setText(String.format(activity.getResources().getString(R.string.ruppes_value_format_without_space), Data.userData.getPaytmBalanceStr()));
        relativeLayoutPaytm.setOnClickListener(this);
        linearLayoutCash.setOnClickListener(this);

        if(PaymentOption.PAYTM.getOrdinal() == Prefs.with(activity).getInt(SPLabels.PREFERRED_PAYMENT_OPTION, PaymentOption.PAYTM.getOrdinal())){
            paymentSelection(radioBtnPaytm, radioBtnCash);
        } else{
            paymentSelection(radioBtnCash, radioBtnPaytm);
        }

        return rootView;
    }


    private void paymentSelection(ImageView selected, ImageView unSelected){
        selected.setImageResource(R.drawable.radio_selected_icon);
        unSelected.setImageResource(R.drawable.radio_unselected_icon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relativeLayoutPaytm:
                paymentSelection(radioBtnPaytm, radioBtnCash);
                break;

            case R.id.linearLayoutCash:
                paymentSelection(radioBtnCash, radioBtnPaytm);
                break;
        }
    }
}
