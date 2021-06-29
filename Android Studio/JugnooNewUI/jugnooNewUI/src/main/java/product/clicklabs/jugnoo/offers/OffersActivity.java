package product.clicklabs.jugnoo.offers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.utils.Utils;

public class OffersActivity extends BaseActivity {

    private TextView balance;
    private ImageView imageViewBack;
    private CardView buyAirtime, promotions, airtimeHistory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offers);

        balance = (TextView) findViewById(R.id.balance);
        balance.setText(Utils.formatCurrencyValue(Data.autoData.getCurrency(), "604"));

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        buyAirtime = (CardView) findViewById(R.id.buyAirtime);
        buyAirtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, BuyAirtimeActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        promotions = (CardView) findViewById(R.id.promotions);
        promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, PromotionActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        airtimeHistory = (CardView) findViewById(R.id.airtimeHistory);
        airtimeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, AirtimeHistoryActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
    }
}
