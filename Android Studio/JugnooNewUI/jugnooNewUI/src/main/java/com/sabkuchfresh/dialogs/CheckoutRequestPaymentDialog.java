package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.adapters.CheckoutRequestCancellationAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 12/06/17.
 */

public class CheckoutRequestPaymentDialog extends Dialog {

    @Bind(R.id.dismiss_view)
    View dismissView;
    @Bind(R.id.tv_label_request)
    TextView tvLabelRequest;
    @Bind(R.id.tv_label_expiry_time)
    TextView tvLabelExpiryTime;
    @Bind(R.id.layout_payment_status)
    LinearLayout layoutPaymentStatus;
    @Bind(R.id.layout_cancel_payment)
    LinearLayout layoutCancelPayment;
    @Bind(R.id.tv_value_expiry)
    TextView tvValueExpiry;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.recycler_cancellation_reasons)
    RecyclerView recyclerCancellationReasons;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    private long timerStartedAt;
    private long expiryTimeMilliSecs;
    private Activity activity;


    // TODO: 12/06/17 Handle Automatic TimeZone not set case if any


    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {

            if (progressBar.getProgress() < progressBar.getMax()) {
                progressBar.postDelayed(this, 100);
                setTime();
            } else {
                tvValueExpiry.setText(null);
                tvLabelExpiryTime.setText("Your request has expired.");
            }
        }
    };

    @OnClick({R.id.dismiss_view, R.id.btn_back, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dismiss_view:
                if (layoutPaymentStatus.getVisibility() == View.VISIBLE) {
                    layoutPaymentStatus.setVisibility(View.GONE);
                    layoutCancelPayment.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_back:
                if (layoutCancelPayment.getVisibility() == View.VISIBLE) {
                    layoutPaymentStatus.setVisibility(View.VISIBLE);
                    layoutCancelPayment.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_submit:
                if(checkoutRequestCancellationAdapter!=null && checkoutRequestCancellationAdapter.getSelectedItem()!=null){
                    Toast.makeText(activity, "Cancel api because "+ checkoutRequestCancellationAdapter.getSelectedItem(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public interface CheckoutRequestPaymentListener {


        void onCancelAttempt();

        void onExpired();
    }

    public static CheckoutRequestPaymentDialog init(Activity activity) {
        return new CheckoutRequestPaymentDialog(R.style.Feed_Popup_Theme, activity);

    }

    public CheckoutRequestPaymentDialog setData(String amount, long timerStartedAt, int expiryTimeMilliSecs, ArrayList<String> cancellationReasons, CheckoutRequestPaymentListener checkoutRequestPaymentListener) {
        this.timerStartedAt = timerStartedAt;
        this.expiryTimeMilliSecs = expiryTimeMilliSecs;
        progressBar.setMax(expiryTimeMilliSecs);
        tvLabelRequest.setText(activity.getString(R.string.label_checkout_request, amount));
        setTime();
        setCancellationAdapter();
        return this;

    }

    @Override
    public void show() {
        throw new IllegalArgumentException();
    }

    public void showDialog() {
        progressBar.removeCallbacks(updateProgressRunnable);
        startTimer();
        super.show();
    }


    private CheckoutRequestPaymentDialog(@StyleRes int themeResId, Activity context) {
        super(context, themeResId);
        this.activity = context;
        setContentView(R.layout.dialog_checkout_request_payment);
        ButterKnife.bind(this, getWindow().getDecorView());
        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.LEFT | Gravity.TOP;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black background during animation
        window.setAttributes(wlp);


    }

    private void setTime() {


        int timeDiff = (int) (System.currentTimeMillis() - timerStartedAt);
        tvValueExpiry.setText(getTime((int) (expiryTimeMilliSecs - timeDiff) / 1000));
        progressBar.setProgress((timeDiff));


    }

    private void startTimer() {

        progressBar.post(updateProgressRunnable);
    }

    @Override
    public void dismiss() {
        if (progressBar != null) {
            progressBar.removeCallbacks(updateProgressRunnable);
        }
        super.dismiss();
    }

    private String getTime(int seconds) {

        int hours = seconds / 3600;
        int min = seconds / 60 - hours * 60;
        int secs = seconds % 60;

        if (hours > 0)
            return hours == 1 && min == 0 && secs == 0 ? hours + ":" + min + ":" + secs + " hour" : hours + ":" + min + ":" + secs + " hours";
        else if (min > 0)
            return secs <= 0 && min == 1 ? min + ":" + secs + " minute" : min + ":" + secs + " minutes";
        else
            return seconds <= 1 ? secs + " second" : secs + " seconds";


    }

    private CheckoutRequestCancellationAdapter checkoutRequestCancellationAdapter;

    public void setCancellationAdapter() {
        ArrayList<String> reasons = new ArrayList<>();
        reasons.add("Guri Kang!");
        reasons.add("Guri Kang!");
        reasons.add("Guri Kang!");
        reasons.add("Guri Kang!!!!");
        reasons.add("Guri Kang!!!!");
        reasons.add("Guri Kang!!");
        recyclerCancellationReasons.setLayoutManager(new LinearLayoutManager(activity));
        checkoutRequestCancellationAdapter = new CheckoutRequestCancellationAdapter(activity, reasons, recyclerCancellationReasons, btnSubmit);
        recyclerCancellationReasons.setAdapter(checkoutRequestCancellationAdapter);
        btnSubmit.setEnabled(checkoutRequestCancellationAdapter.getSelectedItem() != null);


    }

}
