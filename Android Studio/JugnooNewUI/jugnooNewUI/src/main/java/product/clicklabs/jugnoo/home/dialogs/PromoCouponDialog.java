package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsRecyclerAdapter;
import product.clicklabs.jugnoo.utils.ASSL;

public class PromoCouponDialog {
    private static final String TAG = PromoCouponDialog.class.getSimpleName();

    private Activity activity;
    private Callback callback;

    private Dialog dialog = null;
    private PromoCouponsRecyclerAdapter adapter;


    public PromoCouponDialog(final Activity activity, final Callback callback) {

        this.activity = activity;
        this.callback = callback;
    }

    public void show(ArrayList<PromoCoupon> promoCoupons) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        try {

            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
            dialog.setContentView(R.layout.dialog_credit_cards);

            ((TextView) dialog.findViewById(R.id.textViewPayForRides)).setText(activity.getString(R.string.offers_and_coupons));

            RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            final RecyclerView rvCards = dialog.findViewById(R.id.rvCards);
            final TextView tvNoView = dialog.findViewById(R.id.tv_noView);
        //            adapter = new PromoCouponsRecyclerAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, callback, rvCards);
//            rvCards.setLayoutManager(new LinearLayoutManager(activity));
//            rvCards.setAdapter(adapter);
            if (promoCoupons==null || promoCoupons.isEmpty()) {
                rvCards.setVisibility(View.GONE);
                tvNoView.setVisibility(View.VISIBLE);
            } else {
                rvCards.setVisibility(View.VISIBLE);
                tvNoView.setVisibility(View.GONE);
                adapter = new PromoCouponsRecyclerAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, callback, rvCards);
                rvCards.setLayoutManager(new LinearLayoutManager(activity));
                rvCards.setAdapter(adapter);
                adapter.notifyDataSetChanged();
           }

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

	public interface Callback{
		void onCouponSelected();
		PromoCoupon getSelectedCoupon();
		boolean setSelectedCoupon(int position);
		void applyPromoCoupon(String text);

	}
}
