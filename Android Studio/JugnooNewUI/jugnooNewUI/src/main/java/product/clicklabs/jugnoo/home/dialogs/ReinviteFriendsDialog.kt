package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_reinvite_friends.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.ReinviteFriendsActivity
import product.clicklabs.jugnoo.utils.Fonts


class ReinviteFriendsDialog : DialogFragment() {
    private lateinit var rootView : View

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
        fun newInstance(imageUrl:String, message:String): ReinviteFriendsDialog {
            val dialogFragment = ReinviteFriendsDialog()
            val bundle = Bundle()
            bundle.putString(Constants.KEY_IMAGE, imageUrl)
            bundle.putString(Constants.KEY_MESSAGE, message)
            dialogFragment.arguments = bundle
            return dialogFragment
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
            dialog.window?.setBackgroundDrawableResource(R.color.black_translucent);
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_reinvite_friends, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFonts()
        setData()
        setClickListeners()
    }

    private fun setFonts() {
        tvLabel.setTypeface(Fonts.mavenMedium(requireContext()), BOLD)
        tvCalloutFriends.setTypeface(Fonts.mavenMedium(requireContext()), BOLD)
        tvReinviteMessage.typeface = Fonts.mavenMedium(requireContext())
        btnReInvite.typeface = Fonts.mavenMedium(rootView.context)
        btnCancel.typeface = Fonts.mavenMedium(rootView.context)
    }

    /**
     *
     */
    private fun setData() {
        setLabelText()

        tvCalloutFriends.text = getString(R.string.call_out_your_friends_who_are_app_members, getString(R.string.app_name))

        tvReinviteMessage.text = arguments!!.getString(Constants.KEY_MESSAGE)

        val image = arguments!!.getString(Constants.KEY_IMAGE)
        if(!TextUtils.isEmpty(image)){
            Picasso.with(activity).load(image).into(ivReinviteImage)
        }


    }

    private fun setLabelText() {
        tvLabel.text = getString(R.string.become_a)
        tvLabel.append(" ")
        val betterFriend = SpannableStringBuilder(getString(R.string.better_friend))
        betterFriend.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.text_color_orange)), 0, betterFriend.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLabel.append(betterFriend)
        tvLabel.append(" ")
        tvLabel.append(getString(R.string.to_your))
        tvLabel.append(" ")
        val appName = SpannableStringBuilder(getString(R.string.app_name))
        appName.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.text_color_orange)), 0, appName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLabel.append(appName)
        tvLabel.append(" ")
        tvLabel.append(getString(R.string.friend))
    }


    private fun setClickListeners(){
        btnCancel.setOnClickListener{
            dismiss()
        }
        btnReInvite.setOnClickListener{
            startActivity(Intent(requireContext(), ReinviteFriendsActivity::class.java))
            dismiss()
        }
    }

}