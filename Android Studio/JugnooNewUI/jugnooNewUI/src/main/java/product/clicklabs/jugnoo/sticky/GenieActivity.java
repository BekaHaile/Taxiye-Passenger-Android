package product.clicklabs.jugnoo.sticky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by socomo on 12/10/15.
 */
public class GenieActivity extends Activity {

    private RelativeLayout rv;
    private LinearLayout linearLayoutInner;
    private ImageView imageViewClose;
    private TextView textViewJugnoo, textViewETA, textViewBaseFair, textViewPerKM, textViewWait;

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
    }
}
