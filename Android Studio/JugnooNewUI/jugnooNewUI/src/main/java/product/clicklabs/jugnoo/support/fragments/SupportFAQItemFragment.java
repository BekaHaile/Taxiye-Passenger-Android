package product.clicklabs.jugnoo.support.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.ViewType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportFAQItemFragment extends Fragment implements FlurryEventNames, Constants {

	private ScrollView scrollViewRoot;
	private TextView textViewSubtitle, textViewDescription;
	private EditText editTextMessage;
	private Button buttonSubmit;

	private View rootView;
    private SupportActivity activity;

	private String parentName;
	private ShowPanelResponse.Item item;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportFAQItemFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQItemFragment(String parentName, ShowPanelResponse.Item item){
		this.parentName = parentName;
		this.item = item;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_item, container, false);

        activity = (SupportActivity) getActivity();

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewSubtitle = (TextView)rootView.findViewById(R.id.textViewSubtitle);
		textViewSubtitle.setTypeface(Fonts.mavenRegular(activity));
		textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
		textViewDescription.setTypeface(Fonts.mavenLight(activity));
		editTextMessage = (EditText)rootView.findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(Fonts.mavenLight(activity));
		buttonSubmit = (Button)rootView.findViewById(R.id.buttonSubmit);
		buttonSubmit.setTypeface(Fonts.mavenRegular(activity));

		textViewSubtitle.setText(parentName);
		textViewDescription.setText(item.getText());

		if(ViewType.TEXT_BOX.getOrdinal() == item.getViewType()){
			editTextMessage.setVisibility(View.VISIBLE);
			buttonSubmit.setVisibility(View.VISIBLE);
		} else if(ViewType.CALL_BUTTON.getOrdinal() == item.getViewType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.VISIBLE);
			buttonSubmit.setText(activity.getResources().getString(R.string.call_driver).toUpperCase(Locale.ENGLISH));
		} else if(ViewType.TEXT_ONLY.getOrdinal() == item.getViewType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.GONE);
		}

		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		return rootView;
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(scrollViewRoot);
        System.gc();
	}



}
