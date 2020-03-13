package com.fugu.agent.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.FuguColorConfig;
import com.fugu.FuguStringConfig;
import com.fugu.R;
import com.fugu.agent.AgentBroadcastActivity;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.helper.BroadcastListenerHelper;
import com.fugu.agent.listeners.BroadcastListener;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.broadcastResponse.BroadcastModel;
import com.fugu.agent.model.broadcastResponse.Tag;
import com.fugu.agent.model.broadcastResponse.User;
import com.fugu.agent.model.broadcastStatus.BroadcastResponseModel;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.retrofit.CommonParams;
import com.fugu.utils.loadingBox.LoadingBox;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by gurmail on 20/07/18.
 *
 * @author gurmail
 */

public class BroadcastFragment extends BaseFragment implements BroadcastListener.BroadcastResponse {

    private static final String TAG = BroadcastFragment.class.getSimpleName();
    private BroadcastListener broadcastListener;
    private UserData userData;

    private ArrayList<Tag> pairBoolData = new ArrayList<>();
    private ArrayList<User> pairFleetData = new ArrayList<>();
    private ArrayList<User> finalFleetData = new ArrayList<>();

    private BroadcastModel broadcastModel;
    private Button sendBtn;
    private TextView broadcastStatus, titleView, titleInfo, selectTeamsView, selectFleetsView, showFleetsView;
    private EditText edTitleView, edMessageView;
    private LinearLayout selectedTeamsLayout, selectedFleetsLayout, showFleetsLayout;
    private AgentBroadcastActivity activity;

    private BroadcastFilterList filterList;
    private FuguStringConfig fuguStringConfig;
    private FuguColorConfig fuguColorConfig;

    private int teamId = -2;
    private String teamName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fugu_broadcast_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastListener = new BroadcastListenerHelper();
        activity = (AgentBroadcastActivity) getActivity();
        activity.updateToolBar("Broadcast Message");
        fuguStringConfig = CommonData.getStringConfig();
        fuguColorConfig = CommonData.getColorConfig();
        if (userData == null)
            userData = AgentCommonData.getUserData();
        String accessToken = userData.getAccessToken();
        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.ACCESS_TOKEN, accessToken)
                .build();
        broadcastListener.getGroupingList(commonParams.getMap(), this);

        titleView = view.findViewById(R.id.title_view);
        titleInfo = view.findViewById(R.id.title_view_info);
        selectTeamsView = view.findViewById(R.id.select_teams_view);
        selectFleetsView = view.findViewById(R.id.select_fleets_view);
        showFleetsView = view.findViewById(R.id.show_fleets_view);

        edTitleView = view.findViewById(R.id.ed_title_view);
        edMessageView = view.findViewById(R.id.ed_message_view);
        sendBtn = view.findViewById(R.id.sendBtn);

        selectedTeamsLayout = view.findViewById(R.id.selected_teams_layout);
        selectedFleetsLayout = view.findViewById(R.id.selected_fleets_layout);
        showFleetsLayout = view.findViewById(R.id.show_fleets_layout);
        broadcastStatus = view.findViewById(R.id.broadcastStatus);

        setTextViews();
        setSpinnerColor(1);

        edMessageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.ed_message_view) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        broadcastStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBroadcastStatus();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalFleetData == null || finalFleetData.size() == 0) {
                    Toast.makeText(getActivity(), "Please select "+fuguStringConfig.getFuguDisplayNameForCustomers(), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edTitleView.getText().toString().trim())) {
                    Toast.makeText(getActivity(), fuguStringConfig.getFuguTitleString()+" can't be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edMessageView.getText().toString().trim())) {
                    Toast.makeText(getActivity(), fuguStringConfig.getFuguMessageString()+" can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    LoadingBox.showOn(getActivity());
                    sendingBroadcastMessages();
                }
            }
        });

        selectedTeamsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(pairBoolData == null || pairBoolData.size() == 0) {
//                    Toast.makeText(activity, fuguStringConfig.getFuguSelectTeamsString()+ " first", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                hideKeyboard(edTitleView);

                BroadcastFilterList broadcastFilterList = new BroadcastFilterList();
                Bundle bundle = new Bundle();
                bundle.putString("data", new Gson().toJson(pairBoolData));
                bundle.putInt("fragment_type", 1);
                bundle.putString("title", fuguStringConfig.getFuguSelectTeamsString());
                broadcastFilterList.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_layout, broadcastFilterList, BroadcastFilterList.class.getName())
                        .addToBackStack(BroadcastFilterList.class.getName())
                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
                                .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                        .commitAllowingStateLoss();

            }
        });

        selectedFleetsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(pairBoolData == null || pairBoolData.size() == 0) {
//                    Toast.makeText(activity, "No Fleet selected", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                hideKeyboard(edTitleView);

                BroadcastFilterList broadcastFilterList = new BroadcastFilterList();
                Bundle bundle = new Bundle();
                bundle.putString("data", new Gson().toJson(pairFleetData));
                bundle.putInt("fragment_type", 2);
                bundle.putString("team_name", teamName);
                bundle.putString("title", fuguStringConfig.getFuguSelectString()+ " "+fuguStringConfig.getFuguDisplayNameForCustomers());
                broadcastFilterList.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_layout, broadcastFilterList, BroadcastFilterList.class.getName())
                        .addToBackStack(BroadcastFilterList.class.getName())
                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
                                .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                        .commitAllowingStateLoss();
            }
        });

        showFleetsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(edTitleView);
                BroadcastFilterList broadcastFilterList = new BroadcastFilterList();
                Bundle bundle = new Bundle();
                bundle.putString("data", new Gson().toJson(finalFleetData));
                bundle.putInt("fragment_type", 3);
                bundle.putString("title", fuguStringConfig.getFuguSelectedString()+" "+fuguStringConfig.getFuguDisplayNameForCustomers());
                broadcastFilterList.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_layout, broadcastFilterList, BroadcastFilterList.class.getName())
                        .addToBackStack(BroadcastFilterList.class.getName())
                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
                                .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                        .commitAllowingStateLoss();
            }
        });

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setTextViews() {

        titleView.setText(String.format("%s %s", fuguStringConfig.getFuguBroadCastTitle(), fuguStringConfig.getFuguDisplayNameForCustomers()));
        titleInfo.setText(String.format("%s %s", fuguStringConfig.getFuguBroadCastTitleInfo(), fuguStringConfig.getFuguDisplayNameForCustomers()));
        selectTeamsView.setText(fuguStringConfig.getFuguSelectTeamsString());
        //selectFleetsView.setText(fuguStringConfig.getFuguSelectString() + " "+ fuguStringConfig.getFuguDisplayNameForCustomers());
        setShowValue(0);

        edTitleView.setHint(fuguStringConfig.getFuguTitleString());
        edMessageView.setHint(fuguStringConfig.getFuguMessageString());
        sendBtn.setText(fuguStringConfig.getFuguSendButtonString());
        broadcastStatus.setText(fuguStringConfig.getFuguSeePreviousMessges());
    }

    private void setShowValue(int count) {
        showFleetsView.setText(fuguStringConfig.getFuguShowString() + " "+ fuguStringConfig.getFuguDisplayNameForCustomers() + "("+ count +" "+fuguStringConfig.getFuguSelectedString() + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        broadcastListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            activity.updateToolBar("Broadcast Message");
    }

    private void setSpinnerColor(int pos) {
        switch (pos) {
            case 1:
                showFleetsView.setTextColor(Color.parseColor("#882c2333"));
                selectFleetsView.setTextColor(Color.parseColor("#882c2333"));

                selectedFleetsLayout.setClickable(false);
                selectedFleetsLayout.setEnabled(false);
                showFleetsLayout.setClickable(false);
                showFleetsLayout.setEnabled(false);
                break;
            case 2:
                selectFleetsView.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
                showFleetsView.setTextColor(Color.parseColor("#882c2333"));

                selectedFleetsLayout.setClickable(true);
                selectedFleetsLayout.setEnabled(true);
                showFleetsLayout.setClickable(false);
                showFleetsLayout.setEnabled(false);
                break;
            case 3:
                showFleetsView.setTextColor(fuguColorConfig.getFuguTextColorPrimary());

                showFleetsLayout.setClickable(true);
                showFleetsLayout.setEnabled(true);
                break;

            case 4:
                showFleetsView.setTextColor(Color.parseColor("#882c2333"));

                showFleetsLayout.setClickable(false);
                showFleetsLayout.setEnabled(false);
                break;
            default:

                break;
        }
    }


    @Override
    public void groupingResponse(BroadcastModel broadcastModel) {
        LoadingBox.hide();
        this.broadcastModel = broadcastModel;

        Tag tag = new Tag();
        tag.setTagId(-1);
        tag.setSelected(false);
        tag.setTagName(fuguStringConfig.getFuguAllTeamString());


        pairBoolData.add(tag);
        pairBoolData.addAll(broadcastModel.getData().getTags());

    }

    @Override
    public void sendBroadcastResponse(BroadcastResponseModel responseModel) {
        LoadingBox.hide();
        Toast.makeText(activity, ""+responseModel.getMessage(), Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    @Override
    public void broadcastListResponse(BroadcastResponseModel responseModel) {

    }

    @Override
    public void onFailure(int type, String errorMessage) {
        LoadingBox.hide();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        switch (type) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            default:

                break;
        }
    }

    public void selectedTeam(int id, Tag tag) {
        teamId = id;
        selectedFleetsLayout.setEnabled(true);
        selectedFleetsLayout.setClickable(true);
        selectFleetsView.setText(String.format("%s %s", fuguStringConfig.getFuguSelectString(), fuguStringConfig.getFuguDisplayNameForCustomers()));
        finalFleetData.clear();
        if (teamId == -1) {
            for (int i = 1; i < pairBoolData.size(); i++) {
                finalFleetData.addAll(pairBoolData.get(i).getUsers());
                pairBoolData.get(i).setSelected(true);
            }
            pairBoolData.get(0).setSelected(true);
            selectedFleetsLayout.setClickable(false);
            selectedFleetsLayout.setEnabled(false);

            showFleetsLayout.setEnabled(true);
            showFleetsLayout.setClickable(true);
            setSpinnerColor(3);
            setShowValue(finalFleetData.size());
            teamName = fuguStringConfig.getFuguAllTeamString();
            selectTeamsView.setText(fuguStringConfig.getFuguAllTeamString());
            selectFleetsView.setText(String.format("%s %s %s", fuguStringConfig.getFuguAllAgentsString(), fuguStringConfig.getFuguDisplayNameForCustomers(), fuguStringConfig.getFuguSelectedString()));
        } else if (teamId > -1) {
            pairFleetData.clear();
            User user = new User();
            user.setUserId(-1);
            user.setFullName(fuguStringConfig.getFuguAllAgentsString()+" "+fuguStringConfig.getFuguDisplayNameForCustomers());
            if (id == -1)
                user.setSelected(true);
            pairFleetData.add(user);
            pairBoolData.get(0).setSelected(false);
            pairFleetData.addAll(tag.getUsers());
            for (int i = 1; i < pairBoolData.size(); i++) {
                if(id == pairBoolData.get(i).getTagId().intValue())
                    pairBoolData.get(i).setSelected(true);
                else
                    pairBoolData.get(i).setSelected(false);
            }
            setSpinnerColor(2);
//            finalFleetData.clear();
            teamName = tag.getTagName();
            selectTeamsView.setText(tag.getTagName());
        } else {
            teamName = "";
            setSpinnerColor(1);
//            pairFleetData.clear();
            finalFleetData.clear();
            for (int i = 1; i < pairBoolData.size(); i++) {
                pairBoolData.get(i).setSelected(false);
            }
            selectTeamsView.setText(fuguStringConfig.getFuguSelectTeamsString());
        }
    }

    public void selectedFleet(ArrayList<User> userData) {
        pairFleetData.clear();
        pairFleetData.addAll(userData);
        finalFleetData.clear();
        if (userData.get(0).isSelected()) {
            finalFleetData.addAll(userData);
            finalFleetData.remove(0);
            showFleetsLayout.setEnabled(true);
            showFleetsLayout.setClickable(true);
            setSpinnerColor(3);
            setShowValue(userData.size() - 1);
        } else {
            boolean flag = false;
            int count = 0;
            for (int i = 1; i < userData.size(); i++) {
             if(userData.get(i).isSelected()) {
                 finalFleetData.add(userData.get(i));
                 flag = true;
                 count = count + 1;
             }
            }
            setShowValue(count);
            if(flag) {
                setSpinnerColor(3);
                showFleetsLayout.setEnabled(true);
                showFleetsLayout.setClickable(true);
            } else {
                setSpinnerColor(4);
            }
        }
    }

    private void sendingBroadcastMessages() {
        if (broadcastListener == null)
            broadcastListener = new BroadcastListenerHelper();

        if (userData == null)
            userData = AgentCommonData.getUserData();
        String accessToken = userData.getAccessToken();

        ArrayList<Integer> fleets = new ArrayList<>();
        for(User user : finalFleetData) {
            fleets.add(user.getUserId());
        }

        CommonParams commonParams = new CommonParams.Builder()
                .add("access_token", accessToken)
                .add("user_ids", new Gson().toJson(fleets))
                .add("user_first_message", edMessageView.getText().toString().trim())
                .add("broadcast_title", edTitleView.getText().toString().trim())
                .build();

        LoadingBox.showOn(getActivity());
        broadcastListener.sendBroadcastMessage(commonParams.getMap(), this);
    }

    private void openBroadcastStatus() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.main_layout, new BroadcastStatusFragment(), BroadcastStatusFragment.class.getName())
                .addToBackStack(BroadcastStatusFragment.class.getName())
                .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager()
                        .getBackStackEntryAt(getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                .commitAllowingStateLoss();
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
        inflater.inflate(R.menu.fugu_chat_menu, menu);

    }

}
