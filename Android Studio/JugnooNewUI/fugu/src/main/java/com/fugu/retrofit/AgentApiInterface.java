package com.fugu.retrofit;

import com.fugu.agent.model.FuguAgentGetMessageParams;
import com.fugu.agent.model.FuguAgentGetMessageResponse;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.LoginResponse;
import com.fugu.agent.model.broadcastResponse.BroadcastModel;
import com.fugu.agent.model.broadcastStatus.BroadcastResponseModel;
import com.fugu.agent.model.createConversation.CreateConversation;
import com.fugu.agent.model.unreadResponse.UnreadCountResponse;
import com.fugu.agent.model.user_details.UserDetailsResponse;
import com.fugu.model.FuguGetMessageParams;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguPutUserDetailsResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public interface AgentApiInterface {

    @FormUrlEncoded
    @POST("/api/agent/agentLoginViaAuthToken")
    Call<LoginResponse> verifyAuthToken(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/agent/agentLogin")
    Call<LoginResponse> login(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/agent/agentLogout")
    Call<LoginResponse> logout(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/conversation/v1/getConversations")
    Call<GetConversationResponse> getConversation(@FieldMap Map<String, Object> map);

    @POST("/api/conversation/getMessages")
    Call<FuguAgentGetMessageResponse> getMessages(@Body FuguAgentGetMessageParams obj);

    @FormUrlEncoded
    @POST("/api/conversation/createConversation")
    Call<CreateConversation> createConversation(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("api/users/getUserDetails")
    Call<UserDetailsResponse> getUserDetails(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/conversation/markConversation")
    Call<GetConversationResponse> markConversation(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/conversation/get_customer_unread_count")
    Call<UnreadCountResponse> getUnreadCount(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/getGroupingTag")
    Call<BroadcastModel> getGroupingTag(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/sendBroadcastMessage")
    Call<BroadcastResponseModel> sendBroadcastMessage(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/getBroadcastList")
    Call<BroadcastResponseModel> getBroadcastList(@FieldMap Map<String, Object> map);



}
