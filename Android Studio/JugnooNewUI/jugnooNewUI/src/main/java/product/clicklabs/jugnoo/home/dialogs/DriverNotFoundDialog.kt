package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_no_rides_found.view.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.RequestRideConfirm
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import java.util.regex.Pattern


class DriverNotFoundDialog : DialogFragment() {
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
        fun newInstance(requestRide : RequestRideConfirm): DriverNotFoundDialog {
            val itemImageDialog = DriverNotFoundDialog()
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
        rootView = inflater.inflate(R.layout.dialog_no_rides_found, container, false)
        setFonts()
        setData()
        return rootView
    }

    private fun setFonts() {
        rootView.tvFare.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvVehicleName.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvTotalFareLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvTotalFare.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvPickup.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvDrop.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvTipLabel.typeface = Fonts.mavenRegular(rootView.context)
        rootView.etAdditionalFare.typeface = Fonts.mavenRegular(rootView.context)
        rootView.tvNoRidesFound.setTypeface(Fonts.mavenRegular(rootView.context), BOLD)
    }

    /**
     *
     */
    private fun setData() {
        var addedTip = 0.0
        var isTotalInRange = false
        var requestRide : RequestRideConfirm? = null

        rootView.etAdditionalFare.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(5, 2))

        rootView.tvLabel.visibility = View.GONE

        if(arguments != null) {
            requestRide = arguments?.getParcelable("requestRide")!!
            rootView.tvPickup.text = requestRide.pickup
            rootView.tvDrop.text = requestRide.drop
            rootView.tvVehicleName.text = requestRide.vehicleName


            if(requestRide.fare != 0.0) {
                isTotalInRange = false
                rootView.tvFare.text = Utils.formatCurrencyValue(requestRide.currency, requestRide.fare)
            } else if(requestRide.fare == 0.0 && requestRide.minFare != 0.0 && requestRide.maxFare != 0.0) {
                isTotalInRange = true
                rootView.tvFare.text = Utils.formatCurrencyValue(requestRide.currency, requestRide.minFare).plus(" - ")
                        .plus(Utils.formatCurrencyValue(requestRide.currency, requestRide.maxFare))
            } else {
                isTotalInRange = false
                rootView.tvFare.visibility = View.GONE
            }

            setTotalFare(requestRide, addedTip, isTotalInRange)

            if(!requestRide.vehicleIcon.isNullOrEmpty()) {
                Glide.with(rootView.context).load(requestRide.vehicleIcon?.replace("http:", "https:"))
                        .into(rootView.ivVehicle)
            }

            if(requestRide.drop.isNullOrEmpty()) {
                rootView.groupDrop.visibility = View.GONE
            }
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
        rootView.btnCancel.setOnClickListener {
            (context as RideRequestConfirmListener).onCancelClick()
            dismiss()
        }
        rootView.btnOk.setOnClickListener {
            if(addedTip > 0.0) {
                Data.autoData.noDriverFoundTip = addedTip
                (context as RideRequestConfirmListener).onOkClick()
                dismiss()
            }
        }
    }

    private fun setTotalFare(requestRide: RequestRideConfirm?, addedTip: Double, totalInRange: Boolean) {
        if (totalInRange) {
            rootView.tvTotalFare.text = Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.minFare!! + addedTip))
                    .plus(" - ")
                    .plus(Utils.formatCurrencyValue(requestRide.currency, (requestRide.maxFare + addedTip)))
        } else {
            rootView.tvTotalFare.text = Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.fare!! + addedTip))
        }
    }

    interface RideRequestConfirmListener {
        fun onCancelClick()
        fun onOkClick()
    }

    inner class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

        private var mPattern: Pattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {

            val matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else null
        }

    }
}