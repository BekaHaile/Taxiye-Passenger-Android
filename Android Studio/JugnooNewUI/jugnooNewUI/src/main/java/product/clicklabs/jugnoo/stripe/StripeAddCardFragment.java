package product.clicklabs.jugnoo.stripe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.stripe.android.CardUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardNumberEditText;
import com.stripe.android.view.ExpiryDateEditText;
import com.stripe.android.view.StripeEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeAddCardFragment extends Fragment {

    private static final String TAG = StripeAddCardFragment.class.getName();

    @Bind(R.id.btn_add_card)
    Button btnAddCard;
    Stripe stripe;
    @Bind(R.id.edt_card_number)
    CardNumberEditText edtCardNumber;
    @Bind(R.id.edt_date)
    ExpiryDateEditText edtDate;
    @Bind(R.id.edt_cvv)
    StripeEditText edtCvv;
    @Bind(R.id.textViewTitle)
    TextView textViewTitle;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        stripe = new Stripe(getActivity(), BuildConfig.STRIPE_KEY);
        ButterKnife.bind(this, rootView);
        textViewTitle.setTypeface(Fonts.avenirNext(getActivity()));
        updateIcon(null);
        edtCardNumber.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtDate.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtCvv.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i < 4) {
                    String brand =  CardUtils.getPossibleCardType(charSequence.toString());
                    updateIcon(brand);
                    updateCvc(brand);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_add_card)
    public void onViewClicked() {
        String cardNumber = edtCardNumber.getCardNumber();
        int[] cardDate = edtDate.getValidDateFields();
        if (cardNumber == null || cardDate == null || cardDate.length != 2) {
            return;
        }



        Card card = new Card(
                edtCardNumber.getCardNumber(),
                cardDate[0],
                cardDate[1],
                edtCvv.getText().toString()
        );



        if (!card.validateCard()) {
            Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error),Toast.LENGTH_SHORT).show();
            return;
        }

        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server

                        Log.i(TAG, token.getId());
                        Log.i(TAG, token.toString());

                        addCardApi( token);


                    }

                    public void onError(Exception error) {
                        // Show localized error message
                        Log.e(TAG, error.getMessage());
                        Toast.makeText(getContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );


    }

    private void addCardApi(Token token) {
        Map<String,String> params = new HashMap<>();
        params.put("stripe_token",token.getId());
        params.put("last_4",token.getCard().getLast4());
        params.put("brand",token.getCard().getBrand());
        params.put("exp_month",String.valueOf(token.getCard().getExpMonth()));
        params.put("exp_year",String.valueOf(token.getCard().getExpYear()));




//        new ApiCommon<StripeCardResponse>(getActivity()).showLoader(true).execute(params, ApiName.CITY_INFO_API,);
    }


    private void updateCvc(@NonNull @Card.CardBrand String brand) {
        if (Card.AMERICAN_EXPRESS.equals(brand)) {
            edtCvv.setFilters(
                    new InputFilter[] {
                            new InputFilter.LengthFilter(Card.CVC_LENGTH_AMERICAN_EXPRESS)});
            edtCvv.setHint(getString(com.stripe.android.R.string.cvc_amex_hint));
        } else {
            edtCvv.setFilters(
                    new InputFilter[] {new InputFilter.LengthFilter(Card.CVC_LENGTH_COMMON)});
            edtCvv.setHint(getString(com.stripe.android.R.string.cvc_number_hint));
        }
    }

    private @Card.CardBrand String brand;
    private void updateIcon(@Card.CardBrand String brand) {


        if(this.brand!=null && this.brand.equals(brand)){
            return;

        }

        this.brand = brand;
        if (brand==null || Card.UNKNOWN.equals(brand)) {
            Drawable icon  = getResources().getDrawable(com.stripe.android.R.drawable.ic_unknown);
            edtCardNumber.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);
        } else {
            edtCardNumber.setCompoundDrawablesWithIntrinsicBounds(BRAND_RESOURCE_MAP.get(brand),0,0,0);
        }
    }




}
