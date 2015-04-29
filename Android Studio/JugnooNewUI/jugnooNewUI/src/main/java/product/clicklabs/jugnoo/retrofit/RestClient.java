package product.clicklabs.jugnoo.retrofit;

import product.clicklabs.jugnoo.config.Config;
import retrofit.RestAdapter;


/**
 * Rest client
 */
public class RestClient {
    private static ApiService apiService = null;

    public static ApiService getApiService() {
        if (apiService == null) {

           // For object response which is default
//            RestAdapter restAdapter = new RestAdapter.Builder()
//                    .setEndpoint(Config.getServerUrl())
//                    .build();


//            //For type string response
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Config.getServerUrl())
                    .setConverter(new StringConverter())    //converter for response type
                    .build();


            apiService = restAdapter.create(ApiService.class);
        }
        return apiService;
    }


}
