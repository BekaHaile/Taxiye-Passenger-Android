package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.CheckoutChargesAdapter;
import com.sabkuchfresh.adapters.FreshCartItemsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by Parminder Saini on 12/06/17.
 */

public class CheckoutPriceMismatchDialog extends Dialog {


    @BindView(R.id.listViewCart)
    NonScrollListView listViewCart;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.listViewCharges)
    NonScrollListView listViewCharges;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.textViewCartItems)
    TextView textViewCartItems;
    private Activity activity;
    private FreshCartItemsAdapter freshCartItemsAdapter;

    public CheckoutPriceMismatchDialogListener getCheckoutPriceMismatchDialogListener() {
        return checkoutPriceMismatchDialogListener;
    }

    private CheckoutPriceMismatchDialogListener checkoutPriceMismatchDialogListener;

    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                checkoutPriceMismatchDialogListener.onCancelClick();
                break;
            case R.id.btn_confirm:
                dismiss();
                checkoutPriceMismatchDialogListener.onSubmitClick();
                break;
        }
    }

    public interface CheckoutPriceMismatchDialogListener {


        void onCancelClick();

        void onSubmitClick();

    }

    public static CheckoutPriceMismatchDialog init(Activity activity) {
        return new CheckoutPriceMismatchDialog(R.style.Checkout_Price_Mismatch_Theme, activity);

    }

    public CheckoutPriceMismatchDialog setData(CheckoutPriceMismatchDialogListener checkoutRequestPaymentListener, FreshCartItemsAdapter freshCartItemsAdapter, CheckoutChargesAdapter checkoutChargesAdapter, int noOfItems) {
        textViewCartItems.setAllCaps(false);
        textViewCartItems.setText(activity.getString(R.string.cart_items_popup_format, String.valueOf(noOfItems)));
        this.freshCartItemsAdapter = freshCartItemsAdapter;
        this.checkoutPriceMismatchDialogListener = checkoutRequestPaymentListener;
        listViewCharges.setAdapter(checkoutChargesAdapter);
        listViewCart.setAdapter(freshCartItemsAdapter);
        return this;

    }

    @Override
    public void show() {
        throw new IllegalArgumentException();
    }

    public void showDialog() {
        if (freshCartItemsAdapter != null)
            freshCartItemsAdapter.notifyDataSetChanged();
        super.show();
    }

    @Override
    public void dismiss() {
        if (freshCartItemsAdapter != null)
            freshCartItemsAdapter.notifyDataSetChanged();
        super.dismiss();
    }

    private CheckoutPriceMismatchDialog(@StyleRes int themeResId, Activity context) {
        super(context, themeResId);
        this.activity = context;
        setContentView(R.layout.dialog_checkout_price_mismatch);
        ButterKnife.bind(this, getWindow().getDecorView());
        ASSL.DoMagic(rootLayout);
        btnCancel.setSelected(true);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.START | Gravity.CENTER;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black background during animation
        window.setAttributes(wlp);


    }

}
