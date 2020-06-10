package product.clicklabs.jugnoo.home.dialogs

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_safety_info.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.AutoData
import product.clicklabs.jugnoo.home.adapters.SafetyInfoListAdapter
import product.clicklabs.jugnoo.utils.Fonts

class SafetyInfoDialog : DialogFragment(){

    private var callback: Callback? = null
    private val TAG = SafetyInfoDialog::class.java.simpleName

    var safetyInfoListAdapter: SafetyInfoListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
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
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.Animations_LoadingDialogFade
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_safety_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        tvTitle.typeface = Fonts.mavenMedium(requireContext())
        buttonYes.typeface = Fonts.mavenMedium(requireContext())
        rvSafetyOptions.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        callback?.getAutoData()?.safetyInfoData?.run{
            safetyInfoListAdapter = SafetyInfoListAdapter(list ?: arrayListOf())
            rvSafetyOptions.adapter = safetyInfoListAdapter
            tvTitle.text = title ?: ""

            if(!TextUtils.isEmpty(image)){
                Picasso.with(requireContext()).load(image)
                        .placeholder(R.drawable.ic_notification_placeholder)
                        .error(R.drawable.ic_notification_placeholder)
                        .into(ivPicture)
            }
        }

        buttonYes.setOnClickListener {
            callback?.onSafetyInfoDialogDismiss()
            dismiss()
        }
        ivClose.setOnClickListener {
            callback?.onSafetyInfoDialogDismiss()
            dismiss()
        }

    }

    companion object{

        fun newInstance():SafetyInfoDialog{
            val dialog = SafetyInfoDialog()
            val bundle = Bundle()

            dialog.arguments = bundle
            return dialog
        }
    }


    interface Callback {
        fun onSafetyInfoDialogDismiss()
        fun getAutoData(): AutoData?
    }

}