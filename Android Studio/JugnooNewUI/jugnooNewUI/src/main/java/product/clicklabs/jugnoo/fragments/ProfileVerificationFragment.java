package product.clicklabs.jugnoo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.DocumentStatusAdapter;
import product.clicklabs.jugnoo.retrofit.model.DocumentData;
import product.clicklabs.jugnoo.retrofit.model.FetchDocumentResponse;
import product.clicklabs.jugnoo.retrofit.model.UploadDocumentResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;

import static com.sabkuchfresh.analytics.GAAction.PROFILE;
import static com.sabkuchfresh.analytics.GAAction.USER;
import static com.sabkuchfresh.analytics.GACategory.SIDE_MENU;

public class ProfileVerificationFragment extends Fragment implements GAAction, GACategory {

    private AccountActivity activity;
    private TextView textViewLogout,tvNeedHelp,tvStatus;
    private RecyclerView rvVerificationDocs;
    private DocumentStatusAdapter documentStatusAdapter;
    private List<DocumentData> documentDataList;

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
        fetchAllDocuments();
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
        tvNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAUtils.event(PROFILE_SCREEN, SUPPORT+CLICKED, "");
                activity.startActivity(new Intent(activity, SupportActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
        SpannableStringBuilder spannableText = new SpannableStringBuilder(getString(R.string.status_colon_approval,getString(R.string.pending)));
                spannableText.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStatus.setText(spannableText);
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

    public void fetchAllDocuments() {
        HashMap<String,String> params = new HashMap<>();
        new ApiCommon<FetchDocumentResponse>(activity).putDefaultParams(true).showLoader(true).execute(params, ApiName.FETCH_DOCUMENTS, new APICommonCallback<FetchDocumentResponse>() {
            @Override
            public void onSuccess(FetchDocumentResponse fetchDocumentResponse, String message, int flag) {
                documentDataList = fetchDocumentResponse.getDocumentDataList();
                if(rvVerificationDocs.getAdapter() == null) {
                    documentStatusAdapter = new DocumentStatusAdapter(activity,documentDataList, new DocumentStatusAdapter.OnDocumentClicked() {
                    @Override
                    public void onDocClick(int position) {
                        activity.openDocumentUploadFragment(documentDataList.get(position));
                    }
                    });

                    rvVerificationDocs.setAdapter(documentStatusAdapter);
                } else {
                    documentStatusAdapter.updateList(documentDataList);
                }
            }

            @Override
            public boolean onError(FetchDocumentResponse fetchDocumentResponse, String message, int flag) {
                return false;
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            fetchAllDocuments();
        }
    }
}
