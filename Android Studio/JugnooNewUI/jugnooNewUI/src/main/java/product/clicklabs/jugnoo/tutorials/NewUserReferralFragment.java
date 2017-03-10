package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserReferralFragment extends Fragment {

    private View root;
    private Button buttonApplyPromo;
    private NewUserChutiyapaa activity;
    private RelativeLayout rlRoot;

    public static NewUserReferralFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserReferralFragment fragment = new NewUserReferralFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_referral, container, false);
        rlRoot = (RelativeLayout) root.findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(activity, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity = (NewUserChutiyapaa)getActivity();
        buttonApplyPromo = (Button) root.findViewById(R.id.buttonApplyPromo);

        buttonApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openNewUserCompleteProfileFragment(activity, activity.getRlContainer());
            }
        });

        return root;
    }
}
