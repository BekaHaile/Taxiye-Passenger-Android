package com.country.picker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

public class CountryPickerDialog extends DialogFragment implements OnItemClickListener {

    // region Variables
    private CountryPickerDialogInteractionListener dialogInteractionListener;
    private EditText searchEditText;
    private RecyclerView countriesRecyclerView;
    private CountriesAdapter adapter;
    private List<Country> searchResults;
    private OnCountryPickerListener listener;
    private LinearLayout llNoData;
    // endregion

    // region Constructors
    public static CountryPickerDialog newInstance() {
        return new CountryPickerDialog();
    }
    // endregion

    // region Lifecycle
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, null);
        getDialog().setTitle(R.string.country_picker_header);
        searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
        countriesRecyclerView = (RecyclerView) view.findViewById(R.id.countries_recycler_view);
        llNoData = (LinearLayout) view.findViewById(R.id.llNoData);
        setupRecyclerView();
        if (!dialogInteractionListener.canSearch()) {
            searchEditText.setVisibility(View.GONE);
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable searchQuery) {
                search(searchQuery.toString());
            }
        });
        searchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null && getView() != null) {
                    searchEditText.requestFocus();
                    Utils.showKeyboard(getActivity(), searchEditText);
                }
            }
        }, 500);
        return view;
    }

    @Override
    public void onItemClicked(Country country) {
        if (listener != null) {
            listener.onSelectCountry(country);
            dismiss();
        }
    }
    // endregion

    // region Setter Methods
    public void setCountryPickerListener(OnCountryPickerListener listener) {
        this.listener = listener;
    }

    public void setDialogInteractionListener(
            CountryPickerDialogInteractionListener dialogInteractionListener) {
        this.dialogInteractionListener = dialogInteractionListener;
    }
    // endregion

    // region Utility Methods
    private void search(String searchQuery) {
        searchResults.clear();
        for (Country country : dialogInteractionListener.getAllCountries()) {
            if (country.getName().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                searchResults.add(country);
            }
        }
        dialogInteractionListener.sortCountries(searchResults);
        if(searchResults.size()>0) {
            llNoData.setVisibility(View.GONE);
            countriesRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            llNoData.setVisibility(View.VISIBLE);
            countriesRecyclerView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        searchResults = new ArrayList<>();
        searchResults.addAll(dialogInteractionListener.getAllCountries());
        adapter = new CountriesAdapter(getActivity(), searchResults, this);
        countriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        countriesRecyclerView.setLayoutManager(layoutManager);
        countriesRecyclerView.setAdapter(adapter);
    }

    // endregion

    //region Interface
    public interface CountryPickerDialogInteractionListener {
        List<Country> getAllCountries();

        void sortCountries(List<Country> searchResults);

        boolean canSearch();
    }
    // endregion
}
