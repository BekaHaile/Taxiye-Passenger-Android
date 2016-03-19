package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo on 10/15/15.
 */
public class NotificationData {

    private int notificationId;
    private String timePushArrived;
    private String title;
    private String message;
    private String deepIndex;
    private String timeToDisplay;
    private String timeTillDisplay;
    private String notificationImage;
    private boolean expanded;

    public NotificationData(int notificationId, String timePushArrived, String title, String message, String deepIndex, String timeToDisplay, String timeTillDisplay, String notificationImage) {
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
