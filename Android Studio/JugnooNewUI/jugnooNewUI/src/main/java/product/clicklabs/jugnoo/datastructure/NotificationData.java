package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo on 10/15/15.
 */
public class NotificationData {

    private int notificationId;
    private String timePushArrived;
    private String message;
    private String deepIndex;
    private String timeToDisplay;
    private String timeTillDisplay;
    private String notificationImage;

    public NotificationData(int notificationId, String timePushArrived, String message, String deepIndex, String timeToDisplay, String timeTillDisplay, String notificationImage) {
        this.notificationId = notificationId;
        this.timePushArrived = timePushArrived;
        this.message = message;
        this.deepIndex = deepIndex;
        this.timeToDisplay = timeToDisplay;
        this.timeTillDisplay = timeTillDisplay;
        this.notificationImage = notificationImage;
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

    public String getDeepIndex() {
        return deepIndex;
    }

    public void setDeepIndex(String deepIndex) {
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
}
