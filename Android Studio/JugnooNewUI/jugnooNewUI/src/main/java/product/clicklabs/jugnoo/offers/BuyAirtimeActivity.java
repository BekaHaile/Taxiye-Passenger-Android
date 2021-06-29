package product.clicklabs.jugnoo.offers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.R;

public class BuyAirtimeActivity extends BaseActivity {

    private ImageView imageViewBack;
    private CardView buyAirtime1, buyAirtime2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_airtime);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        buyAirtime1 = (CardView) findViewById(R.id.buyAirtime1);
        buyAirtime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyAirtime("50");
            }
        });
        buyAirtime2 = (CardView) findViewById(R.id.buyAirtime2);
        buyAirtime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyAirtime("100");
            }
        });

    }

    public void buyAirtime(String amount){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BuyAirtimeActivity.this);
        builder1.setMessage(getString(R.string.are_you_sure_you_want_to_buy_airtime, amount));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //call api
                    }
                });

        builder1.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
