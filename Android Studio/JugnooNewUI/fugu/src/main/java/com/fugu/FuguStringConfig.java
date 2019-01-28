package com.fugu;

/**
 * Created by gurmail on 31/07/18.
 *
 * @author gurmail
 */

public class FuguStringConfig {

    private String fuguDisplayNameForCustomers = "Fleet";
    private String fuguBroadCastTitle = "This will send message to your active*";
    private String fuguBroadCastTitleInfo = "*Based on past 30 days login activity.";
    private String fuguSelectTeamsString = "Select Team";
    private String fuguShowString = "Show";
    private String fuguSelectString = "Select";
    private String fuguSelectedString = "Selected";
    private String fuguTitleString = "Title";
    private String fuguMessageString = "Message";
    private String fuguSeePreviousMessges = "See Previous Messages";
    private String fuguAllTeamString = "All Teams";
    private String fuguAllAgentsString = "All";
    private String fuguSendButtonString = "Send";

    private String fuguServerDisconnect = "Server disconnected";
    private String fuguNoNetworkConnected = "No internet connected";
    private String fuguFetchingMessages = "Updating new messages";
    private String fuguServerConnecting = "Server connecting... ";

    public String getFuguServerConnecting() {
        return fuguServerConnecting;
    }

    public String getFuguServerDisconnect() {
        return fuguServerDisconnect;
    }

    public String getFuguFetchingMessages() {
        return fuguFetchingMessages;
    }

    public String getFuguNoNetworkConnected() {
        return fuguNoNetworkConnected;
    }

    public String getFuguDisplayNameForCustomers() {
        return fuguDisplayNameForCustomers;
    }

    public String getFuguBroadCastTitle() {
        return fuguBroadCastTitle;
    }

    public String getFuguBroadCastTitleInfo() {
        return fuguBroadCastTitleInfo;
    }

    public String getFuguSelectTeamsString() {
        return fuguSelectTeamsString;
    }

    public String getFuguShowString() {
        return fuguShowString;
    }

    public String getFuguSelectString() {
        return fuguSelectString;
    }

    public String getFuguSelectedString() {
        return fuguSelectedString;
    }

    public String getFuguTitleString() {
        return fuguTitleString;
    }

    public String getFuguMessageString() {
        return fuguMessageString;
    }

    public String getFuguSeePreviousMessges() {
        return fuguSeePreviousMessges;
    }

    public String getFuguAllTeamString() {
        return fuguAllTeamString;
    }

    public String getFuguAllAgentsString() {
        return fuguAllAgentsString;
    }

    public String getFuguSendButtonString() {
        return fuguSendButtonString;
    }

    public static class Builder {

        private FuguStringConfig fuguStringConfig = new FuguStringConfig();

        public Builder fuguBroadCastTitle(String fuguBroadCastTitle) {
            fuguStringConfig.fuguBroadCastTitle = fuguBroadCastTitle;
            return this;
        }

        public Builder fuguBroadCastTitleInfo(String fuguBroadCastTitleInfo) {
            fuguStringConfig.fuguBroadCastTitleInfo = fuguBroadCastTitleInfo;
            return this;
        }

        public Builder fuguSelectTeamsString(String fuguSelectTeamsString) {
            fuguStringConfig.fuguSelectTeamsString = fuguSelectTeamsString;
            return this;
        }

        public Builder fuguShowString(String fuguShowString) {
            fuguStringConfig.fuguShowString = fuguShowString;
            return this;
        }

        public Builder fuguSelectString(String fuguSelectString) {
            fuguStringConfig.fuguSelectString = fuguSelectString;
            return this;
        }

        public Builder fuguSelectedString(String fuguSelectedString) {
            fuguStringConfig.fuguSelectedString = fuguSelectedString;
            return this;
        }

        public Builder fuguTitleString(String fuguTitleString) {
            fuguStringConfig.fuguTitleString = fuguTitleString;
            return this;
        }

        public Builder fuguMessageString(String fuguMessageString) {
            fuguStringConfig.fuguMessageString = fuguMessageString;
            return this;
        }

        public Builder fuguSeePreviousMessges(String fuguSeePreviousMessges) {
            fuguStringConfig.fuguSeePreviousMessges = fuguSeePreviousMessges;
            return this;
        }

        public Builder fuguAllTeamString(String fuguAllTeamString) {
            fuguStringConfig.fuguAllTeamString = fuguAllTeamString;
            return this;
        }

        public Builder fuguAllAgentsString(String fuguAllAgentsString) {
            fuguStringConfig.fuguAllAgentsString = fuguAllAgentsString;
            return this;
        }

        public Builder fuguDisplayNameForCustomers(String fuguDisplayNameForCustomers) {
            fuguStringConfig.fuguDisplayNameForCustomers = fuguDisplayNameForCustomers;
            return this;
        }

        public Builder fuguSendButtonString(String fuguSendButtonString) {
            fuguStringConfig.fuguSendButtonString = fuguSendButtonString;
            return this;
        }

        public Builder fuguServerDisconnect(String fuguServerDisconnect) {
            fuguStringConfig.fuguServerDisconnect = fuguServerDisconnect;
            return this;
        }

        public Builder fuguNoNetworkConnected(String fuguNoNetworkConnected) {
            fuguStringConfig.fuguNoNetworkConnected = fuguNoNetworkConnected;
            return this;
        }

        public Builder fuguFetchingMessages(String fuguFetchingMessages) {
            fuguStringConfig.fuguFetchingMessages = fuguFetchingMessages;
            return this;
        }

        public Builder fuguServerConnecting(String fuguServerConnecting) {
            fuguStringConfig.fuguServerConnecting = fuguServerConnecting;
            return this;
        }

        public FuguStringConfig build() {
            return fuguStringConfig;
        }
    }
}
