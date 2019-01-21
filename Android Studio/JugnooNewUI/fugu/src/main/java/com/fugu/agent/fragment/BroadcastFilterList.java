package com.fugu.agent.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fugu.FuguStringConfig;
import com.fugu.R;
import com.fugu.agent.AgentBroadcastActivity;
import com.fugu.agent.adapter.FleetListAdapter;
import com.fugu.agent.adapter.ListAdapter;
import com.fugu.agent.model.broadcastResponse.Tag;
import com.fugu.agent.model.broadcastResponse.User;
import com.fugu.database.CommonData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 30/07/18.
 *
 * @author gurmail
 */

public class BroadcastFilterList extends Fragment {

    private static final String TAG = BroadcastFilterList.class.getSimpleName();
    private ListAdapter teamAdapter;
    private FleetListAdapter fleetListAdapter;

    private int fragmentType;
//    private int teamId = -2;

    private ArrayList<Tag> pairBoolData = new ArrayList<>();
    private ArrayList<User> pairFleetData = new ArrayList<>();

    private Type taglistType = new TypeToken<List<Tag>>() {
    }.getType();

    private Type userlistType = new TypeToken<List<User>>() {
    }.getType();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private Button applyBtn;
    private TextView selectedTeamName;
    private AgentBroadcastActivity activity;
    private String title = "";
    private String teamName = "";
    private FuguStringConfig fuguStringConfig;

    public int teamIdd= -2;
    public Tag teamTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fragmentType = getArguments().getInt("fragment_type", 1);
            String data = getArguments().getString("data");
            title = getArguments().getString("title", "Select Item");
            if (fragmentType == 1) {
                pairBoolData = new Gson().fromJson(data, taglistType);
            } else {
                pairFleetData = new Gson().fromJson(data, userlistType);
            }
            teamName = getArguments().getString("team_name", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hippo_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (AgentBroadcastActivity) getActivity();
        fuguStringConfig = CommonData.getStringConfig();
        applyBtn = view.findViewById(R.id.apply_btn);

        selectedTeamName = view.findViewById(R.id.selected_team);
        selectedTeamName.setTypeface(null, Typeface.BOLD);

        refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setEnabled(false);
        recyclerView = view.findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        activity.updateToolBar(title);
        if (fragmentType == 1) {
            teamAdapter = new ListAdapter(getActivity(), pairBoolData, new ListAdapter.SelectedId() {
                @Override
                public void selectedTeamId(int id, Tag tag) {
                    teamIdd = id;
                    teamTag = tag;
                }
            });
            recyclerView.setAdapter(teamAdapter);
            applyBtn.setVisibility(View.VISIBLE);
        } else {
            fleetListAdapter = new FleetListAdapter(getActivity(), fragmentType, pairFleetData);
            recyclerView.setAdapter(fleetListAdapter);
            if (fragmentType == 2) {
                selectedTeamName.setVisibility(View.VISIBLE);
                selectedTeamName.setText(teamName);
                applyBtn.setVisibility(View.VISIBLE);
            } else if(fragmentType == 3) {
                applyBtn.setVisibility(View.GONE);
            }
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentType == 1) {
                    if(activity.getBroadcastFragment() != null) {
                        activity.getBroadcastFragment().selectedTeam(teamIdd, teamTag);
                    }
                    if (teamIdd > -1) {
                        User user = new User();
                        user.setUserId(-1);
                        user.setFullName(fuguStringConfig.getFuguAllAgentsString()+" "+fuguStringConfig.getFuguDisplayNameForCustomers());
                        if (teamIdd == -1)
                            user.setSelected(true);
                        pairFleetData.add(user);

                        for (int i = 0; i < pairBoolData.size(); i++) {
                            if (teamIdd == pairBoolData.get(i).getTagId()) {
                                pairFleetData.addAll(pairBoolData.get(i).getUsers());
                                break;
                            }
                        }
                        String title = fuguStringConfig.getFuguSelectString() + " " +fuguStringConfig.getFuguDisplayNameForCustomers();
                        activity.updateToolBar(title);
                        selectedTeamName.setVisibility(View.VISIBLE);
                        selectedTeamName.setText(teamTag.getTagName());
                        fragmentType = 2;
                        fleetListAdapter = new FleetListAdapter(getActivity(), fragmentType, pairFleetData);
                        recyclerView.setAdapter(fleetListAdapter);
                    } else {
                        activity.getSupportFragmentManager().popBackStack();
                    }
                } else {
                    if (activity.getBroadcastFragment() != null) {
                        activity.getBroadcastFragment().selectedFleet(pairFleetData);
                    }
                    activity.getSupportFragmentManager().popBackStack();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                activity.onBackPressed();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fugu_empty_menu, menu);

    }
}
