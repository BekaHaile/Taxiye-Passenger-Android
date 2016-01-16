package product.clicklabs.jugnoo.support.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.SupportFAQ;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportFAQQuesAnsFragment extends Fragment implements FlurryEventNames, Constants {

	private ScrollView scrollViewRoot;
	private TextView textViewQuestion, textViewAnswer;

	private View rootView;
    private SupportActivity activity;

	private SupportFAQ.QuestionAnswer questionAnswer;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportFAQQuesAnsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQQuesAnsFragment(SupportFAQ.QuestionAnswer questionAnswer){
		this.questionAnswer = questionAnswer;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_faq_ques_ans, container, false);

        activity = (SupportActivity) getActivity();

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewQuestion = (TextView)rootView.findViewById(R.id.textViewQuestion);
		textViewQuestion.setTypeface(Fonts.mavenRegular(activity));
		textViewAnswer = (TextView)rootView.findViewById(R.id.textViewAnswer);
		textViewAnswer.setTypeface(Fonts.mavenLight(activity));

		textViewQuestion.setText(questionAnswer.getQuestion());
		textViewAnswer.setText(questionAnswer.getAnswer());

		return rootView;
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(scrollViewRoot);
        System.gc();
	}



}
