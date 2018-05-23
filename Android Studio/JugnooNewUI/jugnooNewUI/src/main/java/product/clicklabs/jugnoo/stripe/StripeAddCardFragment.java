package product.clicklabs.jugnoo.stripe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardNumberEditText;
import com.stripe.android.view.ExpiryDateEditText;
import com.stripe.android.view.StripeEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeAddCardFragment extends Fragment {


    @Bind(R.id.btn_add_card)
    Button btnAddCard;
    Stripe stripe;


    private static final String TAG = StripeAddCardFragment.class.getName();
    @Bind(R.id.edt_card_number)
    CardNumberEditText edtCardNumber;
    @Bind(R.id.edt_date)
    ExpiryDateEditText edtDate;
    @Bind(R.id.edt_cvv)
    StripeEditText edtCvv;

    private static final String ARGS_EDIT_MODE = "edit_mode";
    private boolean editMode = false;
    public StripeAddCardFragment newInstance(Bundle bundle){
        StripeAddCardFragment stripeAddCardFragment = new StripeAddCardFragment();
        stripeAddCardFragment.setArguments(bundle);
        return stripeAddCardFragment;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editMode = getArguments()!=null && getArguments().containsKey(ARGS_EDIT_MODE);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        stripe = new Stripe(getActivity(), BuildConfig.STRIPE_KEY);
        ButterKnife.bind(this, rootView);
        edtCardNumber.setErrorColor(ContextCompat.getColor(getActivity(),R.color.red_status));
        edtDate.setErrorColor(ContextCompat.getColor(getActivity(),R.color.red_status));
        edtCvv.setErrorColor(ContextCompat.getColor(getActivity(),R.color.red_status));
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
            return ;
        }



        Card card = new Card(
                edtCardNumber.getCardNumber(),
                cardDate[0],
                cardDate[1],
                edtCvv.getText().toString()
        );


        if(!card.validateCard()){
            return;
        }

        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server

                        Log.i(TAG, token.getId());
                        Log.i(TAG, token.toString());
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


}
