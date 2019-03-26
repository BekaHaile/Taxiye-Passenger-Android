package product.clicklabs.jugnoo.rentals.qrscanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.rentals.damagereport.DamageReportActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

public class ScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView scannerView;
    private Button flashlightButton;
    private boolean isFlashLightOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerView = findViewById(R.id.scanner);
        scannerView.setTorchListener(this);
        flashlightButton = findViewById(R.id.flashlight_button);
        capture = new CaptureManager(this, scannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        Intent intent = getIntent();
        final int damageReportActivityCode = intent.getIntExtra("DamageReportActivity", -1);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlashlight()) {
            flashlightButton.setVisibility(View.GONE);
        } else {
            flashlightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFlashlight();
                }
            });
        }

        Button buttonNumber = findViewById(R.id.enter_number_button);
        buttonNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Update after adding the damage report activity

                if (damageReportActivityCode == DamageReportActivity.DAMAGE_REPORT_ACTIVITY) {
                    finish();
                } else {
                    EnterQrNumber(ScannerActivity.this);
                }
            }
        });
    }


    // Extracting Bike number from the scan
    public String extractNumber(final String result) {

        Log.d("MainActivity ", result);
        String bikeNumber;
        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
        } else if (result.length() == 11) {
            // TODO apply the check that all 11 digits must be numbers
            bikeNumber = result;
        } else {
            bikeNumber = "error";
        }
        return bikeNumber;
    }


    private boolean hasFlashlight() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (isFlashLightOn) {
            scannerView.setTorchOff();
            isFlashLightOn = false;
        } else {
            scannerView.setTorchOn();
            isFlashLightOn = true;
        }
    }

    @Override
    public void onTorchOn() {
        flashlightButton.setText(R.string.turn_flashlight_off);
    }

    @Override
    public void onTorchOff() {
        flashlightButton.setText(R.string.turn_flashlight_on);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return scannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void EnterQrNumber(final Activity activity) {

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_edittext_confirm);

            RelativeLayout frameLayout = dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);


            TextView textHead = dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            TextView textMessage = dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenLight(activity));
            final EditText editTextNumber = dialog.findViewById(R.id.etCode);
            editTextNumber.setTypeface(Fonts.mavenMedium(activity));

            textHead.setText(R.string.enter_qr_number);
            textMessage.setText(R.string.please_enter_qr_number_to_continue);

            textHead.setVisibility(View.GONE);
            textMessage.setVisibility(View.GONE);
            editTextNumber.setHint(R.string.enter_qr_number);

            final Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
            btnConfirm.setTypeface(Fonts.mavenRegular(activity));
            final String[] bikeNumber = new String[1];

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bikeNumber[0] = editTextNumber.getText().toString();

                    Log.d("HomeActivityResume","QR CODE SCANNER Edittext " + editTextNumber);

                    if (bikeNumber[0].length() < 9) {
                        Toast.makeText(ScannerActivity.this, "QR Number too small", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = getIntent();
                        Log.d("HomeActivityResume","QR CODE SCANNER bike Number" + bikeNumber[0]);
                        intent.putExtra("qrCode", bikeNumber[0]);
                        setResult(RESULT_OK, intent);
                        dialog.dismiss();
                        finish();
                    }
                }

            });

            dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });

            frameLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.showSoftKeyboard(ScannerActivity.this, editTextNumber);
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
