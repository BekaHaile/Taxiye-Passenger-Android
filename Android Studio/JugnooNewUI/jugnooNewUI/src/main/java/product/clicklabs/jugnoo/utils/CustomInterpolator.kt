package product.clicklabs.jugnoo.utils

import android.view.animation.Interpolator

class CustomInterpolator() : Interpolator {
    fun CustomInterpolator() {}

    override fun getInterpolation(t: Float): Float {
        val x = 2.0f * t - 1.0f
        return 0.5f * (x * x * x + 1.0f)
    }
}