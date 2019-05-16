package product.clicklabs.jugnoo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.DocumentStatusAdapter;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;

import static com.sabkuchfresh.analytics.GAAction.PROFILE;
import static com.sabkuchfresh.analytics.GAAction.USER;
import static com.sabkuchfresh.analytics.GACategory.SIDE_MENU;

public class ProfileVerificationFragment extends Fragment {

    private AccountActivity activity;
    private TextView textViewLogout,tvNeedHelp,tvStatus;
    private RecyclerView rvVerificationDocs;
    private DocumentStatusAdapter documentStatusAdapter;

    public static ProfileVerificationFragment newInstance() {
        ProfileVerificationFragment fragment = new ProfileVerificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView= inflater.inflate(R.layout.fragment_profile_verification, container, false);
        tvStatus = itemView.findViewById(R.id.tvStatus); tvStatus.setTypeface(Fonts.mavenMedium(activity));
        tvNeedHelp = itemView.findViewById(R.id.tvNeedHelp); tvNeedHelp.setTypeface(Fonts.mavenMedium(activity));
        textViewLogout = itemView.findViewById(R.id.textViewLogout); textViewLogout.setTypeface(Fonts.mavenMedium(activity));
        rvVerificationDocs = itemView.findViewById(R.id.rvVerificationDocs);
        rvVerificationDocs.setItemAnimator(new DefaultItemAnimator());
        rvVerificationDocs.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        documentStatusAdapter = new DocumentStatusAdapter(activity, new DocumentStatusAdapter.OnDocumentClicked() {
            @Override
            public void onDocClick(int position) {
//                TODO IMPLEMENT THIS
                Toast.makeText(activity,"Under Construction with position: "+position,Toast.LENGTH_SHORT).show();
                activity.openDocumentUploadFragment("1234");
            }
        });
        rvVerificationDocs.setAdapter(documentStatusAdapter);
        rvVerificationDocs.setNestedScrollingEnabled(false);
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                        getResources().getString(R.string.are_you_sure_you_want_to_logout),
                        getResources().getString(R.string.logout), getResources().getString(R.string.cancel),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                activity.logoutAsync(activity);
                            }
                        },
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                            }
                        },
                        true, false);
                GAUtils.event(SIDE_MENU, USER + PROFILE, GAAction.LOGOUT);

            }
        });
        tvStatus.setText(activity.getString(R.string.status_colon_approval,"Pending"));
        return itemView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof AccountActivity) {
            activity = (AccountActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

}
