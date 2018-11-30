package com.fugu;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.fugu.activity.FuguBaseActivity;

/**
 * Created by bhavya on 01/08/17.
 */

public class FuguColorConfig {

    public int getFuguActionBarBg() {
        return Color.parseColor(fuguActionBarBg);
    }

    public int getFuguRunsOnColor() {
        return Color.parseColor("#627de3");
    }

    public int getFuguActionBarText() {
        return Color.parseColor(fuguActionBarText);
    }

    public int getFuguBgMessageYou() {
        return Color.parseColor(fuguBgMessageYou);
    }

    public int getFuguPrivateMsg(){
        return Color.parseColor(fuguBgPrivateMessageYou);
    }

    public int getFuguAgentBgMessageYou() {
        return Color.parseColor(fuguAgentBgMessageYou);
    }

    public int getFuguBgMessageFrom() {
        return Color.parseColor(fuguBgMessageFrom);
    }

    public int getFuguPrimaryTextMsgYou() {
        return Color.parseColor(fuguPrimaryTextMsgYou);
    }

    public int getFuguMessageRead() {
        return Color.parseColor(fuguMessageRead);
    }

    public int getFuguPrimaryTextMsgFrom() {
        return Color.parseColor(fuguPrimaryTextMsgFrom);
    }

    public int getFuguSecondaryTextMsgYou() {
        return Color.parseColor(fuguSecondaryTextMsgYou);
    }

    public int getFuguSecondaryTextMsgFrom() {
        return Color.parseColor(fuguSecondaryTextMsgFrom);
    }

    public int getFuguSecondaryTextMsgFromName() {
        return Color.parseColor(fuguSecondaryTextMsgFromName);
    }

    public int getFuguPrimaryTextMsgFromName() {
        return Color.parseColor(fuguPrimaryTextMsgFromName);
    }

    public int getFuguTextColorPrimary() {
        return Color.parseColor(fuguTextColorPrimary);
    }

    public int getFuguTextColorSecondary() {
        return Color.parseColor(fuguTextColorSecondary);
    }

    public int getFuguChannelDateText() {
        return Color.parseColor(fuguChannelDateText);
    }

    public int getFuguChatBg() {
        return Color.parseColor(fuguChatBg);
    }

    public int getFuguBorderColor() {
        return Color.parseColor(fuguBorderColor);
    }

    public int getFuguChatDateText() {
        return Color.parseColor(fuguChatDateText);
    }

    public int getFuguThemeColorPrimary() {
        return Color.parseColor(fuguThemeColorPrimary);
    }

    public int getFuguThemeColorSecondary() {
        return Color.parseColor(fuguThemeColorSecondary);
    }

    public int getFuguTypeMessageBg() {
        return Color.parseColor(fuguTypeMessageBg);
    }

    public int getFuguTypeMessageHint() {
        return Color.parseColor(fuguTypeMessageHint);
    }

    public int getFuguTypeMessageText() {
        return Color.parseColor(fuguTypeMessageText);
    }

    public int getFuguChannelBg() {
        return Color.parseColor(fuguChannelBg);
    }

    public int getFuguChannelItemBgPressed() {
        return Color.parseColor(fuguChannelItemBgPressed);
    }

    public int getFuguChannelItemBg() {
        return Color.parseColor(fuguChannelItemBg);
    }


    public int getFuguTabTextColor() {
        return Color.parseColor(fuguTabTextColor);
    }

    public int getFuguTabSelectedTextColor() {
        return Color.parseColor(fuguTabSelectedTextColor);
    }

    public int getFuguSelectedTabIndicatorColor() {
        return Color.parseColor(fuguSelectedTabIndicatorColor);
    }

    public int getFuguHomeColor() {
        return Color.parseColor(fuguHomeColor);
    }

    public int getFuguFaqDescription() {
        return Color.parseColor(hippoFaqDescription);
    }

    public int getHippoLoaderColor() {
        return Color.parseColor(hippoLoaderColor);
    }


//    public int getFuguConnecting() {
//        return Color.parseColor(fuguConnecting);
//    }

    public int getFuguNotConnected() {
        return Color.parseColor(fuguNotConnected);
    }

    public int getFuguConnected() {
        return Color.parseColor(fuguConnected);
    }


    private String fuguActionBarBg = "#627de3";
    private String fuguActionBarText = "#ffffff";
    private String fuguBgMessageYou = "#ffffff";
    private String fuguBgPrivateMessageYou = "#FEF8E3";
    private String fuguAgentBgMessageYou = "#ffffff";
    private String fuguBgMessageFrom = "#e8ecfc";
    private String fuguPrimaryTextMsgYou = "#2c2333";
    private String fuguMessageRead = "#627de3";
    private String fuguPrimaryTextMsgFrom = "#2c2333";
    private String fuguSecondaryTextMsgYou = "#8e8e8e";
    private String fuguSecondaryTextMsgFrom = "#8e8e8e";

    private String fuguPrimaryTextMsgFromName = "#aaaaaa";
    private String fuguSecondaryTextMsgFromName = "#627de3";

    private String fuguTabTextColor = "#8e8e8e";
    private String fuguTabSelectedTextColor = "#2c2333";
    private String fuguSelectedTabIndicatorColor = "#627de3";

    private String fuguTextColorPrimary = "#2c2333";
    private String fuguTextColorSecondary = "#8e8e8e";
    private String fuguChannelDateText = "#88838c";
    private String fuguChatBg = "#f8f9ff";
    private String fuguBorderColor = "#dce0e6";
    private String fuguChatDateText = "#51445c";
    private String fuguThemeColorPrimary = "#627de3";
    private String fuguThemeColorSecondary = "#6cc64d";
    private String fuguTypeMessageBg = "#ffffff";
    private String fuguTypeMessageHint = "#8e8e8e";
    private String fuguTypeMessageText = "#2c2333";
    private String fuguChannelBg = "#ffffff";
    private String fuguChannelItemBg = "#ffffff";
    private String fuguChannelItemBgPressed = "#ffd2d1d1";
    private String fuguHomeColor = "#FFFFFF";
    private String hippoFaqDescription = "#858585";
    private String hippoLoaderColor = "#0000FF";

//    private String fuguConnecting = "#FFA500";
    private String fuguNotConnected = "#FF0000";
    private String fuguConnected = "#00AA00";

    public static class Builder {
        private FuguColorConfig fuguColorConfig = new FuguColorConfig();

        public Builder fuguActionBarBg(String fuguActionBarBg) {
            fuguColorConfig.fuguActionBarBg = fuguActionBarBg;
            return this;
        }

        public Builder fuguActionBarText(String fuguActionBarText) {
            fuguColorConfig.fuguActionBarText = fuguActionBarText;
            return this;
        }

        public Builder fuguBgMessageYou(String fuguBgMessageYou) {
            fuguColorConfig.fuguBgMessageYou = fuguBgMessageYou;
            return this;
        }

        public Builder fuguAgentBgMessageYou(String fuguAgentBgMessageYou) {
            fuguColorConfig.fuguAgentBgMessageYou = fuguAgentBgMessageYou;
            return this;
        }

        public Builder fuguBgMessageFrom(String fuguBgMessageFrom) {
            fuguColorConfig.fuguBgMessageFrom = fuguBgMessageFrom;
            return this;
        }

        public Builder fuguPrimaryTextMsgYou(String fuguPrimaryTextMsgYou) {
            fuguColorConfig.fuguPrimaryTextMsgYou = fuguPrimaryTextMsgYou;
            return this;
        }

        public Builder fuguMessageRead(String fuguMessageRead) {
            fuguColorConfig.fuguMessageRead = fuguMessageRead;
            return this;
        }

        public Builder fuguPrimaryTextMsgFrom(String fuguPrimaryTextMsgFrom) {
            fuguColorConfig.fuguPrimaryTextMsgFrom = fuguPrimaryTextMsgFrom;
            return this;
        }

        public Builder fuguSecondaryTextMsgYou(String fuguSecondaryTextMsgYou) {
            fuguColorConfig.fuguSecondaryTextMsgYou = fuguSecondaryTextMsgYou;
            return this;
        }

        public Builder fuguSecondaryTextMsgFrom(String fuguSecondaryTextMsgFrom) {
            fuguColorConfig.fuguSecondaryTextMsgFrom = fuguSecondaryTextMsgFrom;
            return this;
        }

        public Builder fuguPrimaryTextMsgFromName(String fuguPrimaryTextMsgFromName) {
            fuguColorConfig.fuguPrimaryTextMsgFromName = fuguPrimaryTextMsgFromName;
            return this;
        }

        public Builder fuguSecondaryTextMsgFromName(String fuguSecondaryTextMsgFromName) {
            fuguColorConfig.fuguSecondaryTextMsgFromName = fuguSecondaryTextMsgFromName;
            return this;
        }

        public Builder fuguTextColorPrimary(String fuguTextColorPrimary) {
            fuguColorConfig.fuguTextColorPrimary = fuguTextColorPrimary;
            return this;
        }

        public Builder fuguTextColorSecondary(String fuguTextColorSecondary) {
            fuguColorConfig.fuguTextColorSecondary = fuguTextColorSecondary;
            return this;
        }

        public Builder fuguChannelDateText(String fuguChannelDateText) {
            fuguColorConfig.fuguChannelDateText = fuguChannelDateText;
            return this;
        }

        public Builder fuguChatBg(String fuguChatBg) {
            fuguColorConfig.fuguChatBg = fuguChatBg;
            return this;
        }

        public Builder fuguBorderColor(String fuguBorderColor) {
            fuguColorConfig.fuguBorderColor = fuguBorderColor;
            return this;
        }

        public Builder fuguChatDateText(String fuguChatDateText) {
            fuguColorConfig.fuguChatDateText = fuguChatDateText;
            return this;
        }

        public Builder fuguThemeColorPrimary(String fuguThemeColorPrimary) {
            fuguColorConfig.fuguThemeColorPrimary = fuguThemeColorPrimary;
            return this;
        }

        public Builder fuguThemeColorSecondary(String fuguThemeColorSecondary) {
            fuguColorConfig.fuguThemeColorSecondary = fuguThemeColorSecondary;
            return this;
        }

        public Builder fuguTypeMessageBg(String fuguTypeMessageBg) {
            fuguColorConfig.fuguTypeMessageBg = fuguTypeMessageBg;
            return this;
        }

        public Builder fuguTypeMessageHint(String fuguTypeMessageHint) {
            fuguColorConfig.fuguTypeMessageHint = fuguTypeMessageHint;
            return this;
        }

        public Builder fuguTypeMessageText(String fuguTypeMessageText) {
            fuguColorConfig.fuguTypeMessageText = fuguTypeMessageText;
            return this;
        }

        public Builder fuguChannelBg(String fuguChannelBg) {
            fuguColorConfig.fuguChannelBg = fuguChannelBg;
            return this;
        }

        public Builder fuguChannelItemBgPressed(String fuguChannelItemBgPressed) {
            fuguColorConfig.fuguChannelItemBgPressed = fuguChannelItemBgPressed;
            return this;
        }

        public Builder fuguChannelItemBg(String fuguChannelItemBg) {
            fuguColorConfig.fuguChannelItemBg = fuguChannelItemBg;
            return this;
        }

        public Builder fuguTabTextColor(String fuguTabTextColor) {
            fuguColorConfig.fuguTabTextColor = fuguTabTextColor;
            return this;
        }

        public Builder fuguTabSelectedTextColor(String fuguTabSelectedTextColor) {
            fuguColorConfig.fuguTabSelectedTextColor = fuguTabSelectedTextColor;
            return this;
        }

        public Builder fuguSelectedTabIndicatorColor(String fuguSelectedTabIndicatorColor) {
            fuguColorConfig.fuguSelectedTabIndicatorColor = fuguSelectedTabIndicatorColor;
            return this;
        }

        public Builder fuguHomeColor(String fuguHomeColor) {
            fuguColorConfig.fuguHomeColor = fuguHomeColor;
            return this;
        }

        public Builder hippoFaqDescription(String hippoFaqDescription) {
            fuguColorConfig.hippoFaqDescription = hippoFaqDescription;
            return this;
        }

        public Builder huguLoaderColor(String huguLoaderColor) {
            fuguColorConfig.hippoLoaderColor = huguLoaderColor;
            return this;
        }

        public Builder fuguBgPrivateMessageYou(String fuguBgPrivateMessageYou) {
            fuguColorConfig.fuguBgPrivateMessageYou = fuguBgPrivateMessageYou;
            return this;
        }

//        public Builder fuguConnecting(String fuguConnecting) {
//            fuguColorConfig.fuguConnecting = fuguConnecting;
//            return this;
//        }
        public Builder fuguNotConnected(String fuguNotConnected) {
            fuguColorConfig.fuguNotConnected = fuguNotConnected;
            return this;
        }
        public Builder fuguConnected(String fuguConnected) {
            fuguColorConfig.fuguConnected = fuguConnected;
            return this;
        }

        public FuguColorConfig build() {
            return fuguColorConfig;
        }

    }

    public static StateListDrawable makeSelector(int color, int colorPressed) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(0, colorPressed, false));
        res.addState(new int[]{}, roundedBackground(0, color, false));
        return res;
    }

    public static StateListDrawable makeRoundedSelector(int color) {
        return makeRoundedSelector(color, 150);
    }
    public static StateListDrawable makeRoundedSelector(int color, float radius) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(radius, color, true));
        res.addState(new int[]{}, roundedBackground(radius, color, false));
        return res;
    }
    /*
    public static StateListDrawable makeRoundedSelector(int color) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(150, color, true));
        res.addState(new int[]{}, roundedBackground(150, color, false));
        return res;
    }*/

    private static ShapeDrawable roundedBackground(float radius, int color, boolean isPressed) {
        ShapeDrawable footerBackground = new ShapeDrawable();

        // The corners are ordered top-left, top-right, bottom-right,
        // bottom-left. For each corner, the array contains 2 values, [X_radius,
        // Y_radius]

        float[] radii = new float[8];
        radii[0] = radius;
        radii[1] = radius;

        radii[2] = radius;
        radii[3] = radius;

        radii[4] = radius;
        radii[5] = radius;

        radii[6] = radius;
        radii[7] = radius;

        footerBackground.setShape(new RoundRectShape(radii, null, null));

        footerBackground.getPaint().setColor(color);
        if (isPressed)
            footerBackground.setAlpha(250);

        return footerBackground;
    }

}
