package product.clicklabs.jugnoo.rentals.damagereport;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.utils.ImageCompression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.rentals.qrscanner.ScannerActivity;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import product.clicklabs.jugnoo.home.HomeActivity;

public class DamageReportActivity extends AppCompatActivity implements DamageReportImageAdapter.DamageReportListener {

    public static final int DAMAGE_REPORT_ACTIVITY = 805;

    private final static int REQUEST_CODE_SELECT_IMAGES = 157;
//    private final static int MAX_IMAGE_COUNT = 1;


    private RecyclerView recyclerViewDamageReport;
    private ImageView imageViewScan;
    private EditText editTextBikeNumber;
    private EditText editTextDescription;
    private Button reportButton;
    private RecyclerView recyclerViewDamageImage;
    private DamageReportImageAdapter damageReportImageAdapter;
    private DamageReportAdapter damageReportAdapter;
    private ImageView imageViewBack;

    private Picker picker;
    private ImageCompression imageCompressionTask;

    ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damage_report);

        recyclerViewDamageReport = findViewById(R.id.recycler_view_damage_report);
        imageViewScan = findViewById(R.id.image_view_scan);
        editTextBikeNumber = findViewById(R.id.edit_text_bike_number);
        reportButton = findViewById(R.id.button_report);
        recyclerViewDamageImage = findViewById(R.id.recycle_view_damage_image);
        imageViewBack = findViewById(R.id.button_back);
        editTextDescription = findViewById(R.id.edit_view_description);

        GridLayoutManager gridLayoutManagerDamageList = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewDamageReport.setLayoutManager(gridLayoutManagerDamageList);

        GridLayoutManager gridLayoutManagerDamageImage = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewDamageImage.setLayoutManager(gridLayoutManagerDamageImage);

        damageReportAdapter = new DamageReportAdapter();
        recyclerViewDamageReport.setAdapter(damageReportAdapter);


        List<String> damageKind  = Data.autoData.getFaultConditions();


        for (String aDamageKind : damageKind) {
            damageReportAdapter.insertItemInList(aDamageKind);
        }


        damageReportImageAdapter = new DamageReportImageAdapter(this);
        recyclerViewDamageImage.setAdapter(damageReportImageAdapter);

        imageViewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intent = new IntentIntegrator(DamageReportActivity.this).setCaptureActivity(ScannerActivity.class);
                intent.addExtra("DamageReportActivity", DAMAGE_REPORT_ACTIVITY);
                intent.initiateScan();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequiredFields()) {
                    Utils.showToast(DamageReportActivity.this,"All fields should be filled");

                } else {
                    compressImage();
                }
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Request Code for IntentItegrator -> 49374

        if (resultCode == RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String bikeNumber = extractQRCode(result.getContents());
                    if (!bikeNumber.equals("error")) {
                        editTextBikeNumber.setText(bikeNumber);
                    } else {
                        Toast.makeText(this, getString(R.string.incorrect_qr_code), Toast.LENGTH_SHORT).show();
                        editTextBikeNumber.requestFocus();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.scan_not_complete), Toast.LENGTH_SHORT).show();
                    editTextBikeNumber.requestFocus();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK) {
            Log.d("DamageActivityRequest", "OnActivityResult");

            showImage(data);
        } else {
//            editTextBikeNumber.performClick();
//            editTextBikeNumber.requestFocus();
//            try {
//
//                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                keyboard.showSoftInput(editTextBikeNumber, 0);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }

//            editTextBikeNumber.requestFocus();
//            Utils.showSoftKeyboard(DamageReportActivity.this,editTextBikeNumber);

            if (editTextBikeNumber.requestFocus()) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextBikeNumber, InputMethodManager.SHOW_IMPLICIT);
            }

        }

    }


//    private String checkRequiredFields()
//    {
//        String message = "";
//        if(editTextBikeNumber.getText().toString().equals(""))
//        {
//            message = getString(R.string.bike_number_required);
//        }
//        else if(editTextDescription.getText().toString().equals(""))
//        {
//            message = getString(R.string.bike_number_required);
//
//        }
//        else if(imageList.size() == 0)
//        {
//            message = getString(R.string.bike_number_required);
//        }
//        else if(damageReportAdapter.getDamageItemsList().size() == 0)
//        {
//            message = getString(R.string.bike_number_required);
//        }
//        return message;
//
//
//    }


    private boolean checkRequiredFields()
    {
        if(editTextBikeNumber.getText().toString().equals(""))
        {
            return false;
        }
        else if(editTextDescription.getText().toString().equals(""))
        {
            return false;

        }
        else if(imageList.size() == 0)
        {
            return false;
        }
        else if(damageReportAdapter.getDamageItemsList().size() == 0)
        {
            return false;
        }
        return true;

    }


    public String extractQRCode(final String result) {

        String bikeNumber = "";
        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
        } else {
            bikeNumber = result;
        }
        return bikeNumber;
    }


    @Override
    public void selectImage() {
        if (hasPermissions(DamageReportActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            pickImages();
        } else {
            ActivityCompat.requestPermissions(DamageReportActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SELECT_IMAGES);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DamageReportActivity.this, getString(R.string.permissions_required), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            selectImage();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void pickImages() {
        if (picker == null) {
            picker = new Picker.Builder(this, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
        }
        picker.setLimit(damageReportImageAdapter.getMaxNoOfImages() - imageList.size());
        picker.startActivity(this, REQUEST_CODE_SELECT_IMAGES);

    }


    @Override
    public void imageRemoved(ArrayList<String> imageAdapterList) {
        imageList.clear();
        imageList.addAll(imageAdapterList);
    }


    @Override
    public void displayImage(int position) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.damage_image_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView image = dialog.findViewById(R.id.damage_image);
        Glide.with(this)
                .load(imageList.get(position))
                .into(image);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    private void showImage(Intent data) {
        if (data != null && data.getSerializableExtra("imagesList") != null) {
            ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
            if (images != null && images.size() != 0) {

                //Compress Images if any new added
                for (Object image : images) {
                    if (image instanceof ImageEntry) {
                        if (imageList == null)
                            imageList = new ArrayList<>();

                        imageList.add(((ImageEntry) image).path);

                    }
                }

                damageReportImageAdapter.clearList();
                damageReportImageAdapter.insertImageInList(imageList);
            }

        }
    }

    private void compressImage() {
        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        if (imageList != null && imageList.size() > 0) {
            //upload feedback with new Images
            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                @Override
                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                    if (output != null) {
                        for (ImageCompression.CompressedImageModel file : output) {
                            if (file != null) {
                                multipartTypedOutput.addPart(Constants.KEY_DAMAGE_IMAGE, new TypedFile("image/*", file.getFile()));
                            }
                        }
                    }

                    damageReportApi(multipartTypedOutput);
                }
                @Override
                public void onError() {
                    DialogPopup.dismissLoadingDialog();
                }
            }, this);
            imageCompressionTask.execute(imageList.toArray(new String[imageList.size()]));
        }
        else{
            multipartTypedOutput.addPart(Constants.KEY_DAMAGE_IMAGE,new TypedString(""));
            damageReportApi(multipartTypedOutput);
        }



    }

    private void damageReportApi(final MultipartTypedOutput multipartTypedOutput)
    {

        List<String> damageListItems = damageReportAdapter.getDamageItemsList();
        StringBuilder damageListItemString = new StringBuilder();

        for (int i = 0; i < damageListItems.size(); i++) {
            damageListItemString.append(damageListItems.get(i));
            if (i == damageListItems.size() - 1) {
                break;
            }
            damageListItemString.append(";;;");

        }

        int regionId = 0;
        List<Region> region = Data.autoData.getRegions();

        for(int i = 0;i < region.size();i++)
        {
            if(region.get(i).getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal())
            {
                regionId = region.get(i).getRegionId();
            }
        }


        multipartTypedOutput.addPart(Constants.KEY_FAULT_CONDITION, new TypedString(damageListItemString.toString()));
        multipartTypedOutput.addPart(Constants.KEY_DESCRIPTION, new TypedString(editTextDescription.getText().toString()));
        multipartTypedOutput.addPart(Constants.KEY_LATITUDE,new TypedString(String.valueOf(HomeActivity.myLocation.getLatitude())));
        multipartTypedOutput.addPart(Constants.KEY_LONGITUDE,new TypedString(String.valueOf(HomeActivity.myLocation.getLongitude())));
        multipartTypedOutput.addPart(Constants.KEY_REGION_ID,new TypedString(String.valueOf(regionId)));
        multipartTypedOutput.addPart(Constants.KEY_EXTERNAL_ID,new TypedString(editTextBikeNumber.getText().toString()));

        // todo address, location_id

        multipartTypedOutput.addPart(Constants.KEY_ADDRESS,new TypedString("0"));
        multipartTypedOutput.addPart(Constants.KEY_LOCATION_ID,new TypedString("1"));





        new ApiCommon<>(this).showLoader(true).putAccessToken(true)
                .execute(multipartTypedOutput, ApiName.RENTALS_INSERT_DAMAGE_REPORT, new APICommonCallback<FeedCommonResponse>() {
                    @Override
                    public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        Toast.makeText(DamageReportActivity.this,message,Toast.LENGTH_LONG).show();
                        finish();
                        Log.d("DamageReportRental", " Success");

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        Log.d("DamageReportRental", " Failure");
                        //  DialogPopup.dialogRentalLock(HomeActivity.this);
                        return false;
                    }
                });
    }

}
