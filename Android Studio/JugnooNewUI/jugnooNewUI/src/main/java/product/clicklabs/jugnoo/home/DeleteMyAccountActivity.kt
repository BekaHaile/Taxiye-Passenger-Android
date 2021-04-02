package product.clicklabs.jugnoo.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.activity_delete_account.*
import product.clicklabs.jugnoo.BaseAppCompatActivity
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Prefs
import product.clicklabs.jugnoo.utils.Utils
import java.util.*

class DeleteMyAccountActivity : BaseAppCompatActivity() {

    private var scrolled: Boolean = false

    companion object {
        @JvmStatic
        fun callingIntent(context: Context): Intent {
            return Intent(context, DeleteMyAccountActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
//        Utils.setStatusBarColor(R.color.theme_color, this@DeleteMyAccountActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)
        initView()
        setListeners()
        setFonts()
    }

    private fun setListeners() {

        imageViewBack.setOnClickListener {
            onBackPressed()
        }

        btnDeleteMyAccount.setOnClickListener {
            DialogPopup.alertPopupTwoButtonsWithListeners(this@DeleteMyAccountActivity, "", getString(R.string.are_you_sure_you_want_to_delete_your_account),
                    getString(R.string.yes), getString(R.string.no), { view12: View? -> deleteAccountApi() }, { view1: View? -> }, false, false)
        }

        etFeedBack.setOnFocusChangeListener { view, b ->
            Handler().postDelayed({ scrollView.smoothScrollTo(0, viewForScroll.bottom) }, 200)
            scrolled = true
        }

        etFeedBack.setOnClickListener {
            Handler().postDelayed({ scrollView.smoothScrollTo(0, viewForScroll.bottom) }, 200)
            scrolled = true
        }
    }

    override fun onPause() {
        etFeedBack.clearFocus()
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        if(scrolled) {
            etFeedBack.requestFocus()
        }
    }
    private fun deleteAccountApi() {
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
        if(etFeedBack.text.toString().trim().isNotEmpty()) {
            params[Constants.KEY_REASON] = etFeedBack.text.toString().trim()
        }
        ApiCommon<FeedCommonResponse>(this@DeleteMyAccountActivity).showLoader(true).execute(params, ApiName.REQUEST_DELETE_ACCOUNT,
                object : APICommonCallback<FeedCommonResponse>() {
                    override fun onSuccess(response: FeedCommonResponse, message: String?, flag: Int) {
                        DialogPopup.alertPopupWithListener(this@DeleteMyAccountActivity, "",
                                message, getString(R.string.ok), { v: View? ->
                            Prefs.with(this@DeleteMyAccountActivity).save(Constants.KEY_REQUESTED_FOR_ACCOUNT_DELETION, 1)
                            setResult(Activity.RESULT_OK)
                            onBackPressed()
                        }, false, false, false)
                    }

                    override fun onError(feedCommonResponse: FeedCommonResponse, message: String?, flag: Int): Boolean {
                        return false
                    }
                })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    private fun setFonts() {
        tvNameAndMsg.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD)
        tvAbtToDelete.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD)
        textViewTitle.typeface = Fonts.avenirNext(this)
        tvAlertMsg.typeface = Fonts.mavenRegular(this)
        tvDontWant.typeface = Fonts.mavenRegular(this)
        etFeedBack.typeface = Fonts.mavenMedium(this)
        btnDeleteMyAccount.typeface = Fonts.mavenMedium(this)
    }

    private fun initView() {
        textViewTitle.text = getString(R.string.delete_account)
        tvDontWant.text = getString(R.string.i_don_t_want_to_use_s_anymore, getString(R.string.app_name))
        tvNameAndMsg.text = getString(R.string.s_we_re_sorry_to_see_you_go, Data.userData.userName)
    }
}