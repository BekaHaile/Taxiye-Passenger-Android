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
import android.widget.Toast;

import product.clicklabs.jugnoo.config.Config;
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
    RelativeLayout relativeLayoutLive4012, relativeLayoutTest8012, relativeLayoutTest8013, relativeLayoutTest8014, relativeLayoutTest8015, relativeLayoutCustom;
    ImageView imageViewLive4012, imageViewTest8012, imageViewTest8013, imageViewTest8014, imageViewTest8015, imageViewCustom;
    EditText editTextCustom;

    RelativeLayout relativeLayoutServerEnvFresh;
    ImageView imageViewArrowServerEnvFresh;
    LinearLayout linearLayoutServerEnvFreshBelow;
    RelativeLayout relativeLayoutLiveFresh, relativeLayoutTestFresh, relativeLayoutCustomFresh;
    ImageView imageViewLiveFresh, imageViewTestFresh, imageViewCustomFresh;
    EditText editTextCustomFresh;


    RelativeLayout relativeLayoutAuto;
    Button buttonSave, buttonCancel;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;

    int showAllDriversValue = 0;
    int showDriverInfoValue = 0;

    String selectedServer = Config.getDefaultServerUrl();
    String selectedServerFresh = Config.getFreshDefaultServerUrl();

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
        relativeLayoutTest8013 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8013);
        relativeLayoutTest8014 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8014);
        relativeLayoutTest8015 = (RelativeLayout) findViewById(R.id.relativeLayoutTest8015);
        relativeLayoutCustom = (RelativeLayout) findViewById(R.id.relativeLayoutCustom);
        imageViewLive4012 = (ImageView) findViewById(R.id.imageViewLive4012);
        imageViewTest8012 = (ImageView) findViewById(R.id.imageViewTest8012);
        imageViewTest8013 = (ImageView) findViewById(R.id.imageViewTest8013);
        imageViewTest8014 = (ImageView) findViewById(R.id.imageViewTest8014);
        imageViewTest8015 = (ImageView) findViewById(R.id.imageViewTest8015);
        imageViewCustom = (ImageView) findViewById(R.id.imageViewCustom);
        ((TextView) findViewById(R.id.textViewLive4012)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8012)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8013)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8014)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewTest8015)).setTypeface(Fonts.mavenLight(this));
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



        relativeLayoutAuto = (RelativeLayout) findViewById(R.id.relativeLayoutAuto);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setTypeface(Fonts.mavenRegular(this));
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
                        editTextCustom.setError("Please enter something");
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
                        editTextCustomFresh.setError("Please enter something");
                        stop = true;
                    } else {
                        selectedServerFresh = customUrl;
                        Prefs.with(DebugOptionsActivity.this).save(SPLabels.FRESH_SERVER_SELECTED, selectedServerFresh);
                    }
                } else {
                    Prefs.with(DebugOptionsActivity.this).save(SPLabels.FRESH_SERVER_SELECTED, selectedServerFresh);
                }

                if(!stop){
                    performBackPressed();
                }
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

        imageViewArrowDebugOptions.setRotation(90);
        linearLayoutDebugOptionsBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnv.setRotation(90);
        linearLayoutServerEnvBelow.setVisibility(View.VISIBLE);
        imageViewArrowServerEnvFresh.setRotation(90);
        linearLayoutServerEnvFreshBelow.setVisibility(View.VISIBLE);


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
            }
        });

        relativeLayoutTest8012.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDevServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8013.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDev1ServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8014.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDev2ServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutTest8015.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServer = Config.getDev3ServerUrl();
                setServerUI(selectedServer);
            }
        });

        relativeLayoutCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustom.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustom.requestFocus();
                    editTextCustom.setError("Please enter something");
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
            }
        });

        relativeLayoutTestFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServerFresh = Config.getFreshDevServerUrl();
                setFreshServerUI(selectedServerFresh);
            }
        });

        relativeLayoutCustomFresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = editTextCustomFresh.getText().toString().trim();
                if ("".equalsIgnoreCase(customUrl)) {
                    editTextCustomFresh.requestFocus();
                    editTextCustomFresh.setError("Please enter something");
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    public void setServerUI(String selectedServer) {
        relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
        relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

        imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
        imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
        imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
        imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
        imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
        imageViewCustom.setImageResource(R.drawable.check_box_unchecked);

        if (selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())) {
            relativeLayoutLive4012.setBackgroundColor(Color.WHITE);
            imageViewLive4012.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getDevServerUrl())) {
            relativeLayoutTest8012.setBackgroundColor(Color.WHITE);
            imageViewTest8012.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getDev1ServerUrl())) {
            relativeLayoutTest8013.setBackgroundColor(Color.WHITE);
            imageViewTest8013.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getDev2ServerUrl())) {
            relativeLayoutTest8014.setBackgroundColor(Color.WHITE);
            imageViewTest8014.setImageResource(R.drawable.check_box_checked);
        }
        else if (selectedServer.equalsIgnoreCase(Config.getDev3ServerUrl())) {
            relativeLayoutTest8015.setBackgroundColor(Color.WHITE);
            imageViewTest8015.setImageResource(R.drawable.check_box_checked);
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
                Toast.makeText(this, "Registration complete = " + intent.getStringExtra(Data.DEVICE_TOKEN), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Registration failed" + "", Toast.LENGTH_LONG).show();
            }
        } else if (Data.REGISTRATION_FAILED.equalsIgnoreCase(intent.getAction())) {
            if (intent.hasExtra(Data.ERROR)) {
                Toast.makeText(this, "Registration failed = " + intent.getStringExtra(Data.ERROR), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Registration failed" + "", Toast.LENGTH_LONG).show();
            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
