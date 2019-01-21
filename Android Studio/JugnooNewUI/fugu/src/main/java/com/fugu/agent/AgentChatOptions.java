package com.fugu.agent;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.R;
import com.fugu.agent.Util.DialogPop;
import com.fugu.agent.Util.MessageMode;
import com.fugu.agent.Util.PhoneFunctions;
import com.fugu.agent.Util.TagContainerLayout;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.model.ApiResponseFlags;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.TagData;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.agent.model.user_details.UserDetailsResponse;
import com.fugu.constant.FuguAppConstant;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class AgentChatOptions extends AgentBaseActivity {
    private static final String TAG = AgentChatOptions.class.getSimpleName();

    private RelativeLayout rlAgentName, rlTag;
    private TextView tvCloseConversation, agentName, tvCustomerName, tvCustomerActivationTime,
            etCustomerEmail, etCustomerPhone, tvCustomAttributes, etCustomerLocation;
    private View viewCustomerDetails;
    private TagContainerLayout tagLayout;
    private LinearLayout llName;
    private ImageView ivCustomerImage;

    private Conversation conversation;
    private ArrayList<TagData> tagData = new ArrayList<>();
    private Type listType = new TypeToken<List<TagData>>() {
    }.getType();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_agent_detail);

        initView();
        conversation = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.CONVERSATION), Conversation.class);
        tagData = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.TAGS_DATA), listType);
        if (conversation.getStatus() != null)
            status = conversation.getStatus().equals(MessageMode.OPEN_CHAT.getOrdinal()) ? MessageMode.CLOSED_CHAT.getOrdinal() : MessageMode.OPEN_CHAT.getOrdinal();
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, "Info");
        setData();
    }

    private void initView() {
        tvCloseConversation = findViewById(R.id.tvCloseConversation);
        rlAgentName = findViewById(R.id.rlAgentName);
        agentName = findViewById(R.id.tvAgentName);

        rlTag = findViewById(R.id.rlTag);
        tagLayout = findViewById(R.id.tagLayout);
        tagLayout.setBackgroundColor(Color.WHITE);
        tagLayout.setTagTextColor(Color.WHITE);

        llName = findViewById(R.id.llName);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerActivationTime = findViewById(R.id.tvCustomerActivationTime);
        ivCustomerImage = findViewById(R.id.ivCustomerImage);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        tvCustomAttributes = findViewById(R.id.tvCustomAttributes);
        etCustomerLocation = findViewById(R.id.etCustomerLocation);
        viewCustomerDetails = findViewById(R.id.viewCustomerDetails);

        etCustomerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGmail(etCustomerEmail.getText().toString());
            }
        });
        etCustomerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPhone(etCustomerPhone.getText().toString());
            }
        });

    }

    private void setData() {
        agentName.setText(conversation.getAgentName());
//        tvCustomerName.setText(conversation.getChannelName());
//        tvCustomerActivationTime.setText();
//        etCustomerEmail.setText();
//        etCustomerPhone.setText();
        if (conversation.getStatus().equals(MessageMode.OPEN_CHAT.getOrdinal())) {
            tvCloseConversation.setText(getResources().getString(R.string.fugu_close_conversation));
        } else {
            tvCloseConversation.setText(getResources().getString(R.string.fugu_reopen_conversation));
        }
        tvCloseConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMarkConversation();
            }
        });

        if(conversation.getUserId() != null && conversation.getUserId() > 0)
            getUserData();
        setTags();
    }

    public ArrayList<String> tagList = new ArrayList<>();
    public ArrayList<Integer> colorsTag = new ArrayList<>();

    public void setTags() {
        if (tagData.isEmpty()) {
            tagLayout.setVisibility(View.GONE);
        } else {
            tagLayout.setVisibility(View.VISIBLE);
        }
        tagLayout.removeAllTags();
        for (int i = 0; i < tagData.size(); i++) {
            tagList.add(tagData.get(i).getTag_name());
            colorsTag.add(Color.parseColor(tagData.get(i).getColor_code().toLowerCase()));
        }

        tagLayout.setTags(tagList, colorsTag);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void goToGmail(String mail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void goToPhone(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getUserData() {
        if (isNetworkAvailable()) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(FuguAppConstant.ACCESS_TOKEN, AgentCommonData.getUserData().getAccessToken())
                    .add(FuguAppConstant.USER_ID, conversation.getUserId())
                    .build();
            RestClient.getAgentApiInterface().getUserDetails(commonParams.getMap())
                    .enqueue(new ResponseResolver<UserDetailsResponse>(AgentChatOptions.this, true, true) {
                        @Override
                        public void success(UserDetailsResponse userDetailsResponse) {
                            try {
                                tvCustomerName.setText(userDetailsResponse.getData().get(0).getFullName());
                                tvCustomerActivationTime.setText("Active " +
                                        DateUtils.getInstance().getTimeToDisplay(userDetailsResponse.getData().get(0).getLastSeen()));

//                                if (userDetailsResponse.getData().get(0).getFirstSeen() == null || userDetailsResponse.getData().get(0).getFirstSeen().isEmpty()) {
//                                    viewSiteVisit.setVisibility(View.GONE);
//                                }

                                if ((userDetailsResponse.getData().get(0).getPhoneNumber() == null
                                        || userDetailsResponse.getData().get(0).getPhoneNumber().isEmpty()
                                        || userDetailsResponse.getData().get(0).getPhoneNumber().equals(""))) {
                                    etCustomerPhone.setVisibility(View.GONE);
                                } else {
                                    etCustomerPhone.setVisibility(View.VISIBLE);
                                    etCustomerPhone.setText(userDetailsResponse.getData().get(0).getPhoneNumber());
                                    PhoneFunctions phoneFunctions = new PhoneFunctions();
                                    String phone = "";
                                    if (userDetailsResponse.getData().get(0).getPhoneNumber().substring(0, 1).equals("+")) {
                                        phone = userDetailsResponse.getData().get(0).getPhoneNumber().substring(1, userDetailsResponse.getData().get(0).getPhoneNumber().length());
                                    }
                                    String countryCode = phoneFunctions.getCountry(getResources().getStringArray(R.array.FuguCountryCodes)
                                            , phone);
                                    if (countryCode != null && !countryCode.isEmpty()) {
                                        etCustomerPhone.setText("+" + countryCode + "-" + userDetailsResponse.getData().get(0).getPhoneNumber().substring(countryCode.length() + 1));
                                    }
                                }

                                if ((userDetailsResponse.getData().get(0).getEmail() == null
                                        || userDetailsResponse.getData().get(0).getEmail().isEmpty()
                                        || userDetailsResponse.getData().get(0).getEmail().equals(""))) {
                                    etCustomerEmail.setVisibility(View.GONE);
                                } else {
                                    etCustomerEmail.setVisibility(View.VISIBLE);
                                    etCustomerEmail.setText(userDetailsResponse.getData().get(0).getEmail());
                                }

                                if ((userDetailsResponse.getData().get(0).getPhoneNumber() == null
                                        || userDetailsResponse.getData().get(0).getPhoneNumber().isEmpty()
                                        || userDetailsResponse.getData().get(0).getPhoneNumber().equals("")) &&
                                        (userDetailsResponse.getData().get(0).getEmail() == null
                                                || userDetailsResponse.getData().get(0).getEmail().isEmpty()
                                                || userDetailsResponse.getData().get(0).getEmail().equals(""))) {
                                    viewCustomerDetails.setVisibility(View.GONE);
                                }
//                                setViewPager(userDetailsResponse);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            FuguLog.e("error", "error");
                        }
                    });
        } else {
            Toast.makeText(AgentChatOptions.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private int status;
    private void performMarkConversation() {
        String message = (status == MessageMode.OPEN_CHAT.getOrdinal() ? getResources().getString(R.string.fugu_reopen_chat_message) : getResources().getString(R.string.fugu_close_chat_message));
        String positive = (status == MessageMode.OPEN_CHAT.getOrdinal() ? getResources().getString(R.string.fugu_reopen_caps) : getResources().getString(R.string.fugu_close));
        DialogPop dialogPop = new DialogPop();
        dialogPop.alertPopupWithTwoButton(AgentChatOptions.this, "", message, positive
                , getResources().getString(R.string.fugu_cancel), new DialogPop.Callback() {
                    @Override
                    public void onPositiveClick() {
                        apiMarkConversation();
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                });
    }

    private void apiMarkConversation() {
        if (isNetworkAvailable()) {
//            CommonParams commonParams = new CommonParams.Builder()
//                    .add(FuguAppConstant.ACCESS_TOKEN, AgentCommonData.getUserData().getAccessToken())
//                    .add(FuguAppConstant.CHANNEL_ID, String.valueOf(conversation.getChannelId()))
//                    .add(FuguAppConstant.USER_ID, AgentCommonData.getUserData().getUserId())
//                    .add(FuguAppConstant.STATUS, String.valueOf(status))
//                    .build();

            CommonParams.Builder builder = new CommonParams.Builder();
            builder.add(FuguAppConstant.ACCESS_TOKEN, AgentCommonData.getUserData().getAccessToken());
            builder.add(FuguAppConstant.CHANNEL_ID, String.valueOf(conversation.getChannelId()));
            builder.add(FuguAppConstant.EN_USER_ID, AgentCommonData.getUserData().getEnUserId());
            builder.add(FuguAppConstant.STATUS, String.valueOf(status));
            builder.add(FuguAppConstant.CREATED_BY, AgentCommonData.getUserData().getFullName());
//            if(!TextUtils.isEmpty(dealId))
//                builder.add(FuguAppConstant.DEAL_ID, dealId);

            CommonParams commonParams = builder.build();

            RestClient.getAgentApiInterface().markConversation(commonParams.getMap())
                    .enqueue(new ResponseResolver<GetConversationResponse>(AgentChatOptions.this, true, true) {
                        @Override
                        public void success(GetConversationResponse loginResponse) {
                            try {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == loginResponse.getStatusCode()) {
                                    Bundle conData = new Bundle();
                                    conData.putString(FuguAppConstant.CHANNEL_ID, String.valueOf(conversation.getChannelId()));
                                    Intent intent = new Intent();
                                    intent.putExtras(conData);
                                    if (conversation.getStatus() == MessageMode.OPEN_CHAT.getOrdinal()) {
                                        setResult(MessageMode.CLOSED_CHAT.getOrdinal(), intent);
                                    } else {
                                        setResult(MessageMode.OPEN_CHAT.getOrdinal(), intent);
                                    }
                                    onBackPressed();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            FuguLog.e("error", "error");
                        }
                    });
        } else {
            Toast.makeText(AgentChatOptions.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
