package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserCompleteProfileFragment extends Fragment{

    private View root;
    private Button bClaimCoupon;
    private LinearLayout rlRoot;
    private NewUserChutiyapaa activity;

    public static NewUserCompleteProfileFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserCompleteProfileFragment fragment = new NewUserCompleteProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_complete_profile, container, false);
        rlRoot = (LinearLayout) root.findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(getActivity(), rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity = (NewUserChutiyapaa) getActivity();
        bClaimCoupon = (Button) root.findViewById(R.id.bClaimCoupon);

        bClaimCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openNewUserWalletFragment(activity, activity.getRlContainer());
            }
        });

        return root;
    }
}
