package com.fugu.agent.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fugu.R;
import com.fugu.agent.AgentBroadcastActivity;
import com.fugu.agent.adapter.BroadcastStatusAdapter;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.helper.BroadcastListenerHelper;
import com.fugu.agent.listeners.BroadcastListener;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.broadcastResponse.BroadcastModel;
import com.fugu.agent.model.broadcastStatus.BroadcastInfo;
import com.fugu.agent.model.broadcastStatus.BroadcastResponseModel;
import com.fugu.retrofit.CommonParams;
import com.fugu.utils.loadingBox.LoadingBox;

import java.util.ArrayList;

/**
 * Created by gurmail on 26/07/18.
 *
 * @author gurmail
 */

public class BroadcastStatusFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BroadcastListener.BroadcastResponse {

    private static final String TAG = BroadcastStatusFragment.class.getSimpleName();
    private BroadcastStatusAdapter statusAdapter;
    private BroadcastListener broadcastListener;
    private UserData userData;
    private ArrayList<BroadcastInfo> broadcastInfos = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private LinearLayout llNoConversation;
    private AgentBroadcastActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hippo_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (AgentBroadcastActivity) getActivity();
        activity.updateToolBar("Broadcast History");

        broadcastListener = new BroadcastListenerHelper();
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        llNoConversation = view.findViewById(R.id.llNoConversation);
        refreshLayout.setEnabled(false);
        listView = view.findViewById(R.id.list_view);

        statusAdapter = new BroadcastStatusAdapter(broadcastInfos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(statusAdapter);

        getBroadcastStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        broadcastListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        getBroadcastStatus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            activity.updateToolBar("Broadcast History");
    }

    private void getBroadcastStatus() {
        if (userData == null)
            userData = AgentCommonData.getUserData();
        String accessToken = userData.getAccessToken();

        CommonParams commonParams = new CommonParams.Builder()
                .add("access_token", accessToken)
                .build();

        LoadingBox.showOn(getActivity());
        broadcastListener.getBroadcastList(commonParams.getMap(), this);

    }

    @Override
    public void groupingResponse(BroadcastModel broadcastModel) {

    }

    @Override
    public void sendBroadcastResponse(BroadcastResponseModel responseModel) {

    }

    @Override
    public void broadcastListResponse(BroadcastResponseModel responseModel) {
        LoadingBox.hide();
        if(responseModel.getData() == null || responseModel.getData().getBroadcastInfo() == null || responseModel.getData().getBroadcastInfo().size() == 0) {
            llNoConversation.setVisibility(View.VISIBLE);
        } else {
            llNoConversation.setVisibility(View.GONE);
            broadcastInfos.addAll(responseModel.getData().getBroadcastInfo());
            statusAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(int type, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        LoadingBox.hide();
        switch (type) {
            case 1:

                break;
            default:

                break;
        }
    }
}
