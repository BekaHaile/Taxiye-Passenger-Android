package product.clicklabs.jugnoo.promotion.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.analytics.GACategory
import com.sabkuchfresh.analytics.GAUtils
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_referrals.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.promotion.ReferralActions
import product.clicklabs.jugnoo.promotion.ShareActivity
import product.clicklabs.jugnoo.promotion.adapters.MediaInfoFragmentAdapter
import product.clicklabs.jugnoo.promotion.dialogs.ReferDriverDialog
import product.clicklabs.jugnoo.promotion.models.ReferralTxnResponse
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import java.util.*


class ReferralsFragment : Fragment(), GACategory, GAAction {


    private var rootView: View? = null
    private var activity: ShareActivity? = null
    private var bundle: Bundle? = null

    private var mediaInfoFragmentAdapter: MediaInfoFragmentAdapter? = null

    private var referDriverDialog: ReferDriverDialog? = null

    private var referralTxnResponse: ReferralTxnResponse? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_referrals, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity = requireActivity() as ShareActivity
        bundle = Bundle()


        GAUtils.trackScreenView(GAAction.REFERRAL + GAAction.HOME)

        textViewDesc!!.typeface = Fonts.mavenMedium(activity)

        textViewLeaderboardSingle!!.typeface = Fonts.mavenMedium(activity)

        textViewLeaderboard.typeface = Fonts.mavenMedium(activity)
        textViewReferDriver!!.typeface = Fonts.mavenMedium(activity)



        tvWhatsapp.typeface = Fonts.mavenMedium(activity)
        tvMoreSharingOptions!!.typeface = Fonts.mavenRegular(activity)
        Utils.setTextUnderline(tvMoreSharingOptions, activity!!.getString(R.string.view_more_sharing_options))
        tvYourReferralCode.typeface = Fonts.mavenRegular(activity)
        tvReferralCodeValue!!.typeface = Fonts.mavenMedium(activity)


        tvMoreSharingOptions!!.setOnClickListener {
            if (MyApplication.getInstance().isOnline) {
                ReferralActions.openGenericShareIntent(activity, activity!!.callbackManager)

                GAUtils.event(GACategory.SIDE_MENU, GAAction.FREE_GIFT, GAAction.MORE_SHARING_OPTIONS + GAAction.CLICKED)
            } else {
                DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.connection_lost_desc))
            }
        }


        llWhatsappShare!!.setOnClickListener {
            if (Utils.appInstalledOrNot(activity!!, "com.whatsapp")) {
                ReferralActions.shareToWhatsapp(activity)
            } else {
                ReferralActions.openGenericShareIntent(activity, null)
            }
            GAUtils.event(GACategory.SIDE_MENU, GAAction.FREE_GIFT, GAAction.WHATSAPP + GAAction.INVITE + GAAction.CLICKED)
        }

        llReferralCode!!.setOnClickListener {
            try {
                val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(tvReferralCodeValue!!.text.toString(),
                        tvReferralCodeValue!!.text.toString())
                clipboard.setPrimaryClip(clip)
                Utils.showToast(activity, activity!!.getString(R.string.referral_code_copied))
                GAUtils.event(GACategory.SIDE_MENU, GAAction.FREE_GIFT, GAAction.REFERRAL + GAAction.CODE_COPIED)
            } catch (e: Exception) {
            }
        }

        linearLayoutLeaderBoard!!.setOnClickListener { activity!!.openLeaderboardFragment() }

        linearLayoutRefer!!.setOnClickListener { relativeLayoutReferSingle!!.performClick() }

        relativeLayoutLeaderboardSingle!!.setOnClickListener { linearLayoutLeaderBoard!!.performClick() }

        textViewReferDriver!!.setOnClickListener { getReferDriverDialog().show() }

        tvReferralsCount!!.setOnClickListener { v -> openReferralTxnFragment(ReferralTxnFragment.STATE_REFERRALS) }
        tvCashEarned!!.setOnClickListener { v -> openReferralTxnFragment(ReferralTxnFragment.STATE_TOTAL) }
        tvCashEarnedToday!!.setOnClickListener { v -> openReferralTxnFragment(ReferralTxnFragment.STATE_TODAY) }

        try {

            if (Data.userData != null && Data.userData.referralMessages.multiLevelReferralEnabled) {
                referralInfoApi()

                llUserReferralData!!.visibility = View.VISIBLE

                setHighlightText(tvReferralsCount!!, getString(R.string.referrals), Data.userData.referralMessages.referralsCount.toInt().toString())
                setHighlightText(tvCashEarned!!, getString(R.string.cash_earned), Utils.formatCurrencyValue(Data.autoData.currency, Data.userData.referralMessages.referralEarnedTotal))
                setHighlightText(tvCashEarnedToday!!, getString(R.string.earned_today), Utils.formatCurrencyValue(Data.autoData.currency, Data.userData.referralMessages.referralEarnedToday))



                if (Data.userData.referralMessages.referralImages == null || Data.userData.referralMessages.referralImages.size == 0) {  // images length 0
                    rlViewPager!!.visibility = View.GONE
                    imageViewLogo!!.visibility = View.VISIBLE
                } else {
                    rlViewPager!!.visibility = View.VISIBLE
                    imageViewLogo!!.visibility = View.GONE

                    mediaInfoFragmentAdapter = MediaInfoFragmentAdapter(childFragmentManager, Data.userData.referralMessages.referralImages)
                    viewPagerImageVideo!!.adapter = mediaInfoFragmentAdapter
                    tabDots!!.setupWithViewPager(viewPagerImageVideo)

                    setTabLayoutMargin(tabDots!!)
                }

            } else {
                llUserReferralData!!.visibility = View.GONE
                rlViewPager!!.visibility = View.GONE
                imageViewLogo!!.visibility = View.VISIBLE
            }


            tvReferralCodeValue!!.text = Data.userData.referralCode
            textViewDesc!!.text = Data.userData.referralMessages.referralShortMessage + " " + getString(R.string.details)

            val ss = SpannableString(Data.userData.referralMessages.referralShortMessage + "\n " + getString(R.string.details))
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val html = Utils.DetectHtml.isHtml(Data.userData.referralMessages.referralMoreInfoMessage)
                        DialogPopup.alertPopupLeftOriented(activity, "",
                                Data.userData.referralMessages.referralMoreInfoMessage, true, true, html)
                        GAUtils.event(GACategory.SIDE_MENU, GAAction.FREE_GIFT, GAAction.DETAILS + GAAction.CLICKED)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            ss.setSpan(clickableSpan, textViewDesc!!.text.length - getString(R.string.details).length, textViewDesc!!.text.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            textViewDesc!!.text = ss
            textViewDesc!!.movementMethod = LinkMovementMethod.getInstance()

            if (!"".equals(Data.userData.inviteEarnScreenImage, ignoreCase = true)) {
                Picasso.with(activity).load(Data.userData.inviteEarnScreenImage)
                        .into(imageViewLogo)
            }

            relativeLayoutReferContainer!!.visibility = View.GONE
            if (Data.userData != null) {
                relativeLayoutMultipleTab!!.visibility = View.GONE
                relativeLayoutReferSingle!!.visibility = View.GONE
                relativeLayoutLeaderboardSingle!!.visibility = View.GONE
                if (Data.userData.referralLeaderboardEnabled == 1 && Data.userData.getcToDReferralEnabled() == 1) {
                    relativeLayoutMultipleTab!!.visibility = View.VISIBLE
                    relativeLayoutReferContainer!!.visibility = View.VISIBLE
                } else if (Data.userData.referralLeaderboardEnabled == 1 && Data.userData.getcToDReferralEnabled() != 1) {
                    relativeLayoutLeaderboardSingle!!.visibility = View.VISIBLE
                    relativeLayoutReferContainer!!.visibility = View.VISIBLE
                } else if (Data.userData.referralLeaderboardEnabled != 1 && Data.userData.getcToDReferralEnabled() == 1) {
                    relativeLayoutReferSingle!!.visibility = View.VISIBLE
                    relativeLayoutReferContainer!!.visibility = View.VISIBLE
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun setReferralStats(referralTxnResponse: ReferralTxnResponse?) {
        if(referralTxnResponse?.referralData != null
                && referralTxnResponse.referralData!!.referralsCount != null
                && referralTxnResponse.referralData!!.referralEarnedTotal != null
                && referralTxnResponse.referralData!!.referralEarnedToday != null) {
            setHighlightText(tvReferralsCount!!, getString(R.string.referrals),
                    referralTxnResponse.referralData!!.referralsCount!!.toInt().toString())
            setHighlightText(tvCashEarned!!, getString(R.string.cash_earned),
                    Utils.formatCurrencyValue(Data.autoData.currency, referralTxnResponse.referralData!!.referralEarnedTotal!!))
            setHighlightText(tvCashEarnedToday!!, getString(R.string.earned_today),
                    Utils.formatCurrencyValue(Data.autoData.currency, referralTxnResponse.referralData!!.referralEarnedToday!!))

            Data.userData.referralMessages.referralsCount = referralTxnResponse.referralData!!.referralsCount!!
            Data.userData.referralMessages.referralEarnedTotal = referralTxnResponse.referralData!!.referralEarnedTotal!!
            Data.userData.referralMessages.referralEarnedToday = referralTxnResponse.referralData!!.referralEarnedToday!!
        }
    }

    private fun getReferDriverDialog(): ReferDriverDialog {
        if (referDriverDialog == null) {
            referDriverDialog = ReferDriverDialog(activity)
        }
        return referDriverDialog!!
    }


    private fun setHighlightText(textView: TextView, normal: String, highlight: String) {
        textView.text = normal
        val ssb = SpannableStringBuilder(highlight)
        ssb.setSpan(RelativeSizeSpan(1f), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.append("\n")
        textView.append(ssb)
    }

    private fun setTabLayoutMargin(tabDots: TabLayout) {
        if (tabDots.tabCount == 1) {
            tabDots.visibility = View.GONE
        } else {
            tabDots.visibility = View.VISIBLE
            for (i in 0 until tabDots.tabCount) {
                val tab = (tabDots.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tab.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(activity!!.resources.getDimensionPixelSize(R.dimen.dp_4), 0, 0, 0)
                p.marginStart = activity!!.resources.getDimensionPixelSize(R.dimen.dp_4)
                p.marginEnd = 0
                tab.requestLayout()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): ReferralsFragment {
            val fragment = ReferralsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }


    private fun referralInfoApi(){

        if (!HomeActivity.checkIfUserDataNull(activity) && Data.userData.referralMessages.multiLevelReferralEnabled) {
            val params = HashMap<String, String>()
            ApiCommon<ReferralTxnResponse>(requireActivity()).showLoader(true)
                    .execute(params, ApiName.REFERRAL_INFO, object: APICommonCallback<ReferralTxnResponse>(){
                        override fun onSuccess(t: ReferralTxnResponse?, message: String?, flag: Int) {
                            referralTxnResponse = t
                            setReferralStats(referralTxnResponse)
                        }

                        override fun onError(t: ReferralTxnResponse?, message: String?, flag: Int): Boolean {
                            return false
                        }
                    })
        }

    }

    private fun openReferralTxnFragment(state:Int){
        activity!!.openReferralTxnFragment(state)
    }

    public fun getTxnList(state:Int):MutableList<Any>?{
        val list = mutableListOf<Any>()
        if(state == ReferralTxnFragment.STATE_REFERRALS
                && referralTxnResponse != null && referralTxnResponse!!.referralData != null && referralTxnResponse!!.referralData!!.referrals != null){
            list.addAll(referralTxnResponse!!.referralData!!.referrals!!)
        }
        else if(state == ReferralTxnFragment.STATE_TOTAL
                && referralTxnResponse != null && referralTxnResponse!!.referralData != null && referralTxnResponse!!.referralData!!.txns != null){
            list.addAll(referralTxnResponse!!.referralData!!.txns!!)
        }
        else if(state == ReferralTxnFragment.STATE_TODAY
                && referralTxnResponse != null && referralTxnResponse!!.referralData != null && referralTxnResponse!!.referralData!!.txns != null){
            val currentDate = DateOperations.getCurrentDate()
            val currentDateItems = referralTxnResponse!!.referralData!!.txns!!.filter {
                if(it.isToday != null){
                    it.isToday == 1
                } else {
                    it.creditedOn?.contains(currentDate) ?: false
                }
            }
            list.addAll(currentDateItems)
        }

        return list
    }

}
