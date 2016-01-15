package product.clicklabs.jugnoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;


public class DebugOptionsActivity extends BaseActivity {

	RelativeLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;


    RelativeLayout relativeLayoutShowAllDrivers, relativeLayoutShowDriverInfo;
    ImageView imageViewShowAllDrivers, imageViewShowDriverInfo;

    RelativeLayout relativeLayoutLive4012, relativeLayoutTest8012, relativeLayoutTest8013, relativeLayoutTest8014, relativeLayoutTest8015, relativeLayoutCustom;
    ImageView imageViewLive4012, imageViewTest8012, imageViewTest8013, imageViewTest8014, imageViewTest8015, imageViewCustom;
	EditText editTextCustom;

    Button buttonSave, buttonCancel, buttonRefreshGCM;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

    int showAllDriversValue = 0;
    int showDriverInfoValue = 0;

    String selectedServer = Config.getDefaultServerUrl();

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

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        ((TextView) findViewById(R.id.textViewDebugOptions)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewServerEnv)).setTypeface(Fonts.mavenRegular(this));

        relativeLayoutShowAllDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutShowAllDrivers);
        relativeLayoutShowDriverInfo = (RelativeLayout) findViewById(R.id.relativeLayoutShowDriverInfo);

        imageViewShowAllDrivers = (ImageView) findViewById(R.id.imageViewShowAllDrivers);
        imageViewShowDriverInfo = (ImageView) findViewById(R.id.imageViewShowDriverInfo);

        ((TextView) findViewById(R.id.textViewShowAllDrivers)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewShowDriverInfo)).setTypeface(Fonts.mavenLight(this));




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
		editTextCustom = (EditText) findViewById(R.id.editTextCustom); editTextCustom.setTypeface(Fonts.latoRegular(this));


        buttonSave = (Button) findViewById(R.id.buttonSave); buttonSave.setTypeface(Fonts.mavenRegular(this));
        buttonCancel = (Button) findViewById(R.id.buttonCancel); buttonCancel.setTypeface(Fonts.mavenRegular(this));
		buttonRefreshGCM = (Button) findViewById(R.id.buttonRefreshGCM); buttonRefreshGCM.setTypeface(Fonts.mavenRegular(this));


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
				if(!selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())
						&& !selectedServer.equalsIgnoreCase(Config.getDevServerUrl())
						&& !selectedServer.equalsIgnoreCase(Config.getDev1ServerUrl())
						&& !selectedServer.equalsIgnoreCase(Config.getDev2ServerUrl())
						&& !selectedServer.equalsIgnoreCase(Config.getDev3ServerUrl())){
					String customUrl = editTextCustom.getText().toString().trim();
					if("".equalsIgnoreCase(customUrl)){
						editTextCustom.requestFocus();
						editTextCustom.setError("Please enter something");
					}
					else {
						selectedServer = customUrl;
						Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
						Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
						Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
						performBackPressed();
					}
				}
				else{
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_ALL_DRIVERS, showAllDriversValue);
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SHOW_DRIVER_INFO, showDriverInfoValue);
					Prefs.with(DebugOptionsActivity.this).save(SPLabels.SERVER_SELECTED, selectedServer);
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

		buttonRefreshGCM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DebugOptionsActivity.this, RegistrationIntentService.class);
				intent.putExtra(Data.INTENT_CLASS_NAME, DebugOptionsActivity.class.getName());
				startService(intent);
			}
		});



        showAllDriversValue = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfoValue = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);

        selectedServer = Prefs.with(this).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());

        if(showAllDriversValue == 1){
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.WHITE);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_checked);
        }
        else{
            relativeLayoutShowAllDrivers.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowAllDrivers.setImageResource(R.drawable.check_box_unchecked);
        }

        if(showDriverInfoValue == 1){
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked);
        }
        else{
            relativeLayoutShowDriverInfo.setBackgroundColor(Color.TRANSPARENT);
            imageViewShowDriverInfo.setImageResource(R.drawable.check_box_unchecked);
        }

        setServerUI(selectedServer);



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
                if(showDriverInfoValue == 0){
                    showDriverInfoValue = 1;
                    relativeLayoutShowDriverInfo.setBackgroundColor(Color.WHITE);
                    imageViewShowDriverInfo.setImageResource(R.drawable.check_box_checked);
                }
                else{
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
				if("".equalsIgnoreCase(customUrl)){
					editTextCustom.requestFocus();
					editTextCustom.setError("Please enter something");
				}
				else {
					selectedServer = customUrl;
					setServerUI(selectedServer);
				}
			}
		});

		editTextCustom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				relativeLayoutCustom.performClick();
				selectedServer = editTextCustom.getText().toString().trim();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, buttonRefreshGCM.getTop());
					}
				}, 200);
			}
		});

		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {

			}

			@Override
			public void keyBoardClosed() {

			}
		}));



		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}


    public void setServerUI(String selectedServer){
        if(selectedServer.equalsIgnoreCase(Config.getLiveServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_checked);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
			imageViewCustom.setImageResource(R.drawable.check_box_unchecked);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDevServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8012.setImageResource(R.drawable.check_box_checked);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
			imageViewCustom.setImageResource(R.drawable.check_box_unchecked);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDev1ServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8013.setImageResource(R.drawable.check_box_checked);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
			imageViewCustom.setImageResource(R.drawable.check_box_unchecked);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDev2ServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.WHITE);
            relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8014.setImageResource(R.drawable.check_box_checked);
            imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
			imageViewCustom.setImageResource(R.drawable.check_box_unchecked);
        }
        else if(selectedServer.equalsIgnoreCase(Config.getDev3ServerUrl())){
            relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
            relativeLayoutTest8015.setBackgroundColor(Color.WHITE);
			relativeLayoutCustom.setBackgroundColor(Color.TRANSPARENT);

            imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
            imageViewTest8015.setImageResource(R.drawable.check_box_checked);
			imageViewCustom.setImageResource(R.drawable.check_box_unchecked);
        }
		else{
			relativeLayoutLive4012.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutTest8012.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutTest8013.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutTest8014.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutTest8015.setBackgroundColor(Color.TRANSPARENT);
			relativeLayoutCustom.setBackgroundColor(Color.WHITE);

			imageViewLive4012.setImageResource(R.drawable.check_box_unchecked);
			imageViewTest8012.setImageResource(R.drawable.check_box_unchecked);
			imageViewTest8013.setImageResource(R.drawable.check_box_unchecked);
			imageViewTest8014.setImageResource(R.drawable.check_box_unchecked);
			imageViewTest8015.setImageResource(R.drawable.check_box_unchecked);
			imageViewCustom.setImageResource(R.drawable.check_box_checked);

			editTextCustom.setText(selectedServer);
			editTextCustom.setSelection(editTextCustom.getText().toString().length());
		}
    }
    



	
	public void performBackPressed(){
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
		if(Data.REGISTRATION_COMPLETE.equalsIgnoreCase(intent.getAction())){
			if(intent.hasExtra(Data.DEVICE_TOKEN)){
				Toast.makeText(this, "Registration complete = " + intent.getStringExtra(Data.DEVICE_TOKEN), Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(this, "Registration failed"+"", Toast.LENGTH_LONG).show();
			}
		}
		else if(Data.REGISTRATION_FAILED.equalsIgnoreCase(intent.getAction())){
			if(intent.hasExtra(Data.ERROR)){
				Toast.makeText(this, "Registration failed = "+intent.getStringExtra(Data.ERROR), Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(this, "Registration failed"+"", Toast.LENGTH_LONG).show();
			}
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	
}
