package product.clicklabs.jugnoo.home.dialogs

import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Fonts

object ThreeButtonDialog {


    fun show(activity: Activity, title:String?, message:String?,
             positiveText:String?,
             neutralText:String?,
             negativeText:String?,
             cancellable:Boolean,
             callback: Callback?) {

        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_three_buttons_capsule_vert)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(cancellable)
            dialog.setCanceledOnTouchOutside(cancellable)


            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            tvTitle.typeface = Fonts.mavenMedium(activity)
            tvTitle.text = title
            tvTitle.visibility = if(TextUtils.isEmpty(title)) View.GONE else View.VISIBLE

            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
            tvMessage.typeface = Fonts.mavenRegular(activity)
            tvMessage.text = message
            tvMessage.visibility = if(TextUtils.isEmpty(message)) View.GONE else View.VISIBLE

            val bPositive = dialog.findViewById<Button>(R.id.bPositive)
            bPositive.typeface = Fonts.mavenMedium(activity)
            if(!TextUtils.isEmpty(positiveText)){
                bPositive.text = positiveText
            }
            bPositive.setOnClickListener {
                dialog.dismiss()
                callback?.onPositiveClick()
            }

            val bNeutral = dialog.findViewById<Button>(R.id.bNeutral)
            bNeutral.typeface = Fonts.mavenMedium(activity)
            if(!TextUtils.isEmpty(neutralText)){
                bNeutral.text = neutralText
            }
            bNeutral.setOnClickListener{
                dialog.dismiss()
                callback?.onNeutralClick()
            }

            val bNegative = dialog.findViewById<Button>(R.id.bNegative)
            bNegative.typeface = Fonts.mavenMedium(activity)
            if(!TextUtils.isEmpty(negativeText)){
                bNegative.text = negativeText
            }
            bNegative.setOnClickListener{
                dialog.dismiss()
                callback?.onNegativeClick()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    interface Callback{
        fun onPositiveClick()
        fun onNeutralClick()
        fun onNegativeClick()
    }


}