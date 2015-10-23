package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import product.clicklabs.jugnoo.adapters.SearchListActionsHandler;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by Ankit on 10/7/15.
 */
public class AddPlaceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageView imageViewBack, imageViewSearchCross;
    private LinearLayout root;
    private EditText editTextSearch;
    private ProgressWheel progressBarSearch;
    private NonScrollListView listViewSearch;
    private LinearLayout linearLayoutSearch, linearLayoutScrollSearch;
    private TextView textViewScrollSearch, textViewTitle;
    private Button buttonRemove;
    private String placeName;

	private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        root = (LinearLayout) findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        buttonRemove = (Button)findViewById(R.id.buttonRemove);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);editTextSearch.setTypeface(Fonts.latoRegular(this));



        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        progressBarSearch = (ProgressWheel) findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
        listViewSearch = (NonScrollListView) findViewById(R.id.listViewSearch);
        //linearLayoutSearch = (LinearLayout) findViewById(R.id.linearLayoutSearch);
        imageViewSearchCross = (ImageView) findViewById(R.id.imageViewSearchCross); imageViewSearchCross.setVisibility(View.GONE);
        linearLayoutScrollSearch = (LinearLayout) findViewById(R.id.linearLayoutScrollSearch);
        textViewScrollSearch = (TextView) findViewById(R.id.textViewScrollSearch);


        linearLayoutScrollSearch.getViewTreeObserver().addOnGlobalLayoutListener(new
                KeyboardLayoutListener(linearLayoutScrollSearch, textViewScrollSearch, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {

            }

            @Override
            public void keyBoardClosed() {

            }
        }));

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(placeName.equalsIgnoreCase(SPLabels.ADD_HOME)){
                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_HOME, "");
                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_WORK)){
                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_WORK, "");
                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_GYM)){
                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_GYM, "");
                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_FRIEND)){
                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_FRIEND, "");
                }
                Intent intent=new Intent();
                intent.putExtra("PLACE", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        SearchListAdapter searchListAdapter = new SearchListAdapter(this, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
                new SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        if(text.length() > 0){
                            imageViewSearchCross.setVisibility(View.VISIBLE);
                        }
                        else{
                            imageViewSearchCross.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSearchPost() {
                        progressBarSearch.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
                        Gson gson = new Gson();
                        String strResult = gson.toJson(autoCompleteSearchResult);

                        Log.v("onPlaceClick result ","---> "+strResult);

                        editTextSearch.setText(autoCompleteSearchResult.name+", "+autoCompleteSearchResult.address);
                        Intent intent=new Intent();
                        intent.putExtra("PLACE", strResult);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onPlaceSearchPre() {
                        //progressBarInitialSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        Log.v("Search result ", "---> " + searchResult.getClass().getName());
                    }

                    @Override
                    public void onPlaceSearchError() {
                        //progressBarInitialSearch.setVisibility(View.GONE);
                    }
                });

        listViewSearch.setAdapter(searchListAdapter);

        imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra("requestCode")){
            placeName = getIntent().getStringExtra("requestCode");
            textViewTitle.setText("EDIT "+placeName);
            buttonRemove.setText("REMOVE "+placeName);

            if(!getIntent().getStringExtra("address").equalsIgnoreCase("")){
                Gson gson = new Gson();
                AutoCompleteSearchResult searchResult = gson.fromJson(getIntent().getStringExtra("address"),
                        AutoCompleteSearchResult.class);
                //String searchText =  searchResult.name.replaceAll("[-+.^:&]","");
                editTextSearch.setText(searchResult.name);
                buttonRemove.setVisibility(View.VISIBLE);
            }else {
                buttonRemove.setVisibility(View.GONE);
            }
        }
    }

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

    public void performBackPressed(){
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

	@Override
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

}
