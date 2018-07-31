package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sabkuchfresh.adapters.CheckoutRequestCancellationAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 12/06/17.
 */

public class CheckoutRequestPaymentDialog extends Dialog {

    @BindView(R.id.dismiss_view)
    View dismissView;
    @BindView(R.id.tv_label_request)
    TextView tvLabelRequest;
    @BindView(R.id.tv_label_expiry_time)
    TextView tvLabelExpiryTime;
    @BindView(R.id.layout_payment_status)
    LinearLayout layoutPaymentStatus;
    @BindView(R.id.layout_cancel_payment)
    LinearLayout layoutCancelPayment;
    @BindView(R.id.tv_value_expiry)
    TextView tvValueExpiry;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_cancellation_reasons)
    RecyclerView recyclerCancellationReasons;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.btn_try_again)
    Button btnTryAgain;
    private long timerStartedAt;
    private long expiryTimeMilliSecs;
    private Activity activity;
    private ArrayList<String> cancellationReasons;
    private CheckoutRequestPaymentListener checkoutRequestPaymentListener;
    private boolean isTimerExpired;



    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {

            if (progressBar.getProgress() >0) {
                progressBar.postDelayed(this, 100);
                setTime();
            } else {
                isTimerExpired = true;
                tvValueExpiry.setText(null);
                tvLabelExpiryTime.setText(R.string.waiting_for_response);
                btnTryAgain.setVisibility(View.VISIBLE);
                btnTryAgain.setEnabled(false);
                progressBar.setVisibility(View.GONE);
                checkoutRequestPaymentListener.onTimerComplete();

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
                    checkoutRequestPaymentListener.onCancelClick(checkoutRequestCancellationAdapter.getSelectedItem());
//                    Toast.makeText(activity, "Cancel api because "+ checkoutRequestCancellationAdapter.getSelectedItem(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void enableRetryButton() {
        btnTryAgain.setEnabled(true);
        tvLabelExpiryTime.setText(R.string.waiting_for_response);

    }

    public interface CheckoutRequestPaymentListener {


        void onCancelClick(String reason);

        void onTimerComplete();

        void onRetryClick();
    }

    public static CheckoutRequestPaymentDialog init(Activity activity) {
        return new CheckoutRequestPaymentDialog(R.style.Checkout_Icici_Popup_Theme, activity);

    }

    public CheckoutRequestPaymentDialog setData(String amount, long timerStartedAt, long expiryTimeMilliSecs, ArrayList<String> cancellationReasons, CheckoutRequestPaymentListener checkoutRequestPaymentListener, String jugnooVpaHandle) {
        isTimerExpired = false;
        this.timerStartedAt = timerStartedAt;
        this.checkoutRequestPaymentListener = checkoutRequestPaymentListener;
        this.expiryTimeMilliSecs = expiryTimeMilliSecs;
        progressBar.setMax((int)expiryTimeMilliSecs);
        String formattedAmount = null;
        try {
            Double amountInDouble = Double.parseDouble(amount);
            formattedAmount = new DecimalFormat("#.00").format(amountInDouble);
            tvLabelRequest.setText(activity.getString(R.string.label_checkout_request, formattedAmount,jugnooVpaHandle));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            tvLabelRequest.setText(activity.getString(R.string.label_checkout_request, amount,jugnooVpaHandle));

        }
        tvLabelExpiryTime.setText(R.string.label_request_expires_in);
        this.cancellationReasons = cancellationReasons;
        layoutPaymentStatus.setVisibility(View.VISIBLE);
        layoutCancelPayment.setVisibility(View.GONE);
        setTime();
        setCancellationAdapter();
        btnTryAgain.setVisibility(View.GONE);
        btnTryAgain.setEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onViewClicked(dismissView);
                    return true;
                }
                return false;

            }


        });
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
        setCancelable(false);
        wlp.gravity = Gravity.START | Gravity.TOP;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black background during animation
        window.setAttributes(wlp);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setEnabled(false);
                checkoutRequestPaymentListener.onRetryClick();
            }
        });



    }

    private void setTime() {


        int timeDiff = (int) (System.currentTimeMillis() - timerStartedAt);
        tvValueExpiry.setText(getTime(((int) (expiryTimeMilliSecs - timeDiff) / 1000) +1));
        progressBar.setProgress(progressBar.getMax()- (timeDiff));


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
            return hours == 1 && min == 0 && secs == 0 ? hours + ":0" + min + ":" + secs + " hour" : hours + ":" + min + ":0" + secs + " hours";
        else if (min > 0)
            return secs <= 0 && min == 1 ? min  + " minute" :secs>=0 && secs<10? min + ":0" +  secs + " minutes"  :  min + ":" +  secs + " minutes";
        else if (secs > 0)
            return   seconds <= 1 ? secs + " second" : secs + " seconds";
        else
            return null;



    }

    private CheckoutRequestCancellationAdapter checkoutRequestCancellationAdapter;

    public void setCancellationAdapter() {

        recyclerCancellationReasons.setLayoutManager(new LinearLayoutManager(activity));
        checkoutRequestCancellationAdapter = new CheckoutRequestCancellationAdapter(activity, cancellationReasons, recyclerCancellationReasons, btnSubmit,btnBack );
        recyclerCancellationReasons.setAdapter(checkoutRequestCancellationAdapter);
        btnSubmit.setEnabled(checkoutRequestCancellationAdapter.getSelectedItem() != null);
        btnBack.setSelected(btnSubmit.isEnabled());


    }
    public void toggleConnectionState(boolean isAvailable){
        if(isAvailable){
            tvValueExpiry.setVisibility(View.VISIBLE);
            btnTryAgain.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvLabelExpiryTime.setText(R.string.label_request_expired);
        } else{

            tvValueExpiry.setVisibility(View.INVISIBLE);
            btnTryAgain.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tvLabelExpiryTime.setText(R.string.label_waiting_connection);
        }


    }

    public void stopTimer(){
        if(progressBar!=null)
           progressBar.removeCallbacks(updateProgressRunnable);
    }

    public void resumeTimer(){
        if (progressBar!=null) {
            setTime();
            startTimer();
        }
    }

    public boolean isTimerExpired(){
        return isTimerExpired;
    }
}
