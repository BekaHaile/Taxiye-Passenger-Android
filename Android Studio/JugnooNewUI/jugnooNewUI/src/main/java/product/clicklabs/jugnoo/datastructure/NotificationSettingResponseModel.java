package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 10/08/16.
 */
public class NotificationSettingResponseModel {
    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("data")
    @Expose
    private List<NotificationPrefData> data = new ArrayList<>();

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return The data
     */
    public List<NotificationPrefData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<NotificationPrefData> data) {
        this.data = data;
    }

    public class NotificationPrefData {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("content")
        @Expose
        private String content;
        @SerializedName("is_editable")
        @Expose
        private Integer isEditable;

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The status
         */
        public Integer getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(Integer status) {
            this.status = status;
        }

        /**
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return The content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content The content
         */
        public void setContent(String content) {
            this.content = content;
        }


        public Integer getIsEditable() {
            return isEditable;
        }

        public void setIsEditable(Integer isEditable) {
            this.isEditable = isEditable;
        }



    }


}