package product.clicklabs.jugnoo.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import product.clicklabs.jugnoo.retrofit.model.DocumentData;
import product.clicklabs.jugnoo.retrofit.model.UploadDocumentResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;

public class DocumentUploadFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_IMAGES = 99;
    private static final int REQ_CODE_IMAGE_PERMISSION = 1001;
    private AccountActivity activity;
    private DocumentData documentData;

    private PermissionCommon mPermissionCommon;
    private Picker picker;
    private ImageCompression imageCompressionTask;
    private ArrayList<Object> imageObjectList = new ArrayList<>();
    private ImageView ivSetCapturedImage, ivSetCapturedImage2, imageViewUploadDoc;
    private ImageView deleteImage1, deleteImage2;
    private RelativeLayout relativeLayoutImageStatus;
    private Button btSubmit;
    private RelativeLayout rlAddImage1, rlAddImage2;
    private TextView tvDocStatus;

    public static DocumentUploadFragment newInstance(DocumentData documentData) {
        DocumentUploadFragment fragment = new DocumentUploadFragment();
        Bundle args = new Bundle();
        args.putParcelable("documentData",documentData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.documentData = getArguments().getParcelable("documentData");
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

        if(documentData.getImagesList()!=null && documentData.getImagesList().size() > 0) {
            imageObjectList.addAll(documentData.getImagesList());
            refreshImageUI();
        }

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
                if(imageObjectList.size() == documentData.getNumImagesRequired()) {
                    activity.onBackPressed();
                } else {
                    Snackbar.make(rootView,getString(R.string.min_documents_required,String.valueOf(documentData.getNumImagesRequired())),Snackbar.LENGTH_SHORT).show();
                }
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

        if (picker == null) {
            picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.SINGLE_IMAGE).build();
        }
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
                    addImage(imageObjectList.size()-1);
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
                if(imageObjectList.get(0) instanceof ImageEntry) {
                    Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(0)).path))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage);
                } else {
                    Picasso.with(activity).load((String) imageObjectList.get(0))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage);
                }
                if(imageObjectList.get(1) instanceof ImageEntry) {
                    Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(1)).path))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage2);
                } else {
                    Picasso.with(activity).load((String) imageObjectList.get(1))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage2);
                }
            } catch (Exception unhandled) {
            }
        } else if (imageObjectList.size() == 1) {
            try {
                rlAddImage1.setVisibility(View.VISIBLE);
                deleteImage1.setVisibility(View.VISIBLE);
                rlAddImage2.setVisibility(View.GONE);
                deleteImage2.setVisibility(View.GONE);
                imageViewUploadDoc.setVisibility(View.VISIBLE);
                if(imageObjectList.get(0) instanceof ImageEntry) {
                    Picasso.with(activity).load(new File(((ImageEntry) imageObjectList.get(0)).path))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage);
                } else {
                    Picasso.with(activity).load((String) imageObjectList.get(0))
                            .transform(new RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage);
                }
            } catch (Exception unhandled) { }
        } else {
            rlAddImage1.setVisibility(View.GONE);
            deleteImage1.setVisibility(View.GONE);
            rlAddImage2.setVisibility(View.GONE);
            deleteImage2.setVisibility(View.GONE);
            imageViewUploadDoc.setVisibility(View.VISIBLE);
        }
        if(documentData.getStatus() == DocStatus.UPLOADED.getI()) {
            tvDocStatus.setText(getString(R.string.uploaded));
        } else if(documentData.getStatus() == DocStatus.REJECTED.getI()) {
            tvDocStatus.setText(getString(R.string.rejected));
        } else if(documentData.getStatus() == DocStatus.APPROVAL_PENDING.getI()) {
            tvDocStatus.setText(getString(R.string.approval_pending));
        } else if(documentData.getStatus() == DocStatus.VERIFIED.getI()) {
            tvDocStatus.setText(getString(R.string.verified));
        }
    }

    public void uploadImage(int imagePosition,int docId,String documentType) {
        ArrayList<String> imageEntries = null;
            if (imageObjectList.get(imagePosition) instanceof ImageEntry) {
                    imageEntries = new ArrayList<>();
                imageEntries.add(((ImageEntry) imageObjectList.get(imagePosition)).path);

        }
        if (imageEntries != null) {
            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                @Override
                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                    MultipartTypedOutput params = new MultipartTypedOutput();
                    if (output != null) {
                        for (ImageCompression.CompressedImageModel file : output) {
                            if (file != null) {
                                    params.addPart(Constants.KEY_IMAGE, new TypedFile(Constants.MIME_TYPE, file.getFile()));
                            }
                        }
                        params.addPart("doc_id", new TypedString(String.valueOf(docId)));
                        params.addPart("img_position",new TypedString(String.valueOf(imagePosition)));
                        new ApiCommon<UploadDocumentResponse>(activity).showLoader(true).putAccessToken(true).execute(params
                                , ApiName.UPLOAD_VERICATION_DOCUMENTS
                                , new APICommonCallback<UploadDocumentResponse>() {
                                    @Override
                                    public void onSuccess(UploadDocumentResponse uploadDocumentResponse, String message, int flag) {
                                            DialogPopup.alertPopup(activity,"",message);
                                    }

                                    @Override
                                    public boolean onError(UploadDocumentResponse uploadDocumentResponse, String message, int flag) {
                                        DialogPopup.alertPopup(activity,"",message);
                                        return false;
                                    }
                                });
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

    public void addImage(final int column) {
        try {

            if (documentData.getStatus() == DocStatus.REJECTED.getI()) {
                DialogPopup.alertPopupTwoButtonsWithListeners(activity,
                        activity.getResources().getString(R.string.rejection_reason),
                        documentData.getReason(),
                        activity.getResources().getString(R.string.upload_again),
                        activity.getResources().getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadImage(column,documentData.getDocumentId(),documentData.getDocumentType());
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, true, true);
            } else {
                uploadImage(column,documentData.getDocumentId(),documentData.getDocumentType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public enum DocStatus{
        UPLOADED(4), REJECTED(2), VERIFIED(3), APPROVAL_PENDING(1) ;
        int i;
        DocStatus(int i){
            this.i = i;
        }

        public int getI(){
            return i;
        }
    }
}