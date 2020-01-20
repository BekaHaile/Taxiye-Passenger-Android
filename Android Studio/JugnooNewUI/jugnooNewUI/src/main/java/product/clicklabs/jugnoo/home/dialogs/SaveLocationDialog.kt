package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.synthetic.main.dialog_save_location.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.ASSL



class SaveLocationDialog : DialogFragment() {
    private var lat : Double? = 0.0
    private var lng : Double? = 0.0
    private var address : String? = ""
    private var isPickup : Boolean? = false
    private lateinit var rootView : View
    private var finalViewHeight : Float? = 0f

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(lat : Double, lng : Double, address : String, isPickup : Boolean, finalViewHeight : Float): SaveLocationDialog {
            val itemImageDialog = SaveLocationDialog()
            val bundle = Bundle()
            bundle.putString("address", address)
            bundle.putDouble("lat", lat)
            bundle.putDouble("lng", lng)
            bundle.putBoolean("isPickup", isPickup)
            bundle.putFloat("finalViewHeight", finalViewHeight)
            itemImageDialog.arguments = bundle
            return itemImageDialog
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_save_location, container, false)
        setData()
        return rootView
    }

    /**
     *
     */
    private fun setData() {
        if(arguments != null) {
            lat = arguments?.getDouble("lat")
            lng = arguments?.getDouble("lng")
            address = arguments?.getString("address")
            isPickup = arguments?.getBoolean("isPickup")
            finalViewHeight = arguments?.getFloat("finalViewHeight")
            rootView.setPadding(80, 0, 80, (ASSL.Yscale() * finalViewHeight!!).toInt())
            if(isPickup!!) {
                rootView.ivLocationMarker.setImageResource(R.drawable.pin_ball_start)
                rootView.tvSaveAddress.setText(R.string.save_your_pickup_location)
            } else {
                rootView.ivLocationMarker.setImageResource(R.drawable.pin_ball_end)
                rootView.tvSaveAddress.setText(R.string.save_your_drop_location)
            }
        }
        rootView.ivSkip.setOnClickListener {
            (context as SaveLocationListener).onSkipClicked(isPickup!!)
            dismiss()
        }
        rootView.tvSaveAddress.setOnClickListener {
            (context as SaveLocationListener).onSaveLocationClick(isPickup!!)
            dismiss()
        }
        rootView.ivSaveLocation.setOnClickListener {
            (context as SaveLocationListener).onSaveLocationClick(isPickup!!)
            dismiss()
        }
    }

    interface SaveLocationListener {
        fun onSkipClicked(isPickup : Boolean)
        fun onSaveLocationClick(isPickup: Boolean)
    }
}