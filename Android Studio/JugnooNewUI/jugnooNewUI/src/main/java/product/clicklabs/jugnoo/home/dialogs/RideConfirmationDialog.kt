package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.RequestRideConfirm
import product.clicklabs.jugnoo.utils.Fonts


class RideConfirmationDialog : DialogFragment() {
    private var isPickup : Boolean? = false
    private lateinit var rootView : View

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
        fun newInstance(requestRide : RequestRideConfirm): RideConfirmationDialog {
            val itemImageDialog = RideConfirmationDialog()
            val bundle = Bundle()
            bundle.putParcelable("requestRide", requestRide)
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
            dialog.window?.setBackgroundDrawableResource(R.color.black_translucent);
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_ride_confirmation, container, false)
        setFonts()
        setData()
        return rootView
    }

    private fun setFonts() {
        rootView.tvEstimateFare.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvVehicleName.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvPickup.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvDrop.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvNotes.typeface = Fonts.mavenMedium(rootView.context)
    }

    /**
     *
     */
    private fun setData() {
        if(arguments != null) {
            val requestRide : RequestRideConfirm = arguments?.getParcelable("requestRide")!!
            rootView.tvPickup.text = requestRide.pickup
            rootView.tvDrop.text = requestRide.drop
            rootView.tvVehicleName.text = requestRide.vehicleName

            if(requestRide.estimateFare.isNullOrEmpty()) {
                rootView.tvEstimateFare.visibility = View.GONE
                rootView.ivEstimateFare.visibility = View.GONE
            } else {
                rootView.tvEstimateFare.text = requestRide.estimateFare
            }

            if(!requestRide.vehicleIcon.isNullOrEmpty()) {
                Glide.with(rootView.context).load(requestRide.vehicleIcon?.replace("http:", "https:"))
                        .into(rootView.ivVehicle)
            }
            if(!requestRide.note.isNullOrEmpty()) {
                rootView.tvNotes.text = requestRide.note
            } else {
                rootView.tvNotes.visibility = View.GONE
                rootView.viewSeparatorNotes.visibility = View.GONE
            }

            if(requestRide.drop.isNullOrEmpty()) {
                rootView.groupDrop.visibility = View.GONE
            }
        }
        rootView.btnCancel.setOnClickListener {
            (context as RideRequestConfirmListener).onCancelClick(isPickup!!)
            dismiss()
        }
        rootView.btnOk.setOnClickListener {
            (context as RideRequestConfirmListener).onOkClick(isPickup!!)
            dismiss()
        }
    }

    interface RideRequestConfirmListener {
        fun onCancelClick(isPickup : Boolean)
        fun onOkClick(isPickup: Boolean)
    }
}