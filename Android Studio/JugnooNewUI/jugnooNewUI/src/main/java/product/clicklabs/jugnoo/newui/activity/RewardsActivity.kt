package product.clicklabs.jugnoo.newui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.analytics.GACategory
import com.sabkuchfresh.analytics.GAUtils
import kotlinx.android.synthetic.main.activity_rewards.*
import org.json.JSONObject
import product.clicklabs.jugnoo.*
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance
import product.clicklabs.jugnoo.config.Config
import product.clicklabs.jugnoo.datastructure.*
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.home.models.VehicleTypeValue
import product.clicklabs.jugnoo.newui.adapter.RewardsAdapter
import product.clicklabs.jugnoo.newui.dialog.RewardsDialog
import product.clicklabs.jugnoo.promotion.fragments.PromoDescriptionFragment
import product.clicklabs.jugnoo.promotion.models.Promo
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Utils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*
import kotlin.collections.ArrayList

class RewardsActivity : BaseFragmentActivity(), RewardsDialog.ScratchCardRevealedListener {
    override fun onScratchCardRevealed() {
        requestCode = Activity.RESULT_OK
        getCouponsAndPromotions(this)
    }

    private val promosList = ArrayList<Promo>()
    private val promosTemp = ArrayList<Promo>()
    private var rewardsAdapter : RewardsAdapter? = null
    private var filterEnabled = false
    private var requestCode : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        getCouponsAndPromotions(this@RewardsActivity)
        setRecyclerView()
        setListenerView()
    }

    private fun setListenerView() {
        ivInfo.visibility = View.INVISIBLE
        ivBack.setOnClickListener {
            if(filterEnabled) {
                filterEnabled = false
                promosList.clear()
                promosList.addAll(promosTemp)
                groupCashback.visibility = View.VISIBLE
                groupOffer.visibility = View.VISIBLE
                rewardsAdapter?.notifyDataSetChanged()
                val guideLine : Guideline= findViewById(R.id.glMid)
                val params : ConstraintLayout.LayoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
                params.guidePercent = 0.50f // 45% // range: 0 <-> 1
                guideLine.layoutParams = params
                tvTitle.text = getString(R.string.text_rewards)
            } else {
                setResult(requestCode)
                super.onBackPressed()
            }
        }
        ivCashBack.setOnClickListener {
            promosList.clear()
            for (promo in promosTemp) {
                if(promo.couponCardType == 1 && promo.promoCoupon.benefitType() == 3) {
                    promosList.add(promo)
                }
            }
            groupCashback.visibility = View.VISIBLE
            groupOffer.visibility = View.GONE
            filterEnabled = true
            rewardsAdapter?.notifyDataSetChanged()
            tvTitle.text = getString(R.string.cashback)
        }
        ivOffers.setOnClickListener {
            promosList.clear()
            for (promo in promosTemp) {
                if(promo.couponCardType == 1 && promo.promoCoupon.benefitType() != 3) {
                    promosList.add(promo)
                }
            }
            val guideLine : Guideline= findViewById(R.id.glMid)
            val params : ConstraintLayout.LayoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 0f // 45% // range: 0 <-> 1
            guideLine.layoutParams = params
            groupCashback.visibility = View.GONE
            groupOffer.visibility = View.VISIBLE
            filterEnabled = true
            rewardsAdapter?.notifyDataSetChanged()
            tvTitle.text = getString(R.string.offers)
        }
    }

    private fun setRecyclerView() {
        rvRewards.layoutManager = GridLayoutManager(this@RewardsActivity, 2)
        rvRewards.isNestedScrollingEnabled = false
        rewardsAdapter = RewardsAdapter(rvRewards, promosList, object : RewardsAdapter.RewardCardListener {
            override fun onCardScratched() {

            }

            override fun onCardClicked(promo: Promo, index: Int) {
                if(promo.couponCardType == 1 && !promo.isScratched) {
                    if(MyApplication.getInstance().isOnline) {
                        val ft = supportFragmentManager.beginTransaction()
                        val prev = supportFragmentManager.findFragmentByTag("scratchDialog")
                        if (prev != null) {
                            ft.remove(prev)
                        }
                        ft.addToBackStack(null)
                        val dialogFragment = RewardsDialog.newInstance(promo, index % 2 == 0, false)
                        dialogFragment.show(ft, "scratchDialog")
                    } else {
                        Toast.makeText(this@RewardsActivity, getString(R.string.text_no_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                } else if(promo.couponCardType == 1 && promo.isScratched && promo.promoCoupon.benefitType() != 3
                        || promo.couponCardType == 0) {
                    openPromoDescriptionFragment(promo.name, promo.clientId, promo.promoCoupon)
                    GAUtils.event(GACategory.SIDE_MENU, GAAction.PROMOTIONS + GAAction.OFFER + GAAction.TNC + GAAction.CLICKED, promo.promoCoupon.title)
                }
            }

        })
        rvRewards.adapter = rewardsAdapter
    }

    fun openPromoDescriptionFragment(offeringName: String, clientId: String, promoCoupon: PromoCoupon) {
        removeFragment(supportFragmentManager.findFragmentByTag(PromoDescriptionFragment::class.java.name))
        val fragment = PromoDescriptionFragment.newInstance(offeringName, clientId, promoCoupon)
        llContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(llContainer.id,
                        fragment,
                        PromoDescriptionFragment::class.java.name)
                .commitAllowingStateLoss()
        tvTitle.text = getString(R.string.terms_of_use)
    }

    fun removeFragment(fragment: Fragment?) {
        if (fragment != null) {
            llContainer.visibility = View.GONE
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            tvTitle.text = getString(R.string.promotions)
        }
    }

    private val handler = Handler()

    fun getCouponsAndPromotions(activity: Activity) {
        try {
            if (!HomeActivity.checkIfUserDataNull(activity)) {
                if (MyApplication.getInstance().isOnline) {
//                    DialogPopup.showLoadingDialog(activity, getString(R.string.loading))

                    val params = HashMap<String, String>()
                    params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                    params[Constants.KEY_LATITUDE] = "" + Data.latitude
                    params[Constants.KEY_LONGITUDE] = "" + Data.longitude

                    HomeUtil().putDefaultParams(params)
                    RestClient.getApiService().getCouponsAndPromotions(params, object : Callback<PromCouponResponse> {
                        override fun success(promCouponResponse: PromCouponResponse, response: Response) {
                            val responseStr = String((response.body as TypedByteArray).bytes)
                            try {
                                val jObj = JSONObject(responseStr)

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    val flag = jObj.getInt("flag")
                                    val message = JSONParser.getServerMessage(jObj)
                                    if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {

                                        parsePromoCoupons(promCouponResponse)
                                        updateListData()

                                    } else {
                                        updateListData()
                                        retryDialogGetPromos(DialogErrorType.OTHER, message)
                                    }
                                } else {
                                    updateListData()
                                }

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                                updateListData()
                                retryDialogGetPromos(DialogErrorType.SERVER_ERROR, "")
                            }

//                            DialogPopup.dismissLoadingDialog()
                        }

                        override fun failure(error: RetrofitError) {
//                            Log.e(TAG, "getCouponsAndPromotions error=$error")
//                            DialogPopup.dismissLoadingDialog()
                            updateListData()
                            retryDialogGetPromos(DialogErrorType.CONNECTION_LOST, "")
                        }
                    })
                } else {
                    retryDialogGetPromos(DialogErrorType.NO_NET, "")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun fillMasterPromoCoupons(promoCouponsMaster: List<PromoCoupon>?,
                                       pcAll: ArrayList<PromoCoupon>?,
                                       pcRides: ArrayList<PromoCoupon>,
                                       pcMenus: ArrayList<PromoCoupon>,
                                       pcDeliveryCustomer: ArrayList<PromoCoupon>,
                                       pcFatafat: ArrayList<PromoCoupon>,
                                       pcMeals: ArrayList<PromoCoupon>) {
        if (promoCouponsMaster != null) {
            for (promoCoupon : PromoCoupon in promoCouponsMaster) {
                if (pcAll != null
                        && promoCoupon.autos == 1
                        && promoCoupon.menus == 1
                        && promoCoupon.deliveryCustomer == 1
                        && (promoCoupon.fresh == 1 || promoCoupon.grocery == 1)
                        && promoCoupon.meals == 1) {
                    pcAll.add(promoCoupon)
                } else {
                    if (promoCoupon.autos == 1) {
                        pcRides.add(promoCoupon)
                    }
                    if (promoCoupon.menus == 1) {
                        pcMenus.add(promoCoupon)
                    }
                    if (promoCoupon.deliveryCustomer == 1) {
                        pcDeliveryCustomer.add(promoCoupon)
                    }
                    if (promoCoupon.fresh == 1 || promoCoupon.grocery == 1) {
                        pcFatafat.add(promoCoupon)
                    }
                    if (promoCoupon.meals == 1) {
                        pcMeals.add(promoCoupon)
                    }
                }
            }
        }
    }


    private fun retryDialogGetPromos(dialogErrorType: DialogErrorType, message: String) {
        if (dialogErrorType == DialogErrorType.OTHER) {
            DialogPopup.alertPopup(this@RewardsActivity, "", message)
        } else {
            DialogPopup.dialogNoInternet(this@RewardsActivity, dialogErrorType, object : Utils.AlertCallBackWithButtonsInterface {
                override fun positiveClick(view: View) {
                    getCouponsAndPromotions(this@RewardsActivity)
                }

                override fun neutralClick(view: View) {

                }

                override fun negativeClick(view: View) {

                }
            })
        }
    }


    /**
     * API call for applying promo code to server
     */
    fun applyPromoCodeAPI(activity: Activity, promoCode: String) {
        try {
            if (!HomeActivity.checkIfUserDataNull(activity)) {
                if (MyApplication.getInstance().isOnline) {
                    DialogPopup.showLoadingDialog(activity, getString(R.string.loading))

                    val params = HashMap<String, String>()
                    params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                    params[Constants.KEY_CODE] = promoCode

                    HomeUtil().putDefaultParams(params)
                    RestClient.getApiService().enterCode(params, object : Callback<SettleUserDebt> {
                        override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                            val responseStr = String((response.body as TypedByteArray).bytes)
//                            Log.i(TAG, "enterCode response = $responseStr")
                            try {
                                val jObj = JSONObject(responseStr)
                                val flag = jObj.getInt("flag")
                                if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                                    HomeActivity.logoutUser(activity)
                                } else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                    val errorMessage = jObj.getString("error")
                                    DialogPopup.alertPopup(activity, "", errorMessage)
//                                    editTextPromoCode.setText("")
                                } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                                    val message = jObj.getString("message")
                                    DialogPopup.dialogBanner(activity, message)
                                    getCouponsAndPromotions(activity)
//                                    editTextPromoCode.setText("")

                                    ApiFetchWalletBalance(activity, object : ApiFetchWalletBalance.Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onFinish() {

                                        }

                                        override fun onFailure() {

                                        }

                                        override fun onRetry(view: View) {

                                        }

                                        override fun onNoRetry(view: View) {

                                        }

                                    }).getBalance(false)
                                } else {
                                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again))
                                }
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again))

                            }

                            DialogPopup.dismissLoadingDialog()
                        }

                        override fun failure(error: RetrofitError) {
//                            Log.e(TAG, "enterCode error=$error")
                            DialogPopup.dismissLoadingDialog()
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again))
                        }
                    })
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            object : Utils.AlertCallBackWithButtonsInterface {
                                override fun positiveClick(v: View) {
                                    applyPromoCodeAPI(activity, promoCode)
                                }

                                override fun neutralClick(v: View) {

                                }

                                override fun negativeClick(v: View) {

                                }
                            })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateListData() {
        if (promosList.size == 0) {
            rvRewards.visibility = View.GONE
            noCoupons.visibility = View.VISIBLE
//            linearLayoutNoOffers.setVisibility(View.VISIBLE)
        } else {
            rvRewards.visibility = View.VISIBLE
            noCoupons.visibility = View.GONE
//            linearLayoutNoOffers.setVisibility(View.GONE)
            rewardsAdapter?.notifyDataSetChanged()
        }
    }

    private fun parsePromoCoupons(promCouponResponse: PromCouponResponse) {
        promosList.clear()
        promosTemp.clear()

        var pcAll = ArrayList<PromoCoupon>()
        var pcRides = ArrayList<PromoCoupon>()
        var pcMenus = ArrayList<PromoCoupon>()
        var pcDeliveryCustomer = ArrayList<PromoCoupon>()
        var pcFatafat = ArrayList<PromoCoupon>()
        var pcMeals = ArrayList<PromoCoupon>()

        fillMasterPromoCoupons(promCouponResponse.commonPromotions, pcAll, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals)
        fillMasterPromoCoupons(promCouponResponse.commonCoupons, pcAll, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals)

        if (promCouponResponse.autosPromotions != null)
            pcRides.addAll(promCouponResponse.autosPromotions)
        if (promCouponResponse.autosCoupons != null)
            pcRides.addAll(promCouponResponse.autosCoupons)

        if (promCouponResponse.menusPromotions != null)
            pcMenus.addAll(promCouponResponse.menusPromotions)
        if (promCouponResponse.menusCoupons != null)
            pcMenus.addAll(promCouponResponse.menusCoupons)

        if (promCouponResponse.deliveryCustomerPromotions != null)
            pcDeliveryCustomer.addAll(promCouponResponse.deliveryCustomerPromotions)
        if (promCouponResponse.deliveryCustomerCoupons != null)
            pcDeliveryCustomer.addAll(promCouponResponse.deliveryCustomerCoupons)

        if (promCouponResponse.freshPromotions != null)
            pcFatafat.addAll(promCouponResponse.freshPromotions)
        if (promCouponResponse.freshCoupons != null)
            pcFatafat.addAll(promCouponResponse.freshCoupons)
        if (promCouponResponse.groceryPromotions != null)
            pcFatafat.addAll(promCouponResponse.groceryPromotions)
        if (promCouponResponse.groceryCoupons != null)
            pcFatafat.addAll(promCouponResponse.groceryCoupons)

        if (promCouponResponse.mealsPromotions != null)
            pcMeals.addAll(promCouponResponse.mealsPromotions)
        if (promCouponResponse.mealsCoupons != null)
            pcMeals.addAll(promCouponResponse.mealsCoupons)


        if (pcAll.size > 0) {
            pcAll = countAndRemoveDuplicatePromoCoupons(pcAll)
            for (pc in pcAll) {
                promosList.add(Promo(getString(R.string.all), "", pc, R.drawable.ic_promo_all, -1, false, 0))
            }
        }
        if (pcRides.size > 0) {
            pcRides = countAndRemoveDuplicatePromoCoupons(pcRides)
            for (pc in pcRides) {
                var id = R.drawable.ic_promo_all
                if (pc.allowedVehicles != null && pc.allowedVehicles.size == 1) {
                    if (pc.allowedVehicles[0] == VehicleTypeValue.TAXI.getOrdinal()) {
                        id = R.drawable.ic_taxi_gradient
                    } else {
                        id = R.drawable.ic_promo_rides
                    }
                }

                promosList.add(Promo(getString(R.string.rides), Config.getAutosClientId(), pc, id, R.color.theme_color, pc.isScratched, pc.couponCardType))
            }
        }
        if (pcMeals.size > 0) {
            pcMeals = countAndRemoveDuplicatePromoCoupons(pcMeals)
            for (pc in pcMeals) {
                promosList.add(Promo(getString(R.string.meals), Config.getMealsClientId(), pc, R.drawable.ic_promo_meals, R.color.pink_meals_fab, pc.isScratched, pc.couponCardType))
            }
        }
        if (pcFatafat.size > 0) {
            pcFatafat = countAndRemoveDuplicatePromoCoupons(pcFatafat)
            for (pc in pcFatafat) {
                promosList.add(Promo(getString(R.string.fatafat), Config.getFreshClientId(), pc, R.drawable.ic_promo_fresh, R.color.fresh_promotions_green, pc.isScratched, pc.couponCardType))
            }
        }
        if (pcMenus.size > 0) {
            pcMenus = countAndRemoveDuplicatePromoCoupons(pcMenus)
            for (pc in pcMenus) {
                promosList.add(Promo(getString(R.string.menus), Config.getMenusClientId(), pc, R.drawable.ic_promo_menus, R.color.purple_menus_fab, pc.isScratched, pc.couponCardType))
            }
        }
        if (pcDeliveryCustomer.size > 0) {
            pcDeliveryCustomer = countAndRemoveDuplicatePromoCoupons(pcDeliveryCustomer)
            for (pc in pcDeliveryCustomer) {
                promosList.add(Promo(getString(R.string.delivery_new_name), Config.getDeliveryCustomerClientId(), pc, R.drawable.ic_promo_menus, R.color.purple_menus_fab, pc.isScratched, pc.couponCardType))
            }
        }

        promosTemp.addAll(promosList)
    }


    private fun countAndRemoveDuplicatePromoCoupons(promoCoupons: ArrayList<PromoCoupon>): ArrayList<PromoCoupon> {
        var promoCoupons = promoCoupons
        for (promoCoupon in promoCoupons) {
            if (promoCoupon is CouponInfo) {
                promoCoupon.isCheckWithCouponId = true
            }
            promoCoupon.repeatedCount = Collections.frequency(promoCoupons, promoCoupon)
        }

        val set = TreeSet(Comparator<PromoCoupon> { o1, o2 ->
            if (o1 == o2) {
                0
            } else 1
        })
        set.addAll(promoCoupons)
        val promoCouponsTemp = ArrayList(set)
        promoCoupons.clear()
        for(i in 0 until promoCouponsTemp.size) {
            if(promoCouponsTemp[i].couponCardType == 1 && !promoCouponsTemp[i].isScratched) {
                promoCoupons.add(promoCouponsTemp[i])
            }
        }
        return promoCoupons
    }
}