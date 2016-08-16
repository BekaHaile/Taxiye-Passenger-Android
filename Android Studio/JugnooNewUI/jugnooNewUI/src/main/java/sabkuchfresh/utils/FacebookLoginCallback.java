package sabkuchfresh.utils;

public interface FacebookLoginCallback {
	void facebookLoginDone(FacebookUserData facebookUserData);
    void facebookLoginError(String message);
}
