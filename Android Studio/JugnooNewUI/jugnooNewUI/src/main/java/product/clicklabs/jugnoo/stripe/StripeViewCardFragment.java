package product.clicklabs.jugnoo.stripe;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeViewCardFragment extends Fragment implements callback {

    private static final String TAG = StripeViewCardFragment.class.getName();


    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    @BindView(R.id.cards_view_recycler)
    RecyclerView cardsRecycler;
    private ArrayList<StripeCardData> stripeCardData;
    // private <StripeCardData> cardsData;
    private PaymentOption paymentOption;
    private static final String ARGS_CARD_DATA = "edit_mode";
    private static final String ARGS_PAYMENT_OPTION = "payment_option";
    private PopupMenu popupMenu;

    private StripeCardsStateListener stripeCardsStateListener;
    private Unbinder unbinder;
    private StripeCardsAdapter adapter;
    private RelativeLayout relativeLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StripeCardsStateListener) {
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }
    }


    public static <T extends StripeCardData> StripeViewCardFragment newInstance(ArrayList<StripeCardData> stripeData, PaymentOption paymentOption) {
        StripeViewCardFragment stripeViewCardFragment = new StripeViewCardFragment();
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_CARD_DATA, gson.toJsonTree(stripeData, new TypeToken<List<StripeCardData>>() {}.getType()).getAsJsonArray().toString());
        bundle.putSerializable(ARGS_PAYMENT_OPTION, paymentOption);
        stripeViewCardFragment.setArguments(bundle);
        return stripeViewCardFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARGS_CARD_DATA)) {
            Gson gson = new Gson();
            stripeCardData = gson.fromJson(getArguments().getString(ARGS_CARD_DATA), new TypeToken<List<StripeCardData>>(){}.getType());
            paymentOption = (PaymentOption) getArguments().getSerializable(ARGS_PAYMENT_OPTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_card, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        textViewTitle.setTypeface(Fonts.avenirNext(getActivity()));
        if (stripeCardData != null && stripeCardData.size()>0) {
            ivMore.setVisibility(View.VISIBLE);
            setAdapter();
        }
        return rootView;
    }

    private void setAdapter() {
        adapter = new StripeCardsAdapter(getActivity(), stripeCardData, this);
        cardsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        cardsRecycler.setItemAnimator(new DefaultItemAnimator());
        cardsRecycler.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
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

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.fragLayout, StripeAddCardFragment.newInstance(paymentOption), StripeAddCardFragment.class.getName())
                        .addToBackStack(StripeAddCardFragment.class.getName())
                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
                                .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                        .commit();

//                if (popupMenu == null) {
//
//
//                    ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
//                    popupMenu = new PopupMenu(ctw, view);
//                    //Inflating the Popup using xml file
//                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_stripe_card, popupMenu.getMenu());
//
//                    //registering popup with OnMenuItemClickListener
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        public boolean onMenuItemClick(MenuItem item) {
//                            if (item.getItemId() == R.id.item_add_card) {
//                                getActivity().getSupportFragmentManager().beginTransaction()
//                                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                                        .add(R.id.fragLayout, StripeAddCardFragment.newInstance(paymentOption), StripeAddCardFragment.class.getName())
//                                        .addToBackStack(StripeAddCardFragment.class.getName())
//                                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
//                                                .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//                                        .commit();
//                                return true;
//                            }
//                            return false;
//                        }
//                    });
//                }
//                popupMenu.show();

                break;
        }
    }


    private void showDeletePopup(int position) {
        DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(), getString(R.string.stripe_delete_card, stripeCardData.get(position).getLast4()), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCardApi(stripeCardData.get(position).getCardId());
            }
        });
    }

    private void deleteCardApi(String card_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_CARD_ID, card_id);
        params.put("is_delete", "1");
        params.put("payment_option", String.valueOf(paymentOption.getOrdinal()));


        new ApiCommon<StripeCardResponse>(getActivity()).showLoader(true).execute(params,
                paymentOption == PaymentOption.PAY_STACK_CARD ? ApiName.DELETE_CARD_PAYSTACK : ApiName.ADD_CARD_API, new APICommonCallback<StripeCardResponse>() {
                    @Override
                    public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {

                        if(Prefs.with(requireActivity()).getString(Constants.STRIPE_SELECTED_POS, "0").equalsIgnoreCase(card_id)){
                            Prefs.with(requireActivity()).save(Constants.STRIPE_SELECTED_POS, "0");
                        }
                        if (stripeCardsStateListener != null) {
                            stripeCardsStateListener.onCardsUpdated(stripeCardResponse.getStripeCardData(), message, false, paymentOption);
                        }

                    }

                    @Override
                    public boolean onError(StripeCardResponse stripeCardResponse, String message, int flag) {
                        return false;
                    }
                });


    }

    @Override
    public void onDelete(int position) {
        showDeletePopup(position);
    }

    public void notifyAdapter(ArrayList<StripeCardData> stripeCardDataN) {
        if (adapter != null) {
        if(stripeCardData.size()>0){
            stripeCardData.clear();
            for(int i=0;i<stripeCardDataN.size();i++){
                stripeCardData.add(stripeCardDataN.get(i));
            }
                adapter.notifyDataSetChanged();
            }
        }
    }

}

interface callback {
    void onDelete(int position);
}

