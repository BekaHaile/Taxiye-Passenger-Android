package product.clicklabs.jugnoo.retrofit.model;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ankit on 11/11/16.
 */

public interface ChatApiService {

    @FormUrlEncoded
    @POST("/chat/v1/fetch_chat")
    void fetchChat(@FieldMap Map<String, String> params,
                   Callback<FetchChatResponse> callback);

    @FormUrlEncoded
    @POST("/chat/v1/post_chat")
    void postChat(@FieldMap Map<String, String> params,
                  Callback<FetchChatResponse> callback);
}
