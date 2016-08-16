package sabkuchfresh.retrofit.model;

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
    private boolean expanded;

    public NotificationData(int notificationId, String timePushArrived, String title, String message, int deepIndex, String timeToDisplay, String timeTillDisplay, String notificationImage) {
        this.notificationId = notificationId;
        this.timePushArrived = timePushArrived;
        this.title = title;
        this.message = message;
        this.deepIndex = deepIndex;
        this.timeToDisplay = timeToDisplay;
        this.timeTillDisplay = timeTillDisplay;
        this.notificationImage = notificationImage;
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
}
