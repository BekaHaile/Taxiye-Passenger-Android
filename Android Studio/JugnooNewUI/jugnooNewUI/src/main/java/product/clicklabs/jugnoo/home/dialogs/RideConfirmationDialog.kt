package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.graphics.Typeface.BOLD
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.*
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.btnCancel
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.btnOk
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.etAdditionalFare
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.groupDrop
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.ivVehicle
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.tvDrop
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.tvLabel
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.tvPickup
import kotlinx.android.synthetic.main.dialog_ride_confirmation.view.tvVehicleName
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.RequestRideConfirm
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils


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
            val rideConfirmationDialog = RideConfirmationDialog()
            val bundle = Bundle()
            bundle.putParcelable("requestRide", requestRide)
            rideConfirmationDialog.arguments = bundle
            return rideConfirmationDialog
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
        rootView.tvEstimateFare.setTypeface(Fonts.mavenRegular(rootView.context), BOLD)
        rootView.tvLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvVehicleName.setTypeface(Fonts.mavenRegular(rootView.context), BOLD)
        rootView.tvPickup.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvDrop.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvNotes.typeface = Fonts.mavenMedium(rootView.context)
        rootView.etAdditionalFare.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvTipMsg.setTypeface(Fonts.mavenRegular(rootView.context), BOLD)
    }

    /**
     *
     */
    private fun setData() {
        var requestRide : RequestRideConfirm? = null
        var addedTip = Data.autoData.noDriverFoundTip
        var isTotalInRange = false
        if(arguments != null) {
            requestRide  = arguments?.getParcelable("requestRide")!!
            rootView.tvPickup.text = requestRide.pickup
            rootView.tvDrop.text = requestRide.drop
            rootView.tvVehicleName.text = requestRide.vehicleName
            rootView.ivEstimateFare.visibility = View.GONE


            if(requestRide.fare != 0.0) {
                isTotalInRange = false
                rootView.tvEstimateFare.text = Utils.formatCurrencyValue(requestRide.currency, requestRide.fare)
            } else if(requestRide.fare == 0.0 && requestRide.minFare != 0.0 && requestRide.maxFare != 0.0) {
                isTotalInRange = true
                rootView.tvEstimateFare.text = Utils.formatCurrencyValue(requestRide.currency, requestRide.minFare).plus(" - ")
                        .plus(Utils.formatCurrencyValue(requestRide.currency, requestRide.maxFare))
            } else {
                isTotalInRange = false
                rootView.tvEstimateFare.visibility = View.GONE
                rootView.ivEstimateFare.visibility = View.GONE
            }

            setTotalFare(requestRide, addedTip, isTotalInRange)

            if(!requestRide.vehicleIcon.isNullOrEmpty()) {
                Glide.with(rootView.context).load(requestRide.vehicleIcon?.replace("http:", "https:"))
                        .into(rootView.ivVehicle)
            }
            if(!requestRide.note.isNullOrEmpty()) {
                rootView.tvNotes.text = requestRide.note
            } else {
                rootView.tvNotes.visibility = View.GONE
            }

            if(requestRide.drop.isNullOrEmpty()) {
                rootView.groupDrop.visibility = View.GONE
            }

            if(requestRide.showTip) {
                rootView.groupTip.visibility = View.VISIBLE
            } else {
                rootView.groupTip.visibility = View.GONE
            }
        }
        rootView.btnCancel.setOnClickListener {
            (context as RideRequestConfirmListener).onCancelClick(isPickup!!)
            dismiss()
        }
        rootView.btnOk.setOnClickListener {
            Data.autoData.noDriverFoundTip = addedTip
            (context as RideRequestConfirmListener).onOkClick(isPickup!!)
            dismiss()
        }

        var countBeforeChange: Int? = 0
        rootView.etAdditionalFare.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty() && p0.toString() != ".") {
                    addedTip = p0.toString().toDouble()
                    setTotalFare(requestRide, addedTip, isTotalInRange)
                } else {
                    addedTip = 0.0
                    setTotalFare(requestRide, addedTip, isTotalInRange)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countBeforeChange = p0.toString().length
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty() && countBeforeChange == 0) {
                    rootView.etAdditionalFare.hint = null
                    if (rootView.etAdditionalFare.textDrawable == null) {
                        rootView.etAdditionalFare.setPrefix(Utils.getCurrencySymbol(requestRide?.currency))

                    } else {
                        rootView.etAdditionalFare.setCompoundDrawables(rootView.etAdditionalFare.textDrawable, null, null, null)
                    }
                } else if (p0.toString().isEmpty()) {
                    rootView.etAdditionalFare.setHint(R.string.hint_tip_amount)
                    rootView.etAdditionalFare.setCompoundDrawables(null, null, null, null)
                }
            }

        })
    }

    private fun setTotalFare(requestRide: RequestRideConfirm?, addedTip: Double, totalInRange: Boolean) {
        if (totalInRange) {
            rootView.tvEstimateFare.text = Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.minFare!! + addedTip))
                    .plus(" - ")
                    .plus(Utils.formatCurrencyValue(requestRide.currency, (requestRide.maxFare + addedTip)))
        } else {
            rootView.tvEstimateFare.text = Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.fare!! + addedTip))
        }
    }

    interface RideRequestConfirmListener {
        fun onCancelClick(isPickup : Boolean)
        fun onOkClick(isPickup: Boolean)
    }
}