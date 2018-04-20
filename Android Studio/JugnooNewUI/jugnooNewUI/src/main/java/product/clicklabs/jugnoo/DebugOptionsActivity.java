package product.clicklabs.jugnoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
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
    TextView textViewScroll;

    int showAllDriversValue = 0;
    int showDriverInfoValue = 0;

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

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


        ((TextView) findViewById(R.id.textViewDebugOptions)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnv)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvFresh)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvMenus)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnvFatafat)).setTypeface(Fonts.mavenRegular(this));


        relativeLayoutDebugOptions = (RelativeLayout) findViewById(R.id.relativeLayoutDebugOptions);
        imageViewArrowDebugOptions = (ImageView) findViewById(R.id.imageViewArrowDebugOptions);
        linearLayoutDebugOptionsBelow = (LinearLayout) findViewById(R.id.linearLayoutDebugOptionsBelow);
        relativeLayoutShowAllDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutShowAllDrivers);
        relativeLayoutShowDriverInfo = (RelativeLayout) findViewById(R.id.relativeLayoutShowDriverInfo);
        imageViewShowAllDrivers = (ImageView) findViewById(R.id.imageViewShowAllDrivers);
        imageViewShowDriverInfo = (ImageView) findViewById(R.id.imageViewShowDriverInfo);
        ((TextView) findViewById(R.id.textViewShowAllDrivers)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewShowDriverInfo)).setTypeface(Fonts.mavenLight(this));



        relativeLayoutServerEnv = (RelativeLayout) findViewById(R.id.relativeLayoutServerEnv);
        imageViewArrowServerEnv = (ImageView) findViewById(R.id.imageViewArrowServerEnv);
        linearLayoutServerEnvBelow = (LinearLayout) findViewById(R.id.linearLayoutServerEnvBelow);
        relativeLayoutLive4012 = (RelativeLayout) findViewById(R.id.relativeLayoutLive4012);
        relativeLayoutTest8012 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8012);
        relativeLayoutCustom = (RelativeLayout) findViewById(R.id.relativeLayoutCustom);
        imageViewLive4012 = (ImageView) findViewById(R.id.imageViewLive4012);
        imageViewTest8012 = (ImageView) findViewById(R.id.imageViewTest8012);
        imageViewCustom = (ImageView) findViewById(R.id.imageViewCustom);
        ((TextView) findViewById(R.id.textViewLive4012)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8012)).setTypeface(Fonts.mavenLight(this));
        editTextCustom = (EditText) findViewById(R.id.editTextCustom);
        editTextCustom.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvFresh = (RelativeLayout) findViewById(R.id.relativeLayoutServerEnvFresh);
        imageViewArrowServerEnvFresh = (ImageView) findViewById(R.id.imageViewArrowServerEnvFresh);
        linearLayoutServerEnvFreshBelow = (LinearLayout) findViewById(R.id.linearLayoutServerEnvFreshBelow);
        relativeLayoutLiveFresh = (RelativeLayout) findViewById(R.id.relativeLayoutLiveFresh);
        relativeLayoutTestFresh = (RelativeLayout) findViewById(R.id.relativeLayoutTestFresh);
        relativeLayoutCustomFresh = (RelativeLayout) findViewById(R.id.relativeLayoutCustomFresh);
        imageViewLiveFresh = (ImageView) findViewById(R.id.imageViewLiveFresh);
        imageViewTestFresh = (ImageView) findViewById(R.id.imageViewTestFresh);
        imageViewCustomFresh = (ImageView) findViewById(R.id.imageViewCustomFresh);
        ((TextView) findViewById(R.id.textViewLiveFresh)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestFresh)).setTypeface(Fonts.mavenLight(this));
        editTextCustomFresh = (EditText) findViewById(R.id.editTextCustomFresh);
        editTextCustomFresh.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvMenus = (RelativeLayout) findViewById(R.id.relativeLayoutServerEnvMenus);
        imageViewArrowServerEnvMenus = (ImageView) findViewById(R.id.imageViewArrowServerEnvMenus);
        linearLayoutServerEnvMenusBelow = (LinearLayout) findViewById(R.id.linearLayoutServerEnvMenusBelow);
        relativeLayoutLiveMenus = (RelativeLayout) findViewById(R.id.relativeLayoutLiveMenus);
        relativeLayoutTestMenus = (RelativeLayout) findViewById(R.id.relativeLayoutTestMenus);
        relativeLayoutCustomMenus = (RelativeLayout) findViewById(R.id.relativeLayoutCustomMenus);
        imageViewLiveMenus = (ImageView) findViewById(R.id.imageViewLiveMenus);
        imageViewTestMenus = (ImageView) findViewById(R.id.imageViewTestMenus);
        imageViewCustomMenus = (ImageView) findViewById(R.id.imageViewCustomMenus);
        ((TextView) findViewById(R.id.textViewLiveMenus)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestMenus)).setTypeface(Fonts.mavenLight(this));
        editTextCustomMenus = (EditText) findViewById(R.id.editTextCustomMenus);
        editTextCustomMenus.setTypeface(Fonts.mavenMedium(this));


        relativeLayoutServerEnvFatafat = (RelativeLayout) findViewById(R.id.relativeLayoutServerEnvFatafat);
        imageViewArrowServerEnvFatafat = (ImageView) findViewById(R.id.imageViewArrowServerEnvFatafat);
        linearLayoutServerEnvFatafatBelow = (LinearLayout) findViewById(R.id.linearLayoutServerEnvFatafatBelow);
        relativeLayoutLiveFatafat = (RelativeLayout) findViewById(R.id.relativeLayoutLiveFatafat);
        relativeLayoutTestFatafat = (RelativeLayout) findViewById(R.id.relativeLayoutTestFatafat);
        relativeLayoutCustomFatafat = (RelativeLayout) findViewById(R.id.relativeLayoutCustomFatafat);
        imageViewLiveFatafat = (ImageView) findViewById(R.id.imageViewLiveFatafat);
        imageViewTestFatafat = (ImageView) findViewById(R.id.imageViewTestFatafat);
        imageViewCustomFatafat = (ImageView) findViewById(R.id.imageViewCustomFatafat);
        ((TextView) findViewById(R.id.textViewLiveFatafat)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTestFatafat)).setTypeface(Fonts.mavenLight(this));
        editTextCustomFatafat = (EditText) findViewById(R.id.editTextCustomFatafat);
        editTextCustomFatafat.setTypeface(Fonts.mavenMedium(this));
        

        relativeLayoutAuto = (RelativeLayout) findViewById(R.id.relativeLayoutAuto);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setTypeface(Fonts.mavenRegular(this));
        buttonSaveTop = (Button) findViewById(R.id.buttonSaveTop);
        buttonSaveTop.setTypeface(Fonts.mavenRegular(this));
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setTypeface(Fonts.mavenRegular(this));


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });


        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
                    }
                } else {
                    Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
                    Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
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
                    SplashNewActivity.allowedAuthChannelsHitOnce = true;
                    performBackPressed();
                }
            }
        });

        buttonSaveTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSave.performClick();
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });



        showAllDriversValue = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfoValue = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);

        selectedServer = Prefs.with(this).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());
        selectedServerFresh = Prefs.with(this).getString(SPLabels.FRESH_SERVER_SELECTED, Config.getFreshDefaultServerUrl());
        selectedServerMenus = Prefs.with(this).getString(SPLabels.MENUS_SERVER_SELECTED, Config.getMenusDefaultServerUrl());
        selectedServerFatafat = Prefs.with(this).getString(SPLabels.FATAFAT_SERVER_SELECTED, Config.getFatafatDefaultServerUrl());

        if (showAllDriversValue == 1) {
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.WHITE);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_checked);
        } else {
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_unchecked);
        }

        if (showDriverInfoValue == 1) {
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked);
        } else {
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_unchecked);
        }

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


        relativeLayoutShowAllDrivers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showAllDriversValue == 0) {
                    showAllDriversValue = 1;
                    relativeLayoutShowAllDrivers.setBackgroundColor(Color.WHITE);
                    imageViewShowAllDrivers.setImageResource(R.drawable.check_box_checked);
                } else {
                    showAllDriversValue = 0;
                    relativeLayoutShowAllDrivers.setBackgroundColor(Color.TRANSPARENT);
                    imageViewShowAllDrivers.setImageResource(R.drawable.check_box_unchecked);
                }
            }
        });


        relativeLayoutShowDriverInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showDriverInfoValue == 0) {
                    showDriverInfoValue = 1;
                    relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
                    imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked);
                } else {
                    showDriverInfoValue = 0;
                    relativeLayoutShowDriverInfo.setBackgroundColor(Color.TRANSPARENT);
                    imageViewShowDriverInfo.setImageResource(R.drawable.check_box_unchecked);
                }
            }
        });


        relativeLayoutLive4012.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getLiveServerUrl();
                setServerUI(selectedServer);
                setAllServersToSame(ProductType.AUTO, ConfigMode.LIVE);
            }
        });

        relativeLayoutTest8012.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDevServerUrl();
                setServerUI(selectedServer);
                setAllServersToSame(ProductType.AUTO, ConfigMode.DEV);
            }
        });

        relativeLayoutCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustom.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustom.requestFocus();
                    editTextCustom.setError(getString(R.string.please_enter_something));
                } else {
                    selectedServer = customUrl;
                    setServerUI(selectedServer);
                }
            }
        });

        editTextCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = editTextCustom.getText().toString().trim();
            }
        });







        relativeLayoutLiveFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFresh = Config.getFreshLiveServerUrl();
                setFreshServerUI(selectedServerFresh);
                setAllServersToSame(ProductType.FRESH, ConfigMode.LIVE);
            }
        });

        relativeLayoutTestFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFresh = Config.getFreshDevServerUrl();
                setFreshServerUI(selectedServerFresh);
                setAllServersToSame(ProductType.FRESH, ConfigMode.DEV);
            }
        });

        relativeLayoutCustomFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustomFresh.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustomFresh.requestFocus();
                    editTextCustomFresh.setError(getString(R.string.please_enter_something));
                } else {
                    selectedServerFresh = customUrl;
                    setFreshServerUI(selectedServerFresh);
                }
            }
        });

        editTextCustomFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFresh = editTextCustomFresh.getText().toString().trim();
            }
        });






        relativeLayoutLiveMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerMenus = Config.getMenusLiveServerUrl();
                setMenusServerUI(selectedServerMenus);
                setAllServersToSame(ProductType.MENUS, ConfigMode.LIVE);
            }
        });

        relativeLayoutTestMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerMenus = Config.getMenusDevServerUrl();
                setMenusServerUI(selectedServerMenus);
                setAllServersToSame(ProductType.MENUS, ConfigMode.DEV);
            }
        });

        relativeLayoutCustomMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustomMenus.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustomMenus.requestFocus();
                    editTextCustomMenus.setError(getString(R.string.please_enter_something));
                } else {
                    selectedServerMenus = customUrl;
                    setMenusServerUI(selectedServerMenus);
                }
            }
        });

        editTextCustomMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerMenus = editTextCustomMenus.getText().toString().trim();
            }
        });



        
        
        relativeLayoutLiveFatafat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFatafat = Config.getFatafatLiveServerUrl();
                setFatafatServerUI(selectedServerFatafat);
                setAllServersToSame(ProductType.FEED, ConfigMode.LIVE);
            }
        });

        relativeLayoutTestFatafat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFatafat = Config.getFatafatDevServerUrl();
                setFatafatServerUI(selectedServerFatafat);
                setAllServersToSame(ProductType.FEED, ConfigMode.DEV);
            }
        });

        relativeLayoutCustomFatafat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustomFatafat.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustomFatafat.requestFocus();
                    editTextCustomFatafat.setError(getString(R.string.please_enter_something));
                } else {
                    selectedServerFatafat = customUrl;
                    setFatafatServerUI(selectedServerFatafat);
                }
            }
        });

        editTextCustomFatafat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFatafat = editTextCustomFatafat.getText().toString().trim();
            }
        });
        
        


        relativeLayoutDebugOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutDebugOptionsBelow.getVisibility() == View.VISIBLE){
                    imageViewArrowDebugOptions.setRotation(270);
                    linearLayoutDebugOptionsBelow.setVisibility(View.GONE);
                } else{
                    imageViewArrowDebugOptions.setRotation(90);
                    linearLayoutDebugOptionsBelow.setVisibility(View.VISIBLE);
                }
            }
        });

        relativeLayoutServerEnv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutServerEnvBelow.getVisibility() == View.VISIBLE){
                    imageViewArrowServerEnv.setRotation(270);
                    linearLayoutServerEnvBelow.setVisibility(View.GONE);
                } else{
                    imageViewArrowServerEnv.setRotation(90);
                    linearLayoutServerEnvBelow.setVisibility(View.VISIBLE);
                }
            }
        });

        relativeLayoutServerEnvFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutServerEnvFreshBelow.getVisibility() == View.VISIBLE){
                    imageViewArrowServerEnvFresh.setRotation(270);
                    linearLayoutServerEnvFreshBelow.setVisibility(View.GONE);
                } else{
                    imageViewArrowServerEnvFresh.setRotation(90);
                    linearLayoutServerEnvFreshBelow.setVisibility(View.VISIBLE);
                }
            }
        });

        relativeLayoutServerEnvMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutServerEnvMenusBelow.getVisibility() == View.VISIBLE){
                    imageViewArrowServerEnvMenus.setRotation(270);
                    linearLayoutServerEnvMenusBelow.setVisibility(View.GONE);
                } else{
                    imageViewArrowServerEnvMenus.setRotation(90);
                    linearLayoutServerEnvMenusBelow.setVisibility(View.VISIBLE);
                }
            }
        });

        relativeLayoutServerEnvFatafat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutServerEnvFatafatBelow.getVisibility() == View.VISIBLE){
                    imageViewArrowServerEnvFatafat.setRotation(270);
                    linearLayoutServerEnvFatafatBelow.setVisibility(View.GONE);
                } else{
                    imageViewArrowServerEnvFatafat.setRotation(90);
                    linearLayoutServerEnvFatafatBelow.setVisibility(View.VISIBLE);
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        startActivity(new Intent(this, SplashNewActivity.class));
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
