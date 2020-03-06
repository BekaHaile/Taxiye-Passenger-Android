package product.clicklabs.jugnoo.tutorials.newtutorials.adapters

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TutorialDAO(
        @Expose @SerializedName("image_url") var pImageUrl: String,
        @Expose @SerializedName("description") var pDescription: String = ""
)