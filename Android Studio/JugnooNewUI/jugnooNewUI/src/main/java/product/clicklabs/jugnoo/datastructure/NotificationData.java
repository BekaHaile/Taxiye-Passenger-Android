package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socomo on 10/15/15.
 */
public class NotificationData {

    @SerializedName("notification_id")
    @Expose
    private int notificationId;
    @SerializedName("timePushArrived")
    @Expose
    private String timePushArrived;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("deepindex")
    @Expose
    private int deepIndex;
    @SerializedName("timeToDisplay")
    @Expose
    private String timeToDisplay;
    @SerializedName("timeTillDisplay")
    @Expose
    private String timeTillDisplay;
    @SerializedName("image")
    @Expose
    private String notificationImage;
    @SerializedName("url")
    @Expose
    private String url;
    private boolean expanded;
    @SerializedName("post_id")
    @Expose
    private int postId;
    @SerializedName("post_notification_id")
    @Expose
    private int postNotificationId;
    @SerializedName("restaurant_id")
    @Expose
    private int restaurantId;
    @SerializedName("feedback_id")
    @Expose
    private int feedbackId;

    public NotificationData(int notificationId, String timePushArrived, String title, String message, int deepIndex,
                            String timeToDisplay, String timeTillDisplay, String notificationImage, String url) {
        this.notificationId = notificationId;
        this.timePushArrived = timePushArrived;
        this.title = title;
        this.message = message;
        this.deepIndex = deepIndex;
        this.timeToDisplay = timeToDisplay;
        this.timeTillDisplay = timeTillDisplay;
        this.notificationImage = notificationImage;
        this.url = url;
        this.expanded = false;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDeepIndex() {
        return deepIndex;
    }

    public void setDeepIndex(int deepIndex) {
        this.deepIndex = deepIndex;
    }

    public String getTimeToDisplay() {
        return timeToDisplay;
    }

    public void setTimeToDisplay(String timeToDisplay) {
        this.timeToDisplay = timeToDisplay;
    }

    public String getTimeTillDisplay() {
        return timeTillDisplay;
    }

    public void setTimeTillDisplay(String timeTillDisplay) {
        this.timeTillDisplay = timeTillDisplay;
    }

    public String getTimePushArrived() {
        return timePushArrived;
    }

    public void setTimePushArrived(String timePushArrived) {
        this.timePushArrived = timePushArrived;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostNotificationId() {
        return postNotificationId;
    }

    public void setPostNotificationId(int postNotificationId) {
        this.postNotificationId = postNotificationId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }
}
