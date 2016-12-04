package com.jugnoo.pay.utils;

public interface FacebookLoginCallback {
	void facebookLoginDone(FacebookUserData facebookUserData);
    void facebookLoginError(String message);
}
