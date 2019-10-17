package product.clicklabs.jugnoo.newui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.v4.app.DialogFragment
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.dialog_rewards.view.*
import kotlinx.android.synthetic.main.dialog_rewards_alt_2.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.newui.utils.customview.ScratchView
import product.clicklabs.jugnoo.promotion.models.Promo
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Log


class RewardsDialog : DialogFragment() {
    private lateinit var rootView : View
    private lateinit var promoData : Promo

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
        fun newInstance(promo: Promo, isFromLeft : Boolean): RewardsDialog {
            val bundle = Bundle()
            bundle.putParcelable("coupon", promo)
            bundle.putBoolean("isFromLeft", isFromLeft)
            val rewardsDialog = RewardsDialog()
            rewardsDialog.arguments = bundle
            return rewardsDialog
        }
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
                addAnimationOperations()
            }, 50)
        }
    }

    private fun addAnimationOperations() {
//        var set = false
        val constraint1 = ConstraintSet()
        constraint1.clone(card)
        val constraint2 = ConstraintSet()
        constraint2.clone(activity, R.layout.dialog_rewards_alt)
        val constraint3 = ConstraintSet()
        constraint3.clone(activity, R.layout.dialog_rewards)

        val transition = ChangeBounds()
        transition.duration = 500
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(card, transition)
//            val constraint = if(set) constraint1 else constraint2
            constraint2.applyTo(card)
//            set = !set
        }

        Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(card, transition)
//            val constraint = if(set) constraint1 else constraint2
                constraint3.applyTo(card)
//            set = !set
            }
        }, 501)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            TransitionManager.beginDelayedTransition(card)
//            val constraint = if(set) constraint1 else constraint2
//            constraint.applyTo(card)
//            set = !set
//        }

        Handler().postDelayed({
            setData()
        }, 1700)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var isFromLeftSide : Boolean = false
        if(arguments != null) {
            isFromLeftSide = arguments!!.getBoolean("isFromLeft", false)
        }
        if(isFromLeftSide) {
            rootView = inflater.inflate(R.layout.dialog_rewards_alt_2, container, false)
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
//            if(promoData.promoCoupon.benefitType() == 3) {
//                scratchView.setOverlayImage(R.drawable.ic_pattern_cashback)
//            } else {
//                scratchView.setOverlayImage(R.drawable.ic_scratch_pattern)
//            }
        }
        rootView.ivClose.setOnClickListener {
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
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCOUNT_ID] = promoData.promoCoupon.id.toString()

        ApiCommon<FeedCommonResponse>(activity).showLoader(false).execute(params, ApiName.SCRATCH_CARD,
                object : APICommonCallback<FeedCommonResponse>() {

                    override fun onSuccess(response: FeedCommonResponse, message: String, flag: Int) {
                        try {
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                Handler().postDelayed({
                                    (activity as ScratchCardRevealedListener).onScratchCardRevealed()
                                    dismiss()
                                }, 10)
                            } else {
                                DialogPopup.alertPopup(activity, "", message)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            DialogPopup.alertPopup(activity, "", getString(R.string.connection_lost_please_try_again))
                        }

                    }

                    override fun onError(feedCommonResponse: FeedCommonResponse, message: String, flag: Int): Boolean {
                        return false
                    }

                    override fun onFinish() {
                        super.onFinish()
                    }
                })
    }

    interface ScratchCardRevealedListener {
        fun onScratchCardRevealed()
    }

}