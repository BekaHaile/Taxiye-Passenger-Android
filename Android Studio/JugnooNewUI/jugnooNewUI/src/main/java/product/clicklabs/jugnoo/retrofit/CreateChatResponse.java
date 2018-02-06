package product.clicklabs.jugnoo.retrofit;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

/**
 * Created by Parminder Saini on 06/02/18.
 */

public class CreateChatResponse  extends FeedCommonResponse{



    @SerializedName("fugu_data")
    private  CreateChatFuguData fuguData;

    @SerializedName("channel_id")
    private String  channelId;


    public String getChannelId() {
        return channelId;
    }


    public CreateChatFuguData getFuguData() {
        return fuguData;
    }

   public class CreateChatFuguData{



        @SerializedName("transaction_id")
        private String channelName;


       @SerializedName("tags")
       private ArrayList<String> fuguTags;



        public String getChannelName() {
            return channelName;
        }

       public ArrayList<String> getFuguTags() {
           return fuguTags;
       }
   }
}
