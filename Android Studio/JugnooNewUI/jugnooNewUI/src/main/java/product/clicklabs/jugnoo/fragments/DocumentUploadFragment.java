package product.clicklabs.jugnoo.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.retrofit.model.UploadDocumentResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;

public class DocumentUploadFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_IMAGES = 99;
    private static final int REQ_CODE_IMAGE_PERMISSION = 1001;
    private AccountActivity activity;
    private String docId;
    private PermissionCommon mPermissionCommon;
    private Picker picker;
    private ImageCompression imageCompressionTask;
    private ArrayList<Object> imageObjectList = new ArrayList<>();
    private int maxNoImages = 2;
    private ImageView ivSetCapturedImage, ivSetCapturedImage2, imageViewUploadDoc;
    private ImageView deleteImage1, deleteImage2;
    private RelativeLayout relativeLayoutImageStatus;
    private Button btSubmit;
    private RelativeLayout rlAddImage1, rlAddImage2;
    private TextView tvDocStatus;

    public static DocumentUploadFragment newInstance(String docId) {
        DocumentUploadFragment fragment = new DocumentUploadFragment();
        Bundle args = new Bundle();
        args.putString("doc_id", docId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docId = getArguments().getString("doc_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_document_upload, container, false);
        mPermissionCommon = new PermissionCommon(this).setCallback(new PermissionCommon.PermissionListener() {
            @Override
            public void permissionGranted(int requestCode) {
                pickImages();
            }

            @Override
            public boolean permissionDenied(int requestCode, boolean neverAsk) {
                return true;
            }

            @Override
            public void onRationalRequestIntercepted(int requestCode) {

            }
        });
        relativeLayoutImageStatus = rootView.findViewById(R.id.relativeLayoutImageStatus);
        ivSetCapturedImage2 = rootView.findViewById(R.id.ivSetCapturedImage2);
        ivSetCapturedImage = rootView.findViewById(R.id.ivSetCapturedImage);
        deleteImage1 = rootView.findViewById(R.id.deleteImage1);
        deleteImage2 = rootView.findViewById(R.id.deleteImage2);
        btSubmit = rootView.findViewById(R.id.btSubmit);
        rlAddImage1 = rootView.findViewById(R.id.rlAddImage1);
        rlAddImage2 = rootView.findViewById(R.id.rlAddImage2);
        tvDocStatus = rootView.findViewById(R.id.tvDocStatus);
        imageViewUploadDoc = rootView.findViewById(R.id.imageViewUploadDoc);

        deleteImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageObjectList.remove(0);
                refreshImageUI();
            }
        });
        deleteImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageObjectList.remove(1);
                refreshImageUI();
            }
        });

        imageViewUploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPermissionCommon.getPermission(REQ_CODE_IMAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountActivity) {
            activity = (AccountActivity) context;
        }
    }

    private void pickImages() {

        int alreadyPresent = imageObjectList == null ? 0 : imageObjectList.size();
        if (picker == null) {
            picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
        }
        picker.setLimit(maxNoImages - alreadyPresent);
        picker.startActivity(DocumentUploadFragment.this, activity, REQUEST_CODE_SELECT_IMAGES);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK) {
            if (data != null && data.getSerializableExtra("imagesList") != null) {
                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {
                    imageObjectList.addAll(images);
                    tvDocStatus.setText(getString(R.string.uploaded));
                    refreshImageUI();
                }
            }
        }
    }

    public void refreshImageUI() {
        if (imageObjectList.size() == 2) {
            try {
                deleteImage1.setVisibility(View.VISIBLE);
                deleteImage2.setVisibility(View.VISIBLE);
                rlAddImage2.setVisibility(View.VISIBLE);
                rlAddImage1.setVisibility(View.VISIBLE);
                imageViewUploadDoc.setVisibility(View.GONE);
                Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(0)).path))
                        .transform(new RoundBorderTransform(6, 0))
                        .resize(300, 300)
                        .centerCrop()
                        .into(ivSetCapturedImage);
                Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(1)).path))
                        .transform(new RoundBorderTransform(6, 0))
                        .resize(300, 300)
                        .centerCrop()
                        .into(ivSetCapturedImage2);
            } catch (Exception unhandled) {
            }
        } else if (imageObjectList.size() == 1) {
            try {
                rlAddImage1.setVisibility(View.VISIBLE);
                deleteImage1.setVisibility(View.VISIBLE);
                rlAddImage2.setVisibility(View.GONE);
                deleteImage2.setVisibility(View.GONE);
                imageViewUploadDoc.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(0)).path))
                        .transform(new RoundBorderTransform(6, 0))
                        .resize(300, 300)
                        .centerCrop()
                        .into(ivSetCapturedImage);
            } catch (Exception unhandled) { }
        } else {
            rlAddImage1.setVisibility(View.GONE);
            deleteImage1.setVisibility(View.GONE);
            rlAddImage2.setVisibility(View.GONE);
            deleteImage2.setVisibility(View.GONE);
            imageViewUploadDoc.setVisibility(View.VISIBLE);
        }
    }

    public void uploadImages() {
        final HashMap<String, String> map = new HashMap<>();
        ArrayList<String> imageEntries = null;
        for (Object image : imageObjectList) {
            if (image instanceof ImageEntry) {
                if (imageEntries == null)
                    imageEntries = new ArrayList<>();

                imageEntries.add(((ImageEntry) image).path);
            }
        }
        if (imageEntries != null) {
            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                @Override
                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                    if (output != null) {
                        for (ImageCompression.CompressedImageModel file : output) {
                            if (file != null) {
                                TypedFile typedFile;
                                typedFile = new TypedFile(Constants.MIME_TYPE, file.getFile());
                                map.put("doc_id", docId);
                                map.put("img_position", String.valueOf(0));
//                                map.put("doc_type_num", String.valueOf(0));
                                map.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                                new ApiCommon<UploadDocumentResponse>(activity).showLoader(true).putDefaultParams(true).execute(typedFile, map
                                        , ApiName.UPLOAD_VERICATION_DOCUMENTS
                                        , new APICommonCallback<UploadDocumentResponse>() {
                                            @Override
                                            public void onSuccess(UploadDocumentResponse uploadDocumentResponse, String message, int flag) {
                                                if (message != null && !TextUtils.isEmpty(message))
                                                    Log.d("message from server", message);
                                            }

                                            @Override
                                            public boolean onError(UploadDocumentResponse uploadDocumentResponse, String message, int flag) {
                                                return false;
                                            }
                                        });
                            }
                        }

                    }

                }

                @Override
                public void onError() {
                    DialogPopup.dismissLoadingDialog();

                }
            }, activity);
            imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
        } else {
            Utils.showToast(activity, activity.getString(R.string.please_upload_images));
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}