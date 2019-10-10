package product.clicklabs.jugnoo.home.dialogs

import android.app.Activity
import android.app.Dialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import product.clicklabs.jugnoo.widgets.PrefixedEditText

object EnterBidDialog {

    fun show(activity: Activity, title:String?, message:String?,
             etHint:String?, etPrefix:String?,
             buttonText:String?, cancellable:Boolean, callback:Callback?) {

        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_edittext_confirm)

            val frameLayout = dialog.findViewById<RelativeLayout>(R.id.rv)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(cancellable)
            dialog.setCanceledOnTouchOutside(cancellable)


            val textHead = dialog.findViewById<TextView>(R.id.textHead)
            textHead.typeface = Fonts.mavenMedium(activity)
            val textMessage = dialog.findViewById<TextView>(R.id.textMessage)
            textMessage.typeface = Fonts.mavenMedium(activity)
            val editTextNumber = dialog.findViewById<PrefixedEditText>(R.id.etCode)
            editTextNumber.typeface = Fonts.mavenMedium(activity)

            textHead.text = title
            textMessage.text = message

            textHead.visibility = if(TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
            textMessage.visibility = if(TextUtils.isEmpty(message)) View.GONE else View.VISIBLE
            if(!TextUtils.isEmpty(etHint)){
                editTextNumber.hint = etHint
            }
            if(!TextUtils.isEmpty(etPrefix)){
                editTextNumber.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        editTextNumber.setPrefix(if(s?.isNullOrEmpty() == true) "" else etPrefix)
                        editTextNumber.hint = if(s?.isNullOrEmpty() == true) etHint else ""
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })
            }

            val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
            btnConfirm.typeface = Fonts.mavenMedium(activity)
            if(!TextUtils.isEmpty(buttonText)){
                btnConfirm.text = buttonText
            }

            btnConfirm.setOnClickListener {
                val value = editTextNumber.text.toString()
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show()
                } else {
                    Utils.hideSoftKeyboard(activity, editTextNumber)
                    dialog.dismiss()
                    callback?.onButtonClick(value)
                }
            }

            dialog.findViewById<View>(R.id.rl1).setOnClickListener { }

            frameLayout.setOnClickListener { if(cancellable)dialog.dismiss() }


            dialog.show()
            editTextNumber.postDelayed({ Utils.showSoftKeyboard(activity, editTextNumber) }, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun showRaiseFareDialog(activity: Activity, message:String?,
             buttonText:String?, minValue:Double, cancellable:Boolean, callback:Callback?) {

        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_two_buttons_capsule_vert)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(cancellable)
            dialog.setCanceledOnTouchOutside(cancellable)


            val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
            tvMessage.typeface = Fonts.mavenMedium(activity)
            tvMessage.text = message

            tvMessage.visibility = if(TextUtils.isEmpty(message)) View.GONE else View.VISIBLE

            val bPositive = dialog.findViewById<Button>(R.id.bPositive)
            bPositive.typeface = Fonts.mavenMedium(activity)
            if(!TextUtils.isEmpty(buttonText)){
                bPositive.text = buttonText
            }

            bPositive.setOnClickListener {
                dialog.dismiss()
                callback?.onButtonClick(minValue.toString())
            }

            val bNegative = dialog.findViewById<Button>(R.id.bNegative)
            bNegative.typeface = Fonts.mavenMedium(activity)
            bNegative.setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface Callback{
        fun onButtonClick(value:String?)
    }

}