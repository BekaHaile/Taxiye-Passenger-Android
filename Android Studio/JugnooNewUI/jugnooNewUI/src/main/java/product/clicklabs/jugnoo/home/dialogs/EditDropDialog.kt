package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.android.gms.maps.model.LatLng
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.dialog_edit_drop.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.FareEstimateResponse
import product.clicklabs.jugnoo.utils.DialogPopup
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
                callback?.onEditDropConfirm(editDropDatum!!.dropLatLng, editDropDatum!!.dropAddress,
                        editDropDatum!!.dropName, editDropDatum!!.poolFareId)
            }
            dismiss()
        }
        btnCancel.setOnClickListener{
            dismiss()
        }

        if(editDropDatum != null){
            tvPickup.text = editDropDatum!!.pickupAddress
            tvDrop.text = editDropDatum!!.dropAddress

            if(editDropDatum!!.oldFare != null) {
                tvOldFareValue.text = Utils.formatCurrencyValue(editDropDatum!!.currency, editDropDatum!!.oldFare!!)
            }
            if(editDropDatum!!.newFare != null) {
                tvNewFareValue.text = Utils.formatCurrencyValue(editDropDatum!!.currency, editDropDatum!!.newFare!!)
            }
        }

    }



    class EditDropDatum(var engagementId:Int?,
                        var pickupLatLng: LatLng?, var pickupAddress:String?,
                        var dropLatLng: LatLng?, var dropAddress:String?, var dropName:String?,
                        var currency:String?, var oldFare:Double?, var newFare:Double?, var poolFareId:Int?):Serializable, Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readParcelable(LatLng::class.java.classLoader),
                parcel.readString(),
                parcel.readParcelable(LatLng::class.java.classLoader),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(engagementId)
            parcel.writeParcelable(pickupLatLng, flags)
            parcel.writeString(pickupAddress)
            parcel.writeParcelable(dropLatLng, flags)
            parcel.writeString(dropAddress)
            parcel.writeString(dropName)
            parcel.writeString(currency)
            parcel.writeValue(oldFare)
            parcel.writeValue(newFare)
            parcel.writeValue(poolFareId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<EditDropDatum> {
            override fun createFromParcel(parcel: Parcel): EditDropDatum {
                return EditDropDatum(parcel)
            }

            override fun newArray(size: Int): Array<EditDropDatum?> {
                return arrayOfNulls(size)
            }
        }
    }

    interface Callback{
        fun onEditDropConfirm(dropLatLng:LatLng?, dropAddress:String?, dropName:String?, poolFareId:Int?)
    }
}

object EditDropConfirmation{


    fun fareEstimateAndConfirm(activity:AppCompatActivity, engagementId:Int?,
                                     pickupLatLng: LatLng?, pickupAddress:String?,
                                     dropLatLng: LatLng?, dropAddress:String?, dropName:String?,
                                     currency:String?){

        val editDropDatum = EditDropDialog.EditDropDatum(engagementId, pickupLatLng, pickupAddress, dropLatLng, dropAddress, dropName, currency, null, null, null)
        estimateFare(activity, editDropDatum)

    }

    private fun estimateFare(activity:AppCompatActivity, editDropDatum : EditDropDialog.EditDropDatum){
        val params = hashMapOf(
                Constants.KEY_ENGAGEMENT_ID to editDropDatum.engagementId.toString(),
                Constants.KEY_LATITUDE to editDropDatum.dropLatLng!!.latitude.toString(),
                Constants.KEY_LONGITUDE to editDropDatum.dropLatLng!!.longitude.toString()
        )
        ApiCommon<FareEstimateResponse>(activity).execute(params, ApiName.FARE_ESTIMATE_WITH_NEW_DROP,
                object : APICommonCallback<FareEstimateResponse>(){
                    override fun onSuccess(t: FareEstimateResponse?, message: String?, flag: Int) {
                        if(t != null){
                            editDropDatum.oldFare = t.oldFare
                            editDropDatum.newFare = t.newFare
                            editDropDatum.poolFareId = t.poolFareId
                            val ft = activity.supportFragmentManager.beginTransaction()
                            val dialogFragment = EditDropDialog.newInstance(editDropDatum)
                            dialogFragment.show(ft, EditDropDialog::class.java.simpleName)
                        } else {
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.something_went_wrong))
                        }
                    }

                    override fun onError(t: FareEstimateResponse?, message: String?, flag: Int): Boolean {
                        return false
                    }

                })
    }

}