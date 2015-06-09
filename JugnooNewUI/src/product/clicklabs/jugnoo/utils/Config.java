package product.clicklabs.jugnoo.utils;

import com.loopj.android.http.AsyncHttpClient;

public class Config {

	public static AppRunMode appRunMode = AppRunMode.DEV;

	public enum AppRunMode {
<<<<<<< HEAD
		DEV, TEST
=======
		DEV, TEST, LIVE
>>>>>>> develop
	}

	
	public static AsyncHttpClient getAsyncHttpClient(){
		if(AppRunMode.TEST == appRunMode){
			return new CustomAsyncHttpClient();
		}
		else{
			return new AsyncHttpClient();
		}
	}
	
	
	public static HttpRequesterFinal getHttpRequester(){
		if(AppRunMode.TEST == appRunMode){
			return new CustomHttpRequesterFinal();
		}
		else{
			return new HttpRequesterFinal();
		}
	}
	
}