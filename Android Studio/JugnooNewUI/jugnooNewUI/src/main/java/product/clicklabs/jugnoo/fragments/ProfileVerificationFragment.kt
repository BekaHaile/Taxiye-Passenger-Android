package product.clicklabs.jugnoo.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.analytics.GAAction.PROFILE
import com.sabkuchfresh.analytics.GAAction.USER
import com.sabkuchfresh.analytics.GACategory
import com.sabkuchfresh.analytics.GACategory.SIDE_MENU
import com.sabkuchfresh.analytics.GAUtils
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName

import java.util.HashMap

import product.clicklabs.jugnoo.AccountActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.adapters.DocumentStatusAdapter
import product.clicklabs.jugnoo.retrofit.model.DocumentData
import product.clicklabs.jugnoo.retrofit.model.FetchDocumentResponse
import product.clicklabs.jugnoo.support.SupportActivity
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts
import kotlinx.android.synthetic.main.fragment_profile_verification.*

class ProfileVerificationFragment : Fragment(), GAAction, GACategory {

    private var documentStatusAdapter: DocumentStatusAdapter? = null
    private var documentDataList: List<DocumentData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatus!!.typeface = Fonts.mavenMedium(activity)
        tvNeedHelp!!.typeface = Fonts.mavenMedium(activity)
        textViewLogout!!.typeface = Fonts.mavenMedium(activity)
        rvVerificationDocs.itemAnimator = DefaultItemAnimator()
        rvVerificationDocs.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        fetchAllDocuments()
        rvVerificationDocs.isNestedScrollingEnabled = false

        textViewLogout!!.setOnClickListener {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                    resources.getString(R.string.are_you_sure_you_want_to_logout),
                    resources.getString(R.string.logout), resources.getString(R.string.cancel),
                    { (activity as AccountActivity).logoutAsync(activity) },
                    { },
                    true, false)
            GAUtils.event(SIDE_MENU, USER + PROFILE, GAAction.LOGOUT)
        }

        tvNeedHelp.setOnClickListener {
            GAUtils.event(GACategory.PROFILE_SCREEN, GAAction.SUPPORT + GAAction.CLICKED, "")
            activity!!.startActivity(Intent(activity, SupportActivity::class.java))
            activity!!.overridePendingTransition(R.anim.right_in, R.anim.right_out)
        }
        val spannableText = SpannableStringBuilder(getString(R.string.status_colon_approval, getString(R.string.pending)))
        spannableText.setSpan(android.text.style.StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvStatus!!.text = spannableText

    }

    fun fetchAllDocuments() {
        val params = HashMap<String, String>()
        ApiCommon<FetchDocumentResponse>(activity).putDefaultParams(true).showLoader(true).execute(params, ApiName.FETCH_DOCUMENTS, object : APICommonCallback<FetchDocumentResponse>() {
            override fun onSuccess(fetchDocumentResponse: FetchDocumentResponse, message: String, flag: Int) {
                documentDataList = fetchDocumentResponse.documentDataList
                if (rvVerificationDocs.adapter == null) {
                    documentStatusAdapter = DocumentStatusAdapter(activity, documentDataList, DocumentStatusAdapter.OnDocumentClicked { position ->
                        addImage(documentDataList!![position]) })
                    rvVerificationDocs.adapter = documentStatusAdapter

                } else {
                    documentStatusAdapter!!.updateList(documentDataList)
                }
            }

            override fun onError(fetchDocumentResponse: FetchDocumentResponse, message: String, flag: Int): Boolean {
                return false
            }
        })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            fetchAllDocuments()
        }
    }

    companion object {

        fun newInstance(): ProfileVerificationFragment {
            return ProfileVerificationFragment()
        }
    }

    fun addImage(documentData: DocumentData) {
        try {
            if (documentData.status == DocumentUploadFragment.DocStatus.REJECTED.i) {
                DialogPopup.alertPopupTwoButtonsWithListeners(activity,
                        activity!!.resources.getString(R.string.rejection_reason),
                        getString(R.string.your_documents_are_rejected_upload_again),
                        activity!!.resources.getString(R.string.upload_again),
                        activity!!.resources.getString(R.string.cancel),
                        { (activity as AccountActivity).openDocumentUploadFragment(documentData) },
                        { }, true, true)
            } else {
                (activity as AccountActivity).openDocumentUploadFragment(documentData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
