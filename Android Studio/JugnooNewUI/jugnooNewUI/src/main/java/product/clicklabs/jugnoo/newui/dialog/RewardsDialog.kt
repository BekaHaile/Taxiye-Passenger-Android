package product.clicklabs.jugnoo.newui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.dialog_rewards.view.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.newui.utils.customview.ScratchView
import product.clicklabs.jugnoo.promotion.models.Promo
import product.clicklabs.jugnoo.utils.DialogPopup
import java.util.*


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
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(promo: Promo): RewardsDialog {
            val bundle = Bundle()
            bundle.putParcelable("coupon", promo)
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
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_rewards, container, false)
        setData()
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
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView) {
                    Toast.makeText(context, "Revealed", Toast.LENGTH_LONG).show()
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
        params[Constants.KEY_COUPON_ID] = promoData.promoCoupon.couponId().toString()

        ApiCommon<FeedCommonResponse>(activity).showLoader(false).execute(params, ApiName.SCRATCH_CARD,
                object : APICommonCallback<FeedCommonResponse>() {

                    override fun onSuccess(response: FeedCommonResponse, message: String, flag: Int) {
                        try {
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                Handler().postDelayed({
                                    (activity as ScratchCardRevealedListener).onScratchCardRevealed()
                                    dismiss()
                                }, 500)
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