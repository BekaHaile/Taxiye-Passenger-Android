package product.clicklabs.jugnoo.rentals.damagereport;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sabkuchfresh.utils.ImageCompression;
import java.util.ArrayList;
import java.util.Objects;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.rentals.qrscanner.ScannerActivity;
import product.clicklabs.jugnoo.utils.DialogPopup;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;

public class DamageReportActivity extends AppCompatActivity implements DamageReportImageAdapter.DamageReportListener {

    public static final int DAMAGE_REPORT_ACTIVITY = 805;

    private final static int REQUEST_CODE_SELECT_IMAGES = 157;
    private final static int MAX_IMAGE_COUNT = 3;


    private RecyclerView recyclerViewDamageReport;
    private ImageView imageViewScan;
    private EditText editTextBikeNumber;
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

        // TODO have to set the number of columns to auto_fit instead of hardCoding

        GridLayoutManager gridLayoutManagerDamageList = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewDamageReport.setLayoutManager(gridLayoutManagerDamageList);

        GridLayoutManager gridLayoutManagerDamageImage = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewDamageImage.setLayoutManager(gridLayoutManagerDamageImage);

        // TODO Values needed to be taken from the backend instead of hardcoding

        Resources res = getBaseContext().getResources();
        String[] damage_kind = res.getStringArray(R.array.damage_kind);
        damageReportAdapter = new DamageReportAdapter();
        recyclerViewDamageReport.setAdapter(damageReportAdapter);

        for (String aDamage_kind : damage_kind) {
            damageReportAdapter.insertItemInList(aDamage_kind);
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
                if (editTextBikeNumber.getText().toString().equals("")) {
                    Toast.makeText(DamageReportActivity.this, getString(R.string.bike_number_required), Toast.LENGTH_SHORT).show();

                } else {

                    //  TODO Send an api to the backend with attached images , bike Number,damages
                    damageReportApi();
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
                    String bikeNumber = extractNumber(result.getContents());
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

            compressImageAndUpload(data);
        } else {
            editTextBikeNumber.requestFocus();
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editTextBikeNumber, 0);
        }

    }

    private void compressImageAndUpload(Intent data) {
        if (data != null && data.getSerializableExtra("imagesList") != null) {
            ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
            final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

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


                if (imageList != null) {
                    //upload feedback with new Images
                    imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(ImageCompression.CompressedImageModel[] output) {

                            if (output != null) {
                                for (ImageCompression.CompressedImageModel file : output) {
                                    if (file != null) {
                                        Log.d("DamageActivityImage", String.valueOf(new TypedFile("image/*", file.getFile())));
                                        multipartTypedOutput.addPart(Constants.KEY_UPDATED_USER_IMAGE, new TypedFile("image/*", file.getFile()));
                                    }
                                }
                                Log.d("DamageActivityImage", String.valueOf(multipartTypedOutput.fileName()));
                            }

//                            //place order with images
//                            updateUserProfileImage(multipartTypedOutput);

                        }

                        @Override
                        public void onError() {
                            DialogPopup.dismissLoadingDialog();

                        }
                    }, this);
                    imageCompressionTask.execute(imageList.toArray(new String[imageList.size()]));
                }
            }
        }
    }


    public String extractNumber(final String result) {
        String bikeNumber;
        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
        } else {
            bikeNumber = "error";
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
        picker.setLimit(MAX_IMAGE_COUNT - imageList.size());
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
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView image = dialog.findViewById(R.id.damage_image);
        Glide.with(this)
                .load(imageList.get(position))
                .into(image);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    void damageReportApi() {

        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

        if (imageList != null) {
            //upload feedback with new Images
            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                @Override
                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                    if (output != null) {
                        for (ImageCompression.CompressedImageModel file : output) {
                            if (file != null) {
                                multipartTypedOutput.addPart("image_damage", new TypedFile("image/*", file.getFile()));
                            }
                        }

                    }

//                            //place order with images
//                            updateUserProfileImage(multipartTypedOutput);

                }

                @Override
                public void onError() {
                    DialogPopup.dismissLoadingDialog();

                }
            }, this);
            imageCompressionTask.execute(imageList.toArray(new String[imageList.size()]));
        } else {

        }

    }
}
