package com.sabkuchfresh.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabkuchfresh.adapters.FeedOfferingListAdapter;

import product.clicklabs.jugnoo.R;


public class FeedHomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public FeedHomeFragment() {
        // Required empty public constructor
    }


    public static FeedHomeFragment newInstance(String param1, String param2) {
        FeedHomeFragment fragment = new FeedHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchFeedsApi(3123.321,31231.3123);
        FeedOfferingListAdapter feedOfferingListAdapter = new FeedOfferingListAdapter(getActivity(), null, recyclerView, new FeedOfferingListAdapter.Callback() {
            @Override
            public void onLikeClick(Object object) {

            }

            @Override
            public void onCommentClick(Object object) {

            }
        });
        recyclerView.setAdapter(feedOfferingListAdapter);
        return rootView;
    }

    private void fetchFeedsApi(double lat, double lng) {




    }

}
