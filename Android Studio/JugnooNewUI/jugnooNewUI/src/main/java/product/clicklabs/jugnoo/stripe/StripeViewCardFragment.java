package product.clicklabs.jugnoo.stripe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stripe.android.model.Card;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeViewCardFragment extends Fragment {

    private static final String TAG = StripeViewCardFragment.class.getName();


    @Bind(R.id.textViewTitle)
    TextView textViewTitle;
    @Bind(R.id.tv_card)
    TextView tvCard;
    private StripeCardData stripeCardData;
    private static final String ARGS_CARD_DATA = "edit_mode";

    public <T extends StripeCardData> StripeAddCardFragment newInstance(T stripeData) {
        StripeAddCardFragment stripeAddCardFragment = new StripeAddCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CARD_DATA, stripeData);
        stripeAddCardFragment.setArguments(bundle);
        return stripeAddCardFragment;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARGS_CARD_DATA)) {
            stripeCardData = getArguments().getParcelable(ARGS_CARD_DATA);
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_card, container, false);
        ButterKnife.bind(this, rootView);
        String formString = stripeCardData.getCardNumber();

        updateIcon(stripeCardData.getBrand());

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private @Card.CardBrand
    String brand;

    private void updateIcon(@Card.CardBrand String brand) {


        if (this.brand != null && this.brand.equals(brand)) {
            return;

        }

        this.brand = brand;
        if (brand == null || Card.UNKNOWN.equals(brand)) {
            Drawable icon = getResources().getDrawable(com.stripe.android.R.drawable.ic_unknown);
            tvCard.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        } else {
            tvCard.setCompoundDrawablesWithIntrinsicBounds(BRAND_RESOURCE_MAP.get(brand), 0, 0, 0);
        }
    }


}
