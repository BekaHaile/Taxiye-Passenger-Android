package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.home.TransactionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Ankit on 10/7/15.
 */
public class AddPlaceActivity extends BaseFragmentActivity {

    private final String TAG = AddPlaceActivity.class.getSimpleName();

    private ImageView imageViewBack, imageViewDelete;
    private LinearLayout root;
    private TextView textViewTitle;
    private int placeRequestCode;
    private SearchResult searchResult;
    private boolean editThisAddress = false;

    private RelativeLayout relativeLayoutContainer;

    private EditText editTextDeliveryAddress;
    private TextView tvDeliveryAddress;
    private RelativeLayout relativeLayoutSearch;
    private ImageView imageViewSearchCross;
    private ProgressWheel progressWheelDeliveryAddressPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        root = (LinearLayout) findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewDelete = (ImageView) findViewById(R.id.imageViewDelete);
        imageViewDelete.setVisibility(View.GONE);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        editTextDeliveryAddress = (EditText) findViewById(R.id.editTextDeliveryAddress);
        editTextDeliveryAddress.setTypeface(Fonts.mavenLight(AddPlaceActivity.this));
        tvDeliveryAddress = (TextView) findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setVisibility(View.GONE);
        relativeLayoutSearch = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);
        imageViewSearchCross = (ImageView) findViewById(R.id.ivDeliveryAddressCross);
        imageViewSearchCross.setVisibility(View.GONE);
        progressWheelDeliveryAddressPin = (ProgressWheel) findViewById(R.id.progressWheelDeliveryAddressPin);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDeliveryAddress.setText("");
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopup.alertPopupTwoButtonsWithListeners(AddPlaceActivity.this, "",
                        getString(R.string.address_delete_confirm_message),
                        getString(R.string.delete), getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hitApiAddHomeWorkAddress(getSearchResult(), true, 0, isEditThisAddress(), getPlaceRequestCode());
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, false, false);
            }
        });

        if(getIntent().hasExtra(Constants.KEY_REQUEST_CODE)){
            placeRequestCode = getIntent().getIntExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
            String address = getIntent().getStringExtra(Constants.KEY_ADDRESS);
            if(TextUtils.isEmpty(address)){
                searchResult = null;
                editThisAddress = false;
                if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                    textViewTitle.setText(R.string.add_home);
                    editTextDeliveryAddress.setHint(R.string.enter_home_location);
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    textViewTitle.setText(R.string.add_work);
                    editTextDeliveryAddress.setHint(R.string.enter_work_location);
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                    textViewTitle.setText(R.string.add_new_address);
                    editTextDeliveryAddress.setHint(R.string.enter_location);
                }
            } else {
                searchResult = new Gson().fromJson(getIntent().getStringExtra(Constants.KEY_ADDRESS), SearchResult.class);
                editThisAddress = true;
                if(!searchResult.isRecentAddress()){
                    textViewTitle.setText(getString(R.string.edit_format, searchResult.getName()));
                    editTextDeliveryAddress.setHint(getString(R.string.enter_location_format, searchResult.getName().toLowerCase()));
                    editTextDeliveryAddress.setText(searchResult.getAddress());
                    editTextDeliveryAddress.setSelection(editTextDeliveryAddress.getText().length());
                } else {
                    textViewTitle.setText(R.string.add_new_address);
                    editTextDeliveryAddress.setHint(R.string.enter_location);
                }
            }
        }

        if(getIntent().getBooleanExtra(Constants.KEY_DIRECT_CONFIRM, false)){
            openAddToAddressBook(getAddressBundle(searchResult));
        } else {
			openDeliveryAddressFragment();
		}
//        if(editThisAddress){
//            openAddToAddressBook(getAddressBundle(searchResult));
//        } else {
//            getTransactionUtils().openDeliveryAddressFragment(this, relativeLayoutContainer);
//        }

    }

	public void openDeliveryAddressFragment() {
		getTransactionUtils().openDeliveryAddressFragment(this, relativeLayoutContainer, false);
	}

	@Override
    protected void onResume() {
        super.onResume();
    }

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

    public void performBackPressed(){
        if(getTopFragment() instanceof DeliveryAddressesFragment && getDeliveryAddressesFragment().backWasConsumed()){
            return;
        }

        final AddToAddressBookFragment fragment = getAddToAddressBookFragment();
        if(fragment != null && fragment.locationEdited){
            DialogPopup.alertPopupTwoButtonsWithListeners(AddPlaceActivity.this, "",
                    getString(R.string.changes_not_updated_exit),
                    getString(R.string.ok), getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fragment.locationEdited = false;
                            performBackPressed();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false, false);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            Intent intent=new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
        }

    }

    public AddToAddressBookFragment getAddToAddressBookFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        if(fragmentTag.equalsIgnoreCase(AddToAddressBookFragment.class.getName())){
            return (AddToAddressBookFragment) fragmentManager.findFragmentByTag(fragmentTag);
        } else{
            return null;
        }
    }

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
		System.gc();
	}


    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public EditText getEditTextDeliveryAddress() {
        return editTextDeliveryAddress;
    }

    private TransactionUtils transactionUtils;

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public void openAddToAddressBook(Bundle bundle) {
        getTransactionUtils().openAddToAddressFragment(this, relativeLayoutContainer, bundle);
    }

    public DeliveryAddressesFragment getDeliveryAddressesFragment() {
        return (DeliveryAddressesFragment) getSupportFragmentManager().findFragmentByTag(DeliveryAddressesFragment.class.getName());
    }

    public int getPlaceRequestCode(){
        return placeRequestCode;
    }

    public void setPlaceRequestCode(int placeRequestCode){
        this.placeRequestCode = placeRequestCode;
    }

    public SearchResult getSearchResult(){
        return searchResult;
    }

    public boolean isEditThisAddress() {
        return editThisAddress;
    }

    public ImageView getImageViewSearchCross() {
        return imageViewSearchCross;
    }

    public RelativeLayout getRelativeLayoutSearch() {
        return relativeLayoutSearch;
    }

    public ImageView getImageViewDelete(){
        return imageViewDelete;
    }

    private ApiAddHomeWorkAddress apiAddHomeWorkAddress;
    public void hitApiAddHomeWorkAddress(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId,
                                         final boolean editThisAddress, final int placeRequestCode){
        if(apiAddHomeWorkAddress == null){
            apiAddHomeWorkAddress = new ApiAddHomeWorkAddress(this, new ApiAddHomeWorkAddress.Callback() {
                @Override
                public void onSuccess(SearchResult searchResult, String strResult, boolean addressDeleted) {
                    try {
                        Fragment deliveryAddressesFragment = getDeliveryAddressesFragment();
                        if(deliveryAddressesFragment != null) {
							getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent();
                    intent.putExtra("PLACE", strResult);
                    if("".equalsIgnoreCase(strResult)) {
                        setResult(RESULT_CANCELED, intent);
                    } else{
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {

                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        apiAddHomeWorkAddress.addHomeAndWorkAddress(searchResult, deleteAddress, matchedWithOtherId, editThisAddress, placeRequestCode);
    }


    private Bundle getAddressBundle(SearchResult searchResult){
        try {
            String current_street = "";
            String current_route = "";
            String current_area = "";
            String current_city = "";
            String current_pincode = "";

            double current_latitude = searchResult.getLatLng().latitude;
            double current_longitude = searchResult.getLatLng().longitude;

            String[] address = searchResult.getAddress().split(",");
            List<String> addressArray = Arrays.asList(address);
            Collections.reverse(addressArray);
            address = (String[]) addressArray.toArray();

            if(address.length > 0 && (!TextUtils.isEmpty(address[0].trim())))
                current_pincode = "" + address[0].trim();
            if(address.length > 1 && (!TextUtils.isEmpty(address[1].trim())))
                current_city = "" + address[1].trim();
            if(address.length > 2 && (!TextUtils.isEmpty(address[2].trim())))
                current_area = "" + address[2].trim();

            if(address.length > 3 && (!TextUtils.isEmpty(address[3].trim())))
                current_route = "" + address[3].trim();

            if(address.length > 4 && (!TextUtils.isEmpty(address[4].trim())))
                current_street = "" + address[4].trim();

            if(address.length > 5){
                current_street = "";
                for(int i=address.length-1; i > 3; i--){
                    if(current_street.equalsIgnoreCase("")){
                        current_street = address[i].trim();
                    } else{
                        current_street = current_street+", "+address[i].trim();
                    }
                }
            }

            Bundle bundle = new Bundle();
            bundle.putString("current_street", current_street);
            bundle.putString("current_route", current_route);
            bundle.putString("current_area", current_area);
            bundle.putString("current_city", current_city);
            bundle.putString("current_pincode", current_pincode);
            bundle.putDouble("current_latitude", current_latitude);
            bundle.putDouble("current_longitude", current_longitude);
            bundle.putString(Constants.KEY_PLACEID, searchResult.getPlaceId());
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Bundle();
    }

    public ProgressWheel getProgressWheelDeliveryAddressPin() {
        return progressWheelDeliveryAddressPin;
    }

    public TextView getTvDeliveryAddress(){
        return tvDeliveryAddress;
    }

    public Fragment getTopFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
