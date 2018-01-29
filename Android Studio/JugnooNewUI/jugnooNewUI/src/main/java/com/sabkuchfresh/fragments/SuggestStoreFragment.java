package com.sabkuchfresh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.picker.image.model.ImageEntry;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;

/**
 * Created by cl-macmini-01 on 1/23/18.
 */

public class SuggestStoreFragment extends Fragment {

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

    private FreshActivity activity;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;
    private PaySlider paySlider;
    private ArrayList<Object> imageObjectList = new ArrayList<>();
    private ImageCompression imageCompressionTask;
    private SearchResult searchResult;



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggest_store, container, false);
        activity.fragmentUISetup(this);
        ButterKnife.bind(this, rootView);

        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                if (!activity.isDeliveryOpenInBackground()) {
                    activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                }
                activity.llPayViewContainer.setVisibility(View.GONE);

            }

            @Override
            public void keyBoardClosed() {
                if (!activity.isDeliveryOpenInBackground()) {
                    if (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }
                }
                activity.llPayViewContainer.setVisibility(View.VISIBLE);

            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);

        /*final String[] str = {"Report 1", "Report 2", "Report 3", "Report 4", "Report 5"};

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, str);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spCategory.setAdapter(categoriesAdapter);*/
        paySlider = new PaySlider(activity.llPayViewContainer, activity.getString(R.string.add_store), activity.getString(R.string.swipe_right_to_add)) {
            @Override
            public void onPayClick() {
                try {
                    final String businessName = edtBusinessName.getText().toString().trim();
                    if (businessName.length() == 0) {
                        Utils.showToast(activity, activity.getString(R.string.please_enter_business_name));
                        throw new Exception();
                    }
                    if (edtPhone.getText().toString().trim().length() == 0) {
                        Utils.showToast(activity, activity.getString(R.string.please_add_phone_number));
                        throw new Exception();
                    }

                    if (edtBusinessName.getText().toString().trim().length() == 0) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_a_store_address));
                        throw new Exception();
                    }



                    // if we have images to upload attach them with params
                    if(imageObjectList!=null && imageObjectList.size()>0){

                        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

                        //Compress Images if any new added
                        ArrayList<String> imageEntries =null;
                        for(Object image:imageObjectList){
                            if(image instanceof ImageEntry){
                                if(imageEntries==null)
                                    imageEntries= new ArrayList<>();

                                imageEntries.add(((ImageEntry) image).path);
                            }
                        }

                        if(imageEntries!=null){
                            //upload feedback with new Images
                            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                                @Override
                                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                                    if(output!=null){
                                        for(ImageCompression.CompressedImageModel file:output)
                                        {
                                            if(file!=null){
                                                multipartTypedOutput.addPart(Constants.KEY_ORDER_IMAGES,new TypedFile("image/*",file.getFile()));
                                            }
                                        }

                                    }
                                    //place order with images
                                    placeOrderApi(businessName,multipartTypedOutput);
                                }

                                @Override
                                public  void onError(){
                                    DialogPopup.dismissLoadingDialog();

                                }
                            },activity);
                            imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
                        }

                    }
                    else {
                        placeOrderApi(businessName, new MultipartTypedOutput());
                    }

//                    GAUtils.event(activity.getGaCategory(), HOME, ORDER_PLACED);
                } catch (Exception e) {
                    paySlider.setSlideInitial();
                }
            }
        };

        return rootView;
    }

    private void setAddress(SearchResult searchResult) {


        this.searchResult = searchResult;
        tvAddress.setText(searchResult.getAddress());

    }

    private void placeOrderApi(String taskDetails, MultipartTypedOutput multipartTypedOutput) {

    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        activity = (FreshActivity) context;
    }


}
