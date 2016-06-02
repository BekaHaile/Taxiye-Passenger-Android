package product.clicklabs.jugnoo.fresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Prefs;


public class FreshAddressFragment extends Fragment {

	private final String TAG = FreshAddressFragment.class.getSimpleName();
	private ScrollView scrollViewRoot;

	private LinearLayout linearLayoutMain;
	private RelativeLayout relativeLayoutHome, relativeLayoutWork;
	private EditText editTextAddress;
	private TextView textViewHome, textViewWork, textViewOther, textViewScroll;

	private View rootView;
    private FreshActivity activity;

	private String homeAddress = "", workAddress = "";

	public FreshAddressFragment(){}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshAddressFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_address, container, false);


        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		relativeLayoutHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutHome);
		relativeLayoutWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWork);

		textViewHome = (TextView) rootView.findViewById(R.id.textViewHome); textViewHome.setTypeface(Fonts.mavenRegular(activity));
		textViewWork = (TextView) rootView.findViewById(R.id.textViewWork); textViewWork.setTypeface(Fonts.mavenRegular(activity));
		textViewOther = (TextView) rootView.findViewById(R.id.textViewOther); textViewOther.setTypeface(Fonts.mavenRegular(activity));
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
		editTextAddress = (EditText) rootView.findViewById(R.id.editTextAddress); editTextAddress.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.setSelectedAddress(homeAddress);
				activity.performBackPressed();
			}
		});

		relativeLayoutWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.setSelectedAddress(workAddress);
				activity.performBackPressed();
			}
		});

		activity.setTopBarAddVisibility(View.GONE);
		KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
				new KeyboardLayoutListener.KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {
				activity.setTopBarAddVisibility(View.VISIBLE);
			}

			@Override
			public void keyBoardClosed() {
			}
		});
		keyboardLayoutListener.setResizeTextView(false);
		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

		setSavePlaces();

		editTextAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					activity.setTopBarAddVisibility(View.GONE);
				} else {
					activity.setTopBarAddVisibility(View.VISIBLE);
				}
			}
		});


		return rootView;
	}

	private void setSavePlaces() {
		if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
			relativeLayoutHome.setVisibility(View.VISIBLE);
			textViewHome.setTextColor(getResources().getColor(R.color.text_color_hint));
			String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
			SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(homeString);
			String s = getResources().getString(R.string.home)+" \n" + searchResult.getAddress();
			SpannableString ss1 = new SpannableString(s);
			ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
			ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color)), 0, 4, 0);// set color
			textViewHome.setText(ss1);
			homeAddress = searchResult.getAddress();
		} else{
			relativeLayoutHome.setVisibility(View.GONE);
			homeAddress = "";
		}

		if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
			relativeLayoutWork.setVisibility(View.VISIBLE);
			textViewWork.setTextColor(getResources().getColor(R.color.text_color_hint));
			String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
			SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(workString);
			String s = getResources().getString(R.string.work)+" \n" + searchResult.getAddress();
			SpannableString ss1 = new SpannableString(s);
			ss1.setSpan(new RelativeSizeSpan(1f), 0, 4, 0); // set size
			ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color)), 0, 4, 0);// set color
			textViewWork.setText(ss1);
			workAddress = searchResult.getAddress();
		} else{
			relativeLayoutWork.setVisibility(View.GONE);
			workAddress = "";
		}
		if("".equalsIgnoreCase(homeAddress) && "".equalsIgnoreCase(workAddress)){
			textViewOther.setText(activity.getResources().getString(R.string.add_address));
			editTextAddress.setHint("");
		} else{
			textViewOther.setText(activity.getResources().getString(R.string.other));
			editTextAddress.setHint(activity.getResources().getString(R.string.add_your_address));
		}

		try{
			if(activity.getSelectedAddress().equalsIgnoreCase("")
					&& activity.getUserCheckoutResponse() != null
					&& activity.getUserCheckoutResponse().getCheckoutData() != null
					&& activity.getUserCheckoutResponse().getCheckoutData().getLastAddress() != null
					&& !activity.getUserCheckoutResponse().getCheckoutData().getLastAddress().equalsIgnoreCase("")){
				editTextAddress.setText(activity.getUserCheckoutResponse().getCheckoutData().getLastAddress());
			} else if(!activity.getSelectedAddress().equalsIgnoreCase("")){
				editTextAddress.setText(activity.getSelectedAddress());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addAddressPress(){
		String address = editTextAddress.getText().toString().trim();
		if(address.length() > 200){
			editTextAddress.requestFocus();
			editTextAddress.setError(activity.getResources().getString(R.string.limit_address));
		} else if(address.length() > 0){
			activity.setSelectedAddress(address);
			activity.performBackPressed();
		} else{
			editTextAddress.requestFocus();
			editTextAddress.setError(activity.getResources().getString(R.string.please_fill_your_address));
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden){
			activity.fragmentUISetup(this);
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(scrollViewRoot);
        System.gc();
	}


}
