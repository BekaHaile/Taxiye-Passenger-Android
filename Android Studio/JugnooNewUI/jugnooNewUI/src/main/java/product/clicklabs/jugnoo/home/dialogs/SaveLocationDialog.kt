package product.clicklabs.jugnoo.home.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_save_location.view.*
import product.clicklabs.jugnoo.R

class SaveLocationDialog : DialogFragment() {
    private var lat : Double? = 0.0
    private var lng : Double? = 0.0
    private var address : String? = ""
    private var isPickup : Boolean? = false
    private lateinit var rootView : View

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog)
    }

    companion object {
        @JvmStatic
        fun newInstance(lat : Double, lng : Double, address : String, isPickup : Boolean): SaveLocationDialog {
            val itemImageDialog = SaveLocationDialog()
            val bundle = Bundle()
            bundle.putString("address", address)
            bundle.putDouble("lat", lat)
            bundle.putDouble("lng", lng)
            bundle.putBoolean("isPickup", isPickup)
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
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_save_location, container, false)
        initView(rootView)
        setData()
        return rootView
    }

    /**
     *
     * @param view View
     */
    private fun initView(view: View) {
        setFonts()
    }

    /**
     *
     */
    private fun setFonts() {
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
        }
        rootView.ivSkip.setOnClickListener {
            (context as SaveLocationListener).onSkipClicked(isPickup!!)
            dismiss()
        }
    }

    interface SaveLocationListener {
        fun onSkipClicked(isPickup : Boolean)
        fun onSaveLocationClick(isPickup: Boolean)
    }
}