package product.clicklabs.jugnoo.retrofit.model

enum class CouponType constructor(val type: Int) {
    LOCATION_INSENSITIVE(1),
    PICKUP_BASED(2),
    DROP_BASED(3),
    PICKUP_DROP_BASED(4)
}