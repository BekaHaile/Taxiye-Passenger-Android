package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CardDetails(
        @SerializedName("last_4") @Expose var last4: String,
        @SerializedName("amount_paid") @Expose var amountPaid: Double,
        @SerializedName("id") @Expose var id: Int)
