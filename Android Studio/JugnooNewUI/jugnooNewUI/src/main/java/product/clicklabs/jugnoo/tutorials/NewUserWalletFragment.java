package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserWalletFragment extends Fragment {

    private View root;
    private RelativeLayout rlRoot;
    private Button bFinish;
    private NewUserChutiyapaa activity;

    public static NewUserWalletFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserWalletFragment fragment = new NewUserWalletFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_wallet, container, false);
        rlRoot = (RelativeLayout) root.findViewById(R.id.rlRoot);
        activity = (NewUserChutiyapaa) getActivity();

        try {
            if (rlRoot != null) {
                new ASSL(getActivity(), rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bFinish = (Button) root.findViewById(R.id.bFinish);

        bFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performBackPressed();
            }
        });

        activity.getTvTitle().setText(activity.getResources().getString(R.string.connect_wallet));
        activity.setTickLineView();
        return root;
    }
}
