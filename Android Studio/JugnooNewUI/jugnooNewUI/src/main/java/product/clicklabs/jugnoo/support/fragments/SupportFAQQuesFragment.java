package product.clicklabs.jugnoo.support.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.SupportFAq;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.adapters.SupportFAQQuesAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.LinearLayoutManager;


public class SupportFAQQuesFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private RecyclerView recyclerViewSupportFaqQues;
	private SupportFAQQuesAdapter supportFAQQuesAdapter;

	private View rootView;
    private SupportActivity activity;

	private SupportFAq supportFAq;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportFAQQuesFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQQuesFragment(SupportFAq supportFAq){
		this.supportFAq = supportFAq;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_faq_ques, container, false);

        activity = (SupportActivity) getActivity();

		activity.setTitle(supportFAq.getName());

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewSupportFaqQues = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaqQues);
		recyclerViewSupportFaqQues.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewSupportFaqQues.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaqQues.setHasFixedSize(false);

		supportFAQQuesAdapter = new SupportFAQQuesAdapter((ArrayList<SupportFAq.QuestionAnswer>) supportFAq.getQuestionAnswers(),
				activity, R.layout.list_item_support_faq,
				new SupportFAQQuesAdapter.Callback() {
					@Override
					public void onClick(int position, SupportFAq.QuestionAnswer questionAnswer) {
						activity.getSupportFragmentManager().beginTransaction()
								.add(activity.getLinearLayoutContainer().getId(),
										new SupportFAQQuesAnsFragment(questionAnswer), SupportFAQQuesAnsFragment.class.getName())
								.addToBackStack(SupportFAQQuesAnsFragment.class.getName())
								.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
										.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
								.commitAllowingStateLoss();
					}
				});
		recyclerViewSupportFaqQues.setAdapter(supportFAQQuesAdapter);

		return rootView;
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
	}



}
