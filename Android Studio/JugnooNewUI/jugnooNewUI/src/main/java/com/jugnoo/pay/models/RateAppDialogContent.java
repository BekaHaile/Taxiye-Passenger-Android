package com.jugnoo.pay.models;

/**
 * Created by Ankit on 4/29/16.
 */
public class RateAppDialogContent {

    private String title;
    private String text;
    private String confirmButtonText;
    private String cancelButtonText;
    private String neverButtonText;
    private String url;

    public RateAppDialogContent(String title, String text, String confirmButtonText, String cancelButtonText, String neverButtonText, String url) {
        this.title = title;
        this.text = text;
        this.confirmButtonText = confirmButtonText;
        this.cancelButtonText = cancelButtonText;
        this.neverButtonText = neverButtonText;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConfirmButtonText() {
        return confirmButtonText;
    }

    public void setConfirmButtonText(String confirmButtonText) {
        this.confirmButtonText = confirmButtonText;
    }

    public String getCancelButtonText() {
        return cancelButtonText;
    }

    public void setCancelButtonText(String cancelButtonText) {
        this.cancelButtonText = cancelButtonText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNeverButtonText() {
        return neverButtonText;
    }

    public void setNeverButtonText(String neverButtonText) {
        this.neverButtonText = neverButtonText;
    }
}
