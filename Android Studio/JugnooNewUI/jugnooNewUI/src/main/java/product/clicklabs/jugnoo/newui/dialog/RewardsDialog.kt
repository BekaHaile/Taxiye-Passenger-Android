package product.clicklabs.jugnoo.newui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.dialog_rewards.view.*
import kotlinx.android.synthetic.main.dialog_rewards_alt_2.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.config.Config
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.datastructure.CouponInfo
import product.clicklabs.jugnoo.newui.utils.customview.ScratchView
import product.clicklabs.jugnoo.promotion.models.Promo
import product.clicklabs.jugnoo.retrofit.model.ScratchCard
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Prefs
import product.clicklabs.jugnoo.utils.scratchanimation.ExplosionField


class RewardsDialog : DialogFragment() {
    private lateinit var rootView : View
    private lateinit var promoData : Promo
    private var setAnimation = false
    private var mExplosionField: ExplosionField? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.attributes?.windowAnimations = R.style.ScratchCardDialog
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(promo: Promo, isFromLeft : Boolean, isFromEndRide : Boolean): RewardsDialog {
            val bundle = Bundle()
            bundle.putParcelable("coupon", promo)
            bundle.putBoolean("isFromLeft", isFromLeft)
            bundle.putBoolean("isFromEndRide", isFromEndRide)
            val rewardsDialog = RewardsDialog()
            rewardsDialog.arguments = bundle
            return rewardsDialog
        }
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        val prev = manager?.findFragmentByTag("scratchDialog")
        if (prev != null) {
            ft?.remove(prev)
        }
        ft?.addToBackStack(null)
        ft?.add(this, tag);
        ft?.commitAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setDimAmount(0.9f)
            Handler().postDelayed({
                if(!setAnimation) {
                    addAnimationOperations()
                }
            }, 50)
        }
    }

    private fun addAnimationOperations() {
        val constraint1 = ConstraintSet()
        constraint1.clone(card)
        val constraint2 = ConstraintSet()
        constraint2.clone(activity, R.layout.dialog_rewards_alt)
        val constraint3 = ConstraintSet()
        constraint3.clone(activity, R.layout.dialog_rewards)

        val transition = ChangeBounds()
        transition.duration = 500
        var delayTime : Long = 1700
        if(arguments!!.getBoolean("isFromEndRide", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(card, transition)
                constraint3.applyTo(card)
            }
            delayTime = 1200
            setAnimation = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(card, transition)
                constraint2.applyTo(card)
            }

            Handler().postDelayed({
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(card, transition)
                    constraint3.applyTo(card)
                }
            }, 400)

            delayTime = 1700
            setAnimation = true
        }
        Handler().postDelayed({
            setData()
        }, delayTime)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var isFromLeftSide : Boolean = false
        var isFromEndRide : Boolean = false
        if(arguments != null) {
            isFromLeftSide = arguments!!.getBoolean("isFromLeft", false)
            isFromEndRide = arguments!!.getBoolean("isFromEndRide", false)
        }
        if(isFromLeftSide) {
            rootView = inflater.inflate(R.layout.dialog_rewards_alt_2, container, false)
        } else if(isFromEndRide){
            rootView = inflater.inflate(R.layout.dialog_rewards_alt, container, false)
        } else {
            rootView = inflater.inflate(R.layout.dialog_rewards_alt_1, container, false)
        }
        return rootView
    }

    /**
     *
     */
    private fun setData() {
        val scratchView : ScratchView = rootView.findViewById(R.id.scratch_view)
        if (arguments != null) {
            promoData = arguments?.getParcelable("coupon")!!
            rootView.tvRewardInfo.text = promoData.name
            rootView.tvAmount.text = promoData.promoCoupon.title
            rootView.tvRewardInfo.visibility = View.VISIBLE
            rootView.tvAmount.visibility = View.VISIBLE
            mExplosionField = ExplosionField.attach2Window(activity, rootView.card)
            if(arguments!!.getBoolean("isFromEndRide", false)) {
//                rootView.tvAmount.visibility = View.GONE
//                rootView.tvRewardInfo.visibility = View.GONE
            }
//            if(promoData.promoCoupon.benefitType() == 3) {
//                scratchView.setOverlayImage(R.drawable.ic_pattern_cashback)
//            } else {
//                scratchView.setOverlayImage(R.drawable.ic_scratch_pattern)
//            }
        }
        rootView.ivClose.setOnClickListener {
            dismiss()
        }
        rootView.tvSkip.setOnClickListener {
            dismiss()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView) {
                    scratchCard()
                }

                override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                    if (percent >= 0.5) {
                        Log.d("Reveal Percentage", "onRevealPercentChangedListener: $percent")
                    }
                }
            })
        }

    }

    private fun scratchCard(){
        mExplosionField?.explode(rootView.ivOffer)
        rootView.tvSkip.visibility = View.GONE
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCOUNT_ID] = promoData.promoCoupon.id.toString()
        params[Constants.KEY_CURRENCY] = Data.autoData.currency

        ApiCommon<ScratchCard>(activity).showLoader(false).execute(params, ApiName.SCRATCH_CARD,
                object : APICommonCallback<ScratchCard>() {

                    override fun onSuccess(response: ScratchCard, message: String, flag: Int) {
                        try {
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                Handler().postDelayed({
                                    (activity as ScratchCardRevealedListener).onScratchCardRevealed()
                                    refreshLoginPromo()
                                    rootView.tvScratchSuccessMsg.text = response.message
                                    if(response.zilch == 0) {
                                        rootView.ivOffer.visibility = View.VISIBLE
                                        mExplosionField?.explode(rootView.ivOffer)
                                    } else {
                                        rootView.ivOffer.visibility = View.INVISIBLE
                                    }
                                    if(response.cashbackSuccessMessage.isNotEmpty()) {
                                        rootView.tvAmount.text = response.cashbackSuccessMessage
                                        rootView.tvAmount.typeface = Fonts.mavenMedium(activity)
                                        rootView.tvAmount.visibility = View.VISIBLE
                                        rootView.tvRewardInfo.visibility = View.GONE
                                    } else {
                                        rootView.tvAmount.visibility = View.VISIBLE
                                        rootView.tvRewardInfo.visibility = View.VISIBLE
                                    }
                                }, 10)
                            } else {
                                DialogPopup.alertPopup(activity, "", message)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            DialogPopup.alertPopup(activity, "", getString(R.string.connection_lost_please_try_again))
                        }

                    }

                    override fun onError(feedCommonResponse: ScratchCard, message: String, flag: Int): Boolean {
                        return false
                    }

                    override fun onFinish() {
                        super.onFinish()
                    }
                })
    }

    private fun refreshLoginPromo() {
        if(Data.autoData != null && !Data.autoData.promoCoupons.isNullOrEmpty()) {
            for (i in 0 until Data.autoData.promoCoupons.size) {
                if (Data.autoData.promoCoupons[i].id == promoData.promoCoupon.id) {
                    Data.autoData.promoCoupons[i].setIsScratched(1)
                    val clientId = Config.getLastOpenedClientId(activity)
                    Prefs.with(activity).save(Constants.SP_USE_COUPON_ + clientId, promoData.promoCoupon.id)
                    Prefs.with(activity).save(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, promoData.promoCoupon is CouponInfo)
                    Prefs.with(activity).save(Constants.SP_PROMO_SCRATCHED, true)
                    break
                }
            }
        }
    }

    interface ScratchCardRevealedListener {
        fun onScratchCardRevealed()
    }

}