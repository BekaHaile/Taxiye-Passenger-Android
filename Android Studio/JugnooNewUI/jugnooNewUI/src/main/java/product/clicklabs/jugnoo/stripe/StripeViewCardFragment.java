package product.clicklabs.jugnoo.stripe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.stripe.android.model.Card;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.wallet.WalletCore;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeViewCardFragment extends Fragment {

    private static final String TAG = StripeViewCardFragment.class.getName();


    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.tv_card)
    TextView tvCard;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    private StripeCardData stripeCardData;
    private PaymentOption paymentOption;
    private static final String ARGS_CARD_DATA = "edit_mode";
    private static final String ARGS_PAYMENT_OPTION = "payment_option";
    private PopupMenu popupMenu;

    private StripeCardsStateListener stripeCardsStateListener;
    private Unbinder unbinder;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StripeCardsStateListener){
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }


    }


    public static  <T extends StripeCardData>  StripeViewCardFragment newInstance(T stripeData,PaymentOption paymentOption) {
        StripeViewCardFragment stripeViewCardFragment = new StripeViewCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CARD_DATA, stripeData);
        bundle.putSerializable(ARGS_PAYMENT_OPTION, paymentOption);
        stripeViewCardFragment.setArguments(bundle);
        return stripeViewCardFragment;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARGS_CARD_DATA)) {
            stripeCardData = getArguments().getParcelable(ARGS_CARD_DATA);
            paymentOption = (PaymentOption) getArguments().getSerializable(ARGS_PAYMENT_OPTION);
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_card, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        textViewTitle.setTypeface(Fonts.avenirNext(getActivity()));
        if (stripeCardData != null) {

            tvCard.setText(WalletCore.getStripeCardDisplayString(getActivity(),stripeCardData.getLast4()));
            tvCard.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.VISIBLE);
            tvCard.setCompoundDrawablesWithIntrinsicBounds(WalletCore.getBrandImage(stripeCardData.getBrand()), 0, 0, 0);
        }

        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null){
            unbinder.unbind();

        }
    }



    @OnClick({R.id.imageViewBack, R.id.ivMore})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.ivMore:
                if(popupMenu==null){


                    ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
                    popupMenu = new PopupMenu(ctw, view);
                    //Inflating the Popup using xml file
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_stripe_card, popupMenu.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.item_delete){
                                showDeletePopup(stripeCardData);
                                return true;

                            }
                            return false;
                        }
                    });
                }
                popupMenu.show();

                break;
        }
    }

    private void showDeletePopup(final StripeCardData stripeCardData) {
        DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(), getString(R.string.stripe_delete_card,stripeCardData.getLast4()), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCardApi(stripeCardData);
            }
        });
    }

    private void deleteCardApi(final StripeCardData stripeCardData) {
        HashMap<String,String> params = new HashMap<>();
        params.put("card_id",stripeCardData.getCardId());
        params.put("is_delete","1");
        params.put("payment_option",String.valueOf(paymentOption.getOrdinal()));




        new ApiCommon<StripeCardResponse>(getActivity()).showLoader(true).execute(params, ApiName.ADD_CARD_API, new APICommonCallback<StripeCardResponse>() {
            @Override
            public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {

                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(stripeCardResponse.getStripeCardData(),message,false,paymentOption);
                }

            }

            @Override
            public boolean onError(StripeCardResponse stripeCardResponse, String message, int flag) {
                return false;
            }
        });


    }
}
