package product.clicklabs.jugnoo.retrofit.model

enum class GenderValues(val type: Int){
    ALL(0),
    MALE(1),
    FEMALE(2),
}

class Gender(val type:Int, val name:String){
    override fun toString(): String {
        return name
    }
}