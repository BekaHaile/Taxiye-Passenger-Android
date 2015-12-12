package product.clicklabs.jugnoo.sticky;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;

/**
 * Created by socomo on 12/10/15.
 */
public class GenieActivity extends Activity implements LocationUpdate {

    private RelativeLayout rv;
    private LinearLayout linearLayoutInner;
    private ImageView imageViewClose;
    private TextView textViewJugnoo, textViewETA, textViewBaseFair, textViewPerKM, textViewWait;
    LocationFetcher locationFetcher;
    private ProgressWheel progressWheel;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_genie_layout);
        rv = (RelativeLayout)findViewById(R.id.rv);
        new ASSL(GenieActivity.this, rv, 1134, 720, false);

        linearLayoutInner = (LinearLayout)findViewById(R.id.innerRl);
        imageViewClose = (ImageView)findViewById(R.id.close);
        textViewJugnoo = (TextView)findViewById(R.id.textViewJugnoo);
        textViewETA = (TextView)findViewById(R.id.textViewETA);
        textViewBaseFair = (TextView)findViewById(R.id.textViewBaseFair);
        textViewPerKM = (TextView)findViewById(R.id.textViewPerKM);
        textViewWait = (TextView)findViewById(R.id.textViewWait);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);

        Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(GenieActivity.this);
        if(!"".equalsIgnoreCase(pair.first)){
            accessToken = pair.first;
        }

        String s= "5 \nmins";
        SpannableString ss=  new SpannableString(s);
        ss.setSpan(new RelativeSizeSpan(1.75f), 0, 2, 0); // set size
        //ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
        textViewETA.setText(ss);


        linearLayoutInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent intent = new Intent(GenieActivity.this, SplashNewActivity.class);
                startActivity(intent);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        locationFetcher = new LocationFetcher(this, 1000, 2);

        Log.v("Lat Long","---> "+locationFetcher.getLatitude()+", "+locationFetcher.getLongitude());
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationFetcher.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationFetcher.destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location, int priority) {

        locationFetcher.getLatitude();
        locationFetcher.getLongitude();

    }


}
