package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.home.TransactionUtils;

import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Ankit on 10/7/15.
 */
public class AddPlaceActivity extends BaseFragmentActivity {

    private final String TAG = AddPlaceActivity.class.getSimpleName();

    private ImageView imageViewBack;
    private LinearLayout root;
    private TextView textViewTitle;
    private Button buttonRemove;
    private int placeRequestCode;
    private SearchResult searchResult;
    private boolean editThisAddress = false;

    private RelativeLayout relativeLayoutContainer;

    private EditText editTextDeliveryAddress;
    private RelativeLayout relativeLayoutSearch;
    private ImageView imageViewSearchCross;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        root = (LinearLayout) findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.avenirNext(this));
        buttonRemove = (Button)findViewById(R.id.buttonRemove); buttonRemove.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        editTextDeliveryAddress = (EditText) findViewById(R.id.editTextDeliveryAddress);
        editTextDeliveryAddress.setTypeface(Fonts.mavenLight(AddPlaceActivity.this));
        relativeLayoutSearch = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);
        imageViewSearchCross = (ImageView) findViewById(R.id.imageViewSearchCross);
        imageViewSearchCross.setVisibility(View.GONE);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiAddHomeWorkAddress(searchResult, true, 0, editThisAddress, placeRequestCode);
            }
        });

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

        if(getIntent().hasExtra(Constants.KEY_REQUEST_CODE)){
            placeRequestCode = getIntent().getIntExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
            String address = getIntent().getStringExtra(Constants.KEY_ADDRESS);
            if(TextUtils.isEmpty(address)){
                searchResult = null;
                editThisAddress = false;
                if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                    textViewTitle.setText("ADD Home");
                    editTextDeliveryAddress.setHint("Enter Home location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    textViewTitle.setText("ADD Work");
                    editTextDeliveryAddress.setHint("Enter Work location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                    textViewTitle.setText("ADD New Address");
                    editTextDeliveryAddress.setHint("Enter location");
                }
                buttonRemove.setVisibility(View.GONE);
            } else {
                searchResult = new Gson().fromJson(getIntent().getStringExtra(Constants.KEY_ADDRESS), SearchResult.class);
                editThisAddress = true;
                textViewTitle.setText("EDIT "+ searchResult.getName());
                buttonRemove.setText("REMOVE "+ searchResult.getName());
                editTextDeliveryAddress.setHint("Enter " + searchResult.getName().toLowerCase() + " location");
                editTextDeliveryAddress.setText(searchResult.getAddress());
                editTextDeliveryAddress.setSelection(editTextDeliveryAddress.getText().length());
                buttonRemove.setVisibility(View.VISIBLE);
            }
        }


        getTransactionUtils().openDeliveryAddressFragment(this, relativeLayoutContainer);

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
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            Intent intent=new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
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

    public void openMapAddress(Bundle bundle) {
        getTransactionUtils().openMapFragment(this, relativeLayoutContainer, bundle);
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

    public SearchResult getSearchResult(){
        return searchResult;
    }

    public boolean isEditThisAddress() {
        return editThisAddress;
    }

    public Button getButtonRemove() {
        return buttonRemove;
    }

    public ImageView getImageViewSearchCross() {
        return imageViewSearchCross;
    }

    public RelativeLayout getRelativeLayoutSearch() {
        return relativeLayoutSearch;
    }

    private ApiAddHomeWorkAddress apiAddHomeWorkAddress;
    public void hitApiAddHomeWorkAddress(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId,
                                         final boolean editThisAddress, final int placeRequestCode){
        if(apiAddHomeWorkAddress == null){
            apiAddHomeWorkAddress = new ApiAddHomeWorkAddress(this, new ApiAddHomeWorkAddress.Callback() {
                @Override
                public void onSuccess(SearchResult searchResult, String strResult) {
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
}
