package product.clicklabs.jugnoo.tutorials.newtutorials.adapters

import com.google.gson.annotations.SerializedName

data class TutorialDAO(
        @SerializedName("image_url") var pImageUrl: String,
        @SerializedName("description") var pDescription: String
)