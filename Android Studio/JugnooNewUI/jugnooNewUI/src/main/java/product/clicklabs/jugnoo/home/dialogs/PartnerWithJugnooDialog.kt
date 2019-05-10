package product.clicklabs.jugnoo.home.dialogs


import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.view.WindowManager
import com.sabkuchfresh.utils.Utils
import kotlinx.android.synthetic.main.dialog_partner.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Prefs

/**
 * Created by shankar on 5/2/16.
 */
class PartnerWithJugnooDialog {

    var dialog:Dialog? = null

    fun show(activity: Activity, callback: Callback) {
        try {
            if(dialog != null && dialog!!.isShowing){
                return
            }
            dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog!!.setContentView(R.layout.dialog_partner)
            with(dialog!!){
                val layoutParams = window!!.attributes
                layoutParams.dimAmount = 0.6f
                window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCancelable(true)
                setCanceledOnTouchOutside(true)

                ivClose.setOnClickListener { dialog!!.dismiss() }
                bPartnerWithJugnoo.setOnClickListener {
                    try {
                        Utils.openUrl(activity, Prefs.with(activity)
                                .getString(Constants.KEY_CUSTOMER_PARTNER_URL, "https://www.jugnoo.in/franchise/"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dialog!!.dismiss()
                }
                relative.setOnClickListener { dialog!!.dismiss() }

                setOnDismissListener(DialogInterface.OnDismissListener {
                    callback.dialogDismissed()
                })
            }
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface Callback{
        fun dialogDismissed()
    }

}