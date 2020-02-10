package product.clicklabs.jugnoo

import android.content.Context
import com.hippocall.HippoCallConfig
import org.json.JSONObject

object HippoCallStub {

    @JvmStatic
    fun init(){
        HippoCallConfig.getInstance().setCallBackListener()
        HippoCallConfig.getInstance().setHippoCallPushIcon(R.mipmap.ic_launcher)
        HippoCallConfig.getInstance().setHippoCallsmaillPushIcon(R.mipmap.notification_icon)
    }

    @JvmStatic
    fun onNotificationReceived(context: Context, messageJson: JSONObject){
        HippoCallConfig.getInstance().onNotificationReceived(context, messageJson)
    }

}