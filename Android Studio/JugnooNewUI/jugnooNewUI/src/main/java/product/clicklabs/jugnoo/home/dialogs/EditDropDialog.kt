package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.dialog_edit_drop.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.apis.ApiFareEstimate
import product.clicklabs.jugnoo.datastructure.PromoCoupon
import product.clicklabs.jugnoo.datastructure.SearchResult
import product.clicklabs.jugnoo.utils.Utils
import java.io.Serializable

class EditDropDialog :DialogFragment(){

    companion object{
        const val KEY_EDIT_DROP_DATUM = "edit_drop_datum"

        @JvmStatic
        fun newInstance(editDropDatum: EditDropDatum):EditDropDialog{
            val dialogFragment = EditDropDialog()
            val bundle = Bundle()
            bundle.putParcelable(KEY_EDIT_DROP_DATUM, editDropDatum)

            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    private var callback:Callback? = null
    private var editDropDatum:EditDropDatum? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is Callback){
            callback = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
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
        return inflater.inflate(R.layout.dialog_edit_drop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editDropDatum = arguments?.getParcelable(KEY_EDIT_DROP_DATUM)

        btnOk.setOnClickListener{
            if(editDropDatum != null) {
                callback?.onEditDropConfirm(editDropDatum!!.dropLatLng, editDropDatum!!.dropAddress)
            }
        }
        btnCancel.setOnClickListener{
            dismiss()
        }

        if(editDropDatum != null){
            tvPickup.text = editDropDatum!!.pickupAddress
            tvDrop.text = editDropDatum!!.dropAddress

            tvOldFareValue.text = Utils.formatCurrencyValue(editDropDatum!!.currency, editDropDatum!!.oldFare)
        }

    }


    fun estimateFare(){


    }

    val apiFareEstimate:ApiFareEstimate by lazy { ApiFareEstimate(requireContext(), object:ApiFareEstimate.Callback{
        override fun onSuccess(list: MutableList<LatLng>?, distanceText: String?, timeText: String?, distanceValue: Double, timeValue: Double, promoCoupon: PromoCoupon?) {
        }

        override fun onRetry() {
        }

        override fun onNoRetry() {
        }

        override fun onFareEstimateSuccess(currency: String?, minFare: String?, maxFare: String?, convenienceCharge: Double, tollCharge: Double) {
        }

        override fun onPoolSuccess(currency: String?, fare: Double, rideDistance: Double, rideDistanceUnit: String?, rideTime: Double, rideTimeUnit: String?, poolFareId: Int, convenienceCharge: Double, text: String?, tollCharge: Double) {
        }

        override fun onDirectionsFailure() {
        }

        override fun onFareEstimateFailure() {
        }

    }) }






    class EditDropDatum(var pickupLatLng: LatLng?, var pickupAddress:String?,
                        var dropSearchResult:SearchResult?,
                        var oldFare:Double, var currency:String?,
                        var regionId:Int?, var ):Serializable, Parcelable

    interface Callback{
        fun onEditDropConfirm(dropLatLng:LatLng?, dropAddress:String?)
    }
}