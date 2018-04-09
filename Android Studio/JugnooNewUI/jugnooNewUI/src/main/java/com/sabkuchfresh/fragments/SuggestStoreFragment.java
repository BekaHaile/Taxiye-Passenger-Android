package com.sabkuchfresh.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.FatafatImageAdapter;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cl-macmini-01 on 1/23/18.
 */

public class SuggestStoreFragment extends Fragment {

    public static final int ID_SELECT_CATEGORY = -1;
    @Bind(R.id.edtBusinessName)
    EditText edtBusinessName;
    @Bind(R.id.tvAddress)
    TextView tvAddress;
    @Bind(R.id.spCategory)
    Spinner spCategory;
    @Bind(R.id.edtPhone)
    EditText edtPhone;
    @Bind(R.id.edtTimings)
    EditText edtTimings;
    @Bind(R.id.cvUploadImages)
    CardView cvUploadImages;
    @Bind(R.id.cvImages)
    CardView cvImages;
    @Bind(R.id.ivUploadImage)
    ImageView ivUploadImage;
    @Bind(R.id.rvImages)
    RecyclerView rvImages;
    @Bind(R.id.edtNotes)
    EditText edtNotes;
    @Bind(R.id.cvNotes)
    CardView cvNotes;
    @Bind(R.id.svSuggestStore)
    ScrollView svSuggestStore;
    @Bind(R.id.llMain)
    LinearLayout llMain;
    @Bind(R.id.svImages)
    HorizontalScrollView svImages;

    private FreshActivity activity;
    private PaySlider paySlider;
    private ArrayList<Object> imageObjectList = new ArrayList<>();
    private ImageCompression imageCompressionTask;
    private SearchResult searchResult;
    public static final int REQUEST_CODE_SELECT_IMAGES= 100;
    private String[] permissionsRequest;
    private Picker picker;
    private int maxNoImages ;
    private FatafatImageAdapter fatafatImageAdapter;
    private List<MenusResponse.Category> categories;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggest_store, container, false);
        activity.fragmentUISetup(this);
        ButterKnife.bind(this, rootView);
        categories = new ArrayList<>();
        categories.add(0,new MenusResponse.Category(ID_SELECT_CATEGORY, getString(R.string.hint_spinner_add_store)));

        List<MenusResponse.Category> merchantCategories = null  ;

        if(Data.getDeliveryCustomerData()!=null){
            merchantCategories =  Data.getDeliveryCustomerData().getMerchantCategoriesList();
        }

       if(merchantCategories!=null){
           categories.addAll(merchantCategories);
       }
        ArrayAdapter<MenusResponse.Category> categoriesAdapter = new ArrayAdapter<MenusResponse.Category>(activity, R.layout.item_spinner_category, categories) {


            @Override
            public boolean isEnabled(int position) {
                return position!=0;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(ContextCompat.getColor(activity,position==0?R.color.text_color_hint_anywhere:R.color.text_color));
                ((TextView) view).setText(categories.get(position).getCategoryName());

                return view;

            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(ContextCompat.getColor(activity,position==0?R.color.text_color_hint_anywhere:R.color.text_color));
                ((TextView) view).setText(categories.get(position).getCategoryName());
                return view;

            }


        };
//        Utils.setMaxHeightToDropDown(spCategory,activity.getResources().getDimensionPixelSize(R.dimen.dp_200));
        spCategory.setAdapter(categoriesAdapter);
        paySlider = new PaySlider(activity.llPayViewContainer, activity.getString(R.string.add_store), activity.getString(R.string.swipe_right_to_add)) {
            @Override
            public void onPayClick() {
                try {
                    final String businessName = edtBusinessName.getText().toString().trim();
                    if (businessName.length() == 0) {
                        Utils.showToast(activity, activity.getString(R.string.please_enter_store_name));
                        throw new Exception();
                    }

                    if (searchResult==null) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_a_store_address));
                        throw new Exception();
                    }

                    if(spCategory.getSelectedItem()==null || !(spCategory.getSelectedItem() instanceof MenusResponse.Category) || ((MenusResponse.Category) spCategory.getSelectedItem()).getId()==ID_SELECT_CATEGORY){
                        Utils.showToast(activity, activity.getString(R.string.please_select_a_category));
                        throw new Exception();
                    }

                    if (!Utils.validPhoneNumber(edtPhone.getText().toString().trim())) {
                        Utils.showToast(activity, activity.getString(R.string.please_add_phone_number));
                        throw new Exception();
                    }








                    // if we have images to upload attach them with params
                    if (imageObjectList != null && imageObjectList.size() > 0) {

                        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

                        //Compress Images if any new added
                        ArrayList<String> imageEntries = null;
                        for (Object image : imageObjectList) {
                            if (image instanceof ImageEntry) {
                                if (imageEntries == null)
                                    imageEntries = new ArrayList<>();

                                imageEntries.add(((ImageEntry) image).path);
                            }
                        }

                        if (imageEntries != null) {
                            //upload feedback with new Images
                            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                                @Override
                                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                                    if (output != null) {
                                        for (ImageCompression.CompressedImageModel file : output) {
                                            if (file != null) {
                                                multipartTypedOutput.addPart(Constants.KEY_IMAGES, new TypedFile("image/*", file.getFile()));
                                            }
                                        }

                                    }
                                    //place order with images
                                    placeOrderApi(multipartTypedOutput);
                                }

                                @Override
                                public void onError() {
                                    DialogPopup.dismissLoadingDialog();

                                }
                            }, activity);
                            imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
                        }

                    } else {
                        placeOrderApi(new MultipartTypedOutput());
                    }

//                    GAUtils.event(activity.getGaCategory(), HOME, ORDER_PLACED);
                } catch (Exception e) {
                    paySlider.setSlideInitial();
                }
            }
        };

        // decide whether to show upload image layout
        if(Data.getDeliveryCustomerData()!=null && Data.getDeliveryCustomerData().getAddStoreImages()>0){
            cvUploadImages.setVisibility(View.VISIBLE);
            maxNoImages = Data.getDeliveryCustomerData().getAddStoreImages();
            cvImages.setVisibility(View.GONE);
            rvImages.setNestedScrollingEnabled(false);
        } else {
            cvUploadImages.setVisibility(View.GONE);
            cvImages.setVisibility(View.GONE);
        }


        return rootView;
    }

    public void setAddress(SearchResult searchResult) {


        this.searchResult = searchResult;
        tvAddress.setText(searchResult.getAddress());

    }

    private void placeOrderApi(MultipartTypedOutput multipartTypedOutput) {

        multipartTypedOutput.addPart("restaurant_name",new TypedString(edtBusinessName.getText().toString()));
        multipartTypedOutput.addPart("restaurant_phone",new TypedString(edtPhone.getText().toString()));
        if(searchResult!=null){
            multipartTypedOutput.addPart("restaurant_address",new TypedString(tvAddress.getText().toString()));
            multipartTypedOutput.addPart("latitude",new TypedString(String.valueOf(searchResult.getLatitude())));
            multipartTypedOutput.addPart("longitude",new TypedString(String.valueOf(searchResult.getLongitude())));
        }

        if(spCategory.getSelectedItem()!=null && spCategory.getSelectedItem() instanceof MenusResponse.Category){
            multipartTypedOutput.addPart("merchant_category_id",new TypedString(String.valueOf(((MenusResponse.Category) spCategory.getSelectedItem()).getId())));
        }else{
            return;
        }
        String timings = edtTimings.getText().toString().trim();
        if(timings.length()>0){
            multipartTypedOutput.addPart("timings",new TypedString(timings));
        }

        String notes = edtNotes.getText().toString().trim();
        if(timings.length()>0){
            multipartTypedOutput.addPart("notes",new TypedString(notes));
        }



        new ApiCommon<>(activity).showLoader(true).execute(multipartTypedOutput, ApiName.SUGGEST_A_STORE, new APICommonCallback<FeedCommonResponse>() {
            @Override
            public boolean onNotConnected() {
                return false;
            }

            @Override
            public boolean onException(Exception e) {
                return false;
            }

            @Override
            public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {
                Utils.showToast(activity,message);
                activity.performBackPressed(false);

            }

            @Override
            public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                return false;
            }

            @Override
            public boolean onFailure(RetrofitError error) {
                return false;
            }

            @Override
            public void onNegativeClick() {

            }

            @Override
            public void onFinish() {
                activity.getHandler().postDelayed(resetSliderRunnable,500);
            }
        });




    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        activity = (FreshActivity) context;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }



    @OnClick({R.id.tvAddress, R.id.llUploadImages, R.id.ivUploadImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvAddress:
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer(), false);
                break;
            case R.id.ivUploadImage:
            case R.id.llUploadImages:
                pickImages();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            activity.fragmentUISetup(this);
            if(getView()!=null){
                spCategory.setEnabled(true);
            }
        }else{
            if(getView()!=null){
                spCategory.setEnabled(false);
            }
        }
        super.onHiddenChanged(hidden);
    }


/***
     * Images Functionality
     *
     */

    /**
     * Allows image selection
     */
    private void pickImages() {

        if(PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED)
        {
            if (permissionsRequest ==null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    permissionsRequest = new String[2];
                    permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    permissionsRequest[1]=Manifest.permission.READ_EXTERNAL_STORAGE;
                } else {
                    permissionsRequest = new String[1];
                    permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
            }

            (SuggestStoreFragment.this).requestPermissions(permissionsRequest, 20);
            return;
        }

        int alreadyPresent = imageObjectList == null ? 0 : imageObjectList.size();
        if(picker ==null){
            picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
        }

        picker.setLimit(maxNoImages -alreadyPresent);
        picker.startActivity(SuggestStoreFragment.this,activity, REQUEST_CODE_SELECT_IMAGES);

    }

    private void setImageAdapter(final ArrayList<Object> objectList) {

        if (objectList == null || objectList.size() == 0) {
            cvImages.setVisibility(View.GONE);
            cvUploadImages.setVisibility(View.VISIBLE);
            return;
        }

        if (fatafatImageAdapter == null) {
            fatafatImageAdapter = new FatafatImageAdapter(activity, objectList, new FatafatImageAdapter.Callback() {
                @Override
                public void onImageClick(Object object) {
                    //View full Image
                }

                @Override
                public void onDelete(Object object) {
                    objectList.remove(object);
                    ivUploadImage.setVisibility(objectList.size()< maxNoImages ?View.VISIBLE:View.GONE);
                    if(objectList.size()==0){
                        cvImages.setVisibility(View.GONE);
                        fatafatImageAdapter =null;
                        cvUploadImages.setVisibility(View.VISIBLE);
                    }
                }
            }, rvImages);
            rvImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            rvImages.setAdapter(fatafatImageAdapter);
        } else {
            fatafatImageAdapter.setList(objectList);
        }

        ivUploadImage.setVisibility(objectList.size()< maxNoImages ?View.VISIBLE:View.GONE);

        if(objectList.size()>0){
            svImages.post(new Runnable() {
                @Override
                public void run() {
                    svImages.fullScroll(View.FOCUS_RIGHT);
                }
            });

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_SELECT_IMAGES && resultCode==RESULT_OK){
            if(data!=null && data.getSerializableExtra("imagesList")!=null)
            {
                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {
                    imageObjectList.addAll(images);
                    cvUploadImages.setVisibility(View.GONE);
                    cvImages.setVisibility(View.VISIBLE);
                    setImageAdapter(imageObjectList);
                }
            }
        }

    }

    private Runnable resetSliderRunnable = new Runnable() {
        @Override
        public void run() {
            paySlider.setSlideInitial();
        }
    };
}
