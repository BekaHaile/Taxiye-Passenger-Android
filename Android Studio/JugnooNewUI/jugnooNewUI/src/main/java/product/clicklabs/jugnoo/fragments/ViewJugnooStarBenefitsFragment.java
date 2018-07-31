package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by Parminder Saini on 01/05/17.
 */

public class ViewJugnooStarBenefitsFragment extends Fragment {


    @BindView(R.id.rvBenefits)
    NonScrollListView rvBenefits;
    @BindView(R.id.btnUpgradeNow)
    Button btnUpgradeNow;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_jstar_view_benefits, container, false);
        unbinder = ButterKnife.bind(this, rootview);
        try {
            if (rootLayout != null) {
                new ASSL(getActivity(), rootLayout, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnUpgradeNow.setTypeface(Fonts.mavenMedium(getActivity()));
        StarMembershipAdapter starMembershipAdapter = new StarMembershipAdapter(getActivity(), Data.userData.getSubscriptionData().getSubscriptionBenefits()
                , new StarMembershipAdapter.Callback() {
            @Override
            public void onUnsubscribe() {
            }
        });

        rvBenefits.setAdapter(starMembershipAdapter);
        return rootview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnUpgradeNow)
    public void onClick() {
        ((JugnooStarSubscribedActivity)getActivity()).openStarCheckoutFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            ((JugnooStarSubscribedActivity)getActivity()).setScreenTitle(getActivity().getString(R.string.benefits));
        }
    }


}
