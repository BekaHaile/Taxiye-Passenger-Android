package com.fugu.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public interface FuguAppConstant {

    String DEV_SERVER = "https://alpha-api.fuguchat.com";
    //String TEST_SERVER = "https://beta-api.fuguchat.com"; // 13.126.0.203:3001
    String TEST_SERVER = "https://hippo-api-dev.fuguchat.com:3011";
    String BETA_SERVER = "https://beta-hippo.fuguchat.com";
    String BETA_LIVE_SERVER = "https://api.fuguchat.com";
    String LIVE_SERVER = "https://api.fuguchat.com";

    String AGENT_TEST_SERVER = "https://hippo-api-dev.fuguchat.com:3011";
    String AGENT_BETA_LIVE_SERVER = "https://beta-live-api.fuguchat.com";
    String AGENT_BETA_SERVER = "https://beta-hippo.fuguchat.com";
    String AGENT_LIVE_SERVER = "https://api.fuguchat.com";



    int SESSION_EXPIRE = 403;
    int DATA_UNAVAILABLE = 406;
    int INVALID_VIDEO_CALL_CREDENTIALS = 413;
    String NETWORK_STATE_INTENT = "network_state_changed";
    String NOTIFICATION_INTENT = "notification_received";
    String NOTIFICATION_TAPPED = "notification_tapped";
    String FUGU_WEBSITE_URL = "https://fuguchat.com";
    String VIDEO_CALL_INTENT = "hippo_video_call_intent";
    String VIDEO_CALL_HUNGUP = "hippo_video_call_hungup";

    int IMAGE_MESSAGE = 10;
    int FILE_MESSAGE = 11;
    int ACTION_MESSAGE = 12;
    int TEXT_MESSAGE = 1;
    int PRIVATE_NOTE = 3;
    int READ_MESSAGE = 6;
    int FEEDBACK_MESSAGE = 14;
    int VIDEO_CALL = 18;

    int VIDEO_CALL_VIEW = 1;
    int AUDIO_CALL_VIEW = 2;

    int ANDROID_USER = 1;

    int CHANNEL_SUBSCRIBED = 1;
    int CHANNEL_UNSUBSCRIBED = 0;

    int STATUS_CHANNEL_CLOSED = 0;
    int STATUS_CHANNEL_OPEN = 1;

    int TYPING_SHOW_MESSAGE = 0;
    int TYPING_STARTED = 1;
    int TYPING_STOPPED = 2;

    int MESSAGE_SENT = 1;
    int MESSAGE_DELIVERED = 2;
    int MESSAGE_READ = 3;
    int MESSAGE_UNSENT = 4;
    int MESSAGE_IMAGE_RETRY = 5;
    int MESSAGE_FILE_RETRY = 6;

    int PERMISSION_CONSTANT_CAMERA = 9;
    int PERMISSION_CONSTANT_GALLERY = 8;
    int PERMISSION_READ_IMAGE_FILE = 4;
    int PERMISSION_SAVE_BITMAP = 5;
    int PERMISSION_READ_FILE = 6;


    int OPEN_CAMERA_ADD_IMAGE = 514;
    int OPEN_GALLERY_ADD_IMAGE = 515;
    int SELECT_FILE = 516;

    //Notification Type
    int NOTIFICATION_DEFAULT = -1;
    int NOTIFICATION_READ_ALL = 6;

    //For agent
    int AGENT_TEXT_MESSAGE = 1;
    int ASSIGN_CHAT = 3;
    int AGENT_REALALL = 6;
    int NEW_AGENT_ADDED = 10;
    int AGENT_STATUS_CHANGED = 11;

    // action
    String FUGU_CUSTOM_ACTION_SELECTED = "FUGU_CUSTOM_ACTION_SELECTED";
    String FUGU_CUSTOM_ACTION_PAYLOAD = "FUGU_CUSTOM_ACTION_PAYLOAD";
    String FUGU_LISTENER_NULL = "fugu_listener_null";

    String IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "fugu" +
            File.separator + "picture";
    String CONVERSATION = "conversation";
    String NOTIFICATION_TYPE = "notification_type";
    String USER_ID = "user_id";
    String EN_USER_ID = "en_user_id";
    String PEER_CHAT_PARAMS = "peer_chat_params";
    String APP_SECRET_KEY = "app_secret_key";
    String DEVICE_TYPE = "device_type";
    String DEVICE_DETAILS = "device_details";
    String RESELLER_TOKEN = "reseller_token";
    String REFERENCE_ID = "reference_id";
    String DEVICE_ID = "device_id";
    String APP_TYPE = "app_type";
    String APP_VERSION="app_version";
    String APP_VERSION_CODE = "app_version_code";
    String ANDROID="Android";
    String USER_UNIQUE_KEY = "user_unique_key";
    String FULL_NAME = "full_name";
    String MESSAGE = "message";
    String NEW_MESSAGE = "new_message";
    String MESSAGE_STATUS = "message_status";
    String MESSAGE_INDEX = "message_index";
    String MESSAGE_TYPE = "message_type";
    String USER_TYPE = "user_type";
    String DATE_TIME = "date_time";
    String EMAIL = "email";
    String PHONE_NUMBER = "phone_number";
    String DEVICE_TOKEN = "device_token";
    String IS_TYPING = "is_typing";
    String CHANNEL_ID = "channel_id";
    String LABEL_ID = "label_id";
    String UNREAD_COUNT = "unread_count";
    String ON_SUBSCRIBE = "on_subscribe";
    String IMAGE_URL = "image_url";
    String THUMBNAIL_URL = "thumbnail_url";
    String ADDRESS = "address";
    String LAT_LONG = "lat_long";
    String IP_ADDRESS = "ip_address";
    String ATTRIBUTES = "attributes";
    String CUSTOM_ATTRIBUTES = "custom_attributes";
    String ISP2P = "isP2P";
    String CHAT_TYPE = "chat_type";
    String ERROR = "error";
    String INTRO_MESSAGE = "intro_message";
    String CUSTOM_ACTION = "custom_action";
    String GROUPING_TAGS = "grouping_tags";
    String ACCESS_TOKEN = "access_token";
    String APP_SOURCE = "source";
    String STATUS = "status";
    String TYPE = "type";
    String PAGE_START = "page_start";
    String PAGE_END = "page_end";
    int ASSIGNMENT_MESSAGE = 2;
    String TAGS_DATA = "tag_data";
    String FRAGMENT_TYPE = "fragment_type";
    String AUTH_TOKEN = "auth_token";
    String CREATE_NEW_CHAT = "create_chat";
    String OTHER_USER_UNIQUE_KEY = "other_user_unique_key";
    String RESPONSE_TYPE = "response_type";
    String APP_SOURCE_TYPE = "source_type";
    String CREATED_BY = "created_by";
    String DEAL_ID = "deal_id";

    String SUPPORT_ID = "support_id";
    String SUPPORT_TRANSACTION_ID = "support_transaction_id";
    String SUPPORT_IS_ACTIVE = "is_active";
    String SOURCE_KEY = "source";

    String CHANNEL_NAME = "channel_name";
    String MESSAGE_UNIQUE_ID = "muid";
    String VIDEO_CALL_TYPE = "video_call_type";
    String IS_SILENT = "is_silent";
    String TITLE = "title";

    /**
     * The type of file being Saved
     */
    enum FileType {

        LOG_FILE("logs", ".log"),
        IMAGE_FILE("snapshots", ".jpg"),
        GENERAL_FILE("public", ".txt"),
        PDF_FILE("public", ".pdf"),
        PRIVATE_FILE("system", ".sys");

        public final String extension;
        public final String directory;

        FileType(String relativePath, String extension) {
            this.extension = extension;
            this.directory = relativePath;
        }
    }

    interface DataType {

        String NUMBER = "Number";
        String TEXT = "Text";
        String IMAGE = "Image";
        String DATE = "Date";
        String DROP_DOWN = "Dropdown";
        String CHECKBOX = "Checkbox";
        String TELEPHONE = "Telephone";
        String PHONE = "phone";
        String EMAIL = "email";
        String URL = "URL";
        String DATE_FUTURE = "Date-Future";
        String DATE_PAST = "Date-Past";
        String DATE_TODAY = "Date-Today";
        String CHECKLIST = "Checklist";
        String TABLE = "Table";
        String DATETIME = "Date-Time";
        String DATETIME_FUTURE = "Datetime-Future";
        String DATETIME_PAST = "Datetime-Past";
        String BARCODE = "Barcode";

    }


    public enum CallType {
        AUDIO("AUDIO"),
        VIDEO("VIDEO");

        public final String type;


        /**
         * @param type
         */
        CallType(final String type) {
            this.type = type;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return type;
        }
    }
}
