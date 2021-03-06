package product.clicklabs.jugnoo.stripe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.feed.ui.api.APICommonCallback;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;

import static com.stripe.android.model.Card.AMERICAN_EXPRESS;
import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;
import static com.stripe.android.model.Card.MASTERCARD;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeAddCardFragment extends Fragment {

    private static final String TAG = StripeAddCardFragment.class.getName();

    @BindView(R.id.btn_add_card)
    Button btnAddCard;
    Stripe stripe;
    @BindView(R.id.edt_card_number)
    CardNumberEditText edtCardNumber;
    @BindView(R.id.edt_date)
    ExpiryDateEditText edtDate;
    @BindView(R.id.edt_cvv)
    StripeEditText edtCvv;
    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.edtCardHolderName)
    EditText edtCardHolderName;

    private StripeCardsStateListener stripeCardsStateListener;

    private Unbinder unbinder;
    private boolean isCardNameMandatory;

    private static final String ARGS_PAYMENT_MODE = "args_payment_mode";
    private PaymentOption paymentOption ;
    public static final String BRAND_ACCEPTACARD_MASTERCARD="Mastercard";
    public static final String BRAND_ACCEPTACARD_AMERICAN_EXPRESS="Amex";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StripeCardsStateListener){
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }

    }

    @SuppressLint("ValidFragment")
    private StripeAddCardFragment(){

    }

    public static StripeAddCardFragment newInstance(PaymentOption paymentOption){

        StripeAddCardFragment stripeAddCardFragment = new StripeAddCardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGS_PAYMENT_MODE,paymentOption);
        stripeAddCardFragment.setArguments(bundle);
        return stripeAddCardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null && getArguments().containsKey(ARGS_PAYMENT_MODE)){

            paymentOption = (PaymentOption) getArguments().getSerializable(ARGS_PAYMENT_MODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        stripe = new Stripe(getActivity(), Config.getServerUrl().equals(Config.getLiveServerUrl())?
                Prefs.with(getActivity()).getString(Constants.KEY_STRIPE_KEY_LIVE, BuildConfig.STRIPE_KEY_LIVE)
                :BuildConfig.STRIPE_KEY_DEV);
        isCardNameMandatory = paymentOption==PaymentOption.ACCEPT_CARD;
        ButterKnife.setDebug(true);
        unbinder = ButterKnife.bind(this, rootView);
        edtCardHolderName.setVisibility(isCardNameMandatory?View.VISIBLE:View.GONE);
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
        if(unbinder!=null)unbinder.unbind();
        }

    @OnClick({R.id.imageViewBack, R.id.btn_add_card})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_add_card:
                String cardNumber = edtCardNumber.getCardNumber();
                int[] cardDate = edtDate.getValidDateFields();
                final String nameOnCard = edtCardHolderName.getText().toString().trim();

                if(isCardNameMandatory){
                    if(TextUtils.isEmpty(nameOnCard)){
                        Toast.makeText(getActivity(),getString(R.string.card_name_not_valid),Toast.LENGTH_SHORT).show();
                        return;
                    }
              }

                if(cardNumber==null){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.card_number)),Toast.LENGTH_SHORT).show();
                    return;

                }


                if (cardDate == null || cardDate.length != 2) {
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.expiry_date)),Toast.LENGTH_SHORT).show();
                    return;
                }


                Card card = new Card(
                        edtCardNumber.getCardNumber(),
                        cardDate[0],
                        cardDate[1],
                        edtCvv.getText().toString()
                );

                if(!card.validateNumber()){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.card_number)),Toast.LENGTH_SHORT).show();
                    return;

                }

                if(!card.validateExpiryDate()){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.expiry_date)),Toast.LENGTH_SHORT).show();
                    return;

                }


                if (!card.validateCVC()) {
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.cvc)),Toast.LENGTH_SHORT).show();
                    return;
                }

                Utils.hideSoftKeyboard(getActivity(),edtCardNumber);
                DialogPopup.showLoadingDialog(getActivity(),getString(R.string.loading));

                if(paymentOption.getOrdinal()==PaymentOption.STRIPE_CARDS.getOrdinal()){
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to  server


                                    DialogPopup.dismissLoadingDialog();
                                    addCardApi(token.getCard(),token.getId(),nameOnCard);


                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    Log.e(TAG, error.getMessage());
                                    DialogPopup.dismissLoadingDialog();
                                    Toast.makeText(getContext(),
                                            error.getLocalizedMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );
                }else{
                    addCardApi(card,null,nameOnCard);
                }

                break;
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            default:
                break;



        }



    }

    private void addCardApi(Card token,@Nullable  String tokenId,String nameOnCard) {
        HashMap<String,String> params = new HashMap<>();
        if(tokenId!=null){
            params.put("stripe_token",tokenId);
         }
        params.put("last_4",token.getLast4());
        params.put("card_number",token.getNumber());
        params.put("brand",formatBrand(token.getBrand()));
        params.put("exp_month",formatExpMonth(token.getExpMonth()));
        params.put("exp_year",String.valueOf(token.getExpYear()));
        params.put("is_delete","0");
        params.put("payment_option",String.valueOf(paymentOption.getOrdinal()));
        params.put("name_on_card", nameOnCard);
        params.put("cvv", token.getCVC());




        new ApiCommon<StripeCardResponse>(getActivity()).showLoader(true).execute(params, ApiName.ADD_CARD_API, new APICommonCallback<StripeCardResponse>() {
            @Override
            public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {

                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(stripeCardResponse.getStripeCardData(),message,true,paymentOption);
                    for(StripeCardData scd : stripeCardResponse.getStripeCardData()){
                        if(scd.getLast4().equalsIgnoreCase(token.getLast4())){
                            Prefs.with(requireActivity()).save(Constants.STRIPE_SELECTED_POS, scd.getCardId());
                            break;
                        }
                    }
                }
                if(Data.userData != null
                        && Prefs.with(requireActivity()).getInt(Constants.KEY_CUSTOMER_SETTLE_DEBT_AFTER_ADD_CARD, 0) == 1) {
                    new UserDebtDialog(requireActivity(), Data.userData, new UserDebtDialog.Callback() {
                        @Override
                        public void successFullyDeducted(double userDebt) {

                        }
                    }).settleUserDebt(requireActivity(), false);
                }
             }

            @Override
            public boolean onError(StripeCardResponse stripeCardResponse, String message, int flag) {
                return false;
            }
        });
    }

    private String formatBrand(String brand) {
        if(brand==null || paymentOption.getOrdinal()!=PaymentOption.ACCEPT_CARD.getOrdinal()){
            return brand;

        }

        if(brand.equals(AMERICAN_EXPRESS)){
            return BRAND_ACCEPTACARD_AMERICAN_EXPRESS;
        }

        if(brand.equals(MASTERCARD)){
            return BRAND_ACCEPTACARD_MASTERCARD;
        }

        return brand;

    }

    private String formatExpMonth(Integer expMonth) {

        if(expMonth==null){
            return "";
        }

        if(expMonth<10){
            return "0"+expMonth;
        }else{
            return String.valueOf(expMonth);
        }
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
