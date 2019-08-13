package product.clicklabs.jugnoo;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.FavouriteDriversAdapter;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.GetFavouriteDriver;
import product.clicklabs.jugnoo.retrofit.model.GetFetchUserDriverResponse;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FavouriteDriversActivity extends Activity {


    private ArrayList<GetFetchUserDriverResponse> fetchUserDriverList;
    private GetFavouriteDriver favouriteDriver;
    private RecyclerView recycleViewFavourite;
    private ImageView imageViewBack;
    private TextView textViewTitle;



    private FavouriteDriversAdapter favouriteDriversAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_drivers);
        favouriteDriver = new GetFavouriteDriver();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        recycleViewFavourite = (RecyclerView) findViewById(R.id.recycleViewFavourite);


        textViewTitle.setText(getResources().getString(R.string.favourite_driver));
        textViewTitle.setTypeface(Fonts.avenirNext(this));

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        getFetchDriversData();
    }

    private void getFetchDriversData() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        new HomeUtil().putDefaultParams(params);
        RestClient.getApiService().getFetchUserDriverMapping(params, new Callback<GetFavouriteDriver>() {
            @Override
            public void success(GetFavouriteDriver jsonObject, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i("TAG","getFetchUserDriverMapping response = " + responseStr);
                try{
                    JSONObject jObj = new JSONObject(responseStr);
                    favouriteDriver   = jsonObject;
                    fetchUserDriverList = favouriteDriver.getData();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recycleViewFavourite.setLayoutManager(linearLayoutManager);
                    favouriteDriversAdapter = new FavouriteDriversAdapter(fetchUserDriverList,FavouriteDriversActivity.this);
                    recycleViewFavourite.setAdapter(favouriteDriversAdapter);



                }catch(Exception e){
                    e.printStackTrace();

                }

            }


            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
