package product.clicklabs.jugnoo.stripe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;

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
    @Bind(R.id.ivMore)
    ImageView ivMore;
    private StripeCardData stripeCardData;
    private static final String ARGS_CARD_DATA = "edit_mode";
    private PopupMenu popupMenu;

    public static  <T extends StripeCardData>  StripeViewCardFragment newInstance(T stripeData) {
        StripeViewCardFragment stripeViewCardFragment = new StripeViewCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CARD_DATA, stripeData);
        stripeViewCardFragment.setArguments(bundle);
        return stripeViewCardFragment;


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
        if (stripeCardData != null) {
            StringBuilder formString = new StringBuilder();

            for (int i = 0; i < 12; i++) {
                if (i != 0 && i % 4 == 0) {
                    formString.append(" ");
                }
                formString.append(R.string.bullet);

            }
            formString.append(" ");
            formString.append(stripeCardData.getLast4());
            tvCard.setText(formString);
            tvCard.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.VISIBLE);
            updateIcon(stripeCardData.getBrand());
        }

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


    @OnClick({R.id.imageViewBack, R.id.ivMore})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageViewBack:
                break;
            case R.id.ivMore:
                if(popupMenu==null){


                    PopupMenu popup = new PopupMenu(getActivity(), view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu_stripe_card, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
        DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCardApi(stripeCardData);
            }
        });
    }

    private void deleteCardApi(StripeCardData stripeCardData) {
        HashMap<String,String> params = new HashMap<>();
        params.put("card_id",stripeCardData.getCardId());
        params.put("is_delete","1");




        new ApiCommon<StripeCardResponse>(getActivity()).showLoader(true).execute(params, ApiName.ADD_CARD_API, new APICommonCallback<StripeCardResponse>() {
            @Override
            public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {
                if(getView()==null || getActivity()==null){
                    return;
                }


                DialogPopup.alertPopupWithListener(getActivity(),"",message, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();

                    }
                });




            }

            @Override
            public boolean onError(StripeCardResponse stripeCardResponse, String message, int flag) {
                return false;
            }
        });


    }
}
