package product.clicklabs.jugnoo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.BlockedDriversAdapter;
import product.clicklabs.jugnoo.adapters.FavouriteDriversAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
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

public class BlockedDriversActivity extends Activity {

    private ArrayList<GetFetchUserDriverResponse> fetchUserDriverListBlocked;
    private GetFavouriteDriver blockedDriver;
    private RecyclerView recycleViewBlocked;
    private ImageView imageViewBack;
    private TextView textViewTitle;
    private LinearLayout linearLayoutNoblockDriver;

    private BlockedDriversAdapter blockedDriversAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_drivers);
        blockedDriver = new GetFavouriteDriver();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        linearLayoutNoblockDriver = (LinearLayout) findViewById(R.id.linearLayoutNoblockDriver);
        linearLayoutNoblockDriver.setVisibility(View.GONE);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        recycleViewBlocked = (RecyclerView) findViewById(R.id.recycleViewBlocked);
        fetchUserDriverListBlocked = new ArrayList<>();

        textViewTitle.setText(getResources().getString(R.string.blocked_driver));
        textViewTitle.setTypeface(Fonts.avenirNext(this));

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        getBlockedFetchDriversData();
    }

    private void getBlockedFetchDriversData() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        new HomeUtil().putDefaultParams(params);
        RestClient.getApiService().getFetchUserDriverMapping(params, new Callback<GetFavouriteDriver>() {
            @Override
            public void success(GetFavouriteDriver jsonObject, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.e("TAG","getBlockedFetchUserDriverMapping response = " + responseStr);
                try{
                    JSONObject jObj = new JSONObject(responseStr);
                    blockedDriver   = jsonObject;
                    if(blockedDriver.getFlag()== ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            if(blockedDriver.getData()!=null&& blockedDriver.getData().size()>0) {
                                if(fetchUserDriverListBlocked!=null && !fetchUserDriverListBlocked.isEmpty()){
                                    fetchUserDriverListBlocked.clear();
                                }
                                for(int i=0;blockedDriver.getData().size()>i;i++){
                                if(blockedDriver.getData().get(i).getType()==0){
                                    fetchUserDriverListBlocked.add(blockedDriver.getData().get(i));
                                }
                                }
                                if(fetchUserDriverListBlocked!=null&&fetchUserDriverListBlocked.size()>0) {
                                    linearLayoutNoblockDriver.setVisibility(View.GONE);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recycleViewBlocked.setLayoutManager(linearLayoutManager);
                                    blockedDriversAdapter = new BlockedDriversAdapter(fetchUserDriverListBlocked, BlockedDriversActivity.this);
                                    recycleViewBlocked.setAdapter(blockedDriversAdapter);
                                }else{
//                                    Toast.makeText(BlockedDriversActivity.this,"No Blocked Drivers",Toast.LENGTH_SHORT).show();
                                    linearLayoutNoblockDriver.setVisibility(View.VISIBLE);
                                }
                            }else{
//                                Toast.makeText(BlockedDriversActivity.this,"No Blocked Driver Found",Toast.LENGTH_SHORT).show();
                                linearLayoutNoblockDriver.setVisibility(View.VISIBLE);
                            }
                    }

                }catch(Exception e){
                    e.printStackTrace();

                }

            }
            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.getMessage().toString());

            }
        });
    }
}

