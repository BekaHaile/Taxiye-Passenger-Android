package product.clicklabs.jugnoo;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppSignatureHelper;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.UniqueIMEIID;
import product.clicklabs.jugnoo.utils.Utils;


public class DebugOptionsActivity extends BaseActivity {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    RelativeLayout relativeLayoutDebugOptions;
    ImageView imageViewArrowDebugOptions;
    LinearLayout linearLayoutDebugOptionsBelow;
    RelativeLayout relativeLayoutShowAllDrivers, relativeLayoutShowDriverInfo;
    ImageView imageViewShowAllDrivers, imageViewShowDriverInfo;

    RelativeLayout relativeLayoutServerEnv;
    ImageView imageViewArrowServerEnv;
    LinearLayout linearLayoutServerEnvBelow;
    RelativeLayout relativeLayoutLive4012, relativeLayoutTest8012, relativeLayoutCustom;
    ImageView imageViewLive4012, imageViewTest8012, imageViewCustom;
    EditText editTextCustom;

    RelativeLayout relativeLayoutServerEnvFresh;
    ImageView imageViewArrowServerEnvFresh;
    LinearLayout linearLayoutServerEnvFreshBelow;
    RelativeLayout relativeLayoutLiveFresh, relativeLayoutTestFresh, relativeLayoutCustomFresh;
    ImageView imageViewLiveFresh, imageViewTestFresh, imageViewCustomFresh;
    EditText editTextCustomFresh;

    RelativeLayout relativeLayoutServerEnvMenus;
    ImageView imageViewArrowServerEnvMenus;
    LinearLayout linearLayoutServerEnvMenusBelow;
    RelativeLayout relativeLayoutLiveMenus, relativeLayoutTestMenus, relativeLayoutCustomMenus;
    ImageView imageViewLiveMenus, imageViewTestMenus, imageViewCustomMenus;
    EditText editTextCustomMenus;

    RelativeLayout relativeLayoutServerEnvFatafat;
    ImageView imageViewArrowServerEnvFatafat;
    LinearLayout linearLayoutServerEnvFatafatBelow;
    RelativeLayout relativeLayoutLiveFatafat, relativeLayoutTestFatafat, relativeLayoutCustomFatafat;
    ImageView imageViewLiveFatafat, imageViewTestFatafat, imageViewCustomFatafat;
    EditText editTextCustomFatafat;


    RelativeLayout relativeLayoutAuto;
    Button buttonSave, buttonSaveTop, buttonCancel;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll, tvDeviceID, tvFBKeyHash, tvSMSHash;
    RelativeLayout rlJungleApisDisable;
    ImageView ivJungleApisDisable;
    String smsHash;

    int showAllDriversValue = 0;
    int showDriverInfoValue = 0;
    int jungleApisDisable = 0;

    String selectedServer = Config.getDefaultServerUrl();
    String selectedServerFresh = Config.getFreshDefaultServerUrl();
    String selectedServerMenus = Config.getMenusDefaultServerUrl();
    String selectedServerFatafat = Config.getFatafatDefaultServerUrl();

    ProgressDialog progressDialog;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_options);

        relative = findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


        ((TextView) findViewById(R.id.textViewDebugOptions)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnv)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvFresh)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvMenus)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvFatafat)).setTypeface(Fonts.mavenRegular(this));


        relativeLayoutDebugOptions = findViewById(R.id.relativeLayoutDebugOptions);
        imageViewArrowDebugOptions = findViewById(R.id.imageViewArrowDebugOptions);
        linearLayoutDebugOptionsBelow = findViewById(R.id.linearLayoutDebugOptionsBelow);
        relativeLayoutShowAllDrivers = findViewById(R.id.relativeLayoutShowAllDrivers);
        relativeLayoutShowDriverInfo = findViewById(R.id.relativeLayoutShowDriverInfo);
        imageViewShowAllDrivers = findViewById(R.id.imageViewShowAllDrivers);
        imageViewShowDriverInfo = findViewById(R.id.imageViewShowDriverInfo);
        ((TextView) findViewById(R.id.textViewShowAllDrivers)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewShowDriverInfo)).setTypeface(Fonts.mavenLight(this));



        relativeLayoutServerEnv = findViewById(R.id.relativeLayoutServerEnv);
        imageViewArrowServerEnv = findViewById(R.id.imageViewArrowServerEnv);
        linearLayoutServerEnvBelow = findViewById(R.id.linearLayoutServerEnvBelow);
        relativeLayoutLive4012 = findViewById(R.id.relativeLayoutLive4012);
        relativeLayoutTest8012 = findViewById(R.id.relativeLayoutTest8012);
        relativeLayoutCustom = findViewById(R.id.relativeLayoutCustom);
        imageViewLive4012 = findViewById(R.id.imageViewLive4012);
        imageViewTest8012 = findViewById(R.id.imageViewTest8012);
        imageViewCustom = findViewById(R.id.imageViewCustom);
        ((TextView) findViewById(R.id.textViewLive4012)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8012)).setTypeface(Fonts.mavenLight(this));
        editTextCustom = findViewById(R.id.editTextCustom);
        editTextCustom.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvFresh = findViewById(R.id.relativeLayoutServerEnvFresh);
        imageViewArrowServerEnvFresh = findViewById(R.id.imageViewArrowServerEnvFresh);
        linearLayoutServerEnvFreshBelow = findViewById(R.id.linearLayoutServerEnvFreshBelow);
        relativeLayoutLiveFresh = findViewById(R.id.relativeLayoutLiveFresh);
        relativeLayoutTestFresh = findViewById(R.id.relativeLayoutTestFresh);
        relativeLayoutCustomFresh = findViewById(R.id.relativeLayoutCustomFresh);
        imageViewLiveFresh = findViewById(R.id.imageViewLiveFresh);
        imageViewTestFresh = findViewById(R.id.imageViewTestFresh);
        imageViewCustomFresh = findViewById(R.id.imageViewCustomFresh);
        ((TextView) findViewById(R.id.textViewLiveFresh)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestFresh)).setTypeface(Fonts.mavenLight(this));
        editTextCustomFresh = findViewById(R.id.editTextCustomFresh);
        editTextCustomFresh.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvMenus = findViewById(R.id.relativeLayoutServerEnvMenus);
        imageViewArrowServerEnvMenus = findViewById(R.id.imageViewArrowServerEnvMenus);
        linearLayoutServerEnvMenusBelow = findViewById(R.id.linearLayoutServerEnvMenusBelow);
        relativeLayoutLiveMenus = findViewById(R.id.relativeLayoutLiveMenus);
        relativeLayoutTestMenus = findViewById(R.id.relativeLayoutTestMenus);
        relativeLayoutCustomMenus = findViewById(R.id.relativeLayoutCustomMenus);
        imageViewLiveMenus = findViewById(R.id.imageViewLiveMenus);
        imageViewTestMenus = findViewById(R.id.imageViewTestMenus);
        imageViewCustomMenus = findViewById(R.id.imageViewCustomMenus);
        ((TextView) findViewById(R.id.textViewLiveMenus)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestMenus)).setTypeface(Fonts.mavenLight(this));
        editTextCustomMenus = findViewById(R.id.editTextCustomMenus);
        editTextCustomMenus.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvFatafat = findViewById(R.id.relativeLayoutServerEnvFatafat);
        imageViewArrowServerEnvFatafat = findViewById(R.id.imageViewArrowServerEnvFatafat);
        linearLayoutServerEnvFatafatBelow = findViewById(R.id.linearLayoutServerEnvFatafatBelow);
        relativeLayoutLiveFatafat = findViewById(R.id.relativeLayoutLiveFatafat);
        relativeLayoutTestFatafat = findViewById(R.id.relativeLayoutTestFatafat);
        relativeLayoutCustomFatafat = findViewById(R.id.relativeLayoutCustomFatafat);
        imageViewLiveFatafat = findViewById(R.id.imageViewLiveFatafat);
        imageViewTestFatafat = findViewById(R.id.imageViewTestFatafat);
        imageViewCustomFatafat = findViewById(R.id.imageViewCustomFatafat);
        ((TextView) findViewById(R.id.textViewLiveFatafat)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestFatafat)).setTypeface(Fonts.mavenLight(this));
        editTextCustomFatafat = findViewById(R.id.editTextCustomFatafat);
        editTextCustomFatafat.setTypeface(Fonts.mavenMedium(this));
        

        relativeLayoutAuto = findViewById(R.id.relativeLayoutAuto);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setTypeface(Fonts.mavenRegular(this));
        buttonSaveTop = findViewById(R.id.buttonSaveTop);
        buttonSaveTop.setTypeface(Fonts.mavenRegular(this));
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setTypeface(Fonts.mavenRegular(this));


        scrollView = findViewById(R.id.scrollView);
        linearLayoutMain = findViewById(R.id.linearLayoutMain);
        textViewScroll = findViewById(R.id.textViewScroll);
		tvDeviceID = findViewById(R.id.tvDeviceID); tvDeviceID.setTypeface(Fonts.mavenMedium(this));
		tvFBKeyHash = findViewById(R.id.tvFBKeyHash); tvFBKeyHash.setTypeface(Fonts.mavenMedium(this));
		tvSMSHash = findViewById(R.id.tvSMSHash); tvSMSHash.setTypeface(Fonts.mavenMedium(this));

		setBoldText(tvDeviceID, "Device ID: ", UniqueIMEIID.getUniqueIMEIId(this));
		tvDeviceID.setOnLongClickListener(v -> {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText(UniqueIMEIID.getUniqueIMEIId(this), UniqueIMEIID.getUniqueIMEIId(this));
				if (clipboard != null) {
					clipboard.setPrimaryClip(clip);
				}
				Utils.showToast(DebugOptionsActivity.this, getString(R.string.copied));
			} catch (Exception ignored) {}
			return false;
		});

		setBoldText(tvFBKeyHash, "FB Keyhash: ", Utils.generateKeyHash(this));
		tvFBKeyHash.setOnLongClickListener(v -> {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText(Utils.generateKeyHash(this), Utils.generateKeyHash(this));
				if (clipboard != null) {
					clipboard.setPrimaryClip(clip);
				}
				Utils.showToast(DebugOptionsActivity.this, getString(R.string.copied));
			} catch (Exception ignored) {}
			return false;
		});


		tvSMSHash.setVisibility(View.GONE);
		ArrayList<String> smsHashes = AppSignatureHelper.Companion.getAppSignatures(this);
		if(smsHashes != null && smsHashes.size() > 0){
			smsHash = smsHashes.get(0);
			setBoldText(tvSMSHash, "SMS Hash: ", smsHash);
			tvSMSHash.setVisibility(View.VISIBLE);
			tvSMSHash.setOnLongClickListener(v -> {
				try {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText(smsHash, smsHash);
					if (clipboard != null) {
						clipboard.setPrimaryClip(clip);
					}
					Utils.showToast(DebugOptionsActivity.this, getString(R.string.copied));
				} catch (Exception ignored) {}
				return false;
			});
		}



		rlJungleApisDisable = findViewById(R.id.rlJungleApisDisable);
		ivJungleApisDisable = findViewById(R.id.ivJungleApisDisable);
		rlJungleApisDisable.setOnClickListener(v -> {
			if(jungleApisDisable == 1){
				jungleApisDisable = 0;
			} else {
				jungleApisDisable = 1;
			}
			toggleJungleApisDisableUI(jungleApisDisable, rlJungleApisDisable, ivJungleApisDisable);
		});


		jungleApisDisable = Data.jungleApisDisable;
		toggleJungleApisDisableUI(jungleApisDisable, rlJungleApisDisable, ivJungleApisDisable);

		imageViewBack.setOnClickListener(v -> performBackPressed());


        buttonSave.setOnClickListener(v -> {
			boolean stop = false;
			if (!selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())
					&& !selectedServer.equalsIgnoreCase(Config.getDevServerUrl())
					&& !selectedServer.equalsIgnoreCase(Config.getDev1ServerUrl())
					&& !selectedServer.equalsIgnoreCase(Config.getDev2ServerUrl())
					&& !selectedServer.equalsIgnoreCase(Config.getDev3ServerUrl())) {
				String customUrl = editTextCustom.getText().toString().trim();
				if ("".equalsIgnoreCase(customUrl)) {
					editTextCustom.requestFocus();
					editTextCustom.setError(getString(R.string.please_enter_something));
					stop = true;
				} else {
					selectedServer = customUrl;
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
					Data.jungleApisDisable = jungleApisDisable;
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
				}
			} else {
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
				Data.jungleApisDisable = jungleApisDisable;
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
			}

			if (!selectedServerFresh.equalsIgnoreCase(Config.getFreshLiveServerUrl())
					&& !selectedServerFresh.equalsIgnoreCase(Config.getFreshDevServerUrl())) {
				String customUrl = editTextCustomFresh.getText().toString().trim();
				if ("".equalsIgnoreCase(customUrl)) {
					editTextCustomFresh.requestFocus();
					editTextCustomFresh.setError(getString(R.string.please_enter_something));
					stop = true;
				} else {
					selectedServerFresh = customUrl;
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.FRESH_SERVER_SELECTED, selectedServerFresh);
				}
			} else {
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.FRESH_SERVER_SELECTED, selectedServerFresh);
			}

			if (!selectedServerMenus.equalsIgnoreCase(Config.getMenusLiveServerUrl())
					&& !selectedServerMenus.equalsIgnoreCase(Config.getMenusDevServerUrl())) {
				String customUrl = editTextCustomMenus.getText().toString().trim();
				if ("".equalsIgnoreCase(customUrl)) {
					editTextCustomMenus.requestFocus();
					editTextCustomMenus.setError(getString(R.string.please_enter_something));
					stop = true;
				} else {
					selectedServerMenus = customUrl;
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.MENUS_SERVER_SELECTED, selectedServerMenus);
				}
			} else {
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.MENUS_SERVER_SELECTED, selectedServerMenus);
			}

			if (!selectedServerFatafat.equalsIgnoreCase(Config.getFatafatLiveServerUrl())
					&& !selectedServerFatafat.equalsIgnoreCase(Config.getFatafatDevServerUrl())) {
				String customUrl = editTextCustomFatafat.getText().toString().trim();
				if ("".equalsIgnoreCase(customUrl)) {
					editTextCustomFatafat.requestFocus();
					editTextCustomFatafat.setError(getString(R.string.please_enter_something));
					stop = true;
				} else {
					selectedServerFatafat = customUrl;
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.FATAFAT_SERVER_SELECTED, selectedServerFatafat);
				}
			} else {
				Prefs.with(DebugOptionsActivity.this).save(SPLabels.FATAFAT_SERVER_SELECTED, selectedServerFatafat);
			}

			if(!stop){
				MyApplication.getInstance().initializeServerURL(this);
				performBackPressed();
			}
		});

        buttonSaveTop.setOnClickListener(v -> buttonSave.performClick());

        buttonCancel.setOnClickListener(v -> performBackPressed());



        showAllDriversValue = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfoValue = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);

        selectedServer = Prefs.with(this).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());
        selectedServerFresh = Prefs.with(this).getString(SPLabels.FRESH_SERVER_SELECTED, Config.getFreshDefaultServerUrl());
        selectedServerMenus = Prefs.with(this).getString(SPLabels.MENUS_SERVER_SELECTED, Config.getMenusDefaultServerUrl());
        selectedServerFatafat = Prefs.with(this).getString(SPLabels.FATAFAT_SERVER_SELECTED, Config.getFatafatDefaultServerUrl());

		toggleJungleApisDisableUI(showAllDriversValue, relativeLayoutShowAllDrivers, imageViewShowAllDrivers);

		toggleJungleApisDisableUI(showDriverInfoValue, relativeLayoutShowDriverInfo, imageViewShowDriverInfo);

		setServerUI(selectedServer);
        setFreshServerUI(selectedServerFresh);
        setMenusServerUI(selectedServerMenus);
        setFatafatServerUI(selectedServerFatafat);

        imageViewArrowDebugOptions.setRotation(90);
        linearLayoutDebugOptionsBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnv.setRotation(90);
        linearLayoutServerEnvBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnvFresh.setRotation(90);
        linearLayoutServerEnvFreshBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnvMenus.setRotation(90);
        linearLayoutServerEnvMenusBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnvFatafat.setRotation(90);
        linearLayoutServerEnvFatafatBelow.setVisibility(View.VISIBLE);


        relativeLayoutShowAllDrivers.setOnClickListener(v -> {
			if (showAllDriversValue == 0) {
				showAllDriversValue = 1;
			} else {
				showAllDriversValue = 0;
			}
			toggleJungleApisDisableUI(showAllDriversValue, relativeLayoutShowAllDrivers, imageViewShowAllDrivers);
		});


        relativeLayoutShowDriverInfo.setOnClickListener(v -> {
			if (showDriverInfoValue == 0) {
				showDriverInfoValue = 1;
			} else {
				showDriverInfoValue = 0;
			}
			toggleJungleApisDisableUI(showDriverInfoValue, relativeLayoutShowDriverInfo, imageViewShowDriverInfo);
		});


        relativeLayoutLive4012.setOnClickListener(v -> {
			selectedServer = Config.getLiveServerUrl();
			setServerUI(selectedServer);
			setAllServersToSame(ProductType.AUTO, ConfigMode.LIVE);
		});

        relativeLayoutTest8012.setOnClickListener(v -> {
			selectedServer = Config.getDevServerUrl();
			setServerUI(selectedServer);
			setAllServersToSame(ProductType.AUTO, ConfigMode.DEV);
		});

        relativeLayoutCustom.setOnClickListener(v -> {
			String customUrl = editTextCustom.getText().toString().trim();
			if ("".equalsIgnoreCase(customUrl)) {
				editTextCustom.requestFocus();
				editTextCustom.setError(getString(R.string.please_enter_something));
			} else {
				selectedServer = customUrl;
				setServerUI(selectedServer);
			}
		});

        editTextCustom.setOnClickListener(v -> selectedServer = editTextCustom.getText().toString().trim());







        relativeLayoutLiveFresh.setOnClickListener(v -> {
			selectedServerFresh = Config.getFreshLiveServerUrl();
			setFreshServerUI(selectedServerFresh);
			setAllServersToSame(ProductType.FRESH, ConfigMode.LIVE);
		});

        relativeLayoutTestFresh.setOnClickListener(v -> {
			selectedServerFresh = Config.getFreshDevServerUrl();
			setFreshServerUI(selectedServerFresh);
			setAllServersToSame(ProductType.FRESH, ConfigMode.DEV);
		});

        relativeLayoutCustomFresh.setOnClickListener(v -> {
			String customUrl = editTextCustomFresh.getText().toString().trim();
			if ("".equalsIgnoreCase(customUrl)) {
				editTextCustomFresh.requestFocus();
				editTextCustomFresh.setError(getString(R.string.please_enter_something));
			} else {
				selectedServerFresh = customUrl;
				setFreshServerUI(selectedServerFresh);
			}
		});

        editTextCustomFresh.setOnClickListener(v -> selectedServerFresh = editTextCustomFresh.getText().toString().trim());






        relativeLayoutLiveMenus.setOnClickListener(v -> {
			selectedServerMenus = Config.getMenusLiveServerUrl();
			setMenusServerUI(selectedServerMenus);
			setAllServersToSame(ProductType.MENUS, ConfigMode.LIVE);
		});

        relativeLayoutTestMenus.setOnClickListener(v -> {
			selectedServerMenus = Config.getMenusDevServerUrl();
			setMenusServerUI(selectedServerMenus);
			setAllServersToSame(ProductType.MENUS, ConfigMode.DEV);
		});

        relativeLayoutCustomMenus.setOnClickListener(v -> {
			String customUrl = editTextCustomMenus.getText().toString().trim();
			if ("".equalsIgnoreCase(customUrl)) {
				editTextCustomMenus.requestFocus();
				editTextCustomMenus.setError(getString(R.string.please_enter_something));
			} else {
				selectedServerMenus = customUrl;
				setMenusServerUI(selectedServerMenus);
			}
		});

        editTextCustomMenus.setOnClickListener(v -> selectedServerMenus = editTextCustomMenus.getText().toString().trim());



        
        
        relativeLayoutLiveFatafat.setOnClickListener(v -> {
			selectedServerFatafat = Config.getFatafatLiveServerUrl();
			setFatafatServerUI(selectedServerFatafat);
			setAllServersToSame(ProductType.FEED, ConfigMode.LIVE);
		});

        relativeLayoutTestFatafat.setOnClickListener(v -> {
			selectedServerFatafat = Config.getFatafatDevServerUrl();
			setFatafatServerUI(selectedServerFatafat);
			setAllServersToSame(ProductType.FEED, ConfigMode.DEV);
		});

        relativeLayoutCustomFatafat.setOnClickListener(v -> {
			String customUrl = editTextCustomFatafat.getText().toString().trim();
			if ("".equalsIgnoreCase(customUrl)) {
				editTextCustomFatafat.requestFocus();
				editTextCustomFatafat.setError(getString(R.string.please_enter_something));
			} else {
				selectedServerFatafat = customUrl;
				setFatafatServerUI(selectedServerFatafat);
			}
		});

        editTextCustomFatafat.setOnClickListener(v -> selectedServerFatafat = editTextCustomFatafat.getText().toString().trim());
        
        


        relativeLayoutDebugOptions.setOnClickListener(v -> {
			if(linearLayoutDebugOptionsBelow.getVisibility() == View.VISIBLE){
				imageViewArrowDebugOptions.setRotation(270);
				linearLayoutDebugOptionsBelow.setVisibility(View.GONE);
			} else{
				imageViewArrowDebugOptions.setRotation(90);
				linearLayoutDebugOptionsBelow.setVisibility(View.VISIBLE);
			}
		});
		imageViewArrowDebugOptions.setRotation(270);
		linearLayoutDebugOptionsBelow.setVisibility(View.GONE);


        relativeLayoutServerEnv.setOnClickListener(v -> {
			if(linearLayoutServerEnvBelow.getVisibility() == View.VISIBLE){
				imageViewArrowServerEnv.setRotation(270);
				linearLayoutServerEnvBelow.setVisibility(View.GONE);
			} else{
				imageViewArrowServerEnv.setRotation(90);
				linearLayoutServerEnvBelow.setVisibility(View.VISIBLE);
			}
		});

        relativeLayoutServerEnvFresh.setOnClickListener(v -> {
			if(linearLayoutServerEnvFreshBelow.getVisibility() == View.VISIBLE){
				imageViewArrowServerEnvFresh.setRotation(270);
				linearLayoutServerEnvFreshBelow.setVisibility(View.GONE);
			} else{
				imageViewArrowServerEnvFresh.setRotation(90);
				linearLayoutServerEnvFreshBelow.setVisibility(View.VISIBLE);
			}
		});

        relativeLayoutServerEnvMenus.setOnClickListener(v -> {
			if(linearLayoutServerEnvMenusBelow.getVisibility() == View.VISIBLE){
				imageViewArrowServerEnvMenus.setRotation(270);
				linearLayoutServerEnvMenusBelow.setVisibility(View.GONE);
			} else{
				imageViewArrowServerEnvMenus.setRotation(90);
				linearLayoutServerEnvMenusBelow.setVisibility(View.VISIBLE);
			}
		});

        relativeLayoutServerEnvFatafat.setOnClickListener(v -> {
			if(linearLayoutServerEnvFatafatBelow.getVisibility() == View.VISIBLE){
				imageViewArrowServerEnvFatafat.setRotation(270);
				linearLayoutServerEnvFatafatBelow.setVisibility(View.GONE);
			} else{
				imageViewArrowServerEnvFatafat.setRotation(90);
				linearLayoutServerEnvFatafatBelow.setVisibility(View.VISIBLE);
			}
		});

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

	private void setBoldText(TextView tvDeviceID, String prefix, String value) {
		tvDeviceID.setText(prefix);
		SpannableStringBuilder ssbId = new SpannableStringBuilder(value);
		ssbId.setSpan(new StyleSpan(Typeface.BOLD), 0, ssbId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvDeviceID.append(ssbId);

		SpannableStringBuilder ssb = new SpannableStringBuilder("Long press to copy");
		ssb.setSpan(new RelativeSizeSpan(0.7f), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvDeviceID.append("\n");
		tvDeviceID.append(ssb);
	}

	private void toggleJungleApisDisableUI(int jungleApisDisable, RelativeLayout rlJungleApisDisable, ImageView ivJungleApisDisable) {
		if (jungleApisDisable == 1) {
			rlJungleApisDisable.setBackgroundColor(Color.WHITE);
			ivJungleApisDisable.setImageResource(R.drawable.check_box_checked);
		} else {
			rlJungleApisDisable.setBackgroundColor(Color.TRANSPARENT);
			ivJungleApisDisable.setImageResource(R.drawable.check_box_unchecked);
		}
	}


	public void setAllServersToSame(ProductType productType, ConfigMode configMode){
        if(configMode == ConfigMode.LIVE){
            if(productType != ProductType.AUTO){
                selectedServer = Config.getLiveServerUrl();
                setServerUI(selectedServer);
            }
            if(productType != ProductType.FRESH){
                selectedServerFresh = Config.getFreshLiveServerUrl();
                setFreshServerUI(selectedServerFresh);
            }
            if(productType != ProductType.MENUS){
                selectedServerMenus = Config.getMenusLiveServerUrl();
                setMenusServerUI(selectedServerMenus);
            }
            if(productType != ProductType.FEED){
                selectedServerFatafat = Config.getFatafatLiveServerUrl();
                setFatafatServerUI(selectedServerFatafat);
            }
        } else if(configMode == ConfigMode.DEV){
            if(productType != ProductType.AUTO){
                selectedServer = Config.getDevServerUrl();
                setServerUI(selectedServer);
            }
            if(productType != ProductType.FRESH){
                selectedServerFresh = Config.getFreshDevServerUrl();
                setFreshServerUI(selectedServerFresh);
            }
            if(productType != ProductType.MENUS){
                selectedServerMenus = Config.getMenusDevServerUrl();
                setMenusServerUI(selectedServerMenus);
            }
            if(productType != ProductType.FEED){
                selectedServerFatafat = Config.getFatafatDevServerUrl();
                setFatafatServerUI(selectedServerFatafat);
            }
        }
    }

    public void setServerUI(String selectedServer) {
        relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

        imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
        imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
        imageViewCustom.setImageResource(R.drawable.check_box_unchecked);

        if (selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())) {
            relativeLayoutLive4012.setBackgroundColor(Color.WHITE);
            imageViewLive4012.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getDevServerUrl())) {
            relativeLayoutTest8012.setBackgroundColor(Color.WHITE);
            imageViewTest8012.setImageResource(R.drawable.check_box_checked);
        }
        else {
            relativeLayoutCustom.setBackgroundColor(Color.WHITE);
            imageViewCustom.setImageResource(R.drawable.check_box_checked);
            editTextCustom.setText(selectedServer);
            editTextCustom.setSelection(editTextCustom.getText().toString().length());
        }
    }

    public void setFreshServerUI(String selectedServer) {
        relativeLayoutLiveFresh.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTestFresh.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutCustomFresh.setBackgroundColor(Color.TRANSPARENT);

        imageViewLiveFresh.setImageResource(R.drawable.check_box_unchecked);
        imageViewTestFresh.setImageResource(R.drawable.check_box_unchecked);
        imageViewCustomFresh.setImageResource(R.drawable.check_box_unchecked);

        if (selectedServer.equalsIgnoreCase(Config.getFreshLiveServerUrl())) {
            relativeLayoutLiveFresh.setBackgroundColor(Color.WHITE);
            imageViewLiveFresh.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getFreshDevServerUrl())) {
            relativeLayoutTestFresh.setBackgroundColor(Color.WHITE);
            imageViewTestFresh.setImageResource(R.drawable.check_box_checked);
        }
        else {
            relativeLayoutCustomFresh.setBackgroundColor(Color.WHITE);
            imageViewCustomFresh.setImageResource(R.drawable.check_box_checked);
            editTextCustomFresh.setText(selectedServer);
            editTextCustomFresh.setSelection(editTextCustomFresh.getText().toString().length());
        }
    }


    public void setMenusServerUI(String selectedServer) {
        relativeLayoutLiveMenus.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTestMenus.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutCustomMenus.setBackgroundColor(Color.TRANSPARENT);

        imageViewLiveMenus.setImageResource(R.drawable.check_box_unchecked);
        imageViewTestMenus.setImageResource(R.drawable.check_box_unchecked);
        imageViewCustomMenus.setImageResource(R.drawable.check_box_unchecked);

        if (selectedServer.equalsIgnoreCase(Config.getMenusLiveServerUrl())) {
            relativeLayoutLiveMenus.setBackgroundColor(Color.WHITE);
            imageViewLiveMenus.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getMenusDevServerUrl())) {
            relativeLayoutTestMenus.setBackgroundColor(Color.WHITE);
            imageViewTestMenus.setImageResource(R.drawable.check_box_checked);
        }
        else {
            relativeLayoutCustomMenus.setBackgroundColor(Color.WHITE);
            imageViewCustomMenus.setImageResource(R.drawable.check_box_checked);
            editTextCustomMenus.setText(selectedServer);
            editTextCustomMenus.setSelection(editTextCustomMenus.getText().toString().length());
        }
    }


    public void setFatafatServerUI(String selectedServer) {
        relativeLayoutLiveFatafat.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTestFatafat.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutCustomFatafat.setBackgroundColor(Color.TRANSPARENT);

        imageViewLiveFatafat.setImageResource(R.drawable.check_box_unchecked);
        imageViewTestFatafat.setImageResource(R.drawable.check_box_unchecked);
        imageViewCustomFatafat.setImageResource(R.drawable.check_box_unchecked);

        if (selectedServer.equalsIgnoreCase(Config.getFatafatLiveServerUrl())) {
            relativeLayoutLiveFatafat.setBackgroundColor(Color.WHITE);
            imageViewLiveFatafat.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getFatafatDevServerUrl())) {
            relativeLayoutTestFatafat.setBackgroundColor(Color.WHITE);
            imageViewTestFatafat.setImageResource(R.drawable.check_box_checked);
        }
        else {
            relativeLayoutCustomFatafat.setBackgroundColor(Color.WHITE);
            imageViewCustomFatafat.setImageResource(R.drawable.check_box_checked);
            editTextCustomFatafat.setText(selectedServer);
            editTextCustomFatafat.setSelection(editTextCustomFatafat.getText().toString().length());
        }
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Data.REGISTRATION_COMPLETE.equalsIgnoreCase(intent.getAction())) {
            if (intent.hasExtra(Data.DEVICE_TOKEN)) {
                Utils.showToast(this, "Registration complete = " + intent.getStringExtra(Data.DEVICE_TOKEN));
            } else {
                Utils.showToast(this, "Registration failed" + "");
            }
        } else if (Data.REGISTRATION_FAILED.equalsIgnoreCase(intent.getAction())) {
            if (intent.hasExtra(Data.ERROR)) {
                Utils.showToast(this, "Registration failed = " + intent.getStringExtra(Data.ERROR));
            } else {
                Utils.showToast(this, "Registration failed" + "");
            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
